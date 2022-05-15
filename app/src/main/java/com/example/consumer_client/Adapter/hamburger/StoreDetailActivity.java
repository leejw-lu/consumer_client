package com.example.consumer_client.Adapter.hamburger;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.consumer_client.R;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.util.ArrayList;

public class StoreDetailActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView reviewRecyclerView;
    private ArrayList<FarmDetailInfo> mList;
    private ArrayList<StoreReviewInfo> mReviewList;
    private FarmDetailAdapter mFarmDetailAdapter;
    private StoreReviewAdapter mStoreReviewAdapter;
    Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        //진행중인공동구매 이동
        TextView textViewJP = (TextView) findViewById(R.id.JP);
        textViewJP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JointPurchaseActivity.class);
                startActivity(intent);
            }
        });

        mContext = this;

        //전체픽업 스토어-> 이용가능한 모든 픽업스토어의 리사이클러뷰 클릭하면 나오는 스토어 세부페이지1
        //intent로 값 넘길때
        Intent intent;
        String store_name, store_info, store_loc, store_hours, store_dayoff;
        Double store_lat,store_long;

        intent=getIntent(); //intent 값 받기

        store_name=intent.getStringExtra("storeName");
        store_info=intent.getStringExtra("storeInfo");
        store_loc=intent.getStringExtra("storeLoc");
        store_hours=intent.getStringExtra("storeHours");
        store_dayoff=intent.getStringExtra("storeRestDays");

        store_lat=Double.parseDouble(intent.getStringExtra("storeLat")); //위도-> double 형변환
        store_long=Double.parseDouble(intent.getStringExtra("storeLong")); //경도

        TextView StoreName = (TextView) findViewById(R.id.StoreName);
        TextView StoreExplain = (TextView) findViewById(R.id.StoreExplain);
        TextView StoreLocation = (TextView) findViewById(R.id.StoreLocation);
        TextView StoreHourTime = (TextView) findViewById(R.id.StoreHourTime);
        TextView StoreDayOff = (TextView) findViewById(R.id.StoreDayOff);

        StoreName.setText(store_name);
        StoreExplain.setText(store_info);
        StoreLocation.setText(store_loc);
        StoreHourTime.setText(store_hours);
        StoreDayOff.setText(store_dayoff);


        //지도
        MapView mapView = new MapView(mContext);
        // 중심점 변경
        //mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(35.01426900000000, 126.7169940), true);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(store_lat, store_long), true);

        // 줌 레벨 변경
        mapView.setZoomLevel(1, true);
        // 줌 인
        mapView.zoomIn(true);
        // 줌 아웃
        mapView.zoomOut(true);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.store_map_view);
        mapViewContainer.addView(mapView);

        //스토어위치 마커 아이콘 띄우기
        MapPoint f_MarkPoint = MapPoint.mapPointWithGeoCoord(store_lat, store_long);  //마커찍기

        MapPOIItem store_marker=new MapPOIItem();
        store_marker.setItemName(store_name); //클릭했을때 가게이름 나오기
        store_marker.setTag(0);
        store_marker.setMapPoint(f_MarkPoint);   //좌표입력받아 현위치로 출력

        //  (클릭 전)기본으로 제공하는 BluePin 마커 모양의 색.
        store_marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        // (클릭 후) 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        store_marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        // 지도화면 위에 추가되는 아이콘을 추가하기 위한 호출(말풍선 모양)
        mapView.addPOIItem(store_marker);


        /// 세부페이지에 있는 리사이클러뷰
        firstInit();

        //추후에 제품 이름 가져올 예정
        for(int i=0;i<10;i++){
            addFarmJointPurchase("product Img", "농가 이름" + i, "농가 제품 이름" + i, "농가 특징" + i, "" + i);
        }
        for(int i=0;i<10;i++){
            addReview("product Img", "@id " + i, "제품명" + i, "" + i, "제품리뷰 제품리뷰 제품리뷰 제품리뷰 제품리뷰 제품리뷰 제품리뷰 제품리뷰 " + i);
        }

        //어뎁터 적용
        mFarmDetailAdapter = new FarmDetailAdapter(mList);
        mRecyclerView.setAdapter(mFarmDetailAdapter);

        //세로로 세팅
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //어뎁터 적용
        mStoreReviewAdapter = new StoreReviewAdapter(mReviewList);
        reviewRecyclerView.setAdapter(mStoreReviewAdapter);

        //두 칸으로 세팅
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, true);
        reviewRecyclerView.setLayoutManager(gridLayoutManager);

    }

    public void firstInit(){
        mRecyclerView = findViewById(R.id.FarmJointPurchaseView);
        reviewRecyclerView = findViewById(R.id.StoreReview);
        mList = new ArrayList<>();
        mReviewList = new ArrayList<>();
    }

    public void addFarmJointPurchase(String farmProdImg, String farmName, String farmProdName, String farmFeature, String farmSituation){
        FarmDetailInfo farmDetail = new FarmDetailInfo();

        farmDetail.setFarmDetailProdImgView(farmProdImg);
        farmDetail.setFarmDetailName(farmName);
        farmDetail.setFarmDetailProdName(farmProdName);

        mList.add(farmDetail);
    }

    public void addReview(String userProfileImg, String userID, String prodName, String starCount, String reviewMessage){
        StoreReviewInfo review = new StoreReviewInfo();

        review.setUserProfileImg(userProfileImg);
        review.setUserID(userID);
        review.setProdName(prodName);
        review.setStarCount(starCount);
        review.setReview(reviewMessage);

        mReviewList.add(review);
    }
}
