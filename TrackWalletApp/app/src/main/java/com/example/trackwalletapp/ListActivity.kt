package com.example.trackwalletapp

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.trackwalletapp.databinding.ActivityListBinding
import com.example.trackwalletapp.model.TransactionDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityListBinding
    private lateinit var incomeAdapter: ArrayAdapter<String>
    private lateinit var expenseAdapter: ArrayAdapter<String>
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch userId from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")

        // Initialize adapters
        incomeAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        expenseAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)

        // Set the adapters to the ListViews
        binding.incomeListView.adapter = incomeAdapter
        binding.expenseListView.adapter = expenseAdapter

        // Initialize Firebase Realtime Database reference
        databaseReference =
            FirebaseDatabase.getInstance("https://trackwalletapp-b9268-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("transactions/$userId")

        // Retrieve data from Firebase and update the adapters for Income and Expense
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                updateAdapter(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error fetching data
            }
        })
    }

    private fun updateAdapter(snapshot: DataSnapshot) {
        incomeAdapter.clear()
        expenseAdapter.clear()

        for (categoryTypeSnapshot in snapshot.children) {
            val categoryType = categoryTypeSnapshot.key

            for (categorySnapshot in categoryTypeSnapshot.children) {
                for (transactionSnapshot in categorySnapshot.children) {
                    val transactionDetails =
                        transactionSnapshot.getValue(TransactionDetails::class.java)

                    if (transactionDetails != null) {
                        // Extract specific details from the TransactionDetails instance
                        val price = transactionDetails.price
                        val reference = transactionDetails.reference
                        val date = transactionDetails.date

                        // Extract category information
                        val category = categorySnapshot.key

                        // Check for null values and type conversions
                        val formattedTransaction = buildString {
                            append("\n$category\n")
                            append("RM $price | ")
                            append("$reference | ")
                            append("$date\n")
                        }

                        if ("Income" == categoryType) {
                            incomeAdapter.add(formattedTransaction)
                        } else if ("Expense" == categoryType) {
                            expenseAdapter.add(formattedTransaction)
                        }
                    }
                }
            }
        }

        incomeAdapter.notifyDataSetChanged()
        expenseAdapter.notifyDataSetChanged()


        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.add

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
                        this, R.anim.slide_in_right, R.anim.slide_out_left
                    )
                    startActivity(
                        Intent(applicationContext, ExpenseIncomeActivity::class.java),
                        options.toBundle()
                    )
                    finish()
                    true
                }

                R.id.statistics -> {
                    // No need to recreate the same activity
                    val options = ActivityOptions.makeCustomAnimation(
                        this, R.anim.slide_in_right, R.anim.slide_out_left
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
                        this, R.anim.slide_in_right, R.anim.slide_out_left
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
}
