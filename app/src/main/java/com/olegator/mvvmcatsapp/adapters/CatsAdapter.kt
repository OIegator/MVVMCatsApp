package com.olegator.mvvmcatsapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.olegator.mvvmcatsapp.R
import com.olegator.mvvmcatsapp.api.models.images.CatImage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.cat_item.view.*

class CatsAdapter : RecyclerView.Adapter<CatsAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    private val differCallback = object : DiffUtil.ItemCallback<CatImage>() {
        override fun areItemsTheSame(
            oldItem: CatImage,
            newItem: CatImage
        ): Boolean {
            return oldItem.id == newItem.id && oldItem.url == newItem.url
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: CatImage,
            newItem: CatImage
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.cat_item,
                parent,
                false
            )
        )
    }

    private var onItemClickListener: ((CatImage) -> Unit)? = null

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val cat = differ.currentList[position]
        holder.itemView.apply {
            Picasso.get().load(cat.url).into(iv_image)
            setOnClickListener {
                onItemClickListener?.let { it(cat) }
            }
        }
    }

    fun setOnItemClickListener(listener: (CatImage) -> Unit) {
        onItemClickListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}