package com.imra.mynews.mvp.views;

import android.os.Bundle;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

/**
 * Date: 25.07.2020
 * Time: 19:47
 *
 * @author IMRA027
 */
@StateStrategyType(AddToEndSingleStrategy.class)
public interface DrawerView extends MvpView {

    void setDrawer (Bundle savedInstanceState);

    void setSubItems (List<String> urls);

    void addNewNewsChannel();

}
