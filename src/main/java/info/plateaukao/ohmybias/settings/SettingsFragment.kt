package info.plateaukao.ohmybias.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import info.plateaukao.ohmybias.R
import info.plateaukao.ohmybias.sound.KeyboardSoundManager

/**
 * 設置 Fragment - 包含鍵盤音效選項
 */
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var soundManager: KeyboardSoundManager
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.keyboard_preferences, rootKey)
        sharedPreferences = preferenceManager.sharedPreferences!!
        soundManager = KeyboardSoundManager(requireContext())

        // 設置音效開關監聽
        val soundSwitch = findPreference<SwitchPreferenceCompat>("keyboard_sound_enabled")
        soundSwitch?.setOnPreferenceChangeListener { _, newValue ->
            val enabled = newValue as Boolean
            soundManager.setEnabled(enabled)
            true
        }

        // 設置測試音效按鈕
        val testSound = findPreference<Preference>("sound_test")
        testSound?.setOnPreferenceClickListener {
            testSoundEffects()
            true
        }
    }

    /**
     * 測試三種音效
     */
    private fun testSoundEffects() {
        soundManager.playNormalKeySound()
        // 延遲後播放其他音效
        view?.postDelayed({
            soundManager.playDeleteKeySound()
        }, 300)
        view?.postDelayed({
            soundManager.playSpaceKeySound()
        }, 600)
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences,
        key: String?
    ) {
        when (key) {
            "keyboard_sound_enabled" -> {
                val enabled = sharedPreferences.getBoolean(key, true)
                soundManager.setEnabled(enabled)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}
