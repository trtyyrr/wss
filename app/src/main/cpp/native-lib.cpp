#include <jni.h>
#include <unistd.h>
#include <sys/prctl.h>

// 必须与 Kotlin 中的包名和方法名对应
extern "C" JNIEXPORT jint JNICALL
Java_com_stealth_manager_RootBridge_triggerKernelRoot(JNIEnv *env, jobject thiz) {
    // 0x1337BEEF 是你在 SM8650 内核补丁中设定的自定义 prctl 选项
    prctl(0x1337BEEF, 1, 0, 0, 0); 
    
    // 返回提权后的实际 UID 给 Kotlin 层
    return getuid(); 
}