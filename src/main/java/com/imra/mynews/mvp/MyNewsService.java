package com.imra.mynews.mvp;

import com.imra.mynews.app.MyNewsApi;
import com.imra.mynews.mvp.models.RSSFeed;

import java.util.List;

import rx.Observable;

/**
 * Date: 27.07.2019
 * Time: 20:16
 *
 * @author IMRA027
 */

public class MyNewsService {

    private MyNewsApi myNewsApi;

    public MyNewsService (MyNewsApi myNewsApi) {this.myNewsApi = myNewsApi;}

    public Observable<RSSFeed> getRSSFeed () {
        return myNewsApi.getRSSFeed();
    }

}
