package acquiring.retail.vladimir.clientside.task;

import android.graphics.Bitmap;

/**
 * Created by Andriod Studio
 * <p>Copyright: Recfaces.com 2017</p>
 *
 * @author Aleksei Feofanov (AFeofanov)
 * @version 1.0
 */

public interface PhotoListener {
    public void onSuccess(Bitmap image);
    public void onError(String result);
}
