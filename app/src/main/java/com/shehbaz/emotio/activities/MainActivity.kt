package com.shehbaz.emotio.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 123
    private val TAG = "MainActivity"
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No layout is needed since FirebaseUI will show its own screens.
        if (FirebaseAuth.getInstance().currentUser == null) {
            launchSignInFlow()
        } else {
            checkUserRoleAndProceed()
        }
    }

    private fun launchSignInFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build()
        )
        // Create and launch sign-in intent.
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                checkUserRoleAndProceed()
            } else {
                // Sign in failed.
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Sign in unsuccessful: ${response?.error?.errorCode}")
//                finish()
            }
        }
    }

    private fun checkUserRoleAndProceed() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val uid = it.uid
            val userRef = firestore.collection("users").document(uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role")
                    Toast.makeText(this, "User role $role", Toast.LENGTH_LONG)
                    if (role == "admin") {
                        startActivity(Intent(this, AdminActivity::class.java))
                    } else {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
                } else {
                    // If no user document exists, assume the user is an analyst.
                    // Create a new document with default role "analyst"
                    val newUser = hashMapOf(
                        "role" to "analyst",
                        "name" to "",
                        "age" to 0,
                        "gender" to "",
                        "cell" to ""
                    )
                    userRef.set(newUser).addOnSuccessListener {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }.addOnFailureListener { e ->
                        Toast.makeText(this, "Error creating user profile", Toast.LENGTH_LONG).show()
                        Log.e(TAG, "Error creating user profile", e)
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error fetching user role", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error fetching user role", e)
            }
        }
    }
}
