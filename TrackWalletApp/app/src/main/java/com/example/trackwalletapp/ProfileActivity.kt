package com.example.trackwalletapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.trackwalletapp.databinding.ActivityProfileBinding
import com.example.trackwalletapp.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance("https://trackwalletapp-b9268-default-rtdb.asia-southeast1.firebasedatabase.app")
        reference = database.getReference("users")

        // Get the username from the intent
        val username = intent.getStringExtra("username")

        val usernameTextView: TextView = findViewById(R.id.usernameTextView)
        usernameTextView.text = "Welcome, $username!"
        // Fetch data from the database based on the username
        reference.child(username.orEmpty()).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Assuming you have a User data class
                val user = snapshot.getValue(User::class.java)

                // Display the user data in your UI
                if (user != null) {
                    binding.userUsername.text = "Username: ${user.username.orEmpty()}"
                    binding.userEmail.text = "Email: ${user.email.orEmpty()}"
                    // Display other user data as needed
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors if any
            }
        })
    }
}
