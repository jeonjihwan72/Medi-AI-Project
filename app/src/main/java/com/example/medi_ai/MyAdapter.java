package com.example.medi_ai;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Integer> imageList;
    private List<String> collegeList;
    private List<String> countList;
    private List<Integer> colorList;

    public MyAdapter(List<Integer> imageList, List<String> collegeList,
                     List<String> countList, List<Integer> colorList) {
        this.imageList = imageList;
        this.collegeList = collegeList;
        this.countList = countList;
        this.colorList = colorList;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView itemIcon;
        public TextView itemName, itemCount;

        public MyViewHolder(View view) {
            super(view);
            itemIcon = view.findViewById(R.id.college_icon);
            itemName = view.findViewById(R.id.college_name);
            itemCount = view.findViewById(R.id.college_count);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.hanbat_recycler_item_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Integer img = imageList.get(position);
        String name = collegeList.get(position);
        String count = countList.get(position);
        Integer bgColor = colorList.get(position);

        holder.itemIcon.setImageResource(img);
        holder.itemName.setText(name);
        holder.itemCount.setText(count);

        holder.itemView.setBackgroundColor(bgColor);
    }

    @Override
    public int getItemCount() {
        return collegeList.size();
    }
}
