package com.imra.mynews.ui.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import android.text.Html;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.imra.mynews.R;
import com.imra.mynews.di.modules.GlideApp;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

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
    ViewTreeObserver vto;
    int viewWidth;

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
        vto = container.get().getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                container.get().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //viewHeight = container.get().getMeasuredHeight();
                viewWidth = container.get().getMeasuredWidth();
            }
        });
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
            mSource = " ";
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
                    //.transition(DrawableTransitionOptions.withCrossFade())
                    //.apply(RequestOptions.centerInsideTransform())
                    .placeholder(R.drawable.loading_placeholder)
                    .error(R.drawable.error_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
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

            if(!test.isEmpty()) {
                if(test.get(0) == 1) {
                    mDraw = addWaterMark(drawable, 1);
                } else if(test.get(0) == 2) {
                    mDraw = container.get().getResources().getDrawable(R.drawable.coub_icon);
                } else {
                    mDraw = drawable;
                }

                test.remove(0);
            } else {
                mDraw = drawable;
            }

            int drawableWidth = (int) (mDraw.getIntrinsicWidth() * density);
            int drawableHeight = (int) (mDraw.getIntrinsicHeight() * density);
            //int maxWidth = container.get().getMeasuredWidth();

            int calculatedHeight = viewWidth * drawableHeight / drawableWidth;
            mDraw.setBounds(0, 0, viewWidth, calculatedHeight);
            setBounds(0, 0, viewWidth, calculatedHeight);


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

            int calculatedHeight = viewWidth * drawableHeight / drawableWidth;

            int left = viewWidth/2 - (drawableWidth/2);

            //Log.e("GlideGif", "view width " + viewWidth + " and image width " + drawableWidth);
            if(viewWidth > drawableWidth) {
                drawable.setBounds(left, 0, drawableWidth+left, drawableHeight);
                setBounds(left, 0, drawableWidth+left, drawableHeight);
            } else {
                drawable.setBounds(0, 0, viewWidth, calculatedHeight);
                setBounds(0, 0, viewWidth, calculatedHeight);
            }

            mDrawable.setDrawable(drawable);

            if (drawable instanceof Animatable) {
                mDrawable.setCallback((GlideImageGifGetter) container.get().getTag(R.id.drawable_tag));
                ((Animatable) drawable).start();
            }

            container.get().setText(container.get().getText());
        }



        private Drawable addWaterMark(Drawable drawable, int youtubeOrCoub) {
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

            Bitmap waterMark;
            if(youtubeOrCoub == 1) {
                waterMark = BitmapFactory.decodeResource(container.get().getResources(), R.drawable.youtube_icon);
            } else {
                waterMark = BitmapFactory.decodeResource(container.get().getResources(), R.drawable.coub_icon);
            }
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
            if (placeholderDrawable != null) {
                setDrawableOnLoadStart(placeholderDrawable);
            }
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            if (errorDrawable != null) {
                setDrawableOnLoadStart(errorDrawable);
            }
        }

        @Override
        public void onResourceReady(@NonNull Drawable drawable, @Nullable Transition<? super Drawable> transition) {
            setDrawable(drawable);
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholderDrawable) {
            if(placeholderDrawable != null) {
                setDrawableOnLoadStart(placeholderDrawable);
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