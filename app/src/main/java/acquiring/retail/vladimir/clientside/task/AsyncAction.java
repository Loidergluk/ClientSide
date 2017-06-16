package acquiring.retail.vladimir.clientside.task;

import android.os.Bundle;

/**
 * Created by Andriod Studio
 * <p>Copyright: Recfaces.com 2017</p>
 *
 * @author Aleksei Feofanov (AFeofanov)
 * @version 1.0
 */

public class AsyncAction {
    private final String id;

    public AsyncAction(String id) {
        this.id = id;
    }

    public AsyncAction(Bundle bundle) {
        this.id = bundle.getString("id");
    }

    public String getId() {
        return id;
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        return bundle;
    }
}
