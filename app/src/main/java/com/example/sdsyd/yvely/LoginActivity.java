package com.example.sdsyd.yvely;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    EditText idText;  //ID를 입력하는 뷰
    EditText pwText;  //PW를 입력하는
    TextView joinText; //회원가입 화면으로 넘어가기 위해
    Button loginButton; //로그인과 동시에 Sharedpreference에 저장해서 자동로그인 구현하기 위함
    String loginId,loginPw;
    CheckBox checkBox;
    boolean loginCk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        idText = findViewById(R.id.idText);
        pwText = findViewById(R.id.pwText);
        joinText = findViewById(R.id.joinText);
        loginButton = findViewById(R.id.loginButton);
        checkBox = findViewById(R.id.checkBox);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = idText.getText().toString();
                String pw = pwText.getText().toString();
                boolean ck = checkBox.isChecked();

                SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);
                SharedPreferences.Editor autoLogin = auto.edit();

                autoLogin.putString("inputId", id);
                autoLogin.putString("inputPw", pw);
                autoLogin.putBoolean("inputCk", ck);

                autoLogin.apply();

                Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(loginIntent);
                finish();

            }
        });


        //자동로그인 부분
        SharedPreferences auto = getSharedPreferences("auto", Activity.MODE_PRIVATE);

        loginId = auto.getString("inputId",null);
        loginPw = auto.getString("inputPw",null);
        loginCk = auto.getBoolean("inputCk", false);

        if(loginId !=null && loginPw != null) {
            if(loginCk) {
                Intent autoIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(autoIntent);
                finish();
            }
        }



    }
}
