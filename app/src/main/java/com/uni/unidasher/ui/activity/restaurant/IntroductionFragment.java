package com.uni.unidasher.ui.activity.restaurant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uni.unidasher.R;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;
import com.uni.unidasher.ui.fragment.EventBusFragment;
import com.uni.unidasher.ui.utils.Extras;

/**
 * Created by Administrator on 2015/6/6.
 */
public class IntroductionFragment extends EventBusFragment {
    private View mRootView;
    private ShopInfo shopInfo;
    private TextView txtvName,txtvLocation,txtvGood,txtvBad,txtvShopPhone,txtvShopType,txtvShopServiceTime;

    public static IntroductionFragment newInstance(ShopInfo shopInfo){
        IntroductionFragment fragment = new IntroductionFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Extras.Extra_ShopInfo,shopInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if(bundle!=null&&bundle.containsKey(Extras.Extra_ShopInfo)){
            shopInfo = (ShopInfo)bundle.getSerializable(Extras.Extra_ShopInfo);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        findViews(inflater, container);
        setViews();
        setClickListeners();
        return mRootView;
    }

    private void findViews(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.fragment_introduction, container, false);
        txtvName = (TextView)mRootView.findViewById(R.id.txtvName);
        txtvLocation = (TextView)mRootView.findViewById(R.id.txtvLocation);
        txtvGood = (TextView)mRootView.findViewById(R.id.txtvGood);
        txtvBad = (TextView)mRootView.findViewById(R.id.txtvBad);
        txtvShopPhone = (TextView)mRootView.findViewById(R.id.txtvShopPhone);
        txtvShopType = (TextView)mRootView.findViewById(R.id.txtvShopType);
        txtvShopServiceTime = (TextView)mRootView.findViewById(R.id.txtvShopServiceTime);
    }

    public void setViews(){
        if(shopInfo == null){
            return;
        }
        txtvName.setText(shopInfo.getName());
        txtvLocation.setText(shopInfo.getAddress());
        txtvGood.setText(shopInfo.getGoodEvaluate()+"");
        txtvBad.setText(shopInfo.getBadEvaluate()+"");
        txtvShopPhone.setText(shopInfo.getPhone());
        txtvShopType.setText(shopInfo.getTypeTab());
        if(TextUtils.isEmpty(shopInfo.getServiceTimes())){
            txtvShopServiceTime.setText("00:00-24:00");
        }else{
            txtvShopServiceTime.setText(shopInfo.getServiceTimes());
        }

    }

    private void setClickListeners(){
    }
}
