package com.example.sdsyd.yvely.Broadcast;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.airbnb.lottie.LottieAnimationView;
import com.example.sdsyd.yvely.R;
import com.example.sdsyd.yvely.Service.MyService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import io.antmedia.android.broadcaster.ILiveVideoBroadcaster;
import io.antmedia.android.broadcaster.LiveVideoBroadcaster;
import io.antmedia.android.broadcaster.utils.Resolution;

import static com.example.sdsyd.yvely.Broadcast.ListLiveBroadcastingFragment.thumbnailUrl;
import static com.example.sdsyd.yvely.Broadcast.ListVodFragment.vodUrl;
import static com.example.sdsyd.yvely.MainActivity.RTMP_BASE_URL;

public class LiveVideoBroadcasterActivity extends AppCompatActivity {


    private ViewGroup mRootView;
    boolean mIsRecording = false;
//    private EditText mStreamNameEditText;
    private Timer mTimer;
    private long mElapsedTime;
    public TimerHandler mTimerHandler;
    private ImageButton mSettingsButton;
    private CameraResolutionsFragment mCameraResolutionsDialog;
    private Intent mLiveVideoBroadcasterServiceIntent;
    private TextView mStreamLiveStatus;
    private GLSurfaceView mGLView;
    private ILiveVideoBroadcaster mLiveVideoBroadcaster;
    private Button mBroadcastControlButton;

    //내가 만들어가는 부분
    LiveChatAdapter adapter; //라이브 방송에서 채팅관련 어뎁터
    ListView listView; //라이브 방송에서 채팅 리스트뷰 변수 선언
    EditText messageText; //채팅 메세지를 보내기 위해 입력받는 변수
    Button messageSendButton; //채팅 메세지를 보내는 버튼 생성
    private Messenger serviceMessenger = null; //서비스 연결을 위한 변수
    boolean isBound = false; //서비스 중인지 확인하기 위해 선언
    long start; //방송 시작 시간을 정의, 썸네일, vod 를 저장할 때 시간과 이름으로 생성하기 위한 변수
    Intent broadcastingTitleIntent; //방송 제목을 받아오는 인텐트 변수
    LottieAnimationView animationView;

