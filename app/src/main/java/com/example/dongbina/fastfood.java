package com.example.dongbina;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class fastfood extends AppCompatActivity {

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
            setContentView(R.layout.activity_fastfood);

            Button registerButton = (Button) findViewById(R.id.fastfoodmenu1);
            Button registerButton2 = (Button) findViewById(R.id.fastfoodmenu2);
            Button registerButton3 = (Button) findViewById(R.id.fastfoodmenu3);

            registerButton.setOnClickListener(new View.OnClickListener(){
                //35.23938884518133, 129.0862408820401
                @Override
                public void onClick(View v) {
                    //number=1;

                    latitude=35.23252763165402;
                    longitude=129.08778259922798;
                    //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                    Intent gogoIntent = new Intent(fastfood.this, fastfood1_info.class); //register에서  html로 이동
                    fastfood.this.startActivity(gogoIntent);
                }
            });

            registerButton2.setOnClickListener(new View.OnClickListener(){
                //35.23938884518133, 129.0862408820401
                @Override
                public void onClick(View v) {
                    //number=2;

                    latitude=35.229804286065686;
                    longitude=129.08732560902814;
                    //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                    Intent gogoIntent = new Intent(fastfood.this, fastfood2_info.class); //register에서  html로 이동
                    fastfood.this.startActivity(gogoIntent);
                }
            });

            registerButton3.setOnClickListener(new View.OnClickListener(){
                //35.23938884518133, 129.0862408820401
                @Override
                public void onClick(View v) {
                    //number=3;

                    latitude=35.231414528510776;
                    longitude=129.08543913162202;
                    //latitude=35.23152700730556;
                    //longitude=129.08615394155746;
                    //<activity android:name=".APIMainActivity"></activity> 을 추가해야했다.
                    Intent gogoIntent = new Intent(fastfood.this, fastfood3_info.class); //register에서  html로 이동
                    fastfood.this.startActivity(gogoIntent);
                }
            });

        };
}