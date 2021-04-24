package com.example.cuisine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class HomeActivity extends AppCompatActivity {
    BottomNavigationView btnNav;
    ImageView appName,back;
    TextView title,noInfo;
    String foodName,foodVag,foodSpy,foodType,foodMeat,language;
    RecyclerView recyclerView;
    HomeAdapter foodAdapter;
    ArrayList<String> favoriteList;//從 SharedPreferences 撈出的資料
    ArrayList<FoodData> foodData;//用於存取 recyclerView 的資料
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        btnNav = findViewById(R.id.bottom_navigation);
        btnNav.setOnNavigationItemSelectedListener(navListener);
        Menu menu = btnNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.GONE);
        noInfo = (TextView) findViewById(R.id.noInfo);
        appName = (ImageView) findViewById(R.id.appName);
        appName.setVisibility(View.VISIBLE);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadLanguage();
        loadFoodData();
        showFoodInfo();

        if (foodAdapter != null) {
            foodAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
                //點擊前往詳細資訊
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(HomeActivity.this, DetailActivity.class);
                    FoodData fData = foodData.get(position);

                    Bundle bundle = new Bundle();
                    bundle.putString("FOOD_ID", fData.getFoodId());

                    intent.putExtras(bundle);
                    startActivity(intent);
                }

                //點擊加入最愛清單
                @Override
                public void onCollectClick(int position) {
                    loadFavoriteInfo();//取得原先的最愛資訊 favoriteList

                    if (favoriteList == null) {
                        favoriteList = new ArrayList<String>();
                    }

                    //取得所點擊的食物名稱
                    String fName = foodData.get(position).getFoodName();
                    String foodId = foodData.get(position).getFoodId();

                    //呼叫 checkFavoriteRepeat 判斷料理是否已存在最愛清單中
                    if(!checkFavoriteExist(foodId)){
                        //將新的最愛料理加入最愛清單中，並儲存
                        favoriteList.add(foodId);
                        saveFavoriteInfo(favoriteList);
                    }else {
                        //將料理從最愛料理中移除，並儲存
                        for (int i = 0; i < favoriteList.size(); i++) {
                            if (favoriteList.get(i).equals(foodId)) {
                                favoriteList.remove(i);
                                break;
                            }
                        }
                        saveFavoriteInfo(favoriteList);
                    }
                }
            });
        }
    }

    //取得食物資訊
    public void loadFoodData(){
        //判斷查詢條件是否為空值(若為空值撈出所有資料)
        Bundle searchArg = this.getIntent().getExtras();
        if (searchArg == null) {
            try {
                String result = DBConnector.executeQuery("loadFoodData",language);
                Gson gson = new Gson();
                foodData = gson.fromJson(result, new TypeToken<ArrayList<FoodData>>(){}.getType());
            }catch (Exception e){
                Toast.makeText(this, e.toString(), LENGTH_LONG).show();
            }
        }else{
            foodName = searchArg.getString("FOOD_NAME", null);
            foodVag = searchArg.getString("FOOD_VEG", null);
            foodSpy = searchArg.getString("FOOD_SPY",null);
            foodType = searchArg.getString("FOOD_TYPE", null);
            foodMeat = searchArg.getString("FOOD_MEAT", null);
            try {
                String result = DBConnector.executeQuery("searchFood", language, foodName, foodVag, foodSpy, foodType, foodMeat);
                Gson gson = new Gson();
                foodData = gson.fromJson(result, new TypeToken<ArrayList<FoodData>>(){}.getType());
            }catch (Exception e){
                Toast.makeText(this, e.toString(), LENGTH_LONG).show();
            }
        }
    }

    //印出食物資訊
    public void showFoodInfo(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(foodData.size() > 0){
            foodAdapter = new HomeAdapter(this,foodData);
        }else{
            noInfo.setVisibility(View.VISIBLE);
        }
        recyclerView.setAdapter(foodAdapter);
    }

    //判斷該項食物是否已存在最愛清單中
    public boolean checkFavoriteExist(String foodId){
        if(favoriteList == null){
            favoriteList= new ArrayList<String>();
        }
        boolean checkRepeat = false;
        for(int i = 0; i<favoriteList.size();i++){
            if(favoriteList.get(i).equals(foodId)){
                checkRepeat = true;
                break;
            }
        }
        return checkRepeat;
    }

    //儲存最愛資訊
    public void saveFavoriteInfo(ArrayList<String> arrayList){
        SharedPreferences sharedPref = this.getSharedPreferences("favorite", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String favoriteJson = gson.toJson(arrayList);
        editor.putString("favorite",favoriteJson);
        editor.apply();
    }

    //取得最愛清單
    public void loadFavoriteInfo(){
        SharedPreferences sharedPref = this.getSharedPreferences("favorite", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String favoriteJson = sharedPref.getString("favorite",null);
        favoriteList =  gson.fromJson(favoriteJson,new TypeToken<ArrayList<String>>() {}.getType());
    }

    //取得語系
    public void loadLanguage(){
        SharedPreferences sharedPref = this.getSharedPreferences("setting", Context.MODE_PRIVATE);
        language = sharedPref.getString("language",null);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            TextView title = (TextView) findViewById(R.id.title);
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    Intent Home = new Intent(HomeActivity.this, HomeActivity.class);
                    startActivity(Home);
                    break;
                case R.id.nav_search:
                    Intent Search = new Intent(HomeActivity.this, SearchActivity.class);
                    startActivity(Search);
                    break;
                case R.id.nav_person:
                    Intent Person = new Intent(HomeActivity.this, PersonalActivity.class);
                    startActivity(Person);
                    break;
                case  R.id.nav_camera:
                    Intent camera = new Intent(HomeActivity.this, RecognitionActivity.class);
                    startActivity(camera);
                    break;
                case R.id.nav_setting:
                    Intent Setting = new Intent(HomeActivity.this, SettingActivity.class);
                    startActivity(Setting);
                    break;
            }
            return false;
        }
    };
}
