package acquiring.retail.vladimir.clientside.task;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.JsonReader;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static acquiring.retail.vladimir.clientside.task.Service.SERVICE_HOST;

/**
 * Created by Andriod Studio
 * <p>Copyright: Recfaces.com 2017</p>
 *
 * @author Aleksei Feofanov (AFeofanov)
 * @version 1.0
 */

public class AuthTask extends AsyncTask<String,Void,String> {

    private final AuthListener listener;
    private AuthSession session;

    public AuthTask(AuthListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener!=null) {
            if (result != null)
                listener.onError(result);
            else if (session==null)
                listener.onError("Ошибка инициализации сессии");
            else
                listener.onSuccess(session);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {
        // params[0] - qr код
        if (params == null || params.length==0 || params[0]==null || params[0].trim().length()==0) {
            return "Отсутствует данные для инициализации";
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("qr", params[0]);
                URL url = new URL(SERVICE_HOST + "/ui/v1/login-qr/");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setUseCaches(false);
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Connection","Close");
                    urlConnection.connect();
                    try (OutputStream os = urlConnection.getOutputStream()) {
                        try (OutputStreamWriter out = new OutputStreamWriter(os)) {
                            out.write(jsonObject.toString());
                        }
                    }
                    // получение ответа
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        String error = "Ошибка инициализации сессии : " + responseCode;
                        System.err.println(error);
                        System.err.println(urlConnection.getResponseMessage());
                        return error;
                    } else {
                        try (InputStream is = urlConnection.getInputStream()) {
                            try (InputStreamReader isr = new InputStreamReader(is)) {
                                try (JsonReader reader = new JsonReader(isr)) {
                                    session = new JsonParser().parseAuthSession(reader);
                                    try {
                                        if (session == null) {
                                            String error = "Ошибка инициализации сессии";
                                            System.err.println(error);
                                            return error;
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }
    }
}
