package com.example.trackwalletapp

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.trackwalletapp.databinding.ActivitySettingBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class SettingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingBinding
    private lateinit var darkModeSwitch: Switch
    private lateinit var rootView: View
    private var isDarkMode: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userId = getUserIdFromSharedPreferences()
        fetchUserData(userId)

        darkModeSwitch = findViewById(R.id.darkModeSwitch)
        rootView = findViewById(android.R.id.content)

        setupViews()
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setSelectedItemId(R.id.profile)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val options = ActivityOptions.makeCustomAnimation(
                        this,
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    startActivity(
                        Intent(applicationContext, HomeActivity::class.java),
                        options.toBundle()
                    )
                    finish()
                    true
                }

                R.id.add -> {
                    val options = ActivityOptions.makeCustomAnimation(
                        this,
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    startActivity(
                        Intent(applicationContext, ExpenseIncomeActivity::class.java),
                        options.toBundle()
                    )
                    finish()
                    true
                }

                R.id.statistics -> {
                    val options = ActivityOptions.makeCustomAnimation(
                        this,
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    startActivity(
                        Intent(applicationContext, StatisticActivity::class.java),
                        options.toBundle()
                    )
                    finish()
                    true
                }
                R.id.profile -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchUserData(userId: String) {
        Log.d("SettingActivity", "Fetching data for userId: $userId")

        val database =
            FirebaseDatabase.getInstance("https://trackwalletapp-b9268-default-rtdb.asia-southeast1.firebasedatabase.app")
        val usersReference = database.getReference("users")

        val userQuery: Query = usersReference.orderByChild("username").equalTo(userId)

        userQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("SettingActivity", "onDataChange invoked")

                if (snapshot.exists()) {
                    Log.d("SettingActivity", "Snapshot exists")
                    val userData = snapshot.children.first()
                    Log.d("SettingActivity", "User data: $userData")

                    // Retrieve user details from snapshot
                    val username = userData.child("username").getValue(String::class.java) ?: ""
                    val email = userData.child("email").getValue(String::class.java) ?: ""
                    val name = userData.child("name").getValue(String::class.java) ?: ""

                    // Update UI with user details
                    Log.d("SettingActivity", "Before updateUIWithUserData - Email: $email, Name: $name")
                    updateUIWithUserData(email, name)
                    Log.d("SettingActivity", "After updateUIWithUserData")
                } else {
                    Log.d("SettingActivity", "Snapshot does not exist")

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SettingActivity", "Error fetching user data: ${error.message}")
            }
        })
    }

    private fun setupViews() {
        // Set person name
        val userId = getUserIdFromSharedPreferences()

        // Set person name
        binding.personName.text = userId
        // Set dark mode switch listener
        binding.darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Handle dark mode switch changes
            if (isChecked) {
                enableDarkMode()
            } else {
                disableDarkMode()
            }
        }

        // Set logout button click listener
        binding.logoutButton.setOnClickListener {
            clearUsernameFromSharedPreferences()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        binding.darkModeSwitch.isChecked = isDarkMode
    }

    private fun updateUIWithUserData(email: String, name: String) {
        Log.d("SettingActivity", "Updating UI - Email: $email, Name: $name")

        // Update UI elements with user data
        binding.personName.text = name
        binding.personEmail.text = email
        // Add similar updates for other UI elements as needed
    }

    private fun enableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        isDarkMode = true    }

    private fun disableDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        isDarkMode = false    }

    private fun getUserIdFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        return sharedPreferences.getString("userId", "") ?: ""
    }

    private fun clearUsernameFromSharedPreferences() {
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("username")
        editor.apply()
    }
}
