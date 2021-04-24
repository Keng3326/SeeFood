package com.example.cuisine;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;

public class InitialActivity extends AppCompatActivity {
    Button homeBtn,cameraBtn,albumBtn;
    public static DatabaseHelper databaseHelper;
    ImageView imageView;
    Uri imageUri;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_initial);

        databaseHelper = new DatabaseHelper(this);

        homeBtn = (Button) findViewById(R.id.homeBtn);
        cameraBtn = (Button) findViewById(R.id.cameraBtn);
        albumBtn = (Button) findViewById(R.id.albumBtn);
        imageView = (ImageView) findViewById(R.id.imageView);

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InitialActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
    };

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
                Intent intent = new Intent(InitialActivity.this, RecognitionActivity.class);
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
            CropIntent.putExtra("aspectX", 1);
            CropIntent.putExtra("aspectY", 1);
            CropIntent.putExtra("scaleUpIfNeeded", true);
            CropIntent.putExtra("return-data", true);

            startActivityForResult(CropIntent,1);
        }catch (ActivityNotFoundException ex){

        }
    }

    //變更畫面語系
    private void setLocale(String language){
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences sharedPref = this.getSharedPreferences("setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("language",language);
        editor.apply();
    }

    //取得語系
    public void loadLocale(){
        SharedPreferences preferences = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        String language = preferences.getString("language","");
        setLocale(language);
    }

}
