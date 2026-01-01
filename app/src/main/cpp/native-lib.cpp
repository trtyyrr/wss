#include <jni.h>
#include <unistd.h>
#include <sys/prctl.h>
#include <string>

// 这里的宏需要与你内核补丁中定义的“暗号”一致
#define PR_SET_STEALTH_ROOT 0x1337BEEF

extern "C" JNIEXPORT jint JNICALL
Java_com_stealth_manager_RootBridge_triggerKernelRoot(JNIEnv *env, jobject thiz) {
    // 发起特殊的 prctl 系统调用
    // 内核捕获此调用后，会直接修改当前进程的 creds
    int result = prctl(PR_SET_STEALTH_ROOT, 1, 0, 0, 0);
    
    // 返回当前 UID，如果为 0 则表示提权成功
    return getuid();
}