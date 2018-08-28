package com.example.sdsyd.yvely.Service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class MyService extends Service{

     private static final String HOST = "13.125.210.22";
     private final int PORT = 5002;
     static SocketChannel socketChannel;
     String data;

     private Messenger mClient = null;
     public static final int MSG_REGISTER_CLIENT = 1;
     public static final int MSG_SEND_TO_ACTIVITY = 2;
     public static final int MSG_SEND_TO_SERVICE = 3;

     /* 외부로 데이터를 전달하려면 바인더를 사용
    Binder 객체는 IBinder 상속 객체*/
    /*액티비티에서 bindservice()를 실행하면 호출된다, 서비스 객체를 리턴*/
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
        }


    @Override
    public void onCreate() {
        Log.d("debug","check! onCreate");
        super.onCreate();
    }


/*onstartcommand 부분에서 netty 서버와 연결*/
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("debug", "check onStartCommand");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketChannel = SocketChannel.open();
                    socketChannel.configureBlocking(true);
                    socketChannel.connect(new InetSocketAddress(HOST, PORT));
                    Log.d("SUNNY", String.valueOf(socketChannel));
                } catch (Exception ioe) {
                    ioe.printStackTrace();
                }
                receive();
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }/*onStartCommand*/


    public void receive() {
        while (true) {
            try {
                Log.d("SUNNY",  "======================3");

                ByteBuffer byteBuffer = ByteBuffer.allocate(256);
                //서버가 비정상적으로 종료했을 경우 IOException 발생
                int readByteCount = socketChannel.read(byteBuffer); //데이터받기
                Log.d("SUNNY", "readByteCount = "+ readByteCount + "");
                //서버가 정상적으로 Socket의 close()를 호출했을 경우
                if (readByteCount == -1) {
                    throw new IOException();
                }

                byteBuffer.flip(); // 문자열로 변환
                Charset charset = Charset.forName("UTF-8");
                data = charset.decode(byteBuffer).toString();
                Log.d("SUNNY", "msg :" + data);

                if(!data.equals("")){
                    Log.d("SUNNY",  "======================4" + data);
                    sendMsgToActivity(data);
                }

            } catch (IOException e) {
                Log.d("getMsg", e.getMessage() + "");
                try {
                    socketChannel.close();
                    break;
                } catch (IOException ee) {
                    ee.printStackTrace();
                }
            }
        }
    }


    private final Messenger mMessenger = new Messenger(new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClient = msg.replyTo;
                    Log.d("SUNNY",  "서비스에서 MSG_REGISTER_CLIENT 의 mClient " + mClient.toString());
                    break;
                case MSG_SEND_TO_SERVICE:
                    Log.d("SUNNY",  "서비스에서 네티로 메세지 전송 AsyncTask 실행 전" + msg.obj.toString());
                    new SendmsgTask().execute(msg.obj.toString());
                    break;
            }
            return false;
        }
    }));


    //서비스에서 방송자 및 시청자 액티비티로 채팅 메세지 전송
    private void sendMsgToActivity(String sendValue) {
        try {
            Log.d("SUNNY",  "======================5 서비스에서 채팅 메세지 전송" + sendValue);
            Bundle bundle = new Bundle();
            bundle.putString("msg",sendValue);
            Message msg = Message.obtain(null, MSG_SEND_TO_ACTIVITY);
            msg.setData(bundle);
            Log.d("SUNNY",  "======================6 서비스에서 채팅 메세지 전송" + sendValue);
            mClient.send(msg);      // msg 보내기
            Log.d("SUNNY",  "======================7 서비스에서 채팅 메세지 전송" + sendValue);
        } catch (RemoteException ignored) {

        } catch (Exception e){
            Log.d("SUNNY","e.getMessage()" + e.getMessage());
        }
    }

    //네티 서버로 메세지를 전송하는 부분
    private static class SendmsgTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            Log.d("SUNNY",strings[0]);
            try {
                socketChannel
                        .socket()
                        .getOutputStream()
                        .write(strings[0].getBytes("UTF-8")); // 서버로
                Log.d("SUNNY",strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("SUNNY", "catch 문으로 빠짐");
            }
            return null;
        }
    }/*SendmsgTask*/




/*소켓연결 끊기*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            socketChannel.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}/*MyService class*/