    public static final String TYPE_LIVE_LIST_SAVE = "LIVE_LIST_SAVE";
    public static final String TYPE_CHAT_MSG = "CHAT_MSG";

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LiveVideoBroadcaster.LocalBinder binder = (LiveVideoBroadcaster.LocalBinder) service;
            if (mLiveVideoBroadcaster == null) {
                mLiveVideoBroadcaster = binder.getService();
                mLiveVideoBroadcaster.init(LiveVideoBroadcasterActivity.this, mGLView);
                mLiveVideoBroadcaster.setAdaptiveStreaming(true);
            }
            mLiveVideoBroadcaster.openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mLiveVideoBroadcaster = null;
        }
    };

    /////////////////////////////////////내 채팅을 위한 서비스 따로 구현
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
            try{
                Message msg = Message.obtain(null, MyService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                serviceMessenger.send(msg);
            }catch (RemoteException ignored){

            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };



    //서비스로부터 메시지를 받는 부분
    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Log.d("SUNNY","======================1 서비스에서 메세지를 받는 부분");
            switch (msg.what) {

                case MyService.MSG_SEND_TO_ACTIVITY:
                    Log.d("SUNNY","======================2 서비스에서 메세지를 받는 부분");
                    String receiveMsg = msg.getData().getString("msg");
                    Log.d("SUNNY","======================3 서비스에서 메세지를 받는 부분");

                    try {

                        JSONObject json = new JSONObject(receiveMsg);

                        if(json.get("MSG_TYPE").toString().equals(TYPE_CHAT_MSG) && json.get("balloonType").toString().equals("1")) {
                            String getId = json.get("id").toString();
                            String getMsg = json.get("message").toString();

                            adapter.addItem(getId + " : ","별풍선 " + getMsg + "개를 선물했습니다~!");
                            adapter.notifyDataSetChanged();

                            animationView.playAnimation(); //별풍선 애니메이션 플레이

                            //별풍선 보내지 않았을 경우 처리하는 부분
                        } else {
                            String getId = json.get("id").toString();
                            String getMsg = json.get("message").toString();

                            adapter.addItem(getId + " : ", getMsg);
                            adapter.notifyDataSetChanged();
                        }
//                        if(json.get("MSG_TYPE").toString().equals(TYPE_CHAT_MSG)) {
//                            String getId = json.get("id").toString();
//                            String getMsg = json.get("message").toString();
//
//                            adapter.addItem(getId + " : ", getMsg);
//                            adapter.notifyDataSetChanged();
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("SUNNY","======================4 서비스에서 메세지를 받는 부분");
                    break;

            }
            return false;
        }
    }));

    private void sendMessageToService(String str) {
        if (isBound) {
            if (serviceMessenger != null) {
                try {
                    Log.d("SUNNY",  "======================방송자 화면에서 서비스로 메세지 전송" + str);
                    Message msg = Message.obtain(null, MyService.MSG_SEND_TO_SERVICE, str);
                    msg.replyTo = mMessenger;
                    serviceMessenger.send(msg);
                } catch (RemoteException e) {
                }
            }
        }
    }

    void show()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("방송 종료");
        builder.setMessage("방송을 저장하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        PutVodData putVod = new PutVodData();
                        SharedPreferences member = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                        String broadcastingTitle = broadcastingTitleIntent.getStringExtra("broadcastingTitle");
                        String userId = member.getString("inputId", null);

                        putVod.execute(userId, broadcastingTitle, String.valueOf(start));

                        finish();
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

    @SuppressLint("StaticFieldLeak")
    class PutVodData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String id = params[0];
            String title = params[1];
            String startTime = params[2];

            String serverUrl = "http://13.125.210.22/SaveVod.php";
            String postparam = "id=" + id + "&title=" + title + "&thumbnailUrl=" + thumbnailUrl + startTime + id + ".png" + "&vodUrl=" + vodUrl + startTime + id + ".flv";
            Log.e("SUNNY" , postparam);
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
                outs.write(postparam.getBytes("UTF-8"));
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
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //binding on resume not to having leaked service connection
        mLiveVideoBroadcasterServiceIntent = new Intent(this, LiveVideoBroadcaster.class);
        //this makes service do its job until done
        startService(mLiveVideoBroadcasterServiceIntent);

        setContentView(R.layout.activity_live_video_broadcaster);

        mTimerHandler = new TimerHandler();
//        mStreamNameEditText = findViewById(R.id.stream_name_edit_text);

        mRootView = findViewById(R.id.root_layout);
        mSettingsButton = findViewById(R.id.settings_button);
        mStreamLiveStatus = findViewById(R.id.stream_live_status);

        mBroadcastControlButton = (Button) findViewById(R.id.toggle_broadcasting); //방송 시작 버튼
        messageSendButton = findViewById(R.id.messageSend); //메세지 전송 버튼
        messageText = findViewById(R.id.messageText); //메세지 텍스트 뷰
        animationView = findViewById(R.id.animation_view); //별풍선 애니메이션 뷰

        /*방송 시작 버튼 누르기 전에는 보이지 않다가
        * 시작을 하게 되면 보이게 하기 위해 방송 시작 전은 invisible 설정*/
        messageText.setVisibility(View.INVISIBLE);
        messageSendButton.setVisibility(View.INVISIBLE);

        // Configure the GLSurfaceView.  This will start the Renderer thread, with an
        // appropriate EGL activity.
        mGLView = (GLSurfaceView) findViewById(R.id.cameraPreview_surfaceView);
        if (mGLView != null) {
            mGLView.setEGLContextClientVersion(2);     // select GLES 2.0
        }

        broadcastingTitleIntent = getIntent();
        /*리스트뷰 어뎁터 연결*/
        adapter = new LiveChatAdapter();
        listView = findViewById(R.id.liveChatListview);
        listView.setAdapter(adapter);

        Intent serviceIntent = new Intent(LiveVideoBroadcasterActivity.this, MyService.class);
        bindService(serviceIntent,conn,BIND_AUTO_CREATE);
        isBound = true;

        //방송 중 채팅 메세지 전송하는 부분
        messageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!messageText.getText().toString().trim().equals("")){

                    JSONObject json = new JSONObject();

                    SharedPreferences member = getSharedPreferences("auto", Activity.MODE_PRIVATE);

                    String userId = member.getString("inputId", null);
                    String sendMessage = messageText.getText().toString();

                    try {

                        json.put("MSG_TYPE", TYPE_CHAT_MSG);
                        json.put("id", userId);
                        json.put("broadcasterId", userId);
                        json.put("message", sendMessage);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sendMessageToService(json.toString());
                    Log.d("SUNNY",  "======================방송자 화면에서 전송 버튼 클릭했을 때" + sendMessage);
                    adapter.addItem(userId + " : ", sendMessage);
                    adapter.notifyDataSetChanged();

                    messageText.setText("");
                }
            }
        });

        //별풍선 애니메이션 끝나면 사라지게 하기위해 이벤트 리스너를 설정
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }/*onCreate*/

    public void changeCamera(View v) {
        if (mLiveVideoBroadcaster != null) {
            mLiveVideoBroadcaster.changeCamera();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //this lets activity bind
        bindService(mLiveVideoBroadcasterServiceIntent, mConnection, 0);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LiveVideoBroadcaster.PERMISSIONS_REQUEST: {
                if (mLiveVideoBroadcaster.isPermissionGranted()) {
                    mLiveVideoBroadcaster.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
                }
                else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.CAMERA) ||
                            ActivityCompat.shouldShowRequestPermissionRationale(this,
                                    Manifest.permission.RECORD_AUDIO) ) {
                        mLiveVideoBroadcaster.requestPermission();
                    }
                    else {
                        new AlertDialog.Builder(LiveVideoBroadcasterActivity.this)
                                .setTitle(R.string.permission)
                                .setMessage(getString(R.string.app_doesnot_work_without_permissions))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        try {
                                            //Open the specific App Info page:
                                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                                            startActivity(intent);

                                        } catch ( ActivityNotFoundException e ) {
                                            //e.printStackTrace();

                                            //Open the generic Apps page:
                                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                                            startActivity(intent);

                                        }
                                    }
                                })
                                .show();
                    }
                }
                return;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //hide dialog if visible not to create leaked window exception
        if (mCameraResolutionsDialog != null && mCameraResolutionsDialog.isVisible()) {
            mCameraResolutionsDialog.dismiss();
        }
        mLiveVideoBroadcaster.pause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        unbindService(conn);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mLiveVideoBroadcaster.setDisplayOrientation();
        }

    }

    public void showSetResolutionDialog(View v) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragmentDialog = getSupportFragmentManager().findFragmentByTag("dialog");
        if (fragmentDialog != null) {

            ft.remove(fragmentDialog);
        }

        ArrayList<Resolution> sizeList = mLiveVideoBroadcaster.getPreviewSizeList();


        if (sizeList != null && sizeList.size() > 0) {
            mCameraResolutionsDialog = new CameraResolutionsFragment();

            mCameraResolutionsDialog.setCameraResolutions(sizeList, mLiveVideoBroadcaster.getPreviewSize());
            mCameraResolutionsDialog.show(ft, "resolutiton_dialog");
        }
        else {
            Snackbar.make(mRootView, "No resolution available",Snackbar.LENGTH_LONG).show();
        }

    }

    @SuppressLint("StaticFieldLeak")
    public void toggleBroadcasting(View v) {
        if (!mIsRecording)
        {
            if (mLiveVideoBroadcaster != null) {
                if (!mLiveVideoBroadcaster.isConnected()) {
                    /*ListLiveBroadcastingFragment 클래스에서 방송시작 버튼 누르고 방송제목을 받오는 부분*/
//                    Intent broadcastingTitleIntent = getIntent();
                    String broadcastingTitle = broadcastingTitleIntent.getStringExtra("broadcastingTitle");

                    //스트림 네임 정의, 시간과 유저아이디로 구분
                    start = System.currentTimeMillis();
                    //유저 아이디 가져오기
                    SharedPreferences member = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                    String userId = member.getString("inputId", null);

                    new AsyncTask<String, String, Boolean>() {
                        ContentLoadingProgressBar
                                progressBar;
                        @Override
                        protected void onPreExecute() {
                            progressBar = new ContentLoadingProgressBar(LiveVideoBroadcasterActivity.this);
                            progressBar.show();
                        }

                        @Override
                        protected Boolean doInBackground(String... url) {
                            return mLiveVideoBroadcaster.startBroadcasting(url[0]);

                        }

                        @Override
                        protected void onPostExecute(Boolean result) {
                            progressBar.hide();
                            mIsRecording = result;
                            if (result) {
                                mStreamLiveStatus.setVisibility(View.VISIBLE);

                                mBroadcastControlButton.setText(R.string.stop_broadcasting);
                                mSettingsButton.setVisibility(View.GONE);
                                startTimer();//start the recording duration

                            }
                            else {
                                Snackbar.make(mRootView, R.string.stream_not_started, Snackbar.LENGTH_LONG).show();

                                triggerStopRecording();
                            }
                        }
                    }.execute(RTMP_BASE_URL + start + userId);

                    messageSendButton.setVisibility(View.VISIBLE);
                    messageText.setVisibility(View.VISIBLE);

                    JSONObject json = new JSONObject();

                    try {
                        json.put("MSG_TYPE", TYPE_LIVE_LIST_SAVE);
                        json.put("broadcastingTime", start);
                        json.put("broadcasterId", userId);
                        json.put("broadcastingTitle", broadcastingTitle);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    sendMessageToService(json.toString());



                }
                else {
                    Snackbar.make(mRootView, R.string.streaming_not_finished, Snackbar.LENGTH_LONG).show();
                }
            }
            else {
                Snackbar.make(mRootView, R.string.oopps_shouldnt_happen, Snackbar.LENGTH_LONG).show();
            }
        }
        else
        {
            triggerStopRecording();
        }

    }


    public void triggerStopRecording() {
        if (mIsRecording) {
            mBroadcastControlButton.setText("방송 시작");

            mStreamLiveStatus.setVisibility(View.GONE);
            mStreamLiveStatus.setText(R.string.live_indicator);
            mSettingsButton.setVisibility(View.VISIBLE);

            stopTimer();
            mLiveVideoBroadcaster.stopBroadcasting();

            //방송 종료시 방송 정보를 디비에 저장
            show();
        }

        mIsRecording = false;
    }

    //This method starts a mTimer and updates the textview to show elapsed time for recording
    public void startTimer() {

        if(mTimer == null) {
            mTimer = new Timer();
        }

        mElapsedTime = 0;
        mTimer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                mElapsedTime += 1; //increase every sec
                mTimerHandler.obtainMessage(TimerHandler.INCREASE_TIMER).sendToTarget();

                if (mLiveVideoBroadcaster == null || !mLiveVideoBroadcaster.isConnected()) {
                    mTimerHandler.obtainMessage(TimerHandler.CONNECTION_LOST).sendToTarget();
                }
            }
        }, 0, 1000);
    }


    public void stopTimer()
    {
        if (mTimer != null) {
            this.mTimer.cancel();
        }
        this.mTimer = null;
        this.mElapsedTime = 0;
    }

    public void setResolution(Resolution size) {
        mLiveVideoBroadcaster.setResolution(size);
    }

    private class TimerHandler extends Handler {
        static final int CONNECTION_LOST = 2;
        static final int INCREASE_TIMER = 1;

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INCREASE_TIMER:
                    mStreamLiveStatus.setText(getString(R.string.live_indicator) + " - " + getDurationString((int) mElapsedTime));
                    break;
                case CONNECTION_LOST:
                    triggerStopRecording();
                    new AlertDialog.Builder(LiveVideoBroadcasterActivity.this)
                            .setMessage(R.string.broadcast_connection_lost)
                            .setPositiveButton(android.R.string.yes, null)
                            .show();

                    break;
            }
        }
    }

    public static String getDurationString(int seconds) {

        if(seconds < 0 || seconds > 2000000)//there is an codec problem and duration is not set correctly,so display meaningfull string
            seconds = 0;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if(hours == 0)
            return twoDigitString(minutes) + " : " + twoDigitString(seconds);
        else
            return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    public static String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }
}
