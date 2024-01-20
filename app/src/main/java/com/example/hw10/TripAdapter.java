package com.example.hw10;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder>{

   public ArrayList<Trip> trips ;
   TListener listener;


    public TripAdapter(ArrayList<Trip> trips, TListener tListener) {
        this.trips = trips;
        this.listener = tListener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item,parent,false);

        TripViewHolder tripViewHolder = new TripViewHolder(view,listener);
        return tripViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Trip trip = trips.get(position);
        holder.title.setText(trip.getName());
        holder.distance.setText(String.valueOf(trip.getDistance()+" Miles"));
        holder.debut.setText("Started At:"+trip.getDebut());

        if(trip.getStatus().equals("Complete")){
            holder.status.setText("Complete");
            holder.status.setTextColor(Color.parseColor("#04D519"));
        }else if(trip.getStatus().equals("On Going")){
            holder.status.setText("On Going");
            holder.status.setTextColor(Color.parseColor("#FB7500"));
        }
        if(trip.getEnd().isEmpty()){
            holder.end.setText("Completed At: N/A");
        }else{
            holder.end.setText("Completed At:"+trip.getEnd());
        }
        holder.position = position;

    }

    @Override
    public int getItemCount() {
        return this.trips.size();
    }

    class TripViewHolder extends RecyclerView.ViewHolder{
        TextView title,debut,end,status,distance;
        TListener tListener;
        int position;

        public TripViewHolder(@NonNull View itemView, TListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewTitle);
            debut = itemView.findViewById(R.id.textViewDebut);
            end = itemView.findViewById(R.id.textViewEnd);
            status = itemView.findViewById(R.id.textViewStatus);
            distance = itemView.findViewById(R.id.textViewDistance);
            this.tListener = listener;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("demo", "onClick: "+position);
                    tListener.contactDetails(position);
                }
            });
        }
    }

    public interface TListener{
        public void contactDetails(int position);
    }
}
