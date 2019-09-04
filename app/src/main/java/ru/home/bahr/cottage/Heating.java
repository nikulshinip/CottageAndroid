package ru.home.bahr.cottage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Heating extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;                                     //левое меню
    private static final String DRAWER_STATE = "DRAVER_STATE";              //указатель на сохраненное положение меню
    private static final int MENU_ID = 0;                                   //выделяет пунк меню в котором сейчас находимся
    private Toolbar mToolbar;                                               //toolBar

    private static final String SAVE_SETTING_FILE = "cottageSetting";       //указатель на файл настроек
    private static final String SERVER_ADRES_TEXT = "SERVER_ADRES_TEXT";    //указатель на сохраненное имя сервера
    private static final String USER_NAME_TEXT = "USER_NAME_TEXT";          //указатель на сохраненное имя пользователя
    private static final String PASSWORD_TEXT = "PASSWORD_TEXT";            //указатель на сохраненный пароль
    private String[] setting = new String[3];
    private static String url;                                              //итоговый url
    private static String autorization;                                     //строка авторизации

    private CheckBox on_offCheckBox;
    private CheckBox powerCheckBox;
    private SeekBar powerSeekBar;
    private TextView powerText;
    private LinearLayout manualPower;
    private RadioButton algorithm1Radio;
    private RadioButton algorithm2Radio;
    private TextView algorithm1Text;
    private SeekBar algorithm1SeekBar;
    private TextView algorithm2Text;
    private SeekBar algorithm2SeekBar;
    private LinearLayout algorithm1Linear;
    private LinearLayout algorithm2Linear;
    private Button applyButtom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heating);
        //------------------------------------------------------------------------------------------
        //определение переменных
        on_offCheckBox = (CheckBox) findViewById(R.id.onOffCheckBox);
        powerCheckBox = (CheckBox) findViewById(R.id.autoPower);
        powerSeekBar = (SeekBar) findViewById(R.id.kvSeekBar);
        powerText = (TextView) findViewById(R.id.kvText);
        manualPower = (LinearLayout) findViewById(R.id.manualPowerRegulator);
        algorithm1Radio = (RadioButton) findViewById(R.id.algoritm1);
        algorithm2Radio = (RadioButton) findViewById(R.id.algoritm2);
        algorithm1Text = (TextView) findViewById(R.id.algoritm1Text);
        algorithm1SeekBar = (SeekBar) findViewById(R.id.algoritm1SeekBar);
        algorithm2Text = (TextView) findViewById(R.id.algoritm2Text);
        algorithm2SeekBar = (SeekBar) findViewById(R.id.algoritm2SeekBar);
        algorithm1Linear = (LinearLayout) findViewById(R.id.algoritm1Linear);
        algorithm2Linear = (LinearLayout) findViewById(R.id.algoritm2Linear);
        applyButtom = (Button) findViewById(R.id.applyButtom);
        //------------------------------------------------------------------------------------------
        //чтение настроек, выполнение запроса данных
        SharedPreferences sPrefs = getSharedPreferences(SAVE_SETTING_FILE, MODE_PRIVATE);
        setting[0] = sPrefs.getString(SERVER_ADRES_TEXT, null);
        setting[1] = sPrefs.getString(USER_NAME_TEXT, null);
        setting[2] = sPrefs.getString(PASSWORD_TEXT, null);
        if(setting[0] != null && setting[1] != null && setting[2] != null){
            url = "http://" + setting[0] + "/boiler/getdata/";        //url
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
            Toast.makeText(Heating.this, R.string.settingError , Toast.LENGTH_LONG).show();
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
        //------------------------------------------------------------------------------------------
        //обработка SeekBar`s
        //регулировка мощности
        powerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                float value = seekBar.getProgress();
                value = (float) (value * 2.5);
                powerText.setText(String.valueOf(value)+"кВт");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //температура комнаты
        algorithm1SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int value = seekBar.getProgress();
                algorithm1Text.setText(String.valueOf(value) + "°C");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //температура обратки
        algorithm2SeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int value = seekBar.getProgress() + 40;
                algorithm2Text.setText(String.valueOf(value) + "°C");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

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
            Intent intent = new Intent(Heating.this, Cottage.class);
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

        public Map<String,String> parametr = new HashMap<String, String>(){{
            put("on_off_options", null);
            put("mode", null);
            put("autoPower", null);
            put("power", null);
            put("on_off", null);
            put("overheating", null);
            put("lamp1", null);
            put("lamp2", null);
            put("lamp3", null);
            put("lamp4", null);
            put("lamp5", null);
            put("lamp6", null);
            put("pump1", null);
            put("floor1_tSetting", null);
            put("back_tSetting", null);
            put("t_boiler", null);
            put("t_obratka", null);
            put("t_out", null);
            put("t_floor1", null);
        }};

        private void onOffSetImage(int imgId, String geter){
            ImageView image = (ImageView) findViewById(imgId);
            if("1".equals(parametr.get(geter))){
                image.setImageResource(R.drawable.heating_lamp_on);
            }else{
                image.setImageResource(R.drawable.heating_lamp_off);
            }
        }

        private void setTemperature(int textId, String geter){
            TextView textView = (TextView) findViewById(textId);
            textView.setText(parametr.get(geter) + "°C");
            if("9999".equals(parametr.get(geter))){
                textView.setTextColor(Color.YELLOW);
            }else{
                textView.setTextColor(Color.WHITE);
            }
        }

        private void setCheckedCheckBox(int checkId, String geter){
            CheckBox checkBox = (CheckBox) findViewById(checkId);
            checkBox.setClickable(true);
            if("1".equals(parametr.get(geter))){
                checkBox.setChecked(true);
            }else{
                checkBox.setChecked(false);
            }
        }

        private void setPowerLamp(int imgId, String geter){
            ImageView image = (ImageView) findViewById(imgId);
            if("1".equals(parametr.get(geter))){
                image.setImageResource(R.drawable.heating_power_lamp_on);
            }else{
                image.setImageResource(R.drawable.heating_power_lamp_off);
            }
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

            if(result == null){
                Toast.makeText(Heating.this, getResources().getString(R.string.updateError), Toast.LENGTH_LONG).show();    //если что не так, то данных нет
                return;
            }

            JSONObject data = null;
            String str = new String();

            try {
                data = new JSONObject(result);          //конвектируем полученную с сервера строку в обьект JSON данных
                for(Map.Entry entry: parametr.entrySet()){
                    str = data.getString(entry.getKey().toString());
                    entry.setValue(str);
                }
            }catch (Exception e){
                Toast.makeText(Heating.this, getResources().getString(R.string.updateError), Toast.LENGTH_LONG).show();    //если что не так, то данных нет
                return;
            }
            //--------------------------------------------------------------------------------------
            //heating_on_off
            onOffSetImage(R.id.onOffDin, "on_off");
            onOffSetImage(R.id.overheating, "overheating");
            onOffSetImage(R.id.pump, "pump1");

            setCheckedCheckBox(R.id.onOffCheckBox, "on_off_options");
            //--------------------------------------------------------------------------------------
            //heating_temperature

            setTemperature(R.id.t_boiler, "t_boiler");
            setTemperature(R.id.t_obratki, "t_obratka");
            setTemperature(R.id.t_out, "t_out");
            setTemperature(R.id.t_floor, "t_floor1");

            //--------------------------------------------------------------------------------------
            //heating_power
            setCheckedCheckBox(R.id.autoPower, "autoPower");

            float power = Float.parseFloat(parametr.get("power"));
            powerText.setText(Float.toString(power) + "кВт");

            int progres = (int)(power / 2.5f);
            powerSeekBar.setProgress(progres);

            if("0".equals(parametr.get("autoPower"))){
                manualPower.setVisibility(View.VISIBLE);
            }else{
                manualPower.setVisibility(View.GONE);
            }

            setPowerLamp(R.id.lamp1, "lamp1");
            setPowerLamp(R.id.lamp2, "lamp2");
            setPowerLamp(R.id.lamp3, "lamp3");
            setPowerLamp(R.id.lamp4, "lamp4");
            setPowerLamp(R.id.lamp5, "lamp5");
            setPowerLamp(R.id.lamp6, "lamp6");

            //--------------------------------------------------------------------------------------
            //heating_algorithm
            algorithm1Radio.setClickable(true);
            algorithm2Radio.setClickable(true);
            if("0".equals(parametr.get("mode"))){
                algorithm1Radio.setChecked(true);

                algorithm1Text.setText(parametr.get("floor1_tSetting") + "°C");

                progres = Integer.parseInt(parametr.get("floor1_tSetting"));
                algorithm1SeekBar.setProgress(progres);
                //----------------------------------------------------------------------------------
                algorithm2Text.setText(parametr.get("back_tSetting") + "°C");

                progres = Integer.parseInt(parametr.get("back_tSetting"));
                progres = progres-40;
                algorithm2SeekBar.setProgress(progres);
                //----------------------------------------------------------------------------------
                algorithm1Linear.setVisibility(View.VISIBLE);
                algorithm2Linear.setVisibility(View.GONE);

            }else if("1".equals(parametr.get("mode"))){
                algorithm2Radio.setChecked(true);

                algorithm2Text.setText(parametr.get("back_tSetting") + "°C");

                progres = Integer.parseInt(parametr.get("back_tSetting"));
                progres = progres-40;
                algorithm2SeekBar.setProgress(progres);
                //----------------------------------------------------------------------------------
                algorithm1Text.setText(parametr.get("floor1_tSetting") + "°C");

                progres = Integer.parseInt(parametr.get("floor1_tSetting"));
                algorithm1SeekBar.setProgress(progres);
                //----------------------------------------------------------------------------------
                algorithm2Linear.setVisibility(View.VISIBLE);
                algorithm1Linear.setVisibility(View.GONE);

            }
            //--------------------------------------------------------------------------------------
            //heating_buttom
            applyButtom.setClickable(true);
        }

    }
    //----------------------------------------------------------------------------------------------
    //обработка клика на CheckBoxe autoPower
    public void onCheckAutoPower(View view){
        if(powerCheckBox.isChecked()){
            manualPower.setVisibility(View.GONE);
        }else{
            manualPower.setVisibility(View.VISIBLE);
        }
    }

    //обработка клика на RadioButtom "по температуре комнаты"
    public void onCheckAlgoritm1(View view){
        if(algorithm1Radio.isChecked()){
            algorithm1Linear.setVisibility(View.VISIBLE);
            algorithm2Linear.setVisibility(View.GONE);
        }
    }

    //обработка клика на RadioButtom "по температуре обратки"
    public void onCheckAlgoritm2(View view){
        if(algorithm2Radio.isChecked()){
            algorithm2Linear.setVisibility(View.VISIBLE);
            algorithm1Linear.setVisibility(View.GONE);
        }
    }
    //----------------------------------------------------------------------------------------------
    //обработка кнопки таймауты
    public void onTimeoutClick(View view){
        Intent intent = new Intent(Heating.this, HeatingTimeout.class);
        startActivity(intent);
        return;
    }
    //обработка кнопки применить
    public void onApplyButtomClick(View view){

        Map<String, String> boilerSetting = new HashMap<String, String>();
        //------------------------------------------------------------------------------------------
        if(on_offCheckBox.isChecked()){
            boilerSetting.put("on_off", "1");
        }else{
            boilerSetting.put("on_off", "0");
        }

        if(powerCheckBox.isChecked()){
            boilerSetting.put("autoPower", "1");
        }else{
            boilerSetting.put("autoPower", "0");
        }

        if(algorithm1Radio.isChecked()){
            boilerSetting.put("mode", "0");
        }

        if(algorithm2Radio.isChecked()){
            boilerSetting.put("mode", "1");
        }

        float value = powerSeekBar.getProgress();
        value = value * 2.5f;
        boilerSetting.put("power", Float.toString(value));

        int val = algorithm1SeekBar.getProgress();
        boilerSetting.put("temp1", Integer.toString(val));

        val = algorithm2SeekBar.getProgress();
        val = val + 40;
        boilerSetting.put("back", Integer.toString(val));
        //------------------------------------------------------------------------------------------
        ApplyBoilerSetting applySetting = new ApplyBoilerSetting(boilerSetting);
        String url = "http://" + setting[0] + "/boiler/setsetting/";
        applySetting.execute(url, autorization);

    }
    //----------------------------------------------------------------------------------------------
    //применение настроек на сервере
    private class ApplyBoilerSetting extends AsyncTask<String, Void, Boolean>{

        private ServerConnectUpload connect;
        private Map<String, String> boilerSetting;

        public ApplyBoilerSetting(Map<String, String> boilerSetting){
            this.boilerSetting = boilerSetting;
        }

        @Override
        protected Boolean doInBackground(String... param){
            connect = new ServerConnectUpload(param[0], param[1], boilerSetting);
            return connect.getUrl();
        }

        @Override
        protected void onPostExecute(Boolean result){
            if(result){
                Toast.makeText(Heating.this, getResources().getString(R.string.applyOk), Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(Heating.this, getResources().getString(R.string.applyFalse), Toast.LENGTH_LONG).show();
            }
            return;
        }
    }

}
