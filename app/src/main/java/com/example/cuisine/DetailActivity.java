package com.example.cuisine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class DetailActivity extends AppCompatActivity{
    BottomNavigationView btnNav;
    TextView title,foodId,foodName,foodDesc,material,nutrition,calorie,speakCN,speakTW;
    ImageView foodImg,back,appName;
    Button startCookBtn;
    String strFoodId,language;
    private SoundPool soundPool;
    private int voiceCH,voiceTW;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        btnNav = findViewById(R.id.bottom_navigation);
        btnNav.setOnNavigationItemSelectedListener(navListener);
        Menu menu = btnNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(R.string.detailTitle);
        appName = (ImageView) findViewById(R.id.appName);
        appName.setVisibility(View.GONE);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        foodId = (TextView) findViewById(R.id.foodId);
        foodName = (TextView) findViewById(R.id.foodName);
        foodImg = (ImageView) findViewById(R.id.foodImg);
        foodDesc = (TextView) findViewById(R.id.foodDesc);
        material = (TextView) findViewById(R.id.material);
        nutrition = (TextView) findViewById(R.id.nutrition);
        calorie = (TextView) findViewById(R.id.calorie);
        speakCN = (TextView) findViewById(R.id.speakCH);
        speakTW = (TextView) findViewById(R.id.speakTW);

        startCookBtn = (Button) findViewById(R.id.startCookBtn);
        startCookBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(DetailActivity.this, RecipeActivity.class);

                Bundle bundle = new Bundle();
                bundle.putString("FOOD_ID", foodId.getText().toString());

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        loadLanguage();

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            strFoodId = bundle.getString("FOOD_ID", "null");
            try {
                String result = DBConnector.executeQuery("findFoodById", language, strFoodId);
                Gson gson = new Gson();
                ArrayList<FoodData> foodData = gson.fromJson(result, new TypeToken<ArrayList<FoodData>>(){}.getType());

                foodImg.setImageBitmap(imageNicer(foodData.get(0).getFoodImg()));
                foodId.setText(foodData.get(0).getFoodId());
                foodName.setText(foodData.get(0).getFoodName());
                foodDesc.setText(foodData.get(0).getFoodDesc());
                material.setText(foodData.get(0).getMaterial());
                nutrition.setText(foodData.get(0).getNutrition());
                calorie.setText(foodData.get(0).getCalorie());

            }catch (Exception e){
                Toast.makeText(this, e.toString(), LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this,R.string.error, LENGTH_SHORT).show();
        }

        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        loadFoodSound();

        speakCN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(voiceCH,1,1,0,0, 1);
            }
        });

        speakTW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(voiceTW,1,1,0,0, 1);
            }
        });
    }

    //取得語音檔
    public void loadFoodSound(){
        switch (strFoodId){
            case "1":
                voiceCH = soundPool.load(this,R.raw.sweet_sour,1);
                break;
            case "2":
                voiceCH = soundPool.load(this,R.raw.ma_po,1);
                break;
            case "3":
                voiceCH = soundPool.load(this,R.raw.ant,1);
                break;
            case "4":
                voiceCH = soundPool.load(this,R.raw.kung_pao,1);
                break;
            case "5":
                voiceCH = soundPool.load(this,R.raw.bitter_melon,1);
                break;
            case "6":
                voiceCH = soundPool.load(this,R.raw.tomato_egg,1);
                break;
            case "7":
                voiceCH = soundPool.load(this,R.raw.stinky,1);
                break;
            case "8":
                voiceCH = soundPool.load(this,R.raw.oyster,1);
                break;
            case "9":
                voiceCH = soundPool.load(this,R.raw.pig_blood,1);
                break;
            case "10":
                voiceCH = soundPool.load(this,R.raw.big_little,1);
                break;
            case "11":
                voiceCH = soundPool.load(this,R.raw.potato_ball,1);
                break;
            case "12":
                voiceCH = soundPool.load(this,R.raw.green_onion,1);
                break;
            case "13":
                voiceCH = soundPool.load(this,R.raw.intestine,1);
                break;
            case "14":
                voiceCH = soundPool.load(this,R.raw.meatball,1);
                break;
            case "15":
                voiceCH = soundPool.load(this,R.raw.duck_blood,1);
                break;
        }
        switch (strFoodId){
            case "1":
                voiceTW = soundPool.load(this,R.raw.sweet_sour_tw,1);
                break;
            case "2":
                voiceTW = soundPool.load(this,R.raw.ma_po_tw,1);
                break;
            case "3":
                voiceTW = soundPool.load(this,R.raw.ant_tw,1);
                break;
            case "4":
                voiceTW = soundPool.load(this,R.raw.kung_pao_tw,1);
                break;
            case "5":
                voiceTW = soundPool.load(this,R.raw.bitter_melon_tw,1);
                break;
            case "6":
                voiceTW = soundPool.load(this,R.raw.tomato_egg_tw,1);
                break;
            case "7":
                voiceTW = soundPool.load(this,R.raw.stinky_tw,1);
                break;
            case "8":
                voiceTW = soundPool.load(this,R.raw.oyster_tw,1);
                break;
            case "9":
                voiceTW = soundPool.load(this,R.raw.pig_blood_tw,1);
                break;
            case "10":
                voiceTW = soundPool.load(this,R.raw.big_little_tw,1);
                break;
            case "11":
                voiceTW = soundPool.load(this,R.raw.potato_tw,1);
                break;
            case "12":
                voiceTW = soundPool.load(this,R.raw.green_onion_tw,1);
                break;
            case "13":
                voiceTW = soundPool.load(this,R.raw.intestine_tw,1);
                break;
            case "14":
                voiceTW = soundPool.load(this,R.raw.meatball_tw,1);
                break;
            case "15":
                voiceTW = soundPool.load(this,R.raw.duck_blood_tw,1);
                break;
        }
    }

    //取得語系
    public void loadLanguage(){
        SharedPreferences sharedPref = this.getSharedPreferences("setting", Context.MODE_PRIVATE);
        language = sharedPref.getString("language",null);
    }

    //優化圖片(避免outOfMemory)
    public static Bitmap imageNicer(String url){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inSampleSize = 2;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = null;
        try {
            is = (InputStream) new URL(url).getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(is,null,opt);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    Intent Home = new Intent(DetailActivity.this, HomeActivity.class);
                    startActivity(Home);
                    break;
                case R.id.nav_search:
                    Intent Search = new Intent(DetailActivity.this, SearchActivity.class);
                    startActivity(Search);
                    break;
                case R.id.nav_person:
                    Intent Person = new Intent(DetailActivity.this, PersonalActivity.class);
                    startActivity(Person);
                    break;
                case  R.id.nav_camera:
                    Intent camera = new Intent(DetailActivity.this, RecognitionActivity.class);
                    startActivity(camera);
                    break;
                case R.id.nav_setting:
                    Intent Setting = new Intent(DetailActivity.this, SettingActivity.class);
                    startActivity(Setting);
                    break;
            }
            return false;
        }
    };

}
