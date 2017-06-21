package acquiring.retail.vladimir.clientside.task;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import static acquiring.retail.vladimir.clientside.task.Service.ENGINE_TYPE_2D;
import static acquiring.retail.vladimir.clientside.task.Service.SERVICE_HOST;

/**
 * Created by Andriod Studio
 * <p>Copyright: Recfaces.com 2017</p>
 *
 * @author Aleksei Feofanov (AFeofanov)
 * @version 1.0
 */

public class DetectTask extends AsyncTask<Bitmap,Void,String> {


    private final AuthSession session;
    private final Profile profile;
    private final DetectListener listener;
    private String portraitPhotoId;

    public DetectTask(AuthSession session, Profile profile, DetectListener listener) {
        this.session = session;
        this.profile = profile;
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener!=null) {
            if (result != null)
                listener.onError(result);
            else if (portraitPhotoId==null)
                listener.onError("Photo detecting error");
            else {
                profile.setPhotoId(portraitPhotoId);
                listener.onSuccess(profile);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private AsyncAction createAsyncAction() throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("typeAction", "addProfile");
        jsonObject.put("status", "new");
        jsonObject.put("clientId", session.getClientId());
        URL url = new URL(SERVICE_HOST + "/ui/v1/registry/async-actions/");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("sessionid", session.getSessionId());
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
                String error = "Ошибка создания aa: " + responseCode;
                System.err.println(error);
                System.err.println(urlConnection.getResponseMessage());
                throw new RuntimeException(error);
            } else {
                try (JsonReader reader = new JsonReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    return new JsonParser().parseAsyncAction(reader);
                }
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private UploadProfile createUploadProfile(AsyncAction aa) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("asyncActionsId", aa.getId());
        jsonObject.put("status", "new");
        jsonObject.put("fileName", "new_photo.jpeg");
        jsonObject.put("toProfileId", profile.getId());
        jsonObject.put("setDefaultPortrait", true);
        URL url = new URL(SERVICE_HOST + "/ui/v1/registry/async-actions/"+aa.getId()+"/upload-profiles/");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("sessionid", session.getSessionId());
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
                String error = "Ошибка создания up: " + responseCode;
                System.err.println(error);
                System.err.println(urlConnection.getResponseMessage());
                throw new RuntimeException(error);
            } else {
                try (JsonReader reader = new JsonReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    return new JsonParser().parseUploadProfile(reader);
                }
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private void startAsyncAction(AsyncAction aa) throws JSONException, IOException {
        URL url = new URL(SERVICE_HOST + "/ui/v1/registry/async-actions/"+aa.getId()+"/run/");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
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
                String error = "Ошибка создания aa: " + responseCode;
                System.err.println(error);
                System.err.println(urlConnection.getResponseMessage());
                throw new RuntimeException(error);
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    private void startUploadProfile(AsyncAction aa,UploadProfile up) throws JSONException, IOException {
        URL url = new URL(SERVICE_HOST + "/ui/v1/registry/async-actions/"+aa.getId()+"/upload-profiles/"+up.getId()+"/start-upload/");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
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
                String error = "Ошибка выполнения start-upload: " + responseCode;
                System.err.println(error);
                System.err.println(urlConnection.getResponseMessage());
                throw new RuntimeException(error);
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void endUploadProfile(AsyncAction aa, UploadProfile up, Photo p) throws JSONException, IOException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("photoId", p.getId());
        jsonObject.put("portraitTypeId", "");
        jsonObject.put("engineTypeId", ENGINE_TYPE_2D);
        URL url = new URL(SERVICE_HOST + "/ui/v1/registry/async-actions/"+aa.getId()+"/upload-profiles/"+up.getId()+"/end-upload/");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("sessionid", session.getSessionId());
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
                String error = "Ошибка выполнения end-upload: " + responseCode;
                System.err.println(error);
                System.err.println(urlConnection.getResponseMessage());
                throw new RuntimeException(error);
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private Photo uploadPhoto(Bitmap image) throws JSONException, IOException {
        UUID rnd = UUID.randomUUID();
        URL url = new URL(SERVICE_HOST + "/api/v1/photo/upload?photoType=2&boxId=00000000-0000-0000-0000-000000000000&msgId="+
                rnd.toString()+"&orgId="+session.getClientId()+"&ext=jpg&fileName="+rnd.toString()+".jpg");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("sessionid", session.getSessionId());
            urlConnection.setRequestProperty("Connection","Close");
            urlConnection.connect();

            try (OutputStream os = urlConnection.getOutputStream()) {
                try(ByteArrayOutputStream bao = new ByteArrayOutputStream()) {
                    image.compress(Bitmap.CompressFormat.JPEG, 90, bao);
                    bao.writeTo(os);
                }
            }
            // получение ответа
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                String error = "Ошибка выполнения end-upload: " + responseCode;
                System.err.println(error);
                System.err.println(urlConnection.getResponseMessage());
                throw new RuntimeException(error);
            } else {
                try (JsonReader reader = new JsonReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    return new JsonParser().parsePhoto(reader);
                }
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private UploadProfile checkUploadProfile(AsyncAction aa, UploadProfile up) throws JSONException, IOException {
        URL url = new URL(SERVICE_HOST + "/ui/v1/registry/async-actions/list-upload-profiles");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setReadTimeout(10000);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestProperty("sessionid", session.getSessionId());
            urlConnection.setRequestProperty("Connection","Close");
            urlConnection.connect();
            try (OutputStream os = urlConnection.getOutputStream()) {
                try (OutputStreamWriter out = new OutputStreamWriter(os)) {
                    out.write("[\""+up.getId()+"\"]");
                }
            }
            // получение ответа
            int responseCode = urlConnection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                String error = "Ошибка выполнения list-upload-profiles: " + responseCode;
                System.err.println(error);
                System.err.println(urlConnection.getResponseMessage());
                throw new RuntimeException(error);
            } else {
                try (JsonReader reader = new JsonReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    UploadProfile[] lst = new JsonParser().parseUploadProfileArray(reader);
                    if (lst.length==1) {
                        return lst[0];
                    }
                }
            }
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private String loadProfilePhoto() throws JSONException, IOException {
        URL url = new URL(SERVICE_HOST + "/api/v1/dict/photo-profile/"+profile.getId());
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
                String error = "Ошибка загрузки профиля: " + responseCode;
                System.err.println(error);
                System.err.println(urlConnection.getResponseMessage());
                throw new RuntimeException(error);
            } else {
                try (JsonReader reader = new JsonReader(new InputStreamReader(urlConnection.getInputStream()))) {
                    Profile p = new JsonParser().parseProfile(reader);
                    if (p!=null) {
                        return p.getPhotoId();
                    }
                }
            }
        } finally {
            urlConnection.disconnect();
        }
        return null;
    }

    @Override
    protected String doInBackground(Bitmap... params) {
        if (profile == null || profile.getId().trim().length()==0) {
            return "Не задан профиль";
        } else {
            portraitPhotoId = null;
            try {
                AsyncAction aa = createAsyncAction();
                if (aa != null) {
                    startAsyncAction(aa);
                    UploadProfile up = createUploadProfile(aa);
                    if (up != null) {
                        startUploadProfile(aa, up);
                        Photo p = uploadPhoto(params[0]);
                        endUploadProfile(aa,up,p);
                        int count = 0;
                        while (!"detected".equals(up.getStatus()) && !"error".equals(up.getStatus())) {
                            Thread.sleep(1000);
                            UploadProfile nup = checkUploadProfile(aa, up);
                            if (nup!=null && nup.getId().equals(up.getId())) {
                                up = nup;
                            }
                            count++;
                            if (count>15) {
                                break;
                            }
                        }
                        if ("detected".equals(up.getStatus())) {
                            portraitPhotoId = loadProfilePhoto();
                            return null;
                        }
                    }
                }
                return "Процесс обработки фото не завершен, попробуйте еще раз";
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        }
    }
}
