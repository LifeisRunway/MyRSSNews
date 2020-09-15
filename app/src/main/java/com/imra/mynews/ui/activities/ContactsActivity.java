package com.imra.mynews.ui.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.imra.mynews.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import moxy.MvpAppCompatActivity;

/**
 * Date: 10.09.2020
 * Time: 19:32
 *
 * @author IMRA027
 */
public class ContactsActivity extends MvpAppCompatActivity {

    @BindView(R.id.toolbar_contacts)
    Toolbar mToolbar;

    @BindView(R.id.tv_github_url)
    TextView mGithub;

    @BindView(R.id.tv_github_email)
    TextView mEmail;

    private Unbinder unbinder;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mGithub.setText(clickableSpan(getResources().getString(R.string.lifeisrunway_github), R.color.colorPrimaryDark));
        mGithub.setMovementMethod(LinkMovementMethod.getInstance());
        mEmail.setText(clickableSpan(getResources().getString(R.string.lifeisrunway_email), R.color.colorPrimaryDark));
        mEmail.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @NonNull
    private SpannableString clickableSpan (String link, @ColorInt int color) {

        CharSequence sequence = Html.fromHtml(link);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            int start = strBuilder.getSpanStart(span);
            int end = strBuilder.getSpanEnd(span);
            int flags = strBuilder.getSpanFlags(span);
            ClickableSpan clickable = new ClickableSpan() {
                public void onClick(@NonNull View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(span.getURL()));
                    mGithub.getContext().startActivity(browserIntent);
                }
                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(color);
                    ds.setUnderlineText(false);
                }
            };
            strBuilder.setSpan(clickable, start, end, flags);
            strBuilder.removeSpan(span);
        }
        return new SpannableString(strBuilder);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
