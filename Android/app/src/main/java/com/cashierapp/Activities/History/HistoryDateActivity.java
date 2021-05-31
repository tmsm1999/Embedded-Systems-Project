package com.cashierapp.Activities.History;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cashierapp.Activities.AboutActivity;
import com.cashierapp.Activities.Cashier.CashiersActivity;
import com.cashierapp.Adapters.CashiersAdapter;
import com.cashierapp.Classes.Cashiers;
import com.cashierapp.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class HistoryDateActivity extends AppCompatActivity {

    public static final String MESSAGE_DATE = "com.cashierapp.HistoryDateActivity.MESSAGE_DATE";
    public static final String MESSAGE_CASHIER_ID = "com.cashierapp.HistoryDateActivity.MESSAGE_CASHIER_ID";


    private NavigationView mNavView;
    private RecyclerView mCashiersRecycleView;
    private CashiersAdapter mAdapter;
    private String         mCashierID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashiers);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mCashierID = intent.getStringExtra(HistoryActivity.MESSAGE);

        InitToolbar("Historic");
        InitVars();
    }

    private void InitVars() {
        mCashiersRecycleView = findViewById(R.id.cashiers_recycleview);
        mNavView             = findViewById(R.id.navView);

        TextView cashierIDTV = findViewById(R.id.textview_historycashieridd);

        cashierIDTV.setText("Cashier: " + mCashierID);
        cashierIDTV.setVisibility(View.VISIBLE);

        InitRecycleView();
        //InitNavView();
    }

    private void InitToolbar(String tittle) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setTitle(tittle);
            //actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ((DrawerLayout)findViewById(R.id.activity_cashierDL)).openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    /*private void InitNavView() {
        mNavView.setNavigationItemSelectedListener(item -> {
            Log.d("menu", item.toString());
            switch (item.getItemId()){
                case R.id.menu_cashiers:
                    onBackPressed();
                    onBackPressed();
                    break;
                case R.id.menu_history:
                    onBackPressed();
                    return true;
                case R.id.menu_about:
                    onBackPressed();
                    onBackPressed();
                    Intent intent = new Intent(this, AboutActivity.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        });
    }*/

    private void InitRecycleView(){
        mAdapter = new CashiersAdapter(this, mCashiersRecycleView, CashiersAdapter.AdapterType.HistoryDate, mCashierID);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mCashiersRecycleView.setLayoutManager(llm);
        mCashiersRecycleView.setAdapter(mAdapter);

        Query query = FirebaseDatabase.getInstance().getReference().child("History").child(mCashierID);
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
