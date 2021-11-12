package com.example.rxtest.ui.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rxtest.ui.theme.ApplicationTheme
import com.example.rxtest.R
import com.example.rxtest.ui.compose.model.CardModel

class ComposeActivity: ComponentActivity() {
    private val TAG = ComposeActivity::class::simpleName

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApplicationTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
//                    linearLayoutTest("Android")
//                    boxTest("Android")
//                    lazyColumnTest("Android")
                    DrawCards()
                }
            }
        }
    }

    @Composable
    fun columnTest(name: String) {
        // column: linearlayout의 orientation Vertical
        // row: linearlayout의 orientatiton horizontal
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Blue)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround
        ) {
            Text(text = "Hello $name!")
            Spacer(Modifier.width(16.dp)) // 간격 주는애 (view같은거인듯)
            Text(text = "$name Hello!")
        }
    }

    @Composable
    fun boxTest(name: String) {
        // box: 겹치는게 허용되는 framelayout 같은애
        Box(
            modifier = Modifier
                .background(color = Color.Green)
                .width(300.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopStart) {
                Text("Hello $name!")
            }
            Box(modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd) {
                Text("$name Hello!")
            }
        }
    }

    @Composable
    fun lazyColumnTest(name: String) { // listView나 RecyclerView를 간단하게 만들 수 있다.
        // lazyColumn은 scroll이 기본으로 제공되고
        // 그냥 Column을 쓰면 val state = rememberScrollState, Modifier에 .verticalScroll(state)을 써야한다.
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val list: MutableList<String> = mutableListOf()
            Text("test title")
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                for( i in 1..50) {
                    list.add(name + i)
                }
                item {
                    Text("Lazy Header")
                }
                items(list) { content ->
                    Text(content);
                }
                item {
                    Text("Lazy Footer")
                }
            }
        }
    }

    @ExperimentalFoundationApi
    @Composable
    fun DrawCards() {
        val cards: MutableList<CardModel> = mutableListOf()
        cards.add(CardModel(R.drawable.test_1, false))
        cards.add(CardModel(R.drawable.test_2, true))
        cards.add(CardModel(R.drawable.test_3, false))
        cards.add(CardModel(R.drawable.test_4, true))

        cards.add(CardModel(R.drawable.test_1, false))
        cards.add(CardModel(R.drawable.test_2, true))
        cards.add(CardModel(R.drawable.test_3, false))
        cards.add(CardModel(R.drawable.test_4, true))

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            cells = GridCells.Fixed(2)
        ) {
            items(cards) { item ->
                var isFavorite by rememberSaveable {
                    mutableStateOf(item.isFavorite)
                }
                val modifier = Modifier
                    .fillMaxWidth(0.5f) // half width
                    .padding(16.dp)
                ImageCard(
                    modifier,
                    resId = item.resId,
                    isFavorite = isFavorite, { favorite ->
                        isFavorite = favorite
                    }
                )
            }
        }
    }

    @Composable
    fun ImageCard(
        modifier: Modifier,
        resId: Int,
        isFavorite: Boolean,
        onTabFavorite: (Boolean) -> Unit
    ) {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(8.dp),
            elevation = 5.dp
        ) {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = resId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop)
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopEnd) {
                    IconButton(onClick = {
                        onTabFavorite.invoke(!isFavorite)
                        Log.d("asd", "favorite IconButton")
                    }) {
                       Icon(
                           imageVector = if(isFavorite) {
                               Icons.Default.Favorite
                           } else {
                               Icons.Default.FavoriteBorder
                           },
                           tint = Color.Green,
                           contentDescription = "favorite description")
                        }
                    }
            }
        }
    }

    @ExperimentalFoundationApi
    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        ApplicationTheme {
//            linearLayoutTest("Android")
//            boxTest("android")
//            lazyColumnTest("android")
            DrawCards()
        }
    }
}