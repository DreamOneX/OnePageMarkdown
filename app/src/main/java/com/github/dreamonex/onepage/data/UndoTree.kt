package com.github.dreamonex.onepage.data

/**
 * 线程安全、支持分支的撤销 / 重做树
 */
class UndoTree(initial: String = "") {

    private data class Node(
        val text: String,
        val parent: Node?,
        val children: MutableList<Node> = mutableListOf()
    )

    private val lock = Any()
    private var current: Node = Node(initial, null)
    private var size = 1
    private val MAX = 500

    fun commit(newText: String) = synchronized(lock) {
        if (newText == current.text) return
        /* 清除未来分支 */
        current.children.clear()
        /* 新节点 */
        current.children += Node(newText, current)
        current = current.children.last()
        size++
        trim()
    }

    fun undo(): String? = synchronized(lock) {
        current.parent?.also { current = it }?.text
    }

    fun redo(): String? = synchronized(lock) {
        current.children.lastOrNull()?.also { current = it }?.text
    }

    private fun trim() {               // 环形删除最早节点
        if (size <= MAX) return
        var n = current
        while (n.parent?.parent != null) n = n.parent!!
        n.parent!!.children.removeFirst()
        size--
    }
}
