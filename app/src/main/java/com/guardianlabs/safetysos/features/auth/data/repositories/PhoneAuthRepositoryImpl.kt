package com.guardianlabs.safetysos.features.auth.data.repositories

import android.app.Activity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.guardianlabs.safetysos.features.auth.domain.repositories.PhoneAuthRepository
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

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
                    // Auto-retrieval scenario (optional handling)
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
    ): Result<String> = suspendCancellableCoroutine { continuation ->
        val credential = PhoneAuthProvider.getCredential(verificationId, userCode)

        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.user?.getIdToken(true)
                        ?.addOnSuccessListener { result ->
                            val token = result.token ?: ""
                            continuation.resume(Result.success(token))
                        }
                        ?.addOnFailureListener { exception ->
                            continuation.resume(Result.failure(exception))
                        }
                } else {
                    val error = task.exception ?: Exception("Invalid OTP code.")
                    continuation.resume(Result.failure(error))
                }
            }
    }
}
