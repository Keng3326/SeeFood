package com.example.cuisine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

public class PersonalActivity extends AppCompatActivity {
    BottomNavigationView btnNav;
    TextView recordBtn,favoriteBtn,title;
    ImageView back,appName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        btnNav = findViewById(R.id.bottom_navigation);
        btnNav.setOnNavigationItemSelectedListener(navListener);
        Menu menu = btnNav.getMenu();
        MenuItem menuItem = menu.getItem(3);
        menuItem.setChecked(true);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(R.string.personalTitle);
        appName = (ImageView) findViewById(R.id.appName);
        appName.setVisibility(View.GONE);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recordBtn = (TextView) findViewById(R.id.recordBtn);
        favoriteBtn = (TextView) findViewById(R.id.favoriteBtn);

        recordBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(PersonalActivity.this,RecordActivity.class);
                startActivity(intent);
            }
        });

        favoriteBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(PersonalActivity.this,FavoriteActivity.class);
                startActivity(intent);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    Intent Home = new Intent(PersonalActivity.this, HomeActivity.class);
                    startActivity(Home);
                    break;
                case R.id.nav_search:
                    Intent Search = new Intent(PersonalActivity.this, SearchActivity.class);
                    startActivity(Search);
                    break;
                case R.id.nav_person:
                    Intent Person = new Intent(PersonalActivity.this, PersonalActivity.class);
                    startActivity(Person);
                    break;
                case  R.id.nav_camera:
                    Intent camera = new Intent(PersonalActivity.this, RecognitionActivity.class);
                    startActivity(camera);
                    break;
                case R.id.nav_setting:
                    Intent Setting = new Intent(PersonalActivity.this, SettingActivity.class);
                    startActivity(Setting);
                    break;
            }
            return false;
        }
    };
}
