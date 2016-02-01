package com.uni.unidasher.ui.activity.restaurant;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.eventbus.Subscribe;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.database.DatabaseHelper;
import com.uni.unidasher.data.datamodel.databaseAndApi.DishInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.UserAdressInfo;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShoppingBasket;
import com.uni.unidasher.data.datamodel.databaseAndApi.ShopInfo;
import com.uni.unidasher.data.entity.shop.TypeInfo;
import com.uni.unidasher.data.event.ShopDishEvent;
import com.uni.unidasher.data.utils.BeanUtil;
import com.uni.unidasher.listener.OnTakeDishListener;
import com.uni.unidasher.ui.adapter.DishListContentAdapter;
import com.uni.unidasher.ui.adapter.DishTypeAdapter;
import com.uni.unidasher.ui.dialog.AlertConfirmCancel;
import com.uni.unidasher.ui.dialog.OrderConfirmDialog;
import com.uni.unidasher.ui.fragment.EventBusFragment;
import com.uni.unidasher.ui.utils.CommonUtils;
import com.uni.unidasher.ui.utils.Extras;
import com.uni.unidasher.ui.utils.HandlerHelper;
import com.uni.unidasher.ui.widget.CustomDrawerLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/6/6.
 */
public class DishListFragment extends EventBusFragment {
    private View mRootView;
    private TextView txtvPlaceOrder;
    private ListView listvDishType;
    private ListView listvDish;
    private DishTypeAdapter dishTypeAdapter;
    private DishListContentAdapter dishListContentAdapter;
    private ShopInfo shopInfo;
    private DataProvider mDataProvider;
    private Map<Integer,Object> dishFilterType = new HashMap<>();
    private int currentType = 0;
    private TextView txtvTotalPrice;
    private TextView txtvTotalCount;
    private boolean isSameShopFlag = false;
    private TextView txtvTip;
    private CustomDrawerLayout mDrawer;
    private RelativeLayout rightDrawerLayout;

    public static DishListFragment newInstance(ShopInfo shopInfo){
        DishListFragment fragment = new DishListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Extras.Extra_ShopInfo,shopInfo);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataProvider = DataProvider.getInstance(getActivity());
        Bundle bundle = getArguments();
        if(bundle!=null&&bundle.containsKey(Extras.Extra_ShopInfo)){
            shopInfo = (ShopInfo)bundle.getSerializable(Extras.Extra_ShopInfo);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        findViews(inflater, container);
        setViews();
        setClickListeners();
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(shopInfo!=null){
            mDataProvider.retrieveShopDishList(shopInfo.getSid());
        }
    }

    private void findViews(LayoutInflater inflater, ViewGroup container) {
        mRootView = inflater.inflate(R.layout.fragment_menu_list, container, false);
        mDrawer = (CustomDrawerLayout)mRootView.findViewById(R.id.drawer);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        rightDrawerLayout = (RelativeLayout)mRootView.findViewById(R.id.right_drawer_layout);
        txtvTotalPrice = (TextView)mRootView.findViewById(R.id.txtvTotalPrice);
        txtvTotalCount = (TextView)mRootView.findViewById(R.id.txtvTotalCount);
        txtvPlaceOrder = (TextView)mRootView.findViewById(R.id.txtvPlaceOrder);
        listvDishType = (ListView)mRootView.findViewById(R.id.listvMenuType);
        listvDish = (ListView)mRootView.findViewById(R.id.listvMenuContent);
        txtvTip = (TextView)mRootView.findViewById(R.id.txtvTip);

        dishTypeAdapter = new DishTypeAdapter(getActivity());
        listvDishType.setAdapter(dishTypeAdapter);
        dishListContentAdapter = new DishListContentAdapter(getActivity());
        listvDish.setAdapter(dishListContentAdapter);

    }

