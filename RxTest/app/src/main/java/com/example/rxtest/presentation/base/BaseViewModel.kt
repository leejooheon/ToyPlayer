package com.example.rxtest.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.reactivestreams.Publisher

open class BaseViewModel : ViewModel() {

    protected val mDisposables = CompositeDisposable()

    protected fun Disposable.track() {
        mDisposables.add(this)
    }

    override fun onCleared() {
        mDisposables.clear()
        super.onCleared()
    }

    protected fun <T> MediatorLiveData<T>.add(publisher: Publisher<T>) {
        addSource(LiveDataReactiveStreams.fromPublisher(publisher)) {
            postValue(it)
        }
    }
}

//open class BaseViewModel : ViewModel(){
//    private val TAG = BaseViewModel::class.simpleName
//    protected val mDisposable = CompositeDisposable()
//
//    val _errorMessageLiveData = MutableLiveData<String>()
//    val _isSuccess = MutableLiveData<Boolean>()
//
//    val _loading = MutableLiveData<Boolean>()
//    val loading: LiveData<Boolean> get() = _loading
//
//    override fun onCleared() {
//        mDisposable.clear()
//        super.onCleared()
//    }
//
//    protected fun <T> MediatorLiveData<T>.add(publisher: Publisher<T>) {
//        addSource(LiveDataReactiveStreams.fromPublisher(publisher)) {
//            postValue(it)
//        }
//    }
//
//    // 일반적인 네트워크 통신시 사용할 base 통신 함수
////    protected fun <T : Any> excute(
////        single: Single<T>,
////        res: (T) -> Unit,
////        isShowLoad: Boolean = true
////    ) {
////        single.subscribeOn(Schedulers.io())
////            .observeOn(AndroidSchedulers.mainThread())
////            .doOnSubscribe { if (isShowLoad) _loading.value = true }
////            .doAfterTerminate { if (isShowLoad) _loading.value = false }
////            .subscribeBy(onSuccess = {
////                res(it)
////            }, onError = {// 에러나면 해당 에러메시지 토스트메시지로 띄워줌
////                Log.e(TAG, "ViewModel Single Excute() onError -> " + it.message)
////                _errorMessageLiveData.value = it.message
////            })
////            .addTo(compositeDisposable)
////    }
//}