package com.imra.mynews.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.imra.mynews.R;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.presenters.MainPresenter;
import com.imra.mynews.mvp.presenters.RepositoriesPresenter;
import com.imra.mynews.mvp.views.MainInterface;
import com.imra.mynews.mvp.views.RepositoriesView;
import com.imra.mynews.ui.adapters.RepositoriesAdapter;
import com.imra.mynews.ui.fragments.Fragment;
import com.imra.mynews.ui.views.ListViewFrameSwipeRefreshLayout;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;

/**
 * Date: 28.06.2020
 * Time: 21:15
 *
 * @author IMRA027
 */
public class SavedNewsActivity extends MvpAppCompatActivity implements RepositoriesView, MainInterface, RepositoriesAdapter.OnScrollToBottomListener {

    @InjectPresenter
    MainPresenter mMainPresenter;

    @InjectPresenter
    RepositoriesPresenter mRepositoriesPresenter;

    @BindView(R.id.activity_home_progress_bar_repositories2)
    ProgressBar mRepositoriesProgressBar;
    @BindView(R.id.activity_home_list_view_repositories2)
    ListView mListView;
    @BindView(R.id.activity_home_text_view_no_repositories2)
    TextView mNoRepositoriesTextView;
    @BindView(R.id.toolbar2)
    Toolbar mToolbar;

    @BindView(R.id.activity_home_swipe_refresh_layout2)
    ListViewFrameSwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.activity_home_frame_layout_details2)
    FrameLayout mDetailsFragmeLayout;

    Unbinder unbinder;
    Disposable disposable;
    private RepositoriesAdapter mReposAdapter;
    private AlertDialog mErrorDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_news);

        unbinder = ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle(R.string.saved_rss);

        mSwipeRefreshLayout.setListViewChild(mListView);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mRepositoriesPresenter.offlineNews(true);
        });

        mReposAdapter = new RepositoriesAdapter(getMvpDelegate(), this);
        mListView.setAdapter(mReposAdapter);
        mListView.setOnItemClickListener((AdapterView<?> parent, View view, int pos, long id) -> {
            if(mReposAdapter.getItemViewType(pos) != 0) {
                return;
            }
            mMainPresenter.onRepositorySelection(pos, mReposAdapter.getItem(pos));
        });

        mRepositoriesPresenter.offlineNews(false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(mDetailsFragmeLayout.getVisibility() == View.GONE) {
            onBackPressed();
        } else {
            mDetailsFragmeLayout.setVisibility(View.GONE);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mDetailsFragmeLayout.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            YoYo.with(Techniques.FadeOut)
                    .duration(500)
                    .playOn(mDetailsFragmeLayout);

            disposable = Observable.just(mDetailsFragmeLayout)
                    .subscribeOn(Schedulers.io())
                    .delay(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mDFL -> mDFL.setVisibility(View.GONE));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if(disposable != null) disposable.dispose();
    }

    @Override
    public void setSelection(int position) {
        mReposAdapter.setSelection(position);
    }

    @Override
    public void showDetailsContainer(int position) {
        mDetailsFragmeLayout.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn)
                .duration(800)
                .playOn(mDetailsFragmeLayout);
    }

    @Override
    public void showDetails(int position, Article article) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_home_frame_layout_details2, Fragment.getInstance(position, article))
                .commit();
    }

    @Override
    public void onStartLoading() {
        mSwipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void onFinishLoading() {
        mSwipeRefreshLayout.setEnabled(true);
    }

    @Override
    public void showRefreshing() {
        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(true));
    }

    @Override
    public void hideRefreshing() {
        mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(false));
    }

    @Override
    public void showListProgress() {
        mListView.setVisibility(View.GONE);
        mNoRepositoriesTextView.setVisibility(View.GONE);
        mRepositoriesProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideListProgress() {
        mListView.setVisibility(View.VISIBLE);
        mRepositoriesProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        mErrorDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mRepositoriesPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void hideError() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.hide();
        }
    }

    @Override
    public void setRepositories(RSSFeed repositories) {
        mListView.setEmptyView(mNoRepositoriesTextView);
        mReposAdapter.setRepositories(repositories);
    }

    @Override
    public void setChannelTitle(RSSFeed rssFeed) {

    }

    @Override
    public void addRepositories(RSSFeed repositories) {
        mListView.setEmptyView(mNoRepositoriesTextView);
        mReposAdapter.addRepositories(repositories);
    }

    @Override
    public void setFirestoneMap(Map<String, Object> firestoneMap) {

    }

    @Override
    public void onScrollToBottom() {
        //mRepositoriesPresenter.offlineNews(false);
    }
}
