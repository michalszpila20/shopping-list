package com.android.sharedshoppinglist.database

data class ProductViewModel(
    val productId: String,
    val productName: String,
    val quantity: String,
    val store: String,
    val category: String
)