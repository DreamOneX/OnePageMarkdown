package com.github.dreamonex.onepage.ui

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.github.dreamonex.onepage.data.DraftStore
import com.github.dreamonex.onepage.undo.UndoTree
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarkdownEditorScreen(ctx: Context = LocalContext.current) {
    val scope = rememberCoroutineScope()
    var text by remember { mutableStateOf("") }
    val tree = remember { UndoTree() }
    var showBranches by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        text = DraftStore.load(ctx)
        tree.commit(text)
    }

    fun commit(newText: String) {
        tree.commit(newText)
        text = newText
        scope.launch { DraftStore.save(ctx, newText) }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = false,
                    onClick = { if (tree.canUndo()) text = tree.undo() },
                    icon = { Icon(Icons.Default.Undo, contentDescription = "Undo") },
                    label = { Text("撤销") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { if (tree.canRedo()) text = tree.redo() },
                    icon = { Icon(Icons.Default.Redo, contentDescription = "Redo") },
                    label = { Text("重做") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { showBranches = true },
                    icon = { Icon(Icons.Default.History, contentDescription = "Branches") },
                    label = { Text("分支") }
                )
            }
        }
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            BasicTextField(
                value = text,
                onValueChange = { commit(it) },
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                visualTransformation = MarkdownVisualTransformation(),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onBackground)
            )
            if (showBranches) {
                ModalBottomSheet(
                    onDismissRequest = { showBranches = false },
                    sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
                ) {
                    Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Text("选择分支", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        val branches = tree.branchTexts()
                        branches.forEachIndexed { i, t ->
                            TextButton(onClick = {
                                if (tree.canRedo()) { text = tree.redo(i); showBranches = false }
                            }) { Text(if (t.isBlank()) "(空白)" else t) }
                        }
                        if (branches.isEmpty()) Text("无可用分支")
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}