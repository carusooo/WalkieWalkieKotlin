package com.example.macarus0.walkiewalkie.view

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem

import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel

import butterknife.BindView
import butterknife.ButterKnife
import com.example.macarus0.walkiewalkie.data.Dog

class SelectDogActivity : AppCompatActivity() {

    private lateinit var mViewModel: WalkieViewModel
    @BindView(R.id.select_contact_toolbar)
    @JvmField  var mToolbar: Toolbar? = null
    @BindView(R.id.select_items_list)
    @JvmField  var mItemsRecyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_contact)
        ButterKnife.bind(this)
        mViewModel = ViewModelProviders.of(this).get(WalkieViewModel::class.java)
        setSupportActionBar(mToolbar)
        // add back arrow to toolbar
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.setTitle(R.string.select_dog)
        }

        val intent = intent
        val ownerId = intent.getLongExtra(OWNER_ID, -1)

        val linearLayoutManager = LinearLayoutManager(this)
        mItemsRecyclerView!!.layoutManager = linearLayoutManager

        val dogListAdapter = DogListAdapter()
        dogListAdapter.dogClickHandler = {it ->  this.dogClick(it) }
        dogListAdapter.setShowOwners(false)
        mItemsRecyclerView!!.adapter = dogListAdapter
        mViewModel.getAllAvailableDogs(ownerId).observe(this, Observer<List<Dog>> { dogListAdapter.setDogs(requireNotNull(it)) })
    }

    fun dogClick(id: Long) {
        val intent = Intent()
        intent.putExtra(DOG_ID, id)
        setResult(Activity.RESULT_OK, intent)
        finishAfterTransition()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finishAfterTransition()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {

        const val DOG_ID = "dog_id"
        const val OWNER_ID = "owner_id"
    }

}
