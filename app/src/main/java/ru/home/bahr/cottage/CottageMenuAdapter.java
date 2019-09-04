package ru.home.bahr.cottage;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CottageMenuAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] names;
    private final int id;

    public CottageMenuAdapter(Activity context, String[] names, int id){       //конструктор
        super(context, R.layout.menu_item, names);
        this.context = context;
        this.names = names;
        this.id = id;
    }

    static class ViewHolder {                                           //клас под данные
        public ImageView imageView;
        public TextView textView;
        public LinearLayout linear;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder = new ViewHolder();                                       //создание View
        View rowView;// = convertView;                                                 //View из xml
//        if(rowView==null/* || holder==null*/) {                                                         //если не одной строки не созданно

            LayoutInflater inflater = context.getLayoutInflater();                  //создаем inflater
            rowView = inflater.inflate(R.layout.menu_item, null, true);             //и заполняем из xml
            holder.textView = (TextView) rowView.findViewById(R.id.textLine);
            holder.imageView = (ImageView) rowView.findViewById(R.id.imageLine);
            holder.linear = (LinearLayout) rowView.findViewById(R.id.MenuItem);

//        }else{
//            holder = (ViewHolder) rowView.getTag();                                     //если строка создана то передаем ее в holder
//        }
        String s = names[position];
        String[] volume = s.split(",");                                                 //разбираем строку на составляюшие
        holder.textView.setText(volume[0]);                                             //меняем текст на первую составляюшую строки
        String patch = "android.resource://ru.home.bahr.cottage/drawable/" + volume[1]; //составляем адрес картинки из второй составляюших строки
        Uri uriImg = Uri.parse(patch);                                                  //парсим рисунок
        holder.imageView.setImageURI(uriImg);
        if(position==id){                                                               //если позиция, выбранасейчас
            holder.linear.setBackgroundResource(R.color.menuItemPushColor);             //меняем свет фона строки
        }
        return rowView;             //возврашаем View строки
    }
}
