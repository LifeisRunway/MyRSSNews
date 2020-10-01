package com.imra.mynews.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.imra.mynews.R;
import com.imra.mynews.mvp.presenters.LoginPresenter;
import com.imra.mynews.mvp.views.LoginView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;

/**
 * Date: 03.08.2020
 * Time: 20:40
 *
 * @author IMRA027
 */
public class LoginActivity extends MvpAppCompatActivity implements LoginView {

    @InjectPresenter
    LoginPresenter mLoginPresenter;

    public static final int GOOGLE_SIGN_IN_CODE = 10005;
    private static final String TAG = "Tag";
    @BindView(R.id.sign_up_btn)
    TextView mSignUpBtn;

    @BindView(R.id.login_btn)
    TextView mLoginBtn;

    @BindView(R.id.email)
    EditText mEmailET;

    @BindView(R.id.pass)
    EditText mPassET;

    @BindView(R.id.google)
    ImageView mGoogle;

    private Unbinder unbinder;
    private FirebaseAuth mAuth;
    private GoogleSignInOptions gso;
    private GoogleSignInClient signInClient;
    private GoogleSignInAccount googleSignInAccount;
    private static final int REQUEST_CODE_PERMISSION = 27;
    private final String [] permissions = new String[] {
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_WIFI_STATE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        unbinder = ButterKnife.bind(this);
        if(!hasPermissons()) {
            requestPermissionWithRationale();
        }
        mAuth = FirebaseAuth.getInstance();
        //mLoginPresenter.isEnter();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, gso);

        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
//        if(signInClient != null) {
//            Toast.makeText(this, "User is logged in already!", Toast.LENGTH_SHORT).show();
//        }
        mGoogle.setOnClickListener(v -> mLoginPresenter.onClickGoogleSignIn());
        mSignUpBtn.setOnClickListener(v -> mLoginPresenter.onClickSignUp());
        mLoginBtn.setOnClickListener(v -> mLoginPresenter.onClickSignIn());
    }

    private boolean hasPermissons () {
        int res = 0;

        for (String permission : permissions) {
            res = checkCallingOrSelfPermission(permission);
            if(res != PackageManager.PERMISSION_GRANTED) {return false;}
        }
        return true;
    }

    private void requestPerms () {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_CODE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION :
                for (int res : grantResults) {
                    //if user granted all permissions
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }
                break;
            default:
                allowed = false;
                break;
        }

        if(!allowed) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (String perm : permissions) {
                    if (shouldShowRequestPermissionRationale(perm)) {
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                    } else {
                        showNoPermissionSnackbar();
                    }
                }
            }
        }
    }

    private void showNoPermissionSnackbar() {
        Snackbar.make(LoginActivity.this.findViewById(R.id.activity_login), "Permissions isn't granted", Snackbar.LENGTH_LONG)
                .setAction("SETTINGS", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();
                        Toast.makeText(getApplicationContext(), "Open permissions and granted", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void requestPermissionWithRationale() {
        boolean isDeny = false;

        for(String perm : permissions) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,perm)) {isDeny = true;}
        }

        if(isDeny) {
            final String message = "Permissions is needed to work app";
            Snackbar.make(LoginActivity.this.findViewById(R.id.activity_login), message, Snackbar.LENGTH_LONG)
                    .setAction("GRANT", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPerms();
                        }
                    })
                    .show();
        } else {
            requestPerms();
        }
    }

    private void openApplicationSettings () {
        Intent appSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettings, REQUEST_CODE_PERMISSION);
    }

    @Override
    public void isEnter() {
        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            //Toast.makeText(getApplicationContext(), "Авторизовано", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public void onClickSignUp () {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    @Override
    public void onClickSignIn () {
        if(!TextUtils.isEmpty(mEmailET.getText().toString()) && !TextUtils.isEmpty(mPassET.getText().toString())) {
            mAuth.signInWithEmailAndPassword(mEmailET.getText().toString(), mPassET.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if(task.isSuccessful()) {
                            //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, MainActivity.class));
                            finishAffinity();
                        } else {
                            Toast.makeText(getApplicationContext(), "Check Email or Password!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "Please, enter Email and Password!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClickGoogleSignIn () {
        Intent sign = signInClient.getSignInIntent();
        startActivityForResult(sign, GOOGLE_SIGN_IN_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GOOGLE_SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                //Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                assert account != null;
                mLoginPresenter.firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    @Override
    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        //Log.d(TAG, "signInWithCredential:success");
                        startActivity(new Intent(this, MainActivity.class));
                        finishAffinity();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
