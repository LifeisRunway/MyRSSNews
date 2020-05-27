package com.imra.mynews.mvp.presenters;

import com.imra.mynews.mvp.views.RepositoryLikesView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.disposables.Disposable;


/**
 * Date: 01.02.2020
 * Time: 19:03
 *
 * @author IMRA027
 */
public class RepositoryLikesPresenter extends BasePresenter<RepositoryLikesView> {
    public static final String TAG = "RepositoryLikesPresenter";

    private List<Integer> mInProgress = new ArrayList<>();
    private List<Integer> mLikedIds = new ArrayList<>();

    public void toggleLike(int id) {
        if (mInProgress.contains(id)) {
            return;
        }

        mInProgress.add(id);

        getViewState().updateLikes(mInProgress, mLikedIds);

        final Observable<Boolean> toggleObservable = Observable.create(subscriber -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            subscriber.onNext(!mLikedIds.contains(id));
        });

        Disposable dis = toggleObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isLiked -> {
                    onComplete(id, isLiked);
                }, throwable -> {
                    onFail(id);
                });
        unsubscribeOnDestroy(dis);
    }

    private void onComplete(int id, Boolean isLiked) {
        if (!mInProgress.contains(id)) {
            return;
        }

        mInProgress.remove(Integer.valueOf(id));
        if (isLiked) {
            mLikedIds.add(id);
        } else {
            mLikedIds.remove(Integer.valueOf(id));
        }

        getViewState().updateLikes(mInProgress, mLikedIds);
    }

    private void onFail(int id) {
        if (!mInProgress.contains(id)) {
            return;
        }

        mInProgress.remove(Integer.valueOf(id));
        getViewState().updateLikes(mInProgress, mLikedIds);
    }
}
