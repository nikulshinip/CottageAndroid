package ru.home.bahr.cottage;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class TemperatureAdapter extends BaseAdapter{

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String TEMPERATURE = "temperatur";

    private Activity context;                           //Activity
    private ArrayList<Map<String, String>> arrayMap;    //данные наложения

    public TemperatureAdapter(Activity context, ArrayList<Map<String, String>> arrayMap){
        this.context = context;
        this.arrayMap = arrayMap;
    }

    static class HolderView{
        public TextView id;
        public TextView title;
        public TextView temperature;
        public ImageView image;
    }

    @Override
    public int getCount() {
        return arrayMap.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayMap.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        HolderView holder = new HolderView();
        View rowView = view;

        if(rowView==null){
            LayoutInflater inflater = context.getLayoutInflater();                  //создаем inflater
            rowView = inflater.inflate(R.layout.temperature_item, null, true);             //и заполняем из xml

            holder.id = (TextView) rowView.findViewById(R.id.number);
            holder.title = (TextView) rowView.findViewById(R.id.title);
            holder.temperature = (TextView) rowView.findViewById(R.id.temperature);
            holder.image = (ImageView) rowView.findViewById(R.id.image);
            rowView.setTag(holder);
        }else{
            holder = (HolderView) rowView.getTag();                                     //если строка создана то передаем ее в holder
        }

        holder.id.setText(arrayMap.get(i).get(ID));
        holder.title.setText("Температура " + arrayMap.get(i).get(TITLE));
        holder.temperature.setText(arrayMap.get(i).get(TEMPERATURE) + "°C");
        if("9999".equals(arrayMap.get(i).get(TEMPERATURE))){
            holder.temperature.setTextColor(Color.YELLOW);
            holder.image.setImageResource(R.drawable.are);
        }else if(Float.valueOf(arrayMap.get(i).get(TEMPERATURE))<25){
            holder.temperature.setTextColor(ContextCompat.getColor(context, R.color.toolBarShadow));
            holder.image.setImageResource(R.drawable.flake);
        }else{
            holder.temperature.setTextColor(ContextCompat.getColor(context, R.color.buttomStartEndGradientPressed));
            holder.image.setImageResource(R.drawable.sunny);
        }

        return rowView;
    }

}
