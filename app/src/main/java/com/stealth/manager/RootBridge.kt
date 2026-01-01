package com.stealth.manager

import android.util.Log

/**
 * RootBridge: 负责与底层内核补丁进行通信的核心桥接类
 * * 功能：
 * 1. 加载 C++ 编写的 Stealth JNI 库
 * 2. 触发内核中预留的隐形提权入口
 * 3. 绕过常规的 'su' 文件检测机制
 */
object RootBridge {

    private const val TAG = "StealthRootBridge"

    init {
        try {
            // 加载在 CMakeLists.txt 中定义的库名称：stealth_jni
            System.loadLibrary("stealth_jni")
            Log.d(TAG, "Native library 'stealth_jni' loaded successfully.")
        } catch (e: UnsatisfiedLinkError) {
            Log.e(TAG, "Failed to load native library: ${e.message}")
        }
    }

    /**
     * 调用 C++ 层定义的提权逻辑
     * * @return 当前进程的 UID。如果提权成功，返回值应为 0 (Root)
     */
    external fun triggerKernelRoot(): Int

    /**
     * 检查当前 App 是否已通过内核补丁获取 Root 权限
     */
    fun isRootSuccessful(): Boolean {
        val uid = triggerKernelRoot()
        Log.d(TAG, "Current process UID after trigger: $uid")
        return uid == 0
    }

    /**
     * 执行底层 Shell 指令
     * 利用内核提权后的进程上下文执行命令，无需显式调用 /system/bin/su
     */
    fun executeCommand(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            process.inputStream.bufferedReader().readText()
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}