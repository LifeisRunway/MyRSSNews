package com.imra.mynews.ui.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.TextView;

import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.imra.mynews.di.modules.GlideApp;
//import com.imra.mynews.di.modules.GlideApp;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Date: 12.05.2020
 * Time: 21:19
 *
 * @author IMRA027
 */
public class GlideImageGetter {
    private WeakReference<TextView> container;
    private Context mContext;
    private boolean matchParentWidth;
    //private HtmlImagesHandler imagesHandler;
    private float density = 1.0f;
    Disposable dis;

    public GlideImageGetter(Context context) {this(context,false, false, null);}

    public GlideImageGetter(Context context, boolean matchParentWidth, boolean densityAware,
                            @Nullable HtmlImagesHandler imagesHandler) {
        mContext = context;
        this.matchParentWidth = matchParentWidth;
        //this.imagesHandler = imagesHandler;
        if (densityAware) {
            density = context.getResources().getDisplayMetrics().density;
        }
    }

    public Drawable getDrawable(String source) {

//        if (imagesHandler != null) {
//            imagesHandler.addImage(source);
//        }

        BitmapDrawablePlaceholder drawable = new BitmapDrawablePlaceholder();

        dis = Observable.just(1)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe((temp) ->
                    GlideApp.with(mContext)
                    .asBitmap()
                    .load(source)
                    //.override(150,150)
                    .into(drawable));
        return drawable;
    }

    private class BitmapDrawablePlaceholder extends BitmapDrawable implements Target<Bitmap> {

        protected Drawable drawable;

        BitmapDrawablePlaceholder() {
            super(mContext.getResources(),
                    Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        }

        @Override
        public void draw(final Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
                System.out.println("рисуем");
            }
        }

        private void setDrawable(Drawable drawable) {
            this.drawable = drawable;
            System.out.println("setDrawable");
            //container.get().setText(container.get().getText());
        }

        @Override
        public void onLoadStarted(@Nullable Drawable placeholderDrawable) {
            if(placeholderDrawable != null) {
                setDrawable(placeholderDrawable);
            }
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            if (errorDrawable != null) {
                setDrawable(errorDrawable);
            }
        }

        @Override
        public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
            setDrawable(new BitmapDrawable(mContext.getResources(), bitmap));
            System.out.println("готово");
            dis.dispose();
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholderDrawable) {
            if(placeholderDrawable != null) {
                setDrawable(placeholderDrawable);
                System.out.println("очистка");
            }
        }

        @Override
        public void getSize(@NonNull SizeReadyCallback cb) {
            cb.onSizeReady(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        @Override
        public void removeCallback(@NonNull SizeReadyCallback cb) {}

        @Override
        public void setRequest(@Nullable Request request) {}

        @Nullable
        @Override
        public Request getRequest() {
            return null;
        }

        @Override
        public void onStart() {
            System.out.println("старт");
        }

        @Override
        public void onStop() {
            System.out.println("стоп");
        }

        @Override
        public void onDestroy() {
            System.out.println("дестрой");
        }

    }

    public interface HtmlImagesHandler {
        void addImage(String uri);
    }
}
