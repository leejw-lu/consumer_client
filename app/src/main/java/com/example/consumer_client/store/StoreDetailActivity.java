package com.example.consumer_client.store;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.consumer_client.farm.FarmDetailAdapter;
import com.example.consumer_client.farm.FarmDetailInfo;
import com.example.consumer_client.md.JointPurchaseActivity;
import com.example.consumer_client.R;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface StoreDetailService {
    @POST("storeDetail/")
    Call<ResponseBody> StoreDetail(@Body JsonObject body);    //post store_id
}

public class StoreDetailActivity extends AppCompatActivity {

    StoreDetailService service;
    JsonParser jsonParser;
    JsonObject res;
    JsonArray storeArray,jpArray,rvwArray, pu_start, pu_end;
    String store_id, store_name;
    Context mContext;

    private RecyclerView mRecyclerView;
    private RecyclerView reviewRecyclerView;
    private ArrayList<FarmDetailInfo> mList;
    private ArrayList<StoreReviewInfo> mReviewList;
    private FarmDetailAdapter mFarmDetailAdapter;
    private StoreReviewAdapter mStoreReviewAdapter;

    String user_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_detail);

        mContext = this;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseurl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(StoreDetailService.class);
        jsonParser = new JsonParser();

        Intent intent;
        intent=getIntent(); //intent 값 받기
        user_id=intent.getStringExtra("user_id");
        store_id=intent.getStringExtra("storeid");

        TextView StoreName = (TextView) findViewById(R.id.StoreName);
        TextView StoreExplain = (TextView) findViewById(R.id.StoreExplain);
        TextView StoreLocation = (TextView) findViewById(R.id.StoreLocation);
        TextView StoreHourTime = (TextView) findViewById(R.id.StoreHourTime);
        //TextView StoreDayOff = (TextView) findViewById(R.id.StoreDayOff);
        TextView StoreJointPurchaseCount = (TextView) findViewById(R.id.StoreJointPurchaseCount);

        JsonObject body = new JsonObject();
        body.addProperty("id", store_id);
        Call<ResponseBody> call = service.StoreDetail(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    try {
                         res= (JsonObject) jsonParser.parse(response.body().string());

                        //store정보
                        storeArray= res.get("store_result").getAsJsonArray();
                        //md정보
                        jpArray=res.get("jp_result").getAsJsonArray();
                        pu_start = res.get("pu_start").getAsJsonArray();
                        pu_end = res.get("pu_end").getAsJsonArray();
                        //리뷰정보
                        rvwArray= res.get("review_result").getAsJsonArray();
                        store_name=storeArray.get(0).getAsJsonObject().get("store_name").getAsString();

                        StoreName.setText(store_name);
                        StoreExplain.setText(storeArray.get(0).getAsJsonObject().get("store_info").getAsString());
                        StoreLocation.setText(storeArray.get(0).getAsJsonObject().get("store_loc").getAsString());
                        StoreHourTime.setText(storeArray.get(0).getAsJsonObject().get("store_hours").getAsString());
                        //StoreDayOff.setText(storeArray.get(0).getAsJsonObject().get("store_restDays").getAsString());
                        StoreJointPurchaseCount.setText(String.valueOf(jpArray.size()));

                        final Geocoder geocoder = new Geocoder(getApplicationContext());
                        String store_loc= storeArray.get(0).getAsJsonObject().get("store_loc").getAsString();
                        List<Address> address=  geocoder.getFromLocationName(store_loc,10);
                        Address location = address.get(0);
                        double store_lat=location.getLatitude();
                        double store_long=location.getLongitude();

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

                        firstInit();

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
                        GridLayoutManager gridLayoutManager = new GridLayoutManager(StoreDetailActivity.this, 2, GridLayoutManager.VERTICAL, true);
                        reviewRecyclerView.setLayoutManager(gridLayoutManager);

                        //진행중인 공동구매 md
                        for(int i=0;i<jpArray.size();i++){
                            addFarmJointPurchase(jpArray.get(i).getAsJsonObject().get("farm_name").getAsString(), jpArray.get(i).getAsJsonObject().get("md_name").getAsString(), jpArray.get(i).getAsJsonObject().get("store_name").getAsString(),pu_start.get(i).getAsString()+" ~ "+pu_end.get(i).getAsString());
                        }

                        //리뷰
                        for(int i=0;i<rvwArray.size();i++){
                            addReview("product Img", "@id " + i, rvwArray.get(i).getAsJsonObject().get("md_name").getAsString(), rvwArray.get(i).getAsJsonObject().get("rvw_rating").getAsString(),rvwArray.get(i).getAsJsonObject().get("rvw_content").getAsString());
                        }

                        mFarmDetailAdapter.setOnItemClickListener(
                                new FarmDetailAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View v, int pos) {
                                        Intent intent = new Intent(StoreDetailActivity.this, JointPurchaseActivity.class);
                                        intent.putExtra("user_id", user_id);
                                        intent.putExtra("md_id", jpArray.get(pos).getAsJsonObject().get("md_id").getAsString());
                                        startActivity(intent);
                                    }
                                }
                        );

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(StoreDetailActivity.this, "스토어 세부 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("세부", t.getMessage());
            }

        });

    }

    public void firstInit(){
        mRecyclerView = findViewById(R.id.FarmJointPurchaseView);
        reviewRecyclerView = findViewById(R.id.StoreReview);
        mList = new ArrayList<>();
        mReviewList = new ArrayList<>();
    }

    public void addFarmJointPurchase(String farmName, String prodName, String storeName, String puTerm){
        FarmDetailInfo farmDetail = new FarmDetailInfo();

        farmDetail.setFarmName(farmName);
        farmDetail.setProdName(prodName);
        farmDetail.setStoreName(storeName);
//        farmDetail.setPaySchedule(paySchedule);
        farmDetail.setPuTerm(puTerm);

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