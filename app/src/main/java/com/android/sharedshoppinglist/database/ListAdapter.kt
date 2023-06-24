package com.android.sharedshoppinglist.database

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.android.sharedshoppinglist.alarm.AlarmReceiver
import com.android.sharedshoppinglist.R

class ListAdapter(private val mList: ArrayList<ListViewModel>) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private lateinit var date: String
    private lateinit var time: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_design, parent, false)

        return ViewHolder(view, mListener)
    }

    // binds the list items to a view
    @SuppressLint("Range")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val listsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.textView.text = listsViewModel.listName

        if (listsViewModel.reminder == "On"){
            holder.textView.setTextColor(Color.parseColor("#0000FF"))
        }else{
            holder.textView.setTextColor(Color.parseColor("#000000"))
        }

        val context = holder.textView.context

        val db = DatabaseHelper(context)

        holder.alarmButton.setOnClickListener {

            val cursor : Cursor? = db.getListRow(listsViewModel.listId)

            if (cursor!!.count > 0) {
                cursor.moveToFirst()

                if(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_REMINDER)) == "On"){
                    date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE))
                    time = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TIME))
                    Toast.makeText(context, "Alarm is set for: $date $time", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(context, "Alarm is not set for this list", Toast.LENGTH_SHORT).show()
                }
            }
            cursor.close()
        }

        holder.deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to delete this list?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    // Delete selected note from database
                    val cursor : Cursor? = db.getListRow(listsViewModel.listId)

                    if (cursor!!.count > 0) {
                        cursor.moveToFirst()

                        val requestId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CODE))
                        Toast.makeText(context, "Request code2: $requestId", Toast.LENGTH_SHORT).show()


                        val intent = Intent(context.applicationContext, AlarmReceiver::class.java)

                        val alarmManager =
                            context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                        val pendingIntent =
                            PendingIntent.getBroadcast(context.applicationContext, requestId, intent,
                                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)

                        if (pendingIntent != null && alarmManager != null) {
                            alarmManager.cancel(pendingIntent)
                            pendingIntent.cancel()
                        }


                        cursor.close()

                        db.deleteList(listsViewModel.listId)
                        db.deleteAllProductsFromList(listsViewModel.listId)

                        mList.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, mList.size)

                    }

                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()

        }

    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View, listener: OnNoteListener) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.textViewListName)
        val alarmButton: ImageButton = itemView.findViewById(R.id.imageButtonAlarm)
        val deleteButton: ImageButton = itemView.findViewById(R.id.imageButtonDelete)

        init {
            itemView.setOnClickListener{
                listener.onNoteListener(bindingAdapterPosition)
            }
        }
    }

    private lateinit var mListener: OnNoteListener

    interface OnNoteListener{
        fun onNoteListener(position: Int)
    }

    fun setOnItemClickListener(listener: OnNoteListener){
        mListener = listener
    }
}