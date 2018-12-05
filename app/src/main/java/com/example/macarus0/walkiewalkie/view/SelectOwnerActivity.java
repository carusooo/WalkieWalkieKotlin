package com.example.macarus0.walkiewalkie.view;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.macarus0.walkiewalkie.R;
import com.example.macarus0.walkiewalkie.viewmodel.WalkieViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectOwnerActivity extends AppCompatActivity implements OwnerListAdapter.OwnerClickHandler{

    public static final String OWNER_ID = "owner_id";

    WalkieViewModel mViewModel;
    @BindView(R.id.select_contact_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.select_items_list)
    RecyclerView mItemsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contact);
        ButterKnife.bind(this);
        mViewModel = ViewModelProviders.of(this).get(WalkieViewModel.class);
        setSupportActionBar(mToolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.select_owner);
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mItemsRecyclerView.setLayoutManager(linearLayoutManager);

        OwnerListAdapter ownerListAdapter = new OwnerListAdapter();
        ownerListAdapter.setOwnerClickHandler(this::ownerClick);
        ownerListAdapter.setShowDogs(false);
        mItemsRecyclerView.setAdapter(ownerListAdapter);
        mViewModel.getAllAvailableOwners().observe(this, ownerListAdapter::setOwners);
    }

    @Override
    public void ownerClick(long id) {
        Intent intent = new Intent();
        intent.putExtra(OWNER_ID, id);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


}
