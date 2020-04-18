package com.dal.tourism;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.io.Serializable;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class LocationViewAdapter extends RecyclerView.Adapter<LocationViewAdapter.ViewHolder> implements Serializable {

    private ArrayList<String> mLocations;
    private ArrayList<String> mImages;
    private Context mContext;

    public LocationViewAdapter(ArrayList<String> mLocations, ArrayList<String> mImages, Context mContext) {
        this.mLocations = mLocations;
        this.mImages = mImages;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_location_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.txt_locationName.setText(WordUtils.capitalizeFully(mLocations.get(position)));
        Picasso.get().load(mImages.get(position)).into(holder.image);

        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, ViewDestinationsActivity.class);
                intent.putExtra("location", holder.txt_locationName.getText().toString());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mLocations.size();
    }

    public void filterList(ArrayList<String> fLocations, ArrayList<String> fImages) {
        mLocations = fLocations;
        mImages = fImages;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_locationName;
        CircleImageView image;
        RelativeLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_locationName = itemView.findViewById(R.id.txt_locationName);
            image = itemView.findViewById(R.id.image);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }

}
