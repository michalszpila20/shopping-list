import android.app.AlertDialog
import com.android.sharedshoppinglist.database.ProductViewModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.sharedshoppinglist.R
import com.android.sharedshoppinglist.database.DatabaseHelper

class ProductAdapter(private var mList: ArrayList<ProductViewModel>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_product, parent, false)

        return ViewHolder(view, mListener)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val productsViewModel = mList[position]

        // sets the text to the textview from our itemHolder class
        holder.productName.text = productsViewModel.productName
        holder.productQuantity.text = productsViewModel.quantity
        holder.productStore.text = productsViewModel.store
        holder.productCategory.text = productsViewModel.category

        val context = holder.productName.context

        holder.deleteProductButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to delete this product?")
                .setCancelable(false)
                .setPositiveButton("Yes") { _, _ ->
                    // Delete selected note from database
                    val db = DatabaseHelper(context)
                    db.deleteProduct(productsViewModel.productId)

                    mList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, mList.size)
                }
                .setNegativeButton("No") { dialog, _ ->
                    // Dismiss the dialog
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
        val productName : TextView = itemView.findViewById(R.id.textViewName)
        val productQuantity : TextView = itemView.findViewById(R.id.textViewQuantity)
        val productStore : TextView = itemView.findViewById(R.id.textViewStore)
        val productCategory : TextView = itemView.findViewById(R.id.textViewCategory)
        val deleteProductButton : ImageButton = itemView.findViewById(R.id.imageButtonDeleteProduct)

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