package com.github.dreamonex.onepage.undo

/**
 * Simple undo tree that supports branching.
 * Each node stores a full snapshot string for simplicity.
 */
class UndoTree(initial: String = "") {

    private data class Node(
        val text: String,
        val parent: Node? = null,
        val children: MutableList<Node> = mutableListOf()
    )

    private var current: Node = Node(initial)

    val currentText: String
        get() = current.text

    /** Commit a new snapshot and create/advance a branch if text changed. */
    fun commit(newText: String) {
        if (newText == current.text) return
        val node = Node(newText, current)
        current.children.add(node)
        current = node
    }

    fun canUndo(): Boolean = current.parent != null

    fun undo(): String {
        check(canUndo()) { "Nothing to undo" }
        current = current.parent!!
        return current.text
    }

    fun branchTexts(): List<String> = current.children.map { it.text }
}