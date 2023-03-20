package com.example.consumer_client.shopping_info;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;

import com.example.consumer_client.MainActivity;
import com.example.consumer_client.R;
import com.example.consumer_client.cart.CartListActivity;
import com.example.consumer_client.mypage.AboutGDJActivity;

public class ShoppingInfoActivity extends TabActivity {
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_tab_host);

        Intent intent = getIntent(); //intent 값 받기
        user_id=intent.getStringExtra("user_id");

        TabHost tabHost = getTabHost(); //탭 호스트 객체 생성

        // 탭스팩 선언하고, 탭의 내부 명칭, 탭에 출력될 글 작성
        TabHost.TabSpec spec;

        //뒤로가기
        ImageView gotoBack = findViewById(R.id.gotoBack);
        gotoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ShoppingInfoActivity.this, MainActivity.class);
                intent1.putExtra("user_id", user_id);
                startActivity(intent1);
            }
        });

        //상단바 장바구니
        ImageView gotoCart = findViewById(R.id.gotoCart);
        gotoCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShoppingInfoActivity.this, CartListActivity.class);
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });

        //탭에서 액티비티를 사용할 수 있도록 인텐트 생성
        intent = new Intent().setClass(this,OrderList.class);
        intent.putExtra("user_id", user_id);
        spec = tabHost.newTabSpec("ShoppingInfo"); // 객체를 생성
        spec.setIndicator("상세주문내역"); //탭의 이름 설정
        spec.setContent(intent);
        tabHost.addTab(spec);

        //탭에서 액티비티를 사용할 수 있도록 인텐트 생성
        intent = new Intent().setClass(this, CancelList.class);
        intent.putExtra("user_id", user_id);
        spec = tabHost.newTabSpec("CancelList"); // 객체를 생성
        spec.setIndicator("취소내역"); //탭의 이름 설정
        spec.setContent(intent);
        tabHost.addTab(spec);

        //탭에서 액티비티를 사용할 수 있도록 인텐트 생성
        intent = new Intent().setClass(this, ReviewList.class);
        intent.putExtra("user_id", user_id);
        spec = tabHost.newTabSpec("ReviewList"); // 객체를 생성
        spec.setIndicator("상품리뷰"); //탭의 이름 설정
        spec.setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0); //먼저 열릴 탭을 선택
    }
}
