// app/src/main/java/com/novalisten/app/NovaListenApp.kt

package com.novalisten.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Point d'entrée de l'application NovaListen.
 *
 * @HiltAndroidApp déclenche la génération de code Hilt.
 * Implémente Configuration.Provider pour que WorkManager
 * utilise Hilt pour l'injection dans les Workers.
 */
@HiltAndroidApp
class NovaListenApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}