package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.pets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.coherentsolutions.by.max.sir.androidtrainingtasks.R
import kotlinx.android.synthetic.main.pet_image_recycler_item.view.*
import timber.log.Timber

class PhotoGridAdapter : ListAdapter<String?, PhotoGridAdapter.PhotoViewHolder>(DiffGridCallback) {
    inner class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)


    companion object {
        const val PHOTO_GRID_ADAPTER_TAG = "photo-grid-adapter"

        object DiffGridCallback : DiffUtil.ItemCallback<String?>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pet_image_recycler_item, parent, false)
        return PhotoViewHolder(view)
    }


    /**
     * TODO
     *  fix bug with scrooll the same viewHolders
     */
    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.itemView.apply {
            if (getItem(position) != null) {
                val photoUrl = getItem(position) as String
                Timber.tag(PHOTO_GRID_ADAPTER_TAG)
                    .i("Parsing Url: $photoUrl with https protocol")
                val imgUri =
                    photoUrl.toUri().buildUpon().scheme("https").build()
                Glide.with(pet_item_image.context)
                    .load(imgUri)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.loading_animation)
                            .error(R.drawable.rastitelnojdni_brontosavr)
                    )
                    .into(pet_item_image)
                pet_item_image.contentDescription = photoUrl
            } else {

                Timber.tag(PHOTO_GRID_ADAPTER_TAG).i("url was null")
                /**
                 * TODO in real API
                 *                 pet_item_image.visibility=View.GONE
                 */
            }
        }
    }
}