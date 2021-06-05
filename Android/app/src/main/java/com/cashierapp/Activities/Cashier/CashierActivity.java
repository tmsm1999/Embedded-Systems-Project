package com.cashierapp.Activities.Cashier;

import android.content.Intent;
import android.os.AsyncTask;
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
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class CashierActivity extends AppCompatActivity {

    private RecyclerView mCashierRecycleView;
    private CashierAdapter mAdapter;
    private NavigationView mNavView;
    private String         mCashierID;
    private String         mServer;
    private Button         mButton;
    private final OkHttpClient client = new OkHttpClient();


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
        //mCallApi = new CallAPI();

        // Getting the server address
        FirebaseDatabase.getInstance().getReference().child("Server").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mServer = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        LinearLayout linearLayout = findViewById(R.id.layout_button_restart);
        linearLayout.setVisibility(View.VISIBLE);

        mButton = findViewById(R.id.button_erase);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Request request = new Request.Builder()
                                .url("http://" + mServer + "/movetohistory?cashier=" + mCashierID)
                                .get()
                                .build();

                        try (Response response = client.newCall(request).execute()) {
                            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                            Headers responseHeaders = response.headers();
                            for (int i = 0; i < responseHeaders.size(); i++) {
                                System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
                            }

                            System.out.println(response.body().string());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
        mButton.setClickable(false);

        InitRecycleView();
        //InitNavView();
    }

   /* public class CallAPI extends AsyncTask<String, String, String> {

        public CallAPI(){
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String urlString = params[0]; // URL to call
            String data = params[1]; //data to post
            OutputStream out = null;

            try {
                URL url = new URL(urlString);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                out = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                writer.write(data);
                writer.flush();
                writer.close();
                out.close();

                urlConnection.connect();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }*/

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
            if (dataSnapshot.getValue().toString().equals("null"))
                return;

            mButton.setClickable(true);
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

            if (dataSnapshot.getValue().toString().equals("null"))
                return;

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

            if (dataSnapshot.getValue().toString().equals("null"))
                return;

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
