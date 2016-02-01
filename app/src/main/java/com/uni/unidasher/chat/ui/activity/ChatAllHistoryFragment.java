package com.uni.unidasher.chat.ui.activity;

import java.lang.Exception;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.google.common.eventbus.Subscribe;
import com.uni.unidasher.BuildConfig;
import com.uni.unidasher.R;
import com.uni.unidasher.chat.ui.Constant;
import com.uni.unidasher.DasherApplication;
import com.uni.unidasher.chat.ui.adapter.ChatAllHistoryAdapter;
import com.uni.unidasher.data.DasherMgr;
import com.uni.unidasher.data.DataProvider;
import com.uni.unidasher.data.entity.ChatInfo;
import com.uni.unidasher.data.entity.OrderInfo;
import com.uni.unidasher.data.event.ReOrderListEvent;
import com.uni.unidasher.data.event.ReOrderListForChatEvent;
import com.uni.unidasher.data.rest.RESTManager;
import com.uni.unidasher.data.status.Status;
import com.uni.unidasher.data.utils.Constants;
import com.uni.unidasher.ui.activity.TabActivity;
import com.uni.unidasher.ui.adapter.ChatHistoryListAdapter;
import com.uni.unidasher.ui.fragment.EventBusFragment;
import com.uni.unidasher.ui.utils.Extras;
import com.uni.unidasher.ui.utils.HandlerHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 显示所有会话记录，比较简单的实现，更好的可能是把陌生人存入本地，这样取到的聊天记录是可控的
 *
 */
public class ChatAllHistoryFragment extends EventBusFragment {

