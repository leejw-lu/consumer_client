package com.example.consumer_client.farm;

import static com.example.consumer_client.address.LocationDistance.distance;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;

import android.net.Uri;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.consumer_client.md.JointPurchaseActivity;
import com.example.consumer_client.R;
import com.example.consumer_client.md.MdDetailInfo;
import com.example.consumer_client.md.MdListMainActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

interface FarmDetailService{
    @POST("farmDetail")
    Call<ResponseBody> farmDetail(@Body JsonObject body);
}

public class FarmDetailActivity extends AppCompatActivity {
    String TAG = FarmDetailActivity.class.getSimpleName();

    FarmDetailService service;
    JsonParser jsonParser;
    JsonObject res;
    JsonArray farmArray, mdArray, pu_start, dDay;
    String farm_id, farm_name, farmer_name, farm_info, farm_loc, farm_main_item, farm_phone, md_price;

    private RecyclerView mRecyclerView;
    private ArrayList<MdDetailInfo> mList;
    private FarmDetailAdapter mFarmDetailAdapter;

    Context mContext;

    String user_id, standard_address;
    double myTownLat;
    double myTownLong;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_detail);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.baseurl))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(FarmDetailService.class);
        jsonParser = new JsonParser();

        mContext = this;

        ImageView FarmMainImg = findViewById(R.id.FarmMainImg);
        ImageView FarmStoryImg = findViewById(R.id.FarmStoryImg);
        TextView FarmName = (TextView) findViewById(R.id.FarmName);
        TextView UpFarmerName = (TextView) findViewById(R.id.farm_up_FarmerName);
        TextView FarmerName = (TextView) findViewById(R.id.FarmerName);
        TextView UpFarmName = (TextView) findViewById(R.id.farm_up_FarmName);
        TextView FarmExplain = (TextView) findViewById(R.id.FarmExplain);
        TextView FarmLocation = (TextView) findViewById(R.id.FarmLocation);
        TextView FarmMainItem = (TextView) findViewById(R.id.FarmMainItem);
        TextView FarmPhone = (TextView) findViewById(R.id.FarmPhone);
        TextView FarmPurchaseCount = (TextView) findViewById(R.id.FarmPurchaseCount);
        //공유하기
        ImageView KakaoShare = (ImageView) findViewById(R.id.KakaoShare);

        Intent intent;
        intent=getIntent();

        user_id=intent.getStringExtra("user_id");
        farm_id = intent.getStringExtra("farm_id");
        standard_address=intent.getStringExtra("standard_address");

        final Geocoder geocoder = new Geocoder(getApplicationContext());
        List<Address> myAddr = null;
        try {
            myAddr = geocoder.getFromLocationName(standard_address, 8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address location = myAddr.get(0);
        myTownLat= location.getLatitude();
        myTownLong = location.getLongitude();

        JsonObject body = new JsonObject();
        body.addProperty("farm_id", farm_id);

        Call<ResponseBody> call = service.farmDetail(body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        res =  (JsonObject) jsonParser.parse(response.body().string());

                        //farm 정보
                        farmArray = res.get("farm_data").getAsJsonArray();
                        farm_name = farmArray.get(0).getAsJsonObject().get("farm_name").getAsString();
                        farmer_name = farmArray.get(0).getAsJsonObject().get("farm_farmer").getAsString();
                        farm_info = farmArray.get(0).getAsJsonObject().get("farm_info").getAsString();
                        farm_loc = farmArray.get(0).getAsJsonObject().get("farm_loc").getAsString();
                        farm_main_item = farmArray.get(0).getAsJsonObject().get("farm_mainItem").getAsString();
                        farm_phone = farmArray.get(0).getAsJsonObject().get("farm_phone").getAsString();

                        //md 정보
                        mdArray = res.get("md_data").getAsJsonArray();

                        //pu_start
                        pu_start = res.get("pu_start").getAsJsonArray();
                        dDay = res.get("dDay").getAsJsonArray();

//                        FarmMainImg.setImageDrawable("https://ggdjang.s3.ap-northeast-2.amazonaws.com/" + farmArray.get(0).getAsJsonObject().get("farm_mainImg"));
                        Glide.with(FarmDetailActivity.this)
                                .load("https://ggdjang.s3.ap-northeast-2.amazonaws.com/" + farmArray.get(0).getAsJsonObject().get("farm_mainImg").getAsString())
                                .into(FarmMainImg);
                        Glide.with(FarmDetailActivity.this)
                                .load("https://ggdjang.s3.ap-northeast-2.amazonaws.com/" + farmArray.get(0).getAsJsonObject().get("farm_detailImg").getAsString())
                                .into(FarmStoryImg);
                        FarmName.setText(farm_name);
                        UpFarmName.setText(farm_name);
                        FarmerName.setText(farmer_name);
                        UpFarmerName.setText(farmer_name);
                        FarmExplain.setText(farm_info);
                        FarmLocation.setText(farm_loc);
                        FarmMainItem.setText(farm_main_item);
                        FarmPhone.setText(farm_phone);
                        FarmPurchaseCount.setText(String.valueOf(mdArray.size()));
                        //공유하기
                        KakaoShare.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    FeedTemplate params = FeedTemplate
                                            .newBuilder(ContentObject.newBuilder(mdArray.get(0).getAsJsonObject().get("farm_farmer").getAsString() +" 농부님의 " + farm_name + "에서 구매하기!",
                                                    "https://ggdjang.s3.ap-northeast-2.amazonaws.com/" + farmArray.get(0).getAsJsonObject().get("farm_mainImg").getAsString(),
                                                    LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                                            .setMobileWebUrl("https://developers.kakao.com").build())
                                                    .setDescrption("근처에서 직접 픽업하고 소포장을 줄여 환경도 지키자!")
                                                    .build())
                                            .addButton(new ButtonObject("웹에서 보기", LinkObject.newBuilder().setWebUrl("https://developers.kakao.com").setMobileWebUrl("https://developers.kakao.com").build()))
                                            .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                                                    .setWebUrl("https://developers.kakao.com")
                                                    .setMobileWebUrl("https://developers.kakao.com")
                                                    .setAndroidExecutionParams("key1=value1")
                                                    .setIosExecutionParams("key1=value1")
                                                    .build()))
                                            .build();

                                    Map<String, String> serverCallbackArgs = new HashMap<String, String>();
                                    serverCallbackArgs.put("user_id", "${current_user_id}");
                                    serverCallbackArgs.put("product_id", "${shared_product_id}");


                                    KakaoLinkService.getInstance().sendDefault(mContext, params, new ResponseCallback<KakaoLinkResponse>() {
                                        @Override
                                        public void onFailure(ErrorResult errorResult) {}

                                        @Override
                                        public void onSuccess(KakaoLinkResponse result) {
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();}
                            }
                        });

                        //세부 페이지1 (진행 중인 공동구매) 리사이클러뷰 띄우게하기
                        firstInit();

                        //어뎁터 적용
                        mFarmDetailAdapter = new FarmDetailAdapter(mList);
                        mRecyclerView.setAdapter(mFarmDetailAdapter);

                        //세로로 세팅
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
                        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        mRecyclerView.setLayoutManager(linearLayoutManager);

                        GridLayoutManager gridLayoutManager = new GridLayoutManager(FarmDetailActivity.this, 2, GridLayoutManager.VERTICAL, false);
                        mRecyclerView.setLayoutManager(gridLayoutManager);

                        for(int i=0;i<mdArray.size();i++){

                            List<Address> address = null;
                            try {
                                address = geocoder.getFromLocationName(mdArray.get(i).getAsJsonObject().get("store_loc").getAsString(), 8);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Address location = address.get(0);
                            double store_lat = location.getLatitude();
                            double store_long = location.getLongitude();
                            //자신이 설정한 위치와 스토어 거리 distance 구하기
                            double distanceKilo = distance(myTownLat, myTownLong, store_lat, store_long, "kilometer");

                            String realIf0;
                            if (dDay.get(i).getAsString().equals("0")) realIf0 = "D - day";
                            else if(dDay.get(i).getAsInt() < 0) realIf0 = "D + "+ Math.abs(dDay.get(i).getAsInt());
                            else realIf0 = "D - " + dDay.get(i).getAsString();


                            addFarmJointPurchase(
                                    "https://ggdjang.s3.ap-northeast-2.amazonaws.com/" + mdArray.get(i).getAsJsonObject().get("mdimg_thumbnail").getAsString(),
                                    mdArray.get(i).getAsJsonObject().get("md_name").getAsString(),
                                    mdArray.get(i).getAsJsonObject().get("store_name").getAsString(),
                                    String.format("%.2f", distanceKilo)+"km",
                                    mdArray.get(i).getAsJsonObject().get("pay_price").getAsString(),
                                    realIf0,
                                    pu_start.get(i).getAsString());
                        }

                        //거리 가까운순으로 정렬
                        mList.sort(new Comparator<MdDetailInfo>() {
                            @Override
                            public int compare(MdDetailInfo o1, MdDetailInfo o2) {
                                int ret;
                                Double distance1 = Double.valueOf(o1.getDistance().substring(0, o1.getDistance().length() - 2));
                                Double distance2 = Double.valueOf(o2.getDistance().substring(0, o2.getDistance().length() - 2));
                                //거리비교
                                ret = distance1.compareTo(distance2);
                                return ret;
                            }
                        });

                        mFarmDetailAdapter.setOnItemClickListener(
                            new FarmDetailAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View v, int pos) {
                                    Intent intent = new Intent(FarmDetailActivity.this, JointPurchaseActivity.class);
                                    intent.putExtra("user_id", user_id);
                                    intent.putExtra("md_id", mdArray.get(pos).getAsJsonObject().get("md_id").getAsString());

                                    startActivity(intent);
                                }
                            }
                    );

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        Log.d(TAG, "Fail " + response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "onFailure: e " + t.getMessage());
            }
        });
    }

    public void firstInit(){
        mRecyclerView = findViewById(R.id.FarmPurchaseView);
        mList = new ArrayList<>();
    }

    public void addFarmJointPurchase(String prodImgName, String prodName, String storeName, String distance, String mdPrice, String dDay, String puTime){
        MdDetailInfo mdDetail = new MdDetailInfo();

        mdDetail.setProdImg(prodImgName);
        mdDetail.setProdName(prodName);
        mdDetail.setStoreName(storeName);
        mdDetail.setDistance(distance);
        mdDetail.setMdPrice(mdPrice);
        mdDetail.setDday(dDay);
        // 미터 및 픽업 예정일 추가해야돼
        mdDetail.setPuTime(puTime);
        mList.add(mdDetail);
    }
}
