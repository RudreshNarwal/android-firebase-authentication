package com.borges.consulting

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.dialog_resend_verification.*

class ResendVerificationDialog : DialogFragment() {
    private val TAG = "ResendVerification"
    private val firebaseAuth get() = FirebaseAuth.getInstance()
    private val email get() = textResendEmail.text.toString()
    private val password get() = textResendPassword.text.toString()
    private val context get() = activity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.dialog_resend_verification, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonConfirmResendEmail.setOnClickListener {
            Log.d(TAG, "onClick: attempting to resend verification email.")

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authenticateAndResendEmail()
            } else {
                showToast("All fields must be filled out.")
            }
        }

        buttonCancelResend.setOnClickListener { dialog.dismiss() }
    }

    private fun authenticateAndResendEmail() {
        val credential = EmailAuthProvider.getCredential(email, password)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "onComplete: re-authenticate success.")
                sendVerificationEmail()
                firebaseAuth.signOut()
                dialog.dismiss()
            }
        }
            .addOnFailureListener { exception ->
                Log.d(TAG, "onFailure: ${exception.message}")
                showToast("Invalid Credentials \nReset your password and try again.")
                dialog.dismiss()
            }
    }

    private fun sendVerificationEmail() {
        val user = firebaseAuth.currentUser
        user?.sendEmailVerification()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

}