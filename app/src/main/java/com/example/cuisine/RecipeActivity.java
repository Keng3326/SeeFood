package com.example.cuisine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.CountDownTimer;
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

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

public class RecipeActivity extends AppCompatActivity {
    BottomNavigationView btnNav;
    TextView recipeDesc,stepTag,recipeTitle,timeText;
    Button nextBtn,previousBtn,finishBtn;
    ImageView resetBtn;
    String[] step;
    String strFoodId,language;
    CountDownTimer timer;
    Boolean isPause = true;
    int i = 0;
    long initTime = 600000;
    long leftTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        btnNav = findViewById(R.id.bottom_navigation);
        btnNav.setOnNavigationItemSelectedListener(navListener);
        Menu menu = btnNav.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);

        recipeDesc = (TextView) findViewById(R.id.recipeDesc);
        stepTag = (TextView) findViewById(R.id.stepTag);
        recipeTitle = (TextView) findViewById(R.id.recipeTitle);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        previousBtn = (Button) findViewById(R.id.previousBtn);
        finishBtn = (Button) findViewById(R.id.finishBtn);
        timeText = (TextView) findViewById(R.id.timeText);
        timeText.setVisibility(View.GONE);
        resetBtn = (ImageView) findViewById(R.id.resetBtn);
        resetBtn.setVisibility(View.GONE);

        loadLanguage();
        loadFoodId();
        loadRecipeStep();
        showRecipe();
        checkBtnVisible();
        checkTimeVisible();

        //顯示下一步驟
        nextBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                i++;
                resetTimer();
                showRecipe();
                checkBtnVisible();
                checkTimeVisible();
            }
        });

        //顯示上一步驟
        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i--;
                resetTimer();
                showRecipe();
                checkBtnVisible();
                checkTimeVisible();
            }
        });

        //回到介紹頁面
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecipeActivity.this, DetailActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putString("FOOD_ID", strFoodId);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });

        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPause){
                    startTimer();
                }else{
                    pauseTimer();
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               resetTimer();
            }
        });
        updateCountDownText();

    }

    //開始倒數計時
    private void startTimer(){
        timer = new CountDownTimer(leftTime,1000){
            @Override
            public void onTick(long millisUntilFinished) {
                leftTime = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                isPause = true;
                timeText.setText("00:00");
            }
        }.start();
        isPause = false;

    }

    //暫停時間
    private void pauseTimer(){
        timer.cancel();
        isPause = true;
    }

    //重製時間
    private void resetTimer(){
        leftTime = initTime;
        if(timer!=null){
            timer.cancel();
        }
        isPause = true;
        updateCountDownText();
    }

    //更新畫面時間
    private void updateCountDownText(){
        int minutes = (int) ((leftTime / 1000) / 60);
        int seconds = (int) ((leftTime / 1000) % 60);

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        timeText.setText(timeLeftFormatted);
    }

    //撈出食物名稱
    public void loadFoodId(){
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            strFoodId = bundle.getString("FOOD_ID", null);

            //顯示食物名稱
            String recipe = getResources().getString(R.string.recipe);
            String result = DBConnector.executeQuery("findFoodById",language,strFoodId);
            Gson gson = new Gson();
            ArrayList<FoodData> foodData = gson.fromJson(result, new TypeToken<ArrayList<FoodData>>(){}.getType());
            recipeTitle.setText(recipe + " - " + foodData.get(0).getFoodName());
        }else{
            Toast.makeText(this,R.string.error,LENGTH_SHORT).show();
        }
    }

    //依食物名稱從資料庫撈出相應食譜
    public void loadRecipeStep(){
        try {
            String result = DBConnector.executeQuery("recipePage",language,strFoodId);
            step = result.split("#");
        }catch (Exception e){
            Toast.makeText(this, e.toString(), LENGTH_LONG).show();
        }
    }

    //顯示食譜於畫面
    public void showRecipe(){
        if(step[i] != null){
            int stepNum = i+1;
            stepTag.setText("STEP"+stepNum);
            recipeDesc.setText(step[i]); //印出當前步驟
        }else {
            recipeDesc.setText(R.string.noInfo);
        }
    }

    //判斷按鍵隱藏或顯示
    public void checkBtnVisible() {
        //若現在為最後一步 nextBtn 隱藏，否則顯示
        //若現在為最後一步 finishBtn 顯示，否則隱藏
        if (step.length == i + 1) {
            nextBtn.setVisibility(View.GONE);
            finishBtn.setVisibility(View.VISIBLE);
        } else {
            nextBtn.setVisibility(View.VISIBLE);
            finishBtn.setVisibility(View.GONE);
        }

        //若現在為第一步 previousBtn 隱藏，否則顯示
        if (i == 0) {
            previousBtn.setVisibility(View.GONE);
        } else {
            previousBtn.setVisibility(View.VISIBLE);
        }
    }

    //判斷是否需要計時
    public void checkTimeVisible(){
        switch (strFoodId){
            case "1":
                if(i==3){
                    initTime = 1800000;
                    timeText.setVisibility(View.VISIBLE);
                    resetBtn.setVisibility(View.VISIBLE);
                }else{
                    timeText.setVisibility(View.GONE);
                    resetBtn.setVisibility(View.GONE);
                }
                break;
            case "4":
                if(i==1){
                    initTime = 900000;
                    timeText.setVisibility(View.VISIBLE);
                    resetBtn.setVisibility(View.VISIBLE);
                }else if(i==2){
                    initTime = 45000;
                    timeText.setVisibility(View.VISIBLE);
                    resetBtn.setVisibility(View.VISIBLE);
                }else{
                    timeText.setVisibility(View.GONE);
                    resetBtn.setVisibility(View.GONE);
                }
                break;
            case "5":
                if(i==1){
                    initTime = 120000;
                    timeText.setVisibility(View.VISIBLE);
                    resetBtn.setVisibility(View.VISIBLE);
                }else{
                    timeText.setVisibility(View.GONE);
                    resetBtn.setVisibility(View.GONE);
                }
                break;
            case "8":
                if(i==4){
                    initTime = 30000;
                    timeText.setVisibility(View.VISIBLE);
                    resetBtn.setVisibility(View.VISIBLE);
                }else{
                    timeText.setVisibility(View.GONE);
                    resetBtn.setVisibility(View.GONE);
                }
                break;
            case "10":
                if(i==0){
                    initTime = 600000;
                    timeText.setVisibility(View.VISIBLE);
                    resetBtn.setVisibility(View.VISIBLE);
                }else if(i==4){
                    initTime = 600000;
                    timeText.setVisibility(View.VISIBLE);
                    resetBtn.setVisibility(View.VISIBLE);
                }else if(i==5){
                    initTime = 300000;
                    timeText.setVisibility(View.VISIBLE);
                    resetBtn.setVisibility(View.VISIBLE);
                }else{
                    timeText.setVisibility(View.GONE);
                    resetBtn.setVisibility(View.GONE);
                }
                break;
            case "11":
                if(i==5){
                    initTime = 30000;
                    timeText.setVisibility(View.VISIBLE);
                    resetBtn.setVisibility(View.VISIBLE);
                }else{
                    timeText.setVisibility(View.GONE);
                    resetBtn.setVisibility(View.GONE);
                }
                break;
            case "12":
                if(i==2){
                    initTime = 1200000;
                    timeText.setVisibility(View.VISIBLE);
                    resetBtn.setVisibility(View.VISIBLE);
                }else if(i==6){
                    initTime = 900000;
                    timeText.setVisibility(View.VISIBLE);
                    resetBtn.setVisibility(View.VISIBLE);
                }else{
                    timeText.setVisibility(View.GONE);
                    resetBtn.setVisibility(View.GONE);
                }
                break;
            default:
                timeText.setVisibility(View.GONE);
                resetBtn.setVisibility(View.GONE);
                break;
        }
        leftTime = initTime;
        updateCountDownText();
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
                    Intent Home = new Intent(RecipeActivity.this, HomeActivity.class);
                    startActivity(Home);
                    break;
                case R.id.nav_search:
                    Intent Search = new Intent(RecipeActivity.this, SearchActivity.class);
                    startActivity(Search);
                    break;
                case R.id.nav_person:
                    Intent Person = new Intent(RecipeActivity.this, PersonalActivity.class);
                    startActivity(Person);
                    break;
                case  R.id.nav_camera:
                    Intent intent = new Intent(RecipeActivity.this, RecognitionActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_setting:
                    Intent Setting = new Intent(RecipeActivity.this, SettingActivity.class);
                    startActivity(Setting);
                    break;
            }
            return false;
        }
    };
}
