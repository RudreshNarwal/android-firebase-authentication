package com.borges.consulting

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"

    private val email get() = textLoginEmail.text.toString()
    private val password get() = textLoginPassword.text.toString()

    private val firebaseAuth get() = FirebaseAuth.getInstance()
    private var user: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        buttonOpenRegistration.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        buttonSignIn.setOnClickListener {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                Log.d(TAG, "onClick: attempting to authenticate")

                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            user = firebaseAuth.currentUser

                            user?.let {
                                if (it.isEmailVerified) {
                                    Log.d(TAG, "onAuthStateChanged: signed_in: ${it.uid}")
                                    showToast("Authenticated with: ${it.email}")

                                    startActivity(Intent(this, SignedInActivity::class.java))
                                    finish()

                                } else {
                                    showToast("Check your email inbox for a Verification Link then try again.")
                                    firebaseAuth.signOut()
                                }
                            }

                        } else {
                            Log.w(TAG, "signInWithEmail: failure", task.exception)
                            showToast("Authentication failed.")
                            user = null
                        }
                    }


            } else {
                Toast.makeText(this, "You didn't fill in all the fields.", Toast.LENGTH_LONG).show()
            }
        }

        buttonResendVerification.setOnClickListener {
            val dialog = ResendVerificationDialog()
            dialog.show(supportFragmentManager, "Resend Verification Email")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}
