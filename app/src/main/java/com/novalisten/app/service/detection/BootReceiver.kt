// service/detection/BootReceiver.kt
package com.novalisten.app.service.detection

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Redémarre le service de détection après un reboot du téléphone.
 * Nécessite la permission RECEIVE_BOOT_COMPLETED.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            DetectionService.start(context)
        }
    }
}