package com.example.dongbina;

import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;


public class html extends AppCompatActivity {

    private String htmlPageUrl = "https://www.pusan.ac.kr/kor/CMS/MenuMgr/menuListOnWeekly.do?mCode=MN203";
    private String[] htmlContentInStringFormat = new String[12];
    private String[] time = new String[12];
    private TextView[] Views = new TextView[12];
    private Boolean PN;
    private Boolean[] time_or_menu = new Boolean[12];
    private TextView name;
    private int[] ids= {R.id.Kb, R.id.Kl, R.id.Kd, R.id.Mb, R.id.Ml, R.id.Md, R.id.Sb, R.id.Sl, R.id.Sd, R.id.Hb, R.id.Hl, R.id.Hd};
    private Map<String, Integer> dir = new HashMap<String, Integer>();
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html);

        for(int i = 0; i < 12; i++){
            htmlContentInStringFormat[i] = "";
            time[i] = "";
            time_or_menu[i] = false; // false일때 메뉴 출력
            Views[i] = (TextView)findViewById(ids[i]);
        }
        Views[0].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                time_or_menu[0] = !time_or_menu[0];
                if(time_or_menu[0]) //시간 보이기
                    Views[0].setText(time[0]);
                else
                    Views[0].setText(htmlContentInStringFormat[0]);
            }
        });
        Views[1].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                time_or_menu[1] = !time_or_menu[1];
                if(time_or_menu[1]) //시간 보이기
                    Views[1].setText(time[1]);
                else
                    Views[1].setText(htmlContentInStringFormat[1]);
            }
        });
        Views[2].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                time_or_menu[2] = !time_or_menu[2];
                if(time_or_menu[2]) //시간 보이기
                    Views[2].setText(time[2]);
                else
                    Views[2].setText(htmlContentInStringFormat[2]);
            }
        });
        Views[3].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                time_or_menu[3] = !time_or_menu[3];
                if(time_or_menu[3]) //시간 보이기
                    Views[3].setText(time[3]);
                else
                    Views[3].setText(htmlContentInStringFormat[3]);
            }
        });
        Views[4].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                time_or_menu[4] = !time_or_menu[4];
                if(time_or_menu[4]) //시간 보이기
                    Views[4].setText(time[4]);
                else
                    Views[4].setText(htmlContentInStringFormat[4]);
            }
        });
        Views[5].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                time_or_menu[5] = !time_or_menu[5];
                if(time_or_menu[5]) //시간 보이기
                    Views[5].setText(time[5]);
                else
                    Views[5].setText(htmlContentInStringFormat[5]);
            }
        });
        Views[6].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                time_or_menu[6] = !time_or_menu[6];
                if(time_or_menu[6]) //시간 보이기
                    Views[6].setText(time[6]);
                else
                    Views[6].setText(htmlContentInStringFormat[6]);
            }
        });
        Views[7].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                time_or_menu[7] = !time_or_menu[7];
                if(time_or_menu[7]) //시간 보이기
                    Views[7].setText(time[7]);
                else
                    Views[7].setText(htmlContentInStringFormat[7]);
            }
        });
        Views[8].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                time_or_menu[8] = !time_or_menu[8];
                if(time_or_menu[8]) //시간 보이기
                    Views[8].setText(time[8]);
                else
                    Views[8].setText(htmlContentInStringFormat[8]);
            }
        });
        Views[9].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                time_or_menu[9] = !time_or_menu[9];
                if(time_or_menu[9]) //시간 보이기
                    Views[9].setText(time[9]);
                else
                    Views[9].setText(htmlContentInStringFormat[9]);
            }
        });
        Views[10].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                time_or_menu[10] = !time_or_menu[10];
                if(time_or_menu[10]) //시간 보이기
                    Views[10].setText(time[10]);
                else
                    Views[10].setText(htmlContentInStringFormat[10]);
            }
        });
        Views[11].setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                time_or_menu[11] = !time_or_menu[11];
                if(time_or_menu[11]) //시간 보이기
                    Views[11].setText(time[11]);
                else
                    Views[11].setText(htmlContentInStringFormat[11]);
            }
        });

        PN = false;
        name = (TextView)findViewById(R.id.pyo_name);
        dir.put("금정회관", 0);
        dir.put("문창회관", 3);
        dir.put("샛벌회관", 6);
        dir.put("학생회관", 9);

        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }

    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(htmlPageUrl).get();
                String change = doc.toString();
                change = change.replace("<!--", " ").replace("-->", " ");
                doc = Jsoup.parse(change);
                Elements element = doc.select("tr");
                if (element.select("p").size() == 0){
                    PN = true;
                }
                for(Element e: element.select("tr")) {
                    if(e.text().trim().split(" ")[1].equals("학생") || e.text().trim().split(" ")[1].equals("식당")){
                        int count = 0;
                        for (Element t: e.select("th ul li")){
                            time[dir.get(e.select("tr th").text().split(" ")[0]) + count] += t.text();
                            count += 1;
                            if(count == 3) break;
                        }
                        count = 0;
                        for (Element a: e.select("td")){
                            for(Element b: a.select("ul li")){
                                htmlContentInStringFormat[dir.get(e.select("tr th").text().split(" ")[0]) + count] += b.select("h3").text() + '\n';
                                if(b.select("p").text().contains(",")){
                                    htmlContentInStringFormat[dir.get(e.select("tr th").text().split(" ")[0]) + count] += b.select("p").text().replace(',', '\n');
                                }else{
                                    htmlContentInStringFormat[dir.get(e.select("tr th").text().split(" ")[0]) + count] += b.select("p").text().replace(' ', '\n');
                                }
                                htmlContentInStringFormat[dir.get(e.select("tr th").text().split(" ")[0]) + count] += "\n\n";
                            }
                            count += 1;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            for(int i = 0; i < 12; i++){
                Views[i].setText(htmlContentInStringFormat[i]);
            }
            if(PN)
                name.setText("오늘은 학식이 제공되지 않습니다.");
        }
    }
}