package com.imra.mynews.app;

import com.imra.mynews.di.common.Json;
import com.imra.mynews.di.common.Xml;
import com.imra.mynews.mvp.models.ItemHtml;
import com.imra.mynews.mvp.models.RSSFeed;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Url;

/**
 * Date: 27.07.2019
 * Time: 17:25
 *
 * @author IMRA027
 */

public interface MyNewsApi {

//    @GET("rss")
//    Observable<RSSFeed> getRSSFeed();

    @GET
    @Xml
    Observable<RSSFeed> getRSSFeed(@Url String mUrl);

    @GET
    @Json
    Observable<List<ItemHtml>> findRSSFeeds(@Url String mUrl);

}
