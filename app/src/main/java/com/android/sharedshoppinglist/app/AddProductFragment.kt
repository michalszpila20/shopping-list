package com.android.sharedshoppinglist.app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.databinding.DataBindingUtil
import com.android.sharedshoppinglist.R
import com.android.sharedshoppinglist.database.DatabaseHelper
import com.android.sharedshoppinglist.databinding.FragmentAddProductBinding

class AddProductFragment : Fragment() {

    private lateinit var listId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            listId = requireArguments().getString("listId").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentAddProductBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_product, container,false)

        val spinner : Spinner = binding.spinner2

        activity?.let {
            ArrayAdapter.createFromResource(
                it.applicationContext,
                R.array.categories,
                android.R.layout.simple_spinner_item

            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }
        }

        binding.addBtn.setOnClickListener {
            val db = activity?.let { it1 -> DatabaseHelper(it1) }

            val name = binding.productNameEditText.text.toString()
            val quantity = binding.productQuantityEditText.text.toString()
            val store = binding.storeEditText.text.toString()
            val category = spinner.selectedItem.toString()

            db?.insertProduct(name, quantity, store, category, listId)

            binding.productNameEditText.text.clear()
            binding.productQuantityEditText.text.clear()
            binding.storeEditText.text.clear()
        }

        return binding.root
    }

}