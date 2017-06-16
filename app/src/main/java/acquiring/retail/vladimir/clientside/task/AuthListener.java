package acquiring.retail.vladimir.clientside.task;

/**
 * Created by Andriod Studio
 * <p>Copyright: Recfaces.com 2017</p>
 *
 * @author Aleksei Feofanov (AFeofanov)
 * @version 1.0
 */

public interface AuthListener {
    public void onSuccess(AuthSession session);
    public void onError(String result);
}
