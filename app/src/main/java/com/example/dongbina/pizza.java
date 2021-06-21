package com.example.dongbina;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class pizza extends AppCompatActivity {

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
        setContentView(R.layout.activity_pizza);

        Button registerButton = (Button) findViewById(R.id.pizzamenu1);
        Button registerButton2 = (Button) findViewById(R.id.pizzamenu2);
        Button registerButton3 = (Button) findViewById(R.id.pizzamenu3);

        registerButton.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=1;

                latitude=35.241206318862396;
                longitude=129.08622515320488;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(pizza.this, pizza1_info.class); //register에서  html로 이동
                pizza.this.startActivity(gogoIntent);
            }
        });

        registerButton2.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=2;

                latitude=35.232182079285934;
                longitude=129.08409662621625;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(pizza.this, pizza2_info.class); //register에서  html로 이동
                pizza.this.startActivity(gogoIntent);
            }
        });

        registerButton3.setOnClickListener(new View.OnClickListener(){
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=3;

                latitude= 35.23092420139408;
                longitude=129.08504759752597;
                //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                Intent gogoIntent = new Intent(pizza.this, pizza3_info.class); //register에서  html로 이동
                pizza.this.startActivity(gogoIntent);
            }
        });

    };
}