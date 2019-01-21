package com.example.macarus0.walkiewalkie.view

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem

import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel

import butterknife.BindView
import butterknife.ButterKnife
import com.example.macarus0.walkiewalkie.data.Owner

class SelectOwnerActivity : AppCompatActivity() {

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
            supportActionBar!!.setTitle(R.string.select_owner)
        }

        val intent = intent
        val dogId = intent.getLongExtra(DOG_ID, -1)

        val linearLayoutManager = LinearLayoutManager(this)
        mItemsRecyclerView!!.layoutManager = linearLayoutManager

        val ownerListAdapter = OwnerListAdapter()
        ownerListAdapter.ownerClickHandler = {it ->  this.ownerClick(it) }
        ownerListAdapter.setShowDogs(false)
        mItemsRecyclerView!!.adapter = ownerListAdapter
        mViewModel.getAllAvailableOwners(dogId).observe(this, Observer<List<Owner>> { ownerListAdapter.setOwners(requireNotNull(it)) })
    }

    fun ownerClick(id: Long) {
        val intent = Intent()
        intent.putExtra(OWNER_ID, id)
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

        const val OWNER_ID = "owner_id"
        const val DOG_ID = "dog_id"
    }


}
