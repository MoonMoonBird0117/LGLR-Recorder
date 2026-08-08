package com.example.myapplication1.ui

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** 缓存 formatter，避免每条记录都创建新的 SimpleDateFormat（主线程调用，线程安全无虞） */
private val TIME_FORMATTER: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

/** 格式化开箱时间为 "yyyy-MM-dd HH:mm" */
fun formatTime(timestamp: Long): String {
    return TIME_FORMATTER.format(Date(timestamp))
}
