package com.uni.unidasher.ui.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.igexin.sdk.PushManager;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DasherMgr;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.status.Status;
import com.uni.unidasher.ui.activity.EarningsActivity;
import com.uni.unidasher.ui.activity.LoginActivity;
import com.uni.unidasher.ui.activity.ModifyInfoActivity;
import com.uni.unidasher.ui.activity.OrderHistoryActivity;
import com.uni.unidasher.ui.activity.TabActivity;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.Extras;

/**
 * Created by Administrator on 2015/5/21.
 */
public class AccountFragment extends EventBusFragment {
    private View mRootView;
    private TextView txtvChangeIdentity,txtvOrderHistory,txtvSendOrderHistory,txtvEarnHistory,txtvSendValidate,txtvAccountInfo,txtvChangePwd;
    private Button btnExit;
    private TextView txtvBack,txtvNavTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        findViews(inflater, container);
        setClickListeners();
        return mRootView;
    }

    private void findViews(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.fragment_account, container, false);
        txtvBack = (TextView)mRootView.findViewById(R.id.txtvBack);
        txtvBack.setVisibility(View.INVISIBLE);
        txtvNavTitle = (TextView)mRootView.findViewById(R.id.txtvNavTitle);
        txtvNavTitle.setText("账户");
        txtvOrderHistory = (TextView)mRootView.findViewById(R.id.txtvOrderHistory);
        txtvSendOrderHistory = (TextView)mRootView.findViewById(R.id.txtvSendOrderHistory);
        txtvEarnHistory = (TextView)mRootView.findViewById(R.id.txtvEarnHistory);
        txtvSendValidate = (TextView)mRootView.findViewById(R.id.txtvSendValidate);
        txtvAccountInfo = (TextView)mRootView.findViewById(R.id.txtvAccountInfo);
        txtvChangePwd = (TextView)mRootView.findViewById(R.id.txtvChangePwd);
        btnExit = (Button)mRootView.findViewById(R.id.btnExit);
        txtvChangeIdentity = (TextView)mRootView.findViewById(R.id.txtvChangeIdentity);
    }

    private void setClickListeners(){
        txtvOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(Extras.Extra_Is_Customer,true);
                startActivity(new Intent(getActivity(), OrderHistoryActivity.class).putExtras(bundle));
            }
        });
        txtvSendOrderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putBoolean(Extras.Extra_Is_Customer,false);
                startActivity(new Intent(getActivity(), OrderHistoryActivity.class).putExtras(bundle));
            }
        });
        txtvEarnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), EarningsActivity.class));
            }
        });
        txtvSendValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity()!=null){
                    startActivity(new Intent(getActivity(), ModifyInfoActivity.class).putExtra(Extras.Extra_Modify_Base_Info_Page, Status.ModifyBaseInfo.Status_Send_Validate_Info));
                }
            }
        });
        txtvAccountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity()!=null){
                    startActivity(new Intent(getActivity(), ModifyInfoActivity.class).putExtra(Extras.Extra_Modify_Base_Info_Page, Status.ModifyBaseInfo.Status_Account_Info));
                }
            }
        });
        txtvChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity()!=null){
                    startActivityForResult(new Intent(getActivity(), ModifyInfoActivity.class).putExtra(Extras.Extra_Modify_Base_Info_Page, Status.ModifyBaseInfo.Status_Modify_Password), Extras.Request_Code_Modify_Password);
                }
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Alert.cancelNotify(getActivity());//去除通知
                PushManager.getInstance().turnOffPush(getActivity());//关闭推送
                DataProvider.getInstance(getActivity()).logOut();
                startActivity(new Intent(getActivity(),LoginActivity.class));
                getActivity().finish();
            }
        });
        txtvChangeIdentity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity()!=null){
                    ((TabActivity)getActivity()).switchUserIdentityFromAccount();
                }
            }
        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            boolean isCustomer = DasherMgr.getInstance(getActivity()).isCustomer();
            String str = getResources().getString(R.string.accountChangeIndentity);
            txtvChangeIdentity.setText(isCustomer ? str + "送单人" : str + "订单人");
            Drawable drawable = getResources().getDrawable(isCustomer ? R.mipmap.nav_right_send : R.mipmap.nav_right_customer);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            txtvChangeIdentity.setCompoundDrawables(null, null, drawable, null);
        }
    }
}
