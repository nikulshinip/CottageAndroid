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
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class Setting extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;                                     //левое меню
    private static final String DRAWER_STATE = "DRAVER_STATE";              //указатель на сохраненное положение меню
    private static final int MENU_ID = 3;                                   //выделяет пунк меню в котором сейчас находимся
    private Toolbar mToolbar;                                               //верхнее меню

    private SettingItem serverAdresItem;                                    //item адрес сервера
    private SettingItem userNameItem;                                       //item имя пользователя
    private SettingItem passwordItem;                                       //item пароль
    private static final String SERVER_ADRES_TEXT = "SERVER_ADRES_TEXT";    //указатель на сохраненное имя сервера
    private static final String USER_NAME_TEXT = "USER_NAME_TEXT";          //указатель на сохраненное имя пользователя
    private static final String PASSWORD_TEXT = "PASSWORD_TEXT";            //указатель на сохраненный пароль
    private LinearLayout itemConteiner;                                     //контенер для item

    private static final String SAVE_SETTING_FILE = "cottageSetting";       //указатель на файл настроек
    private static final String EMPTY_TEXT = new String();                  //Пустая строка

    private CheckConnect connect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

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
        //------------------------------------------------------------------------------------------
        SharedPreferences sPrefs = getSharedPreferences(SAVE_SETTING_FILE, MODE_PRIVATE);

        //отрисовка пунктов настроек
        if(savedInstanceState!=null){   //если состаяние активити менялось
            serverAdresItem = new SettingItem(this, getResources().getString(R.string.hostName), savedInstanceState.getString(SERVER_ADRES_TEXT));
            userNameItem = new SettingItem(this, getResources().getString(R.string.userName), savedInstanceState.getString(USER_NAME_TEXT));
            passwordItem = new SettingItem(this, getResources().getString(R.string.password), savedInstanceState.getString(PASSWORD_TEXT));
        }else{                          //если состаяние активити не менялось (первоначальное положение)
            serverAdresItem = new SettingItem(this, getResources().getString(R.string.hostName), sPrefs.getString(SERVER_ADRES_TEXT, EMPTY_TEXT));
            userNameItem = new SettingItem(this, getResources().getString(R.string.userName), sPrefs.getString(USER_NAME_TEXT, EMPTY_TEXT));
            passwordItem = new SettingItem(this, getResources().getString(R.string.password), sPrefs.getString(PASSWORD_TEXT, EMPTY_TEXT));
        }
        passwordItem.setPassword();     //у пароля скрыть вводимые силволы
        itemConteiner = (LinearLayout) findViewById(R.id.itemConteiner);
        itemConteiner.addView(serverAdresItem);     //отрисовка item`s
        itemConteiner.addView(userNameItem);
        itemConteiner.addView(passwordItem);

    }

    //----------------------------------------------------------------------------------------------

    //обработка кнопки проверить соединение
    public void onCheckConnect(View view) throws UnsupportedEncodingException {

        String url = "http://" + serverAdresItem.getText() + "/android/checkconnect/";        //url
        String autorization = userNameItem.getText() + ":" + passwordItem.getText();        //строка для индитификации пользователя
        byte[] data = autorization.getBytes("UTF-8");
        autorization = Base64.encodeToString(data, Base64.NO_PADDING);                      //шифруем строку индентификации

        connect = new CheckConnect();                                                       //устанавливаем соединение и запрашиваем данные
        connect.execute(url, autorization);

    }

    //обработчик кнопки сохранить
    public void onSaveSetting(View view){
        SharedPreferences sPrefs = getSharedPreferences(SAVE_SETTING_FILE, MODE_PRIVATE);   //открываем файл настроек
        SharedPreferences.Editor sPrefsEditor = sPrefs.edit();                              //создаем редактор файла настроек
        sPrefsEditor.putString(SERVER_ADRES_TEXT, serverAdresItem.getText());               //добавляем в файл необходимые поля
        sPrefsEditor.putString(USER_NAME_TEXT, userNameItem.getText());
        sPrefsEditor.putString(PASSWORD_TEXT, passwordItem.getText());
        sPrefsEditor.commit();                                                             //сохраняем файл
    }

    @Override   //обработка кнопок меню (у меня нет)
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //обработчик кнопки меню
        if (id == android.R.id.home) {                  //при нажатии кнопки меню
            mDrawerLayout.openDrawer(Gravity.LEFT);     //открываем левое меню
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override   //обработка кнопки назад
    public void onBackPressed(){
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){           //если открыто меню
            mDrawerLayout.closeDrawer(GravityCompat.START);     //то закрыть его
        }else{                                                  //иначе
            Intent intent = new Intent(Setting.this, Cottage.class);
            startActivity(intent);
            super.onBackPressed();                              //стандартный обработчик
        }
    }

    //----------------------------------------------------------------------------------------------

    @Override   //сохранение состояния Activiti
    protected void onSaveInstanceState(Bundle outState){
        outState.putBoolean(DRAWER_STATE, mDrawerLayout.isDrawerOpen(Gravity.LEFT));    //собираем необходимые данные в Bandle
        outState.putString(SERVER_ADRES_TEXT, serverAdresItem.getText());
        outState.putString(USER_NAME_TEXT, userNameItem.getText());
        outState.putString(PASSWORD_TEXT, passwordItem.getText());
        super.onSaveInstanceState(outState);                                            //сохраняем Bandle
    }

    //----------------------------------------------------------------------------------------------
    private class CheckConnect extends AsyncTask<String, Void, String>{
        private static final String CONNECT = "connect";
        private static final String CONNECT_FOUND ="ok";

        private FrameLayout progressBar = (FrameLayout) findViewById(R.id.progressBar);     //обьект прогресс бара

        private ServerConnectDownload connect;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            progressBar.setVisibility(View.VISIBLE);    //показать прогресбар
            progressBar.setClickable(true);             //запретить нажатия по нижнем кнопкам

        }

        @Override
        protected String doInBackground(String... param){
            connect = new ServerConnectDownload(param[0], param[1]);    //соединяемся с сервером и запрашиваем данные по url
            String result = connect.getUrl();                   //возврашаем полученную от сервера строку
            return result;
        }

        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            JSONObject data = null;
            String connect = new String();

            try {
                data = new JSONObject(result);          //конвектируем полученную с сервера строку в обьект JSON данных
                connect = data.getString(CONNECT);      //получаем данные с заданной строки
            }catch (Exception e){
                connect = null;                         //если что не так, то данных нет
            }

            progressBar.setVisibility(View.INVISIBLE);  //убрать прогресс бар
            progressBar.setClickable(false);

            if(CONNECT_FOUND.equals(connect)){          //сравниваем полученную строку с эталонной и выводим сообщение с результатом
                Toast.makeText(Setting.this, getResources().getString(R.string.connectSuses), Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(Setting.this, getResources().getString(R.string.connectBad), Toast.LENGTH_LONG).show();
            }
        }
    }

}
