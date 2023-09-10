package com.jooheon.clean_architecture.features.github.main.presentation.main.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jooheon.clean_architecture.toyproject.features.common.compose.components.CustomSurface
import com.jooheon.clean_architecture.toyproject.features.common.compose.theme.themes.PreviewTheme

@Composable
fun RepositoryCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    color: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    border: BorderStroke? = null,
    elevation: Dp = 4.dp,
    content: @Composable () -> Unit
) {
    CustomSurface(
        modifier = modifier,
        shape = shape,
        color = color,
        contentColor = contentColor,
        elevation = elevation,
        border = border,
        content = content
    )
}

@Preview
@Composable
private fun RepositoryCardPreview() {
    PreviewTheme {
        RepositoryCard(
            modifier = Modifier
                .size(
                    width = 170.dp,
                    height = 250.dp
                )
                .padding(bottom = 16.dp),
            content = { }
        )
    }
}