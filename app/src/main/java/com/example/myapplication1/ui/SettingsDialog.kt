package com.example.myapplication1.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.myapplication1.data.ShipType

/**
 * 设置对话框：为每个舰船调节记录颜色。
 * 每个船可以点击选择色板颜色，或选"默认"恢复默认方案。
 */
@Composable
fun SettingsDialog(
    shipColors: Map<ShipType, Int?>,
    maxRecordsShown: Int?,
    versionName: String,
    onMaxRecordsShownChange: (Int?) -> Unit,
    onSetColor: (ShipType, Int?) -> Unit,
    onOpenGitHub: () -> Unit,
    onDismiss: () -> Unit
) {
    // 当前正在调色的船
    var editingShip by remember { mutableStateOf<ShipType?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置 · 舰船颜色") },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    "自定义每个舰船在记录中的颜色",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    ShipType.all.forEachIndexed { index, shipType ->
                        ShipColorRow(
                            shipType = shipType,
                            configuredColor = shipColors[shipType],
                            onClick = { editingShip = shipType }
                        )
                        if (index < ShipType.all.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 2.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // 仅显示最近 N 条
                Text(
                    "仅显示最近 N 条记录（10-50，留空显示全部）",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    var inputText by remember {
                        mutableStateOf(maxRecordsShown?.toString() ?: "")
                    }
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it.filter { c -> c.isDigit() }.take(2) },
                        label = { Text("显示条数") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = {
                        val parsed = inputText.toIntOrNull()
                        onMaxRecordsShownChange(
                            if (parsed != null && parsed in 10..50) parsed else null
                        )
                    }) {
                        Text("应用")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // 关于区域
                Text(
                    "拉格朗日开箱记录器 v$versionName",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    "创建日期：2026-08-08",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "by-MMB",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp)
                )
                // GitHub 链接（bug / 建议提交）
                TextButton(
                    onClick = onOpenGitHub,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text("问题 / 建议 → GitHub")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("完成")
            }
        }
    )

    // 颜色选择子对话框
    val currentShip = editingShip
    if (currentShip != null) {
        ColorPickerDialog(
            title = "${currentShip.label} 颜色",
            currentColor = shipColors[currentShip],
            onSelect = { argb ->
                onSetColor(currentShip, argb)
                editingShip = null
            },
            onDismiss = { editingShip = null }
        )
    }
}

/** 单行：船名 + 当前颜色圆点 + 切换提示 */
@Composable
private fun ShipColorRow(
    shipType: ShipType,
    configuredColor: Int?,
    onClick: () -> Unit
) {
    val fallback = MaterialTheme.colorScheme.onSurface
    val displayColor = resolveShipColor(shipType, configuredColor, fallback)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = shipType.label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (configuredColor == null) {
            Text(
                text = "默认",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            modifier = Modifier.padding(start = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 当前颜色圆点
            ColorDot(color = displayColor)
            Text(
                text = "调整",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun ColorDot(color: Color) {
    Row(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape = CircleShape
            )
    ) {}
}

/** 色板选择对话框 */
@Composable
private fun ColorPickerDialog(
    title: String,
    currentColor: Int?,
    onSelect: (Int?) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // 恢复默认
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(null) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ColorDot(color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        text = "使用默认颜色",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                HorizontalDivider()
                // 色板
                shipColorPalette.chunked(4).forEach { rowColors ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        rowColors.forEach { color ->
                            val isSelected = currentColor == color.value.toInt()
                            Row(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        border = BorderStroke(
                                            if (isSelected) 3.dp else 1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.onSurface
                                            else MaterialTheme.colorScheme.outlineVariant
                                        ),
                                        shape = CircleShape
                                    )
                                    .clickable { onSelect(color.value.toInt()) }
                            ) {}
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
