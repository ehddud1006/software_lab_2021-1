package com.example.dongbina;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class chinesefood extends AppCompatActivity {

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
        setContentView(R.layout.activity_chinesefood);

        Button registerButton = (Button) findViewById(R.id.chinesemenu1);
        Button registerButton2 = (Button) findViewById(R.id.chinesemenu2);
        Button registerButton3 = (Button) findViewById(R.id.chinesemenu3);

        registerButton.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=1;

                latitude=35.232466287122335;
                longitude=129.0877182267558;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(chinesefood.this, chinesefood1_info.class); //register에서  html로 이동
                chinesefood.this.startActivity(gogoIntent);
            }
        });

        registerButton2.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=2;

                latitude=35.22948288410722;
                longitude=129.0831540403238;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(chinesefood.this, info.class); //register에서  html로 이동
                chinesefood.this.startActivity(gogoIntent);
            }
        });

        registerButton3.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=3;

                latitude=35.22477149876917;
                longitude=129.08111199983665;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(chinesefood.this, chinesefood3_info.class); //register에서  html로 이동
                chinesefood.this.startActivity(gogoIntent);
            }
        });

    };
}