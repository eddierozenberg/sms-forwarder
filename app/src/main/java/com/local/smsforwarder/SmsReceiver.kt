package com.local.smsforwarder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
        if (!Prefs.isEnabled(context)) return

        val target = Prefs.getTargetNumber(context)
        if (target.isBlank()) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return
        if (messages.isEmpty()) return

        val from = messages.firstOrNull()?.displayOriginatingAddress.orEmpty()
        val body = messages.joinToString(separator = "") { it.displayMessageBody.orEmpty() }

        // Avoid forwarding loops (e.g. reply from the target number, or our own prefix).
        if (from.filter(Char::isDigit) == target.filter(Char::isDigit)) return
        if (body.startsWith(FORWARD_PREFIX)) return

        val outbound = "$FORWARD_PREFIX From $from: $body"
        try {
            val smsManager = context.getSystemService(SmsManager::class.java)
                ?: SmsManager.getDefault()

            val parts = smsManager.divideMessage(outbound)
            if (parts.size == 1) {
                smsManager.sendTextMessage(target, null, outbound, null, null)
            } else {
                smsManager.sendMultipartTextMessage(target, null, parts, null, null)
            }
            Log.i(TAG, "Forwarded SMS from $from to $target")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to forward SMS", e)
        }
    }

    companion object {
        private const val TAG = "SmsForwarder"
        const val FORWARD_PREFIX = "[Fwd]"
    }
}
