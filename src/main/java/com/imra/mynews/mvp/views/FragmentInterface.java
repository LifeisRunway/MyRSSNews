package com.imra.mynews.mvp.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Date: 27.07.2019
 * Time: 21:33
 *
 * @author IMRA027
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface FragmentInterface extends MvpView {

    void setTitle();


}
