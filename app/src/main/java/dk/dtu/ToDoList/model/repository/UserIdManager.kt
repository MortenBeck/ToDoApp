package dk.dtu.ToDoList.util

import android.content.Context
import java.util.UUID

object UserIdManager {

    private const val PREFS_NAME = "user_prefs"
    private const val USER_ID_KEY = "user_id"

    fun getUserId(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Check if a user ID already exists
        var userId = sharedPreferences.getString(USER_ID_KEY, null)

        if (userId == null) {
            // Generate a new UUID and save it
            userId = UUID.randomUUID().toString()
            sharedPreferences.edit().putString(USER_ID_KEY, userId).apply()
        }

        return userId
    }
}
