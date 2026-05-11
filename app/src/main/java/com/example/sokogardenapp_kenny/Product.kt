package com.example.sokogardenapp_kenny

import android.content.Intent
import android.util.Log
import android.view.*
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.json.JSONArray
import org.json.JSONObject

data class Product(
    val product_id: Int,
    val product_name: String,
    val product_description: String?,
    val product_cost: Int,
    val device_photo: String?
)

class ProductAdapter(private var productList: MutableList<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(), Filterable {

    private var fullList: MutableList<Product> = productList.toMutableList()

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.product_name)
        val txtDesc: TextView = itemView.findViewById(R.id.product_description)
        val txtPrice: TextView = itemView.findViewById(R.id.product_cost)
        val imgProduct: ImageView = itemView.findViewById(R.id.product_photo)
        val btnPurchase: TextView = itemView.findViewById(R.id.purchase)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.txtName.text = product.product_name
        holder.txtDesc.text = product.product_description ?: "No description available"
        holder.txtPrice.text = "KES ${product.product_cost}"

        // Dynamic Image Retrieval from your AlwaysData server
        val imageUrl = if (!product.device_photo.isNullOrEmpty()) {
            val photo = product.device_photo
            when {
                photo.startsWith("http") -> photo
                photo.startsWith("static/") -> "https://kennyfungo.alwaysdata.net/$photo"
                else -> "https://kennyfungo.alwaysdata.net/static/images/$photo"
            }
        } else {
            null
        }

        Log.d("ProductAdapter", "Loading image: $imageUrl")

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .into(holder.imgProduct)

        holder.btnPurchase.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, PaymentActivity::class.java).apply {
                putExtra("product_id", product.product_id)
                putExtra("product_name", product.product_name)
                putExtra("product_description", product.product_description)
                putExtra("product_cost", product.product_cost)
                // Use "device_photo" as key to be consistent
                putExtra("device_photo", product.device_photo)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = productList.size

    // Method to update data safely and refresh fullList for filtering
    fun updateData(newList: List<Product>) {
        fullList.clear()
        fullList.addAll(newList)
        productList.clear()
        productList.addAll(newList)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = mutableListOf<Product>()
                if (constraint.isNullOrEmpty()) {
                    results.addAll(fullList)
                } else {
                    val query = constraint.toString().lowercase().trim()
                    for (item in fullList) {
                        if (item.product_name.lowercase().contains(query) ||
                            item.product_description?.lowercase()?.contains(query) == true) {
                            results.add(item)
                        }
                    }
                }
                return FilterResults().apply { values = results }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                productList.clear()
                val filtered = results?.values as? List<Product> ?: emptyList()
                productList.addAll(filtered)
                notifyDataSetChanged()
            }
        }
    }

    companion object {
        fun fromJsonArray(jsonArray: JSONArray): MutableList<Product> {
            val list = mutableListOf<Product>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                
                // Flexible parsing to handle multiple possible key names
                val id = getInt(obj, "device_id", "product_id", "id")
                val name = getString(obj, "device_name", "product_name", "name")
                val desc = getString(obj, "device_description", "product_description", "description")
                val cost = getInt(obj, "device_cost", "product_cost", "cost")
                val photo = getString(obj, "device_photo", "product_photo", "photo")

                list.add(Product(id, name, desc, cost, photo))
            }
            return list
        }

        private fun getString(obj: JSONObject, vararg keys: String): String {
            for (key in keys) {
                if (obj.has(key) && !obj.isNull(key)) return obj.optString(key)
            }
            return ""
        }

        private fun getInt(obj: JSONObject, vararg keys: String): Int {
            for (key in keys) {
                if (obj.has(key) && !obj.isNull(key)) return obj.optInt(key)
            }
            return 0
        }
    }
}
