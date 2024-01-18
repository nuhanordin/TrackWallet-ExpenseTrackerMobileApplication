package com.example.trackwalletapp

import android.app.ActivityOptions
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.trackwalletapp.databinding.ActivityExpenseIncomeBinding
import com.example.trackwalletapp.databinding.DialogAddBinding
import com.example.trackwalletapp.databinding.DialogAddIncomeExpenseBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ExpenseIncomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityExpenseIncomeBinding
    private lateinit var adapter: ArrayAdapter<String>
    private var selectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseIncomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch userId from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)

        val buttonExpense = binding.button
        val buttonIncome = binding.button2
        val buttonAdd = binding.button3

        buttonExpense.setOnClickListener {
            selectedCategory = "Expense"
            showList("Expense", listOf("Travel", "Utility", "Food"), userId)
            buttonAdd.visibility = View.VISIBLE
        }

        buttonIncome.setOnClickListener {
            selectedCategory = "Income"
            showList("Income", listOf("Salary", "Part Time", "Allowance"), userId)
            buttonAdd.visibility = View.VISIBLE
        }

        buttonAdd.setOnClickListener {
            if (selectedCategory != null) {
                showAddDialog(userId)
            } else {
                // Handle the case where no category is selected
            }
        }

        val buttonShowList = binding.buttonShowList
        buttonShowList.setOnClickListener {
            // Add any data you want to pass to ListActivity using Intent extras
            val intent = Intent(this, ListActivity::class.java)
            startActivity(intent)
        }

        binding.gridView.setOnItemClickListener { _, _, position, _ ->
            val category = adapter.getItem(position)
            val transactionType = when {
                category in listOf("Salary", "Part Time", "Allowance") -> "Income"
                else -> "Expense"
            }

            // Retrieve userId from SharedPreferences
            val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
            val userId = sharedPreferences.getString("userId", "")

            // Check if userId is valid before calling the method
            if (userId.isNullOrEmpty()) {
                // Handle the case where userId is null or empty (e.g., show an error message)
                Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show()
            } else {
                showAddTransactionDialog(category, transactionType, userId)
            }
        }

        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setSelectedItemId(R.id.add)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    // Replace the overridePendingTransition method with ActivityOptions
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
                    startActivity(Intent(applicationContext, HomeActivity::class.java), options.toBundle())
                    finish()

                    true
                }
                R.id.add -> {
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
                    val options = ActivityOptions.makeCustomAnimation(
                        this,
                        R.anim.slide_in_right,
                        R.anim.slide_out_left
                    )
                    startActivity(
                        Intent(applicationContext, SettingActivity::class.java),
                        options.toBundle()
                    )
                    finish()

                    true
                }
                else -> false
            }
        }
    }


    private fun showList(category: String, itemList: List<String>, userId: String?) {
        // Create a set to store unique items
        val uniqueItems = HashSet<String>()

        // Add the items from the existing list to the set
        uniqueItems.addAll(itemList)

        // Fetch the items from the database and add them to the set
        val database = FirebaseDatabase.getInstance("https://trackwalletapp-b9268-default-rtdb.asia-southeast1.firebasedatabase.app")
        val categoryReference = database.getReference("transactions/$userId/$category")

        categoryReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val itemName = childSnapshot.key
                    if (itemName != null) {
                        // Add only if it's not already in the set
                        uniqueItems.add(itemName)
                    }
                }

                // Clear the existing adapter
                adapter.clear()

                // Add items from the set to the adapter
                adapter.addAll(uniqueItems.toList())

                // Update the GridView adapter
                binding.gridView.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database read error if needed
            }
        })
    }



    private fun showAddDialog(userId: String?) {
        val dialogBinding = DialogAddBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this)
            .setTitle("Add New Category")
            .setView(dialogBinding.root)

        val editTextCategory = dialogBinding.editTextCategory
        val buttonAddTransaction = dialogBinding.buttonAddTransaction

        val dialog = builder.create()

        buttonAddTransaction.setOnClickListener {
            val newCategory = editTextCategory.text.toString().trim()

            if (newCategory.isNotEmpty()) {
                // Store the new category under the selected buttonExpense/buttonIncome node
                val database = FirebaseDatabase.getInstance("https://trackwalletapp-b9268-default-rtdb.asia-southeast1.firebasedatabase.app")
                val categoryReference = database.getReference("transactions/$userId/$selectedCategory")
                categoryReference.child(newCategory).setValue(true)

                // Update the list
                showList(selectedCategory!!, listOf(newCategory), userId)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showAddTransactionDialog(category: String?, transactionType: String?, userId: String?) {
        val dialogBinding = DialogAddIncomeExpenseBinding.inflate(LayoutInflater.from(this))
        val builder = AlertDialog.Builder(this)
            .setTitle("Add Transaction: $category")
            .setView(dialogBinding.root)

        val editTextPrice = dialogBinding.editTextPrice
        val editTextReference = dialogBinding.editTextReference
        val editTextDate = dialogBinding.editTextDate
        val buttonAddTransaction = dialogBinding.buttonAddTransaction
        val buttonOpenDatePicker = dialogBinding.buttonOpenDatePicker

        val dialog = builder.create()

        // Open date picker when the button is clicked
        buttonOpenDatePicker.setOnClickListener {
            showDatePickerDialog(dialogBinding.editTextDate)
        }

        buttonAddTransaction.setOnClickListener {
            val priceString = editTextPrice.text.toString().trim()
            val reference = editTextReference.text.toString().trim()
            val date = editTextDate.text.toString().trim()

            if (priceString.isNotEmpty() && reference.isNotEmpty() && date.isNotEmpty()) {
                try {
                    // Convert price to Double
                    val priceValue = priceString.toDouble()

                    // Convert date to a format suitable for Firebase (assuming yyyy-MM-dd)
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val formattedDate = dateFormat.format(Date())

                    // Create a Map to store the transaction details
                    val transactionMap = mapOf(
                        "price" to priceValue,
                        "reference" to reference,
                        "date" to formattedDate
                    )

                    // Store the data in Firebase Realtime Database
                    val database =
                        FirebaseDatabase.getInstance("https://trackwalletapp-b9268-default-rtdb.asia-southeast1.firebasedatabase.app")

                    val transactionsReference = if (transactionType == "Income") {
                        database.getReference("transactions/$userId/Income")
                    } else {
                        database.getReference("transactions/$userId/Expense")
                    }

                    // Specify a new child node under the category with transaction details
                    val categoryReference = transactionsReference.child(category ?: "")

                    // Check if the category exists in the list
                    if (categoryReference.key == null) {
                        // If not, add it to the database
                    }

                    val newTransactionReference = categoryReference.push()

                    // Add transaction details to the database
                    newTransactionReference.setValue(transactionMap)

                    // Open ListActivity with category and transaction details
                     val intent = Intent(this, ListActivity::class.java)
                     intent.putExtra("category", category)
                     startActivity(intent)
                     dialog.dismiss()
                   // val intent = Intent(this, ListActivity::class.java)
                   // startActivity(intent)
                } catch (e: NumberFormatException) {
                    // Handle the case where the entered price is not a valid double
                    Log.e("ExpenseIncomeActivity", "Invalid price format: $priceString")
                    // You may want to show a message to the user
                }
            }
        }
        dialog.show()
    }

    private fun showDatePickerDialog(editTextDate: EditText) {
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                editTextDate.setText(formattedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }
}