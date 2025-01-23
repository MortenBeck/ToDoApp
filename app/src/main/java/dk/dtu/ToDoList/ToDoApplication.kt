package dk.dtu.ToDoList

import kotlinx.coroutines.tasks.await
import android.app.Application
import android.util.Log
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.ConnectionResult
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import dk.dtu.ToDoList.domain.repository.AuthRepository
import dk.dtu.ToDoList.domain.repository.AuthRepositoryImpl
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ToDoApplication : Application() {
    private val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(FirebaseAuth.getInstance())
    }

    private val applicationScope = CoroutineScope(
        SupervisorJob() +
                Dispatchers.Main +
                CoroutineExceptionHandler { _, throwable ->
                    Log.e(TAG, "Coroutine exception: ", throwable)
                }
    )

    override fun onCreate() {
        super.onCreate()
        initializeComponents()
    }

    private fun initializeComponents() {
        initializeFirebase()
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
                authRepository.signInAnonymously()
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