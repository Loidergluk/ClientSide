package acquiring.retail.vladimir.clientside.task;

/**
 * Created by Andriod Studio
 * <p>Copyright: Recfaces.com 2017</p>
 *
 * @author Aleksei Feofanov (AFeofanov)
 * @version 1.0
 */

public interface DetectListener {
    public void onSuccess(Profile profile);
    public void onError(String result);
}
