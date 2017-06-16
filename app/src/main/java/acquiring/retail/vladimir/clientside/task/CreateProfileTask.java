package acquiring.retail.vladimir.clientside.task;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.JsonReader;

import org.json.JSONObject;

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

public class CreateProfileTask extends AsyncTask<String,Void,String> {

    private final AuthSession session;
    private final ProfileListener listener;
    private final String firstName;
    private final String lastName;
    private final String middleName;
    private final String phone;
    private Profile profile;

    public CreateProfileTask(AuthSession session, String firstName, String lastName, String middleName, String phone, ProfileListener listener) {
        this.session = session;
        this.listener = listener;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.phone = phone;
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener!=null) {
            if (result != null)
                listener.onError(result);
            else {
                System.out.println("Create profile: "+profile.getBundle().toString());
                listener.onSuccess(profile);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {
        if (phone!=null && phone.trim().length()==0) {
            return "Не задан номер телефона";
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("clientId", session.getClientId());
                jsonObject.put("phoneNumber", phone);
                jsonObject.put("firstName", firstName);
                jsonObject.put("lastName", lastName);
                jsonObject.put("middleName", middleName);
                URL url = new URL(SERVICE_HOST + "/api/v1/dict/photo-profile");
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
                    urlConnection.setRequestProperty("sessionid", session.getSessionId());
                    urlConnection.connect();
                    try (OutputStream os = urlConnection.getOutputStream()) {
                        try (OutputStreamWriter out = new OutputStreamWriter(os)) {
                            out.write(jsonObject.toString());
                        }
                    }
                    System.out.println("POST "+SERVICE_HOST + "/api/v1/dict/photo-profile  "+jsonObject.toString());
                    // получение ответа
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        String error = "Ошибка создания профиля: " + responseCode;
                        System.err.println(error);
                        System.err.println(urlConnection.getResponseMessage());
                        return error;
                    } else {
                        try (JsonReader reader = new JsonReader(new InputStreamReader(urlConnection.getInputStream()))) {
                            profile = new JsonParser().parseProfile(reader);
                            try {
                                if (profile == null || profile.getId() == null) {
                                    String error = "Профиль не создан";
                                    System.err.println(error);
                                    return error;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
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
