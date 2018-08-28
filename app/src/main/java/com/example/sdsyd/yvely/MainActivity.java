package com.example.sdsyd.yvely;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.sdsyd.yvely.Broadcast.TabPagerAdapter;
import com.example.sdsyd.yvely.Service.MyService;



public class MainActivity extends AppCompatActivity {

    /**
     * PLEASE WRITE RTMP BASE URL of the your RTMP SERVER.13.125.210.22
     */
    public static final String RTMP_BASE_URL = "rtmp://13.125.210.22:1935/myapp/";
    public static final String DASH_BASE_URL = "http://13.125.210.22:80/dash/";

    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent serviceIntent = new Intent(MainActivity.this, MyService.class);

        /*서비스 시작*/
        if(startService(serviceIntent) == null){
            startService(serviceIntent);
        }

        /*Initializing the TabLayout;*/
        TabLayout tabLayout = findViewById(R.id.TabLayout);

        /*Initializing ViewPager*/
        viewPager = findViewById(R.id.ViewPager);

        /*Creating adapter*/
        TabPagerAdapter pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        /*Set TabSelectedListener*/
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//
//        //Jedis pool 생성 (JedisPoolConfig, host, port, timeout, password)
//        JedisPool pool = new JedisPool(jedisPoolConfig, "13.125.210.22", 6379, 1000);
//        //thread, db pool 처럼 필요할 때 마다 getResource()로 받아서 쓰기
//        Jedis jedis = pool.getResource();
//        //테스트 데이터 입력
//        jedis.set("summy", "잘 들어갔니??");
//        //테스트 데이터 출력
//        Log.e("SUNNY", jedis.get("sunny"));
//
//        jedis.close();
//        pool.close();

    }//onCreate

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}

