package com.example.cuisine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Locale;

public class RecordActivity extends AppCompatActivity {
    BottomNavigationView btnNav;
    RecyclerView recyclerView;
    RecordAdapter recordAdapter;
    ArrayList<RecordData> recordData;//用於存取 recyclerView 的資料
    String language;
    TextView title;
    ImageView back,appName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

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
        title.setText(R.string.recordTitle);
        appName = (ImageView) findViewById(R.id.appName);
        appName.setVisibility(View.GONE);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadLanguage();
        findRecordData();
        showRecordInfo();

        recordAdapter.setOnItemClickListener(new RecordAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(RecordActivity.this, DetailActivity.class);
                RecordData fData = recordData.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("FOOD_ID", fData.getFoodId());

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    //從SQLite撈出辨識紀錄
    public void findRecordData(){
        Cursor cursor = InitialActivity.databaseHelper.getData();
        recordData = new ArrayList<RecordData>();
        while (cursor.moveToNext()){
            String foodId = cursor.getString(1);
            //利用SQLite中的foodId去資料庫中撈出食物名稱
            String result = DBConnector.executeQuery("findFoodById", language, foodId);
            Gson gson = new Gson();
            ArrayList<FoodData> foodData = gson.fromJson(result, new TypeToken<ArrayList<FoodData>>(){}.getType());
            String name = foodData.get(0).getFoodName();

            String date = cursor.getString(2);
            byte[] image = cursor.getBlob(3);

            recordData.add(new RecordData(foodId,name,date,image));
        }
        cursor.close();
        InitialActivity.databaseHelper.close();
    }

    //將辨識紀錄顯示於畫面 recyclerView
    public void showRecordInfo(){
        if(recordData == null){
            recordData = new ArrayList<RecordData>();
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recordAdapter = new RecordAdapter(this,recordData);
        recyclerView.setAdapter(recordAdapter);
    }

    //取得語系
    public void loadLanguage(){
        SharedPreferences sharedPref = this.getSharedPreferences("setting", Context.MODE_PRIVATE);
        language = sharedPref.getString("language",null);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    Intent Home = new Intent(RecordActivity.this, HomeActivity.class);
                    startActivity(Home);
                    break;
                case R.id.nav_search:
                    Intent Search = new Intent(RecordActivity.this, SearchActivity.class);
                    startActivity(Search);
                    break;
                case R.id.nav_person:
                    Intent Person = new Intent(RecordActivity.this, PersonalActivity.class);
                    startActivity(Person);
                    break;
                case  R.id.nav_camera:
                    Intent camera = new Intent(RecordActivity.this, RecognitionActivity.class);
                    startActivity(camera);
                    break;
                case R.id.nav_setting:
                    Intent Setting = new Intent(RecordActivity.this, SettingActivity.class);
                    startActivity(Setting);
                    break;
            }
            return false;
        }
    };
}
