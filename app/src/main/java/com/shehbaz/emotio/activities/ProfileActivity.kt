package com.shehbaz.emotio.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.shehbaz.emotio.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shehbaz.emotio.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadProfile()

        binding.btnSave.setOnClickListener {
            saveProfile()
        }

        binding.btnDelete.setOnClickListener {
            deleteProfile()
        }
    }

    private fun loadProfile() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    binding.etName.setText(document.getString("name") ?: "")
                    binding.etAge.setText(document.getLong("age")?.toString() ?: "")
                    binding.etGender.setText(document.getString("gender") ?: "")
                    binding.etCell.setText(document.getString("cell") ?: "")
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error loading profile", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error loading profile", e)
            }
    }

    private fun saveProfile() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid

        val profile = hashMapOf(
            "name" to binding.etName.text.toString(),
            ("age" to binding.etAge.text.toString().toIntOrNull() ?: 0) as Pair<Any, Any>,
            "gender" to binding.etGender.text.toString(),
            "cell" to binding.etCell.text.toString(),
            "role" to "analyst"
        )

        firestore.collection("users").document(uid).set(profile)
            .addOnSuccessListener {
                Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving profile", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error saving profile", e)
            }
    }

    private fun deleteProfile() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid

        firestore.collection("users").document(uid).delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Profile deleted", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error deleting profile", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error deleting profile", e)
            }
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == R.id.action_sign_out) {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
