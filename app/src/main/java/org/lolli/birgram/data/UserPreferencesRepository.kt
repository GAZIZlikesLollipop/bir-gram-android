package org.lolli.birgram.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.lolli.birgram.Route

val Context.dataStore by preferencesDataStore("userPreferences")

interface UserPreferencesRepo {
    val initialRoute: Flow<String>
    suspend fun updateInitialRoute(route: String)
}

class UserPreferencesRepository(val context: Context): UserPreferencesRepo {
    private companion object {
        val INITIAL_ROUTE = stringPreferencesKey("initialRoute")
    }

    private val dataStore = context.dataStore

    override val initialRoute: Flow<String> = dataStore.data.map {
        it[INITIAL_ROUTE] ?: Route.Auth.route
    }

    override suspend fun updateInitialRoute(route: String) {
        dataStore.edit {
            it.toMutablePreferences()[INITIAL_ROUTE] = route
        }
    }

}