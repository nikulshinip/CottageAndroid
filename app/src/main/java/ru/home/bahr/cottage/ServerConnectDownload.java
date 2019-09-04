package ru.home.bahr.cottage;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerConnectDownload {
    private static final String REQUEST_METHOD = "POST";            //метод соединения
    private static final String AUTHORIZATION = "Authorization";    //строка для авторизации на сервере
    private static final String BASIC = "Basic ";                   //тип авторизации

    private String url;
    private String authorization;

    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;
    private String result = new String();

    public ServerConnectDownload(String url, String authorization){         //конструктор класса с необходимыми параметрами
        this.url = url;
        this.authorization = authorization;
    }

    public String getUrl(){     //метод соединение с сервером
        try {
            URL url = new URL(this.url);
            urlConnection = (HttpURLConnection) url.openConnection();                   //определение url
            urlConnection.setRequestMethod(REQUEST_METHOD);                             //обьевление метода передачи данных
            urlConnection.setRequestProperty(AUTHORIZATION, BASIC + authorization);     //отсылаем строку авторизации
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);

            //urlConnection.connect();                                                    //установка соединения

            InputStream inputStream = urlConnection.getInputStream();                   //получение входного потока
            StringBuffer buffer = new StringBuffer();                                   //создание буфера под данные

            reader = new BufferedReader(new InputStreamReader(inputStream));            //создание ridera данных

            String line;
            while ((line = reader.readLine()) != null) {                                //считываем поступаймые данные в буфер, построчно
                buffer.append(line);
            }

            result = buffer.toString();                                                 //делаем из буфера с данными строку

        }catch (Exception e){
            result = null;                                                              //если были ошибки, то строка null
        }finally {
            if(urlConnection!=null) {
                urlConnection.disconnect();
            }
        }

        return result;                                                                  //возврашаем резултат
    }
}