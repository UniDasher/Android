package com.uni.unidasher.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.uni.unidasher.AppConstants;
import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.datamodel.databaseAndApi.DishInfo;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.data.utils.Constants;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.BitmapCompressor;
import com.uni.unidasher.ui.utils.Extras;
import com.uni.unidasher.ui.utils.HandlerHelper;
import com.uni.unidasher.ui.utils.ImageCaptureHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class UploadPhotoActivity extends EventBusActivity {

    private TextView txtvNext;
    private CircleImageView imgvPhoto;
    private ProgressBar progressBar;
    private DataProvider mDataProvider;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        mDataProvider = DataProvider.getInstance(this);
        findViews();
        setClickListeners();
    }

    private void findViews(){
        txtvNext = (TextView)findViewById(R.id.txtvNext);
        imgvPhoto = (CircleImageView)findViewById(R.id.imgvLogo);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        downLogo(mDataProvider.getUserLogo());
    }

    private void setClickListeners(){
        imgvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfilePicture();
            }
        });
        txtvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadPhotoActivity.this,TabActivity.class));
                finish();
            }
        });
    }

    private void downLogo(final String path){
        HandlerHelper.post(new HandlerHelper.onRun() {
            @Override
            public void run() {
                ImageLoader.getInstance().displayImage(path,imgvPhoto,new SimpleImageLoadingListener(){
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        super.onLoadingComplete(imageUri, view, loadedImage);
                        progressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        super.onLoadingFailed(imageUri, view, failReason);
                        progressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void editProfilePicture() {
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.dialog_take_photo, new String[]{
                "拍照",
                "本地相册选择",
                getResources().getString(R.string.cancel)}) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return super.getView(position, convertView, parent);
            }

        };
        AlertDialog alertDialog = new AlertDialog.Builder(this)
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
            ImageCaptureHelper.launchCameraApp(Extras.Request_Code_Take_Photo, this, null);
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
        String resizedImagePath = getFilesDir() + "/avatar.jpg";
        File resizedFile = new File(resizedImagePath);
        try {
            BitmapCompressor.compressImageToFile(path, resizedFile);
            uploadUserLogo(resizedImagePath);
//            imgvPhoto.setImageBitmap(getLoacalBitmap(resizedImagePath));
//            DataProvider.getInstance(this).updateProfilePicture(resizedImagePath);
        } catch (IOException ioe) {
            Toast.makeText(this, "图片过程中，出现了问题!", Toast.LENGTH_LONG).show();
        }
    }

    public void uploadUserLogo(String path){
        progressDialog = ProgressDialog.show(UploadPhotoActivity.this, "", "Please wait...", true, true);
        DataProvider.getInstance(this).uploadUserLogo(path, new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if(BuildConfig.DEBUG){
                    Log.i("uploadUserLogo",success+"-->"+resultStr);
                }
                String logoUrl = "";
                if(success){
                    try{
                        JSONObject jsonObject = new JSONObject(resultStr);
                        if(jsonObject!=null){
                               logoUrl = jsonObject.optString("fileName","");
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    downLogo(AppConstants.HOST+logoUrl);
                }else{
                    HandlerHelper.post(new HandlerHelper.onRun() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            Alert.showTip(UploadPhotoActivity.this,"提示","头像上传失败",true,null);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Extras.Request_Code_Pick_Photo:
                if (resultCode == Activity.RESULT_OK  && data != null && data.getData() != null) {

                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
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

                    File tempCamPhotoFile = ImageCaptureHelper.retrievePhotoResult(this, null);
                    String photoPath = tempCamPhotoFile.getPath();

                    resizeAndSendPhoto(photoPath);
//                    setPhoto();
                }
                break;
//            case CANCEL:
//                break;
        }

    }

    public static Bitmap getLoacalBitmap(String url) {
        try {
            return BitmapFactory.decodeFile(url);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
