package com.example.sdsyd.yvely.Kakaopay;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.sdsyd.yvely.R;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import worker8.com.github.radiogroupplus.RadioGroupPlus;

public class BuyBalloonActivity extends AppCompatActivity {

    RadioGroupPlus mRadioGroupPlus; //라디오 버튼에 대한 그룹, 체크된 라디오버튼의 아이디값을 비교해서 결제하기 위함
    TextView balloonPointTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_balloon);

        mRadioGroupPlus = findViewById(R.id.radio_group_plus); //별풍선 갯수를 선택해서 결제하기 위한 레이아웃 할당
        balloonPointTv = findViewById(R.id.balloonPoint);

        GetBalloonData getBalloonData = new GetBalloonData();
        getBalloonData.execute();

    }//onCreate

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("SUNNY", "onResume 으로 들어오냐?");
        GetBalloonData getBalloonData = new GetBalloonData();
        getBalloonData.execute();
    }

    /*각각의 라디오버튼에대한 이벤트, 결제하기 버튼을
    * 누르게 되면 선택한 라디오버튼에대한 값을 설정해서 카카오페이 결제 시스템을 실행!*/
    public void onOrderClicked(View view) {
        if(R.id.balloon10Rb == mRadioGroupPlus.getCheckedRadioButtonId()){

           PushItem("별풍선", "10", "1000");

        }else if(R.id.balloon50Rb == mRadioGroupPlus.getCheckedRadioButtonId()){

            PushItem("별풍선", "50", "4900");

        }else if(R.id.balloon100Rb == mRadioGroupPlus.getCheckedRadioButtonId()){

            PushItem("별풍선", "100", "9500");

        }else if(R.id.balloon300Rb == mRadioGroupPlus.getCheckedRadioButtonId()){

            PushItem("별풍선", "300", "29000");
        }

    }//onOrderClicked

    //별풍선 이름, 갯수, 가격을 입력받아 웹뷰로 전달하기 위한 함수
    public void PushItem(String itemName, String quantity, String totalAmount){

        Intent webViewIntent = new Intent(BuyBalloonActivity.this, KakaopayWebview.class);
        webViewIntent.putExtra("itemName", itemName);
        webViewIntent.putExtra("quantity", quantity);
        webViewIntent.putExtra("totalAmount", totalAmount);
        startActivity(webViewIntent);

    }//PushItem

    //서버에서 별풍선 갯수를 가져오는 AsyncTask
    @SuppressLint("StaticFieldLeak")
    class GetBalloonData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String serverUrl = "http://13.125.210.22/GetBalloonPoint.php";
            Log.e("SUNNY", serverUrl);
            try {
                Log.e("SUNNY", "확인");
                //*서버 연결
                URL url = new URL(serverUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST"); //get or post 요청방식 선택
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  //타입 설정
                conn.setDoInput(true);  //서버 통신에서 입력 가능한 상태로 만듬

                conn.connect();

                //*안드로이드 -> 서버 파라미터값 전달
                OutputStream outs = conn.getOutputStream();  //output스트림 개방
                outs.flush();   //flush()는 현재 버퍼에 저장되어 있는 내용을 클라이언트로 전송하고 버퍼를 비운다
                outs.close();

                //*서버 -> 안드로이드 파라미터값 전달
                InputStream is;
                BufferedReader br;

                is = conn.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                br = new BufferedReader(isr);

                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    Log.e("SUNNY", sb.toString());
                }

                br.close();

                return sb.toString();


            } catch (Exception e) {

                return null;
            }
        }

        //가져온 별풍선 수를 텍스트뷰에 셋팅
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("SUNNY", result);
            if (result != null) {
                try {
                    JSONObject jb = new JSONObject(result);
                    JSONArray post = jb.getJSONArray("result");
                    Log.e("SUNNY", "post" + post);
                    for(int i = 0 ; i < post.length(); i++){
                        JSONObject c = post.getJSONObject(i);
                        String getPoint = c.getString("point");
                        balloonPointTv.setText(getPoint);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }//onPostExecute
    }//GetBalloonData

}//BuyBalloonActivity
