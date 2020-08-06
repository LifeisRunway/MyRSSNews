package com.imra.mynews.ui.activities;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.imra.mynews.R;
import com.imra.mynews.di.modules.GlideApp;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.ItemHtml;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.presenters.DrawerPresenter;
import com.imra.mynews.mvp.presenters.MainPresenter;
import com.imra.mynews.mvp.presenters.RepositoriesPresenter;
import com.imra.mynews.mvp.views.DrawerView;
import com.imra.mynews.mvp.views.MainInterface;
import com.imra.mynews.mvp.views.RepositoriesView;
import com.imra.mynews.ui.adapters.RepositoriesAdapter;
import com.imra.mynews.ui.fragments.Fragment;
import com.imra.mynews.ui.utils.GlideImageGetter;
import com.imra.mynews.ui.views.FrameSwipeRefreshLayout;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ExpandableBadgeDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import java.util.List;
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
import moxy.presenter.ProvidePresenter;


public class MainActivity extends MvpAppCompatActivity implements MainInterface, RepositoriesView, DrawerView, RepositoriesAdapter.OnScrollToBottomListener{

    @InjectPresenter
    MainPresenter mMainPresenter;

    @InjectPresenter
    RepositoriesPresenter mRepositoriesPresenter;

    @InjectPresenter
    DrawerPresenter mDrawerPresenter;

//    @Inject
//    SharedPreferences mDrawerPresenter.getSP(;
//
//    @Inject
//    SharedPreferences.Editor mDrawerPresenter.getSPEditor();

//    @Inject
//    boolean isVisited;

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
    @BindView (R.id.tvChanDesc)
    TextView mChannelDescription;

    @BindView(R.id.activity_home_frame_layout_details)
    FrameLayout mDetailsFrameLayout;

    private AlertDialog mErrorDialog;
    private Unbinder unbinder;
    private int visible = View.VISIBLE;


    private RepositoriesAdapter mReposAdapter;

    private Drawer mDrawer = null;
    private AccountHeader mAccountHeader = null;
    private static final int PROFILE_SETTING = 100000;
    int num = 1;

    //SharedPreferences sp;

    private String oldUrl;

    private static final String MY_URL = "url";
    private static final String MY_SETTINGS = "settings";

    private int mCheck = -1;

    Disposable disposable;
    Context mContext;
    int identif = 1;

    ExpandableBadgeDrawerItem expDrawItem;
    Bundle mBundle;
    FirebaseUser user;
    String uName;
    String uEmail;
    Uri uIcon;

    @ProvidePresenter
    DrawerPresenter provideDrawerPresenter () { return new DrawerPresenter(mBundle); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBundle = savedInstanceState;
        mContext = this;

        unbinder = ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mDetailsFrameLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }

        //Перенести это дерьмо в какой-нибудь Presenter
        //user = FirebaseAuth.getInstance().getCurrentUser();
        if (mDrawerPresenter.getUser() != null) {
            uName = mDrawerPresenter.getUser().getDisplayName();
            uEmail = mDrawerPresenter.getUser().getEmail();
            uIcon = mDrawerPresenter.getUser().getPhotoUrl();
        } else {
            uName = "Null";
            uEmail = "null@null";
            uIcon = Uri.parse("https://avatars2.githubusercontent.com/u/39906544?v=3&s=460");
        }

        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDetailsFrameLayout.setVisibility(View.GONE);

        oldUrl = mMainPresenter.getSP().getString(MY_URL,"");

