package com.imra.mynews.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Date: 13.09.2020
 * Time: 15:41
 *
 * @author IMRA027
 */
public class ListViewFrameSwipeRefreshLayout extends SwipeRefreshLayout {

    private ListView mListViewChild;

    public ListViewFrameSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ListViewFrameSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setListViewChild(ListView listViewChild) {
        mListViewChild = listViewChild;
    }

    @Override
    public boolean canChildScrollUp() {
        if (mListViewChild != null && mListViewChild.getVisibility() == VISIBLE) {
            return mListViewChild.canScrollVertically(-1);
        }

        return super.canChildScrollUp();
    }
}
