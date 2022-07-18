package com.example.growup.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.growup.R;
import com.example.growup.control.plant.DateManager;
import com.example.growup.control.plant.DateManagerFactory;
import com.example.growup.control.plant.PlantController;
import com.example.growup.plantModel.Plant;

import java.util.ArrayList;

public class PlantListAdapter extends RecyclerView.Adapter<PlantListAdapter.ViewHolder> {

    private ArrayList <Plant> plants;
    private LayoutInflater Inflater;
    private ItemClickListener clickListener;
    private PlantController plantController;
    private DateManager dateManager;
    private Context ctx;


    PlantListAdapter(Context ctx, ArrayList<Plant> plants,PlantController plantController) {
        this.Inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
        this.plants = plants;
        this.plantController = plantController;
        dateManager = DateManagerFactory.produceDateManager();
    }

    // inflates the row layout from xml when needed
    @Override
    public PlantListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = Inflater.inflate(R.layout.row_plant, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String plantName = plants.get(position).getPlantName();
        String water = plants.get(position).getNextWater();
        String nextWatering = "water on the: "+ plants.get(position).getNextWater();
        Bitmap plantImage = plantController.retrievePlantImage(plants.get(position));
        holder.nextWatering.setText(nextWatering);
        if (dateManager.indicateDueDate(water)){
            holder.nextWatering.setTextColor(ContextCompat.getColor(ctx, R.color.red));
        }
        holder.plantName.setText(plantName);
        if (plantImage != null){
        holder.plantImageView.setImageBitmap(plantImage);}
        else{holder.plantImageView.setImageResource(R.drawable.defaultplant);}
    }


    // total number of rows
    @Override
    public int getItemCount() {
        return plants.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView plantName;
        TextView nextWatering;
        ImageView plantImageView;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            plantName = itemView.findViewById(R.id.plantNameRv);
            nextWatering = itemView.findViewById(R.id.nextWateringRv);
            plantImageView = itemView.findViewById(R.id.deviceImageRv);
            checkBox = itemView.findViewById(R.id.watered);
            checkBox.setOnClickListener(this::onCheckBoxClick);
            itemView.setOnClickListener(this);
        }

        public void onCheckBoxClick(View view){
            if (clickListener != null) clickListener.onCheckBoxClick((CheckBox) view, getAdapterPosition());
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    Plant getItem(int id) {
        return plants.get(id);
    }

    // allows click events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
        void onCheckBoxClick(CheckBox checkBox, int position);
    }



}

