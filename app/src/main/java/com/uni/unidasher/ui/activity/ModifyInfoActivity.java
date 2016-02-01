package com.uni.unidasher.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.uni.unidasher.R;
import com.uni.unidasher.chat.ui.activity.BaseActivity;
import com.uni.unidasher.data.status.Status;
import com.uni.unidasher.ui.fragment.AccountInfoFragment;
import com.uni.unidasher.ui.fragment.BaseInfoModifyFragment;
import com.uni.unidasher.ui.fragment.ModifyPasswordFragment;
import com.uni.unidasher.ui.fragment.SendValidateInfo;
import com.uni.unidasher.ui.utils.Extras;

public class ModifyInfoActivity extends BaseActivity {
//    private TextView txtvBack,txtvNavTitle;
    private int status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_info);
        findViews();
        status = getIntent().getIntExtra(Extras.Extra_Modify_Base_Info_Page,0);
        setViews();
        setClickListeners();
    }

    private void findViews(){
//        txtvBack = (TextView)findViewById(R.id.txtvBack);
//        txtvNavTitle = (TextView)findViewById(R.id.txtvNavTitle);
    }

    private void setClickListeners(){
//        txtvBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBack();
//            }
//        });
    }

    public void setFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations( R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_out_to_right,R.anim.slide_in_from_left);
        transaction.replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName()).commit();
    }

    public void setViews(){
        Fragment fragment = null;
        String navTitle = "";
        switch (status){
            case Status.ModifyBaseInfo.Status_Modify_Password:
//                navTitle = "密码修改";
                fragment = new ModifyPasswordFragment();
                break;
            case Status.ModifyBaseInfo.Status_Account_Info:
//                navTitle = "账号信息";
                fragment = new AccountInfoFragment();
                break;
            case Status.ModifyBaseInfo.Status_Send_Validate_Info:
//                navTitle = "配送认证";
                fragment = new SendValidateInfo();
                break;
        }
//        txtvNavTitle.setText(navTitle);
        setFragment(fragment);
    }

    public void updateInfo(int type){
        Fragment fragment = BaseInfoModifyFragment.newInstance(type);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations( R.anim.slide_in_from_right, R.anim.slide_out_to_left, R.anim.slide_in_from_left,R.anim.slide_out_to_right);
        transaction.replace(R.id.content_frame, fragment, fragment.getClass().getSimpleName());
        transaction.addToBackStack(fragment.getClass().getSimpleName()).commit();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        onBack();
    }

    public void onBack(){
        if(getSupportFragmentManager().getBackStackEntryCount()>1){
            getSupportFragmentManager().popBackStack();
        }else{
            finish();
        }
    }
}
