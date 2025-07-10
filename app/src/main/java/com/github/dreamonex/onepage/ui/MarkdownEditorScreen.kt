package com.github.dreamonex.onepage.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun MarkdownEditorScreen(
    initialText: String,
    onSave: (String) -> Unit
) {
    var text by rememberSaveable { mutableStateOf(initialText) }
    var wysiwyg by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                IconButton(onClick = { wysiwyg = !wysiwyg }) {
                    Icon(
                        imageVector = if (wysiwyg) Icons.Filled.Code else Icons.Filled.Visibility,
                        contentDescription = if (wysiwyg) "切换为纯文本" else "切换为所见即所得"
                    )
                }
                Spacer(Modifier.weight(1f))
                TextButton(onClick = { onSave(text) }) {
                    Text("保存")
                }
            }
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal
                ),
                visualTransformation = if (wysiwyg) MarkdownVisualTransformation() else VisualTransformation.None
            )
        }
    }
}
