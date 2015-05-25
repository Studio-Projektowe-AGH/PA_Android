package com.studiumrogusowe.goparty.authorization.accountmanager;

/**
 * Created by O10 on 16.04.15.
 */

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.studiumrogusowe.goparty.authorization.LoginActivity;
import com.studiumrogusowe.goparty.authorization.api.AuthRestAdapter;
import com.studiumrogusowe.goparty.authorization.api.AuthorizationUtilities;
import com.studiumrogusowe.goparty.authorization.api.model.AuthRefreshBodyObject;
import com.studiumrogusowe.goparty.authorization.api.model.AuthResponseObject;

/**
 * Created by O10 on 01.04.15.
 */
public class GoPartyAuthenticator extends AbstractAccountAuthenticator {

    private Context context;

    public GoPartyAuthenticator(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse accountAuthenticatorResponse, String s) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {

        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, accountType);
        intent.putExtra(LoginActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(LoginActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        final AccountManager am = AccountManager.get(context);

        final String authToken = am.peekAuthToken(account, authTokenType);
        final String refreshToken = am.peekAuthToken(account, AuthorizationUtilities.REFRESH_TOKEN_TYPE);

        // sprawdzamy czy token jest obecny
        if (!TextUtils.isEmpty(authToken)) {
            final Bundle result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
            return result;
        }

        //mamy refresh token probujemy pozyskać access
        if (!TextUtils.isEmpty(refreshToken)) {
            try {
                AuthRefreshBodyObject authRefreshBodyObject = new AuthRefreshBodyObject(refreshToken);
                final AuthRestAdapter authenticatorRestAdapter = AuthRestAdapter.getInstance();
                final AuthResponseObject responseObject = authenticatorRestAdapter.getAuthApi().refreshToken(authRefreshBodyObject);
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                result.putString(AccountManager.KEY_AUTHTOKEN, responseObject.getAccess_token());
                am.setAuthToken(account, AuthorizationUtilities.REFRESH_TOKEN_TYPE, responseObject.getRefresh_token());
                am.setAuthToken(account, AuthorizationUtilities.ACCESS_TOKEN_TYPE, responseObject.getAccess_token());
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(getClass().getSimpleName(), "Refreshing failed");
            }
        }


        // Oba sposoby zawidoły musimy zalogować się ponownie
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_TYPE, account.type);
        intent.putExtra(LoginActivity.ARG_AUTH_TYPE, authTokenType);
        intent.putExtra(LoginActivity.ARG_ACCOUNT_NAME, account.name);

        final Bundle bundle = new Bundle();
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(String s) {
        return null;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String s, Bundle bundle) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse accountAuthenticatorResponse, Account account, String[] strings) throws NetworkErrorException {
        return null;
    }
}
