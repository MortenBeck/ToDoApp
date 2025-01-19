package dk.dtu.ToDoList

import android.app.Application
import android.util.Log
import androidx.core.os.BuildCompat
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dk.dtu.ToDoList.util.UserIdManager
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ToDoApplication : Application() {

    // Application-scoped coroutine scope
    private val applicationScope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Main +
                CoroutineExceptionHandler { _, throwable ->
                    Log.e(TAG, "Coroutine exception: ", throwable)
                }
    )

    override fun onCreate() {
        super.onCreate()

        // Initialize core components
        initializeComponents()
    }

    private fun initializeComponents() {
        // Initialize Firebase
        initializeFirebase()

        // Check and initialize Google Play Services
        checkGooglePlayServices()
    }

    private fun initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase", e)
        }
    }

    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)

        when (resultCode) {
            ConnectionResult.SUCCESS -> {
                Log.d(TAG, "Google Play Services available")
                initializeAuthentication()
            }
            else -> handleGooglePlayServicesError(googleApiAvailability, resultCode)
        }
    }

    private fun handleGooglePlayServicesError(
        googleApiAvailability: GoogleApiAvailability,
        resultCode: Int
    ) {
        val errorMessage = googleApiAvailability.getErrorString(resultCode)
        Log.e(TAG, "Google Play Services not available: $errorMessage")

        if (googleApiAvailability.isUserResolvableError(resultCode)) {
            Log.w(TAG, "Google Play Services error is resolvable")
            // Error will be handled in MainActivity
        }
    }

    private fun initializeAuthentication() {
        val auth = FirebaseAuth.getInstance()

        when (auth.currentUser) {
            null -> performAnonymousSignIn()
            else -> Log.d(TAG, "User already signed in: ${auth.currentUser?.uid}")
        }
    }

    private fun performAnonymousSignIn() {
        applicationScope.launch(Dispatchers.IO) {
            try {
                UserIdManager.signInAnonymously()
                Log.d(TAG, "Anonymous sign-in successful")
            } catch (e: Exception) {
                Log.e(TAG, "Anonymous sign-in failed", e)
            }
        }
    }

    companion object {
        private const val TAG = "ToDoApplication"
    }
}