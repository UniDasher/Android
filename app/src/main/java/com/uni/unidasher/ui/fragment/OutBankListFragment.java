package com.uni.unidasher.ui.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.R;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.entity.EarnRecordInfo;
import com.uni.unidasher.data.entity.OutInfo;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.data.utils.Constants;
import com.uni.unidasher.ui.dialog.DatePicker;
import com.uni.unidasher.ui.utils.HandlerHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OutBankListFragment extends EventBusFragment {

    private View mRootView;
    private ListView listView;
    private OutAdapter adapter;
    private LayoutInflater inflater;
    private DataProvider mDataProvider;
    private TextView txtvStartDate,txtvEndDate,txtvConfirm;
    private LinearLayout layoutEmpty;
    public static OutBankListFragment newInstance(String param1, String param2) {
        OutBankListFragment fragment = new OutBankListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public OutBankListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        mDataProvider = DataProvider.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        findViews(inflater, container);
        setViews();
        setClickListeners();
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
    }



    private void findViews(LayoutInflater inflater, ViewGroup container){
        this.inflater = inflater;
        mRootView = inflater.inflate(R.layout.fragment_out_bank_list, container, false);
        txtvStartDate = (TextView)mRootView.findViewById(R.id.txtvStartDate);
        txtvEndDate = (TextView)mRootView.findViewById(R.id.txtvEndDate);
        txtvConfirm = (TextView)mRootView.findViewById(R.id.txtvConfirm);
        layoutEmpty = (LinearLayout)mRootView.findViewById(R.id.layoutEmpty);

        listView = (ListView)mRootView.findViewById(R.id.listView);
        adapter = new OutAdapter();
        listView.setAdapter(adapter);
    }

    private void setViews(){
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTimeInMillis(date.getTime());
        int endYear = calendar.get(Calendar.YEAR);
        int endMonth = calendar.get(Calendar.MONTH)+1;
        int endDay = calendar.get(Calendar.DAY_OF_MONTH);
        txtvEndDate.setText(endYear+"-"+String.format("%02d", endMonth)+"-"+String.format("%02d", endDay));

        calendar.add(Calendar.DAY_OF_MONTH, -6);
        int startYear = calendar.get(Calendar.YEAR);
        int startMonth = calendar.get(Calendar.MONTH)+1;
        int startDay = calendar.get(Calendar.DAY_OF_MONTH);
        txtvStartDate.setText(startYear + "-" + String.format("%02d", startMonth) + "-" + String.format("%02d", startDay));
    }

    public void setClickListeners(){
        txtvStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertPicker(true);
            }
        });
        txtvEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertPicker(false);
            }
        });
        txtvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
            }
        });
    }

    public void getData(){
        String startDate = txtvStartDate.getText().toString();
        String endDate = txtvEndDate.getText().toString();
        mDataProvider.retrieveOutList(startDate, endDate, new RESTManager.OnObjectDownloadedListener() {
            @Override
            public void onObjectDownloaded(boolean success, String resultStr, String tipStr) {
                if (BuildConfig.DEBUG) {
                    Log.i("retrieveOutList", success + "--->" + resultStr);
                }
                final List<OutInfo> outInfoList = new ArrayList<OutInfo>();
                if (success) {
                    try {
                        JSONObject jsonObject = new JSONObject(resultStr);
                        JSONArray dataArray = jsonObject.optJSONArray("list");
                        if (dataArray != null) {
                            for (int i = 0; i < dataArray.length(); i++) {
                                outInfoList.add(Constants.GSON_RECEIVED.fromJson(dataArray.optJSONObject(i).toString(), OutInfo.class));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                HandlerHelper.post(new HandlerHelper.onRun() {
                    @Override
                    public void run() {
                        if(outInfoList!=null&&outInfoList.size()>0){
                            listView.setVisibility(View.VISIBLE);
                            layoutEmpty.setVisibility(View.GONE);
                            refresh(outInfoList);
                        }else{
                            listView.setVisibility(View.GONE);
                            layoutEmpty.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    public void alertPicker(final boolean isStart){
        String value = isStart?txtvStartDate.getText().toString():txtvEndDate.getText().toString();
        String values[] = value.split("-");
        int year = Integer.parseInt(values[0]);
        int month = Integer.parseInt(values[1]);
        int day = Integer.parseInt(values[2]);
        new DatePicker(getActivity(), year, month, day, new DatePicker.OnDateSelectListener() {
            @Override
            public void onSelect(String year, String month, String day) {
                String date = year+"-"+String.format("%02d", Integer.parseInt(month))+"-"+String.format("%02d", Integer.parseInt(day));
                if(isStart){
                    txtvStartDate.setText(date);
                }else {
                    txtvEndDate.setText(date);
                }
            }
        }).show();
    }

    public void refresh(List<OutInfo> outInfoList){
        if(outInfoList!=null&&outInfoList.size()>0){
            adapter.refreshData(outInfoList);
        }
    }

    class OutAdapter extends BaseAdapter{
        private List<OutInfo> outInfoList = new ArrayList<OutInfo>();

        public void refreshData(List<OutInfo> ls){
            if(ls == null){
                outInfoList = new ArrayList<OutInfo>();
            }else{
                outInfoList = ls;
            }
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return outInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return outInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView txtvBankNum,txtvDate,txtvMoney;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.adapter_listview_out,parent,false);
            }
            txtvBankNum = (TextView)convertView.findViewById(R.id.txtvBankNum);
            txtvDate = (TextView)convertView.findViewById(R.id.txtvDate);
            txtvMoney = (TextView)convertView.findViewById(R.id.txtvMoney);
            OutInfo outInfo = outInfoList.get(position);
            txtvBankNum.setText(outInfo.getSettleNumber());
            txtvDate.setText(outInfo.getCreateDate().substring(0,19));
            txtvMoney.setText(outInfo.getCreateDate());
            return convertView;
        }
    }

}
