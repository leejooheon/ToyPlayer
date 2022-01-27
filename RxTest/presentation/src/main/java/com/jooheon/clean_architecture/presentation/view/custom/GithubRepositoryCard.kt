package com.jooheon.clean_architecture.presentation.view.custom

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.presentation.R

@Preview
@ExperimentalMaterialApi
@Composable
fun GithubRepositoryCard(
    modifier: Modifier,
    card: Entity.Repository,
    onItemClicked: (Entity.Repository) -> Unit,
    onInfoButtonClicked: (Entity.Repository) -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        elevation = 5.dp,
        onClick = { onItemClicked(card) }
    ) {
        Box(
            modifier = Modifier
                .height(200.dp)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row {
                Image(
                    painter = painterResource(id = R.drawable.test_1),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(onClick = {
                        Log.d("asd", "favorite IconButton, ${card.toString()}")
                        onInfoButtonClicked(card)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "info description"
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "name: ${card.name}",
                    maxLines = 1,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = "id: ${card.id}")
            }
        }
    }
}