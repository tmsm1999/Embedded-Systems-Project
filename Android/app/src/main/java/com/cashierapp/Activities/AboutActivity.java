package com.cashierapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.cashierapp.Activities.History.HistoryActivity;
import com.cashierapp.R;
import com.google.android.material.navigation.NavigationView;

public class AboutActivity extends AppCompatActivity {
    private NavigationView mNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        InitVars();
        InitToolbar("About");
    }

    private void InitVars() {
        mNavView             = findViewById(R.id.navView);
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
        ((DrawerLayout)findViewById(R.id.activity_aboutDL)).openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    private void InitNavView() {
        mNavView.setNavigationItemSelectedListener(item -> {
            Log.d("menu", item.toString());
            switch (item.getItemId()){
                case R.id.menu_cashiers:
                    onBackPressed();
                    return true;
                case R.id.menu_history:
                    onBackPressed();
                    Intent intent2 = new Intent(this, HistoryActivity.class);
                    startActivity(intent2);
                    return true;
                case R.id.menu_about:
                    return true;
            }
            return false;
        });
    }
}
