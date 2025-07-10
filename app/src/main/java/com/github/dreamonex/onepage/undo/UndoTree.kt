package com.github.dreamonex.onepage.undo

class UndoTree(initial: String = "") {
    private data class Node(val text: String, val parent: Node? = null, val children: MutableList<Node> = mutableListOf())
    private var current: Node = Node(initial)
    fun commit(newText: String) {
        if (newText == current.text) return
        val n = Node(newText, current)
        current.children.add(n)
        current = n
    }
    fun canUndo() = current.parent != null
    fun undo(): String { check(canUndo()); current = current.parent!!; return current.text }
    fun canRedo() = current.children.isNotEmpty()
    fun redo(idx: Int = 0): String { check(canRedo()); current = current.children[idx]; return current.text }
    fun branchTexts() = current.children.map { it.text.take(40) }
}