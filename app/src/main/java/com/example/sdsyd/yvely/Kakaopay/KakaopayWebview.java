package com.example.sdsyd.yvely.Kakaopay;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.sdsyd.yvely.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

public class KakaopayWebview extends AppCompatActivity {

    WebView webView; //카카오페이 실행할 웹뷰 변수
    String tid; //tid값을 결제승인 url로 넘길 때 post로 전달하기 위해 생성

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakaopay_webview);

        webView = findViewById(R.id.webView); //카카오페이 웹뷰를 띄워주기 위해 레이아웃 할당

        webView.getSettings().setJavaScriptEnabled(true); //웹뷰에서 자바스크립트 실행가능
        webView.setWebViewClient(new MyWebViewClient()); //웹뷰에 MyWebViewClient를 연결

        //별풍선 갯수와 가격을 BuyBalloonActivity에서 받아와 셋팅
        Intent getItemData = getIntent();
        String itemName = getItemData.getStringExtra("itemName");
        String quantity = getItemData.getStringExtra("quantity");
        String totalAmount = getItemData.getStringExtra("totalAmount");

        //카카오페이 결제준비 Async 실행
        KakaopayReady kr = new KakaopayReady();
        kr.execute(itemName, quantity, totalAmount);


    }

    //웹뷰 클래스 정의,보내기 = POST=posturl & 로드 = GET=loadurl
    //posturl은 post메소드를 이용해서 url 로드
    //loadurl은 get메소드를 이용해서 url 로드
    public class MyWebViewClient extends WebViewClient {

        static final String INTENT_URI_START = "intent:";

        //webview에서 페이지 이동이 가능하게 하는 메소드
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String uri) {
            if (uri.toLowerCase().startsWith(INTENT_URI_START)) {
                Intent parsedIntent = null;
                try {
                    parsedIntent = Intent.parseUri(uri, 0);
                    startActivity(parsedIntent);
                } catch (ActivityNotFoundException | URISyntaxException ignored) {

                }

            } else {
                try {
                    Uri parseUri = Uri.parse(uri);
                    String authority = parseUri.getAuthority(); //13.125.210.22 와 같이 서버주소를 셋팅
                    String path = parseUri.getPath(); // /kakaopay/KakaopayApproval.php 와 같이 php파일이 있는 서버경로 셋팅
                    String query = parseUri.getQuery(); //pg_token=15fe6256ee387e9a0c40 쿼리문 셋팅
                    String[] pgToken = query.split("\\=",2); //pg_token값을 "=" 기준으로 스플릿
                    Log.e("SUNNY", authority);
                    Log.e("SUNNY", query);

                    String postTid = "tid=" + URLEncoder.encode(tid, "UTF-8") + "&pg_token=" + URLEncoder.encode(pgToken[1], "UTF-8");
                    view.postUrl("http://"+authority+path, postTid.getBytes());
                    Log.e("SUNNY", postTid);
                    Log.e("SUNNY", uri);
                    Log.e("SUNNY", "http://"+authority+path);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }//shouldOverrideUrlLoading
    }//MyWebViewClient


    @SuppressLint("StaticFieldLeak")
    class KakaopayReady extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String itemName = params[0];
            String quantity = params[1];
            String totalAmount = params[2];

            String serverUrl = "http://13.125.210.22/kakaopay/KakaopayReady.php";
            Log.e("SUNNY", serverUrl);

            String postParam = "itemName=" + itemName + "&quantity=" + quantity + "&totalAmount=" + totalAmount;
            Log.e("postparam", postParam);

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
                outs.write(postParam.getBytes("UTF-8"));
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

        }//doInBackground

        //결제준비 요청에 대한 응답 데이터들을 가져온다, 웹뷰에 띄워주기 위함
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("SUNNY", result);
            if (result != null) {
                try {

                    JSONObject jb = new JSONObject(result);
                    Log.e("SUNNY", "jb" + jb);

                    String next_redirect_mobile_url = jb.getString("next_redirect_mobile_url");
                    tid = jb.getString("tid");

                    Log.e("SUNNY", "next_redirect_mobile_url" + next_redirect_mobile_url);
                    Log.e("SUNNY", "tid" + tid);

                    //webView url 셋팅
                    webView.loadUrl(next_redirect_mobile_url);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }//onPostExecute
    }//KakaopayReady

}
