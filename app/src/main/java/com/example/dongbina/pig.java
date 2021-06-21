package com.example.dongbina;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class pig extends AppCompatActivity {

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
        setContentView(R.layout.activity_pig);

        Button registerButton = (Button) findViewById(R.id.pigmenu1);
        Button registerButton2 = (Button) findViewById(R.id.pigmenu2);
        Button registerButton3 = (Button) findViewById(R.id.pigmenu3);

        registerButton.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=1;

                latitude=35.23316702427506;
                longitude=129.08525785490576;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(pig.this, pig1_info.class); //register에서  html로 이동
                pig.this.startActivity(gogoIntent);
            }
        });

        registerButton2.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=2;

                latitude=35.23245960296912;
                longitude=129.0856004214598;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(pig.this, pig2_info.class); //register에서  html로 이동
                pig.this.startActivity(gogoIntent);
            }
        });

        registerButton3.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=3;

                latitude=35.238979391213114;
                longitude=129.08843283397417;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(pig.this, pig3_info.class); //register에서  html로 이동
                pig.this.startActivity(gogoIntent);
            }
        });
    };
}