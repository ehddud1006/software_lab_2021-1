package com.example.dongbina;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class pizza2_info extends AppCompatActivity {

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
        setContentView(R.layout.activity_pizza2_info);

        Button registerButton = (Button) findViewById(R.id.show_road);

        registerButton.setOnClickListener(new View.OnClickListener() {
            //35.23938884518133, 129.0862408820401
            @Override
            public void onClick(View v) {
                //number=1;

                Intent gogoIntentgg = new Intent(pizza2_info.this, APIMainActivity.class); //register에서  html로 이동
                pizza2_info.this.startActivity(gogoIntentgg);
            }
        });



    }


}