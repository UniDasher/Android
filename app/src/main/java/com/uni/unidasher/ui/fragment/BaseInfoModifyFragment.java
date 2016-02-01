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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.entity.UserInfo;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.data.status.Status;
import com.uni.unidasher.ui.activity.ModifyInfoActivity;
import com.uni.unidasher.ui.dialog.AlertPrompt;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.Extras;
import com.uni.unidasher.ui.utils.HandlerHelper;
import com.uni.unidasher.ui.widget.ClearEditText;

public class BaseInfoModifyFragment extends EventBusFragment {

    private View mRootView;
    private TextView txtvBack,txtvNavTitle;
    private int type;
    private LinearLayout layoutPhone;
    private TextView txtvSave;
    private EditText editValidationCode;
    private EditText etInput;
    private DataProvider mDataProvider;


    public static BaseInfoModifyFragment newInstance(int type) {
        BaseInfoModifyFragment fragment = new BaseInfoModifyFragment();
        Bundle args = new Bundle();
        args.putInt(Extras.Extra_Modify_BaseInfo_Type,type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataProvider = DataProvider.getInstance(getActivity());
        if (getArguments() != null) {
            type = getArguments().getInt(Extras.Extra_Modify_BaseInfo_Type);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        findViews(inflater,container);
        setClickListeners();
        setViews();
        return mRootView;
    }

    private void findViews(LayoutInflater inflater, ViewGroup container){
        mRootView = inflater.inflate(R.layout.fragment_base_info_modify, container, false);
        txtvBack = (TextView)mRootView.findViewById(R.id.txtvBack);
        editValidationCode = (EditText)mRootView.findViewById(R.id.editValidationCode);
        txtvNavTitle = (TextView)mRootView.findViewById(R.id.txtvNavTitle);
        etInput = (EditText)mRootView.findViewById(R.id.etInput);
        layoutPhone = (LinearLayout)mRootView.findViewById(R.id.layoutPhone);
        txtvSave = (TextView)mRootView.findViewById(R.id.txtvSave);

    }

    private void setClickListeners(){
        txtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ModifyInfoActivity)getActivity()).onBack();
            }
        });
        txtvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String value = etInput.getText().toString();
                if(!TextUtils.isEmpty(value)){
                    switch (type){
                        case Status.User.Modify_User_Phone:
                            String code = editValidationCode.getText().toString();
                            if(!TextUtils.isEmpty(code)){
                                mDataProvider.updatePhoneInfo(value, code, new RESTManager.OnObjectDownloadedListener() {
                                    @Override
                                    public void onObjectDownloaded(final boolean success, String resultStr, String tipStr) {
                                        if(BuildConfig.DEBUG){
                                            Log.i("updatePhoneInfo",success+"---->"+resultStr);
                                        }
                                        HandlerHelper.post(new HandlerHelper.onRun() {
                                            @Override
                                            public void run() {
                                                if(success){
                                                    Alert.showTip(getActivity(), "提示", "电话已修改。", false, new AlertPrompt.OnAlertClickListener() {
                                                        @Override
                                                        public void onClick() {
                                                            ((ModifyInfoActivity) getActivity()).onBack();
                                                        }
                                                    });

                                                }else{
                                                    Alert.showTip(getActivity(),"提示","提交信息有误，请重新填写。",true,null);
                                                }
                                            }
                                        });
                                    }
                                });
                            }


                            break;
                        case Status.User.Modify_User_NickName:
                            mDataProvider.updateNickName(value, new RESTManager.OnObjectDownloadedListener() {
                                @Override
                                public void onObjectDownloaded(final boolean success, String resultStr, String tipStr) {
                                    if(BuildConfig.DEBUG){
                                        Log.i("updateNickName",success+"---->"+resultStr);
                                    }
                                    HandlerHelper.post(new HandlerHelper.onRun() {
                                        @Override
                                        public void run() {
                                            if(success){
                                                Alert.showTip(getActivity(), "提示", "用户名已修改。", false, new AlertPrompt.OnAlertClickListener() {
                                                    @Override
                                                    public void onClick() {
                                                        ((ModifyInfoActivity) getActivity()).onBack();
                                                    }
                                                });
                                            }else{
                                                Alert.showTip(getActivity(),"提示","提交信息有误，请重新填写。",true,null);
                                            }
                                        }
                                    });
                                }
                            });
                            break;
                    }
                }
            }
        });
    }

    private void setViews(){
        UserInfo userInfo = mDataProvider.getUserInfo();
        switch (type){
            case Status.User.Modify_User_Phone:
                txtvNavTitle.setText("手机号修改");
                layoutPhone.setVisibility(View.VISIBLE);
                etInput.setText(userInfo.getMobilePhone());
                break;
            case Status.User.Modify_User_NickName:
                txtvNavTitle.setText("用户名修改");
                layoutPhone.setVisibility(View.GONE);
                etInput.setText(userInfo.getNickName());
                break;
        }
        etInput.setFocusable(true);
        etInput.setFocusableInTouchMode(true);
        etInput.requestFocus();
        etInput.setSelection(etInput.getText().length());

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
