package com.example.rxtest.main

import com.example.rxtest.base.BaseViewModel

class MainViewModel : BaseViewModel(){
    init {
        //초기화 블럭
    }
    fun onClickGetImageButton() {
        excute(repository.requestProducts(), { items ->
            _productListLiveData.value = items
        })
    }
}