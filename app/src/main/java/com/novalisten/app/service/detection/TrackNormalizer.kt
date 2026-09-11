// service/detection/TrackNormalizer.kt
package com.novalisten.app.service.detection

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Nettoie et normalise les données brutes de détection.
 *
 * Problèmes traités :
 * - Espaces en trop ("  Blinding  Lights  " → "Blinding Lights")
 * - Casse ("BLINDING LIGHTS" → "Blinding Lights")
 * - Mentions "feat." / "ft." / "with" dans le titre
 * - Caractères spéciaux parasites
 * - Parenthèses inutiles ("(Official Video)", "(Lyric Video)", etc.)
 */
@Singleton
class TrackNormalizer @Inject constructor() {

    companion object {
        // Patterns à supprimer du titre
        private val NOISE_PATTERNS = listOf(
            Regex("""\(Official\s*(Music)?\s*Video\)""", RegexOption.IGNORE_CASE),
            Regex("""\(Lyric\s*Video\)""", RegexOption.IGNORE_CASE),
            Regex("""\(Audio\)""", RegexOption.IGNORE_CASE),
            Regex("""\(HD\)""", RegexOption.IGNORE_CASE),
            Regex("""\(HQ\)""", RegexOption.IGNORE_CASE),
            Regex("""\(Official Audio\)""", RegexOption.IGNORE_CASE),
            Regex("""\(Visualizer\)""", RegexOption.IGNORE_CASE),
            Regex("""\[.*?\]"""),   // Tout ce qui est entre crochets
        )

        // Apps musicales connues → source
        val KNOWN_MUSIC_APPS = mapOf(
            "com.spotify.music"          to "SPOTIFY",
            "com.google.android.apps.youtube.music" to "YOUTUBE_MUSIC",
            "deezer.android.app"         to "DEEZER",
            "com.tidal.wave"             to "TIDAL",
            "com.apple.android.music"    to "APPLE_MUSIC",
            "org.videolan.vlc"           to "VLC",
            "com.soundcloud.android"     to "OTHER"
        )
    }

    /**
     * Normalise un titre de morceau.
     */
    fun normalizeTitle(raw: String): String {
        var result = raw
        NOISE_PATTERNS.forEach { pattern ->
            result = result.replace(pattern, "")
        }
        return result
            .trim()
            .replace(Regex("\\s+"), " ")  // Espaces multiples → 1 espace
    }

    /**
     * Normalise un nom d'artiste.
     * Supprime "feat.", "ft.", "with" et tout ce qui suit.
     */
    fun normalizeArtist(raw: String): String {
        return raw
            .replace(Regex("""\s*(feat\.|ft\.|with|&)\s+.*$""", RegexOption.IGNORE_CASE), "")
            .trim()
            .replace(Regex("\\s+"), " ")
    }

    /**
     * Crée une clé normalisée pour la déduplication.
     * Lowercase + sans espaces multiples.
     */
    fun toNormalizedKey(text: String): String =
        text.trim().lowercase().replace(Regex("\\s+"), " ")

    /**
     * Normalise un événement brut complet.
     */
    fun normalize(event: RawTrackEvent): RawTrackEvent = event.copy(
        title  = normalizeTitle(event.title),
        artist = normalizeArtist(event.artist),
        album  = event.album?.let { normalizeTitle(it) }
    )
}