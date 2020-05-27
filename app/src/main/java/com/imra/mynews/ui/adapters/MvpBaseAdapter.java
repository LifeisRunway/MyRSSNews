package com.imra.mynews.ui.adapters;

import android.widget.BaseAdapter;

import com.arellomobile.mvp.MvpDelegate;

/**
 * Date: 28.07.2019
 * Time: 14:29
 *
 * @author IMRA027
 */
public abstract class MvpBaseAdapter extends BaseAdapter {

    private MvpDelegate<? extends MvpBaseAdapter> mMvpDelegate;
    private MvpDelegate<?> mParentDelegate;
    private String mChildId;

    public MvpBaseAdapter(MvpDelegate<?> parentDelegate, String childId) {
        mParentDelegate = parentDelegate;
        mChildId = childId;

        getMvpDelegate().onCreate();
    }

    public MvpDelegate getMvpDelegate() {
        if (mMvpDelegate == null) {
            mMvpDelegate = new MvpDelegate<>(this);
            mMvpDelegate.setParentDelegate(mParentDelegate, mChildId);

        }
        return mMvpDelegate;
    }

}
