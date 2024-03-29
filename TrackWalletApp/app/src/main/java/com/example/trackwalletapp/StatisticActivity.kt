package com.example.trackwalletapp

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.trackwalletapp.databinding.ActivityStatisticBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class StatisticActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStatisticBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database =
            FirebaseDatabase.getInstance("https://trackwalletapp-b9268-default-rtdb.asia-southeast1.firebasedatabase.app")
        val sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId", "")

        val expensesReference = database.getReference("transactions/$userId/Expense")
        val incomeReference = database.getReference("transactions/$userId/Income")

        val progressBarCombined = binding.progressBarCombined

        expensesReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(expenseSnapshot: DataSnapshot) {
                incomeReference.addValueEventListener(object : ValueEventListener {
                    @SuppressLint("SetTextI18n")
                    override fun onDataChange(incomeSnapshot: DataSnapshot) {
                        var totalExpenseAmount = 0.0
                        var totalIncomeAmount = 0.0

                        for (categorySnapshot in expenseSnapshot.children) {
                            for (expense in categorySnapshot.children) {
                                val expenseAmount =
                                    expense.child("price").getValue(Double::class.java) ?: 0.0
                                totalExpenseAmount += expenseAmount
                            }
                        }

                        for (categorySnapshot in incomeSnapshot.children) {
                            for (income in categorySnapshot.children) {
                                val incomeAmount =
                                    income.child("price").getValue(Double::class.java) ?: 0.0
                                totalIncomeAmount += incomeAmount
                            }
                        }

                        // Calculate progress percentage
                        val progress = ((totalExpenseAmount / totalIncomeAmount) * 100).toInt()

                        // Set progress and max values
                        progressBarCombined.progress = progress
                        val balance = totalIncomeAmount - totalExpenseAmount

                        binding.textExpenses.text = "Expenses: RM ${String.format("%.2f", totalExpenseAmount)}"
                        binding.textIncome.text = "Incomes: RM ${String.format("%.2f", totalIncomeAmount)}"
                        binding.textBalance.text = "Balance: RM ${String.format("%.2f", balance)}"

                        Log.d("StatisticActivity", "Total Expense Amount: $totalExpenseAmount")
                        Log.d("StatisticActivity", "Total Income Amount: $totalIncomeAmount")
                        Log.d("StatisticActivity", "Balance: $balance")

                        binding.textExpenses.text = "Expenses: RM ${String.format("%.2f", totalExpenseAmount)}"
                        binding.textIncome.text = "Incomes: RM ${String.format("%.2f", totalIncomeAmount)}"
                        binding.textBalance.text = "Balance: RM ${String.format("%.2f", balance)}"
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ExpenseActivity", "Error getting income data", error.toException())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ExpenseActivity", "Error getting expense data", error.toException())
            }
        })


        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.selectedItemId = R.id.statistics

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    val options = ActivityOptions.makeCustomAnimation(
                        this, R.anim.slide_in_right, R.anim.slide_out_left
                    )
                    startActivity(
                        Intent(applicationContext, HomeActivity::class.java), options.toBundle()
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
                    true
                }

                R.id.profile -> {
                    val options = ActivityOptions.makeCustomAnimation(
                        this, R.anim.slide_in_right, R.anim.slide_out_left
                    )
                    startActivity(
                        Intent(applicationContext, SettingActivity::class.java), options.toBundle()
                    )
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
