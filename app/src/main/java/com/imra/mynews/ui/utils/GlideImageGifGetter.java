package com.imra.mynews.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.graphics.drawable.Animatable2Compat;
import android.text.Html;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.ObjectKey;
import com.imra.mynews.R;
import com.imra.mynews.di.modules.GlideApp;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Date: 12.06.2020
 * Time: 18:06
 *
 * @author IMRA027
 */
public class GlideImageGifGetter implements Html.ImageGetter, Drawable.Callback {
    private WeakReference<TextView> container;
    private boolean matchParentWidth;
    private HtmlImagesHandler imagesHandler;
    private float density = 1.0f;

    //private RequestOptions requestOptions;

    private List<Integer> test;

    public GlideImageGifGetter(TextView textView) {
        this(textView, false, false, null);
    }

    public GlideImageGifGetter(TextView textView, boolean matchParentWidth, HtmlImagesHandler imagesHandler) {
        this(textView, matchParentWidth, false, imagesHandler);
    }

    public GlideImageGifGetter(TextView textView, boolean matchParentWidth, boolean densityAware,
                               @Nullable HtmlImagesHandler imagesHandler) {
        this.container = new WeakReference<>(textView);
        this.matchParentWidth = matchParentWidth;
        this.imagesHandler = imagesHandler;
        container.get().setTag(R.id.drawable_tag, this);
        if (densityAware) {
            density = container.get().getResources().getDisplayMetrics().density;
        }
        test = new ArrayList<>();
//        requestOptions = new RequestOptions()
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .signature()
    }

    @Override
    public Drawable getDrawable(String source) {

        final String mSource;

        if(source.contains("youtube") && source.contains("embed/")) {
            mSource = "http://img.youtube.com/vi/" + source.substring(source.indexOf("embed/") + 6) + "/0.jpg";
            test.add(1);
        }else if(source.contains("coub") && source.contains("embed/")) {
            mSource = "https://coubsecureassets-a.akamaihd.net/assets/brand_assets_og_image-4de4b0738780e78134bbb312d76412f0c3259b5e26323e2f5b899a5cfff5ee3d.png";
            test.add(2);
        } else {
            mSource = source;
            test.add(0);
        }

        if (imagesHandler != null) {
            imagesHandler.addImage(mSource);
        }

        UrlDrawable urlDrawable = new UrlDrawable();
        BitmapDrawablePlaceholder drawable = new BitmapDrawablePlaceholder(urlDrawable);

        container.get().post(() -> {
            GlideApp
                    .with(container.get().getContext())
                    .asDrawable()
                    .load(mSource)
                    .apply(RequestOptions.centerInsideTransform())
                    //.placeholder(R.drawable.load_animation)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(drawable);
        });

        return drawable;
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable who) {
        container.get().invalidate();
    }

    @Override
    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {

    }

    @Override
    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

    }

    private class BitmapDrawablePlaceholder extends BitmapDrawable implements Target<Drawable> {

        protected UrlDrawable mDrawable;
        Drawable mDraw;
        private Disposable dis;

        BitmapDrawablePlaceholder(UrlDrawable drawable) {
            super(container.get().getResources(),
                    Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
            this.mDrawable = drawable;
        }

        @Override
        public void draw(final Canvas canvas) {
            if (mDrawable != null) {
                mDrawable.draw(canvas);
            }
        }

        private void setDrawable(Drawable drawable) {

            if(test.get(0) == 1) {
//                dis = Observable.just(1)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe((tem) -> { mDraw = addWaterMark(drawable);});
                mDraw = addWaterMark(drawable);
            } else if(test.get(0) == 2) {
                mDraw = drawable;
            } else {
                mDraw = drawable;
            }

            test.remove(0);

            int drawableWidth = (int) (mDraw.getIntrinsicWidth() * density);
            int drawableHeight = (int) (mDraw.getIntrinsicHeight() * density);
            int maxWidth = container.get().getMeasuredWidth();

            int calculatedHeight = maxWidth * drawableHeight / drawableWidth;
            mDraw.setBounds(0, 0, maxWidth, calculatedHeight);
            setBounds(0, 0, maxWidth, calculatedHeight);


            mDrawable.setDrawable(mDraw);

            if (mDraw instanceof Animatable) {
                mDrawable.setCallback((GlideImageGifGetter) container.get().getTag(R.id.drawable_tag));
                ((Animatable) mDraw).start();
            }

            container.get().setText(container.get().getText());
        }

        private void setDrawableOnLoadStart (Drawable drawable) {

            int drawableWidth = (int) (drawable.getIntrinsicWidth() * density);
            int drawableHeight = (int) (drawable.getIntrinsicHeight() * density);
            int maxWidth = container.get().getMeasuredWidth();
            //if ((drawableWidth > maxWidth) || matchParentWidth) {
//                int calculatedHeight = maxWidth * drawableHeight / drawableWidth;
//                drawable.setBounds(0, 0, maxWidth, drawableHeight);
//                setBounds(0, 0, maxWidth, calculatedHeight);
            //} else {
                int calculatedHeight = maxWidth * drawableHeight / drawableWidth;
                drawable.setBounds(0, 0, maxWidth, calculatedHeight);
                setBounds(0, 0, maxWidth, calculatedHeight);
            //}


            mDrawable.setDrawable(drawable);

            if (drawable instanceof Animatable) {
                mDrawable.setCallback((GlideImageGifGetter) container.get().getTag(R.id.drawable_tag));
                ((Animatable) drawable).start();
            }

            container.get().setText(container.get().getText());
        }



        private Drawable addWaterMark(Drawable drawable) {
            Bitmap src;
            if (drawable instanceof Animatable) {
                GifDrawable gd = (GifDrawable) drawable;
                src = gd.getFirstFrame();
            }
            else {
                src = ((BitmapDrawable)drawable).getBitmap();
            }

            int w = src.getWidth();
            int h = src.getHeight();

            Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(src, 0, 0, null);

            Bitmap waterMark = BitmapFactory.decodeResource(container.get().getResources(), R.drawable.youtube_icon);
            int resizeW = waterMark.getWidth()/5;
            int resizeH = waterMark.getHeight()/5;
            Bitmap resizeWM = Bitmap.createScaledBitmap(waterMark,resizeW,resizeH,true);
            int left = w/2 - resizeWM.getWidth()/2;
            int top = h/2 - resizeWM.getHeight()/2;
            canvas.drawBitmap(resizeWM,  left, top, null);

            return new BitmapDrawable(container.get().getResources(), result);
        }



        @Override
        public void onLoadStarted(@Nullable Drawable placeholderDrawable) {
//            if(placeholderDrawable != null) {
//                setDrawable(placeholderDrawable);
//            }

            if (placeholderDrawable != null) {
                setDrawableOnLoadStart(placeholderDrawable);
            }
            System.out.println("Загрузка начата");
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
//            if (errorDrawable != null) {
//                setDrawable(errorDrawable);
//            }
            System.out.println("Загрузка провалена");
        }

        @Override
        public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
            setDrawable(drawable);
            System.out.println("Ресурсы загрузились ");
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholderDrawable) {
            if(placeholderDrawable != null) {
                setDrawableOnLoadStart(placeholderDrawable);
            }
            System.out.println("Очистка");
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
        public void onStart() {}

        @Override
        public void onStop() {}

        @Override
        public void onDestroy() {
            if(dis != null) dis.dispose();
        }

    }

    public interface HtmlImagesHandler {
        void addImage(String uri);
    }
}