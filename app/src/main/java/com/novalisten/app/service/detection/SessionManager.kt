// service/detection/SessionManager.kt
package com.novalisten.app.service.detection

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gère les sessions d'écoute.
 *
 * Une session = groupe d'écoutes consécutives
 * sans pause > 15 minutes.
 *
 * Le session_id est un UUID généré au début de chaque session.
 * Il est stocké dans chaque Play pour relier les écoutes.
 */
@Singleton
class SessionManager @Inject constructor() {

    companion object {
        /** Gap max entre 2 écoutes d'une même session : 15 minutes */
        const val SESSION_GAP_MS = 15 * 60 * 1_000L
    }

    private var currentSessionId: String = generateSessionId()
    private var lastActivityAt: Long     = 0L
    private var sessionStartAt: Long     = System.currentTimeMillis()

    /**
     * Retourne l'ID de session actuel.
     * Crée une nouvelle session si le gap > 15 min.
     */
    fun getCurrentSessionId(): String {
        val now  = System.currentTimeMillis()
        val gap  = now - lastActivityAt

        if (lastActivityAt > 0 && gap > SESSION_GAP_MS) {
            // Trop longtemps sans activité → nouvelle session
            startNewSession()
        }

        lastActivityAt = now
        return currentSessionId
    }

    /**
     * Force le démarrage d'une nouvelle session.
     */
    fun startNewSession() {
        currentSessionId = generateSessionId()
        sessionStartAt   = System.currentTimeMillis()
        lastActivityAt   = System.currentTimeMillis()
    }

    /**
     * Durée de la session actuelle en ms.
     */
    fun currentSessionDurationMs(): Long =
        System.currentTimeMillis() - sessionStartAt

    private fun generateSessionId(): String = UUID.randomUUID().toString()
}