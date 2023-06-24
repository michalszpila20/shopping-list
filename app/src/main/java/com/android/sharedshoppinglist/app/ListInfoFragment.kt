package com.android.sharedshoppinglist.app

import ProductAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.sharedshoppinglist.R
import com.android.sharedshoppinglist.database.DatabaseHelper
import com.android.sharedshoppinglist.database.ProductViewModel
import com.android.sharedshoppinglist.databinding.FragmentListInfoBinding

class ListInfoFragment : Fragment() {

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
        // Inflate the layout for this fragment

        val binding: FragmentListInfoBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_list_info, container,false)

        val bundleListId = bundleOf("listId" to listId)

        binding.listInfoAddProductBtn.setOnClickListener {
            view?.findNavController()?.popBackStack(R.id.newListFragment, false)
            view?.findNavController()?.navigate(R.id.action_listInfoFragment_to_addProductFragment, bundleListId)
        }

        return binding.root
    }


    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerview2)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data = ArrayList<ProductViewModel>()

        val db = context?.let { DatabaseHelper(it.applicationContext) }

        if (arguments != null) {
            listId = requireArguments().getString("listId").toString()
        }

        var cursor = db?.getProductsFromList(listId)

        if (cursor != null) {
            while (cursor.moveToNext())
                data.add(ProductViewModel(
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_PRODUCT)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_STORE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CATEGORY))
                ))
        }

        val adapter = ProductAdapter(data)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : ProductAdapter.OnNoteListener{
            override fun onNoteListener(position: Int) {

                val bundle = Bundle()
                bundle.putString("productId", data[position].productId)

                view.findNavController().navigate(R.id.action_listInfoFragment_to_productInfoFragment, bundle)
            }

        })
    }

}