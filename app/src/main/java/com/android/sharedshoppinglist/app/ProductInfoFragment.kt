package com.android.sharedshoppinglist.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.android.sharedshoppinglist.R
import com.android.sharedshoppinglist.database.DatabaseHelper
import com.android.sharedshoppinglist.database.ProductViewModel
import com.android.sharedshoppinglist.databinding.FragmentProductInfoBinding

class ProductInfoFragment : Fragment() {

    private lateinit var productId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            productId = requireArguments().getString("productId").toString()
        }

    }

    @SuppressLint("Range")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentProductInfoBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_product_info, container,false)

        val data = ArrayList<ProductViewModel>()

        val db = context?.let { DatabaseHelper(it.applicationContext) }

        val cursor = db?.getProduct(productId)


        if (cursor != null) {
            while (cursor.moveToNext())
                data.add(
                    ProductViewModel(
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_PRODUCT)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STORE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY))
                )
                )
        }

        binding.textViewProductName.text = "Product name: ${data[0].productName}"
        binding.textViewProductQuantity.text = "Quantity: ${data[0].quantity}"
        binding.textViewProductStore.text = "Store: ${data[0].store}"

        binding.textViewProductName
        return binding.root
    }

}