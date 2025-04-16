#include <jni.h>
#include <android/log.h>

// JUCE 필수 매크로
#define JUCE_GLOBAL_MODULE_SETTINGS_INCLUDED 1
#define JUCE_MODULE_AVAILABLE_juce_core 1
#define JUCE_MODULE_AVAILABLE_juce_audio_basics 1
#define JUCE_MODULE_AVAILABLE_juce_dsp 1

// JUCE 헤더
#include "juce_module/juce_core/juce_core.h"
#include "juce_module/juce_audio_basics/juce_audio_basics.h"
#include "juce_module/juce_dsp/juce_dsp.h"
#define TAG "JUCE_TEST"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)


std::vector<std::vector<juce::dsp::IIR::Filter<float>>> filters;
std::vector<juce::dsp::IIR::Filter<float>> lowpasses;
juce::SmoothedValue<float> smoothedGain;

juce::dsp::ProcessorChain<
        juce::dsp::Gain<float>,          // 0
        juce::dsp::WaveShaper<float>,    // 1 (softclip)
        juce::dsp::Limiter<float>        // 2
> processingChain;

extern "C"
JNIEXPORT void JNICALL
Java_com_jooheon_toyplayer_features_musicservice_audio_JuceEqualizerAudioProcessor_nativePrepare(
        JNIEnv* env, jobject,
        jfloat sampleRate,
        jint channelCount,
        jfloatArray freqs_,
        jfloatArray gains_) {

    int numBands = env->GetArrayLength(freqs_);
    auto* freqs = env->GetFloatArrayElements(freqs_, nullptr);
    auto* gains = env->GetFloatArrayElements(gains_, nullptr);

    // 💡 채널 개수에 맞춰 resize
    filters.clear();
    filters.resize(channelCount);

    juce::dsp::ProcessSpec processSpec;
    processSpec.sampleRate = sampleRate;
    processSpec.maximumBlockSize = 512;
    processSpec.numChannels = (juce::uint32)channelCount;

    // EQ 필터 준비
    for (int ch = 0; ch < channelCount; ++ch) {
        auto& chFilters = filters[ch];
        chFilters.resize(numBands);

        for (int i = 0; i < numBands; ++i) {
            auto computeAdaptiveQ = [](float freqHz) -> float {
                // 예: 20Hz → Q=0.5, 1000Hz → Q=1.0, 16000Hz → Q=2.0
                float norm = juce::jlimit(0.0f, 1.0f, std::log2(freqHz / 20.0f) / std::log2(16000.0f / 20.0f));
                return juce::jmap(norm, 0.5f, 2.0f); // Q 값 범위
            };
            auto computeAdaptiveGain = [](float freqHz, float inputGainDb) -> float {
                float lowLimit = -12.0f;
                float highLimit = 12.0f;

                // 고주파수일수록 더 큰 gain 허용
                float norm = juce::jlimit(0.0f, 1.0f, std::log2(freqHz / 20.0f) / std::log2(16000.0f / 20.0f));
                float maxGain = juce::jmap(norm, 6.0f, 12.0f);  // 저음은 ±6dB, 고음은 ±12dB

                return juce::jlimit(-maxGain, maxGain, inputGainDb);
            };
            float freq = freqs[i];
            float gainDb = gains[i];

            float q = computeAdaptiveQ(freq);
            float adaptedGainDb = computeAdaptiveGain(freq, gainDb);
            float linearGain = juce::Decibels::decibelsToGain(adaptedGainDb);
            auto coeff = juce::dsp::IIR::Coefficients<float>::makePeakFilter(
                    (double)sampleRate,
                    freq,
                    q,
                    linearGain
            );

            chFilters[i].prepare(processSpec);
            chFilters[i].coefficients = coeff;
            chFilters[i].reset();
        }
    }

    env->ReleaseFloatArrayElements(freqs_, freqs, 0);
    env->ReleaseFloatArrayElements(gains_, gains, 0);

    LOGI("✅ nativePrepare: %d bands, %.1fHz, %dch", numBands, sampleRate, channelCount);
    processingChain.prepare(processSpec);

// Gain smoothing 초기화
    smoothedGain.reset(sampleRate, 0.05); // 50ms attack/release
    smoothedGain.setTargetValue(1.0f);

// Soft clipper 설정
    processingChain.get<1>().functionToUse = [](float x) {
        const float threshold = 0.98f;
        float absVal = std::abs(x);
        if (absVal <= threshold)
            return x;
        float sign = x >= 0.0f ? 1.0f : -1.0f;
        float knee = 1.0f - threshold;
        float over = (absVal - threshold) / knee;
        return sign * (threshold + knee * std::tanh(over));
    };

    // Limiter 설정
    processingChain.get<2>().setThreshold(-0.1f); // dB

    lowpasses.clear();
    lowpasses.resize(channelCount);
    for (int ch = 0; ch < channelCount; ++ch)
        lowpasses[ch] = juce::dsp::IIR::Coefficients<float>::makeHighPass(sampleRate, 18.0);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_jooheon_toyplayer_features_musicservice_audio_JuceEqualizerAudioProcessor_nativeProcessSamples(
        JNIEnv* env,
        jobject,
        jfloatArray input_,
        jfloatArray output_,
        jint chCount,
        jint numFrames
) {
    auto* input = env->GetFloatArrayElements(input_, nullptr);
    auto* output = env->GetFloatArrayElements(output_, nullptr);

    std::vector<std::vector<float>> channelBuffers(chCount, std::vector<float>(numFrames));

    // [1] input → EQ 필터 → 저장
    for (int i = 0; i < numFrames; ++i) {
        for (int ch = 0; ch < chCount; ++ch) {
            int idx = i * chCount + ch;
            float sample = input[idx];

            for (auto& filter : filters[ch]) {
                sample = lowpasses[ch].processSample(sample);
                sample = filter.processSample(sample);
            }

            channelBuffers[ch][i] = sample;
        }
    }
    // [2] Block 기반 후처리 (Gain → SoftClip → Limiter)
    float* channelPtrs[16]; // 최대 16ch 까지 지원
    for (int ch = 0; ch < chCount; ++ch)
        channelPtrs[ch] = channelBuffers[ch].data();

    juce::dsp::AudioBlock<float> block(channelPtrs, chCount, numFrames);
    juce::dsp::ProcessContextReplacing<float> context(block);

    // smoothed gain 적용
    processingChain.get<0>().setGainLinear(smoothedGain.getNextValue());
    processingChain.process(context); // Gain → WaveShaper → Limiter

    // [3] planar → interleaved 복사
    for (int i = 0; i < numFrames; ++i) {
        for (int ch = 0; ch < chCount; ++ch) {
            int idx = i * chCount + ch;
            output[idx] = channelBuffers[ch][i];
        }
    }

    env->ReleaseFloatArrayElements(input_, input, JNI_ABORT);
    env->ReleaseFloatArrayElements(output_, output, 0);
}


// JNI 로그 테스트
extern "C"
JNIEXPORT void JNICALL
Java_com_jooheon_toyplayer_core_juce_JuceInitializer_initialize(JNIEnv*, jobject) {
    LOGI("✅ Hello from native-lib.cpp with JUCE DSP!");
}