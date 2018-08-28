package com.example.sdsyd.yvely.Broadcast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sdsyd.yvely.R;

import java.util.ArrayList;

public class VodAdapter extends BaseAdapter {

    ArrayList<VodListviewitem> vodListviewitems = new ArrayList<>();

    VodAdapter(){

    }

    @Override
    public int getCount() {
        return vodListviewitems.size();
    }

    @Override
    public Object getItem(int position) {
        return vodListviewitems.get(position);
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
            convertView = inflater.inflate(R.layout.activity_vod_listviewitem,parent,false);
        }

        ImageView vodImage = convertView.findViewById(R.id.vodImage);
        TextView vodId = convertView.findViewById(R.id.vodId);
        TextView vodTitle = convertView.findViewById(R.id.vodTitle);
        TextView vodUrl = convertView.findViewById(R.id.vodUrl);

        VodListviewitem listviewItem = vodListviewitems.get(position);

        Glide.with(context)
                .load(listviewItem.getVodImage())
                .into(vodImage);
        vodId.setText(listviewItem.getVodId());
        vodTitle.setText(listviewItem.getVodTitle());
        vodUrl.setText(listviewItem.getVodUrl());

        return convertView;
    }

    public void addItem(String image, String id, String title, String vod){

        VodListviewitem item = new VodListviewitem();

        item.setVodImage(image);
        item.setVodId(id);
        item.setVodTitle(title);
        item.setVodUrl(vod);

        vodListviewitems.add(item);
    }

}
