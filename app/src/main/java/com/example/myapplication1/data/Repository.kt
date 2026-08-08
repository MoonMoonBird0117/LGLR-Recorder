package com.example.myapplication1.data

import android.content.Context
import kotlinx.serialization.encodeToString
import java.util.UUID

/**
 * 本地数据仓库：用 SharedPreferences + JSON 持久化，
 * 无网络请求，所有数据仅保存在手机本地。
 */
class Repository(context: Context) {

    private val prefs =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val json = appJson

    // ---------- 读取 ----------

    fun loadData(): AppData {
        val raw = prefs.getString(KEY_DATA, null) ?: return AppData()
        return try {
            json.decodeFromString<AppData>(raw)
        } catch (e: Exception) {
            // 数据损坏时返回空数据，避免崩溃
            AppData()
        }
    }

    private fun saveData(data: AppData) {
        prefs.edit().putString(KEY_DATA, json.encodeToString(data)).apply()
    }

    // ---------- 账号操作 ----------

    fun addAccount(name: String): Account {
        val data = loadData()
        val account = Account(id = UUID.randomUUID().toString(), name = name, createdAt = System.currentTimeMillis())
        val accounts = data.accounts + account
        // 第一个账号自动设为当前账号
        val currentId = data.currentAccountId ?: account.id
        saveData(data.copy(accounts = accounts, currentAccountId = currentId))
        return account
    }

    fun deleteAccount(accountId: String) {
        val data = loadData()
        val accounts = data.accounts.filterNot { it.id == accountId }
        val records = data.records.filterNot { it.accountId == accountId }
        // 若删除的是当前账号，切换到剩余账号中的第一个
        var currentId = data.currentAccountId
        if (currentId == accountId) {
            currentId = accounts.firstOrNull()?.id
        }
        saveData(data.copy(accounts = accounts, records = records, currentAccountId = currentId))
    }

    fun setCurrentAccount(accountId: String) {
        val data = loadData()
        if (data.accounts.any { it.id == accountId }) {
            saveData(data.copy(currentAccountId = accountId))
        }
    }

    fun getCurrentAccountId(): String? = loadData().currentAccountId

    // ---------- 开箱记录操作 ----------

    fun addRecord(
        accountId: String,
        shipType: ShipType,
        resultType: ResultType,
        isGuaranteed: Boolean,
        note: String = ""
    ) {
        val data = loadData()
        // 该账号下已有记录数决定序号
        val accountRecords = data.records.filter { it.accountId == accountId }
        val sequenceNo = accountRecords.size + 1
        val record = BoxRecord(
            id = UUID.randomUUID().toString(),
            accountId = accountId,
            shipType = shipType,
            resultType = resultType,
            isGuaranteed = isGuaranteed,
            openTime = System.currentTimeMillis(),
            sequenceNo = sequenceNo,
            note = note.trim()
        )
        saveData(data.copy(records = data.records + record))
    }

    fun deleteRecord(recordId: String) {
        val data = loadData()
        saveData(data.copy(records = data.records.filterNot { it.id == recordId }))
    }

    fun clearAccountRecords(accountId: String) {
        val data = loadData()
        saveData(data.copy(records = data.records.filterNot { it.accountId == accountId }))
    }

    // ---------- 船舶颜色设置 ----------

    /** 读取某个船的配置颜色；未配置时返回 null（表示用默认颜色方案） */
    fun getShipColor(shipType: ShipType): Int? {
        val key = colorKey(shipType)
        return if (prefs.contains(key)) prefs.getInt(key, 0) else null
    }

    /** 保存某个船的颜色；传 null 恢复默认 */
    fun setShipColor(shipType: ShipType, colorArgb: Int?) {
        val key = colorKey(shipType)
        prefs.edit().apply {
            if (colorArgb == null) remove(key) else putInt(key, colorArgb)
        }.apply()
    }

    private fun colorKey(shipType: ShipType) = "ship_color_${shipType.name}"

    companion object {
        private const val PREFS_NAME = "box_opener_prefs"
        private const val KEY_DATA = "app_data_v1"
    }
}
