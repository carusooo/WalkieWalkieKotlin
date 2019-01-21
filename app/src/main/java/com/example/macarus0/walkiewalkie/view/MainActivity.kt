package com.example.macarus0.walkiewalkie.view

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.data.Dog
import com.example.macarus0.walkiewalkie.data.Owner
import com.example.macarus0.walkiewalkie.data.Walk
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    internal var TAG = "MainActivity"
    private lateinit var mViewModel: WalkieViewModel
    private val mOnNavigationItemSelectedListener = { item: MenuItem -> selectBottomItem(item.itemId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        mViewModel = ViewModelProviders.of(this).get(WalkieViewModel::class.java)

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        selectBottomItem(R.id.navigation_dogs)

        startWalkFab.setOnClickListener { _ -> startWalk() }

        val linearLayoutManager = LinearLayoutManager(this)
        itemsListRecyclerView.layoutManager = linearLayoutManager
    }

    private fun showDogsList() {
        addItemButton.text = getText(R.string.add_dog)
        addItemButton.visibility = View.VISIBLE
        val dogListAdapter = DogListAdapter()
        dogListAdapter.dogClickHandler = { id -> showDog(id) }
        itemsListRecyclerView.adapter = dogListAdapter
        mViewModel.getAllDogs().observe(this, Observer<List<Dog>> { dogListAdapter.setDogs(requireNotNull(it)) })
        addItemButton.setOnClickListener { v -> showDog(DogContactActivity.ADD_DOG.toLong()) }
    }

    private fun showOwnersList() {
        addItemButton.text = getText(R.string.add_owner)
        addItemButton.visibility = View.VISIBLE
        val ownerListAdapter = OwnerListAdapter()
        ownerListAdapter.ownerClickHandler = { id -> showOwner(id) }
        itemsListRecyclerView.adapter = ownerListAdapter
        mViewModel.allOwners.observe(this, Observer<List<Owner>> { ownerListAdapter.setOwners(requireNotNull(it)) })
        addItemButton.setOnClickListener { _ -> showOwner(OwnerContactActivity.ADD_OWNER.toLong()) }
    }

    private fun showWalksList() {
        addItemButton.visibility = View.GONE
        val walkListAdapter = WalkListAdapter()
        walkListAdapter.walkClickHandler = { id -> showWalk(id) }
        itemsListRecyclerView!!.adapter = walkListAdapter
        mViewModel.allWalks.observe(this, Observer<List<Walk>> { walkListAdapter.setWalks(requireNotNull(it)) })
    }

    fun showDog(id: Long) {
        val intent = Intent(this, DogContactActivity::class.java)
        intent.putExtra(DogContactActivity.DOG_ID, id)
        startActivity(intent)
    }

    fun showOwner(id: Long) {
        val intent = Intent(this, OwnerContactActivity::class.java)
        intent.putExtra(OwnerContactActivity.OWNER_ID, id)
        startActivity(intent)
    }

    fun showWalk(id: Long) {
        val intent = Intent(this, WalkSummaryActivity::class.java)
        intent.putExtra(WalkSummaryActivity.WALK_ID, id)
        startActivity(intent)
    }

    private fun selectBottomItem(itemId: Int): Boolean {
        when (itemId) {
            R.id.navigation_dogs -> {
                showDogsList()
                return true
            }
            R.id.navigation_owners -> {
                showOwnersList()
                return true
            }
            R.id.navigation_walks -> {
                showWalksList()
                return true
            }
        }
        return false
    }

    private fun startWalk() {
        val intent = Intent(this, StartWalkActivity::class.java)
        startActivity(intent)
    }
}
