package ru.home.bahr.cottage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class Device extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;                                     //левое меню
    private static final String DRAWER_STATE = "DRAVER_STATE";              //указатель на сохраненное положение меню
    private static final int MENU_ID = 2;                                   //выделяет пунк меню в котором сейчас находимся
    private Toolbar mToolbar;

    private static final String SAVE_SETTING_FILE = "cottageSetting";       //указатель на файл настроек
    private static final String SERVER_ADRES_TEXT = "SERVER_ADRES_TEXT";    //указатель на сохраненное имя сервера
    private static final String USER_NAME_TEXT = "USER_NAME_TEXT";          //указатель на сохраненное имя пользователя
    private static final String PASSWORD_TEXT = "PASSWORD_TEXT";            //указатель на сохраненный пароль
    private String[] setting = new String[3];
    private static String url;                                              //итоговый url
    private static String autorization;                                     //строка авторизации

    private ListView deviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        //------------------------------------------------------------------------------------------
        //определение переменных
        deviceList = (ListView) findViewById(R.id.deviceList);

        //------------------------------------------------------------------------------------------
        //чтение настроек, выполнение запроса данных
        SharedPreferences sPrefs = getSharedPreferences(SAVE_SETTING_FILE, MODE_PRIVATE);
        setting[0] = sPrefs.getString(SERVER_ADRES_TEXT, null);
        setting[1] = sPrefs.getString(USER_NAME_TEXT, null);
        setting[2] = sPrefs.getString(PASSWORD_TEXT, null);
        if(setting[0] != null && setting[1] != null && setting[2] != null){
            url = "http://" + setting[0] + "/android/getonewaredata/";        //url
            autorization = setting[1] + ":" + setting[2];            //строка для индитификации пользователя
            byte[] data = new byte[0];

            try {
                data = autorization.getBytes("UTF-8");
                autorization = Base64.encodeToString(data, Base64.NO_PADDING);                      //шифруем строку индентификации
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            Update getData = new Update();
            getData.execute(url, autorization);

        }else{
            Toast.makeText(Device.this, R.string.settingError, Toast.LENGTH_LONG).show();
        }

        //------------------------------------------------------------------------------------------
        //тулбар
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        //отрисовка левого слоя выезжающего меню
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        if(savedInstanceState!=null){
            if(savedInstanceState.getBoolean(DRAWER_STATE)){
                mDrawerLayout.openDrawer(Gravity.LEFT);                 //открытие левого меню
            }
        }

        //создание обработчика для поведения слоя меню (скрыть клавиатуру при открытии меню)
        ListernedDrawerLayout onList = new ListernedDrawerLayout(this);
        mDrawerLayout.setDrawerListener(onList);

        //отрисовка меню
        CottageMenu menu = new CottageMenu(this, R.id.left_drawer, MENU_ID);

    }
    //----------------------------------------------------------------------------------------------
    @Override   //стандартное меню
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_heating, menu);
        return true;
    }

    @Override   //обработка кнопок меню
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //обработчик кнопки меню
        if (id == android.R.id.home) {                  //при нажатии кнопки меню
            mDrawerLayout.openDrawer(Gravity.LEFT);     //открываем левое меню
            return true;
        }
        if (id == R.id.refresh){
            Update update = new Update();
            update.execute(url, autorization);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override   //обработка кнопки назад
    public void onBackPressed(){
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){           //если открыто меню
            mDrawerLayout.closeDrawer(GravityCompat.START);     //то закрыть его
        }
        else{                                                   //иначе
            Intent intent = new Intent(Device.this, Cottage.class);
            startActivity(intent);
            super.onBackPressed();                              //стандартный обработчик
        }
    }
    //----------------------------------------------------------------------------------------------
    @Override   //сохранение состояния Activiti
    protected void onSaveInstanceState(Bundle outState){
        outState.putBoolean(DRAWER_STATE, mDrawerLayout.isDrawerOpen(Gravity.LEFT));    //собираем необходимые данные в Bandle
        super.onSaveInstanceState(outState);                                            //сохраняем Bandle
    }
    //----------------------------------------------------------------------------------------------
    //первоначальная загрузка, и кнопка обновить
    private class Update extends AsyncTask<String, Void, String> {

        private ServerConnectDownload connect;

        @Override
        protected String doInBackground(String... param){
            connect = new ServerConnectDownload(param[0], param[1]);    //соединяемся с сервером и запрашиваем данные по url
            String result = connect.getUrl();                   //возврашаем полученную от сервера строку
            return result;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            //--------------------------------------------------------------------------------------
            if(result == null){
                Toast.makeText(Device.this, getResources().getString(R.string.updateError), Toast.LENGTH_LONG).show();    //если что не так, то данных нет
                return;
            }
            //--------------------------------------------------------------------------------------
            JSONObject data;
            JSONObject temperature;
            JSONObject ds2408;
            JSONArray namesTemperature;
            JSONArray namesDS2408;
            ArrayList<Map<String, String>> temperatureList;
            Map<String, String> temperatureData;
            ArrayList<Map<String, String>> ds2408List;
            Map<String, String> ds2408Data;
            //--------------------------------------------------------------------------------------
            try{

                data = new JSONObject(result);          //конвектируем полученную с сервера строку в обьект JSON данных
                temperature = data.getJSONObject("temperature");
                ds2408 = data.getJSONObject("ds2408");

                namesTemperature = temperature.names();
                namesDS2408 = ds2408.names();

                int[] sortNamesTemperature = new int[namesTemperature.length()];
                int[] sortNamesDS2408 = new int[namesDS2408.length()];

                for(int i=0;i<namesTemperature.length();i++){
                    sortNamesTemperature[i] = Integer.valueOf(namesTemperature.getString(i));
                }
                Arrays.sort(sortNamesTemperature);
                for(int i=0;i<namesDS2408.length();i++){
                    sortNamesDS2408[i] = Integer.valueOf(namesDS2408.getString(i));
                }
                Arrays.sort(sortNamesDS2408);

                temperatureList = new ArrayList<Map<String, String>>(sortNamesTemperature.length);
                for(int i=0;i<sortNamesTemperature.length;i++){
                    JSONObject tmp = temperature.getJSONObject(String.valueOf(sortNamesTemperature[i]));
                    temperatureData = new TreeMap<String, String>();
                    temperatureData.put("id", String.valueOf(sortNamesTemperature[i]));
                    temperatureData.put("address", tmp.getString("address"));
                    temperatureData.put("temperatur", tmp.getString("temperatur"));
                    temperatureData.put("title", tmp.getString("title"));
                    temperatureList.add(temperatureData);
                }

                ds2408List = new ArrayList<Map<String, String>>(sortNamesDS2408.length);
                for(int i=0;i<sortNamesDS2408.length;i++){
                    JSONObject tmp = ds2408.getJSONObject(String.valueOf(sortNamesDS2408[i]));
                    ds2408Data = new TreeMap<String, String>();
                    ds2408Data.put("id", String.valueOf(sortNamesDS2408[i]));
                    ds2408Data.put("address", tmp.getString("address"));
                    ds2408Data.put("title", tmp.getString("title"));
                    ds2408Data.put("connect", tmp.getString("connect"));
                    ds2408Data.put("din1", tmp.getString("din1"));
                    ds2408Data.put("din2", tmp.getString("din2"));
                    ds2408Data.put("din3", tmp.getString("din3"));
                    ds2408Data.put("din4", tmp.getString("din4"));
                    ds2408Data.put("dout1", tmp.getString("dout1"));
                    ds2408Data.put("dout2", tmp.getString("dout2"));
                    ds2408Data.put("dout3", tmp.getString("dout3"));
                    ds2408List.add(ds2408Data);
                }

            }catch (Exception e){
                Toast.makeText(Device.this, getResources().getString(R.string.updateError), Toast.LENGTH_LONG).show();    //если что не так, то данных нет
                return;
            }
            //--------------------------------------------------------------------------------------

            DeviceAdapter dAdapter = new DeviceAdapter(Device.this, temperatureList, ds2408List);

            deviceList.setAdapter(dAdapter);

            //--------------------------------------------------------------------------------------

        }

    }
}
