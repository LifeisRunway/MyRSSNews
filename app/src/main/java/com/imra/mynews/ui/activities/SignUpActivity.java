package com.imra.mynews.ui.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.imra.mynews.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import moxy.MvpAppCompatActivity;

/**
 * Date: 14.08.2020
 * Time: 20:46
 *
 * @author IMRA027
 */

public class SignUpActivity extends MvpAppCompatActivity {

    @BindView(R.id.name)
    EditText et_name;

    @BindView(R.id.email)
    EditText et_email;

    @BindView(R.id.pass)
    EditText et_password;

    @BindView(R.id.pass2)
    EditText et_password2;

    @BindView(R.id.login_btn)
    TextView tv_signup;

    private FirebaseAuth mAuth;
    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        unbinder = ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        tv_signup.setOnClickListener(v -> onClickSignUp());
    }

    public void onClickSignUp () {
        if(isNonEmpty()) {
            if(isSixOrMore()) {
                Toast.makeText(getApplicationContext(), "Password must be 6 character or more!", Toast.LENGTH_SHORT).show();
            } else {
                if(isSamePass()) {
                    mAuth.createUserWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString())
                            .addOnCompleteListener(this, task -> {
                                if(task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "User SignUp Successful!", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                } else {
                                    Toast.makeText(getApplicationContext(), "User SignUp Failed!", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(this, "Passwords are not the same!", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Please, enter Email and Password!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNonEmpty () {
        return (!TextUtils.isEmpty(et_email.getText().toString()) && !TextUtils.isEmpty(et_password.getText().toString()) && !TextUtils.isEmpty(et_password2.getText().toString()) && !TextUtils.isEmpty(et_name.getText().toString()));
    }

    private boolean isSixOrMore () {
        return et_password.getText().toString().length() < 6 || et_password2.getText().toString().length() < 6;
    }

    private boolean isSamePass () {
        return TextUtils.equals(et_password.getText().toString(), et_password2.getText().toString());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
