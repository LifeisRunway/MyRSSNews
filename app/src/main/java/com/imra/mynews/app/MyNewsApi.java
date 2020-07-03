package com.imra.mynews.app;

import com.imra.mynews.di.common.Scalar;
import com.imra.mynews.di.common.Xml;
import com.imra.mynews.mvp.models.ItemHtml;
import com.imra.mynews.mvp.models.RSSFeed;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
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


    @GET @Xml
    Observable<RSSFeed> getRSSFeed(@Url String mUrl);

    @GET @Scalar
    Observable<Response<String>> findRSSFeeds(@Url String mUrl);

}
