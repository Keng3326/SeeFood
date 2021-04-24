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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.EventListener;
import java.util.Locale;

public class RecognitionActivity extends AppCompatActivity {
    BottomNavigationView btnNav;
    ImageView imageView;
    Button recognitionBtn,renewBtn;
    Bitmap bitmap;
    TextView title,noImageText;
    ImageView appName,back;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        btnNav = findViewById(R.id.bottom_navigation);
        btnNav.setOnNavigationItemSelectedListener(navListener);
        Menu menu = btnNav.getMenu();
        MenuItem menuItem = menu.getItem(2);
        menuItem.setChecked(true);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        noImageText = (TextView) findViewById(R.id.noImageText);
        imageView = (ImageView) findViewById(R.id.image);
        recognitionBtn = (Button) findViewById(R.id.recognitionBtn);
        renewBtn = (Button) findViewById(R.id.renewBtn);

        title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(R.string.recognitionTitle);
        appName = (ImageView) findViewById(R.id.appName);
        appName.setVisibility(View.GONE);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null){
            bitmap = bundle.getParcelable("bitmap");
            imageView.setImageBitmap(bitmap);
        }else {
            openCamera();
        }

        recognitionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognitionActivity.this, LoadingActivity.class);
                Bundle bundle = new Bundle();
                bundle.putParcelable("bitmap",bitmap);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        renewBtn = (Button) findViewById(R.id.renewBtn);
        renewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] listItem = {"開啟相簿","開啟相機"};
                AlertDialog.Builder alert = new AlertDialog.Builder(RecognitionActivity.this);
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
        noImageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String openCamera = getResources().getString(R.string.openCamera);
                String openAlbum = getResources().getString(R.string.openAlbum);
                final String[] listItem = {openAlbum,openCamera};
                AlertDialog.Builder alert = new AlertDialog.Builder(RecognitionActivity.this);
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
        checkImageExist();
    }
    public void checkImageExist(){
        if(imageView.getDrawable() == null){
            noImageText.setVisibility(View.VISIBLE);
            recognitionBtn.setVisibility(View.GONE);
            renewBtn.setVisibility(View.GONE);
        }else {
            noImageText.setVisibility(View.GONE);
            recognitionBtn.setVisibility(View.VISIBLE);
            renewBtn.setVisibility(View.VISIBLE);
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
                imageView.setImageBitmap(bitmap);
                checkImageExist();
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
                    Intent Home = new Intent(RecognitionActivity.this, HomeActivity.class);
                    startActivity(Home);
                    break;
                case R.id.nav_search:
                    Intent Search = new Intent(RecognitionActivity.this, SearchActivity.class);
                    startActivity(Search);
                    break;
                case R.id.nav_person:
                    Intent Person = new Intent(RecognitionActivity.this, PersonalActivity.class);
                    startActivity(Person);
                    break;
                case  R.id.nav_camera:
                    Intent camera = new Intent(RecognitionActivity.this, RecognitionActivity.class);
                    startActivity(camera);
                    break;
                case R.id.nav_setting:
                    Intent Setting = new Intent(RecognitionActivity.this, SettingActivity.class);
                    startActivity(Setting);
                    break;
            }
            return false;
        }
    };
}
