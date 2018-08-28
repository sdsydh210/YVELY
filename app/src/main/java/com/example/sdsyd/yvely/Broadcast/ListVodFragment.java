package com.example.sdsyd.yvely.Broadcast;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.sdsyd.yvely.Broadcast.LivePlayer.LiveVideoPlayerActivity;
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

public class ListVodFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    public static final String vodUrl = "http://13.125.210.22/vod/"; //서버에 vod 저장 경로, vod 목록 불러오기 위해 생성

    SwipeRefreshLayout swipeLayout; //vod 목록을 갱신을 위한 스와이프 변수 생성

    //vod 목록 리스트뷰와 어뎁터
    VodAdapter adapter;
    ListView listView;


    public ListVodFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_vod,container,false);


        swipeLayout = view.findViewById(R.id.vodSwipeRefresh); //스와이프 레이아웃 생성, vod 갱신 하기 위해
        swipeLayout.setOnRefreshListener(this); //스와이프 리스너 생성

        /*리스트뷰 어뎁터 연결*/
        adapter = new VodAdapter();
        listView = view.findViewById(R.id.vodListview);
        listView.setAdapter(adapter);

        //vod 방송 목록 중에서 하나를 선택했을 때 방송을 볼 수 있는 화면으로 이동하는 이벤트
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), LiveVideoPlayerActivity.class);
                i.putExtra("vodUrl", adapter.vodListviewitems.get(position).getVodUrl());
                startActivity(i);
            }
        });

        //처음에 vod 목록을 클리어하고 새로 불러오는 부분, 다른 화면에 갔다가 왔을 때 새로 생긴 vod가
        //있을 수 있기 때문에 클리어 해주고 서버에서 vod목록을 불러온다.
        adapter.vodListviewitems.clear();

        GetVodData getVodData = new GetVodData();
        getVodData.execute();

        return view;
    }

    //vod 리스트를 스와이프로 갱신하는 함수
    @Override
    public void onRefresh() {

        adapter.vodListviewitems.clear();

        GetVodData getVodData = new GetVodData();
        getVodData.execute();

        swipeLayout.setRefreshing(false);

    }

    //서버에서 vod 목록을 불러오는 AsyncTask
     @SuppressLint("StaticFieldLeak")
     class GetVodData extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {

            String serverUrl = "http://13.125.210.22/GetVod.php";
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
        //가져온 작성글 데이터들을 리스트뷰에 추가 시켜준다
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
                        String getId = c.getString("id");
                        String getTitle = c.getString("title");
                        String getThumbnailUrl = c.getString("thumbnailUrl");
                        String getVodUrl = c.getString("vodUrl");

                        Log.e("SUNNY", "getId" + getId);
                        Log.e("SUNNY", "getTitle" + getTitle);
                        Log.e("SUNNY", "getThumbnailUrl" + getThumbnailUrl);
                        Log.e("SUNNY", "getVodUrl" + getVodUrl);

                        adapter.addItem(getThumbnailUrl, getId, getTitle, getVodUrl);
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }//onPostExecute

    }//getVodData
}
