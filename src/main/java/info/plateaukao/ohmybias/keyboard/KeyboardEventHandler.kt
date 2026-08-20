package info.plateaukao.ohmybias.keyboard

import android.content.Context
import info.plateaukao.ohmybias.sound.KeyboardSoundManager

/**
 * 處理鍵盤事件並播放相應的音效
 */
class KeyboardEventHandler(context: Context) {
    private val soundManager = KeyboardSoundManager(context)

    /**
     * 處理鍵盤按鍵事件
     * @param keyCode 按鍵代碼
     * @param isDeleteKey 是否為刪除鍵
     * @param isSpaceKey 是否為空白鍵
     */
    fun onKeyPressed(
        keyCode: Int,
        isDeleteKey: Boolean = false,
        isSpaceKey: Boolean = false
    ) {
        when {
            isDeleteKey -> soundManager.playDeleteKeySound()
            isSpaceKey -> soundManager.playSpaceKeySound()
            else -> soundManager.playNormalKeySound()
        }
    }

    /**
     * 設置音效啟用狀態
     */
    fun setSoundEnabled(enabled: Boolean) {
        soundManager.setEnabled(enabled)
    }

    /**
     * 釋放資源
     */
    fun release() {
        soundManager.release()
    }
}
