package com.dal.tourism;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import org.apache.commons.lang3.text.WordUtils;

import java.io.Serializable;
import java.util.ArrayList;


public class DestinationViewAdapter extends RecyclerView.Adapter<DestinationViewAdapter.ViewHolder> implements Serializable {

    private ArrayList<String> mDestinations;
    private ArrayList<String> mDescriptions;
    private ArrayList<String> mImages;
    private Context mContext;

    public DestinationViewAdapter(ArrayList<String> mDestinations, ArrayList<String> mDescriptions, ArrayList<String> mImages, Context mContext) {
        this.mDestinations = mDestinations;
        this.mDescriptions = mDescriptions;
        this.mImages       = mImages;
        this.mContext      = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_destination_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.txt_destinationName.setText(WordUtils.capitalizeFully(mDestinations.get(position)));
        holder.txt_destinationDescription.setText(WordUtils.capitalizeFully(mDescriptions.get(position)));
        Picasso.get().load(mImages.get(position)).into(holder.image);

        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, TicketActivity.class);
                intent.putExtra("destinationName", mDestinations.get(position));
                intent.putExtra("destinationImage", mImages.get(position));
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDestinations.size();
    }

    public void filterList(ArrayList<String> fDestinations, ArrayList<String> fDescriptions, ArrayList<String> fImages) {
        mDestinations = fDestinations;
        mDescriptions = fDescriptions;
        mImages = fImages;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_destinationName;
        TextView txt_destinationDescription;
        ImageView image;
        RelativeLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_destinationName = itemView.findViewById(R.id.txt_destinationName);
            txt_destinationDescription = itemView.findViewById(R.id.txt_destinationDescription);
            image = itemView.findViewById(R.id.image);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }


}
