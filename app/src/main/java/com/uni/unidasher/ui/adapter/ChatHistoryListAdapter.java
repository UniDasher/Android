package com.uni.unidasher.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.uni.unidasher.AppConstants;
import com.uni.unidasher.R;
import com.uni.unidasher.chat.ui.Constant;
import com.uni.unidasher.chat.ui.utils.SmileUtils;
import com.uni.unidasher.data.entity.ChatInfo;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2015/7/4.
 */
public class ChatHistoryListAdapter extends BaseAdapter {

    ArrayList<ChatInfo> chatInfos = new ArrayList<>();
    LayoutInflater inflater;
    Context mContext;
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private boolean isUnReadMsg = false;

    public ChatHistoryListAdapter(Context context) {
        mContext = context;
        inflater = LayoutInflater.from(context);
    }

    public void refresh(ArrayList<ChatInfo> ls){
        isUnReadMsg = false;
        if(ls == null){
            chatInfos = new ArrayList<>();
        }else{
            chatInfos = ls;
        }
        notifyDataSetChanged();
    }

    public boolean isUnReadMsg(){
        if(chatInfos==null||chatInfos.size() == 0){
            return false;
        }else{
            boolean isExist = false;
            for(ChatInfo chatInfo:chatInfos){
                if(chatInfo.getUnReadCount()>0){
                    isExist = true;
                    break;
                }
            }
            return isExist;
        }
    }

    @Override
    public int getCount() {
        return chatInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return chatInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adaper_chat_history_listview, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.txtvOrderId = (TextView)convertView.findViewById(R.id.txtvOrderId);
            holder.txtvChatName = (TextView)convertView.findViewById(R.id.txtvChatName);
            holder.txtvLastMsg = (TextView)convertView.findViewById(R.id.txtvLastMsg);
            holder.txtvUnReadCount = (TextView)convertView.findViewById(R.id.txtvUnReadCount);
            holder.txtvShopName = (TextView)convertView.findViewById(R.id.txtvShopName);
            holder.imgvUserHead = (CircleImageView)convertView.findViewById(R.id.imgvUserHead);
//            holder.name = (TextView) convertView.findViewById(R.id.name);
//            holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
//            holder.message = (TextView) convertView.findViewById(R.id.message);
//            holder.time = (TextView) convertView.findViewById(R.id.time);
//            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
//            holder.msgState = convertView.findViewById(R.id.msg_state);
//            holder.list_item_layout = (RelativeLayout) convertView.findViewById(R.id.list_item_layout);
            convertView.setTag(holder);
        }
        ChatInfo chatInfo = chatInfos.get(position);
        holder.txtvOrderId.setText(chatInfo.getOrderId());
        holder.txtvChatName.setText(chatInfo.getChatName());
        holder.txtvShopName.setText(chatInfo.getShopName());
        if(chatInfo.getUnReadCount() == 0){
            holder.txtvUnReadCount.setVisibility(View.GONE);
        }else{
            holder.txtvUnReadCount.setVisibility(View.VISIBLE);
            holder.txtvUnReadCount.setText(chatInfo.getUnReadCount()+"");
            isUnReadMsg = true;
        }

        EMMessage emMessage = chatInfo.getMessage();
        if(emMessage!=null){
            holder.txtvLastMsg.setText(SmileUtils.getSmiledText(mContext, getMessageDigest(emMessage, mContext)),
                    TextView.BufferType.SPANNABLE);
        }else{
            holder.txtvLastMsg.setText("");
        }

        imageLoader.displayImage(AppConstants.HOST + chatInfo.getChatLogo(),
                holder.imgvUserHead,
                new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true).resetViewBeforeLoading(true).build());

        return convertView;
    }

    private static class ViewHolder {

        TextView txtvOrderId;
        /** 和谁的聊天记录 */
        TextView txtvChatName;
//        /** 消息未读数 */
        TextView txtvUnReadCount;
        /** 最后一条消息的内容 */
        TextView txtvLastMsg;
        TextView txtvShopName;
        CircleImageView imgvUserHead;
//        /** 最后一条消息的时间 */
//        TextView time;
//        /** 用户头像 */
//        ImageView avatar;
//        /** 最后一条消息的发送状态 */
//        View msgState;
//        /** 整个list中每一行总布局 */
//        RelativeLayout list_item_layout;
    }
    private String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION: // 位置消息
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    // 从sdk中提到了ui中，使用更简单不犯错的获取string的方法
                    // digest = EasyUtils.getAppResourceString(context,
                    // "location_recv");
                    digest = getStrng(context, R.string.location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;
                } else {
                    // digest = EasyUtils.getAppResourceString(context,
                    // "location_prefix");
                    digest = getStrng(context, R.string.location_prefix);
                }
                break;
            case IMAGE: // 图片消息
                ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
                digest = getStrng(context, R.string.picture) + imageBody.getFileName();
                break;
            case VOICE:// 语音消息
                digest = getStrng(context, R.string.voice);
                break;
            case VIDEO: // 视频消息
                digest = getStrng(context, R.string.video);
                break;
            case TXT: // 文本消息
                if(!message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,false)){
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = txtBody.getMessage();
                }else{
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = getStrng(context, R.string.voice_call) + txtBody.getMessage();
                }
                break;
            case FILE: // 普通文件消息
                digest = getStrng(context, R.string.file);
                break;
            default:
                System.err.println("error, unknow type");
                return "";
        }

        return digest;
    }

    String getStrng(Context context, int resId) {
        return context.getResources().getString(resId);
    }
}
