package dk.dtu.ToDoList

import android.app.Application
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dk.dtu.ToDoList.util.UserIdManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.util.Log

class ToDoApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase first
        FirebaseApp.initializeApp(this)

        // Check Google Play Services availability
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e("ToDoApplication", "Google Play Services not available: ${googleApiAvailability.getErrorString(resultCode)}")
            // Handle the error - you might want to show a dialog to the user
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                Log.w("ToDoApplication", "Google Play Services error is resolvable")
            }
        } else {
            Log.d("ToDoApplication", "Google Play Services available")

            // Initialize authentication
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                // Sign in anonymously if no user is signed in
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        UserIdManager.signInAnonymously()
                        Log.d("ToDoApplication", "Anonymous sign-in successful")
                    } catch (e: Exception) {
                        Log.e("ToDoApplication", "Anonymous sign-in failed", e)
                        e.printStackTrace()
                    }
                }
            } else {
                Log.d("ToDoApplication", "User already signed in: ${auth.currentUser?.uid}")
            }
        }
    }
}