package com.example.myapplication1.ui

import androidx.compose.ui.graphics.Color
import com.example.myapplication1.data.ShipType

/**
 * 默认颜色方案：只有巡洋舰、战巡、航母带默认颜色，
 * 其他舰船使用主题默认色（由调用方提供）。
 */
val defaultShipTypeColors: Map<ShipType, Color> = mapOf(
    ShipType.CRUISER to Color(0xFF1E88E5),          // 巡洋舰 · 蓝
    ShipType.STRATEGIC_CRUISER to Color(0xFF8E24AA), // 战巡 · 紫
    ShipType.CARRIER to Color(0xFFF9A825)            // 航母 · 金
)

/** 设置里可选的颜色（含"默认"由 null 表示） */
val shipColorPalette: List<Color> = listOf(
    Color(0xFFE53935), // 红
    Color(0xFFF4511E), // 橙
    Color(0xFFFDD835), // 黄
    Color(0xFF43A047), // 绿
    Color(0xFF1E88E5), // 蓝
    Color(0xFF8E24AA), // 紫
    Color(0xFF00ACC1), // 青
    Color(0xFF6D4C41)  // 棕
)

/**
 * 计算某船的展示颜色：
 * 1. 用户配置的颜色优先
 * 2. 否则使用默认方案色（巡洋舰/战巡/航母）
 * 3. 都没有则用 fallback（主题默认色）
 */
fun resolveShipColor(
    shipType: ShipType,
    configured: Int?,
    fallback: Color
): Color {
    if (configured != null) return Color(configured)
    return defaultShipTypeColors[shipType] ?: fallback
}
