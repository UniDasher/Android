package com.uni.unidasher.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.igexin.sdk.PushManager;
import com.uni.unidasher.R;
import com.uni.unidasher.chat.applib.controller.HXSDKHelper;
import com.uni.unidasher.chat.ui.DasherHXSDKHelper;
import com.uni.unidasher.chat.ui.activity.ChatAllHistoryFragment;
import com.uni.unidasher.data.DasherMgr;
import com.uni.unidasher.data.DataPrefs;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.api.request.DeviceInfo;
import com.uni.unidasher.data.status.PushCode;
import com.uni.unidasher.ui.fragment.AccountFragment;
import com.uni.unidasher.ui.fragment.ChatFragment;
import com.uni.unidasher.ui.fragment.NearByFragment;
import com.uni.unidasher.ui.fragment.OrderFragment;
import com.uni.unidasher.ui.fragment.ShoppingBasketFragment;
import com.uni.unidasher.ui.utils.Alert;
import com.uni.unidasher.ui.utils.Extras;

import java.util.Hashtable;
import java.util.List;

public class TabActivity extends EventBusActivity implements EMEventListener {
    public static final int QequestCode_SearchAddress = 0;

    public static final int Tab_Nearby = 0;
    public static final int Tab_Order = 1;
    public static final int Tab_Chat = 2;
    public static final int Tab_Account = 3;
    public static final int Tab_ShoppingBasket = 4;

