package com.imra.mynews.mvp.presenters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.ColorInt;

import com.github.florent37.glidepalette.GlidePalette;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.imra.mynews.R;
import com.imra.mynews.app.MyNewsApp;
import com.imra.mynews.di.common.ArticleDao;
import com.imra.mynews.di.modules.GlideApp;
import com.imra.mynews.di.modules.GlideRequest;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.views.DrawerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import moxy.InjectViewState;
import moxy.MvpPresenter;

/**
 * Date: 25.07.2020
 * Time: 19:53
 *
 * @author IMRA027
 */
@InjectViewState
public class DrawerPresenter extends MvpPresenter<DrawerView> {

    @Inject
    ArticleDao mAD;

    Bundle bundle;
    List<String> urlRssFeeds;
    List<String> iconRssFeeds;
    List<String> tagRssFeeds;
    List<RSSFeed> mRssFeeds;
    RSSFeed tmpRss;
    FirebaseUser user;
    @ColorInt
    private int colorRss;
    Context mContext;

    Map<String, String> urlsAndIcons;

    public DrawerPresenter (Bundle savedInstanceState) {
        MyNewsApp.getAppComponent().inject3(this);
        bundle = savedInstanceState;
        urlRssFeeds = new ArrayList<>();
        tagRssFeeds = new ArrayList<>();
        mRssFeeds = new ArrayList<>();
        urlsAndIcons = new HashMap<>();
        user = FirebaseAuth.getInstance().getCurrentUser();
        setTemp();
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        getViewState().setDrawer(bundle);
    }

    public void addSubItem(String url) {
        changeBackCol(url);
        tmpRss = mAD.getRssForDrawer(url);
        getViewState().addSubItem(tmpRss);
    }

    public void setSubItems(Map<String, Object> firestoneData, Context context) {
        mContext = context;
        mRssFeeds.clear();
        tagRssFeeds.clear();
        if(!firestoneData.isEmpty()) {
            for(Map.Entry e : firestoneData.entrySet()) {
                if(mAD.getRssForDrawer(e.getKey().toString()) != null) {
                    if(mAD.getRssForDrawer(e.getKey().toString()).getColorChannel() == 0) {
                        changeBackCol(e.getKey().toString());
                    }
                    mRssFeeds.add(mAD.getRssForDrawer(e.getKey().toString()));
                } else {
                    RSSFeed r = new RSSFeed();
                    r.setUrl(e.getKey().toString());
                    r.setIconUrl(e.getValue().toString());
                    String tag = e.getKey().toString()
                            .replaceFirst("[^/]+//(www\\.)*","")
                            .replaceFirst("/.+","");
                    r.setTag(tag);
                    //mAD.insertOrUpdateRss(r);
                    mRssFeeds.add(r);
                }
            }
        }

        if(!mRssFeeds.isEmpty()) {
            for(RSSFeed rssFeed : mRssFeeds) {
                if(!tagRssFeeds.contains(rssFeed.getTag())) {
                    tagRssFeeds.add(rssFeed.getTag());
                }
            }
        }
        getViewState().setSubItems(tagRssFeeds);
    }

    public List<String> getUrlRssFeeds() {
        return urlRssFeeds;
    }

    public void setTemp() {
        mRssFeeds = mAD.getAllRssFeeds();
        if(!mRssFeeds.isEmpty()) {
            for(RSSFeed rssFeed : mRssFeeds) {
                //if(rssFeed.getUrl() != null) {
                urlRssFeeds.add(rssFeed.getUrl());
                //}
            }
        }
    }

    public List<RSSFeed> getRssForTag (String tag) {
        return mAD.getRssAsTag(tag);
    }

    public void addNewNewsChannel(String name) {
        getViewState().addNewNewsChannel(name);
    }

    public boolean checkDouble (String nameChannel) {
        mRssFeeds.clear();
        mRssFeeds = mAD.getAllRssFeeds();
        if(!mRssFeeds.isEmpty()) {
            List<String> temp = new ArrayList<>();
            for (RSSFeed r : mRssFeeds) {

                if(r.getUrl() != null) {
                    temp.add(r.getUrl()
                            .replaceFirst("[^/]+//(www\\.)*","")
                            .replaceFirst("/.+",""));
                }
            }
            for(String s : temp) {
                if(s.equals(nameChannel)) return true;
            }
        }
        return false;
    }

    public String getIconUrl (String url) {
        return mAD.getRssForDrawer(url).getIconUrl();
    }

    @ColorInt
    public int getColorChannel (String url) {
        return mAD.getRssForDrawer(url).getColorChannel();
    }

    public void deleteSubItem(String url) {
        mAD.deleteRssFeed(url);
    }

    public void deleteManySubItems(List<String> urls) {
        mAD.deleteManyRssFeeds(urls);
    }

    public FirebaseUser getUser () {
        return user;
    }

    public void changeBackCol(String url) {
        RSSFeed rssFeed = mAD.getRssForDrawer(url);
        if (rssFeed != null) {
            if(rssFeed.getColorChannel() == 0) {
                GlideRequest<Drawable> request = GlideApp.with(mContext).load(rssFeed.getIconUrl());
                getColorGlidePalette(request,rssFeed,0);
            }
        }
    }

    private void getColorGlidePalette (GlideRequest<Drawable> request, RSSFeed rssFeed, int glideProfile ) {

        switch(glideProfile) {
            case 0 :
                request
                        .listener(GlidePalette.with(rssFeed.getIconUrl())
                                .use(GlidePalette.Profile.VIBRANT_DARK)
                                .intoCallBack(palette -> {
                                    if(palette != null && palette.getVibrantSwatch() != null) {
                                        setColorRss(palette.getVibrantSwatch().getRgb());
                                        rssFeed.setColorChannel(getColorRss());
                                        mAD.updateRss(rssFeed);
                                        Log.e("Таг", rssFeed.getChannelTitle() + " это rssfeed цвет 0 " + getColorRss());
                                    } else getColorGlidePalette(request, rssFeed, 1);
                                })
                                .crossfade(false))
                        .submit();
                break;
            case 1 :
                request
                        .listener(GlidePalette.with(rssFeed.getIconUrl())
                                .use(GlidePalette.Profile.VIBRANT)
                                .intoCallBack(palette -> {
                                    if(palette != null && palette.getVibrantSwatch() != null) {
                                        setColorRss(palette.getVibrantSwatch().getRgb());
                                        rssFeed.setColorChannel(getColorRss());
                                        mAD.updateRss(rssFeed);
                                        Log.e("Таг", rssFeed.getChannelTitle() + " это rssfeed цвет 1 " + getColorRss());
                                    }
                                })
                                .crossfade(false))
                        .submit();
                break;
        }
    }

    public int manipulateColor(int color) {
        //float factor = 0.5f;
        int r = Math.round(Color.red(color));
        int g = Math.round(Color.green(color));
        int b = Math.round(Color.blue(color));

        return Color.argb(210,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

    public int getColorRss() {
        return colorRss;
    }

    public void setColorRss(@ColorInt int colorRss) {
        this.colorRss = colorRss;
    }
}
