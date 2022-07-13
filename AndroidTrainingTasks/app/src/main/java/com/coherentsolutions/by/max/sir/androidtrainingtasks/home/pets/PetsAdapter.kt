package com.coherentsolutions.by.max.sir.androidtrainingtasks.home.pets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coherentsolutions.by.max.sir.androidtrainingtasks.R
import com.coherentsolutions.by.max.sir.androidtrainingtasks.home.entities.PetResponse
import kotlinx.android.synthetic.main.recyclerview_item.view.*
import timber.log.Timber

class PetsAdapter(val onClickListener: OnClickListener) :
    ListAdapter<PetResponse, PetsAdapter.PetViewHolder>(DiffCallback) {

    companion object {
        const val PETS_ADAPTER_TAG = "pets-adapter"

        object DiffCallback : DiffUtil.ItemCallback<PetResponse>() {
            override fun areItemsTheSame(oldItem: PetResponse, newItem: PetResponse): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: PetResponse, newItem: PetResponse): Boolean {
                return oldItem.photoUrls == newItem.photoUrls
            }
        }
    }

    private lateinit var adapter: PhotoGridAdapter

    inner class PetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        adapter = PhotoGridAdapter()
        view.pet_item_images_recyclerview.adapter = adapter
        return PetViewHolder(view)
    }

    override fun onBindViewHolder(holder: PetViewHolder, position: Int) {
        holder.itemView.apply {
            setOnClickListener {
                onClickListener.onClick(getItem(position))
            }

            pet_item_name.text = getItem(position).name
            getItem(position).category.let {
                if (it != null) {
                    pet_category_item_text.text = it.name ?: ""
                }
            }

            val photoUrls = getItem(position).photoUrls
            if (photoUrls != null) {
                if (photoUrls.isNotEmpty())
                    adapter.submitList(getItem(position).photoUrls)
            } else {
                Timber.tag(PETS_ADAPTER_TAG)
                    .i("PetResponse with id: ${getItem(position).id} has no valid urls")
                /**
                 * TODO("decide") about clear images that has no urls
                 *  |
                 *  ->  pet_item_image.visibility = View.GONE
                 */
            }


        }
    }

    class OnClickListener(val onClickListener: (PetResponse) -> Unit) {
        fun onClick(petResponse: PetResponse) = onClickListener(petResponse)
    }
}