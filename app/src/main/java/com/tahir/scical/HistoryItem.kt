package com.tahir.scical

data class HistoryItem(
    val formula: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis()
)
