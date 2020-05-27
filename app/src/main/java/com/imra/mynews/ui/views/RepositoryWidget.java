package com.imra.mynews.ui.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.view.View;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.presenters.RepositoryPresenter;
import com.imra.mynews.mvp.views.RepositoryView;
import com.imra.mynews.ui.utils.GlideImageGetter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: 29.07.2019
 * Time: 22:52
 *
 * @author IMRA027
 */
public class RepositoryWidget extends AppCompatTextView implements RepositoryView {
    private MvpDelegate mParentDelegate;
    private MvpDelegate mMvpDelegate;
    private Article mArticle;
    private int mPos;

    @InjectPresenter
    RepositoryPresenter mRepositoryPresenter;

    public RepositoryWidget(Context context) {
        super(context);
    }

    public RepositoryWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RepositoryWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public RepositoryWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
    }

    @ProvidePresenter
    RepositoryPresenter provideRepositoryPresenter() {
        return new RepositoryPresenter(mPos, mArticle);
    }

    public void initWidget(MvpDelegate parentDelegate, Article article, int position) {
        mParentDelegate = parentDelegate;
        mArticle = article;
        mPos = position;
        getMvpDelegate().onCreate();
        getMvpDelegate().onAttach();
    }

    public MvpDelegate getMvpDelegate() {

        if (mMvpDelegate == null) {
            mMvpDelegate = new MvpDelegate<>(this);
            mMvpDelegate.setParentDelegate(mParentDelegate, String.valueOf(mPos));
        }

        return mMvpDelegate;
    }

    @Override
    public void showRepository(int position, Article article) {
        setText(clickableSpan(mArticle.getDescription(), Color.BLUE));
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    @NonNull
    public SpannableString clickableSpan (@NonNull String description, @ColorInt int color) {
        GlideImageGetter glideImageGetter = new GlideImageGetter(this);
        CharSequence sequence = Html.fromHtml(description, glideImageGetter, null);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            int start = strBuilder.getSpanStart(span);
            int end = strBuilder.getSpanEnd(span);
            int flags = strBuilder.getSpanFlags(span);
            ClickableSpan clickable = new ClickableSpan() {
                public void onClick(@NonNull View view) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(span.getURL()));
                    getContext().startActivity(browserIntent);
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

}
