package com.imra.mynews.mvp.views;

import moxy.MvpView;
import moxy.viewstate.strategy.AddToEndSingleStrategy;
import moxy.viewstate.strategy.StateStrategyType;

/**
 * Date: 20.08.2020
 * Time: 20:33
 *
 * @author IMRA027
 */
@StateStrategyType(AddToEndSingleStrategy.class)
public interface LoginView extends MvpView {

    void onClickSignUp ();

    void onClickSignIn ();

    void onClickGoogleSignIn ();

    void firebaseAuthWithGoogle(String idToken);

    void isEnter();

}
