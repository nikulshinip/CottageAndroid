package ru.home.bahr.cottage;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class CottageMenu {

    private ListView menuList;

    public CottageMenu(final Activity context, int listId, final int id){
        menuList = (ListView) context.findViewById(listId);

        menuList.setAdapter(new CottageMenuAdapter(context, context.getResources().getStringArray(R.array.menu), id));
        //обработка нажатий на пункты меню
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==id){
                    return;
                }
                if (i==0) {   //ОТОПЛЕНИЕ
                    Intent intent = new Intent(context, Heating.class);
                    context.startActivity(intent);
                    return;
                }
                if(i==1){   //ТЕМПЕРАТУРЫ
                    Intent intent = new Intent(context, Temperature.class);
                    context.startActivity(intent);
                    return;
                }
                if(i==2){   //ОБОРУДОВАНИЕ
                    Intent intent = new Intent(context, Device.class);
                    context.startActivity(intent);
                    return;
                }
                if (i == 3) {       //НАСТРОЙКИ
                    Intent intent = new Intent(context, Setting.class);
                    context.startActivity(intent);
                    return;
                }
                if (i == 4) {   //ВЫХОД
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                    return;
                }
                Toast.makeText(context, "Item in position " + i + " clicked", Toast.LENGTH_LONG).show(); //заменить
            }
        });
    }

}