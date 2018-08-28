package com.example.sdsyd.yvely.Broadcast;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sdsyd.yvely.R;
import com.example.sdsyd.yvely.Broadcast.LivePlayer.LiveVideoPlayerActivity;
import com.example.sdsyd.yvely.Service.MyService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.BIND_AUTO_CREATE;
import static com.example.sdsyd.yvely.Broadcast.LiveVideoBroadcasterActivity.TYPE_LIVE_LIST_SAVE;

public class ListLiveBroadcastingFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeLayout; //새로운 라이브 방송이 생겼을 때 갱신주기위해 생성
    EditText titleText = null; //방송을 시작할 때 방송 제목을 입력받기위해 생성

    LiveListAdapter adapter; //라이브 방송 관련 어뎁터
    ListView listView; //라이브 방송 리스트뷰 만들기위해 생성

    public static final String TYPE_LIVE_LIST_GET = "LIVE_LIST_GET"; //서버로부터 라이브 목록을 받아오기위한 변수

    //썸네일 이미지 불러올 주소, 서버에 저장된 썸네일 불러오기위한 변수
    public static final String thumbnailUrl = "http://13.125.210.22/thumbnail/";

    boolean isBound = false; //서비스 중인지 확인하기 위해 선언
    private Messenger serviceMessenger = null; //서비스 연결을 위한 메신저

    //서비스와 연결을 하는 부분, 연결을 통해 메세지를 주고 받고 하기 위함
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {

            ////////////////////////////////////////////////////////////채팅을 위해 서비스 바인딩하는 부분
            serviceMessenger = new Messenger(service);
            try{
                Message msg = Message.obtain(null, MyService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                serviceMessenger.send(msg);
                Log.d("SUNNY","onServiceConnected 에서 서비스에 메세지 보내는 부분" + msg);
            }catch (RemoteException ignored) {

            }
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    //서비스로부터 메세지를 받는 부분 즉, 서버에서 메세지를 받는다.
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d("SUNNY","FRAGMENT 에서 받는 부분" + msg);
            switch (msg.what) {
                case MyService.MSG_SEND_TO_ACTIVITY:
                    Log.d("SUNNY","FRAGMENT 에서 받는 부분" + msg);
                    String receiveMsg = msg.getData().getString("msg");
                    Log.d("SUNNY","FRAGMENT 에서 받는 부분" + receiveMsg);

                    try {
                        JSONArray liveList = new JSONArray(receiveMsg);

                        for(int i = 0; i < liveList.length(); i++){
                            JSONObject json = liveList.getJSONObject(i);
                            String getType = json.get("MSG_TYPE").toString();

                            if(getType.equals(TYPE_LIVE_LIST_SAVE)) {
                                String getId = json.get("broadcasterId").toString();
                                String getTitle = json.get("broadcastingTitle").toString();
                                String getTime = json.get("broadcastingTime").toString();

                                adapter.addItem(thumbnailUrl + getTime + getId + ".png", getId, getTitle, getTime);
                                Log.d("SUNNY","썸네일 url : " + thumbnailUrl + getTime + getId + ".png");
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        break;
                    }
            }
            return false;
        }
    }));

    //서비스로 메세지를 보내는 부분 즉, 서버에 메세지를 보내는 부분이다.
    private void sendMessageToService(String str) {
        if (isBound) {
            if (serviceMessenger != null) {
                try {
                    Log.d("SUNNY",  "라이브 방송목록 프레그먼트 " + str);
                    Message msg = Message.obtain(null, MyService.MSG_SEND_TO_SERVICE, str);
                    msg.replyTo = mMessenger;
                    serviceMessenger.send(msg);
                } catch (RemoteException ignored) {

                }
            }
        }
    }

    //프래그먼트 refresh 기능, 서버에서 라이브 목록 받아오깅!
    private void refresh() throws JSONException {

        adapter.liveListListviewitems.clear();

        JSONObject json = new JSONObject();

        json.put("MSG_TYPE", TYPE_LIVE_LIST_GET);

        sendMessageToService(json.toString());

        swipeLayout.setRefreshing(false);
    }

    //방송을 시작하기 전에 방송 제목을 입력받기 위한 알림창 생성 함수
    void show()
    {
        titleText = new EditText(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("방송 제목을 입력하세요");
        builder.setView(titleText);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(),titleText.getText().toString() ,Toast.LENGTH_LONG).show();
                        Intent i = new Intent(getActivity(), LiveVideoBroadcasterActivity.class);
                        i.putExtra("broadcastingTitle", titleText.getText().toString());
                        startActivity(i);
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }


/*빈 생성자를 생성해서 crash 방지*/
    public ListLiveBroadcastingFragment(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_live_broadcasting, container, false);

        swipeLayout = view.findViewById(R.id.swipeRefresh); //스와이프 생성, 라이브 방송 리스트 갱신
        swipeLayout.setOnRefreshListener(this); //스와이프를 했을 때 발생할 이벤트 리스터 생성

        //리스트뷰 어뎁터 연결 부분
        adapter = new LiveListAdapter();
        listView = view.findViewById(R.id.liveListListview);
        listView.setAdapter(adapter);

        //서비스 바인딩 하는 부분
        Intent serviceIntent = new Intent(getActivity(), MyService.class);
        getActivity().bindService(serviceIntent,mConnection,BIND_AUTO_CREATE);
        isBound = true;

        Log.d("SUNNY","onCreateView 에서 바인드서비스 부분" + getActivity().bindService(serviceIntent,mConnection,BIND_AUTO_CREATE));
        Log.d("SUNNY","onCreateView 에서 isBound " + isBound);

        //라이브 방송 리스트 중 하나를 클릭했을 때 방송을 시작하는 화면으로 이동하는 이벤트 리스너
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), LiveVideoPlayerActivity.class);
                i.putExtra("broadcasterId", adapter.liveListListviewitems.get(position).getBroadcasterId());
                i.putExtra("broadcastingStartTime", adapter.liveListListviewitems.get(position).getBroadcastingStartTime());
                startActivity(i);
            }
        });

        //방송 시작 버튼을 만들고 클릭했을 때 일어나는 이벤트를 정의
        // 방송 제목을 입력할 수 있게 show()함수 호출
        TextView start;
        start = view.findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });


        return view;

    }/*onCreateView*/

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unbindService(mConnection);
    }


    @Override
    public void onRefresh() {
        try {
            refresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}//ListLiveBroadcastingFragment
