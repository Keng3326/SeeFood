package com.example.cuisine;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class FavoriteAdapter extends RecyclerView .Adapter<FavoriteAdapter.FavoriteViewHolder>{
    String desc;
    private Context mCtx;
    private List<FoodData> favoriteDataList;
    private OnItemClickListener mListener;
    private ImageView deleteBtn;

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public FavoriteAdapter(Context mCtx, List<FoodData> favoriteDataList) {
        this.mCtx = mCtx;
        this.favoriteDataList = favoriteDataList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.favorite_data,null);

        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FoodData favoriteData = favoriteDataList.get(position);

        holder.foodId.setText(favoriteData.getFoodId());
        holder.foodName.setText(favoriteData.getFoodName());
        if(favoriteData.getFoodDesc().length() > 25){
            desc = favoriteData.getFoodDesc().substring(0,25);
            desc += "...";
        }else{
            desc = favoriteData.getFoodDesc();
        }
        holder.foodDesc.setText(desc);
        holder.foodImg.setImageDrawable(loadImageFromURL(favoriteData.getFoodImg()));

    }

    private Drawable loadImageFromURL(String url) {
        try{
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable draw = Drawable.createFromStream(is, "src");
            return draw;
        }catch (Exception e) {
            //TODO handle error
            Log.i("loadingImg", e.toString());
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return favoriteDataList.size();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder{
        ImageView foodImg;
        TextView foodName,foodDesc,foodId;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            foodId = itemView.findViewById(R.id.foodId);
            foodImg = itemView.findViewById(R.id.foodImg);
            foodName = itemView.findViewById(R.id.foodName);
            foodDesc = itemView.findViewById(R.id.foodDesc);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }
}
