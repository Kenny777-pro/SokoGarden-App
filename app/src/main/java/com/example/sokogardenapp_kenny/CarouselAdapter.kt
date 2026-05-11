package com.example.sokogardenapp_kenny

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton

data class Promo(
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val buttonText: String = "Shop Now"
)

class CarouselAdapter(private val promoList: List<Promo>, private val onPromoClick: (Promo) -> Unit) :
    RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    class CarouselViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.carouselImage)
        val title: TextView = view.findViewById(R.id.carouselTitle)
        val subtitle: TextView = view.findViewById(R.id.carouselSubtitle)
        val button: MaterialButton = view.findViewById(R.id.carouselButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val promo = promoList[position]
        holder.title.text = promo.title
        holder.subtitle.text = promo.subtitle
        holder.button.text = promo.buttonText

        Glide.with(holder.itemView.context)
            .load(promo.imageUrl)
            .placeholder(R.drawable.img9)
            .error(R.drawable.img10)
            .into(holder.image)

        holder.button.setOnClickListener { onPromoClick(promo) }
        holder.itemView.setOnClickListener { onPromoClick(promo) }
    }

    override fun getItemCount(): Int = promoList.size
}
