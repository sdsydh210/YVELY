package com.example.sdsyd.yvely.Broadcast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sdsyd.yvely.R;

import java.util.ArrayList;

public class LiveChatAdapter extends BaseAdapter {

    ArrayList<LiveChatListviewitem> liveChatListviewitems = new ArrayList<>();

    public LiveChatAdapter(){

    }

    @Override
    public int getCount() {
        return liveChatListviewitems.size();
    }

    @Override
    public Object getItem(int position) {
        return liveChatListviewitems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_live_chat_listviewitem,parent,false);
        }

        TextView playerId = convertView.findViewById(R.id.playerId);
        TextView playerMessage = convertView.findViewById(R.id.playerMessage);

        LiveChatListviewitem listviewItem = liveChatListviewitems.get(position);

        playerId.setText(listviewItem.getPlayerId());
        playerMessage.setText(listviewItem.getPlayerMessage());

        return convertView;
    }

    public void addItem(String id, String message){

        LiveChatListviewitem item = new LiveChatListviewitem();

        item.setPlayerId(id);
        item.setPlayerMessage(message);

        liveChatListviewitems.add(item);
    }

}/*LiveChatAdapter*/
