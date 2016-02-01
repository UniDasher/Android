package com.uni.unidasher.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.uni.unidasher.R;
import com.uni.unidasher.ui.fragment.EarnRecordListFragment;
import com.uni.unidasher.ui.fragment.OutBankListFragment;

public class EarningsDateRecordActivity extends EventBusActivity {
    private TextView txtvBack,txtvNavTitle;
    private TextView[] mTabs;
    private Fragment[] fragments;
    private int currentTabIndex;
    private int index;
    private EarnRecordListFragment earnRecordListFragment;
    private OutBankListFragment outBankListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings_date_record);
        findViews();
        setViews();
        initTab();
        setClickListeners();
    }

    private void findViews(){
        txtvBack = (TextView)findViewById(R.id.txtvBack);
        txtvNavTitle = (TextView)findViewById(R.id.txtvNavTitle);
        txtvNavTitle.setText("收益记录");
    }

    private void setViews(){
//        if(shopInfo!=null){
//            txtvNavTitle.setText(shopInfo.getName());
//        }
    }

    private void initTab(){
        mTabs = new TextView[2];
        mTabs[0] = (TextView) findViewById(R.id.txtvEarn);
        mTabs[1] = (TextView) findViewById(R.id.txtvOut);
        mTabs[0].setSelected(true);

        earnRecordListFragment = new EarnRecordListFragment();
        outBankListFragment = new OutBankListFragment();
        fragments = new Fragment[] {earnRecordListFragment,outBankListFragment};
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, earnRecordListFragment)
                .show(earnRecordListFragment).commit();
    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.txtvEarn:
                index = 0;
                break;
            case R.id.txtvOut:
                index = 1;
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
        mTabs[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }
    private void setClickListeners(){
        txtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
