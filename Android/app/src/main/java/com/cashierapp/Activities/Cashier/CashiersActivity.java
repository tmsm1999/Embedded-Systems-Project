package com.cashierapp.Activities.Cashier;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.cashierapp.Activities.AboutActivity;
import com.cashierapp.Activities.History.HistoryActivity;
import com.cashierapp.Adapters.CashiersAdapter;
import com.cashierapp.Classes.Cashiers;
import com.cashierapp.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;

public class CashiersActivity extends AppCompatActivity {

    public static final String MESSAGE = "com.cashierapp.CashiersActivity.MESSAGE";

    private NavigationView      mNavView;
    private RecyclerView        mCashiersRecycleView;
    private CashiersAdapter     mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashiers);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        InitToolbar("Cashiers");
        InitVars();
    }

    private void InitVars() {
        mCashiersRecycleView = findViewById(R.id.cashiers_recycleview);
        mNavView             = findViewById(R.id.navView);

        InitRecycleView();
        InitNavView();
    }

    private void InitToolbar(String tittle) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setTitle(tittle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ((DrawerLayout)findViewById(R.id.activity_cashierDL)).openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    private void InitNavView() {
        mNavView.setNavigationItemSelectedListener(item -> {
            Log.d("menu", item.toString());
            switch (item.getItemId()){
                case R.id.menu_cashiers:
                    break;
                case R.id.menu_history:
                    Intent intent2 = new Intent(this, HistoryActivity.class);
                    startActivity(intent2);
                    return true;
                case R.id.menu_about:
                    Intent intent = new Intent(this, AboutActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        });
    }

    private void InitRecycleView(){
        mAdapter = new CashiersAdapter(this, mCashiersRecycleView, CashiersAdapter.AdapterType.Cashier, "");

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mCashiersRecycleView.setLayoutManager(llm);
        mCashiersRecycleView.setAdapter(mAdapter);

        Query query = FirebaseDatabase.getInstance().getReference().child("Cashiers");
        query.addChildEventListener(childEventListener);

        mCashiersRecycleView.addItemDecoration(new DividerItemDecoration(mCashiersRecycleView.getContext(), DividerItemDecoration.VERTICAL));
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d("onChildAdded", "previous child " + previousChildName);
            Cashiers c = new Cashiers(dataSnapshot.getKey());
            mAdapter.addData(c);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d("onChildChanged", "previous child " + previousChildName);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d("onChildRemoved", "child removed");
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d("onChildMoved", "previousChildName");
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.d("onCancelled", "Error: " + databaseError.getMessage());
        }
    };
}