    private Fragment[] fragments;
    private NearByFragment nearByFragment;
    private OrderFragment orderFragment;
    private ChatFragment chatFragment;
    private AccountFragment accountFragment;
    private ChatAllHistoryFragment chatAllHistoryFragment;
    private ShoppingBasketFragment shoppingBasketFragment;
    private int currentTabIndex;
    private int index;
    private TextView[] mTabs;
    private ImageView imgvUnReadChat;
    private DataProvider mDataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        PushManager.getInstance().initialize(this.getApplicationContext());
        PushManager.getInstance().turnOnPush(this);
        mDataProvider = DataProvider.getInstance(this);
        reportDeviceInfo();
        initTab();
        findViews();

//        EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
        EMChat.getInstance().setAppInited();
        mDataProvider.refreshUserInfo();
        dealIntent(this.getIntent());
//        Alert.notifyMsg(this,"this is title","this is notification message!");
    }

    private void findViews(){
        imgvUnReadChat = (ImageView)findViewById(R.id.imgvUnReadChat);
    }

    private void initTab(){
        mTabs = new TextView[5];
        mTabs[0] = (TextView) findViewById(R.id.textNearby);
        mTabs[1] = (TextView) findViewById(R.id.textOrder);
        mTabs[2] = (TextView) findViewById(R.id.textChat);
        mTabs[3] = (TextView) findViewById(R.id.textAccount);
        mTabs[4] = (TextView) findViewById(R.id.textShoppingBasket);
        // 把第一个tab设为选中状态
        mTabs[0].setSelected(true);

        nearByFragment = new NearByFragment();
        orderFragment = new OrderFragment();
        chatFragment = new ChatFragment();
        accountFragment = new AccountFragment();
        chatAllHistoryFragment = new ChatAllHistoryFragment();
        shoppingBasketFragment = new ShoppingBasketFragment();
        fragments = new Fragment[] { nearByFragment, orderFragment, chatAllHistoryFragment, accountFragment, shoppingBasketFragment};
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame,nearByFragment)
//                .add(R.id.content_frame,orderFragment)
                .add(R.id.content_frame,chatFragment)
                .add(R.id.content_frame,accountFragment)
                .hide(orderFragment)
                .hide(chatFragment)
                .hide(accountFragment)
                .show(nearByFragment).commit();
    }

    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.textNearby:
                index = Tab_Nearby;
                break;
            case R.id.textOrder:
			    index = Tab_Order;
                break;
            case R.id.textChat:
                index = Tab_Chat;
                break;
            case R.id.textAccount:
                index = Tab_Account;
                break;
            case R.id.textShoppingBasket:
                index = Tab_ShoppingBasket;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.content_frame, fragments[index]);
            }
            trx.show(fragments[index]).commit();
            if(index == Tab_Order){
                orderFragment.refreshOrderList();
            }
        }
        mTabs[currentTabIndex].setSelected(false);
        // 把当前tab设为选中状态
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    public void refreshTabUI(){
        if(DasherMgr.getInstance(this).isCustomer()){
            mTabs[4].setVisibility(View.VISIBLE);
        }else{
            mTabs[4].setVisibility(View.GONE);
        }
    }

    /**
     * 起价设备基本信息
     */
    public void reportDeviceInfo(){
        String pushId = PushManager.getInstance().getClientid(this);
        Log.i("PushId","----->"+pushId);
        mDataProvider.reportDeviceInfo(new DeviceInfo(pushId));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshShoppingBasketPage();
        refreshChatUI();
        EMChatManager.getInstance().activityResumed();
        EMChatManager.getInstance().registerEventListener(this, new EMNotifierEvent.Event[]{EMNotifierEvent.Event.EventNewMessage});
    }

    @Override
    protected void onStop(){
        EMChatManager.getInstance().unregisterEventListener(this);
        super.onStop();
    }



    /**
     * 监听事件
     */
    @Override
    public void onEvent(EMNotifierEvent event) {
        switch (event.getEvent()) {
            case EventNewMessage: {//普通消息
                EMMessage message = (EMMessage) event.getData();
                //提示新消息
                HXSDKHelper.getInstance().getNotifier().onNewMsg(message);
                refreshChatUI();
                break;
            }
            case EventOfflineMessage:{
                refreshChatUI();
                break;
            }
            default:
                break;
        }
    }

    /**
     * 刷新购物车页面
     */
    public void refreshShoppingBasketPage(){
        if (currentTabIndex == Tab_ShoppingBasket) {
            // 当前页面如果为聊天历史页面，刷新此页面
            if (shoppingBasketFragment != null) {
                shoppingBasketFragment.refreshShopInfo();
            }
        }
    }

    /**
     * 刷新聊天未读消息
     */
    private void refreshChatUI(){
        runOnUiThread(new Runnable() {
            public void run() {
                // 刷新bottom bar消息未读数
                boolean isExist = EMChatManager.getInstance().getUnreadMsgsCount()>0?true:false;
                updateUnreadChat(isExist);
                if (currentTabIndex == Tab_Chat) {
                    // 当前页面如果为聊天历史页面，刷新此页面
                    if (chatAllHistoryFragment != null) {
                        chatAllHistoryFragment.refresh();
                    }
                }
            }
        });
    }

    /**
     * 从账户界面切换身份
     */
    public void switchUserIdentityFromAccount(){
        if (currentTabIndex == Tab_Nearby) {
            // 当前页面如果为聊天历史页面，刷新此页面
            if (nearByFragment != null) {
                nearByFragment.switchUserIdentityFromAccount();
            }
        }else{
            onTabClicked(findViewById(R.id.textNearby));
            if (nearByFragment != null) {
                nearByFragment.switchUserIdentityFromAccount();
            }
        }
    }

    /**
     * 刷新未读消息数
     */
    public void updateUnreadChat(boolean isExist) {
        if (isExist) {
            imgvUnReadChat.setVisibility(View.VISIBLE);
        } else {
            Hashtable<String, EMConversation> conversationList = EMChatManager.getInstance().getAllConversations();
            for(EMConversation conversation:conversationList.values()){
                conversation.resetUnreadMsgCount();
            }
            imgvUnReadChat.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        dealIntent(intent);
    }

    public void dealIntent(Intent intent){
        Log.i("onNewIntent","------------->onNewIntent");
        Bundle bundle = intent.getExtras();
        if(bundle!=null){
            int code = bundle.getInt(PushCode.PushChatExtrasCode,0);
            boolean isCustomer = bundle.getString(PushCode.PushExtrasUserType,"1").equals("1")?true:false;
            boolean currentCustomer = DasherMgr.getInstance(this).isCustomer();
            switch (code){
                case PushCode.Code_Chat_Msg:
                    if(currentTabIndex == Tab_Chat){
                        if (chatAllHistoryFragment != null) {
                            chatAllHistoryFragment.refresh();
                        }
                    }else{
                        onTabClicked(mTabs[Tab_Chat]);
                    }
                    break;
                case PushCode.Code_Order_Received:
                    if(currentTabIndex == Tab_Order){
                        if (orderFragment != null) {
                            orderFragment.refreshOrderList();
                        }
                    }else{
                        onTabClicked(mTabs[Tab_Order]);
                    }
                    break;
                case PushCode.Code_Apply_Sender:
                    if(currentTabIndex == Tab_Order){
                    }else{
                        onTabClicked(mTabs[Tab_Account]);
                    }
                    break;
                case PushCode.Code_Order_Completed:
                case PushCode.Code_Order_TimeOut:
                case PushCode.Code_Order_Complained:
                case PushCode.Code_Order_Cancel_Completed:
                case PushCode.Code_Order_Complained_Completed:
                    if(currentTabIndex == Tab_Order){
                    }else{
                        onTabClicked(mTabs[Tab_Account]);
                    }
                    break;
                case PushCode.Code_Sender_Get_Money:
                    break;
            }

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode== KeyEvent.KEYCODE_BACK)
        {
            finish();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(intent == null){
            return;
        }
        Bundle bundle = intent.getExtras();
        if(bundle == null){
            return;
        }
        switch (resultCode){
            case QequestCode_SearchAddress:
                String searchCity = bundle.getString(AdressSearchActivity.SearchCity);
                String searchKey = bundle.getString(AdressSearchActivity.SearchKey);
                if(!TextUtils.isEmpty(searchCity)&&!TextUtils.isEmpty(searchKey)){
                    nearByFragment.updateSearchAddress(searchCity,searchKey);
                }
                break;
            case Extras.Request_Code_Compain_Or_Complete:
                boolean isUpdateOrder = bundle.getBoolean(Extras.Extra_Compain_Or_Complete_Flag,false);
                if(isUpdateOrder){
                    orderFragment.deleteOrder();
                }
                break;
            case Extras.Request_Code_Modify_Password:
                boolean isModify = bundle.getBoolean(Extras.Extra_Modify_Password_Flag,false);
                if(isModify){
                    DataProvider.getInstance(this).logOut();
                    startActivity(new Intent(this,LoginActivity.class));
                    finish();
                }
                break;
        }
    }
}
