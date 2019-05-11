package com.borges.consulting

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private val DOMAIN_NAME = "gmail.com"
    private val TAG = "RegisterActivity"
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val email get() = textEmail.text.toString()
    private val password get() = textPassword.text.toString()
    private val confirmPassword get() = textPasswordConfirm.text.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        buttonRegister.setOnClickListener {
            if (formIsValid()) {
                hideSoftKeyboard()
                registerNewEmail()
            }
        }
    }

    private fun registerNewEmail() {
        progressBarRegister.visibility = ProgressBar.VISIBLE

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            Log.d(TAG, "onComplete: ${task.isSuccessful}")

            if (task.isSuccessful) {
                Log.d(TAG, "onRegister: success, AuthState: ${firebaseAuth.currentUser?.uid}")
                sendVerificationEmail()
                firebaseAuth.signOut()
            }
            else {
                Log.d(TAG, "onRegister: failed: ${task.exception}")
                showToast("Registration failed. Please try again.")
            }

        }

        progressBarRegister.visibility = ProgressBar.INVISIBLE
    }

    private fun formIsValid(): Boolean {
        return if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
            if (isValidDomain(email)) {
                if (password == confirmPassword) {
                    true
                } else {
                    Log.d(TAG, "onValidate: FAILED - passwords do not match")
                    showToast("Passwords do not match")
                    false
                }
            } else {
                Log.d(TAG, "onValidate: FAILED - email doesn't match domain")
                showToast("Please register with Company email")
                false
            }
        } else {
            Log.d(TAG, "onValidate: FAILED - not all fields filled in")
            showToast("You must fill out all the fields")
            false
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun isValidDomain(email: String): Boolean {
        return Regex("^[A-Za-z0-9._%+-]+@$DOMAIN_NAME$").matches(email)
    }

    private fun sendVerificationEmail() {
        val user = firebaseAuth.currentUser

        user?.sendEmailVerification()?.addOnCompleteListener { task ->
            if (task.isSuccessful)
                showToast("Successfully registered. Please check your email to verify before logging in.")
            else
                showToast("Couldn't send verification email.")
        }
    }

    private fun hideSoftKeyboard() {
        onBackPressed()
    }
}
