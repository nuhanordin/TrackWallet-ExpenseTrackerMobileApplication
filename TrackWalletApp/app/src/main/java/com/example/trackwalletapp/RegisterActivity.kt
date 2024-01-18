package com.example.trackwalletapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.trackwalletapp.model.User
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var inputFullName: EditText
    private lateinit var inputUsername: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var loginRedirectText: TextView
    private lateinit var registerButton: Button
    private lateinit var database: FirebaseDatabase
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        inputFullName = findViewById(R.id.fullname)
        inputEmail = findViewById(R.id.email)
        inputUsername = findViewById(R.id.username)
        inputPassword = findViewById(R.id.password)
        loginRedirectText = findViewById(R.id.buttonLogin)
        registerButton = findViewById(R.id.buttonRegister)

        registerButton.setOnClickListener {
            database = FirebaseDatabase.getInstance("https://trackwalletapp-b9268-default-rtdb.asia-southeast1.firebasedatabase.app")
            reference = database.getReference("users")

            val email = inputEmail.text.toString()
            val name = inputFullName.text.toString()
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()

            val helperClass = User(email, name, password, username)
            reference.child(username).setValue(helperClass)

            Toast.makeText(this, "You have signed up successfully!", Toast.LENGTH_SHORT).show()

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        loginRedirectText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}