    private void setViews(){
        if(shopInfo!=null){
            if(shopInfo.isOpenService()&& !TextUtils.isEmpty(CommonUtils.isCurrentInTimePeriod(shopInfo.getServiceTimes()))){
                txtvTip.setVisibility(View.GONE);
                txtvPlaceOrder.setVisibility(View.VISIBLE);
            }else{
                txtvTip.setVisibility(View.VISIBLE);
                txtvPlaceOrder.setVisibility(View.GONE);
            }

        }
        isSameShopFlag = isSameShop();
        refreshBottomTotalCountAndPrice();
    }

    private void setClickListeners(){
        /**
         * 下单
         */
        txtvPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtvTotalCount.getText().equals("0")){
                    startActivityForResult(new Intent(getActivity(),OrderPayActivity.class),Extras.Request_Code_Update_Dishes_Of_Pay);
                }
            }
        });

        listvDishType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                if(currentType == position){
//                    return;
//                }
                currentType = position;
                showTypeDishList(currentType);
            }
        });

        dishListContentAdapter.setOnTakeDishListener(new OnTakeDishListener() {
            @Override
            public void onAdd(final int position) {
                if(!isSameShopFlag){
                    new AlertConfirmCancel(getActivity(), "", "购物篮有已点菜品，是否清空？", new AlertConfirmCancel.OnAlertClickListener() {
                        @Override
                        public void onConfirm() {
                            mDataProvider.clearShoppingBasket(new DatabaseHelper.OnDatabaseInsertedListener() {
                                @Override
                                public void onDatabaseInserted() {
                                    isSameShopFlag = true;
                                    updateShoppingBasketShopInfo(new DatabaseHelper.OnDatabaseInsertedListener() {
                                        @Override
                                        public void onDatabaseInserted() {
                                            HandlerHelper.post(new HandlerHelper.onRun() {
                                                @Override
                                                public void run() {
                                                    refreshBottomTotalCountAndPrice();
                                                    doDish(position, true);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                        @Override
                        public void onCancel() {

                        }
                    }).show();
                }else{
                    doDish(position, true);
                }
            }

            @Override
            public void onRemove(int position) {
                DishInfo dishInfo = (DishInfo)dishListContentAdapter.getItem(position);
                if(dishInfo.getCount()>0){
                    doDish(position, false);
                }
            }
        });

//        mDrawer.setDrawerListener(new DrawerLayout.DrawerListener() {
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//            }
//
//            @Override
//            public void onDrawerStateChanged(int newState) {
//
//            }
//        });
    }

    /**
     * 判断购物篮信息是否与此餐馆一致
     * @return
     */
    private boolean isSameShop(){
        boolean isFlag;
        ShoppingBasket shoppingBasket = mDataProvider.getShoppingBasket();
        if(shoppingBasket!=null){
            if(shoppingBasket.getShopInfo() == null || shoppingBasket.getShopInfo().getSid()==null || shoppingBasket.getShopInfo().getSid().equals(shopInfo.getSid())){
                isFlag = true;
                updateShoppingBasketShopInfo(null);
            }else{
                if(shoppingBasket.getDishInfos()==null||shoppingBasket.getDishInfos().size()==0){
                    updateShoppingBasketShopInfo(null);
                    isFlag = true;
                }else{
                    isFlag = false;
                }
            }
        }else{
            updateShoppingBasketShopInfo(null);
            isFlag = true;
        }
        return isFlag;
    }

    /**
     * 更新购物篮商家信息
     */
    private void updateShoppingBasketShopInfo(DatabaseHelper.OnDatabaseInsertedListener listener){
        if(shopInfo==null)
            return;
        shopInfo.setUserId(mDataProvider.getUserId());
        mDataProvider.getDatabaseHelper().insertIfNotExistsOrUpdate(shopInfo,null);
        ShoppingBasket shoppingBasket = mDataProvider.getShoppingBasket();
        if(shoppingBasket==null){
            shoppingBasket = new ShoppingBasket();
        }
        shoppingBasket.setShopInfo(shopInfo);
        mDataProvider.updateShoppingBasket(shoppingBasket,listener);
    }

    /**
     * 刷新已点数量和价格
     */
    public synchronized void refreshBottomTotalCountAndPrice(){
        int totalCount = 0;
        int totalPrice = 0;
        if(isSameShopFlag){
            ShoppingBasket shoppingBasket = mDataProvider.getShoppingBasket();
            if (shoppingBasket!=null&&shoppingBasket.getDishInfos()!=null&&shoppingBasket.getDishInfos().size()>0){
                List<DishInfo> shoppingDishInfos = shoppingBasket.getDishInfos();
                for(DishInfo dishInfo:shoppingDishInfos){
                    totalCount+=dishInfo.getCount();
                    totalPrice+=(dishInfo.getCount()*dishInfo.getPrice());
                }
            }
        }
        txtvTotalPrice.setText(totalPrice + "");
        txtvTotalCount.setText(totalCount + "");
    }

    /**
     * 增加或减少菜品-------点菜
     * @param position
     * @param isAdd
     */
    public synchronized void doDish(final int position, final boolean isAdd){
        DishInfo dishInfo = BeanUtil.cloneTo((DishInfo)dishListContentAdapter.getItem(position));
        ShoppingBasket shoppingBasket = mDataProvider.getShoppingBasket();
        if (shoppingBasket!=null){
            List<DishInfo> shoppingDishInfos = shoppingBasket.getDishInfos();
            if(shoppingDishInfos!=null){
                boolean isExist = false;
                for(DishInfo d:shoppingDishInfos){//判断购物篮是否存在此类菜品
                    if(dishInfo.getDishId().equals(d.getDishId())){
                        int count = d.getCount();
                        if(isAdd){
                            count++;
                        }else{
                            if(count > 0){
                                count --;
                            }
                        }
                        d.setCount(count);
                        isExist = true;
                        break;
                    }
                }
                if(!isExist && isAdd){
                    int count = dishInfo.getCount();
                    count++;
                    dishInfo.setCount(count);
                    shoppingDishInfos.add(dishInfo);
                }
            }else{
                shoppingDishInfos = new ArrayList<>();
                int count = dishInfo.getCount();
                count++;
                dishInfo.setCount(count);
                shoppingDishInfos.add(dishInfo);
            }
            shoppingBasket.setDishInfos(shoppingDishInfos);
        }else{
            shoppingBasket = new ShoppingBasket();
            List<DishInfo> dishInfos = new ArrayList<>();
            int count = dishInfo.getCount();
            count++;
            dishInfo.setCount(count);
            dishInfos.add(dishInfo);
            shoppingBasket.setDishInfos(dishInfos);
        }
        mDataProvider.updateShoppingBasket(shoppingBasket, new DatabaseHelper.OnDatabaseInsertedListener() {
            @Override
            public void onDatabaseInserted() {
                HandlerHelper.post(new HandlerHelper.onRun() {
                    @Override
                    public void run() {
                        if (isAdd) {
                            dishListContentAdapter.add(position);
                        } else {
                            dishListContentAdapter.remove(position);
                        }
                        refreshBottomTotalCountAndPrice();
                    }
                });
            }
        });

    }


    /**
     * 展示对应类型的菜品
     * @param position
     */
    private void showTypeDishList(int position){
        int typeId = ((TypeInfo)dishTypeAdapter.getItem(position)).getId();
        ArrayList<DishInfo> dishInfos = BeanUtil.cloneTo((ArrayList<DishInfo>) dishFilterType.get(typeId));
        ShoppingBasket shoppingBasket = mDataProvider.getShoppingBasket();
        if (isSameShopFlag&&shoppingBasket!=null&&shoppingBasket.getDishInfos()!=null&&shoppingBasket.getDishInfos().size()>0){
            List<DishInfo> shoppingDishInfos = shoppingBasket.getDishInfos();
            List<DishInfo> ls = BeanUtil.cloneTo(shoppingDishInfos);
            boolean isUpdateShoppingBasket = false;
            for(DishInfo d:ls){
                if(typeId == d.getTypeId()){
                    boolean isExist = false;
                    for(DishInfo dishInfo:dishInfos){
                        if(dishInfo.getDishId().equals(d.getDishId())){
                            dishInfo.setCount(d.getCount());
                            isExist = true;
                            break;
                        }
                    }
                    if(!isExist){
                        //购物车数据与获取菜单数据不统一，数据有异
                        isUpdateShoppingBasket = true;
                        shoppingDishInfos.remove(d);
                    }
                }
            }
            if(isUpdateShoppingBasket){
                shoppingBasket.setDishInfos(shoppingDishInfos);
                mDataProvider.updateShoppingBasket(shoppingBasket,null);
            }
        }
        dishListContentAdapter.refreshData(dishInfos);
//        dishTypeAdapter.refreshState(position);
        mDrawer.openDrawer(rightDrawerLayout);
    }

    /**
     * 更新当前类型菜品数量
     */
    public void updateDishList(){
        HandlerHelper.post(new HandlerHelper.onRun() {
            @Override
            public void run() {
                showTypeDishList(currentType);
                refreshBottomTotalCountAndPrice();
            }
        });
    }

    public boolean isOpenDrawer(){
        return mDrawer.isDrawerOpen(rightDrawerLayout);
    }

    public void closeDrawer(){
        HandlerHelper.post(new HandlerHelper.onRun() {
            @Override
            public void run() {
                mDrawer.closeDrawer(rightDrawerLayout);
            }
        });
    }

    @Subscribe
    public void onDishInfoListEvent(final ShopDishEvent shopDishEvent){
        if( shopDishEvent!=null
            &&shopDishEvent.getShopDishList()!=null
            &&shopDishEvent.getShopDishList().getDishInfos()!=null
            &&shopDishEvent.getShopDishList().getDishInfos().size()>0
            &&shopDishEvent.getShopDishList().getTypeInfos()!=null
            &&shopDishEvent.getShopDishList().getTypeInfos().size()>0){
            ArrayList<DishInfo> dishInfos = shopDishEvent.getShopDishList().getDishInfos();
            ArrayList<TypeInfo> typeInfos = shopDishEvent.getShopDishList().getTypeInfos();

            //菜品分类
            for(TypeInfo typeInfo:typeInfos){
                ArrayList<DishInfo> mapDish = new ArrayList<>();
                for(DishInfo dishInfo:dishInfos){
                    if(dishInfo.getTypeId() == typeInfo.getId()){
                        mapDish.add(dishInfo);
                    }
                }
                dishFilterType.put(typeInfo.getId(),mapDish);
            }

            //匹配购物篮菜品类型与最新商家的菜品类型是否一致
            ShoppingBasket shoppingBasket = mDataProvider.getShoppingBasket();
            if (isSameShopFlag&&shoppingBasket!=null&&shoppingBasket.getDishInfos()!=null&&shoppingBasket.getDishInfos().size()>0){
                List<DishInfo> shoppingDishInfos = shoppingBasket.getDishInfos();
                List<DishInfo> ls = BeanUtil.cloneTo(shoppingDishInfos);
                boolean isUpdateShoppingBasket = false;
                for(DishInfo d:ls){
                    boolean isExistType = false;
                    for(TypeInfo typeInfo:typeInfos){
                        if(d.getTypeId() == typeInfo.getId()){
                            isExistType = true;
                            break;
                        }
                    }
                    if(!isExistType){
                        isUpdateShoppingBasket = true;
                        shoppingDishInfos.remove(d);
                    }
                }
                if(isUpdateShoppingBasket){
                    shoppingBasket.setDishInfos(shoppingDishInfos);
                    mDataProvider.updateShoppingBasket(shoppingBasket,null);
                }
            }

            //数据刷新
            HandlerHelper.post(new HandlerHelper.onRun() {
                @Override
                public void run() {
                    dishTypeAdapter.refreshData(shopDishEvent.getShopDishList().getTypeInfos());
//                    showTypeDishList(currentType);
                }
            });
        }

    }

}
