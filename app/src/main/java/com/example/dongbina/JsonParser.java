package com.example.dongbina;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {
    static String[] nami ;
    static String[] hami ;
    public static String[] getNami() {
        return nami;
    }
    public static String[] getHami() {
        return hami;
    }
    public static void jsonParser(String resultJson) {


        try{
            System.out.println("LOVE");
            //System.out.println(resultJson);
            JSONObject jsonObject = new JSONObject(resultJson);
            JSONObject Object = (JSONObject) jsonObject.get("route");
            JSONArray Array1 = (JSONArray) Object.get("tracomfort");
            JSONObject Object2 = (JSONObject) Array1.get(0);

            //https://mohading.tistory.com/32
            //route < trafast < path
            System.out.println( Object.toString());
            System.out.println( ((JSONObject) jsonObject.get("route")).toString());
            System.out.println(Array1.get(0).toString());
            System.out.println(Array1.toString());
            System.out.println(Object2.get("path").toString());
            String A = Object2.get("path").toString();
            String[] gg = A.split(",");
            nami = new String[gg.length];
            int a = 0;
            for(String s : gg) {
                if(a%2==0)
                    nami[a]= s.replace("[", "");
                if(a%2==1)
                    nami[a]= s.replace("]", "");
                //System.out.println(s);
                a++;
            }
            double latitude = APIMainActivity.getStartPoint1();
            double longitude = APIMainActivity.getStartPoint2();

            double goal_latitude = 35.179470 ;
            double goal_longitude = 129.075986;
            int number;
            int type = register.getType();
            System.out.println(type);
            if(type==1)
            {
                //number = koreanfood.getNumber();
                //if(number==1) {
                goal_latitude = koreanfood.getStartPoint1();
                goal_longitude = koreanfood.getStartPoint2();
                //}

            }
            else if(type==2)
            {
                //number = koreanfood.getNumber();
                //if(number==1) {
                goal_latitude = chinesefood.getStartPoint1();
                goal_longitude = chinesefood.getStartPoint2();
                //}

            }
            else if(type==3)
            {
                //number = koreanfood.getNumber();
                //if(number==1) {
                goal_latitude = chicken.getStartPoint1();
                goal_longitude = chicken.getStartPoint2();
                //}

            }
            else if(type==4)
            {
                //number = koreanfood.getNumber();
                //if(number==1) {
                goal_latitude = pizza.getStartPoint1();
                goal_longitude = pizza.getStartPoint2();
                //}

            }
            else if(type==5)
            {
                //number = koreanfood.getNumber();
                //if(number==1) {
                goal_latitude = fastfood.getStartPoint1();
                goal_longitude = fastfood.getStartPoint2();
                //}

            }
            else if(type==6)
            {
                //number = koreanfood.getNumber();
                //if(number==1) {
                goal_latitude = pig.getStartPoint1();
                goal_longitude = pig.getStartPoint2();
                //}

            }

            /*
            for(String s : nami) {
                System.out.println(s);
            }

            */
            System.out.println("nami - length");
            System.out.println(nami.length);
            System.out.println( jsonObject.get("code"));
            System.out.println( Array1.get(1));
            System.out.println("LOVE3");
            System.out.println( Array1.get(1).toString());
            System.out.println("LOVE2");
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject2 = new JSONObject();

            jsonArray = jsonObject.getJSONArray("path");
            for(int i=0; i<=jsonArray.length(); i++){
                jsonObject2 = jsonArray.getJSONObject(i);
                //String cardTitle = jsonObject2.getString("cardTitle");
                //Log.d("resultData","cardTitle : "+cardTitle);
                System.out.println(jsonObject2);
            }
        } catch (JSONException e) {
        }

    }

}

/*
{

        "code": 0,
        "message": "길찾기를 성공하였습니다.",
        "currentDateTime": "2018-12-21T14:45:34",
        "route": {
        "trafast": [
        {
        "summary": {
        "start": {
        "location": [
        127.1058342,
        37.3597078
        ]
        },
        "goal": {
        "location": [
        129.0759853,
        35.1794697
        ],
        "dir": 2
        },
        "distance": 382403,
        "duration": 15372873,
        "bbox": [
        [
        127.0833901,
        35.1793188
        ],
        [
        129.0817364,
        37.3599059
        ]
        ],
        "tollFare": 24500,
        "taxiFare": 319900,
        "fuelPrice": 46027
        },
        "path": [
        [
        127.1059968,
        37.3597093
        ],

        ....

        [
        129.0764276,
        35.1795108
        ],
        [
        129.0762855,
        35.1793188
        ]
        ],
        "section": [
        {
        "pointIndex": 654,
        "pointCount": 358,
        "distance": 22495,
        "name": "죽양대로",
        "congestion": 1,
        "speed": 60
        },
        {
        "pointIndex": 3059,
        "pointCount": 565,
        "distance": 59030,
        "name": "낙동대로",
        "congestion": 1,
        "speed": 89
        },
        {
        "pointIndex": 4708,
        "pointCount": 433,
        "distance": 23385,
        "name": "새마을로",
        "congestion": 1,
        "speed": 66
        }
        ],
        "guide": [
        {
        "pointIndex": 1,
        "type": 3,
        "instructions": "정자일로1사거리에서 '성남대로' 방면으로 우회전",
        "distance": 21,
        "duration": 4725
        },
        {
        "pointIndex": 8,
        "type": 3,
        "instructions": "불정교사거리에서 '수원·용인, 미금역' 방면으로 우회전",
        "distance": 186,
        "duration": 42914
        },

        ....

        {
        "pointIndex": 6824,
        "type": 14,
        "instructions": "연산교차로에서 '서면교차로, 시청·경찰청' 방면으로 오른쪽 1시 방향",
        "distance": 910,
        "duration": 125240
        },
        {
        "pointIndex": 6842,
        "type": 88,
        "instructions": "목적지",
        "distance": 895,
        "duration": 111333
        }
        ]
        }
        ]
        }
        }


*/



