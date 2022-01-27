package com.jooheon.clean_architecture.presentation.view.custom

import android.view.MotionEvent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInteropFilter
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
import com.jooheon.clean_architecture.presentation.theme.CustomTheme
import com.jooheon.clean_architecture.presentation.theme.Purple200
import com.jooheon.clean_architecture.presentation.theme.Purple700

@Composable
fun GithubSearchDialog(openDialog: MutableState<Boolean>, onDismiss: (text: String) -> Unit) {
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

@Preview
@Composable
private fun DialogUI(modifier: Modifier = Modifier, openDialog: MutableState<Boolean>,
                     onDismiss: (text:String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Card (
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 5.dp), // start top end bottom
        elevation = 8.dp
    ){
//      val focusRequester = FocusRequester()
//        DisposableEffect(Unit) {
//            // automatic focus when screen appears
//            // example: modifier = Modifier.focusRequester(focusRequester),
//            focusRequester.requestFocus()
//            onDispose {  }
//        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_github),
                colorFilter = ColorFilter.tint(color = Color.Black),
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
                    color = CustomTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.h6,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    OutlinedTextField(
                        modifier = Modifier
                            .widthIn(1.dp, Dp.Infinity),
                        value = text,
                        onValueChange = { text = it },
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        label = {
                            Text(
                                text = "Input"
                            )
                        },
                        placeholder = {
                            Text(
                                text = "input your github id",
                                style = TextStyle(
                                    color = Color.LightGray,
                                    textAlign = TextAlign.Center
                                )
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = CustomTheme.colors.textPrimary,
                            focusedBorderColor = Color.Green,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.Green,
                            unfocusedLabelColor = Color.Transparent
                        ),
                    )
                }
            }
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .background(Purple200),
                horizontalArrangement = Arrangement.SpaceAround) { // spaceAround가 뭐자??
                TextButton(
                    onClick = {
                        openDialog.value = false
                        onDismiss("")
                    }
                ) {
                    Text(
                        text = "Cancel",
                        fontWeight = FontWeight.Light,
                        color = Color.LightGray,
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
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                    )
                }
            }
        }
    }
}

