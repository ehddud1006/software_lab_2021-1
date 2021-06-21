package com.example.dongbina;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class koreanfood extends AppCompatActivity {
    static double latitude;
    static double longitude;

    static int number;
    public static double getStartPoint1() {
        return latitude;
    }
    public static double getStartPoint2() {
        return longitude;
    }

    public static int getNumber() {
        return number;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_koreanfood);

        Button registerButton = (Button) findViewById(R.id.koreanmenu1);
        Button registerButton2 = (Button) findViewById(R.id.koreanmenu2);
        Button registerButton3 = (Button) findViewById(R.id.koreanmenu3);

        registerButton.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=1;
                latitude=35.23931205752789;
                longitude=129.08628230042177;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(koreanfood.this, koreanfood1_info.class); //register에서  html로 이동
                koreanfood.this.startActivity(gogoIntent);
            }
        });

        registerButton2.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=2;

                //latitude=35.23165061602714;
                //longitude=129.08874
                // 132621628;
                latitude=35.23113587040391;
                longitude=129.08888525751325;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(koreanfood.this, koreanfood2_info.class); //register에서  html로 이동
                koreanfood.this.startActivity(gogoIntent);
            }
        });

        registerButton3.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=3;

                latitude=35.230241629340156;
                longitude=129.08533457753686;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(koreanfood.this, koreanfood3_info.class); //register에서  html로 이동
                koreanfood.this.startActivity(gogoIntent);
            }
        });
        };
}

