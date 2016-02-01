package com.uni.unidasher.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.ui.activity.ModifyInfoActivity;
import com.uni.unidasher.ui.dialog.AlertPrompt;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.Extras;
import com.uni.unidasher.ui.utils.HandlerHelper;

public class ModifyPasswordFragment extends EventBusFragment {
    private TextView txtvBack,txtvNavTitle;
    private View mRootView;
    private EditText etOldPassword,etNewPassword,etRePassword;
    private TextView txtvSave;

    public static ModifyPasswordFragment newInstance(String param1, String param2) {
        ModifyPasswordFragment fragment = new ModifyPasswordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public ModifyPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        findViews(inflater,container);
        setClickListeners();
        return mRootView;
    }

    private void findViews(LayoutInflater inflater, ViewGroup container){
        mRootView = inflater.inflate(R.layout.fragment_modify_password, container, false);
        etOldPassword = (EditText)mRootView.findViewById(R.id.etxtOldPassword);
        etNewPassword = (EditText)mRootView.findViewById(R.id.etxtNewPassword);
        etRePassword = (EditText)mRootView.findViewById(R.id.etxtRePassword);
        txtvSave = (TextView)mRootView.findViewById(R.id.txtvSave);
        txtvBack = (TextView)mRootView.findViewById(R.id.txtvBack);
        txtvNavTitle = (TextView)mRootView.findViewById(R.id.txtvNavTitle);
        txtvNavTitle.setText("密码修改");
    }

    private void setClickListeners(){
        txtvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPwd = etOldPassword.getText().toString();
                String newPwd = etNewPassword.getText().toString();
                String rePwd = etRePassword.getText().toString();
                if(TextUtils.isEmpty(oldPwd)){
//                    etOldPassword.setError("不能为空");
                    Alert.showDefaultToast(getActivity(),"原始密码不能为空!");
                }else if(newPwd.length()<6){
                    Alert.showDefaultToast(getActivity(),"密码不能小于六位!");
                }else if(!newPwd.equals(rePwd)){
//                    etRePassword.setError("密码必须相同");
                    Alert.showDefaultToast(getActivity(),"两次密码输入不同，请重新提交!");
                }else{
                    DataProvider.getInstance(getActivity()).updatePassword(oldPwd, newPwd, new RESTManager.OnObjectDownloadedListener() {
                        @Override
                        public void onObjectDownloaded(final boolean success, String resultStr, final String tipStr) {
                            if(BuildConfig.DEBUG){
                                Log.i("updatePassword",success+"---->"+resultStr);
                            }
                            HandlerHelper.post(new HandlerHelper.onRun() {
                                @Override
                                public void run() {
                                    if(success){
                                        Alert.showTip(getActivity(), "提示", "密码已修改，请用新密码重新登陆", false, new AlertPrompt.OnAlertClickListener() {
                                            @Override
                                            public void onClick() {
                                            Intent intent = new Intent();
                                            Bundle bundle = new Bundle();
                                            bundle.putBoolean(Extras.Extra_Modify_Password_Flag,true);
                                            intent.putExtras(bundle);
                                            getActivity().setResult(Extras.Request_Code_Modify_Password,intent);
                                            ((ModifyInfoActivity) getActivity()).onBack();
                                            }
                                        });
                                    }else{
                                        String msg = "密码修改失败!";
                                        if(TextUtils.isEmpty(tipStr)){
                                            msg = tipStr;
                                        }
                                        Alert.showDefaultToast(getActivity(),msg);
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

        txtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ModifyInfoActivity)getActivity()).onBack();
            }
        });
    }

}
