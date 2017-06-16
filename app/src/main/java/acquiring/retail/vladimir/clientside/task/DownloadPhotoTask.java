package acquiring.retail.vladimir.clientside.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;

import static acquiring.retail.vladimir.clientside.task.Service.SERVICE_HOST;

/**
 * Created by Andriod Studio
 * <p>Copyright: Recfaces.com 2017</p>
 *
 * @author Aleksei Feofanov (AFeofanov)
 * @version 1.0
 */

public class DownloadPhotoTask extends AsyncTask<String,Void,Bitmap> {

    private final PhotoListener listener;

    public DownloadPhotoTask(PhotoListener listener) {
        this.listener = listener;
    }

    protected Bitmap doInBackground(String... params) {
        String photoId = params[0];
        String url = SERVICE_HOST+"/api/v1/photo/" + photoId + "/image/data";
        Bitmap image = null;
        try {
            InputStream in = new java.net.URL(url).openStream();
            image = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    protected void onPostExecute(Bitmap result) {
        if (result != null)
            listener.onSuccess(result);
        else
            listener.onError("Load photo error");
    }
}
