# 鍵盤音效實現指南

## 已完成的工作

✅ 音效資源文件已添加到 `res/raw/`
- `key_normal.ogg` - 普通鍵音效 (740Hz，現代清脆)
- `key_delete.ogg` - 刪除鍵音效 (520Hz，稍低沉)
- `key_space.ogg` - 空白鍵音效 (880Hz，最輕)

✅ 核心代碼已實現
- `KeyboardSoundManager.kt` - 音效管理
- `KeyboardEventHandler.kt` - 事件處理
- `SettingsFragment.kt` - 設置界面
- `keyboard_preferences.xml` - 設置配置

✅ 音效生成腳本
- `scripts/generate_keyboard_sounds.py` - 可自定義生成音效

## 需要完成的集成步驟

### 步驟 1: 更新 AndroidManifest.xml

在 `<manifest>` 標籤內添加以下權限（在 `<application>` 之前）：

```xml
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
```

### 步驟 2: 在鍵盤 IME Service 中初始化

找到實現 `InputMethodService` 的類（通常名稱類似 `XiamiInputMethodService.kt` 或 `KeyboardService.kt`），添加以下代碼：

```kotlin
import info.plateaukao.ohmybias.keyboard.KeyboardEventHandler

class YourKeyboardService : InputMethodService() {
    private lateinit var eventHandler: KeyboardEventHandler
    
    override fun onCreate() {
        super.onCreate()
        eventHandler = KeyboardEventHandler(this)
        
        // 從 SharedPreferences 恢復音效狀態
        val prefs = getSharedPreferences("keyboard_settings", Context.MODE_PRIVATE)
        val soundEnabled = prefs.getBoolean("keyboard_sound_enabled", true)
        eventHandler.setSoundEnabled(soundEnabled)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        eventHandler.release()
    }
}
```

### 步驟 3: 在按鍵事件中觸發音效

在鍵盤的按鍵事件處理方法中（通常是在處理按鍵輸入的地方），添加：

```kotlin
// 當用戶按下鍵盤時
fun onKeyPressed(keyCode: Int) {
    val isDeleteKey = keyCode == KeyEvent.KEYCODE_DEL || keyCode == KeyEvent.KEYCODE_BACKSPACE
    val isSpaceKey = keyCode == KeyEvent.KEYCODE_SPACE
    
    // 觸發音效
    eventHandler.onKeyPressed(
        keyCode = keyCode,
        isDeleteKey = isDeleteKey,
        isSpaceKey = isSpaceKey
    )
    
    // ... 其他按鍵處理邏輯
}
```

### 步驟 4: 在設置中添加音效選項

將 `SettingsFragment` 添加到你的設置 Activity 中，允許用戶控制音效開關。

## 音效特性

- **現代清脆** - 參考 Microsoft SwiftKey 的音效風格
- **三種不同音效** - 區分普通鍵、刪除鍵、空白鍵
- **快速響應** - 使用 SoundPool 確保低延遲
- **用戶控制** - 可在設置中啟用/禁用
- **輕量化** - OGG Vorbis 格式，文件大小優化

## 自定義音效

如果你想修改或重新生成音效，可以運行提供的 Python 腳本：

```bash
pip install pydub scipy numpy
python scripts/generate_keyboard_sounds.py
```

這將生成新的音效文件。你也可以在 `KeyboardSoundGenerator` 類中調整：
- 基本頻率（`base_freq`）
- 持續時間（`duration`）
- 幅度（`amplitude`）
- 包絡參數（`attack_ms`, `decay_ms`）

## 測試

1. 在設置中啟用鍵盤音效
2. 點擊「測試音效」按鈕驗證
3. 使用鍵盤輸入，聽聽各種按鍵的音效
4. 在設置中禁用音效，確保聲音停止

## 故障排除

### 音效不播放
- ✓ 確保 AndroidManifest.xml 中有音效權限
- ✓ 檢查 res/raw/ 中的 OGG 文件
- ✓ 檢查系統音量設置（不是靜音）
- ✓ 查看 Logcat 中的 KeyboardSoundManager 日誌

### 音效延遲
- SoundPool 首次加載時可能有延遲
- 建議在應用啟動時預加載音效

### 兼容性
- 最低 API 21（Android 5.0）
- 支持所有現代 Android 版本
- 某些設備可能不支持 SoundPool（罕見）

## 相關文件

- 音效管理核心：`src/main/java/info/plateaukao/ohmybias/sound/KeyboardSoundManager.kt`
- 事件處理：`src/main/java/info/plateaukao/ohmybias/keyboard/KeyboardEventHandler.kt`
- 設置界面：`src/main/java/info/plateaukao/ohmybias/settings/SettingsFragment.kt`
- 音效資源：`res/raw/key_*.ogg`
- 生成腳本：`scripts/generate_keyboard_sounds.py`
- 設置配置：`res/xml/keyboard_preferences.xml`
- 詳細文檔：`README_SOUND_EFFECTS.md`
