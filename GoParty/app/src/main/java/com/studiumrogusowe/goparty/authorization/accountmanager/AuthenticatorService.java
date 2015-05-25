package com.studiumrogusowe.goparty.authorization.accountmanager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by O10 on 16.04.15.
 */
public class AuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        GoPartyAuthenticator goPartyAuthenticator = new GoPartyAuthenticator(this);

        return goPartyAuthenticator.getIBinder();
    }
}
