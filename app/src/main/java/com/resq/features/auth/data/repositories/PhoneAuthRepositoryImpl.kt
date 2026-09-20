package com.resq.features.auth.data.repositories

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.resq.features.auth.domain.repositories.PhoneAuthRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.tasks.await

class PhoneAuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : PhoneAuthRepository {

    override fun sendOtp(
        phoneNumber: String,
        activity: Activity,
        onCodeSent: (verificationId: String) -> Unit,
        onError: (message: String) -> Unit
    ) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Auto-retrieval flow optional handling
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    onError(e.localizedMessage ?: "SMS verification failed.")
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    onCodeSent(verificationId)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    override suspend fun verifyOtpCode(
        verificationId: String,
        userCode: String
    ): Result<String> {
        // Direct SDK guard: prevents Firebase from crashing with empty sessionInfo
        if (verificationId.isBlank()) {
            return Result.failure(Exception("Session expired. Please tap 'Resend OTP'."))
        }

        return try {
            // 1. Construct Firebase Credential
            val credential = PhoneAuthProvider.getCredential(verificationId, userCode)
            
            // 2. Sign in with credential
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user ?: throw Exception("User missing after verification.")

            // 3. Fetch ID Token for backend auth header registration
            val tokenResult = user.getIdToken(true).await()
            val token = tokenResult.token ?: ""

            Result.success(token)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Explicitly handle standard Firebase invalid OTP code
            Result.failure(Exception("Incorrect OTP code. Please try again."))
        } catch (e: Exception) {
            // General catch-all for network timeouts or expired credentials
            Result.failure(e)
        }
    }
}