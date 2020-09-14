package com.imra.mynews.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.imra.mynews.R;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.presenters.MainPresenter;
import com.imra.mynews.mvp.presenters.RepositoriesPresenter;
import com.imra.mynews.mvp.views.MainInterface;
import com.imra.mynews.mvp.views.RepositoriesView;
import com.imra.mynews.ui.adapters.SearchRSSAdapter;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;

/**
 * Date: 16.05.2020
 * Time: 14:36
 *
 * @author IMRA027
 */
public class FindRSSActivity extends MvpAppCompatActivity implements RepositoriesView, MainInterface {

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
    @BindView(R.id.toolbar_settings)
    Toolbar mToolbar;


    private AlertDialog mErrorDialog;
    Unbinder unbinder;
    SearchRSSAdapter searchRSSAdapter;
    private String mUrl;
    private static final String MY_URL = "url";
    //private static final String MY_SETTINGS = "settings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_rss);

        unbinder = ButterKnife.bind(this);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        searchRSSAdapter = new SearchRSSAdapter(getMvpDelegate(),"");
        mListView.setAdapter(searchRSSAdapter);
        mListView.setOnItemClickListener((AdapterView<?> parent, View view, int pos, long id) -> {
            if(searchRSSAdapter.getItemViewType(pos) != 0) {
                return;
            }
            mMainPresenter.onRepositorySelection(pos, searchRSSAdapter.getItem(pos));
        });
        mSearchButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRepositoriesPresenter.findRSS(false, changeToURL());
            }
        });
        mRepositoriesPresenter.findRSS(false, "");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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
        mListView.setEmptyView(mNoRepositoriesTextView);
        searchRSSAdapter.setRepositories(repositories);
    }

    @Override
    public void setChannelTitle(RSSFeed rssFeed) {

    }

    @Override
    public void addRepositories(RSSFeed repositories) {
        mListView.setEmptyView(mNoRepositoriesTextView);
        searchRSSAdapter.addRepositories(repositories);
    }

    @Override
    public void setSelection(int position) {
        searchRSSAdapter.setSelection(position);
    }

    @Override
    public void showDetailsContainer(int position) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        this.finish();
    }

    @Override
    public void showDetails(int position, Article article) {
        Intent intent = new Intent();
        String description = article.getDescription();
        if (description != null) {
            if ((description.substring(0, 1).equals("/"))) {
                intent.putExtra("url", mUrl + description);
            } else {
                intent.putExtra("url", description);
            }
        }
        intent.putExtra("iconUrl", article.getLink());
        setResult(RESULT_OK,intent);
        this.finish();
    }

}
