package com.imra.mynews.app;

import com.imra.mynews.mvp.models.RSSFeed;

import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Date: 27.07.2019
 * Time: 17:25
 *
 * @author IMRA027
 */

public interface MyNewsApi {
    @GET("rss")
    Observable<RSSFeed> getRSSFeed();
}
