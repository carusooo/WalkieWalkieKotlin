package com.example.macarus0.walkiewalkie.view

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.data.Owner
import com.squareup.picasso.Picasso

import butterknife.BindView
import butterknife.ButterKnife

class OwnerListAdapter : RecyclerView.Adapter<OwnerListAdapter.ViewHolder>() {

    private var mOwners: List<Owner>? = null

    lateinit var ownerClickHandler: (Long) -> Unit

    private var mShowDogs: Boolean = false

    fun setShowDogs(mShowDogs: Boolean) {
        this.mShowDogs = mShowDogs
    }


    fun setOwners(owners: List<Owner>) {
        mOwners = owners
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OwnerListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.owner_contact_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: OwnerListAdapter.ViewHolder, position: Int) {
        val owner = this.mOwners!![position]
        holder.ownerName.text = owner.firstName + " " + owner.lastName
        Picasso.get().load(owner.photoUri).placeholder(R.drawable.ic_default_owner_24dp).into(holder.ownerImage)
        holder.ownerId = owner.id
    }

    override fun getItemCount(): Int {
        if (mOwners == null) {
            Log.i(TAG, "getItemCount: No Owners Yet")
            return 0
        }
        return mOwners!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        internal var ownerId: Long = 0
        var ownerImage: ImageView
        var ownerName: TextView

        init {
            ownerName = itemView.findViewById(R.id.owner_name)
            ownerImage = itemView.findViewById(R.id.owner_image)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Log.i(TAG, "Adapter Clicked")
            ownerClickHandler(ownerId)
        }
    }
    companion object {

        private const val TAG = "OwnerListAdapter"
    }


}
