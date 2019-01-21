package com.example.macarus0.walkiewalkie.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.data.Walk
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel

import butterknife.BindView
import butterknife.ButterKnife

/**
 * A simple [Fragment] subclass.
 * Use the [WalkDogsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WalkDogsFragment : Fragment() {

    @BindView(R.id.walk_dogs_list_title)
    @JvmField var mDogsListTitleTextView: TextView? = null
    @BindView(R.id.walk_dogs_rv)
    @JvmField var mDogRecyclerView: RecyclerView? = null
    private var mWalkId: Long = 0
    private var walkieViewModel: WalkieViewModel? = null
    private var dogPhotoListAdapter: CheckedPhotoListAdapter? = null

    fun setWalkId(mWalkId: Long) {
        this.mWalkId = mWalkId
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(WALK_ID, mWalkId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        walkieViewModel = ViewModelProviders.of(activity!!).get(WalkieViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (savedInstanceState != null) {
            mWalkId = savedInstanceState.getLong(WALK_ID)
        }

        val view = inflater.inflate(R.layout.fragment_walk_dogs, container, false)
        ButterKnife.bind(this, view)
        mDogsListTitleTextView!!.text = activity!!.getText(R.string.walk_dogs_title)

        val gridLayoutManager = GridLayoutManager(activity,
                activity!!.resources.getInteger(R.integer.walk_status_dog_grid_columns))
        mDogRecyclerView!!.layoutManager = gridLayoutManager
        dogPhotoListAdapter = CheckedPhotoListAdapter()
        dogPhotoListAdapter!!.setSupportChecks(false)
        mDogRecyclerView!!.adapter = dogPhotoListAdapter
        walkieViewModel!!.getWalkById(mWalkId).observe(this, Observer<Walk> { this.showDogs(requireNotNull(it)) })

        return view
    }

    private fun showDogs(walk: Walk) {
        dogPhotoListAdapter!!.setDogs(walk.dogs)
    }

    companion object {

        private const val WALK_ID = "walk_dogs_fragment_walk_id"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment WalkDogsFragment.
         */
        fun newInstance(): WalkDogsFragment {
            val fragment = WalkDogsFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}// Required empty public constructor
