package com.example.myapplication1.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication1.data.ResultType
import com.example.myapplication1.data.ShipType

/**
 * 新增开箱记录对话框
 * 舰船顺序：护卫舰 / 驱逐舰 / 巡洋舰 / 战机 / 护航艇 / 战巡 / 航母 / 支援舰
 * 4 + 4 布局。
 */
@Composable
fun AddRecordDialog(
    selectedShipType: ShipType,
    selectedResultType: ResultType,
    isGuaranteed: Boolean,
    noteInput: String,
    onShipTypeChange: (ShipType) -> Unit,
    onResultTypeChange: (ResultType) -> Unit,
    onGuaranteedChange: (Boolean) -> Unit,
    onNoteChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新增开箱记录", style = MaterialTheme.typography.titleMedium) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 舰船类型
                Text("舰船类型", style = MaterialTheme.typography.labelLarge)
                ShipType.all.chunked(4).forEach { rowShips ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowShips.forEach { type ->
                            FilterChip(
                                selected = selectedShipType == type,
                                onClick = { onShipTypeChange(type) },
                                label = {
                                    Text(
                                        text = type.label,
                                        fontSize = 13.sp
                                    )
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                            )
                        }
                    }
                }

                // 结果类型（蓝图 / 蓝点，小字）
                Text("结果类型", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResultType.all.forEach { resultType ->
                        FilterChip(
                            selected = selectedResultType == resultType,
                            onClick = { onResultTypeChange(resultType) },
                            label = { Text(resultType.label, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 保底箱子（可选项）
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isGuaranteed,
                        onCheckedChange = onGuaranteedChange
                    )
                    Text("保底箱", style = MaterialTheme.typography.bodyMedium)
                }

                // 备注
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = onNoteChange,
                    label = { Text("备注（可选）") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
