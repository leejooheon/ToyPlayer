package com.jooheon.clean_architecture.presentation.view.temp.compose

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.jooheon.clean_architecture.presentation.R

import com.jooheon.clean_architecture.presentation.view.temp.compose.model.CardModel

class ComposeViewModel: ViewModel() {
    private val _cards =  mutableStateListOf<CardModel>() // viewModel에서 값에 대한 변경권을 갖고 (private),
    val cards: SnapshotStateList<CardModel> = _cards // view에서는 State를 활용해 참조만 가능하게 한다.

    private val _name = mutableStateOf("my_name")
    val name: State<String> = _name

    init {
        _cards.add(CardModel(R.drawable.test_1, false))
        _cards.add(CardModel(R.drawable.test_2, true))
        _cards.add(CardModel(R.drawable.test_3, false))
        _cards.add(CardModel(R.drawable.test_4, true))
    }

    fun setCard(index: Int, item: CardModel, value: Boolean) {
        _cards[index] = item.copy(item.resId, value)
        /*
            *** 주의할점 ***
            item.isFavorite = false
            이런식으로 값을 변경해도 recomposition이 발생하지 않는다.
            recomposition을 invoke 하려면 추가/제거/교체만 가능하다.
            이 샘플은 copy를 활용해 교체한다.
         */
    }

    fun setName(name: String) {
        _name.value = name
    }
}