#!/bin/bash
# 路径定义
BIN="../bin/magiskboot"
ENGINE="./hex_engine.py"
CONFIG="../include/sm8650_patterns.json"
TARGET_IMG="../init_boot.img"

echo "[*] 开始处理 SM8650 (Snapdragon 8 Gen 3) 镜像..."

# 1. 解包
$BIN unpack $TARGET_IMG

# 2. 修改内核 (使用 Python 引擎)
python3 $ENGINE --kernel kernel --config $CONFIG --version SM8650_GKI_6.1

# 3. 如果需要修改 Ramdisk，可以在此操作
# $BIN cpio ramdisk.cpio "patch"

# 4. 打包并生成镜像
$BIN repack $TARGET_IMG ../patched_init_boot.img

echo "[+] 任务完成！生成的镜像位于根目录: patched_init_boot.img"