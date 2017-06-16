package acquiring.retail.vladimir.clientside.task;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.JsonReader;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static acquiring.retail.vladimir.clientside.task.Service.SERVICE_HOST;

/**
 * Created by Andriod Studio
 * <p>Copyright: Recfaces.com 2017</p>
 *
 * @author Aleksei Feofanov (AFeofanov)
 * @version 1.0
 */

public class LoadProfileTask extends AsyncTask<String,Void,String> {

    public static final String PROFILE_NOT_FOUND_ERROR = "Не найден профиль по указанному телефону";
    private final AuthSession session;
    private final ProfileListener listener;
    private Profile profile;

    public LoadProfileTask(AuthSession session,ProfileListener listener) {
        this.session = session;
        this.listener = listener;
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
        if (params == null || params.length==0) {
            return "Не задан номер телефона";
        } else {
            try {
                String phone = params[0].replaceAll("\\+","");
                URL url = new URL(SERVICE_HOST + "/api/v1/dict/photo-profile?sf=phone_number&search="+phone);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setUseCaches(false);
                    urlConnection.setConnectTimeout(10000);
                    urlConnection.setReadTimeout(10000);
                    urlConnection.setRequestProperty("Content-Type", "application/json");
                    urlConnection.setRequestProperty("sessionid", session.getSessionId());
                    urlConnection.setRequestProperty("Connection","Close");
                    urlConnection.connect();
                    // получение ответа
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode != HttpURLConnection.HTTP_OK) {
                        String error = "Ошибка поиска профиля: " + responseCode;
                        System.err.println(error);
                        System.err.println(urlConnection.getResponseMessage());
                        return error;
                    } else {
                        try (JsonReader reader = new JsonReader(new InputStreamReader(urlConnection.getInputStream()))) {
                            List<Profile> profileList = new JsonParser().parseProfileList(reader);
                            try {
                                if (profileList == null || profileList.size()==0) {
                                    String error = PROFILE_NOT_FOUND_ERROR;
                                    System.err.println(error);
                                    return error;
                                } else if (profileList.size()>1) {
                                    String error = "По указанному телефону найдено несколько профилей";
                                    System.err.println(error);
                                    return error;
                                } else {
                                    profile = profileList.get(0);
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
