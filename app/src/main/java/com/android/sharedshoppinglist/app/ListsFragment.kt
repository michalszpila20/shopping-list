package com.android.sharedshoppinglist.app

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.sharedshoppinglist.R
import com.android.sharedshoppinglist.database.DatabaseHelper
import com.android.sharedshoppinglist.database.ListAdapter
import com.android.sharedshoppinglist.database.ListViewModel
import java.util.*
import kotlin.collections.ArrayList

class ListsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lists, container, false)
    }

    @SuppressLint("Range")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView:RecyclerView=view.findViewById(R.id.recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val data = ArrayList<ListViewModel>()

        val db = context?.let { DatabaseHelper(it.applicationContext) }
        var cursor = db?.getList()

        if (cursor != null) {
            while (cursor.moveToNext())
                data.add(ListViewModel(
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_LIST)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME_LIST)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME)),
                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REMINDER)),
                    cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CODE))
                ))
        }

        val adapter = ListAdapter(data)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener(object : ListAdapter.OnNoteListener{
            override fun onNoteListener(position: Int) {

                val bundle = Bundle()
                bundle.putString("listId", data[position].listId)

                view.findNavController().navigate(R.id.action_listsFragment_to_listInfoFragment, bundle)
            }

        })
    }
}