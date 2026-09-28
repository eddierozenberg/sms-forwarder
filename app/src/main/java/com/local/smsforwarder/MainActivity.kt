package com.local.smsforwarder

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.local.smsforwarder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.all { it }
        if (granted) {
            Toast.makeText(this, "SMS permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "SMS permissions are required to forward", Toast.LENGTH_LONG).show()
            binding.switchEnabled.isChecked = false
            Prefs.setEnabled(this, false)
        }
        refreshStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.inputTarget.setText(Prefs.getTargetNumber(this))
        binding.switchEnabled.isChecked = Prefs.isEnabled(this)

        binding.btnSave.setOnClickListener {
            val number = binding.inputTarget.text?.toString().orEmpty().trim()
            if (number.isBlank()) {
                Toast.makeText(this, "Enter a destination phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Prefs.setTargetNumber(this, number)
            Toast.makeText(this, "Saved $number", Toast.LENGTH_SHORT).show()
            refreshStatus()
        }

        binding.switchEnabled.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked && !hasSmsPermissions()) {
                requestSmsPermissions()
                return@setOnCheckedChangeListener
            }
            if (isChecked && Prefs.getTargetNumber(this).isBlank()) {
                Toast.makeText(this, "Save a destination number first", Toast.LENGTH_SHORT).show()
                binding.switchEnabled.isChecked = false
                return@setOnCheckedChangeListener
            }
            Prefs.setEnabled(this, isChecked)
            refreshStatus()
        }

        binding.btnGrant.setOnClickListener { requestSmsPermissions() }
        refreshStatus()
    }

    private fun hasSmsPermissions(): Boolean {
        val needed = arrayOf(
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS
        )
        return needed.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestSmsPermissions() {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS,
                Manifest.permission.SEND_SMS
            )
        )
    }

    private fun refreshStatus() {
        val target = Prefs.getTargetNumber(this).ifBlank { "(not set)" }
        val enabled = Prefs.isEnabled(this)
        val perms = if (hasSmsPermissions()) "granted" else "missing"
        binding.textStatus.text = "Enabled: $enabled\nTarget: $target\nPermissions: $perms"
    }
}
