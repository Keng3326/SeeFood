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

import static android.widget.Toast.LENGTH_SHORT;

public class FavoriteActivity extends AppCompatActivity {
    TextView title;
    BottomNavigationView btnNav;
    RecyclerView recyclerView;
    FavoriteAdapter favoriteAdapter;
    ArrayList<FoodData> favoriteData;//用於存取 recyclerView 的資料
    ArrayList<String> favoriteList;//從 SharedPreferences 撈出的資料
    String language;
    ImageView back,appName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

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
        title.setText(R.string.favoriteTitle);
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
        loadFavoriteList();
        loadFoodData();
        showFavoriteInfo();

        //監聽recyclerView點擊事件
        favoriteAdapter.setOnItemClickListener(new FavoriteAdapter.OnItemClickListener() {
            //點擊查看詳細資訊
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(FavoriteActivity.this, DetailActivity.class);
                FoodData fData = favoriteData.get(position);

                Bundle bundle = new Bundle();
                bundle.putString("FOOD_ID", fData.getFoodId());

                intent.putExtras(bundle);
                startActivity(intent);
            }

            //刪除最愛資料
            @Override
            public void onDeleteClick(int position) {
                String deleteFoodId = favoriteData.get(position).getFoodId();
                int count = favoriteList.size();
                for (int i = 0; i < favoriteList.size(); i++) {
                    if (favoriteList.get(i).equals(deleteFoodId)) {
                        favoriteList.remove(i);
                        break;
                    }
                }
                if(count == favoriteList.size()){
                    Toast.makeText(FavoriteActivity.this,R.string.error,Toast.LENGTH_LONG).show();
                }else {
                    saveFavoriteInfo(favoriteList);
                    favoriteData.remove(position);
                    favoriteAdapter.notifyItemRemoved(position);
                    Toast.makeText(FavoriteActivity.this,R.string.delete,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //重新儲存最愛清單
    public void saveFavoriteInfo(ArrayList<String> arrayList){
        SharedPreferences sharedPref = this.getSharedPreferences("favorite", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        Gson gson = new Gson();
        String favoriteJson = gson.toJson(arrayList);
        editor.putString("favorite",favoriteJson);
        editor.apply();
    }

    //取得最愛清單
    public void loadFavoriteList(){
        SharedPreferences sharedPref = this.getSharedPreferences("favorite", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String favoriteJson = sharedPref.getString("favorite","");
        favoriteList = gson.fromJson(favoriteJson,new TypeToken<ArrayList<String>>() {}.getType());
    }

    //依據最愛清單找出相對應的料理資訊
    public void loadFoodData(){
        favoriteData = new ArrayList<FoodData>();
        if(favoriteList == null){
            favoriteList = new ArrayList<String>();
            Toast.makeText(this, R.string.noInfo, Toast.LENGTH_LONG).show();
        }

        for(int i = 0; i < favoriteList.size(); i++){
            String result = DBConnector.executeQuery("findFoodById", language, favoriteList.get(i));
            Gson gson = new Gson();
            ArrayList<FoodData> foodData = gson.fromJson(result, new TypeToken<ArrayList<FoodData>>(){}.getType());
            favoriteData.add(foodData.get(0));
        }
    }

    //將料理資訊顯示於畫面 recyclerView
    public void showFavoriteInfo(){
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(favoriteData!=null){
            favoriteAdapter = new FavoriteAdapter(this,favoriteData);
            recyclerView.setAdapter(favoriteAdapter);
        }else{
            Toast.makeText(this,R.string.noInfo,LENGTH_SHORT).show();
        }
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
                    Intent Home = new Intent(FavoriteActivity.this, HomeActivity.class);
                    startActivity(Home);
                    break;
                case R.id.nav_search:
                    Intent Search = new Intent(FavoriteActivity.this, SearchActivity.class);
                    startActivity(Search);
                    break;
                case R.id.nav_person:
                    Intent Person = new Intent(FavoriteActivity.this, PersonalActivity.class);
                    startActivity(Person);
                    break;
                case  R.id.nav_camera:
                    Intent camera = new Intent(FavoriteActivity.this, RecognitionActivity.class);
                    startActivity(camera);
                    break;
                case R.id.nav_setting:
                    Intent Setting = new Intent(FavoriteActivity.this, SettingActivity.class);
                    startActivity(Setting);
                    break;
            }
            return false;
        }
    };
}
