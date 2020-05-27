package com.imra.mynews.ui.activities;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.imra.mynews.R;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.ItemHtml;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.presenters.MainPresenter;
import com.imra.mynews.mvp.presenters.RepositoriesPresenter;
import com.imra.mynews.mvp.views.MainInterface;
import com.imra.mynews.mvp.views.RepositoriesView;
import com.imra.mynews.ui.adapters.RepositoriesAdapter;
import com.imra.mynews.ui.fragments.Fragment;
import com.imra.mynews.ui.views.FrameSwipeRefreshLayout;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class MainActivity extends MvpAppCompatActivity implements MainInterface, RepositoriesView, RepositoriesAdapter.OnScrollToBottomListener{

    @InjectPresenter
    MainPresenter mMainPresenter;

    @InjectPresenter
    RepositoriesPresenter mRepositoriesPresenter;

    @BindView(R.id.activity_home_swipe_refresh_layout)
    FrameSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.activity_home_progress_bar_repositories)
    ProgressBar mRepositoriesProgressBar;
    @BindView(R.id.activity_home_list_view_repositories)
    ListView mListView;
    @BindView(R.id.activity_home_text_view_no_repositories)
    TextView mNoRepositoriesTextView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView (R.id.tvChanTitle)
    TextView mChannelTitle;




    @BindView(R.id.activity_home_frame_layout_details)
    FrameLayout mDetailsFragmeLayout;

//    @BindView(R.id.activity_home_toolbar)
//    Toolbar toolbar;

    private AlertDialog mErrorDialog;
    private Unbinder unbinder;
    private int visible = View.VISIBLE;

    private RepositoriesAdapter mReposAdapter;

    private Drawer mDrawer = null;
    private AccountHeader mAccountHeader = null;
    private static final int PROFILE_SETTING = 100000;

    SharedPreferences sp;

    private static final String MY_URL = "url";
    private static final String MY_SETTINGS = "settings";

    private int mCheck = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences(MY_SETTINGS, Context.MODE_PRIVATE);
        // проверяем, первый ли раз открывается программа
        boolean hasVisited = sp.getBoolean("hasVisited", false);

        if (!hasVisited) {
            // выводим нужную активность
            SharedPreferences.Editor e = sp.edit();
            e.putBoolean("hasVisited", true);
            e.putString(MY_URL,"https://habr.com/rss");
            e.apply();
        }

        unbinder = ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mDetailsFragmeLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setDrawer(savedInstanceState);

        mDetailsFragmeLayout.setVisibility(View.GONE);

        mSwipeRefreshLayout.setListViewChild(mListView);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mRepositoriesPresenter.loadRepositories(true, sp.getString(MY_URL,""));
        });

        mReposAdapter = new RepositoriesAdapter(getMvpDelegate(), this);
        mListView.setAdapter(mReposAdapter);
        mListView.setOnItemClickListener((AdapterView<?> parent, View view, int pos, long id) -> {
            if(mReposAdapter.getItemViewType(pos) != 0) {
                return;
            }
            mMainPresenter.onRepositorySelection(pos, mReposAdapter.getItem(pos));
        });

    }

    private void setDrawer (Bundle savedInstanceState) {
        // Create a sample profile
        final IProfile profile = new ProfileDrawerItem().withName("Imustrunaway").withEmail("imra027@gmail.com").withIcon("https://avatars2.githubusercontent.com/u/39906544?v=3&s=460").withIdentifier(100);

        mAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true) //полупрозрачная строка состояния?
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profile,
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                        new ProfileSettingDrawerItem().withName("Add Account").withDescription("Add new GitHub Account").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add).actionBar().paddingDp(5).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_SETTING),
                        new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(100001)
                ).withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        //sample usage of the onProfileChanged listener
                        //if the clicked item has the identifier 1 add a new profile ;)
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == PROFILE_SETTING) {
                            int count = 100 + mAccountHeader.getProfiles().size() + 1;
                            IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman" + count).withEmail("batman" + count + "@gmail.com").withIcon("https://avatars3.githubusercontent.com/u/39906544?v=3&s=460").withIdentifier(count);
                            if (mAccountHeader.getProfiles() != null) {
                                //we know that there are 2 setting elements. set the new profile above them ;)
                                mAccountHeader.addProfile(newProfile, mAccountHeader.getProfiles().size() - 2);
                            } else {
                                mAccountHeader.addProfiles(newProfile);
                            }
                        }
                        //false if you have not consumed the event and it should close the drawer
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //create the drawer
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withActionBarDrawerToggle(true)
                .withAccountHeader(mAccountHeader)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_vk).withIdentifier(2),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withIdentifier(3)
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if(drawerItem != null) {
                        Intent intent = null;
                        switch ((int)drawerItem.getIdentifier()) {
                            case 1 :
                                intent = new Intent(MainActivity.this, SettingsActivity.class);
                                break;
                            case 2 :
                                intent = new Intent(MainActivity.this, SettingsActivity.class);
                                break;
                            case 3 :
                                intent = new Intent(MainActivity.this, SettingsActivity.class);
                                break;
                            default:
                                break;
                        }
                        if (intent != null) {
                            MainActivity.this.startActivity(intent);
                        }
                    }
                    return false;
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
//              .withShowDrawerUntilDraggedOpened(true)
                .build();

    }

    @Override
    public void onBackPressed() {
        // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
        if (mDrawer.isDrawerOpen()) {
            mDrawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRepositoriesPresenter.loadRepositories(true, sp.getString(MY_URL,""));
    }

    @Override
    public void showDetailsContainer(int position) {

//        switch (mDetailsFragmeLayout.getVisibility()) {
//            case View.GONE:
//                mDetailsFragmeLayout.setVisibility(View.VISIBLE);
//                mCheck = position;
//                break;
//            case View.VISIBLE:
//                if(mCheck==position)
//                    mDetailsFragmeLayout.setVisibility(View.GONE);
//                else mCheck = position;
//                break;
//            case View.INVISIBLE:
//                break;
//        }

        mDetailsFragmeLayout.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.SlideInUp)
                .duration(800)
                .playOn(mDetailsFragmeLayout);


    }

    @Override
    public void setSelection(int position) {
        mReposAdapter.setSelection(position);
        //Выбранный элемент становится вверху экрана
        //mListView.smoothScrollToPositionFromTop(position,0,0);
    }

    @Override
    public void showDetails(int position, Article article) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_home_frame_layout_details, Fragment.getInstance(position, article))
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
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
    public void setRepositories(RSSFeed repositories) {
        mListView.setEmptyView(mNoRepositoriesTextView);
        mReposAdapter.setRepositories(repositories);
}

    @Override
    public void setRepositories(List<ItemHtml> itemHtml) {

    } //времянка

    @Override
    public void setChannelTitle(RSSFeed rssFeed) {
        mChannelTitle.setText(rssFeed.getChannelTitle());
    }

    @Override
    public void addRepositories(RSSFeed repositories) {
        mListView.setEmptyView(mNoRepositoriesTextView);
        mReposAdapter.addRepositories(repositories);
    }

    @Override
    public void addRepositories(List<ItemHtml> itemHtml) {

    } //времянка

    @Override
    public void showError(String message) {
        mErrorDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mRepositoriesPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void hideError () {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.hide();
        }
    }

    @Override
    public void onScrollToBottom() {
        mRepositoriesPresenter.loadNextRepositories(sp.getString(MY_URL,""));
    }



}
