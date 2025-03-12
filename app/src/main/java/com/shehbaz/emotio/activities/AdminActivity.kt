package com.shehbaz.emotio.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.shehbaz.emotio.databinding.AdminActivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shehbaz.emotio.R
import com.shehbaz.emotio.adaptors.AnalystAdapter
import com.shehbaz.emotio.models.Analyst

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: AdminActivityBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "AdminActivity"
    private val analystList = mutableListOf<Analyst>()
    private lateinit var adapter: AnalystAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = AnalystAdapter(analystList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        loadAnalysts()
    }

    private fun loadAnalysts() {
        firestore.collection("users")
            .whereEqualTo("role", "analyst")
            .get()
            .addOnSuccessListener { querySnapshot ->
                analystList.clear()
                for (doc in querySnapshot.documents) {
                    try {
                        val analyst = Analyst(
                            uid = doc.id,
                            name = doc.getString("name") ?: "",
                            age = (doc.getLong("age") ?: 0L).toInt(),
                            gender = doc.getString("gender") ?: "",
                            cell = doc.getString("cell") ?: ""
                        )
                        analystList.add(analyst)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error parsing analyst data", e)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load analysts", Toast.LENGTH_LONG).show()
                Log.e(TAG, "Error loading analysts", e)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_sign_out) {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
