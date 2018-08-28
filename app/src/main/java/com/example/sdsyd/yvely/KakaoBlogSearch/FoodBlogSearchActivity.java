package com.example.sdsyd.yvely.KakaoBlogSearch;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class FoodBlogSearchActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    FoodBlogAdapter adapter; //맛집 블로그 검색을 위한 어댑터
    ListView listView; //검색된 블로그 리스트뷰 만들기위해 생성
    EditText queryText; //검색어를 입력받기위해 EditText 생성
    Button searchBtn; //블로그 검색을 위한 버튼 생성
//    ProgressBar progressBar; //데이터 로딩중을 표시할 프로그레스바
    int page = 1; //페이징 변수, 초기값은 1이다.
    boolean mLockListView = false; //데이터 불러올때 중복안되게 하기위한 변수
    boolean lastItemVisibleFlag = false; //리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    String question;
    String blogPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_blog_search);

        listView = findViewById(R.id.blogListView);
        queryText = findViewById(R.id.queryText);
        searchBtn = findViewById(R.id.searchBtn);
//        progressBar = findViewById(R.id.progressBar);

        adapter = new FoodBlogAdapter();
        listView.setAdapter(adapter);

//        progressBar.setVisibility(View.GONE);

        listView.setOnScrollListener(this);


        //검색 버튼을 클릭했을 때 다음 블로그의 결과값을 가져와서 리스트를 보여준다.
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //EditText가 공백일 때와 아닐 때의 처리할 내용을 분리
                if(queryText.getText().toString().length() == 0){
                    Toast.makeText(getApplicationContext(),"검색어를 입력해주세요!!", Toast.LENGTH_SHORT).show();
                }else {
                    adapter.foodBlogListviewitems.clear();

                    question = queryText.getText().toString();
                    blogPage = String.valueOf(page);
                    KakaoBlogSearch kbs = new KakaoBlogSearch();
                    kbs.execute(question,blogPage);
                }

            }//onClick
        });//setOnClickListener

        //리스트 아이템 하나를 클릭했을 때 해당 블로그로 이동
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //hyper link 걸어서 클릭시 해당 링크로 이동
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri u = Uri.parse(adapter.foodBlogListviewitems.get(position).getBlogUrl());
                i.setData(u);
                startActivity(i);

            }//onItemClick
        });//setOnItemClickListener

    }//onCreate

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        // 1. OnScrollListener.SCROLL_STATE_IDLE : 스크롤이 이동하지 않을때의 이벤트(즉 스크롤이 멈추었을때).
        // 2. lastItemVisibleFlag : 리스트뷰의 마지막 셀의 끝에 스크롤이 이동했을때.
        // 3. mLockListView == false : 데이터 리스트에 다음 데이터를 불러오는 작업이 끝났을때.
        // 1, 2, 3 모두가 true일때 다음 데이터를 불러온다.
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && !mLockListView) {
            // 화면이 바닦에 닿을때 처리
            // 다음 데이터를 불러온다. (AsyncTask를 이용해서 블로그 데이터 가져오기)
            mLockListView = true;
            page++;
            blogPage = String.valueOf(page);
            KakaoBlogSearch kbs = new KakaoBlogSearch();
            kbs.execute(question,blogPage);

        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            // firstVisibleItem : 화면에 보이는 첫번째 리스트의 아이템 번호.
            // visibleItemCount : 화면에 보이는 리스트 아이템의 갯수
            // totalItemCount : 리스트 전체의 총 갯수
            // 리스트의 갯수가 0개 이상이고, 화면에 보이는 맨 하단까지의 아이템 갯수가 총 갯수보다 크거나 같을때.. 즉 리스트의 끝일때. true
            lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

    @SuppressLint("StaticFieldLeak")
    class KakaoBlogSearch extends AsyncTask<String, Void, String> {

        ProgressDialog asyncDialog = new ProgressDialog(FoodBlogSearchActivity.this);

        @Override
        protected void onPreExecute() {

            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("콘텐츠 확인 중 입니다...");

            // show dialog
            asyncDialog.show();
            super.onPreExecute();

        }//onPreExecute

        @Override
        protected String doInBackground(String... params) {

            String question = params[0];
            String blogPage = params[1];

            String serverUrl = "http://13.125.210.22/kakaoblogsearch/KakaoBlogSearch.php";
            Log.e("blog", serverUrl);

            String postParam = "question=" + question + "&blogPage=" + blogPage;
            Log.e("postparam", postParam);

            try {
                Log.e("blog", "확인");
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
                    Log.e("blog", sb.toString());
                }

                br.close();

                return sb.toString();

            } catch (Exception e) {
                return null;
            }

        }//doInBackground

        //블로그 데이터를 가져오는 과정
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.e("blog", result);
            if (result != null) {
                try {

                    JSONObject jb = new JSONObject(result);
                    Log.e("blog", String.valueOf(jb));

                    JSONArray data = jb.getJSONArray("documents");
                    int size = data.length();
                    Log.e("blog", String.valueOf(size));

                    String blogImage, blogUrl, blogDateTime, blogTitle, blogContent;

                    for(int i=0; i < size; i++){

                        blogImage = data.getJSONObject(i).getString("thumbnail");
                        blogUrl = data.getJSONObject(i).getString("url");
                        blogDateTime = data.getJSONObject(i).getString("datetime");
                        blogTitle = data.getJSONObject(i).getString("title");
                        blogContent = data.getJSONObject(i).getString("contents");

                        //시간 추출 하는 과정
                        blogDateTime = blogDateTime.substring(0,10);
                        //블로그 제목과 내용에 있는 html 태그 제거 과정
                        blogTitle = blogTitle.replaceAll("<b>","");
                        blogTitle = blogTitle.replaceAll("</b>","");
                        blogTitle = blogTitle.replaceAll("&amp;","");

                        blogContent = blogContent.replaceAll("<b>","");
                        blogContent = blogContent.replaceAll("</b>","");
                        blogContent = blogContent.replaceAll("&amp;","");

                        Log.e("blog", "blogImage = " + blogImage);
                        Log.e("blog", "blogUrl = " + blogUrl);
                        Log.e("blog", "blogDateTime = " + blogDateTime);
                        Log.e("blog", "blogTitle = " + blogTitle);
                        Log.e("blog", "blogContent = " + blogContent);

                        if(!blogImage.equals("")){
                            adapter.addItem(blogImage, blogUrl, blogDateTime, blogTitle, blogContent);
                        }
                    }//for문 끝
                    adapter.notifyDataSetChanged();
                    mLockListView = false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            //프로그레스 다이얼로그 닫기!
            asyncDialog.dismiss();

        }//onPostExecute
    }//KakaopayReady

}//FoodBlogSearchActivity
