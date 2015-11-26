package br.com.brunooliveira.consultatempo;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Created by bo on 04/09/15.
 * Classe que realiza a consulta no servidor de tempo Open Weather Map
 */
public class ConsultaSituacao extends AsyncTask<Void, Void, String> {

    private ConsultaSituacaoTempoListener listener;
    //URL padrão da API. Para maiores informações, consulte o site http://openweathermap.org/
    private static final String URLLocal = "http://api.openweathermap.org/data/2.5/weather?q=Salvador,br&appid=779c8195e6d62b92cda391166bef6f28";

    public ConsultaSituacao(ConsultaSituacaoTempoListener listener){
        this.listener=listener;
    }

    @Override
    protected String doInBackground(Void... params) {

        try {
            String resultado = consultaServidor();

            return interpretaResultado(resultado);
        }catch (IOException e){
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String interpretaResultado(String resultado) throws JSONException {
        JSONObject object = new JSONObject(resultado);

        JSONObject obj = object.getJSONObject("main");
        //Para obter outros parametros fornecidos pelo servidor, deve-se usar o JSONArray, conforme exemplos abaixo.
        /*JSONArray jsonArray = object.getJSONArray("weather");
        JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);

        int id = jsonObjectWeather.getInt("id");
        String desc = jsonObjectWeather.getString("description");
    */
        //Retorna apenas a temperatura atual fazendo a conversão de Fahrenheit em Celsius
        DecimalFormat df = new DecimalFormat("00");
        return "Temperatura em SSA: " + df.format(obj.getDouble("temp")-273);

    }


    private String consultaServidor() throws IOException {
        InputStream is = null;
        try{
            URL url = new URL(URLLocal);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(25000);
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            conn.connect();
            conn.getResponseCode();

            is = conn.getInputStream();

            Reader reader = null;
            reader = new InputStreamReader(is);
            char[] buffer = new char[2048];
            reader.read(buffer);
            return new String(buffer);

        }finally {
            if(is != null){
                is.close();
            }
        }
    }

    @Override
    protected void onPostExecute(String result) {
        listener.onConsultaConcluida(result);
    }

    public interface ConsultaSituacaoTempoListener{
        void onConsultaConcluida(String situacaoTempo);
    }
}
