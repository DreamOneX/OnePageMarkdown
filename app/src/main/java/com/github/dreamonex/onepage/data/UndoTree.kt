package com.github.dreamonex.onepage.data

class UndoTree {

    private class Node(
        val state: String,
        val parent: Node?
    ) {
        val children = mutableListOf<Node>()
    }

    private val root = Node("", null)
    private var current = root

    companion object {
        private const val MAX_NODES = 500
    }

    fun commit(state: String) {
        current.children.clear()
        val node = Node(state, current)
        current.children += node
        current = node
        if (size() > MAX_NODES) {
            root.children.removeFirst()
        }
    }

    fun undo(): String? {
        if (current.parent == null) return null
        current = current.parent!!
        return current.state
    }

    fun redo(): String? {
        if (current.children.isEmpty()) return null
        current = current.children.last()
        return current.state
    }

    private fun size(node: Node = root): Int =
        1 + node.children.sumOf { size(it) }
}