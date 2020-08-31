package com.imra.mynews.mvp.presenters;

import com.imra.mynews.mvp.views.LoginView;

import javax.inject.Singleton;

import moxy.InjectViewState;
import moxy.MvpPresenter;

/**
 * Date: 20.08.2020
 * Time: 20:41
 *
 * @author IMRA027
 */

@Singleton
@InjectViewState
public class LoginPresenter extends MvpPresenter<LoginView> {

    public void isEnter () {
        getViewState().isEnter();
    }

    public void onClickSignUp () {
        getViewState().onClickSignUp();
    }

    public void onClickSignIn () {
        getViewState().onClickSignIn();
    }

    public void onClickGoogleSignIn () {
        getViewState().onClickGoogleSignIn();
    }

    public void firebaseAuthWithGoogle(String idToken) {
        getViewState().firebaseAuthWithGoogle(idToken);
    }

}
