package com.uni.unidasher.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.uni.unidasher.R;

import java.util.ArrayList;
import java.util.List;

public class AdressSearchActivity extends EventBusActivity implements OnGetSuggestionResultListener{
    public static final String City = "city";
    public static final String SearchCity = "SearchCity";
    public static final String SearchKey = "SearchKey";


    private TextView txtvBack,txtvNavTitle;
    private ListView listvAddress;
    private EditText etxtAddress;
    private SuggestionSearch suggestionSearch;
    private LayoutInflater inflater;
    private AdressAdapter adapter;
    private String currentCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adress_search);
        inflater = LayoutInflater.from(this);
        currentCity = getIntent().getStringExtra(City);
        suggestionSearch = SuggestionSearch.newInstance();
        findViews();
        setClickListeners();
    }

    private void findViews(){
        txtvBack = (TextView)findViewById(R.id.txtvBack);
        txtvNavTitle = (TextView)findViewById(R.id.txtvNavTitle);
        listvAddress = (ListView)findViewById(R.id.listvAddress);
        etxtAddress = (EditText)findViewById(R.id.etxtAddress);
        txtvNavTitle.setText("地址查询");
        adapter = new AdressAdapter();
        listvAddress.setAdapter(adapter);
    }

    private void setClickListeners(){
        suggestionSearch.setOnGetSuggestionResultListener(this);
        txtvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        etxtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = etxtAddress.getText().toString();
                if (TextUtils.isEmpty(keyword)) {
                    adapter.refresh(new ArrayList<SuggestionResult.SuggestionInfo>());
                } else {
                    if(!TextUtils.isEmpty(currentCity)){
                        suggestionSearch.requestSuggestion(new SuggestionSearchOption().keyword(keyword).city(currentCity));
                    }
                }
            }
        });

        listvAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SuggestionResult.SuggestionInfo suggestionInfo = (SuggestionResult.SuggestionInfo)adapter.getItem(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString(SearchCity, suggestionInfo.city);
                bundle.putString(SearchKey, suggestionInfo.district + suggestionInfo.key);
                intent.putExtras(bundle);
                setResult(TabActivity.QequestCode_SearchAddress, intent);
                finish();
            }
        });
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        if (suggestionResult != null && suggestionResult.getAllSuggestions()!=null&&suggestionResult.getAllSuggestions().size()>0 ) {
            adapter.refresh(suggestionResult.getAllSuggestions());
        }else{
            adapter.refresh(new ArrayList<SuggestionResult.SuggestionInfo>());
        }
    }

    class AdressAdapter extends BaseAdapter{
        private List<SuggestionResult.SuggestionInfo> listData = new ArrayList<>();

        public void refresh(List<SuggestionResult.SuggestionInfo> ls){
            listData = ls;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.listview_address_search_layout,null);
                holder = new ViewHolder();
                holder.txtvAddress = (TextView)convertView.findViewById(R.id.txtvAddress);
                holder.txtvCity = (TextView)convertView.findViewById(R.id.txtvCity);
                holder.txtvDistrict = (TextView)convertView.findViewById(R.id.txtvDistrict);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            SuggestionResult.SuggestionInfo suggestionInfo = listData.get(position);
            holder.txtvAddress.setText(suggestionInfo.key);
            holder.txtvCity.setText(suggestionInfo.city);
            holder.txtvDistrict.setText(suggestionInfo.district);
            return convertView;
        }

        class ViewHolder{
            TextView txtvAddress;
            TextView txtvCity;
            TextView txtvDistrict;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        suggestionSearch.destroy();
    }
}
