package com.imra.mynews.mvp.views;

import android.os.Bundle;

import com.imra.mynews.mvp.models.RSSFeed;

import java.util.List;
import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndSingleStrategy;
import moxy.viewstate.strategy.OneExecutionStateStrategy;
import moxy.viewstate.strategy.StateStrategyType;

/**
 * Date: 25.07.2020
 * Time: 19:47
 *
 * @author IMRA027
 */
@StateStrategyType(AddToEndSingleStrategy.class)
public interface DrawerView extends MvpView {

    @StateStrategyType(AddToEndSingleStrategy.class)
    void setDrawer (Bundle savedInstanceState);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void setSubItems (List<String> tags);

    void addSubItem (RSSFeed rssFeed);
}
