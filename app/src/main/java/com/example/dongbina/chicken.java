package com.example.dongbina;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class chicken extends AppCompatActivity {

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
        setContentView(R.layout.activity_chicken);

        Button registerButton = (Button) findViewById(R.id.chickenmenu1);
        Button registerButton2 = (Button) findViewById(R.id.chickenmenu2);
        Button registerButton3 = (Button) findViewById(R.id.chickenmenu3);

        registerButton.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=1;
                latitude=35.22566205128809;
                longitude=129.08560478388677;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(chicken.this, chicken1_info.class); //register에서  html로 이동
                chicken.this.startActivity(gogoIntent);
            }
        });

        registerButton2.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=2;

                latitude= 35.23538331328821;
                longitude=129.08408675689856;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(chicken.this, chicken2_info.class); //register에서  html로 이동
                chicken.this.startActivity(gogoIntent);
            }
        });

        registerButton3.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=3;

                latitude=35.233133847263986;
                longitude=129.08581461087513;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(chicken.this, chicken3_info.class); //register에서  html로 이동
                chicken.this.startActivity(gogoIntent);
            }
        });

    };
}