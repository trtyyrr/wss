#!/bin/bash
# 标注：GitHub Action 专用执行逻辑

MAGISKBOOT="../bin/magiskboot"
KERNEL_PATCHER="./hex_engine.py"
CONFIG="../include/sm8650_patterns.json"

echo "[*] 正在利用 magiskboot 拆解 SM8650 镜像..."
$MAGISKBOOT unpack ../init_boot.img

# 检查内核是否成功提取
if [ ! -f "kernel" ]; then
    echo "[-] 错误：未能从镜像中提取到内核二进制"
    exit 1
fi

echo "[*] 正在应用内核级十六进制补丁..."
# 调用 Python 引擎修改内核二进制
python3 $KERNEL_PATCHER --kernel kernel --config $CONFIG --version SM8650_GKI_6.1

echo "[*] 正在重新打包并计算 SM8650 校验和..."
# magiskboot 会自动处理签名和哈希，确保能够通过引导校验
$MAGISKBOOT repack ../init_boot.img ../patched_init_boot.img

echo "[+] Git 运行环境处理完成"