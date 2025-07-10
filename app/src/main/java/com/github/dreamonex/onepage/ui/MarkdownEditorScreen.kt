package com.github.dreamonex.onepage.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import com.github.dreamonex.onepage.data.DraftStore
import com.github.dreamonex.onepage.data.UndoTree
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.snapshotFlow

@Composable
fun MarkdownEditorScreen() {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    val tree = remember { UndoTree() }
    var wysiwyg by remember { mutableStateOf(true) }

    BasicTextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        visualTransformation = if (wysiwyg) MarkdownVisualTransformation()
                               else VisualTransformation.None,
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onBackground
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)
    )

    LaunchedEffect(text) {
        snapshotFlow { text }
            .debounce(400)
            .collectLatest {
                tree.commit(it)
                DraftStore.save(context, it)
            }
    }

    NavigationBar {
        // existing navigation items …

        NavigationBarItem(
            selected = wysiwyg,
            onClick = { wysiwyg = !wysiwyg },
            icon = {
                Icon(
                    if (wysiwyg) Icons.Default.Code else Icons.Default.Visibility,
                    contentDescription = if (wysiwyg) "切换为纯文本" else "切换为所见即所得"
                )
            },
            label = { Text(if (wysiwyg) "纯文本" else "所见即所得") }
        )
    }
}