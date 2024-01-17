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
/*class RegisterActivity : AppCompatActivity() {

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    //private val usersRef = FirebaseDatabase.getInstance().getReferenceFromUrl("https://trackwalletapp-b9268-default-rtdb.firebaseio.com/").child("users").apply {
      //  keepSynced(true) // Optional: Keeps local data in sync with the server
   // }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val fullnameEditText: EditText = findViewById(R.id.fullname)
        val emailEditText: EditText = findViewById(R.id.email)
        val usernameEditText: EditText = findViewById(R.id.username)
        val passwordEditText: EditText = findViewById(R.id.password)
        val passwordConfirmEditText: EditText = findViewById(R.id.passwordconfirm)
        val registerButton: Button = findViewById(R.id.buttonRegister)

        registerButton.setOnClickListener {
            val fullname = fullnameEditText.text.toString()
            val email = emailEditText.text.toString()
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val passwordConfirm = passwordConfirmEditText.text.toString()

            if (validateInput(fullname, email, username, password, passwordConfirm)) {
                registerUser(fullname, email, username, password)
            }
        }
    }

    private fun validateInput(
        fullname: String,
        email: String,
        username: String,
        password: String,
        passwordConfirm: String
    ): Boolean {
        // Add your own validation logic
        // For simplicity, this example assumes all fields are required
        if (fullname.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty() || passwordConfirm.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != passwordConfirm) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun registerUser(fullname: String, email: String, username: String, password: String) {
        val userId = usersRef.push().key

        if (userId != null) {
            val user = User(fullname, email, username, password)
            usersRef.child(userId).setValue(user)
            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
            Log.d("RegisterActivity", "User registered successfully. userId: $userId")
        } else {
            Log.e("RegisterActivity", "userId is null")
        }
    }


}*/