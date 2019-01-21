package com.example.macarus0.walkiewalkie.view

import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.data.Dog
import com.squareup.picasso.Picasso

class CheckedPhotoListAdapter : RecyclerView.Adapter<CheckedPhotoListAdapter.ViewHolder>() {
    private var mDogs: List<Dog>? = null
    internal var supportChecks = true
    lateinit var dogCheckHandler: (Dog, Boolean) -> Unit


    fun setSupportChecks(supportChecks: Boolean) {
        this.supportChecks = supportChecks
    }

    fun setDogs(dogs: List<Dog>) {
        mDogs = dogs
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckedPhotoListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.contact_card, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: CheckedPhotoListAdapter.ViewHolder, position: Int) {
        val dog = this.mDogs!![position]
        holder.dogName!!.text = dog.name
        Picasso.get().load(dog.photoUri).placeholder(R.drawable.ic_default_dog_24dp).into(holder.dogImage)
        holder.dog = dog
    }

    override fun getItemCount(): Int {
        if (mDogs == null) {
            Log.i(TAG, "getItemCount: No Dogs Yet")
            return 0
        }
        Log.i(TAG, "getItemCount: dogCount " + mDogs!!.size)

        return mDogs!!.size
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        @BindView(R.id.contact_image)
        @JvmField var dogImage: ImageView? = null
        @BindView(R.id.contact_name)
        @JvmField var dogName: TextView? = null
        @BindView(R.id.contact_select)
        @JvmField var dogCheckBox: AppCompatCheckBox? = null
        internal var dog: Dog? = null

        init {
            ButterKnife.bind(this, itemView)
            if (supportChecks) {
                dogCheckBox!!.visibility = View.VISIBLE
                itemView.setOnClickListener(this)
                dogCheckBox!!.setOnCheckedChangeListener(this)
            }
        }

        override fun onClick(view: View) {
            dogCheckHandler(dog!!, !dogCheckBox!!.isChecked)
            dogCheckBox!!.isChecked = !dogCheckBox!!.isChecked
        }

        override fun onCheckedChanged(compoundButton: CompoundButton, isChecked: Boolean) {
            dogCheckHandler(dog!!, isChecked)
        }
    }

    companion object {

        private const val TAG = "CheckedPhotoListAdapter"
    }

}
