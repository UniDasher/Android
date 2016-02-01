package com.uni.unidasher.ui.activity.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.uni.unidasher.R;
import com.uni.unidasher.data.datamodel.databaseAndApi.UserAdressInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;
import com.uni.unidasher.ui.activity.EventBusActivity;
import com.uni.unidasher.ui.utils.Extras;

public class RestaurantActivity extends EventBusActivity {
    private TextView txtvBack,txtvNavTitle;
    private ImageView imgvRight;
    private TextView[] mTabs;
    private Fragment[] fragments;
    private DishListFragment dishListFragment;
    private IntroductionFragment introductionFragment;
    private int currentTabIndex = 0;
    private int index = 0;
    private ShopInfo shopInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null&&bundle.containsKey(Extras.Extra_ShopInfo)){
            shopInfo = (ShopInfo)bundle.getSerializable(Extras.Extra_ShopInfo);
        }
        findViews();
        setViews();
        initTab();
        setClickListeners();
    }

    private void findViews(){
        txtvBack = (TextView)findViewById(R.id.txtvBack);
        txtvNavTitle = (TextView)findViewById(R.id.txtvNavTitle);
        imgvRight = (ImageView)findViewById(R.id.imgvRight);
        imgvRight.setImageResource(R.mipmap.switch_shop_introduction);
        imgvRight.setVisibility(View.VISIBLE);
    }

    private void setViews(){
        if(shopInfo!=null){
            txtvNavTitle.setText(shopInfo.getName());
        }
    }


    private void initTab(){
        mTabs = new TextView[2];
        mTabs[0] = (TextView) findViewById(R.id.txtvOrderFood);
        mTabs[1] = (TextView) findViewById(R.id.txtvIntroduction);
        mTabs[0].setSelected(true);

        dishListFragment = DishListFragment.newInstance(shopInfo);
        introductionFragment = IntroductionFragment.newInstance(shopInfo);
        fragments = new Fragment[] {dishListFragment,introductionFragment};
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, dishListFragment)
                .show(dishListFragment).commit();
    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.txtvOrderFood:
                index = 0;
                break;
            case R.id.txtvIntroduction:
                index = 1;
                break;
            case R.id.imgvRight:
                if(index == 0){
                    index = 1;
                    imgvRight.setImageResource(R.mipmap.switch_shop_dish);
                }else{
                    index = 0;
                    imgvRight.setImageResource(R.mipmap.switch_shop_introduction);
                }
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.content_frame, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
//        mTabs[currentTabIndex].setSelected(false);
//        // 把当前tab设为选中状态
//        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    private void setClickListeners(){
        txtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentTabIndex == 1){
                    finish();
                }else{
                    if(dishListFragment.isOpenDrawer()){
                        dishListFragment.closeDrawer();
                    }else{
                        finish();
                    }
                }
            }
        });
        imgvRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onTabClicked(v);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null)
            return;
        Bundle bundle = data.getExtras();
        if(bundle == null){
            return;
        }
        switch (resultCode){
            case Extras.Request_Code_Update_Dishes_Of_Pay:
                boolean isUpdate = bundle.getBoolean(Extras.Extra_Update_Dishes_Of_Pay_Flag,false);
                if(isUpdate){
                    dishListFragment.updateDishList();
                }
                break;
        }
    }
}
