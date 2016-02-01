package com.uni.unidasher.ui.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.eventbus.Subscribe;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.uni.unidasher.AppConstants;
import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.entity.UserInfo;
import com.uni.unidasher.data.event.RefreshUserInfoEvent;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.data.status.Status;
import com.uni.unidasher.ui.activity.ModifyInfoActivity;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.BitmapCompressor;
import com.uni.unidasher.ui.utils.Extras;
import com.uni.unidasher.ui.utils.HandlerHelper;
import com.uni.unidasher.ui.utils.ImageCaptureHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountInfoFragment extends EventBusFragment {
    private TextView txtvBack,txtvNavTitle;
    private View mRootView;
    private TextView txtvUserName,txtvPhone,txtvEmail;
    private CircleImageView imgvLogo;
    private ProgressBar progressBar;
    public static AccountInfoFragment newInstance(String param1, String param2) {
        AccountInfoFragment fragment = new AccountInfoFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public AccountInfoFragment() {
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
        setViews();
        setClickListeners();
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DataProvider.getInstance(getActivity()).refreshUserInfo();
    }

    private void findViews(LayoutInflater inflater, ViewGroup container){
        mRootView = inflater.inflate(R.layout.fragment_acount_info, container, false);
        txtvUserName = (TextView)mRootView.findViewById(R.id.txtvUserName);
        txtvPhone = (TextView)mRootView.findViewById(R.id.txtvPhone);
//        txtvEmail = (TextView)mRootView.findViewById(R.id.txtvEmail);
        imgvLogo = (CircleImageView)mRootView.findViewById(R.id.imgvLogo);
        progressBar = (ProgressBar)mRootView.findViewById(R.id.progressBar);
        txtvBack = (TextView)mRootView.findViewById(R.id.txtvBack);
        txtvNavTitle = (TextView)mRootView.findViewById(R.id.txtvNavTitle);
        txtvNavTitle.setText("账号信息");
    }

    private void setClickListeners(){
        txtvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity()!=null){
                    ((ModifyInfoActivity)getActivity()).updateInfo(Status.User.Modify_User_NickName);
                }
            }
        });
        txtvPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getActivity()!=null){
                    ((ModifyInfoActivity)getActivity()).updateInfo(Status.User.Modify_User_Phone);
                }
            }
        });

        txtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ModifyInfoActivity)getActivity()).onBack();
            }
        });

        imgvLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfilePicture();
            }
        });
    }

    private void setViews(){
        UserInfo userInfo = DataProvider.getInstance(getActivity()).getUserInfo();
        if(userInfo == null)
            return;
        txtvUserName.setText(userInfo.getNickName());
        txtvPhone.setText(userInfo.getMobilePhone());
//        txtvEmail.setText(userInfo.getEmail());
        downLogo(AppConstants.HOST+userInfo.getLogo());
    }

    public void downLogo(String path){
            ImageLoader.getInstance().displayImage(path, imgvLogo, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    super.onLoadingStarted(imageUri, view);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    super.onLoadingFailed(imageUri, view, failReason);
                    progressBar.setVisibility(View.GONE);
                }
            });
            }

    private void editProfilePicture() {
        ArrayAdapter adapter = new ArrayAdapter<String>(getActivity(), R.layout.dialog_take_photo, new String[]{
                "拍照",
                "本地相册选择",
                getResources().getString(R.string.cancel)}) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }

        };
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case Extras.Request_Code_Take_Photo:
                                startTakePhotoIntent();
                                break;
                            case Extras.Request_Code_Pick_Photo:
                                startPickPhotoIntent();
                                break;
                        }
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.show();
    }

    private void startTakePhotoIntent() {
        try {
            ImageCaptureHelper.launchCameraApp(Extras.Request_Code_Take_Photo, getActivity(), null);
        } catch (Exception e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
        }
    }

    private void startPickPhotoIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "select pic"), Extras.Request_Code_Pick_Photo);
    }

    private void resizeAndSendPhoto(String path) {
        String resizedImagePath = getActivity().getFilesDir() + "/avatar.jpg";
        File resizedFile = new File(resizedImagePath);
        try {
            BitmapCompressor.compressImageToFile(path, resizedFile);
            uploadUserLogo(resizedImagePath);
//            imgvPhoto.setImageBitmap(getLoacalBitmap(resizedImagePath));
//            DataProvider.getInstance(this).updateProfilePicture(resizedImagePath);
        } catch (IOException ioe) {
            Toast.makeText(getActivity(), "图片过程中，出现了问题!", Toast.LENGTH_LONG).show();
        }
    }

    public void uploadUserLogo(String path) {
        DataProvider.getInstance(getActivity()).uploadUserLogo(path, new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("uploadUserLogo", success + "-->" + resultStr);
                }
                String logoUrl = "";
                if (success) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultStr);
                        if (jsonObject != null) {
                            logoUrl = jsonObject.optString("fileName", "");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    DataProvider.getInstance(getActivity()).refreshUserInfo();
//                    final String lu = logoUrl;
//                    HandlerHelper.post(new HandlerHelper.onRun() {
//                        @Override
//                        public void run() {
//                            downLogo(AppConstants.HOST + lu);
//                        }
//                    });

                } else {
                    HandlerHelper.post(new HandlerHelper.onRun() {
                        @Override
                        public void run() {
                            Alert.showTip(getActivity(), "提示", "头像上传失败",true,null);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Extras.Request_Code_Pick_Photo:
                if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String photoPath = cursor.getString(columnIndex);
                    cursor.close();

                    resizeAndSendPhoto(photoPath);
//                    setPhoto();
                }
                break;
            case Extras.Request_Code_Take_Photo:
                if (resultCode == Activity.RESULT_OK) {

                    File tempCamPhotoFile = ImageCaptureHelper.retrievePhotoResult(getActivity(), null);
                    String photoPath = tempCamPhotoFile.getPath();

                    resizeAndSendPhoto(photoPath);
//                    setPhoto();
                }
                break;
//            case CANCEL:
//                break;
        }

    }

    @Subscribe
    public void onRefreshInfoEvent(RefreshUserInfoEvent refreshUserInfoEvent) {
        HandlerHelper.post(new HandlerHelper.onRun() {
            @Override
            public void run() {
                setViews();
            }
        });
    }

}