        mSwipeRefreshLayout.setListViewChild(mListView);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mRepositoriesPresenter.loadRepositories(true, oldUrl, isConnected()));

        mReposAdapter = new RepositoriesAdapter(getMvpDelegate(), this);
        mListView.setAdapter(mReposAdapter);
        mListView.setOnItemClickListener((AdapterView<?> parent, View view, int pos, long id) -> {
            if(mReposAdapter.getItemViewType(pos) != 0) {
                return;
            }
            mMainPresenter.onRepositorySelection(pos, mReposAdapter.getItem(pos));
        });


        mRepositoriesPresenter.loadRepositories(false, oldUrl, isConnected());
    }

    private boolean isConnected () {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = (cm != null) ? cm.getActiveNetworkInfo() : null;
        return nInfo != null && nInfo.isConnected();
    }

    @Override
    public void onBackPressed() {

        if (mDetailsFrameLayout.getVisibility() == View.VISIBLE) {
            YoYo.with(Techniques.FadeOut)
                    .duration(500)
                    .playOn(mDetailsFrameLayout);

            disposable = Observable.just(mDetailsFrameLayout)
                    .subscribeOn(Schedulers.io())
                    .delay(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mDFL -> mDFL.setVisibility(View.GONE));
        } else if (!mDrawer.isDrawerOpen()) {
            mDrawer.openDrawer(); // Закрываем Navigation Drawer по нажатию системной кнопки "Назад" если он открыт
        } else {
            finishAffinity();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
            if(mMainPresenter.isUrl(oldUrl)) {
                oldUrl = mMainPresenter.getSP().getString(MY_URL,"");
                mRepositoriesPresenter.loadRepositories(true, oldUrl, isConnected());
                mDrawerPresenter.addSubItem(oldUrl);
            } else {mRepositoriesPresenter.loadRepositories(true, oldUrl, isConnected());}

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if(disposable != null) disposable.dispose();
    }


    @Override
    public void showDetailsContainer(int position) {
        mDetailsFrameLayout.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn)
                .duration(800)
                .playOn(mDetailsFrameLayout);
    }

    @Override
    public void showDetailsContainer(int position, ItemHtml itemHtml) {

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
        //mDrawerPresenter.setSubItems();
}

    @Override
    public void setRepositories(List<ItemHtml> itemHtml) {

    } //времянка

    @Override
    public void setChannelTitle(RSSFeed rssFeed) {
        mChannelTitle.setText(rssFeed.getChannelTitle());
        if(rssFeed.getChannelDescription() != null) {
            mChannelDescription.setText(rssFeed.getChannelDescription());
            if(mChannelDescription.getVisibility() == View.GONE) { mChannelDescription.setVisibility(View.VISIBLE); }
        } else mChannelDescription.setVisibility(View.GONE);
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
        mRepositoriesPresenter.loadNextRepositories(mMainPresenter.getSP().getString(MY_URL,""), isConnected());
    }

    // Боковая панель
    @Override
    public void setDrawer (Bundle savedInstanceState) {

        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                GlideApp.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                GlideApp.with(imageView.getContext()).clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                //define different placeholders for different imageView targets
                //default tags are accessible via the DrawerImageLoader.Tags
                //custom ones can be checked via string. see the CustomUrlBasePrimaryDrawerItem LINE 111
                if (DrawerImageLoader.Tags.PROFILE.name().equals(tag)) {
                    return DrawerUIUtils.getPlaceHolder(ctx);
                } else if (DrawerImageLoader.Tags.ACCOUNT_HEADER.name().equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(com.mikepenz.materialdrawer.R.color.primary).sizeDp(56);
                } else if ("customUrlItem".equals(tag)) {
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_red_500).sizeDp(56);
                }
                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()
                return super.placeholder(ctx, tag);
            }
        });

        // Create a sample profile
        final IProfile profile = new ProfileDrawerItem().withName(uName).withEmail(uEmail).withIcon(uIcon).withIdentifier(100);

        mAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true) //полупрозрачная строка состояния?
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profile,
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                        new ProfileSettingDrawerItem().withName("Add Account").withDescription("Add new GitHub Account").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_add).actionBar().paddingDp(5).colorRes(R.color.material_drawer_primary_text)).withIdentifier(PROFILE_SETTING),
                        new ProfileSettingDrawerItem().withName("Manage Account").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(100001),
                        new ProfileSettingDrawerItem().withName("Exit Account").withIcon(GoogleMaterial.Icon.gmd_exit_to_app).withIdentifier(100002)

                ).withOnAccountHeaderListener((view, profile1, current) -> {
                    //sample usage of the onProfileChanged listener
                    //if the clicked item has the identifier 1 add a new profile ;)
                    int count = 100 + mAccountHeader.getProfiles().size() + 1;
                    if (profile1 instanceof IDrawerItem && profile1.getIdentifier() == PROFILE_SETTING) {
                        IProfile newProfile = new ProfileDrawerItem().withNameShown(true).withName("Batman" + count).withEmail("batman" + count + "@gmail.com").withIcon("https://avatars3.githubusercontent.com/u/39906544?v=3&s=460").withIdentifier(count);
                        if (mAccountHeader.getProfiles() != null) {
                            //we know that there are 2 setting elements. set the new profile above them ;)
                            mAccountHeader.addProfile(newProfile, mAccountHeader.getProfiles().size() - 2);
                        } else {
                            mAccountHeader.addProfiles(newProfile);
                        }
                    }
                    if (profile1 instanceof IDrawerItem && profile1.getIdentifier() == 100001) {
                        if (mAccountHeader.getProfiles().size() > 3)
                            mAccountHeader.removeProfile(mAccountHeader.getProfiles().size() - 3);
                    }
                    if (profile1 instanceof IDrawerItem && profile1.getIdentifier() == 100002) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(this, LoginActivity.class));
                        finishAffinity();
                    }


                    //false if you have not consumed the event and it should close the drawer
                    return false;
                })
                .withSavedInstance(savedInstanceState)
                .build();

        if(!mDrawerPresenter.getUrlRssFeeds().isEmpty()) {
            expDrawItem = new ExpandableBadgeDrawerItem().withName("Новостные ленты").withIcon(R.drawable.youtube_icon).withIdentifier(3).withSelectable(false).withBadgeStyle(new BadgeStyle().withTextColorRes(R.color.colorText).withColorRes(R.color.colorAccent)).withBadge("0").withSubItems().withIsExpanded(true);
        } else {
            expDrawItem = new ExpandableBadgeDrawerItem().withName("Новостные ленты").withIcon(R.drawable.youtube_icon).withIdentifier(3).withSelectable(false).withBadgeStyle(new BadgeStyle().withTextColorRes(R.color.colorText).withColorRes(R.color.colorAccent)).withBadge("0").withSubItems(
                    new SecondaryDrawerItem()
                            .withName("Нет новостных лент")
                            .withLevel(3)
                            .withIdentifier(2000).withSetSelected(false).withEnabled(false)).withIsExpanded(true);
        }

        //create the drawer
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withActionBarDrawerToggle(true)
                .withAccountHeader(mAccountHeader)
                .withSliderBackgroundColorRes(R.color.colorAppMyNews2)
                .addDrawerItems(
                        new DividerDrawerItem().withEnabled(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_rocket).withIdentifier(1).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_edit).withIdentifier(2).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_offline).withIcon(FontAwesome.Icon.faw_save).withIdentifier(4).withSelectable(false),
                        new SectionDrawerItem().withName("Новости"),
                        expDrawItem
                )
                .withOnDrawerItemClickListener((view, position, drawerItem) -> {
                    if(drawerItem != null) {
                        Intent intent = null;
                        switch ((int)drawerItem.getIdentifier()) {
                            case 1 :
                                break;
                            case 2 :
                                intent = new Intent(MainActivity.this, SettingsActivity.class);
                                break;
                            case 4 :
                                intent = new Intent(MainActivity.this, OfflineActivity.class);
                                break;
                            default:
                                break;
                        }
                        if (intent != null) {
                            MainActivity.this.startActivity(intent);
                        }
                        if((int)drawerItem.getIdentifier() > 2000) {
                            oldUrl = drawerItem.getTag().toString();
                            if(mDetailsFrameLayout.getVisibility() == View.VISIBLE) mDetailsFrameLayout.setVisibility(View.GONE);
                            mRepositoriesPresenter.loadRepositories(true, oldUrl, isConnected());
                            mListView.smoothScrollToPosition(0);
                            if(mMainPresenter.isUrl(oldUrl)) { mMainPresenter.saveSP(oldUrl); }
                            //oldUrl = sp.getString(drawerItem.getTag().toString(), "");
                        }
                    }
                    return false;
                })
                .withOnDrawerItemLongClickListener((view, position, drawerItem) -> {
                    if(drawerItem != null) {
                        if((int)drawerItem.getIdentifier() > 2000) {
                            mErrorDialog = new AlertDialog.Builder(mContext)
                                    .setTitle(drawerItem.getTag().toString())
                                    .setMessage("Удалить?")
                                    .setPositiveButton("Да", (dialog, which) -> {
//                                            SharedPreferences.Editor e = sp.edit();
//                                            e.remove(drawerItem.getTag().toString());
//                                            e.apply();
                                        mDrawerPresenter.deleteSubItem(drawerItem.getTag().toString());
                                        expDrawItem.getSubItems().remove(drawerItem);
                                        expDrawItem.withBadge(String.valueOf(expDrawItem.getSubItems().size()));
                                        mDrawer.updateItem(expDrawItem);
                                        mDrawer.removeItemByPosition(mDrawer.getDrawerItems().size());
                                        dialog.dismiss();
                                    })
                                    .setNegativeButton("Нет", (dialog, which) -> {
                                        dialog.dismiss();
                                    })
                                    .show();
                        }
                    }
                    return false;
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                //.withShowDrawerUntilDraggedOpened(true)
                .build();

        mDrawerPresenter.setSubItems();
    }

    @Override
    public void addSubItem(Map<String, String> urlsAndIcons) {
        String tmp;
        final Drawable[] d = new Drawable[1];
        for (Map.Entry entry : urlsAndIcons.entrySet()) {
            tmp = entry.getKey().toString()
                .replaceFirst("[^/]+//(www\\.)*","")
                    .replaceFirst("/.+","");
            if(!expDrawItem.getSubItems().isEmpty()) {
                if(expDrawItem.getSubItems().get(0).getIdentifier() == 2000) {expDrawItem.getSubItems().remove(0);}
            }

            GlideApp.with(this).asDrawable().load(entry.getValue().toString()).into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    d[0] = resource;
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });

            expDrawItem.getSubItems().add(new SecondaryDrawerItem()
                    .withName(tmp)
                    .withTag(entry.getKey().toString())
                    .withLevel(2)
                    .withIcon(d[0])
                    .withIdentifier(2000 + identif).withSelectable(false));
            identif++;
        }
        expDrawItem.withBadge(String.valueOf(expDrawItem.getSubItems().size()));
        mDrawer.updateItem(expDrawItem);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setSubItems (@NonNull Map<String, String> urlsAndIcons) {
        String tempS;
        Drawable d;
        GlideImageGetter gil = new GlideImageGetter(this);

        for (Map.Entry entry : urlsAndIcons.entrySet()) {
            tempS = entry.getKey().toString()
                    .replaceFirst("[^/]+//(www\\.)*","")
                    .replaceFirst("/.+","");
            if(entry.getValue() != null) {
                d = gil.getDrawable("https://cs.pikabu.ru/assets/images/apple-touch-icon-152x152.png");
            } else {
                d = getDrawable(R.drawable.youtube_icon);
            }


            if(!expDrawItem.getSubItems().isEmpty()) {
                if(expDrawItem.getSubItems().get(0).getIdentifier() == 2000) {expDrawItem.getSubItems().remove(0);}
            }

            expDrawItem.getSubItems().add(new SecondaryDrawerItem()
                    .withName(tempS)
                    .withTag(entry.getKey().toString())
                    .withLevel(2)
                    .withIcon(d)
                    .withIdentifier(2000 + identif).withSelectable(false));
            identif++;
        }
        expDrawItem.withBadge(String.valueOf(expDrawItem.getSubItems().size()));
        mDrawer.updateItem(expDrawItem);
    }

    @Override
    public void addNewNewsChannel () {
        String temp = mMainPresenter.getSP().getString(MY_URL,"")
                .replaceFirst("[^/]+//(www\\.)*","")
                .replaceFirst("/.+","");
        if(mMainPresenter.getSP().getString(temp,"").equals("")) {
            // выводим нужную активность
//            SharedPreferences.Editor e = sp.edit();
//            e.putString(temp, sp.getString(MY_URL,""));
//            e.apply();
            mMainPresenter.getEditor().putString(temp, mMainPresenter.getSP().getString(MY_URL,"")).apply();
            if(expDrawItem.getSubItems().get(0).getIdentifier() == 2000) {expDrawItem.getSubItems().remove(0);}

            expDrawItem.getSubItems().add(new SecondaryDrawerItem()
                    .withName(temp)
                    .withTag(mMainPresenter.getSP().getString(MY_URL,""))
                    .withLevel(2)
                    .withIcon(FontAwesome.Icon.faw_newspaper)
                    .withIdentifier(3000 + identif)
                    .withSelectable(false));

            expDrawItem.withBadge(String.valueOf(expDrawItem.getSubItems().size()));
            mDrawer.updateItem(expDrawItem);
            identif++;
            oldUrl = mMainPresenter.getSP().getString(temp, "");
        }

    }
}
