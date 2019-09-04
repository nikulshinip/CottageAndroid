package ru.home.bahr.cottage;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class HeatingTimeout extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;                                     //левое меню
    private static final String DRAWER_STATE = "DRAVER_STATE";              //указатель на сохраненное положение меню
    private static final int MENU_ID = -1;                                  //выделяет пунк меню в котором сейчас находимся
    private Toolbar mToolbar;                                               //toolBar

    private static final String SAVE_SETTING_FILE = "cottageSetting";       //указатель на файл настроек
    private static final String SERVER_ADRES_TEXT = "SERVER_ADRES_TEXT";    //указатель на сохраненное имя сервера
    private static final String USER_NAME_TEXT = "USER_NAME_TEXT";          //указатель на сохраненное имя пользователя
    private static final String PASSWORD_TEXT = "PASSWORD_TEXT";            //указатель на сохраненный пароль
    private String[] setting = new String[3];
    private String url;                                                     //итоговый url
    private String urlAdd;
    private String urlOnOff;
    private String autorization;                                            //строка авторизации

    private View dialogCustumTitle;
    private TextView titleText;
    private View dialogCustumMessage;
    private EditText editFromEditText;
    private EditText editToEditText;
    private EditText editCommentEditText;

    private ListView tableList;
    private CheckBox checkBox;
    private Button addButton;
    private Button applyButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heating_timeout);
        //------------------------------------------------------------------------------------------
        //определение переменных
        tableList = (ListView) findViewById(R.id.timeOutList);
        checkBox = (CheckBox) findViewById(R.id.onOffCheckBox);
        addButton = (Button) findViewById(R.id.addButtom);
        applyButton = (Button) findViewById(R.id.applyButtom);

        //------------------------------------------------------------------------------------------
        //чтение настроек, выполнение запроса данных
        SharedPreferences sPrefs = getSharedPreferences(SAVE_SETTING_FILE, MODE_PRIVATE);
        setting[0] = sPrefs.getString(SERVER_ADRES_TEXT, null);
        setting[1] = sPrefs.getString(USER_NAME_TEXT, null);
        setting[2] = sPrefs.getString(PASSWORD_TEXT, null);
        if(setting[0] != null && setting[1] != null && setting[2] != null){
            url = "http://" + setting[0] + "/boiler/gettimeout/";    //url
            urlAdd = "http://" + setting[0] + "/boiler/addtable/";
            urlOnOff = "http://" + setting[0] + "/boiler/setontimeoft/";
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
            Toast.makeText(HeatingTimeout.this, R.string.settingError, Toast.LENGTH_LONG).show();
        }
        //------------------------------------------------------------------------------------------
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
    //----------------------------------------------------------------------------------------------
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
    //----------------------------------------------------------------------------------------------
    @Override   //обработка кнопки назад
    public void onBackPressed(){
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){           //если открыто меню
           mDrawerLayout.closeDrawer(GravityCompat.START);      //то закрыть его
        }
        else{                                                   //иначе
            Intent intent = new Intent(HeatingTimeout.this, Heating.class);
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

            if(result == null){
                Toast.makeText(HeatingTimeout.this, getResources().getString(R.string.updateError), Toast.LENGTH_LONG).show();    //если что не так, то данных нет
                return;
            }

            JSONObject data = null;
            JSONArray names = null;
            //Map<Integer, TimeOut> itemData = new TreeMap<Integer, TimeOut>();
            Map<String, String> itemData;// = new TreeMap<String, String>();
            ArrayList<Map<String,String>> itemList;
            Integer timeOutOnOff = null;

            try {
                data = new JSONObject(result);          //конвектируем полученную с сервера строку в обьект JSON данных
                names = data.names();
                int[] sortNames = new int[names.length()];
                for(int i=0;i<names.length();i++){
                    sortNames[i] = Integer.valueOf(names.getString(i));
                }
                Arrays.sort(sortNames);

                itemList = new ArrayList<Map<String, String>>(data.length()-1);
                for(int i=0;i<sortNames.length;i++){
                    if(i<sortNames.length-1){
                        JSONObject tmp = data.getJSONObject(String.valueOf(sortNames[i]));
                        //itemData.put(i, new TimeOut(Integer.valueOf(names.getString(i).toString()), tmp.getString("from"), tmp.getString("before"), tmp.getString("title")));
                        itemData = new TreeMap<String, String>();
                        itemData.put("line", String.valueOf(sortNames[i]));
                        itemData.put("inTimeOut", tmp.getString("from"));
                        itemData.put("outTimeOut", tmp.getString("before"));
                        itemData.put("comment", tmp.getString("title"));
                        itemList.add(itemData);
                    }else{
                        timeOutOnOff = Integer.valueOf( data.getString(String.valueOf(sortNames[i])) );
                    }
                }

            }catch (Exception e){
                Toast.makeText(HeatingTimeout.this, getResources().getString(R.string.updateError), Toast.LENGTH_LONG).show();    //если что не так, то данных нет
                return;
            }

            //--------------------------------------------------------------------------------------
            // CheckBox on off
            if(timeOutOnOff==1){
                checkBox.setChecked(true);
            }
            checkBox.setClickable(true);

            //--------------------------------------------------------------------------------------

            HeatingTimeoutAdapter sAdapter = new HeatingTimeoutAdapter(HeatingTimeout.this, itemList, autorization, setting[0]);

            ListView list = (ListView) findViewById(R.id.timeOutList);
            list.setAdapter(sAdapter);

            //--------------------------------------------------------------------------------------
            addButton.setClickable(true);
            applyButton.setClickable(true);
        }

    }
    //----------------------------------------------------------------------------------------------
    //обработка кнопки addButtom
    public void addButtomClick(View view){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogCustumTitle = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_title, null);
        dialogBuilder.setCustomTitle(dialogCustumTitle);
        titleText = (TextView) dialogCustumTitle.findViewById(R.id.dialogTitleText);
        titleText.setText(R.string.add);

        dialogCustumMessage = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_edit_message, null);
        dialogBuilder.setView(dialogCustumMessage);
        titleText = (TextView) dialogCustumMessage.findViewById(R.id.dialogMessageText);
        titleText.setText(R.string.addString);
        editFromEditText = (EditText) dialogCustumMessage.findViewById(R.id.fromEditText);
        editToEditText = (EditText) dialogCustumMessage.findViewById(R.id.toEditText);
        editCommentEditText = (EditText) dialogCustumMessage.findViewById(R.id.commentEditText);

        dialogBuilder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Map<String, String> add = new HashMap<String, String>();
                add.put("from", String.valueOf(editFromEditText.getText()));
                add.put("before", String.valueOf(editToEditText.getText()));
                add.put("title", String.valueOf(editCommentEditText.getText()));

                AddItem addItem = new AddItem(add);
                addItem.execute(urlAdd, autorization);
            }
        });

        dialogBuilder.setNegativeButton(R.string.cansel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog addDialog = dialogBuilder.create();
        addDialog.show();
    }
    //----------------------------------------------------------------------------------------------
    //добавление записи в таблицу
    private class AddItem extends AsyncTask<String, Void, Boolean>{
        private ServerConnectUpload connect;
        private Map<String, String> add;

        public AddItem(Map<String, String> add){
            this.add = add;
        }

        @Override
        protected Boolean doInBackground(String... param){
            connect = new ServerConnectUpload(param[0], param[1], add);
            return connect.getUrl();
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result){
                Toast.makeText(HeatingTimeout.this, getResources().getString(R.string.addTrue), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(HeatingTimeout.this, HeatingTimeout.class);
                startActivity(intent);
            }else{
                Toast.makeText(HeatingTimeout.this, getResources().getString(R.string.addError), Toast.LENGTH_LONG).show();
            }
        }
    }
    //----------------------------------------------------------------------------------------------
    //обработка кнопки применить
    public void applyButtonClick(View view){
        Map<String, String> on = new HashMap<String, String>();
        String str;

        if(checkBox.isChecked()){
            str = "1";
        }else{
            str = "0";
        }
        on.put("on_off_timeout", str);

        OnOff onOff = new OnOff(on);
        onOff.execute(urlOnOff, autorization);
    }
    //----------------------------------------------------------------------------------------------
    //включение отключение таймаутов
    private class OnOff extends AsyncTask<String, Void, Boolean>{
        private ServerConnectUpload connect;
        private Map<String, String> on;

        public OnOff(Map<String, String> on){
            this.on = on;
        }

        @Override
        protected Boolean doInBackground(String... param){
            connect = new ServerConnectUpload(param[0], param[1], on);
            return connect.getUrl();
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result){
                Toast.makeText(HeatingTimeout.this, getResources().getString(R.string.applyOk), Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(HeatingTimeout.this, getResources().getString(R.string.applyFalse), Toast.LENGTH_LONG).show();
            }
        }
    }
}
