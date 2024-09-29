package com.example.voiceup

import android.Manifest
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var switchService: Switch
    private lateinit var textStatus: TextView
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val PERMISSION_REQUEST_SMS = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switchService = findViewById(R.id.switchService)
        textStatus = findViewById(R.id.textStatus)
        sharedPreferences = getSharedPreferences("VoiceUpPrefs", MODE_PRIVATE)

        // Load the saved state
        val isServiceEnabled = sharedPreferences.getBoolean("isServiceEnabled", false)
        switchService.isChecked = isServiceEnabled
        updateStatusText(isServiceEnabled)

        switchService.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences.edit().putBoolean("isServiceEnabled", isChecked).apply()
            updateStatusText(isChecked)

            if (isChecked) {
                checkAndRequestSmsPermission()
            }
        }

        if (isServiceEnabled) {
            checkAndRequestSmsPermission()
        }
    }

    private fun checkAndRequestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS),
                PERMISSION_REQUEST_SMS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_SMS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                textStatus.text = "Permission granted, service can run."
            } else {
                textStatus.text = "Permission denied, service cannot run."
                switchService.isChecked = false
                sharedPreferences.edit().putBoolean("isServiceEnabled", false).apply()
            }
        }
    }

    private fun updateStatusText(isEnabled: Boolean) {
        textStatus.text = if (isEnabled) "Service is running" else "Service is stopped"
    }
}
