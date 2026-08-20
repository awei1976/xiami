package info.plateaukao.ohmybias.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

/**
 * 管理鍵盤按鍵音效
 * 支持三種音效：
 * 1. 普通鍵 (normalKeySound)
 * 2. 刪除鍵 (deleteKeySound)
 * 3. 空白鍵 (spaceKeySound)
 */
class KeyboardSoundManager(private val context: Context) {
    companion object {
        private const val TAG = "KeyboardSoundManager"
        private const val MAX_STREAMS = 1
    }

    private var soundPool: SoundPool? = null
    private var normalKeySoundId: Int = 0
    private var deleteKeySoundId: Int = 0
    private var spaceKeySoundId: Int = 0
    private var isEnabled: Boolean = true

    init {
        initSoundPool()
    }

    /**
     * 初始化 SoundPool
     */
    private fun initSoundPool() {
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(MAX_STREAMS)
                .setAudioAttributes(audioAttributes)
                .build()

            // 加載音效資源
            // 注意：這些資源ID需要在 res/raw 中定義
            soundPool?.let { pool ->
                normalKeySoundId = pool.load(context, R.raw.key_normal, 1)
                deleteKeySoundId = pool.load(context, R.raw.key_delete, 1)
                spaceKeySoundId = pool.load(context, R.raw.key_space, 1)
            }

            Log.d(TAG, "SoundPool initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize SoundPool: ", e)
            soundPool = null
        }
    }

    /**
     * 播放普通鍵音效
     */
    fun playNormalKeySound() {
        if (isEnabled && normalKeySoundId > 0) {
            soundPool?.play(normalKeySoundId, 0.5f, 0.5f, 1, 0, 1.0f)
        }
    }

    /**
     * 播放刪除鍵音效
     */
    fun playDeleteKeySound() {
        if (isEnabled && deleteKeySoundId > 0) {
            soundPool?.play(deleteKeySoundId, 0.5f, 0.5f, 1, 0, 1.0f)
        }
    }

    /**
     * 播放空白鍵音效
     */
    fun playSpaceKeySound() {
        if (isEnabled && spaceKeySoundId > 0) {
            soundPool?.play(spaceKeySoundId, 0.5f, 0.5f, 1, 0, 1.0f)
        }
    }

    /**
     * 設置音效啟用/禁用狀態
     */
    fun setEnabled(enabled: Boolean) {
        this.isEnabled = enabled
    }

    /**
     * 獲取音效啟用狀態
     */
    fun isEnabled(): Boolean = isEnabled

    /**
     * 释放資源
     */
    fun release() {
        try {
            soundPool?.release()
            soundPool = null
            Log.d(TAG, "SoundPool released")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing SoundPool: ", e)
        }
    }
}
