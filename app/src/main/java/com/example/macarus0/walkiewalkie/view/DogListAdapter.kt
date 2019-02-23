package com.example.macarus0.walkiewalkie.view

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.data.Dog
import com.squareup.picasso.Picasso

import butterknife.BindView
import butterknife.ButterKnife

class DogListAdapter : RecyclerView.Adapter<DogListAdapter.ViewHolder>() {

    private var mDogs: List<Dog>? = null

    lateinit var dogClickHandler: (Long) -> Unit

    private var mShowOwners: Boolean = false


    fun setShowOwners(mShowOwners: Boolean) {
        this.mShowOwners = mShowOwners
    }

    fun setDogs(dogs: List<Dog>) {
        mDogs = dogs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.dog_contact_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: DogListAdapter.ViewHolder, position: Int) {
        val dog = this.mDogs!![position]
        holder.dogName.text = dog.name
        Picasso.get().load(dog.photoUri).placeholder(R.drawable.ic_default_dog_24dp).into(holder.dogImage)
        holder.dogId = dog.id
    }

    override fun getItemCount(): Int {
        if (mDogs == null) {
            Log.i(TAG, "getItemCount: No Dogs Yet")
            return 0
        }
        Log.i(TAG, "getItemCount: " + mDogs!!.size)
        return mDogs!!.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        internal var dogId: Long = 0
        var dogImage: ImageView
        var dogName: TextView

        init {
            dogName = itemView.findViewById(R.id.dogName)
            dogImage = itemView.findViewById(R.id.dog_image)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            Log.i(TAG, "Adapter Clicked")
            dogClickHandler(dogId)
        }
    }

    companion object {

        private const val TAG = "DogListAdapter"
    }

}
