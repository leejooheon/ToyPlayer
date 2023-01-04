package com.jooheon.clean_architecture.presentation.view.custom

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.jooheon.clean_architecture.presentation.R
import com.jooheon.clean_architecture.presentation.theme.themes.CustomTheme
import com.jooheon.clean_architecture.presentation.theme.themes.PreviewTheme
import com.jooheon.clean_architecture.presentation.view.components.outlinedTextFieldColor

@ExperimentalMaterial3Api
@Composable
fun GithubSearchDialog(
    openDialog: MutableState<Boolean>,
    onDismiss: (text: String) -> Unit
) {
    Dialog(
        onDismissRequest = {
            openDialog.value = false
            onDismiss("")
        },
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = true
        )
    ) {
        DialogUI(openDialog = openDialog, onDismiss = {
            onDismiss(it.trim())
        })
    }
}

@ExperimentalMaterial3Api
@Composable
private fun DialogUI(
    modifier: Modifier = Modifier,
    openDialog: MutableState<Boolean>,
    onDismiss: (text:String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Card (
        modifier = modifier.padding(10.dp, 5.dp, 10.dp, 5.dp), // start top end bottom
        shape = RoundedCornerShape(10.dp),
//        elevation = 8.dp
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_github),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .padding(top = 35.dp)
                    .height(70.dp)
                    .fillMaxWidth()
            )
            Column(modifier = Modifier.padding(15.dp)) {
                Text(
                    text = "Search your\nRepository.",
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        modifier = Modifier.widthIn(1.dp, Dp.Infinity),
                        value = text,
                        onValueChange = { text = it },
                        textStyle = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        label = { Text(text = "Input") },
                        placeholder = {
                            Text(
                                text = "input your github id",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center
                                )
                            )
                        },
                        colors = outlinedTextFieldColor()
                    )
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalArrangement = Arrangement.SpaceAround) { // spaceAround가 뭐자??
                TextButton(
                    onClick = {
                        openDialog.value = false
                        onDismiss("")
                    }
                ) {
                    Text(
                        text = "Cancel",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
                TextButton(
                    onClick = {
                        openDialog.value = false
                        onDismiss(text)
                    }
                ) {
                    Text(
                        text = "Search",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Preview
@Composable
fun GithubSearchDialogPreview() {
    PreviewTheme(true) {
        val openDialog = remember { mutableStateOf(true) }

        DialogUI(openDialog = openDialog, onDismiss = {})
    }
}

