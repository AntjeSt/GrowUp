package com.example.growup.view;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.growup.R;
import com.example.growup.control.plant.DateManager;
import com.example.growup.control.plant.DateManagerFactory;
import com.example.growup.control.plant.PlantController;
import com.example.growup.plantModel.Plant;

import java.util.ArrayList;

public class DiscoveredDevicesAdapter extends RecyclerView.Adapter<DiscoveredDevicesAdapter.ViewHolder> {

    private ArrayList<BluetoothDevice> discoveredDevices;
    private LayoutInflater Inflater;
    private DiscoveredDevicesAdapter.ItemClickListener clickListener;
    private Context ctx;


    DiscoveredDevicesAdapter(Context ctx, ArrayList<BluetoothDevice> devices) {
        this.Inflater = LayoutInflater.from(ctx);
        this.ctx = ctx;
        this.discoveredDevices = devices;
    }

    // inflates the row layout from xml when needed
    @Override
    public DiscoveredDevicesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = Inflater.inflate(R.layout.row_discover, parent, false);
        return new DiscoveredDevicesAdapter.ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(DiscoveredDevicesAdapter.ViewHolder holder, int position) {
        String deviceName = discoveredDevices.get(position).getName();
        String deviceAdress = discoveredDevices.get(position).getAddress();
        holder.deviceAdress.setText(deviceAdress);
        holder.deviceName.setText(deviceName);

    }


    // total number of rows
    @Override
    public int getItemCount() {
        return discoveredDevices.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView deviceName;
        TextView deviceAdress;
        Button connectBtn;

        ViewHolder(View itemView) {
            super(itemView);
            deviceName   = itemView.findViewById(R.id.deviceName);
            deviceAdress = itemView.findViewById(R.id.deviceAdress);
            connectBtn = itemView.findViewById(R.id.connect);
            connectBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onButtonClick((Button)view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    BluetoothDevice getItem(int id) {
        return discoveredDevices.get(id);
    }

    // allows click events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        //void onItemClick(View view, int position);
        void onButtonClick(Button connectBtn, int position);
    }

}
