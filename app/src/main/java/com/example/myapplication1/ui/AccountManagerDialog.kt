package com.example.myapplication1.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import com.example.myapplication1.data.Account

/**
 * 账号管理对话框：创建账号、切换账号、删除账号
 */
@Composable
fun AccountManagerDialog(
    accounts: List<Account>,
    currentAccountId: String?,
    onSwitchAccount: (String) -> Unit,
    onDeleteAccount: (String) -> Unit,
    onAddAccount: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var newAccountName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("账号管理") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 创建账号
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newAccountName,
                        onValueChange = { newAccountName = it },
                        label = { Text("新账号名称") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        onClick = {
                            val name = newAccountName.trim()
                            if (name.isNotEmpty()) {
                                onAddAccount(name)
                                newAccountName = ""
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("创建")
                    }
                }

                if (accounts.isEmpty()) {
                    Text(
                        "暂无账号，请先创建",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(accounts, key = { it.id }) { account ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { onSwitchAccount(account.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (account.id == currentAccountId)
                                            Icons.Filled.RadioButtonChecked
                                        else
                                            Icons.Filled.RadioButtonUnchecked,
                                        contentDescription = if (account.id == currentAccountId)
                                            "当前账号"
                                        else
                                            "切换到此账号",
                                        tint = if (account.id == currentAccountId)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = account.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { onDeleteAccount(account.id) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "删除账号",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("完成")
            }
        }
    )
}
