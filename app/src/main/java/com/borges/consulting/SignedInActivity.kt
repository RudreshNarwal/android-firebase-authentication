package com.borges.consulting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_logged_in.*

class SignedInActivity : AppCompatActivity() {
    private val TAG = "SignedInActivity"
    private val firebaseAuth get() = FirebaseAuth.getInstance()
    private val user get() = firebaseAuth.currentUser

    private lateinit var textMessage: TextView
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                textMessage.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                textMessage.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                textMessage.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logged_in)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        textMessage = findViewById(R.id.message)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        buttonSignOut.setOnClickListener {
            firebaseAuth.signOut()
            returnToLogin()
        }

//        setUserDetails()
        getUserDetails()
    }

    override fun onResume() {
        super.onResume()
        checkAuthenticationState()
    }

    private fun setUserDetails() {
        user?.let {
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("Derek Borges")
                .setPhotoUri(Uri.parse("https://images.pexels.com/photos/555790/pexels-photo-555790.png?auto=compress&cs=tinysrgb&dpr=1&w=500"))
                .build()

            it.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "onComplete: User profile updated.")
                    getUserDetails()
                }
            }
        }

    }

    private fun getUserDetails() {
        user?.let {

            val uid = it.uid
            val name = it.displayName
            val email = it.email
            val photoUrl = it.photoUrl

            Log.d(TAG, "getUserDetails: properties: \n uid: $uid\n name: $name\n email: $email\n photuUrl: $photoUrl")
        }
    }

    private fun checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state.")

        user?.let {
            Log.d(TAG, "checkAuthenticationState: user is authenticated.")
        } ?: let {
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.")

            returnToLogin()
        }
    }

    private fun returnToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
