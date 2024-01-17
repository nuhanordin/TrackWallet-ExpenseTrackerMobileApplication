package com.example.trackwalletapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.trackwalletapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener


class LoginActivity : AppCompatActivity() {
    private lateinit var loginUsername: EditText
    private lateinit var loginPassword: EditText
    private lateinit var loginButton: Button
    private lateinit var signupRedirectText: TextView
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginUsername = findViewById(R.id.username)
        loginPassword = findViewById(R.id.password)
        loginButton = findViewById(R.id.buttonLogin)
        signupRedirectText = findViewById(R.id.buttonRegister)

        loginButton.setOnClickListener {
            if (!validateUsername() || !validatePassword()) {
                // Handle validation failure if needed
            } else {
                checkUser()
            }
        }

        signupRedirectText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateUsername(): Boolean {
        val valStr = loginUsername.text.toString()
        return if (valStr.isEmpty()) {
            loginUsername.error = "Username cannot be empty"
            false
        } else {
            loginUsername.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        val valStr = loginPassword.text.toString()
        return if (valStr.isEmpty()) {
            loginPassword.error = "Password cannot be empty"
            false
        } else {
            loginPassword.error = null
            true
        }
    }

    private fun checkUser() {
        val userUsername = loginUsername.text.toString().trim().toLowerCase()
        val userPassword = loginPassword.text.toString().trim()
        database = FirebaseDatabase.getInstance("https://trackwalletapp-b9268-default-rtdb.asia-southeast1.firebasedatabase.app")
        reference = database.getReference("users")

        val checkUserDatabase: Query = reference.orderByChild("username").equalTo(userUsername)

        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var userFound = false

                for (userSnapshot in snapshot.children) {
                    val usernameFromDB = userSnapshot.child("username").getValue(String::class.java)

                    if (usernameFromDB?.toLowerCase() == userUsername) {
                        userFound = true

                        val userId = userSnapshot.key
                        val passwordFromDB = userSnapshot.child("password").getValue(String::class.java)

                        if (passwordFromDB == userPassword) {
                            val nameFromDB = userSnapshot.child("name").getValue(String::class.java)
                            val emailFromDB = userSnapshot.child("email").getValue(String::class.java)

                            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                            intent.putExtra("name", nameFromDB)
                            intent.putExtra("email", emailFromDB)
                            intent.putExtra("username", usernameFromDB)
                            intent.putExtra("password", passwordFromDB)
                            startActivity(intent)

                            if (userId != null) {
                                saveUserIdToSharedPreferences(userId)
                            }
                        } else {
                            loginPassword.error = "Invalid Credentials"
                            loginPassword.requestFocus()
                        }

                        break  // exit the loop once the user is found
                    }
                }

                if (!userFound) {
                    loginUsername.error = "User does not exist"
                    loginUsername.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error if needed
            }
        })
    }


    private fun saveUserIdToSharedPreferences(userId: String?) {
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.putString("userId", userId)
        editor.apply()
    }

}
