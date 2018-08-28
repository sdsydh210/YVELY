package com.example.sdsyd.yvely;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.sdsyd.yvely.KakaoBlogSearch.FoodBlogSearchActivity;
import com.example.sdsyd.yvely.Kakaopay.BuyBalloonActivity;
import com.example.sdsyd.yvely.Token.MyTokenActivity;

public class UserInfoFragment extends Fragment {

    Button coinBtn, balloonBtn, blogSearchBtn; //토큰, 별풍선, AR게임, 예술작품 찍기 등의 버튼 변수 생성

    public UserInfoFragment(){

    } //프래그먼트 초기화를 위해 생성

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        coinBtn = view.findViewById(R.id.coinBtn);
        balloonBtn = view.findViewById(R.id.balloonBtn);
        blogSearchBtn = view.findViewById(R.id.blogSearchBtn);

        //내 지갑 정보와 토큰을 확인할 수 있는 화면으로 넘어가는 리스너
        coinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent coinActivity = new Intent(getActivity(), MyTokenActivity.class);
                startActivity(coinActivity);
            }
        });

        //별풍선 구매를 하는 화면으로 넘어가는 리스너(카카오페이)
        balloonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent balloonActivity = new Intent(getActivity(), BuyBalloonActivity.class);
                startActivity(balloonActivity);
            }
        });

        //맛집이나 블로그 검색을 할 수 있는 화면으로 이동(다음 검색)
        blogSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent blogSearchActivity = new Intent(getActivity(), FoodBlogSearchActivity.class);
                startActivity(blogSearchActivity);
            }
        });

        return view;
    }
}
