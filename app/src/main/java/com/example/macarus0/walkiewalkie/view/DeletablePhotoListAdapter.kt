package com.example.macarus0.walkiewalkie.view

import android.graphics.drawable.Drawable
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.data.PhotoItem
import com.squareup.picasso.Picasso

import butterknife.BindView
import butterknife.ButterKnife

class DeletablePhotoListAdapter : RecyclerView.Adapter<DeletablePhotoListAdapter.ViewHolder>() {
    internal var photos: List<PhotoItem>? = null
        set(value) {
            field = value
            Log.i(TAG, "setPhotos: Loaded photos " + photos?.size)
            notifyDataSetChanged()
        }

    lateinit var deleteHandler: (Long) -> Unit
    var showPhotoLabels = false
    lateinit var placeholderImage: Drawable

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeletablePhotoListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.contact_card, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: DeletablePhotoListAdapter.ViewHolder, position: Int) {
        val photoItem = this.photos!![position]
        Log.i(TAG, "onBindViewHolder: Loading image " + photoItem.photoUri)
        Picasso.get().load(photoItem.photoUri).placeholder(this.placeholderImage).into(holder.walkImage)
        if (showPhotoLabels) {
            holder.tv!!.text = photoItem.name
        }
        holder.removeButton!!.setOnClickListener { _: View -> deleteHandler(photoItem.id) }

    }

    override fun getItemCount(): Int {
        if (photos == null) {
            Log.i(TAG, "getItemCount: No Photos Yet")
            return 0
        }
        Log.i(TAG, "getItemCount: photoCount " + photos!!.size)
        return photos!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        @BindView(R.id.contact_name)
        @JvmField  var tv: TextView? = null
        @BindView(R.id.item_remove)
        @JvmField  var removeButton: AppCompatImageButton? = null

        @BindView(R.id.contact_image)
        @JvmField  var walkImage: ImageView? = null

        init {
            ButterKnife.bind(this, itemView)
            removeButton!!.visibility = View.VISIBLE

            if (!showPhotoLabels) {
                tv!!.visibility = View.INVISIBLE
            }
        }

    }

    companion object {

        private const val TAG = "DeletablePhotoListAdapter"
    }

}
