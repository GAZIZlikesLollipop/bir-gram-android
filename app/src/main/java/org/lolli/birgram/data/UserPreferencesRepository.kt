package org.lolli.birgram.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("userPreferences")

interface UserPreferencesRepo {
    val isDark: Flow<Boolean>
    suspend fun switchIsDark(isDark: Boolean)
}

class UserPreferencesRepository(context: Context): UserPreferencesRepo {
    private companion object {
        val IS_DARK = booleanPreferencesKey("is_dark")
    }

    private val dataStore = context.dataStore

    override val isDark: Flow<Boolean> = dataStore.data.map {
        it[IS_DARK] ?: true
    }

    override suspend fun switchIsDark(isDark: Boolean) {
        dataStore.updateData {
            it.toMutablePreferences().also { pref ->
                pref[IS_DARK] = isDark
            }
        }
    }

}