package com.example.macarus0.walkiewalkie.view

import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.data.Walk

import butterknife.BindView
import butterknife.ButterKnife

import com.example.macarus0.walkiewalkie.util.TimeStampUtil.getStringDate

class WalkListAdapter : RecyclerView.Adapter<WalkListAdapter.ViewHolder>() {

    private var mWalks: List<Walk>? = null

    lateinit var walkClickHandler: (Long) -> Unit

    fun setWalks(walks: List<Walk>) {
        this.mWalks = walks
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalkListAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.walk_summary_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: WalkListAdapter.ViewHolder, position: Int) {
        val walk = this.mWalks!![position]
        holder.walkDate!!.text = getStringDate(walk.walkDate)
        holder.walkDuration!!.text = walk.walkDuration
        holder.walkId = walk.walkId
    }

    override fun getItemCount(): Int {
        if (mWalks == null) {
            Log.i(TAG, "getItemCount: No Walks Yet")
            return 0
        }
        return mWalks!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        internal var walkId: Long = 0
        @BindView(R.id.walk_date)
        @JvmField  var walkDate: TextView? = null
        @BindView(R.id.walk_duration)
        @JvmField  var walkDuration: TextView? = null

        init {
            ButterKnife.bind(this, itemView)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            walkClickHandler(this.walkId)
        }
    }


    companion object {

        private const val TAG = "WalkListAdapter"
    }

}
