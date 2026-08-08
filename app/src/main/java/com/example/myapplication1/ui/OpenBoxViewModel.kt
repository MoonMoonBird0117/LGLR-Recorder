package com.example.myapplication1.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication1.data.Account
import com.example.myapplication1.data.BoxRecord
import com.example.myapplication1.data.Repository
import com.example.myapplication1.data.ResultType
import com.example.myapplication1.data.ShipType
import kotlinx.coroutines.launch

data class OpenBoxUiState(
    val accounts: List<Account> = emptyList(),
    val currentAccountId: String? = null,
    val records: List<BoxRecord> = emptyList(),
    val isAddRecordDialogVisible: Boolean = false,
    val isManageAccountsVisible: Boolean = false,
    val isSettingsVisible: Boolean = false,
    // 仅显示最近 N 条记录（null = 显示全部）
    val maxRecordsShown: Int? = null,
    // 每个船配置的颜色（null = 使用默认颜色方案）
    val shipColors: Map<ShipType, Int?> = emptyMap(),
    // 录入表单状态
    val selectedShipType: ShipType = ShipType.FRIGATE,
    val selectedResultType: ResultType = ResultType.BLUEPRINT,
    val isGuaranteed: Boolean = false,
    val noteInput: String = ""
)

class OpenBoxViewModel(
    private val repository: Repository
) : ViewModel() {

    var uiState by mutableStateOf(OpenBoxUiState())
        private set

    init {
        refresh()
    }

    /** 从仓库加载最新数据并刷新 UI 状态 */
    fun refresh() {
        viewModelScope.launch {
            val data = repository.loadData()
            val currentId = data.currentAccountId
            val allRecords = data.records
                .filter { it.accountId == currentId }
                .sortedByDescending { it.openTime }
            // 应用"仅显示最近 N 条"限制（记录按时间倒序，取前 N 条）
            val limit = uiState.maxRecordsShown
            val visibleRecords = if (limit != null) allRecords.take(limit) else allRecords
            uiState = uiState.copy(
                accounts = data.accounts,
                currentAccountId = currentId,
                records = visibleRecords,
                shipColors = ShipType.all.associateWith { repository.getShipColor(it) }
            )
        }
    }

    /** 设置仅显示最近 N 条记录；传 null 显示全部 */
    fun setMaxRecordsShown(n: Int?) {
        uiState = uiState.copy(maxRecordsShown = n)
        refresh()
    }

    // ---------- 账号 ----------

    fun addAccount(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        repository.addAccount(trimmed)
        refresh()
    }

    fun switchAccount(accountId: String) {
        repository.setCurrentAccount(accountId)
        refresh()
    }

    fun deleteAccount(accountId: String) {
        repository.deleteAccount(accountId)
        refresh()
    }

    // ---------- 开箱记录 ----------

    fun setShipType(type: ShipType) {
        uiState = uiState.copy(selectedShipType = type)
    }

    fun setResultType(type: ResultType) {
        uiState = uiState.copy(selectedResultType = type)
    }

    fun setGuaranteed(checked: Boolean) {
        uiState = uiState.copy(isGuaranteed = checked)
    }

    fun setNoteInput(text: String) {
        uiState = uiState.copy(noteInput = text)
    }

    fun showAddRecordDialog() {
        uiState = uiState.copy(isAddRecordDialogVisible = true)
    }

    fun hideAddRecordDialog() {
        uiState = uiState.copy(isAddRecordDialogVisible = false)
    }

    fun addRecord() {
        val accountId = uiState.currentAccountId ?: return
        repository.addRecord(
            accountId = accountId,
            shipType = uiState.selectedShipType,
            resultType = uiState.selectedResultType,
            isGuaranteed = uiState.isGuaranteed,
            note = uiState.noteInput
        )
        // 重置表单，便于连续录入
        uiState = uiState.copy(
            isGuaranteed = false,
            noteInput = "",
            isAddRecordDialogVisible = false
        )
        refresh()
    }

    fun deleteRecord(recordId: String) {
        repository.deleteRecord(recordId)
        refresh()
    }

    fun clearCurrentAccountRecords() {
        val accountId = uiState.currentAccountId ?: return
        repository.clearAccountRecords(accountId)
        refresh()
    }

    // ---------- 界面开关 ----------

    fun showManageAccounts() {
        uiState = uiState.copy(isManageAccountsVisible = true)
    }

    fun hideManageAccounts() {
        uiState = uiState.copy(isManageAccountsVisible = false)
    }

    fun showSettings() {
        uiState = uiState.copy(isSettingsVisible = true)
    }

    fun hideSettings() {
        uiState = uiState.copy(isSettingsVisible = false)
    }

    // ---------- 颜色设置 ----------

    fun setShipColor(shipType: ShipType, colorArgb: Int?) {
        repository.setShipColor(shipType, colorArgb)
        refresh()
    }

    // ---------- 导出 ----------

    /** 生成当前账号的纯文本导出内容 */
    fun generateExportText(): String {
        val data = repository.loadData()
        val account = data.accounts.firstOrNull { it.id == data.currentAccountId }
        val records = data.records
            .filter { it.accountId == data.currentAccountId }
            .sortedBy { it.sequenceNo }

        val sb = StringBuilder()
        sb.appendLine("${account?.name ?: "开箱记录"} · 开箱记录")
        sb.appendLine("共 ${records.size} 次开箱")
        sb.appendLine()
        records.forEach { record ->
            val guaranteeSuffix = if (record.isGuaranteed) "（保底箱）" else ""
            val noteSuffix = if (record.note.isNotBlank()) " (${record.note})" else ""
            // 蓝图带 *，蓝点不带
            val resultSuffix = if (record.resultType == ResultType.BLUEPRINT) "*" else ""
            sb.appendLine("${record.sequenceNo}-${record.shipType.label}${resultSuffix}$guaranteeSuffix$noteSuffix")
        }
        return sb.toString()
    }
}
