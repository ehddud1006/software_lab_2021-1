package com.example.dongbina;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class register extends AppCompatActivity {
    static int type ;
    public static int getType() {
        return type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registerButton = (Button) findViewById(R.id.CollegeMenu);
        Button registerButton2 = (Button) findViewById(R.id.koreanmenu);
        Button registerButton3 = (Button) findViewById(R.id.chinesemenu);
        Button registerButton4 = (Button) findViewById(R.id.chickenmenu);
        Button registerButton7 = (Button) findViewById(R.id.pigmenu);
        Button registerButton5 = (Button) findViewById(R.id.pizzamenu);
        Button registerButton6 = (Button) findViewById(R.id.fastfoodmenu);

        registerButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(register.this, html.class); //register에서  html로 이동
                register.this.startActivity(registerIntent);
            }
        });

        registerButton2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                type=1;  // 음식 종류
                Intent registerIntent2 = new Intent(register.this, koreanfood.class); //register에서  koreanfood로 이동
                register.this.startActivity(registerIntent2);
            }
        });

        registerButton3.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                type=2;
                Intent registerIntent3 = new Intent(register.this, chinesefood.class); //register에서  chinesefood 이동
                register.this.startActivity(registerIntent3);
            }
        });
        registerButton4.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                type=3;
                Intent registerIntent4 = new Intent(register.this, chicken.class); //register에서  chiccken 이동
                register.this.startActivity(registerIntent4);
            }
        });
        registerButton5.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                type=4;
                Intent registerIntent5 = new Intent(register.this, pizza.class); //register에서  pig 이동
                register.this.startActivity(registerIntent5);
            }
        });

        registerButton6.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                type=5;
                Intent registerIntent6 = new Intent(register.this, fastfood.class); //register에서  pig 이동
                register.this.startActivity(registerIntent6);
            }
        });

        registerButton7.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                type=6;
                Intent registerIntent7 = new Intent(register.this, pig.class); //register에서  pig 이동
                register.this.startActivity(registerIntent7);
            }
        });
    }
}
