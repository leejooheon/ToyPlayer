cmake_minimum_required(VERSION 3.10)
project(JuceEQ)

set(CMAKE_CXX_STANDARD 17)

# JUCE 모듈 위치
set(JUCE_MODULE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/src/main/cpp/juce_module")

# 라이브러리 생성
add_library(
        JuceEQ
        SHARED
        src/main/cpp/native-lib.cpp
        ${JUCE_MODULE_DIR}/juce_core/juce_core.cpp
        ${JUCE_MODULE_DIR}/juce_core/juce_core_CompilationTime.cpp
        ${JUCE_MODULE_DIR}/juce_audio_basics/juce_audio_basics.cpp
        ${JUCE_MODULE_DIR}/juce_dsp/juce_dsp.cpp
)

# Include 경로
target_include_directories(JuceEQ PRIVATE
        ${JUCE_MODULE_DIR}
        ${JUCE_MODULE_DIR}/juce_core
        ${JUCE_MODULE_DIR}/juce_audio_basics
        ${JUCE_MODULE_DIR}/juce_dsp
)

# 컴파일 전처리기 정의
target_compile_definitions(JuceEQ PRIVATE
        JUCE_GLOBAL_MODULE_SETTINGS_INCLUDED=1
        JUCE_MODULE_AVAILABLE_juce_core=1
        JUCE_MODULE_AVAILABLE_juce_audio_basics=1
        JUCE_MODULE_AVAILABLE_juce_dsp=1
        JUCE_ANDROID=1
)

# 링커 설정
target_link_libraries(
        JuceEQ
        log
        android
)
