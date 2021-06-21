package com.example.dongbina;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIExamDatalabTrend extends Thread{

    @Override
    public void run() {
        try {
            String clientId = "7obj24rzqn";//애플리케이션 클라이언트 아이디값";
            String clientSecret = "KZiRCFqfYgZxLkP80YOWiThgxa9Q0NciO8fosOkS";//애플리케이션 클라이언트 시크릿값";크릿값";
            System.out.println("happy2");
            // 큰 수를 먼저 써야한다.
            // 이 class는 naver의 다른 api 를 보면서 익혔다 , 요청헤더,요청바디, 또하나 더 있었는데 기억이 안나네.. 를 고려해야하고
            // 처음 참고했던 api는 search trend api 였다 .
            // 이 api는 예제코드가 없어서 내가 직접 짜야했기에 더 힘들었다.
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

            //http://blog.naver.com/PostView.nhn?blogId=hang_sun&logNo=130049163148&viewDate=&currentPage=1&listtype=0 더블 스트링 형변환
            System.out.println("PLZ");
            System.out.println(goal_latitude );
            System.out.println(goal_longitude);

            //System.out.println("https://naveropenapi.apigw.ntruss.com/map-direction-15/v1/driving?start="+Double.toString(longitude)+","+Double.toString(latitude)+"&goal="+Double.toString(goal_longitude)+","+Double.toString(goal_latitude)+"&option=trafast");
            //String apiURL = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start=127.1058342,37.359708&goal=129.075986,35.179470&option=trafast";

            // latitude=35.23938884518133;
            //                longitude=129.0862408820401;

            // 싯팔 왜 부산대는 길안내가 안될까 .. 저주인가?
            // 오류를 알아냈다 거리가 가까운 경우에는 path를 잘게 나누면안된다 현재 70인 거리를 path를 5정도로 나누니까 됐다. 현재는 행운의 숫자 7로 나눈상태이다. 텀프에 행운이 깃들길..
            // 그런데 path가 적어지면 길안내의 정확성이 떨어진다.. 이를 어떻게 해결해야할지 고민이다.

            //String apiURL = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start="+Double.toString(longitude)+","+Double.toString(latitude)+"&goal="+"129.08265441789578,35.240261966967005"+"&option=trafast";

             //성공String apiURL = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start="+Double.toString(longitude)+","+Double.toString(latitude)+"&goal="+"129.07889422734266,35.065887186313866"+"&option=trafast"; //35.087560717745724, 129.0594015127184
            //자갈치 시장35.096997221327435, 129.03055162621285
            //String apiURL = "https://naveropenapi.apigw.ntruss.com/map-direction-15/v1/driving?start="+Double.toString(longitude)+","+Double.toString(latitude)+"&goal="+"129.0303490225188,35.09690737751043"+"&option=trafast"; //35.087560717745724, 129.0594015127184
            // 서여고
//            String apiURL = "https://naveropenapi.apigw.ntruss.com/map-direction-15/v1/driving?start="+Double.toString(longitude)+","+Double.toString(latitude)+"&goal="+"129.02183841087202,35.113865459182755"+"&option=trafast"; //35.087560717745724, 129.0594015127184

//          부산대
//            String apiURL = "https://naveropenapi.apigw.ntruss.com/map-direction-15/v1/driving?start="+Double.toString(longitude)+","+Double.toString(latitude)+"&goal="+"129.08402770881187,35.24250517830597"+"&option=trafast"; //35.087560717745724, 129.0594015127184

            // 현재는 테스트를 위해 시작점을 부산대로 하였습니다. 실시간으로 바꿀수 있습니다.

            String apiURL = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start="+Double.toString(longitude)+","
                    +Double.toString(latitude)+"&goal="+Double.toString(goal_longitude)+","+Double.toString(goal_latitude)
                    +"&option=tracomfort"; //35.087560717745724, 129.0594015127184

            // String apiURL = "https://naveropenapi.apigw.ntruss.com/map-direction/v1/driving?start="+"129.08112242734668"+","+"35.233862107806736"+"&goal="+"129.0453701973772,35.0848217101995"+"&option=trafast"; //35.087560717745724, 129.0594015127184
            //String body = "{\"startDate\":\"2017-01-01\",\"endDate\":\"2017-04-30\",\"timeUnit\":\"month\",\"keywordGroups\":[{\"groupName\":\"한글\",\"keywords\":[\"한글\",\"korean\"]},{\"groupName\":\"영어\",\"keywords\":[\"영어\",\"english\"]}],\"device\":\"pc\",\"ages\":[\"1\",\"2\"],\"gender\":\"f\"}";

            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            // "GET" 이다 이거 찾은것도 오래걸렸다.
            con.setRequestMethod("GET");
            // 요청 헤더
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
            con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
            //con.setRequestProperty("Content-Type", "application/json");
            /*
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(body.getBytes());
            wr.flush();
            wr.close();
            */

            System.out.println("happy3");
            int responseCode = con.getResponseCode();
            BufferedReader br;
            System.out.println(responseCode);
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 오류 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            System.out.println("happy4");
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();
            //System.out.println(response.toString());
            JsonParser.jsonParser(response.toString());
            System.out.println("happy5");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
