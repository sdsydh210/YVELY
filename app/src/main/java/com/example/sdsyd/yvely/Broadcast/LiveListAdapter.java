package com.example.sdsyd.yvely.Broadcast;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sdsyd.yvely.R;

import java.util.ArrayList;

public class LiveListAdapter extends BaseAdapter{
    ArrayList<LiveListListviewitem> liveListListviewitems = new ArrayList<>();

    LiveListAdapter(){

    }

    @Override
    public int getCount() {
        return liveListListviewitems.size();
    }

    @Override
    public Object getItem(int position) {
        return liveListListviewitems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.activity_live_list_listviewitem,parent,false);
        }

        ImageView broadcastingImage = convertView.findViewById(R.id.broadcastingImage);
        TextView broadcasterId = convertView.findViewById(R.id.broadcasterId);
        TextView broadcastingTitle = convertView.findViewById(R.id.broadcastingTitle);
        TextView broadcastingStartTime = convertView.findViewById(R.id.broadcastingStartTime);

        LiveListListviewitem listviewItem = liveListListviewitems.get(position);

        Log.d("SUNNY","어댑터 글라이드 부분 : " + listviewItem.getBroadcastingImage());
        Glide.with(context)
                .load(listviewItem.getBroadcastingImage())
//                .apply(new RequestOptions()
//                        .override(50,50)
//                .placeholder(R.drawable.ic_livelist)
//                .error(R.drawable.ic_error_pic))
                .into(broadcastingImage);
//        broadcastingImage.setImageURI(Uri.parse(listviewItem.getBroadcastingImage()));
        broadcasterId.setText(listviewItem.getBroadcasterId());
        broadcastingTitle.setText(listviewItem.getBroadcastingTitle());
        broadcastingStartTime.setText(listviewItem.getBroadcastingStartTime());

        return convertView;
    }

    public void addItem(String image, String id, String title, String time){

        LiveListListviewitem item = new LiveListListviewitem();

        item.setBroadcastingImage(image);
        item.setBroadcasterId(id);
        item.setBroadcastingTitle(title);
        item.setBroadcastingStartTime(time);

        liveListListviewitems.add(item);
    }

}/*LiveListAdapter*/

