// service/detection/NovaListenNotificationListener.kt
package com.novalisten.app.service.detection

import android.content.ComponentName
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Service d'écoute des notifications système.
 *
 * Deux rôles :
 * 1. Fournir le token pour MediaSessionManager.getActiveSessions()
 * 2. Fallback de détection via les notifications musicales
 *
 * Nécessite l'activation manuelle par l'utilisateur dans :
 * Paramètres → Applications spéciales → Accès aux notifications
 */
@AndroidEntryPoint
class NovaListenNotificationListener : NotificationListenerService() {

    @Inject
    lateinit var notificationObserver: NotificationObserver

    @Inject
    lateinit var mediaSessionObserver: MediaSessionObserver

    override fun onListenerConnected() {
        super.onListenerConnected()
        // ✅ Construit le ComponentName correctement
        val componentName = ComponentName(
            this,
            NovaListenNotificationListener::class.java
        )
        mediaSessionObserver.start(componentName)
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        mediaSessionObserver.stop()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let { notificationObserver.onNotificationPosted(it) }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn?.let { notificationObserver.onNotificationRemoved(it) }
    }
}