package com.example.voiceup

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.provider.Telephony
import android.util.Log

class SMSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val sharedPreferences = context.getSharedPreferences("VoiceUpPrefs", Context.MODE_PRIVATE)
        val isServiceEnabled = sharedPreferences.getBoolean("isServiceEnabled", false)

        if (!isServiceEnabled) {
            Log.d("SMSReceiver", "Service is disabled. Ignoring SMS.")
            return
        }

        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val messageBody = message.messageBody
                if (messageBody.contains("VOICEUP")) {
                    setMaxVolume(context)
                    Log.d("SMSReceiver", "VOICEUP detected, volume set to maximum")
                }
            }
        }
    }

    private fun setMaxVolume(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING)
        audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI)
    }
}