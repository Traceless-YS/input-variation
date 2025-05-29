# Input Variation Switcher

一个IntelliJ IDEA插件，用于在编写代码和注释时自动切换输入法和修改光标颜色。

## 功能

1. 在编写注释时自动切换至中文输入法
2. 在注释区域中自动将光标颜色变为红色，增强视觉提示
3. 在编写代码时自动切换至英文输入法
4. 在代码区域恢复光标默认颜色

## 支持的注释类型

插件能够识别以下类型的注释：

- Java注释：`//`, `/*`, `*/`, `/**`
- XML注释：`<!-- -->`
- YAML、Python等的注释：`#`

## 开发

该插件基于IntelliJ IDEA插件开发框架开发，使用Java语言编写。

### 构建项目

```bash
./gradlew buildPlugin
```

### 安装插件

1. 在IntelliJ IDEA中，点击 File > Settings > Plugins
2. 点击 "Install Plugin from Disk..."
3. 选择 build/distributions 目录下的插件ZIP文件
4. 重启IDEA

## 注意事项

- 该插件依赖JNA库实现输入法切换功能
- 目前主要支持Windows平台
- 输入法的切换和识别基于Windows的输入法API

## 项目地址

gitee:https://gitee.com/Traceless-YS/input-variation

github:https://github.com/Traceless-YS/input-variation

## 开源协议

MIT License 