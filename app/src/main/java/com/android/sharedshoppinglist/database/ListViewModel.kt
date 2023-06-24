package com.android.sharedshoppinglist.database

data class ListViewModel(
    val listId: String,
    val listName: String,
    val date: String,
    val time: String,
    val reminder: String,
    val code: Int
)
