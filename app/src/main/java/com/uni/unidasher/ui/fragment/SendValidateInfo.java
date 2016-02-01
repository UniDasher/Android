package com.uni.unidasher.ui.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uni.unidasher.AppConstants;
import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.api.request.SenderValidationInfo;
import com.uni.unidasher.data.entity.UserInfo;
import com.uni.unidasher.data.event.RefreshUserInfoEvent;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.data.status.Status;
import com.uni.unidasher.ui.activity.ModifyInfoActivity;
import com.uni.unidasher.ui.dialog.AlertPrompt;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.HandlerHelper;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class SendValidateInfo extends EventBusFragment {
    private TextView txtvBack,txtvNavTitle;
    private View mRootView;
    private EditText etFirstName,etBankNum,etBankDes;
    private TextView txtvStatus,txtvSubmit;
    private DataProvider mDataProvider;
    private CircleImageView imgvLogo;
    public static SendValidateInfo newInstance(String param1, String param2) {
        SendValidateInfo fragment = new SendValidateInfo();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SendValidateInfo() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataProvider = DataProvider.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        findViews(inflater,container);
        setViews();
        setClickListeners();
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDataProvider.refreshUserInfo();
    }

    private void findViews(LayoutInflater inflater, ViewGroup container){
        mRootView = inflater.inflate(R.layout.fragment_send_validate_info, container, false);
        txtvStatus = (TextView)mRootView.findViewById(R.id.txtvStatus);
        etFirstName = (EditText)mRootView.findViewById(R.id.etFirstName);
        etBankNum = (EditText)mRootView.findViewById(R.id.etBankNum);
        etBankDes = (EditText)mRootView.findViewById(R.id.etBankDes);
        txtvSubmit = (TextView)mRootView.findViewById(R.id.txtvSubmit);
        imgvLogo = (CircleImageView)mRootView.findViewById(R.id.imgvLogo);
        txtvBack = (TextView)mRootView.findViewById(R.id.txtvBack);
        txtvNavTitle = (TextView)mRootView.findViewById(R.id.txtvNavTitle);
        txtvNavTitle.setText("配送认证");
    }

    private void setViews(){
        UserInfo userInfo = mDataProvider.getUserInfo();
        etFirstName.setText(userInfo.getFirstName());
        etBankNum.setText(userInfo.getBankAccount());
        etBankDes.setText(userInfo.getBankType());
        int status = userInfo.getStatus();
        txtvStatus.setText(status==-1?"不可用":Status.User.IdentityVerificationStatus[status]);
        if(status == 0||status == 4){
            txtvSubmit.setVisibility(View.VISIBLE);
            etFirstName.setEnabled(true);
            etBankNum.setEnabled(true);
            etBankDes.setEnabled(true);
        }else{
            txtvSubmit.setVisibility(View.GONE);
            etFirstName.setEnabled(false);
            etBankNum.setEnabled(false);
            etBankDes.setEnabled(false);
        }
        ImageLoader.getInstance().displayImage(AppConstants.HOST+userInfo.getLogo(),imgvLogo);
    }

    private void setClickListeners(){
        txtvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = etFirstName.getText().toString();
                String bankNum = etBankNum.getText().toString();
                String bankDes = etBankDes.getText().toString();
                if(TextUtils.isEmpty(firstName)){
                    Alert.showDefaultToast(getActivity(),"真实姓名不能为空!");
                    return;
                }else if(TextUtils.isEmpty(bankNum)){
                    Alert.showDefaultToast(getActivity(),"银行卡号不能为空!");
                    return;
                }else if(TextUtils.isEmpty(bankDes)){
                    Alert.showDefaultToast(getActivity(),"银行卡描述不能为空!");
                    return;
                }
                SenderValidationInfo senderValidationInfo = new SenderValidationInfo(firstName,bankNum,bankDes);
                mDataProvider.reportSenderValidation(senderValidationInfo, new RESTManager.OnObjectDownloadedListener() {
                    @Override
                    public void onObjectDownloaded(final boolean success, String resultStr, String tipStr) {
                        if (BuildConfig.DEBUG) {
                            Log.i("reportSenderValidation:", success + "" + "---" + resultStr);
                        }
                        HandlerHelper.post(new HandlerHelper.onRun() {
                            @Override
                            public void run() {
                                if(success){
                                    txtvSubmit.setVisibility(View.GONE);
                                    etFirstName.setEnabled(false);
                                    etBankNum.setEnabled(false);
                                    etBankDes.setEnabled(false);
                                    mDataProvider.refreshUserInfo();
                                    Alert.showDefaultToast(getActivity(), "认证信息已提交,请等候审核!");
                                }else{
                                    Alert.showDefaultToast(getActivity(), "提交信息有误,请重新填写!");
                                }
                            }
                        });
                    }
                });
            }
        });

        txtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ModifyInfoActivity)getActivity()).onBack();
            }
        });
    }

    @Subscribe
    public void onRefreshUserInfo(RefreshUserInfoEvent refreshUserInfoEvent){
        HandlerHelper.post(new HandlerHelper.onRun() {
            @Override
            public void run() {
                setViews();
            }
        });
    }
}
