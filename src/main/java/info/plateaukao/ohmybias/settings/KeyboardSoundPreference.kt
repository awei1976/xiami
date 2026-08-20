package info.plateaukao.ohmybias.settings

import android.content.Context
import android.util.AttributeSet
import androidx.preference.SwitchPreferenceCompat

/**
 * 鍵盤音效開關偏好設置
 */
class KeyboardSoundPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SwitchPreferenceCompat(context, attrs, defStyleAttr) {

    companion object {
        const val KEY_SOUND_ENABLED = "keyboard_sound_enabled"
    }

    init {
        key = KEY_SOUND_ENABLED
        title = "鍵盤音效"
        summary = "按下按鍵時播放音效"
        isChecked = sharedPreferences?.getBoolean(KEY_SOUND_ENABLED, true) ?: true
    }
}
