package ve.com.willicab.radiognu;

import android.os.AsyncTask;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public abstract class GetJSON extends AsyncTask<String, String, String> {
    private JSONObject data;
    //View v;
    protected GetJSON() {}

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buffer = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            String finalJSON = buffer.toString();
            data = new JSONObject(finalJSON);
            return "ok";

        } catch (IOException | JSONException e) {
            String ErrorJSON = "{\"error\":1,\"descripcion\":\"Error desconocido\"}";
            try {
                data = new JSONObject(ErrorJSON);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "fail";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (data == null) return;
        try {
            onGetJson(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public abstract void onGetJson(JSONObject data) throws JSONException;
}