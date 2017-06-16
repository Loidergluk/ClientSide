package acquiring.retail.vladimir.clientside.task;

import android.os.Bundle;

/**
 * Created by Andriod Studio
 * <p>Copyright: Recfaces.com 2017</p>
 *
 * @author Aleksei Feofanov (AFeofanov)
 * @version 1.0
 */

public class UploadProfile {
    private final String id;
    private final String status;

    public UploadProfile(String id, String status) {
        this.id = id;
        this.status = status;
    }

    public UploadProfile(Bundle bundle) {
        this.id = bundle.getString("id");
        this.status = bundle.getString("status");
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("id",id);
        bundle.putString("status",status);
        return bundle;
    }
}
