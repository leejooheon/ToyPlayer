package com.example.rxtest.presentation.view.temp.compose

import androidx.lifecycle.MutableLiveData
import com.example.rxtest.presentation.view.temp.api.github.GithubClient
import com.example.rxtest.presentation.base.BaseViewModel
import com.example.rxtest.presentation.view.main.MainViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class RepositoryViewModel: BaseViewModel() {
    private val TAG = MainViewModel::class.simpleName

    var repositoryLiveData = MutableLiveData<String>()

    fun callRepositoryApi(owner: String) {
        GithubClient.getClient().getRepos(owner)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
//                    repositoryLiveData.postValue(it.toString()) // BackgroundThread에서 돌다가 main이 게시될때 값을 전달한다.
                    repositoryLiveData.value = it.toString() // MainThread에서 실행해야 한다. (이 rx는 consumer가 mainThread이기때문에 이렇게 해도 됨)
                },
                onError =  {
                    if(!it.message.isNullOrEmpty()) {
                        repositoryLiveData.value = it.message
                    } else {
                        repositoryLiveData.value = "error @@@@"
                    }
                }
            ).track()
    }
}