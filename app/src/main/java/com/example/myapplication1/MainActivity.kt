package com.example.myapplication1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication1.data.Repository
import com.example.myapplication1.ui.MainScreen
import com.example.myapplication1.ui.OpenBoxViewModel
import com.example.myapplication1.ui.theme.MyApplication1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 创建 Repository 并提供给 ViewModel
        val repository = Repository(this)
        val viewModelFactory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return OpenBoxViewModel(repository) as T
            }
        }

        setContent {
            MyApplication1Theme {
                val vm: OpenBoxViewModel = viewModel(factory = viewModelFactory)
                OpenBoxApp(vm = vm)
            }
        }
    }
}

@Composable
private fun OpenBoxApp(vm: OpenBoxViewModel) {
    val context = LocalContext.current

    // 导出：让用户选择保存位置（SAF）
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("text/plain")
    ) { uri ->
        if (uri != null) {
            val content = vm.generateExportText()
            try {
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    output.write(content.toByteArray(Charsets.UTF_8))
                }
            } catch (e: Exception) {
                // 保存失败时静默处理，避免崩溃
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        MainScreen(
            uiState = vm.uiState,
            onAddRecord = vm::addRecord,
            onDeleteRecord = vm::deleteRecord,
            onClearRecords = vm::clearCurrentAccountRecords,
            onShowAddDialog = vm::showAddRecordDialog,
            onDismissAddDialog = vm::hideAddRecordDialog,
            onShowManageAccounts = vm::showManageAccounts,
            onDismissManageAccounts = vm::hideManageAccounts,
            onSwitchAccount = vm::switchAccount,
            onAddAccount = vm::addAccount,
            onDeleteAccount = vm::deleteAccount,
            onShowSettings = vm::showSettings,
            onDismissSettings = vm::hideSettings,
            onSetShipColor = vm::setShipColor,
            onMaxRecordsShownChange = vm::setMaxRecordsShown,
            onOpenGitHub = {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://github.com/MoonMoonBird0117/LGLR-Recorder")
                )
                context.startActivity(intent)
            },
            versionName = BuildConfig.VERSION_NAME,
            onExport = {
                val accountName = vm.uiState.accounts
                    .firstOrNull { it.id == vm.uiState.currentAccountId }
                    ?.name
                exportLauncher.launch("开箱记录_${accountName ?: "当前账号"}.txt")
            },
            onShipTypeChange = vm::setShipType,
            onResultTypeChange = vm::setResultType,
            onGuaranteedChange = vm::setGuaranteed,
            onNoteChange = vm::setNoteInput,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
