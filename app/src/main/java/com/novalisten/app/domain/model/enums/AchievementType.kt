// domain/model/enums/AchievementType.kt
package com.novalisten.app.domain.model.enums

/**
 * Types de réalisations dans NovaListen.
 * Discriminant principal de la table nova_achievements.
 *
 * AWARD  → Nova Awards (annuels/mensuels)
 * RECORD → Records personnels (24 records)
 */
enum class AchievementType {
    AWARD,
    RECORD
}

/**
 * Catégories des Nova Awards (9 awards).
 */
enum class NovaAwardCategory {

    /** 🎵 Chanson de l'année (#1 classement annuel) */
    SONG_OF_YEAR,

    /** 🎤 Artiste de l'année (#1 classement annuel) */
    ARTIST_OF_YEAR,

    /** 💿 Album de l'année (#1 classement annuel) */
    ALBUM_OF_YEAR,

    /** 📈 Plus grosse progression (ratio 30 jours vs ancienneté) */
    BIGGEST_RISE,

    /** 🆕 Révélation (artiste découvert <6 mois + plus écouté) */
    REVELATION,

    /** 🤝 Meilleure fidélité (plus de mois distincts) */
    BEST_LOYALTY,

    /** 💎 Meilleure certification (chanson avec le plus d'écoutes) */
    BEST_CERTIFICATION,

    /** 🔥 Plus long streak (jours consécutifs ≥1 écoute) */
    LONGEST_STREAK,

    /** ⏱️ Session la plus longue (gap = 15 min) */
    LONGEST_SESSION;

    fun displayName(): String = when (this) {
        SONG_OF_YEAR       -> "Chanson de l'Année"
        ARTIST_OF_YEAR     -> "Artiste de l'Année"
        ALBUM_OF_YEAR      -> "Album de l'Année"
        BIGGEST_RISE       -> "Plus Grosse Progression"
        REVELATION         -> "Révélation"
        BEST_LOYALTY       -> "Meilleure Fidélité"
        BEST_CERTIFICATION -> "Meilleure Certification"
        LONGEST_STREAK     -> "Plus Long Streak"
        LONGEST_SESSION    -> "Session la Plus Longue"
    }

    fun emoji(): String = when (this) {
        SONG_OF_YEAR       -> "🎵"
        ARTIST_OF_YEAR     -> "🎤"
        ALBUM_OF_YEAR      -> "💿"
        BIGGEST_RISE       -> "📈"
        REVELATION         -> "🆕"
        BEST_LOYALTY       -> "🤝"
        BEST_CERTIFICATION -> "💎"
        LONGEST_STREAK     -> "🔥"
        LONGEST_SESSION    -> "⏱️"
    }
}