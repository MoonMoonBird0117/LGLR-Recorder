package com.example.myapplication1.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication1.data.BoxRecord
import com.example.myapplication1.data.ResultType
import com.example.myapplication1.data.ShipType

/**
 * 主界面：展示当前账号的开箱记录
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    uiState: OpenBoxUiState,
    onAddRecord: () -> Unit,
    onDeleteRecord: (String) -> Unit,
    onClearRecords: () -> Unit,
    onShowAddDialog: () -> Unit,
    onDismissAddDialog: () -> Unit,
    onShowManageAccounts: () -> Unit,
    onDismissManageAccounts: () -> Unit,
    onSwitchAccount: (String) -> Unit,
    onAddAccount: (String) -> Unit,
    onDeleteAccount: (String) -> Unit,
    onShowSettings: () -> Unit,
    onDismissSettings: () -> Unit,
    onSetShipColor: (ShipType, Int?) -> Unit,
    onMaxRecordsShownChange: (Int?) -> Unit,
    onOpenGitHub: () -> Unit,
    versionName: String,
    onExport: () -> Unit,
    onShipTypeChange: (ShipType) -> Unit,
    onResultTypeChange: (ResultType) -> Unit,
    onGuaranteedChange: (Boolean) -> Unit,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentAccount = uiState.accounts.firstOrNull { it.id == uiState.currentAccountId }
    var showClearConfirm by remember { mutableStateOf(false) }
    var recordPendingDelete by remember { mutableStateOf<BoxRecord?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("开箱记录")
                        if (currentAccount != null) {
                            Text(
                                text = "当前账号：${currentAccount.name} ›",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable(onClick = onShowManageAccounts)
                            )
                        }
                    }
                },
                actions = {
                    // 导出当前账号记录
                    IconButton(onClick = onExport) {
                        Icon(
                            imageVector = Icons.Filled.Upload,
                            contentDescription = "导出记录"
                        )
                    }
                    // 设置
                    IconButton(onClick = onShowSettings) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "设置"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onShowAddDialog,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("新增记录") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            if (uiState.currentAccountId == null) {
                // 无账号时引导创建
                NoAccountHint(onShowManageAccounts = onShowManageAccounts)
            } else if (uiState.records.isEmpty()) {
                EmptyRecordsHint(
                    modifier = Modifier.weight(1f),
                    onClearClick = null
                )
            } else {
                RecordList(
                    records = uiState.records,
                    shipColors = uiState.shipColors,
                    onDelete = { record -> recordPendingDelete = record },
                    onClear = { showClearConfirm = true },
                    modifier = Modifier.weight(1f)
                )
            }
            // 底部小字备注
            Text(
                text = "by-MMB",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 4.dp)
            )
        }
    }

    // 新增记录对话框
    if (uiState.isAddRecordDialogVisible) {
        AddRecordDialog(
            selectedShipType = uiState.selectedShipType,
            selectedResultType = uiState.selectedResultType,
            isGuaranteed = uiState.isGuaranteed,
            noteInput = uiState.noteInput,
            onShipTypeChange = onShipTypeChange,
            onResultTypeChange = onResultTypeChange,
            onGuaranteedChange = onGuaranteedChange,
            onNoteChange = onNoteChange,
            onConfirm = onAddRecord,
            onDismiss = onDismissAddDialog
        )
    }

    // 账号管理对话框
    if (uiState.isManageAccountsVisible) {
        AccountManagerDialog(
            accounts = uiState.accounts,
            currentAccountId = uiState.currentAccountId,
            onSwitchAccount = onSwitchAccount,
            onDeleteAccount = onDeleteAccount,
            onAddAccount = onAddAccount,
            onDismiss = onDismissManageAccounts
        )
    }

    // 设置对话框
    if (uiState.isSettingsVisible) {
        SettingsDialog(
            shipColors = uiState.shipColors,
            maxRecordsShown = uiState.maxRecordsShown,
            versionName = versionName,
            onMaxRecordsShownChange = onMaxRecordsShownChange,
            onSetColor = onSetShipColor,
            onOpenGitHub = onOpenGitHub,
            onDismiss = onDismissSettings
        )
    }

    // 单条删除确认
    val pending = recordPendingDelete
    if (pending != null) {
        AlertDialog(
            onDismissRequest = { recordPendingDelete = null },
            title = { Text("删除记录") },
            text = {
                Text(
                    "确定删除「第 ${pending.sequenceNo} 次 · ${pending.shipType.label} ${pending.resultType.label}」这条记录吗？"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteRecord(pending.id)
                        recordPendingDelete = null
                    }
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { recordPendingDelete = null }) {
                    Text("取消")
                }
            }
        )
    }

    // 清空确认对话框
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = { Text("清空记录") },
            text = { Text("确定要清空当前账号的所有开箱记录吗？此操作不可恢复。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearRecords()
                        showClearConfirm = false
                    }
                ) {
                    Text("清空", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("取消")
                }
            }
        )
    }
}

/** 无账号时的引导提示 */
@Composable
private fun NoAccountHint(onShowManageAccounts: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "还没有账号",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "点击上方「当前账号」或右上角账号图标创建第一个账号",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
        TextButton(onClick = onShowManageAccounts) {
            Text("去创建账号")
        }
    }
}

/** 记录为空时的提示 */
@Composable
private fun EmptyRecordsHint(modifier: Modifier = Modifier, onClearClick: (() -> Unit)?) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "暂无开箱记录",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "点击右下角「新增记录」开始记录",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

/** 记录列表 */
@Composable
private fun RecordList(
    records: List<BoxRecord>,
    shipColors: Map<ShipType, Int?>,
    onDelete: (BoxRecord) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            // 清空按钮行
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Filled.DeleteSweep,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("清空记录", color = MaterialTheme.colorScheme.error)
                }
            }
        }
        items(records, key = { it.id }) { record ->
            RecordItem(
                record = record,
                shipColor = resolveShipColor(
                    shipType = record.shipType,
                    configured = shipColors[record.shipType],
                    fallback = MaterialTheme.colorScheme.onSurface
                ),
                onDelete = { onDelete(record) }
            )
            HorizontalDivider()
        }
    }
}

/** 单条记录卡片 */
@Composable
private fun RecordItem(
    record: BoxRecord,
    shipColor: Color,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧加粗色条（加大的颜色标）
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(48.dp)
                    .clip(CircleShape)
                    .background(shipColor)
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                // 主内容：舰种 + 结果类型
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = record.shipType.label,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = shipColor
                    )
                    Text(
                        text = " · ${record.resultType.label}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (record.resultType == ResultType.BLUEPRINT)
                            Color(0xFF2E7D32)
                        else
                            Color(0xFF1565C0)
                    )
                }
                // 备注
                if (record.note.isNotBlank()) {
                    Text(
                        text = record.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
                // 保底箱
                if (record.isGuaranteed) {
                    Text(
                        text = "保底箱",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFB71C1C),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            // 右上角角落：次数 + 时间（小字）
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "第 ${record.sequenceNo} 次",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatTime(record.openTime),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "删除此记录",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
