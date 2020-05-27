package com.imra.mynews.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.imra.mynews.R;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.ItemHtml;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.presenters.MainPresenter;
import com.imra.mynews.mvp.presenters.RepositoriesPresenter;
import com.imra.mynews.mvp.views.MainInterface;
import com.imra.mynews.mvp.views.RepositoriesView;
import com.imra.mynews.ui.adapters.SearchRSSAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Date: 16.05.2020
 * Time: 14:36
 *
 * @author IMRA027
 */
public class SettingsActivity extends MvpAppCompatActivity implements RepositoriesView, MainInterface {

    @InjectPresenter
    RepositoriesPresenter mRepositoriesPresenter;

    @InjectPresenter
    MainPresenter mMainPresenter;

    @BindView(R.id.activity_settings_progress_bar_repositories)
    ProgressBar mRepositoriesProgressBar;
    @BindView(R.id.activity_settings_list_view_repositories)
    ListView mListView;
    @BindView(R.id.activity_settings_text_view_no_repositories)
    TextView mNoRepositoriesTextView;
    @BindView(R.id.editText)
    EditText mEditText;
    @BindView(R.id.search_button)
    Button mSearchButton;


    private AlertDialog mErrorDialog;
    Unbinder unbinder;
    SearchRSSAdapter searchRSSAdapter;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        unbinder = ButterKnife.bind(this);

        searchRSSAdapter = new SearchRSSAdapter(getMvpDelegate(),"");
        mListView.setAdapter(searchRSSAdapter);
        mListView.setOnItemClickListener((AdapterView<?> parent, View view, int pos, long id) -> {
            if(searchRSSAdapter.getItemViewType(pos) != 0) {
                return;
            }
            mMainPresenter.onRSSSelection(pos, (RSSFeed) searchRSSAdapter.getItem(pos));
        });
        mSearchButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRepositoriesPresenter.findRSS(false, changeToURL());
            }
        });

    }

    private String changeToURL () {
        String input = String.valueOf(mEditText.getText());
        if(!input.matches("^\\w+://")) {
            return mUrl = "http://" + input;
        }
        return mUrl;
    }


    @Override
    public void onStartLoading() {

    }

    @Override
    public void onFinishLoading() {

    }

    @Override
    public void showRefreshing() {

    }

    @Override
    public void hideRefreshing() {

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

    }

    @Override
    public void setRepositories(List<ItemHtml> itemHtml) {
        mListView.setEmptyView(mNoRepositoriesTextView);
        searchRSSAdapter.setRepositories(itemHtml);
    }

    @Override
    public void setChannelTitle(RSSFeed rssFeed) {

    }

    @Override
    public void addRepositories(RSSFeed repositories) {

    }

    @Override
    public void addRepositories(List<ItemHtml> itemHtml) {
        mListView.setEmptyView(mNoRepositoriesTextView);
        searchRSSAdapter.addRepositories(itemHtml);
    }




    @Override
    public void setSelection(int position) {
        searchRSSAdapter.setSelection(position);
    }

    @Override
    public void showDetailsContainer(int position) {
        SharedPreferences sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor e = sp.edit();
        e.putString("url", mUrl);
        e.apply();
        super.onBackPressed();
    }

    @Override
    public void showDetails(int position, Article article) {

    }
}
