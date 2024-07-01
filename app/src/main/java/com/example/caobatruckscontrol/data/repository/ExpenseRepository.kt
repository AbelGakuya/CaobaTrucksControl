package com.example.caobatruckscontrol.data.repository

import com.example.caobatruckscontrol.data.model.Expense
import com.example.caobatruckscontrol.data.model.Expensee

class ExpenseRepository {
    suspend fun saveExpense(expense: Expensee) {
        // Implement your logic to save the expense
        // This could be a call to a local database or a network request
    }
}