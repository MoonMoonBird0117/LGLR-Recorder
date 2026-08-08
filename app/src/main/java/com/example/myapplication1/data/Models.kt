package com.example.myapplication1.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** 舰船类型（顺序：护卫舰、驱逐舰、战机、护航艇、巡洋舰、战巡、航母、支援舰） */
@Serializable
enum class ShipType(val label: String) {
    FRIGATE("护卫舰"),
    DESTROYER("驱逐舰"),
    FIGHTER("战机"),
    ESCORT_SHIP("护航艇"),
    CRUISER("巡洋舰"),
    STRATEGIC_CRUISER("战巡"),
    CARRIER("航母"),
    SUPPORT_SHIP("支援舰");

    companion object {
        val all = entries
    }
}

/** 开箱结果类型 */
@Serializable
enum class ResultType(val label: String) {
    BLUEPRINT("蓝图"),
    TECH_POINT("蓝点");

    companion object {
        val all = entries
    }
}

/** 账号 */
@Serializable
data class Account(
    val id: String,
    val name: String,
    val createdAt: Long
)

/** 单条开箱记录 */
@Serializable
data class BoxRecord(
    val id: String,
    val accountId: String,
    val shipType: ShipType,
    val resultType: ResultType,
    val isGuaranteed: Boolean, // 是否保底箱出货
    val openTime: Long,        // 开箱时间戳（毫秒）
    val sequenceNo: Int,       // 该账号下第几次开箱（1 起）
    val note: String = ""      // 备注
)

/** 本地存储的完整数据 */
@Serializable
data class AppData(
    val accounts: List<Account> = emptyList(),
    val records: List<BoxRecord> = emptyList(),
    val currentAccountId: String? = null
)

/** 全局 JSON 配置（JSON 默认情况下 ignoreUnknownKeys 更安全） */
val appJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}
