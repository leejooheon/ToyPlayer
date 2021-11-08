package com.example.rxtest.base

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

open class BaseViewModel : ViewModel(){
    private val TAG = "BaseViewModel"
    private val compositeDisposable = CompositeDisposable()

    val _errorMessageLiveData = MutableLiveData<String>()
    val _isSuccess = MutableLiveData<Boolean>()
    val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }


    // 일반적인 네트워크 통신시 사용할 base 통신 함수
    protected fun <T : Any> excute(
        single: Single<T>,
        res: (T) -> Unit,
        isShowLoad: Boolean = true
    ) {
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { if (isShowLoad) _loading.value = true }
            .doAfterTerminate { if (isShowLoad) _loading.value = false }
            .subscribeBy(onSuccess = {
                res(it)
            }, onError = {// 에러나면 해당 에러메시지 토스트메시지로 띄워줌
                Log.e(TAG, "ViewModel Single Excute() onError -> " + it.message)
                _errorMessageLiveData.value = it.message
            })
            .addTo(compositeDisposable)
    }

    protected fun <T : Any> excute(
        flowable: Flowable<T>,
        res: (T) -> Unit,
        isShowLoad: Boolean = true
    ) {
        flowable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { if (isShowLoad) _loading.value = true }
            .doAfterTerminate { if (isShowLoad) _loading.value = false }
            .subscribeBy(onNext = {
                res(it)
            }, onError = {// 에러나면 해당 에러메시지 토스트메시지로 띄워줌
                Log.e(TAG, "ViewModel Flowable Excute() onError -> " + it.message)
                _errorMessageLiveData.value = it.message
            }, onComplete = {
                Log.d(TAG, "ViewModel Flowable Excute() onComplete")
            })
            .addTo(compositeDisposable)
    }

    protected fun excute(
        completable: Completable,
        res: (Boolean) -> Unit,
        isShowLoad: Boolean = true
    ) {
        completable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { if (isShowLoad) _loading.value = true }
            .doAfterTerminate { if (isShowLoad) _loading.value = false }
            .subscribeBy(onComplete = {
                res(true)
            }, onError = {// 에러나면 해당 에러메시지 토스트메시지로 띄워줌
                Log.e(TAG, "ViewModel Completable Excute() onError -> " + it.message)
                _errorMessageLiveData.value = it.message
            })
            .addTo(compositeDisposable)
    }

//    // Single 호출 방법예시
//    fun exampleSingleCall() {
//        excute(repository.requestProducts(), { items ->
//            _productListLiveData.value = items
//        })
//    }
//
//    // Flowable 호출 방법예시
//    fun exampleFlowableCall() {
//        excute(repository.requestProducts(), { items ->
//            _productListLiveData.value = items
//        })
//    }
//
//    // Completable 호출 방법예시
//    fun exampleFlowableCall() {
//        excute(repository.requestProducts(), { success ->
//            _isSuccess.value = success
//        })
//    }
}