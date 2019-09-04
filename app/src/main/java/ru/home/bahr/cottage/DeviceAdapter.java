package ru.home.bahr.cottage;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

public class DeviceAdapter extends BaseAdapter {

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String TEMPERATURE = "temperatur";
    private static final String ADDRESS ="address";
    private static final String CONNECT ="connect";
    private static final String DIN1 ="din1";
    private static final String DIN2 ="din2";
    private static final String DIN3 ="din3";
    private static final String DIN4 ="din4";
    private static final String DOUT1 ="dout1";
    private static final String DOUT2 ="dout2";
    private static final String DOUT3 ="dout3";


    private Activity context;                                       //Activity
    private ArrayList<Map<String, String>> arrayTemperatureMap;     //данные наложения
    private ArrayList<Map<String, String>> arrayDS2408Map;

    public DeviceAdapter(Activity context, ArrayList<Map<String, String>> arrayTemperatureMap, ArrayList<Map<String, String>> arrayDS2408Map){
        this.context = context;
        this.arrayTemperatureMap = arrayTemperatureMap;
        this.arrayDS2408Map = arrayDS2408Map;
    }

    static class Holder{
        public LinearLayout backLayout;
        public TextView id;
        public TextView title;
        public TextView address;
        public TextView temperature;
        public TextView din1;
        public TextView din2;
        public TextView din3;
        public TextView din4;
        public TextView dout1;
        public TextView dout2;
        public TextView dout3;

        public void setSignalBackgroung(Activity context, Map<String, String> map){
            TextView[] dinDout = {din1, din2, din3, din4, dout1, dout2, dout3};
            String[] key = {DIN1, DIN2, DIN3, DIN4, DOUT1, DOUT2, DOUT3};

            for(int i=0;i<dinDout.length;i++){
                if("0".equals(map.get(key[i]))){
                    dinDout[i].setBackgroundResource(R.drawable.heating_power_lamp_off);
                    dinDout[i].setTextColor(ContextCompat.getColor(context, R.color.menuTextColor));
                }else if("1".equals(map.get(key[i]))){
                    dinDout[i].setBackgroundResource(R.drawable.heating_power_lamp_on);
                    dinDout[i].setTextColor(ContextCompat.getColor(context, R.color.backGroundColor));
                }
            }
        }
    }

    @Override
    public int getCount() {
        return (arrayTemperatureMap.size() + arrayDS2408Map.size());
    }

    @Override
    public Object getItem(int i) {
        if(i<arrayTemperatureMap.size()){
            return arrayTemperatureMap.get(i);
        }else{
            int x = i - (arrayTemperatureMap.size());
            return arrayDS2408Map.get(x);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int i) {
        if(i<arrayTemperatureMap.size()){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        Holder holder = new Holder();
        View rowView = view;
        int viewType = getItemViewType(i);

        if(rowView==null) {
            LayoutInflater inflater = context.getLayoutInflater();                  //создаем inflater
            if (viewType == 0) {
                rowView = inflater.inflate(R.layout.device_temperature_item, null, true);
            } else {
                rowView = inflater.inflate(R.layout.device_ds2408_item, null, true);
            }
        }
        holder.backLayout = (LinearLayout) rowView.findViewById(R.id.backLayout);
        holder.id = (TextView) rowView.findViewById(R.id.number);
        holder.title = (TextView) rowView.findViewById(R.id.title);
        holder.address = (TextView) rowView.findViewById(R.id.adrress);
        if(viewType==0){

            holder.temperature = (TextView) rowView.findViewById(R.id.temperature);

            holder.id.setText(arrayTemperatureMap.get(i).get(ID));
            holder.title.setText(arrayTemperatureMap.get(i).get(TITLE));
            holder.address.setText(arrayTemperatureMap.get(i).get(ADDRESS));
            holder.temperature.setText(arrayTemperatureMap.get(i).get(TEMPERATURE) + "°C");
            if("9999".equals(arrayTemperatureMap.get(i).get(TEMPERATURE))){
                holder.backLayout.setBackgroundResource(R.drawable.heating_item_red);
            }else{
                holder.backLayout.setBackgroundResource(R.drawable.heating_item_green);
            }
        }
        if(viewType==1){
            int x = i - arrayTemperatureMap.size();

            holder.din1 = (TextView) rowView.findViewById(R.id.din1);
            holder.din2 = (TextView) rowView.findViewById(R.id.din2);
            holder.din3 = (TextView) rowView.findViewById(R.id.din3);
            holder.din4 = (TextView) rowView.findViewById(R.id.din4);
            holder.dout1 = (TextView) rowView.findViewById(R.id.dout1);
            holder.dout2 = (TextView) rowView.findViewById(R.id.dout2);
            holder.dout3 = (TextView) rowView.findViewById(R.id.dout3);

            holder.id.setText(arrayDS2408Map.get(x).get(ID));
            holder.title.setText(arrayDS2408Map.get(x).get(TITLE));
            holder.address.setText(arrayDS2408Map.get(x).get(ADDRESS));
            if("1".equals(arrayDS2408Map.get(x).get(CONNECT))){
                holder.backLayout.setBackgroundResource(R.drawable.heating_item_green);
            }else{
                holder.backLayout.setBackgroundResource(R.drawable.heating_item_red);
            }
            holder.setSignalBackgroung(context, arrayDS2408Map.get(x));

        }
        return rowView;
    }

}
