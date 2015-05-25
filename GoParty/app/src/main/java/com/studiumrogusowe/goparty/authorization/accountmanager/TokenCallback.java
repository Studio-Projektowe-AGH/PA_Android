package com.studiumrogusowe.goparty.authorization.accountmanager;

import android.accounts.Account;

/**
 * Created by O10 on 16.04.15.
 */
public interface TokenCallback {
    void onTokenAquired(String accessToken, Account account);

    void onFailed();
}