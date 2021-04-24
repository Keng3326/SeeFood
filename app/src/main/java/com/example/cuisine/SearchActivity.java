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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    BottomNavigationView btnNav;
    EditText nameEdt;
    Button clearBtn,submitByn;
    Spinner meatSpin,typeSpin;
    RadioGroup vegetarianGroup,spicyGroup;
    TextView title;
    ImageView back,appName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        btnNav = findViewById(R.id.bottom_navigation);
        btnNav.setOnNavigationItemSelectedListener(navListener);
        Menu menu = btnNav.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        title = (TextView) findViewById(R.id.title);
        title.setVisibility(View.VISIBLE);
        title.setText(R.string.searchTitle);
        appName = (ImageView) findViewById(R.id.appName);
        appName.setVisibility(View.GONE);

        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        nameEdt = (EditText) findViewById(R.id.foodNameEditText);
        vegetarianGroup = (RadioGroup) findViewById(R.id.vegetarianGroup);
        spicyGroup = (RadioGroup) findViewById(R.id.spicyGroup);

        typeSpin = findViewById(R.id.typeSpinner);
        ArrayAdapter<CharSequence> typeAdapter = ArrayAdapter.createFromResource(this,R.array.typeSpinner, R.layout.support_simple_spinner_dropdown_item);
        typeAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        typeSpin.setAdapter(typeAdapter);

        meatSpin = findViewById(R.id.meatSpinner);
        ArrayAdapter<CharSequence> meatAdapter = ArrayAdapter.createFromResource(this,R.array.meatSpinner, R.layout.support_simple_spinner_dropdown_item);
        meatAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        meatSpin.setAdapter(meatAdapter);

        submitByn = (Button) findViewById(R.id.submitBtn);
        submitByn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchFood(v);
            }
        });

        clearBtn = (Button) findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameEdt.setText("");
                vegetarianGroup.clearCheck();
                spicyGroup.clearCheck();
                typeSpin.setSelection(0);
                meatSpin.setSelection(0);
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()){
                case R.id.nav_home:
                    Intent Home = new Intent(SearchActivity.this, HomeActivity.class);
                    startActivity(Home);
                    break;
                case R.id.nav_search:
                    Intent Search = new Intent(SearchActivity.this, SearchActivity.class);
                    startActivity(Search);
                    break;
                case R.id.nav_person:
                    Intent Person = new Intent(SearchActivity.this, PersonalActivity.class);
                    startActivity(Person);
                    break;
                case  R.id.nav_camera:
                    Intent intent = new Intent(SearchActivity.this, RecognitionActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_setting:
                    Intent Setting = new Intent(SearchActivity.this, SettingActivity.class);
                    startActivity(Setting);
                    break;
            }
            return false;
        }
    };

    public void searchFood(View view){
        String vegetarian = "";
        String spicy = "";
        String type = "";
        String meat = "";
        String foodName = nameEdt.getText().toString();

        //葷(0)素(1)
        switch (vegetarianGroup.getCheckedRadioButtonId()){
            case R.id.vegetarianBtn:
                vegetarian = "1";
                break;
            case R.id.non_vegetarianBtn:
                vegetarian = "0";
                break;
        }
        //辣(1)不辣(0)
        switch (spicyGroup.getCheckedRadioButtonId()){
            case R.id.spicyBtn:
                spicy = "1";
                break;
            case R.id.non_spicyBtn:
                spicy = "0";
                break;
        }

        if(typeSpin.getSelectedItemPosition() != 0){
            type = typeSpin.getSelectedItem().toString();
        }
        if(meatSpin.getSelectedItemPosition() != 0){
            meat = meatSpin.getSelectedItem().toString();
        }

        Bundle bundle = new Bundle();
        bundle.putString("FOOD_NAME", foodName);
        bundle.putString("FOOD_VEG", vegetarian);
        bundle.putString("FOOD_SPY", spicy);
        bundle.putString("FOOD_TYPE", type);
        bundle.putString("FOOD_MEAT", meat);

        Intent intent = new Intent(SearchActivity.this, HomeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
