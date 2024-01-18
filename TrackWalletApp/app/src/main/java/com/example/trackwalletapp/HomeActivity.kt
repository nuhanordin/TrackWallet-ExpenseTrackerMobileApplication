package com.example.trackwalletapp

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackwalletapp.databinding.ActivityHomeBinding
import com.example.trackwalletapp.databinding.ItemCategoryExpenseBinding
import com.example.trackwalletapp.model.TransactionDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var categoryExpenseAdapter: CategoryExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch userId from SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")
        binding.user.text = "Hi, $userId!"

        // Initialize RecyclerView
        categoryExpenseAdapter = CategoryExpenseAdapter()
        binding.expensesRecyclerView.adapter = categoryExpenseAdapter

        // Fetch total expenses
        fetchTotalExpenses(userId)

        // Fetch categories with details
        fetchCategoriesWithDetails(userId)

        // Initialize bottom navigation view
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setSelectedItemId(R.id.home)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    true
                }

                R.id.add -> {
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
                    startActivity(Intent(applicationContext, ExpenseIncomeActivity::class.java), options.toBundle())
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
                    val options = ActivityOptions.makeCustomAnimation(this, R.anim.slide_in_right, R.anim.slide_out_left)
                    startActivity(Intent(applicationContext, SettingActivity::class.java), options.toBundle())
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun fetchTotalExpenses(userId: String?) {
        val database =
            FirebaseDatabase.getInstance("https://trackwalletapp-b9268-default-rtdb.asia-southeast1.firebasedatabase.app")
        val expensesReference = database.getReference("transactions/$userId/Expense")

        expensesReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalExpense = 0.0

                for (categorySnapshot in snapshot.children) {
                    for (transactionSnapshot in categorySnapshot.children) {
                        try {
                            val transactionDetails =
                                transactionSnapshot.getValue(TransactionDetails::class.java)

                            // Check if transactionDetails is not null
                            if (transactionDetails != null) {
                                totalExpense += transactionDetails.price
                            }
                        } catch (e: Exception) {
                            Log.e("HomeActivity", "Error converting data: $e")
                        }
                    }
                }

                updateTotalExpenses(totalExpense)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeActivity", "Error fetching total expenses: ${error.message}")
            }
        })
    }

    private fun fetchCategoriesWithDetails(userId: String?) {
        val database =
            FirebaseDatabase.getInstance("https://trackwalletapp-b9268-default-rtdb.asia-southeast1.firebasedatabase.app")
        val expensesReference = database.getReference("transactions/$userId/Expense")
        val gridLayoutManager = GridLayoutManager(this, 2)
        binding.expensesRecyclerView.layoutManager = gridLayoutManager

        expensesReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("HomeActivity", "Received data: $snapshot")

                val categoryDetailsList = mutableListOf<Pair<String, List<TransactionDetails>>>()
                for (categorySnapshot in snapshot.children) {
                    val categoryName = categorySnapshot.key
                    val transactionDetailsList = mutableListOf<TransactionDetails>()

                    for (transactionSnapshot in categorySnapshot.children) {
                        try {
                            val transactionDetails =
                                transactionSnapshot.getValue(TransactionDetails::class.java)

                            // Check if transactionDetails is not null
                            if (transactionDetails != null) {
                                transactionDetailsList.add(transactionDetails)
                            }
                        } catch (e: Exception) {
                            Log.e("HomeActivity", "Error converting data: $e")
                        }
                    }

                    // Check if the transactionDetailsList is not empty before adding it to the categoryDetailsList
                    if (!transactionDetailsList.isEmpty()) {
                        categoryName?.let {
                            categoryDetailsList.add(it to transactionDetailsList)
                        }
                    }
                }
                updateCategoriesWithDetails(categoryDetailsList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeActivity", "Error fetching category details: ${error.message}")
            }
        })
    }


    private fun updateTotalExpenses(totalExpense: Double) {
        val totalExpensesAmountTextView = binding.totalExpensesAmount
        totalExpensesAmountTextView.text = String.format("RM%.2f", totalExpense)
    }

    private fun updateCategoriesWithDetails(categoryDetailsList: List<Pair<String, List<TransactionDetails>>>) {
        categoryExpenseAdapter.setCategoryDetailsList(categoryDetailsList)
    }

    inner class CategoryExpenseAdapter : RecyclerView.Adapter<CategoryExpenseAdapter.ViewHolder>() {
        private var categoryDetailsList: List<Pair<String, List<TransactionDetails>>> = emptyList()

        fun setCategoryDetailsList(newData: List<Pair<String, List<TransactionDetails>>>) {
            categoryDetailsList = newData
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                ItemCategoryExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val (categoryName, transactionDetailsList) = categoryDetailsList[position]
            holder.binding.categoryTextView.text = categoryName

            // Build a string containing details of all transactions for the category
            val transactionsText = transactionDetailsList.joinToString("\n") {
                "Price: RM ${it.price}\nDate: ${it.date}\nReference: ${it.reference}\n"
            }

            holder.binding.expenseTextView.text = transactionsText
        }

        override fun getItemCount(): Int {
            return categoryDetailsList.size
        }

        inner class ViewHolder(val binding: ItemCategoryExpenseBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}

