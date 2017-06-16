package acquiring.retail.vladimir.clientside.task;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.JsonReader;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
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

public class UpdateProfileTask extends AsyncTask<String,Void,String> {

    private final AuthSession session;
    private final ProfileListener listener;
    private final Profile profile;

    public UpdateProfileTask(AuthSession session, Profile profile, ProfileListener listener) {
        this.session = session;
        this.listener = listener;
        this.profile = profile;
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener!=null) {
            if (result != null)
                listener.onError(result);
            else
                listener.onSuccess(profile);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected String doInBackground(String... params) {
        if (profile==null || profile.getId().trim().length()==0) {
            return "Не задан профиль";
        } else {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", profile.getId());
                jsonObject.put("clientId", session.getClientId());
                jsonObject.put("phoneNumber", profile.getPhoneNumber());
                jsonObject.put("photoId", profile.getPhotoId());
                jsonObject.put("firstName", profile.getFirstName());
                jsonObject.put("lastName", profile.getLastName());
                jsonObject.put("middleName", profile.getMiddleName());
                URL url = new URL(SERVICE_HOST + "/api/v1/dict/photo-profile/"+profile.getId());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestMethod("PUT");
                    urlConnection.setUseCaches(false);
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("Connection","Close");
                    urlConnection.setRequestProperty("sessionid", session.getSessionId());
                    urlConnection.connect();
                    try (OutputStream os = urlConnection.getOutputStream()) {
                        try (OutputStreamWriter out = new OutputStreamWriter(os)) {
                            System.out.println("PUT "+url.toString()+"\n"+jsonObject.toString());
                            out.write(jsonObject.toString());
                        }
                    }
                    // получение ответа
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        String error = "Ошибка создания профиля: " + responseCode;
                        System.err.println(error);
                        System.err.println(urlConnection.getResponseMessage());
                        try (InputStream is = urlConnection.getErrorStream()) {
                            try (InputStreamReader isr = new InputStreamReader(is)) {
                                try (BufferedReader reader = new BufferedReader(isr)) {
                                    while(reader.ready()) {
                                        String str = reader.readLine();
                                        System.err.println(str);
                                    }
                                }
                            }
                        }
                        return error;
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
