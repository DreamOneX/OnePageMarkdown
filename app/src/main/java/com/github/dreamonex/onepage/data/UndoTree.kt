package com.github.dreamonex.onepage.data

class UndoTree(initial: String = "") {

    companion object {
        private const val MAX_NODES = 500
    }

    private val history = mutableListOf(initial)
    private var index = 0

    fun reset(state: String) {
        history.clear()
        history += state
        index = 0
    }

    fun commit(state: String) {
        if (state == history[index]) return
        if (index < history.lastIndex) {
            history.subList(index + 1, history.size).clear()
        }
        history += state
        index++
        trim()
    }

    fun canUndo(): Boolean = index > 0

    fun canRedo(): Boolean = index < history.lastIndex

    fun undo(): String? {
        if (!canUndo()) return null
        index--
        return history[index]
    }

    fun redo(): String? {
        if (!canRedo()) return null
        index++
        return history[index]
    }

    private fun trim() {
        if (history.size <= MAX_NODES) return
        val overflow = history.size - MAX_NODES
        repeat(overflow) {
            if (history.size > 1) {
                history.removeAt(0)
            }
        }
        index -= overflow
        index = index.coerceIn(0, history.lastIndex)
    }
}
