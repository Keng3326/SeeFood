package com.example.cuisine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecordAdapter extends RecyclerView .Adapter<RecordAdapter.RecordViewHolder>{

    private Context mCtx;
    private List<RecordData> recordDataList;
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public RecordAdapter(Context mCtx, List<RecordData> recordDataList) {
        this.mCtx = mCtx;
        this.recordDataList = recordDataList;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.record_data,null);

        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        RecordData recordData = recordDataList.get(position);

        holder.foodName.setText(recordData.getFoodName());
        holder.foodDate.setText(recordData.getDate());
        holder.foodId.setText(recordData.getFoodId());

        byte[] image = recordData.getFoodImg();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image,0,image.length);
        holder.imageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return recordDataList.size();
    }

    class RecordViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView foodName,foodDate,foodId;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);

            foodId = itemView.findViewById(R.id.foodId);
            imageView = itemView.findViewById(R.id.foodImg);
            foodName = itemView.findViewById(R.id.idenFoodName);
            foodDate = itemView.findViewById(R.id.idenDate);

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

        }
    }
}
