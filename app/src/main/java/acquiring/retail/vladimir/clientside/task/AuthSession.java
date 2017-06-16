package acquiring.retail.vladimir.clientside.task;

import android.os.Bundle;

/**
 * Copyright 2017, eosan.ru. All rights reserved.
 * Created by liza on 03.05.2017.
 */

public class AuthSession {
    private final String sessionId;
    private final String clientId;

    public AuthSession(String sessionId, String clientId) {
        this.sessionId = sessionId;
        this.clientId = clientId;
    }

    public AuthSession(Bundle bundle) {
        this.sessionId = bundle.getString("sessionId");
        this.clientId = bundle.getString("clientId");
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getClientId() {
        return clientId;
    }

    public Bundle getBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("sessionId",sessionId);
        bundle.putString("clientId",clientId);
        return bundle;
    }
}