	private ListView listView;
	private ChatAllHistoryAdapter adapter;
	public RelativeLayout errorItem;
	public TextView errorText;
	private boolean hidden;
	private List<EMConversation> conversationList = new ArrayList<EMConversation>();
	private DataProvider mDataProvider;
	private DasherMgr mDasherMgr;
	private ArrayList<OrderInfo> orderInfos = new ArrayList<>();
	private boolean isCustomer;
	private ArrayList<ChatInfo> chatInfos = new ArrayList<>();
	private Map<String,ChatInfo> mapChatInfo = new HashMap<>();
	private ChatHistoryListAdapter chatHistoryListAdapter;
	private LinearLayout layoutEmpty;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDasherMgr = DasherMgr.getInstance(getActivity());
		mDataProvider = DataProvider.getInstance(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_conversation_history, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
		layoutEmpty = (LinearLayout)getView().findViewById(R.id.layoutEmpty);
		listView = (ListView) getView().findViewById(R.id.list);
//		adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
		chatHistoryListAdapter = new ChatHistoryListAdapter(getActivity());
		// 设置adapter
		listView.setAdapter(chatHistoryListAdapter);
		refresh();

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ChatInfo chatInfo = (ChatInfo)chatHistoryListAdapter.getItem(position);
				String username = chatInfo.getChatId();
				// 进入聊天页面
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				intent.putExtra(Extras.Extra_Chat_ID, username);
				intent.putExtra(Extras.Extra_Chat_Order_ID,chatInfo.getOrderId());
				intent.putExtra(Extras.Extra_Chat_Name, chatInfo.getChatName());
				intent.putExtra(Extras.Extra_Chat_Logo, chatInfo.getChatLogo());
				getActivity().startActivity(intent);
			}
		});
		// 注册上下文菜单
		registerForContextMenu(listView);
	}

	public void refreshActiveOrderListForChat(){
		isCustomer = mDasherMgr.isCustomer();
		mDataProvider.retrieveActiveOrderListForChat(Status.Order.Status_In_Progress,isCustomer);
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		refreshActiveOrderListForChat();
	}

	private void loadConversationsWithRecentChat() {

		if(orderInfos ==null || orderInfos.size() == 0){
			layoutEmpty.setVisibility(View.VISIBLE);
			listView.setVisibility(View.GONE);
			((TabActivity)getActivity()).updateUnreadChat(false);
			return;
		}

		layoutEmpty.setVisibility(View.GONE);
		listView.setVisibility(View.VISIBLE);
		// 获取所有会话，包括陌生人
		Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
		// 过滤掉messages size为0的conversation
		/**
		 * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
		 * 影响排序过程，Collection.sort会产生异常
		 * 保证Conversation在Sort过程中最后一条消息的时间不变
		 * 避免并发问题
		 */
		List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
		synchronized (conversations) {
			if(conversations.size() != 0){
				for(OrderInfo orderInfo:orderInfos) {
					String chatId = isCustomer ? orderInfo.getSenderId() : orderInfo.getUid();
					String orderId = orderInfo.getMid();
					EMConversation conversation = EMChatManager.getInstance().getConversation(chatId);
					if (conversation != null && conversation.getAllMessages().size() > 0) {
						List<EMMessage> allMessages = conversation.getAllMessages();
						boolean isLastMessage = false;
						for (int i = (allMessages.size() - 1); i >= 0; i--) {
							ChatInfo chatInfo = null;
							EMMessage message = allMessages.get(i);
							String msgOrderId = message.getStringAttribute(Extras.Extra_Chat_Order_ID, "");
							if (msgOrderId.equals(orderId)) {
								boolean isUnRead = message.isUnread();
								chatInfo = mapChatInfo.get(msgOrderId);
								if (!isLastMessage) {
									chatInfo.setMessage(message);
									isLastMessage = true;
								}
								chatInfo.addUnReadCount(isUnRead);
								mapChatInfo.put(msgOrderId, chatInfo);
								if (!isUnRead) {
									break;
								}
							}
						}
					}
				}
			}

//				for (EMConversation conversation : conversations.values()) {
//					if (conversation.getAllMessages().size() != 0) {
//						if(chatId.equals(conversation.getUserName())) {
//							List<EMMessage> allMessages = conversation.getAllMessages();
//							boolean isLastMessage = false;
//							for (int i = (allMessages.size() - 1); i >= 0; i--) {
//								ChatInfo chatInfo = null;
//								EMMessage message = allMessages.get(i);
//								String msgOrderId = message.getStringAttribute(Extras.Extra_Chat_Order_ID, "");
//								if (msgOrderId.equals(orderId)) {
//									boolean isUnRead = message.isUnread();
//									chatInfo = mapChatInfo.get(msgOrderId);
//									if (!isLastMessage) {
//										chatInfo.setMessage(message);
//										isLastMessage = true;
//									}
//									chatInfo.addUnReadCount(isUnRead);
//									mapChatInfo.put(msgOrderId, chatInfo);
//									if (!isUnRead) {
//										break;
//									}
//								}
//							}
//						}
//					}
//				}
//			}


//			for (EMConversation conversation : conversations.values()) {
//				if (conversation.getAllMessages().size() != 0) {
//					for(OrderInfo orderInfo:orderInfos){
//						String chatId = isCustomer?orderInfo.getSenderId():orderInfo.getUid();
////						String chatName = isCustomer?orderInfo.getSendUserName():orderInfo.getUserName();
//						String orderId = orderInfo.getMid();
//						if(chatId.equals(conversation.getUserName())){
//							List<EMMessage> allMessages = conversation.getAllMessages();
//							boolean isLastMessage = false;
//							for(int i = (allMessages.size()-1);i>=0;i--){
//								ChatInfo chatInfo = null;
//								EMMessage message = allMessages.get(i);
//								String msgOrderId = message.getStringAttribute(Extras.Extra_Chat_Order_ID,"");
//								if(msgOrderId.equals(orderId)){
//									boolean isUnRead = message.isUnread();
//									chatInfo = mapChatInfo.get(msgOrderId);
//									if(!isLastMessage){
//										chatInfo.setMessage(message);
//										isLastMessage = true;
//									}
//									chatInfo.addUnReadCount(isUnRead);
//									mapChatInfo.put(msgOrderId,chatInfo);
//									if(!isUnRead){
//										break;
//									}
//								}
//							}
//
////							for(EMMessage message:conversation.getAllMessages()){
////								ChatInfo chatInfo = null;
////								String msgOrderId = message.getStringAttribute(Extras.Extra_Chat_Order_ID,"");
////								if(msgOrderId.equals(orderId)){
////									if(mapChatInfo.containsKey(msgOrderId)){
////										chatInfo = mapChatInfo.get(msgOrderId);
////										chatInfo.addUnReadCount(message.isUnread());
////										chatInfo.setMessage(message);
////										mapChatInfo.put(msgOrderId,chatInfo);
////									}
////								}
////							}
////							sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
//						}
//					}
//				}
//			}
		}
		chatInfos = new ArrayList<>(mapChatInfo.values());
		chatHistoryListAdapter.refresh(chatInfos);
		((TabActivity)getActivity()).updateUnreadChat(chatHistoryListAdapter.isUnReadMsg());
//		try {
//			// Internal is TimSort algorithm, has bug
//			sortConversationByLastChatTime(sortList);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		List<EMConversation> list = new ArrayList<EMConversation>();
//		for (Pair<Long, EMConversation> sortItem : sortList) {
//			list.add(sortItem.second);
//		}
//		return null;
	}

	/**
	 * 根据最后一条消息的时间排序
	 *
	 * @param
	 */
	private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
		Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
			@Override
			public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

				if (con1.first == con2.first) {
					return 0;
				} else if (con2.first > con1.first) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}


	@Subscribe
	public void onReOrderListForChatEvent(ReOrderListForChatEvent reOrderListForChatEvent){
		orderInfos = new ArrayList<>();
		mapChatInfo = new HashMap<>();
		if(reOrderListForChatEvent!=null&&reOrderListForChatEvent.getOrderInfos()!=null){
			ArrayList<OrderInfo> ls = reOrderListForChatEvent.getOrderInfos();
			for(OrderInfo orderInfo : ls){
				if(orderInfo.getStatus() == Status.Order.Status_Order_Receieve){
					orderInfos.add(orderInfo);
					ChatInfo chatInfo = new ChatInfo();
					chatInfo.setChatId(isCustomer?orderInfo.getSenderId():orderInfo.getUid());
					chatInfo.setChatName(isCustomer ? orderInfo.getSendUserName() : orderInfo.getUserName());
					chatInfo.setChatLogo(isCustomer?orderInfo.getSendUserHead():orderInfo.getUserLogo());
					chatInfo.setOrderId(orderInfo.getMid());
					chatInfo.setShopName(orderInfo.getShopName());
					mapChatInfo.put(orderInfo.getMid(),chatInfo);
				}
			}
		}
		HandlerHelper.post(new HandlerHelper.onRun() {
			@Override
			public void run() {
				loadConversationsWithRecentChat();
			}
		});

	}


	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
//		if (!hidden && ! ((MainActivity)getActivity()).isConflict) {
//			refresh();
//		}
	}

	@Override
    public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
//        if(((MainActivity)getActivity()).isConflict){
//        	outState.putBoolean("isConflict", true);
//        }else if(((MainActivity)getActivity()).getCurrentAccountRemoved()){
//        	outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
//        }
    }
}
