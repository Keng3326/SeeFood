package com.example.cuisine;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class HomeAdapter extends RecyclerView .Adapter<HomeAdapter.FavoriteViewHolder>{

    private Context mCtx;
    private List<FoodData> foodDataList;
    private OnItemClickListener mListener;
    ArrayList<String> favoriteList;

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onCollectClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public HomeAdapter(Context mCtx, List<FoodData> foodDataList) {
        this.mCtx = mCtx;
        this.foodDataList = foodDataList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.food_data,null);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FoodData foodData = foodDataList.get(position);

        holder.foodId.setText(foodData.getFoodId());
        holder.foodName.setText(foodData.getFoodName());
        holder.foodVag.setText(foodData.getFoodVag());
        holder.foodMeat.setText(foodData.getFoodMeat());
        holder.foodType.setText(foodData.getFoodType());
        holder.foodSpicy.setText(foodData.getFoodSpicy());
        //holder.foodImg.setImageDrawable(loadImageFromURL(foodData.getFoodImg()));
        holder.foodImg.setImageBitmap(imageNicer(foodData.getFoodImg()));

        if(checkFavoriteExist(foodData.getFoodId())){
            holder.favoriteBtn.setImageResource(R.drawable.favorite);
        }else {
            holder.favoriteBtn.setImageResource(R.drawable.favorite_border);
        }

    }

    public static Bitmap imageNicer(String url){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inSampleSize = 3;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        InputStream is = null;
        try {
            is = (InputStream) new URL(url).getContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(is,null,opt);
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
        return foodDataList.size();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder{

        ImageView foodImg;
        TextView foodId,foodName,foodVag,foodMeat,foodType,foodSpicy;
        ImageView favoriteBtn;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);

            foodId = itemView.findViewById(R.id.foodId);
            foodImg = itemView.findViewById(R.id.foodImg);
            foodName = itemView.findViewById(R.id.foodNameHome);
            foodVag = itemView.findViewById(R.id.foodVag);
            foodMeat = itemView.findViewById(R.id.foodMeat);
            foodType = itemView.findViewById(R.id.foodType);
            foodSpicy = itemView.findViewById(R.id.foodSpicy);
            favoriteBtn = itemView.findViewById(R.id.favoriteBtn);

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

            favoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener!=null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            mListener.onCollectClick(position);
                            if(!checkFavoriteExist(foodDataList.get(position).getFoodId())){
                                favoriteBtn.setImageResource(R.drawable.favorite_border);
                            }else {
                                favoriteBtn.setImageResource(R.drawable.favorite);
                            }
                        }
                    }
                }
            });
        }
    }

    //取得最愛清單
    public void loadFavoriteInfo(){
        SharedPreferences sharedPref = mCtx.getSharedPreferences("favorite", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String favoriteJson = sharedPref.getString("favorite",null);
        favoriteList =  gson.fromJson(favoriteJson,new TypeToken<ArrayList<String>>() {}.getType());
    }

    //判斷該項食物是否已存在最愛清單中
    public boolean checkFavoriteExist(String foodId){
        loadFavoriteInfo();
        if(favoriteList==null){
            favoriteList= new ArrayList<String>();
        }

        boolean checkRepeat = false;
        for(int i = 0; i<favoriteList.size();i++){
            if(favoriteList.get(i).equals(foodId)){
                checkRepeat = true;
                break;
            }
        }
        return checkRepeat;
    }
}
