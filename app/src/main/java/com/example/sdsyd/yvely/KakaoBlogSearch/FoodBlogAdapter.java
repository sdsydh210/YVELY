package com.example.sdsyd.yvely.KakaoBlogSearch;

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

public class FoodBlogAdapter extends BaseAdapter{

    ArrayList<FoodBlogListviewitem> foodBlogListviewitems = new ArrayList<>();

    FoodBlogAdapter(){

    }

    @Override
    public int getCount() {
        return foodBlogListviewitems.size();
    }

    @Override
    public Object getItem(int position) {
        return foodBlogListviewitems.get(position);
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
            convertView = inflater.inflate(R.layout.activity_food_blog_listviewitem,parent,false);
        }

        ImageView blogImage = convertView.findViewById(R.id.blogThumbnail);
        TextView blogUrl = convertView.findViewById(R.id.blogUrl);
        TextView blogDateTime = convertView.findViewById(R.id.blogDateTime);
        TextView blogTitle = convertView.findViewById(R.id.blogTitle);
        TextView blogContent = convertView.findViewById(R.id.blogContent);

        FoodBlogListviewitem listviewitem = foodBlogListviewitems.get(position);

        Log.d("SUNNY","어댑터 글라이드 부분 : " + listviewitem.getBlogImage());
        Glide.with(context)
                .load(listviewitem.getBlogImage())
//                .apply(new RequestOptions()
//                        .override(50,50)
//                .placeholder(R.drawable.ic_livelist)
//                .error(R.drawable.ic_error_pic))
                .into(blogImage);
//        broadcastingImage.setImageURI(Uri.parse(listviewItem.getBroadcastingImage()));
        blogUrl.setText(listviewitem.getBlogUrl());
        blogDateTime.setText(listviewitem.getBlogDateTime());
        blogTitle.setText(listviewitem.getBlogTitle());
        blogContent.setText(listviewitem.getBlogContent());

        return convertView;
    }

    public void addItem(String blogImage, String blogUrl, String blogDateTime, String blogTitle, String blogContent){

        FoodBlogListviewitem item = new FoodBlogListviewitem();

        item.setBlogImage(blogImage);
        item.setBlogUrl(blogUrl);
        item.setBlogDateTime(blogDateTime);
        item.setBlogTitle(blogTitle);
        item.setBlogContent(blogContent);

        foodBlogListviewitems.add(item);
    }

}//FoodBlogAdapter
