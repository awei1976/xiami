# 鍵盤音效功能實裝指南

## 功能概述

本指南說明如何在虾米音乐（Xiami）輸入法應用中添加鍵盤音效功能。包含三種音效類型：

1. **普通鍵音效** - 按下字母、數字、標點符號等普通鍵時播放
2. **刪除鍵音效** - 按下退格/刪除鍵時播放
3. **空白鍵音效** - 按下空白鍵時播放

## 文件結構

```
src/
├── main/
│   ├── java/info/plateaukao/ohmybias/
│   │   ├── sound/
│   │   │   └── KeyboardSoundManager.kt          # 音效管理核心類
│   │   ├── settings/
│   │   │   ├── KeyboardSoundPreference.kt       # 音效設置偏好
│   │   │   └── SettingsFragment.kt              # 設置 UI
│   │   └── keyboard/
│   │       └── KeyboardEventHandler.kt          # 鍵盤事件處理
│   └── res/
│       ├── raw/                                 # 音效資源
│       │   ├── key_normal.ogg                   # 普通鍵音效
│       │   ├── key_delete.ogg                   # 刪除鍵音效
│       │   └── key_space.ogg                    # 空白鍵音效
│       ├── values/
│       │   └── attrs.xml                        # 自定義屬性
│       └── xml/
│           └── keyboard_preferences.xml         # 設置配置
```

## 核心類說明

### 1. KeyboardSoundManager

負責管理音效的加載和播放。

```kotlin
val soundManager = KeyboardSoundManager(context)

// 播放不同的音效
soundManager.playNormalKeySound()   // 普通鍵
soundManager.playDeleteKeySound()   // 刪除鍵
soundManager.playSpaceKeySound()    // 空白鍵

// 控制音效狀態
soundManager.setEnabled(true)       // 啟用音效
soundManager.isEnabled()            // 查詢狀態

// 釋放資源
soundManager.release()
```

### 2. KeyboardEventHandler

在鍵盤事件中觸發音效播放。

```kotlin
val eventHandler = KeyboardEventHandler(context)

// 在按鍵事件中調用
eventHandler.onKeyPressed(
    keyCode = keyCode,
    isDeleteKey = keyCode == KeyEvent.KEYCODE_DEL,
    isSpaceKey = keyCode == KeyEvent.KEYCODE_SPACE
)
```

### 3. SettingsFragment

提供用戶界面以控制音效設置。

- 顯示鍵盤音效開關
- 提供測試音效按鈕
- 保存用戶偏好設置

## 集成步驟

### 步驟 1：添加音效資源

在 `res/raw/` 目錄中放置三個音效文件：
- `key_normal.ogg` - 普通鍵音效（短促的嗶聲）
- `key_delete.ogg` - 刪除鍵音效（稍微低沉的聲音）
- `key_space.ogg` - 空白鍵音效（較輕的音效）

**建議規格：**
- 格式：OGG Vorbis（最佳兼容性和文件大小）
- 採樣率：44.1 kHz
- 聲道：單聲道
- 時長：100-200ms（簡短的音效）
- 文件大小：每個 < 50KB

### 步驟 2：在 AndroidManifest.xml 中添加权限

```xml
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
```

### 步驟 3：在鍵盤 Activity 中初始化

```kotlin
private lateinit var eventHandler: KeyboardEventHandler

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    eventHandler = KeyboardEventHandler(this)
    
    // 從 SharedPreferences 恢復音效狀態
    val prefs = getSharedPreferences("keyboard_settings", Context.MODE_PRIVATE)
    val soundEnabled = prefs.getBoolean("keyboard_sound_enabled", true)
    eventHandler.setSoundEnabled(soundEnabled)
}
```

### 步驟 4：在按鍵事件中調用

```kotlin
// 在 onKeyDown 或對應的按鍵事件方法中
ovride fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
    val isDeleteKey = keyCode == KeyEvent.KEYCODE_DEL
    val isSpaceKey = keyCode == KeyEvent.KEYCODE_SPACE
    
    eventHandler.onKeyPressed(
        keyCode = keyCode,
        isDeleteKey = isDeleteKey,
        isSpaceKey = isSpaceKey
    )
    
    return super.onKeyDown(keyCode, event)
}
```

### 步驟 5：銷毀時釋放資源

```kotlin
override fun onDestroy() {
    super.onDestroy()
    eventHandler.release()
}
```

## 音效資源創建指南

### 使用開源工具生成

1. **使用 Audacity（免費）**：
   - 生成不同頻率的簡短 sine wave
   - 導出為 OGG Vorbis 格式
   - 普通鍵：440Hz，150ms
   - 刪除鍵：330Hz，150ms
   - 空白鍵：550Hz，100ms

2. **使用線上工具**：
   - Freesound.org - 下載免費的按鍵音效
   - 修剪到 100-200ms
   - 轉換為 OGG Vorbis

### 使用 ffmpeg 轉換格式

```bash
# 轉換為 OGG Vorbis
ffmpeg -i input.wav -c:a libvorbis -q:a 6 output.ogg

# 檢查文件信息
ffprobe output.ogg
```

## 配置選項

在 `KeyboardSoundManager` 中可調整：

```kotlin
// 音量大小（0.0-1.0）
soundPool?.play(soundId, 0.5f, 0.5f, ...)

// 優先級（通常為 1）
// 流數（MAX_STREAMS = 1，防止同時播放多個音效）
```

## 測試

1. 在設置中啟用音效
2. 點擊「測試音效」按鈕
3. 使用鍵盤輸入，驗證各鍵音效正常播放
4. 在設置中禁用音效，確保聲音停止

## 性能考慮

- **SoundPool** 適合簡短、頻繁的音效（< 1秒）
- 最多同時播放 1 個流，防止聲音重疊
- 音效在應用銷毀時自動釋放
- 建議使用 OGG Vorbis 格式以最小化文件大小

## 故障排除

### 音效不播放
1. 檢查 `res/raw/` 中是否有音效文件
2. 確保權限已添加到 AndroidManifest.xml
3. 檢查系統音量設置
4. 查看 Logcat 中的錯誤信息

### 音效延遲
1. SoundPool 首次加載時可能有延遲
2. 在應用啟動時預加載音效
3. 考慮使用異步加載

### 兼容性問題
1. 測試在多個 Android 版本上
2. API 21+ 支持 AudioAttributes
3. 某些設備可能不支持 SoundPool

## 許可證

本功能實現代碼遵循原應用的許可證。
音效資源應檢查其原始許可證。
