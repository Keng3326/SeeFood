package com.example.cuisine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.widget.Toast.LENGTH_LONG;

public class RecognitionResultActivity extends AppCompatActivity {
    TextView title,foodName,foodId;
    BottomNavigationView btnNav;
    ImageView foodImg;
    Button detailBtn,renewBtn;
    String strfoodId = null;
    String recognitionResult,language;
    Bitmap bitmap;
    ImageView back,appName;
    Uri imageUri;
    public static DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition_result);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectNetwork()
                        .penaltyLog()
                        .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectLeakedClosableObjects()
                        .penaltyLog()
                        .penaltyDeath()
                        .build());

        btnNav = findViewById(R.id.bottom_navigation);
        btnNav.setOnNavigationItemSelectedListener(navListener);
        Menu menu = btnNav.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(R.string.recognitionResultTitle);
        appName = (ImageView) findViewById(R.id.appName);
        appName.setVisibility(View.GONE);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        foodName = (TextView) findViewById(R.id.foodName);
        foodImg = (ImageView) findViewById(R.id.foodImg);
        foodId = (TextView) findViewById(R.id.foodId);
        detailBtn = (Button) findViewById(R.id.detailBtn);
        renewBtn = (Button) findViewById(R.id.renewBtn);

        loadLanguage();

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {

            recognitionResult = bundle.getString("result", "");
            bitmap = bundle.getParcelable("bitmap");
            findFoodId(recognitionResult);

            //如果有找到對應的食物ID，進資料庫撈出食物資訊
            if(strfoodId!=null){
                try {
                    String result = DBConnector.executeQuery("findFoodById", language, strfoodId);
                    Gson gson = new Gson();
                    ArrayList<FoodData> foodData = gson.fromJson(result, new TypeToken<ArrayList<FoodData>>(){}.getType());
                    foodId.setText(foodData.get(0).getFoodId());
                    foodName.setText(foodData.get(0).getFoodName());
                    Picasso.with(this).load(foodData.get(0).getFoodImg()).into(foodImg);
                }catch (Exception e){
                    Toast.makeText(this, e.toString(), LENGTH_LONG).show();
                }
            }else{
                foodName.setText(R.string.noInfo);
            }

        }

        try{
            InitialActivity.databaseHelper.insertData(
                    strfoodId,
                    getDate(),
                    imageViewToByte()
            );
        }catch (Exception e){
            e.printStackTrace();
        }

        detailBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(RecognitionResultActivity.this, DetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("FOOD_ID", foodId.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        renewBtn = (Button) findViewById(R.id.renewBtn);
        renewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] listItem = {"開啟相簿","開啟相機"};
                AlertDialog.Builder alert = new AlertDialog.Builder(RecognitionResultActivity.this);
                alert.setTitle("Select:");
                alert.setItems(listItem, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        if (which == 0){
                            openGallery();
                        } else if(which == 1){
                            openCamera();
                        }
                        dialog.dismiss();
                    }
                });

                AlertDialog dialog = alert.create();
                dialog.show();
            }
        });
    }

    private byte[] imageViewToByte(){
        //Bitmap bitmap =((BitmapDrawable)foodImg.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    //取得目前日期
    private String getDate(){
        Calendar mCal = Calendar.getInstance();
        CharSequence today = DateFormat.format("yyyy/MM/dd", mCal.getTime());
        return today.toString();
    }

    //取得語系
    public void loadLanguage(){
        SharedPreferences sharedPref = this.getSharedPreferences("setting", Context.MODE_PRIVATE);
        language = sharedPref.getString("language",null);
    }

    //辨識結果對照料理ID
    public void findFoodId(String recognitionId){
        switch (recognitionId){
            case "0":
                strfoodId = "3";
                break;
            case "1":
                strfoodId = "14";
                break;
            case "2":
                strfoodId = "15";
                break;
            case "3":
                strfoodId = "13";
                break;
            case "4":
                strfoodId = "4";
                break;
            case "5":
                strfoodId = "8";
                break;
            case "6":
                strfoodId = "5";
                break;
            case "7":
                strfoodId = "10";
                break;
            case "8":
                strfoodId = "12";
                break;
            case "9":
                strfoodId = "2";
                break;
            case "10":
                strfoodId = "7";
                break;
            case "11":
                strfoodId = "1";
                break;
            case "12":
                strfoodId = "11";
                break;
            case "13":
                strfoodId = "6";
                break;
            case "14":
                strfoodId = "9";
                break;
        }
    }

    //開啟相機
    private void openCamera() {
        Intent camera = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),"file"+String.valueOf(System.currentTimeMillis())+".jpg");
        imageUri = Uri.fromFile(file);
        camera.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        camera.putExtra("return_data",true);
        startActivityForResult(camera, 0);
    }

    //開啟相簿
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(gallery,"Select Image From Gallery"),2);
    }

    //取得相片
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == RESULT_OK && requestCode == 0) {
            cropImage();
        }else if(requestCode == 2){
            if(data != null){
                imageUri = data.getData();
                cropImage();
            }
        }else if(requestCode == 1){
            if(data != null){
                Bundle bundle = data.getExtras();
                bitmap = bundle.getParcelable("data");
                Intent intent = new Intent(RecognitionResultActivity.this, RecognitionActivity.class);
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable("bitmap",bitmap);
                intent.putExtras(bundle2);
                startActivity(intent);
            }else{
                finish();
            }
        }
    }

    //裁切相片
    public void cropImage(){
        try{
            Intent CropIntent = new Intent("com.android.camera.action.CROP");
            CropIntent.setDataAndType(imageUri,"image/*");

            CropIntent.putExtra("crop", "true");
            CropIntent.putExtra("outputX", 400);
            CropIntent.putExtra("outputY", 400);
            CropIntent.putExtra("aspectX", 4);
            CropIntent.putExtra("aspectY", 4);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);

            startActivityForResult(CropIntent,1);
        }catch (ActivityNotFoundException ex){

        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    Intent Home = new Intent(RecognitionResultActivity.this, HomeActivity.class);
                    startActivity(Home);
                    break;
                case R.id.nav_search:
                    Intent Search = new Intent(RecognitionResultActivity.this, SearchActivity.class);
                    startActivity(Search);
                    break;
                case R.id.nav_person:
                    Intent Person = new Intent(RecognitionResultActivity.this, PersonalActivity.class);
                    startActivity(Person);
                    break;
                case  R.id.nav_camera:
                    Intent camera = new Intent(RecognitionResultActivity.this, RecognitionActivity.class);
                    startActivity(camera);
                    break;
                case R.id.nav_setting:
                    Intent Setting = new Intent(RecognitionResultActivity.this, SettingActivity.class);
                    startActivity(Setting);
                    break;
            }
            return false;
        }
    };
}
