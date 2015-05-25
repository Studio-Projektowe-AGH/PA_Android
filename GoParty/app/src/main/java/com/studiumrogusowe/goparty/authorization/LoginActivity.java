package com.studiumrogusowe.goparty.authorization;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.studiumrogusowe.goparty.R;
import com.studiumrogusowe.goparty.authorization.api.AuthRestAdapter;
import com.studiumrogusowe.goparty.authorization.api.AuthorizationUtilities;
import com.studiumrogusowe.goparty.authorization.api.model.AuthFacebookLoginBody;
import com.studiumrogusowe.goparty.authorization.api.model.AuthLoginBodyObject;
import com.studiumrogusowe.goparty.authorization.api.model.AuthResponseObject;
import com.studiumrogusowe.goparty.test.MainActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class LoginActivity extends AccountAuthenticatorActivity implements LoaderCallbacks<Cursor> {

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public final static String PARAM_USER_PASS = "user_pass";
    private final static int REG_SINGUP = 1337;
    private final String TAG = getClass().getSimpleName();
    int hiddenPass;
    @InjectView(R.id.email_sign_in_button)
    Button mEmailSignInButton;
    @InjectView(R.id.fb_login_button)
    LoginButton fbLoginButton;
    @InjectView(R.id.email)
    AutoCompleteTextView mEmailView;
    @InjectView(R.id.password)
    EditText mPasswordView;
    @InjectView(R.id.register_button)
    Button registerButton;
    private CallbackManager fbCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(TAG, "Oncreate login activity");


        PackageInfo packageInfo;
        String key = null;
        try {
            //getting application package name, as defined in manifest
            String packageName = getApplicationContext().getPackageName();

            //Retriving package info
            packageInfo = getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

                // String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }


        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.sdkInitialize(getApplicationContext());
        fbCallbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        fbLoginButton.setBackgroundResource(R.drawable.fb_login_button);
        fbLoginButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        fbLoginButton.setReadPermissions(Arrays.asList(new String[]{"public_profile", "email", "user_friends"}));

        fbLoginButton.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                String fbAccessToken = loginResult.getAccessToken().getToken();
                Log.d("token",fbAccessToken);
                final AuthFacebookLoginBody authFacebookLoginBody = new AuthFacebookLoginBody();
                authFacebookLoginBody.setAccessToken(fbAccessToken);

                AuthRestAdapter.getInstance().getAuthApi().getToken(authFacebookLoginBody, new Callback<AuthResponseObject>() {
                    @Override
                    public void success(AuthResponseObject authResponseObject, Response response) {
                        Toast.makeText(LoginActivity.this, "You are logged in with Facebook", Toast.LENGTH_SHORT).show();
                        Log.d("token",authResponseObject.getAccess_token());


                        saveAccount(loginResult.getAccessToken().getUserId(), "", authResponseObject.getAccess_token());
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getApplicationContext(), "fail nasz serwer", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "Exception " + exception);
                Log.d(TAG, "Cause " + exception.getCause());
                Toast.makeText(getApplicationContext(), "facebook error", Toast.LENGTH_LONG).show();
            }
        });
        populateAutoComplete();
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signup = new Intent(getBaseContext(), RegisterActivity.class);
                if (getIntent().getExtras() != null)
                    signup.putExtras(getIntent().getExtras());
                startActivityForResult(signup, REG_SINGUP);
            }
        });
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            executeRequest();
        }
    }

    private void executeRequest() {

        final AuthLoginBodyObject authLoginBodyObject = new AuthLoginBodyObject();
        authLoginBodyObject.setEmail(mEmailView.getText().toString());
        authLoginBodyObject.setPassword(mPasswordView.getText().toString());
        AuthRestAdapter.getInstance().getAuthApi().getToken(authLoginBodyObject, new Callback<AuthResponseObject>() {
            @Override
            public void success(AuthResponseObject authResponseObject, Response response) {
                saveAccount(authLoginBodyObject.getEmail(), authLoginBodyObject.getPassword(), authResponseObject.getAccess_token());
                Toast.makeText(LoginActivity.this, "LOGOWANIE UDANE", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                LoginManager.getInstance().logOut();
                Log.d(TAG, "FAILURE CONNECTING TO API");
                Toast.makeText(LoginActivity.this, "FAILURE CONNECTING TO API", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAccount(String email, String password, String accessToken) {
        AccountManager accountManager = AccountManager.get(LoginActivity.this);

        Bundle data = new Bundle();
        data.putString(AccountManager.KEY_ACCOUNT_NAME, email);
        data.putString(AccountManager.KEY_ACCOUNT_TYPE, getIntent().getStringExtra(ARG_ACCOUNT_TYPE));
        data.putString(AccountManager.KEY_AUTHTOKEN, accessToken);

        // save accessToken to shared-prefs
        SharedPreferences prefs = getSharedPreferences("com.studiumrogusowe.goparty", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("token", "Bearer "+accessToken);
        editor.commit();

        final Account account = new Account(email, getIntent().getStringExtra(ARG_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            accountManager.addAccountExplicitly(account, password, null);
            accountManager.setAuthToken(account, AuthorizationUtilities.ACCESS_TOKEN_TYPE
                    , accessToken);
        } else {
            accountManager.setPassword(account, password);
        }

        Intent intent = new Intent();
        intent.putExtras(data);
        setAccountAuthenticatorResult(data);
        setResult(RESULT_OK, intent);
        finish();
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REG_SINGUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void finishLogin(Intent intent) {
        AccountManager accountManager = AccountManager.get(this);
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, getIntent().getStringExtra(ARG_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = AuthorizationUtilities.ACCESS_TOKEN_TYPE;

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            accountManager.addAccountExplicitly(account, accountPassword, null);
            accountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            accountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    public void secretPass(View view) {
        if (++hiddenPass > 2) {
            Intent test = new Intent(getBaseContext(), MainActivity.class);
            startActivity(test);
            hiddenPass = 0;
        }
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

}

