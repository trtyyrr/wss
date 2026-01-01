import os
import sys
import json
import argparse

class HexEngine:
    def __init__(self, kernel_path, config_path):
        self.kernel_path = kernel_path
        self.config_path = config_path
        self.patterns = self._load_config()

    def _load_config(self):
        """加载存储在 include/ 中的 SM8650 特征库"""
        with open(self.config_path, 'r') as f:
            return json.load(f)

    def apply_patches(self, target_version):
        """执行二进制替换逻辑"""
        if target_version not in self.patterns:
            print(f"[-] 错误: 未在特征库中找到版本 {target_version}")
            return False

        print(f"[*] 正在加载内核镜像: {self.kernel_path}")
        with open(self.kernel_path, 'rb') as f:
            kernel_data = bytearray(f.read())

        version_data = self.patterns[target_version]
        
        for patch_name, patch_info in version_data.items():
            search_hex = bytes.fromhex(patch_info['search'].replace(" ", ""))
            replace_hex = bytes.fromhex(patch_info['replace'].replace(" ", ""))

            # 查找二进制特征位置
            offset = kernel_data.find(search_hex)
            if offset != -1:
                print(f"[+] 匹配成功 [{patch_name}]: 偏移地址 0x{offset:02x}")
                # 执行替换
                kernel_data[offset:offset+len(search_hex)] = replace_hex
            else:
                print(f"[-] 匹配失败 [{patch_name}]: 未找到特征码")

        # 将修改后的数据写回文件
        with open(self.kernel_path, 'wb') as f:
            f.write(kernel_data)
        print("[*] 内核补丁应用完成。")
        return True

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Stealth Root Hex Engine for SM8650")
    parser.add_argument("--kernel", required=True, help="内核二进制文件路径")
    parser.add_argument("--config", required=True, help="特征库 JSON 路径")
    parser.add_argument("--version", default="SM8650_GKI_6.1", help="目标内核版本标识")

    args = parser.parse_args()

    engine = HexEngine(args.kernel, args.config)
    if engine.apply_patches(args.version):
        sys.exit(0)
    else:
        sys.exit(1)