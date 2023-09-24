package com.app_developer.weather_app;

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

public class weatherRVAdapter extends RecyclerView.Adapter<weatherRVAdapter.ViewHolder> {

    private Context context;
    private ArrayList<weatherRVModal> weatherRVModalArrayList;

    public weatherRVAdapter(Context context, ArrayList<weatherRVModal> weatherRVModalArrayList) {
        this.context = context;
        this.weatherRVModalArrayList = weatherRVModalArrayList;
    }


    //tạo và trả về một viewHolder mới
    @NonNull
    @Override
    public weatherRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item, parent, false);
        return new ViewHolder(view);
    }


    //lấy dữ liệu từ mảng và hiển thị nó vào đúng vị trí
    @Override
    public void onBindViewHolder(@NonNull weatherRVAdapter.ViewHolder holder, int position) {
        weatherRVModal modal = weatherRVModalArrayList.get(position);
        holder.temperatureTV.setText(modal.getTemperature() + "°C");
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.conditionIV);
        holder.windTV.setText(modal.getWindSpeed()+"Km/h");
        SimpleDateFormat input = new SimpleDateFormat("dd-MM-yyyy hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");

        try {
            Date t = input.parse(modal.getTime());
            holder.timeTV.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherRVModalArrayList.size();
    }

    //tham chiếu đến các textview
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView windTV, temperatureTV, timeTV;
        private ImageView conditionIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTV = itemView.findViewById(R.id.idTVWindspeed);
            temperatureTV = itemView.findViewById(R.id.idTVtemperature);
            timeTV = itemView.findViewById(R.id.idTVTittletime);
            conditionIV = itemView.findViewById(R.id.idIVCondition);
        }
    }
}
