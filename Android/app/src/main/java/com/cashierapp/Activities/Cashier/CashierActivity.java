package com.cashierapp.Activities.Cashier;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

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
import com.cashierapp.Activities.History.HistoryActivity;
import com.cashierapp.Adapters.CashierAdapter;
import com.cashierapp.Adapters.CashiersAdapter;
import com.cashierapp.Classes.Cashier;
import com.cashierapp.Classes.Cashiers;
import com.cashierapp.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class CashierActivity extends AppCompatActivity {

    private RecyclerView mCashierRecycleView;
    private CashierAdapter mAdapter;
    private NavigationView mNavView;
    private String         mCashierID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashier);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mCashierID = intent.getStringExtra(CashiersActivity.MESSAGE);

        InitToolbar("Cashier " + mCashierID);

        InitVars();
    }

    private void InitVars() {
        mCashierRecycleView = findViewById(R.id.cashier_recycleview);
        mNavView             = findViewById(R.id.navView);
        mAdapter = new CashierAdapter(this);

        LinearLayout linearLayout = findViewById(R.id.layout_button_restart);
        linearLayout.setVisibility(View.VISIBLE);

        Button button = findViewById(R.id.button_erase);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: implement the method to contact the raspberry pi to move the content to historic
            }
        });

        InitRecycleView();
        //InitNavView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ((DrawerLayout)findViewById(R.id.activity_detail_cashierDL)).openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
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

    /*private void InitNavView() {
        mNavView.setNavigationItemSelectedListener(item -> {
            Log.d("menu", item.toString());
            switch (item.getItemId()){
                case R.id.menu_cashiers:
                    onBackPressed();
                    break;
                case R.id.menu_history:
                    onBackPressed();
                    Intent intent2 = new Intent(this, HistoryActivity.class);
                    startActivity(intent2);
                    return true;
                case R.id.menu_about:
                    onBackPressed();
                    Intent intent = new Intent(this, AboutActivity.class );
                    startActivity(intent);
                    return true;
            }
            return false;
        });
    }*/

    private void InitRecycleView(){
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mCashierRecycleView.setLayoutManager(llm);
        mCashierRecycleView.setAdapter(mAdapter);

        Query query = FirebaseDatabase.getInstance().getReference().child("Cashiers").child(mCashierID);
        query.addChildEventListener(childEventListener);

        mCashierRecycleView.addItemDecoration(new DividerItemDecoration(mCashierRecycleView.getContext(), DividerItemDecoration.VERTICAL));
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
            Log.d("onChildAdded", "previous child " + previousChildName);

            Cashier c = new Cashier(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
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
            Cashier c = new Cashier(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
            mAdapter.changeData(c);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d("onChildRemoved", "child removed");

            Cashier c = new Cashier(dataSnapshot.getKey(), dataSnapshot.getValue().toString());
            mAdapter.removeData(c);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
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
