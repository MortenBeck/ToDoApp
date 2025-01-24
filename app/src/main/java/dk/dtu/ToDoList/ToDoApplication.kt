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



/**
 * A custom [Application] class for the ToDoList app. It handles the initialization
 * of Firebase, Google Play Services checks, and sets up an application-scoped
 * [CoroutineScope] for background work.
 *
 * @author helped with chatGPT
 */
class ToDoApplication : Application() {

    /**
     * A [CoroutineScope] tied to the application's lifetime. It uses a [SupervisorJob] so that
     * one failing child job won't cancel all other children, and [Dispatchers.Main] to run
     * coroutines on the main thread by default. The [CoroutineExceptionHandler] logs any
     * uncaught exceptions globally.
     */
    private val applicationScope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Main +
                CoroutineExceptionHandler { _, throwable ->
                    Log.e(TAG, "Coroutine exception: ", throwable)
                }
    )


    /**
     * Called by the Android system when the application is created. Triggers the
     * initialization process for Firebase and Google Play Services.
     */
    override fun onCreate() {
        super.onCreate()

        initializeComponents()
    }

    /**
     * A convenience method to group all initial setup tasks:
     * - Firebase initialization
     * - Google Play Services check
     */
    private fun initializeComponents() {
        initializeFirebase()
        checkGooglePlayServices()
    }

    /**
     * Attempts to initialize Firebase. Logs a success message if initialization
     * completes, otherwise logs an error.
     */
    private fun initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Firebase", e)
        }
    }

    /**
     * Checks if Google Play Services is available on the device. If successful,
     * proceeds to initialize authentication. Otherwise, logs an error message.
     */
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


    /**
     * Handles any error states related to Google Play Services availability.
     * If the error is user-resolvable, logs a warning message so the user can be prompted
     * to resolve it (handled in [MainActivity]).
     *
     * @param googleApiAvailability The [GoogleApiAvailability] instance used for error resolution info.
     * @param resultCode The error code indicating the state of Google Play Services.
     */
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


    /**
     * Checks if the user is already authenticated. If not, performs an anonymous sign-in.
     */
    private fun initializeAuthentication() {
        val auth = FirebaseAuth.getInstance()

        when (auth.currentUser) {
            null -> performAnonymousSignIn()
            else -> Log.d(TAG, "User already signed in: ${auth.currentUser?.uid}")
        }
    }


    /**
     * Launches a coroutine on [Dispatchers.IO] to perform an anonymous sign-in
     * using [UserIdManager]. Logs success or failure messages.
     */
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