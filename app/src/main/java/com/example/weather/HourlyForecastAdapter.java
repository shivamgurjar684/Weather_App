package com.example.weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.ViewHolder> {
    private Context context;
    private ArrayList<HourlyForecast> hourlyForecasts;

    public HourlyForecastAdapter(Context context, ArrayList<HourlyForecast> hourlyForecasts) {
        this.context = context;
        this.hourlyForecasts = hourlyForecasts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        HourlyForecast modal = hourlyForecasts.get(position);

        holder.temperatureTextView.setText(modal.getTemperature() + "°C");
        Picasso.get().load("https:".concat(modal.getIcon())).into(holder.conditionImageView);
        holder.windTextView.setText(modal.getWindSpeed() + "Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");

        try {
            Date t = input.parse(modal.getTime());
            holder.timeTextView.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return hourlyForecasts.size();
    }

    // Creating a ViewHolder Class

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView timeTextView;
        private TextView temperatureTextView;
        private ImageView conditionImageView ;
        private TextView windTextView ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeTextView = itemView.findViewById(R.id.idTVTime);
            temperatureTextView = itemView.findViewById(R.id.idTVTemperature);
            conditionImageView = itemView.findViewById(R.id.idIVCondition);
            windTextView = itemView.findViewById(R.id.idTVWindSpeed);
        }
    }
}

