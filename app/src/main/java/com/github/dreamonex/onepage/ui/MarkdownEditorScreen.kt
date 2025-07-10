package com.github.dreamonex.onepage.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.github.dreamonex.onepage.data.DraftStore
import com.github.dreamonex.onepage.data.UndoTree
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownEditorScreen() {
    val context = LocalContext.current
    var text by rememberSaveable { mutableStateOf("") }
    var wysiwyg by rememberSaveable { mutableStateOf(true) }
    val undoTree = remember { UndoTree("") }
    var isReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val saved = DraftStore.load(context)
        text = saved
        undoTree.reset(saved)
        isReady = true
    }

    LaunchedEffect(isReady) {
        if (!isReady) return@LaunchedEffect
        snapshotFlow { text }
            .debounce(300)
            .collectLatest { value ->
                undoTree.commit(value)
                DraftStore.save(context, value)
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "OnePage Markdown") },
                actions = {
                    IconButton(
                        onClick = {
                            undoTree.undo()?.let { text = it }
                        },
                        enabled = undoTree.canUndo()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Undo,
                            contentDescription = "撤销"
                        )
                    }
                    IconButton(
                        onClick = {
                            undoTree.redo()?.let { text = it }
                        },
                        enabled = undoTree.canRedo()
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Redo,
                            contentDescription = "重做"
                        )
                    }
                    IconButton(onClick = { wysiwyg = !wysiwyg }) {
                        val icon = if (wysiwyg) Icons.Filled.Code else Icons.Filled.Visibility
                        val description = if (wysiwyg) "切换为纯文本模式" else "切换为所见即所得模式"
                        Icon(imageVector = icon, contentDescription = description)
                    }
                }
            )
        }
    ) { innerPadding ->
        TextField(
            value = text,
            onValueChange = { newValue -> text = newValue },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            visualTransformation = if (wysiwyg) MarkdownVisualTransformation() else VisualTransformation.None,
            placeholder = { Text(text = "在此编写 Markdown…") },
            singleLine = false,
            minLines = 8,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        )
    }
}
