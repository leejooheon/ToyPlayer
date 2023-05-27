package com.jooheon.clean_architecture.features.github.main.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.domain.entity.Entity
import com.jooheon.clean_architecture.features.common.compose.components.CustomDivider
import kotlinx.coroutines.flow.StateFlow

@Composable
fun RepositoryColumn(
    idState: StateFlow<String>,
    itemsState: StateFlow<List<Entity.Repository>>,
    onRepositoryClick: (Entity.Repository) -> Unit,
    isPreview:Boolean = false
) {
    val id by idState.collectAsState()
    val items by itemsState.collectAsState()

    CustomDivider(thickness = 2.dp)

    Column(modifier = Modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .heightIn(min = 56.dp)
                .padding(start = 24.dp)
        ) {
            Text(
                text = id,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start)
            )
            IconButton(
                onClick = { /* todo */ },
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ArrowForward,
                    tint = Color.Black,
                    contentDescription = null
                )
            }
        }

        RepositoryItems(
            items = items,
            onRepositoryClick = onRepositoryClick
        )
    }
}