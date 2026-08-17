package com.toukir.equinox

import android.app.Application
import com.toukir.equinox.data.local.EquinoxDatabase
import com.toukir.equinox.data.preferences.UserPreferencesManager
import com.toukir.equinox.data.remote.FirestoreSyncManager
import com.toukir.equinox.data.repository.EquinoxRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class EquinoxApp : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { EquinoxDatabase.getDatabase(this, applicationScope) }
    val preferencesManager by lazy { UserPreferencesManager(this) }
    val syncManager by lazy {
        FirestoreSyncManager(
            context = this,
            logDao = database.logDao(),
            todoDao = database.emergencyTodoDao(),
            quoteDao = database.quoteDao(),
            preferencesManager = preferencesManager
        )
    }
    val repository by lazy {
        EquinoxRepository(
            logDao = database.logDao(),
            emergencyTodoDao = database.emergencyTodoDao(),
            quoteDao = database.quoteDao(),
            preferencesManager = preferencesManager,
            syncManager = syncManager
        )
    }
}
