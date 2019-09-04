package ru.home.bahr.cottage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ServerConnectUpload {
    private static final String REQUEST_METHOD = "POST";            //метод соединения
    private static final String AUTHORIZATION = "Authorization";    //строка для авторизации на сервере
    private static final String BASIC = "Basic ";                   //тип авторизации

    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;
    private String result = new String();

    private String url;
    private String authorization;
    private String zapros = new String();

    public ServerConnectUpload(String url, String authorization, Map<String, String> setting){         //конструктор класса с необходимыми параметрами
        this.url = url;
        this.authorization = authorization;
        for(Map.Entry entry: setting.entrySet()){
            zapros = zapros + entry.getKey() + "=" + entry.getValue().toString()+"&";
        }
        if(zapros!=null){
            zapros = zapros.substring(0, zapros.length()-1);
        }
    }

    public boolean getUrl(){
        try {
            URL url = new URL(this.url);
            urlConnection = (HttpURLConnection) url.openConnection();                   //определение url
            urlConnection.setRequestMethod(REQUEST_METHOD);                             //обьевление метода передачи данных
            urlConnection.setRequestProperty(AUTHORIZATION, BASIC + authorization);     //отсылаем строку авторизации
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setUseCaches(false);
            urlConnection.setDoOutput(true);

            OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(zapros.getBytes());
            outputStream.flush();

            InputStream inputStream = urlConnection.getInputStream();                   //получение входного потока
            StringBuffer buffer = new StringBuffer();                                   //создание буфера под данные

            reader = new BufferedReader(new InputStreamReader(inputStream));            //создание ridera данных

            String line;
            while ((line = reader.readLine()) != null) {                                //считываем поступаймые данные в буфер, построчно
                buffer.append(line);
            }

            result = buffer.toString();

            if(result.equals("true")){
                return true;
            }else{
                return false;
            }

        }catch (Exception e){
            return false;                                                              //если были ошибки
        }finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

}
