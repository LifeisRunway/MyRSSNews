package com.imra.mynews.ui.activities;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.palette.graphics.Palette;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.florent37.glidepalette.GlidePalette;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.util.ArrayUtils;
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
import com.imra.mynews.ui.utils.CustomUrlPrimaryDrawerItem;
import com.imra.mynews.ui.views.FrameSwipeRefreshLayout;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
    @BindView(R.id.v_toolbar_extension)
    FrameLayout mToolbarFrame;
    @BindView (R.id.tvChanTitle)
    TextView mChannelTitle;
    @BindView (R.id.tvChanDesc)
    TextView mChannelDescription;

    @BindView(R.id.activity_home_frame_layout_details)
    FrameLayout mDetailsFrameLayout;

    private AlertDialog mErrorDialog;
    private Unbinder unbinder;
    private int visible = View.VISIBLE;
    private boolean isNew;

    private RepositoriesAdapter mReposAdapter;

    private Drawer mDrawer = null;
    private AccountHeader mAccountHeader = null;
    private static final int PROFILE_SETTING = 100000;
    int num = 1;
    View view ;

    //SharedPreferences sp;

    private String oldUrl;

    private static final String MY_URL = "url";
    private static final String MY_SETTINGS = "settings";

    private int mCheck = -1;

    Disposable disposable;
    Context mContext;
    int identif = 1;

    int sizeStart = 0;
    int sizeEnd = 0;

    ExpandableBadgeDrawerItem expDrawItem;
    Bundle mBundle;
    FirebaseUser user;
    String uName;
    String uEmail;
    Uri uIcon;
    Disposable dis;
    private GoogleSignInOptions gso;
    private GoogleSignInClient signInClient;

    @ProvidePresenter
    DrawerPresenter provideDrawerPresenter () { return new DrawerPresenter(mBundle); }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBundle = savedInstanceState;
        mContext = this;
        view = new View(this);
        view.setBackgroundColor(getResources().getColor(R.color.colorAppMyNews));
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, gso);

        unbinder = ButterKnife.bind(this);
        drawerImageLoader();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mDetailsFrameLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }

        //Перенести это дерьмо в какой-нибудь Presenter
        //user = FirebaseAuth.getInstance().getCurrentUser();
        if (mDrawerPresenter.getUser() != null) {
            uName = mDrawerPresenter.getUser().getDisplayName();
            uEmail = mDrawerPresenter.getUser().getEmail();
            uIcon = mDrawerPresenter.getUser().getPhotoUrl() == null ?
                    Uri.parse("android.resource://com.imra.mynews/" + R.drawable.ic_user_svg) :
                    mDrawerPresenter.getUser().getPhotoUrl();
        } else {
            uName = " ";
            uEmail = " ";
            uIcon = Uri.parse("android.resource://com.imra.mynews/" + R.drawable.ic_user_svg);
        }
        mToolbar.setPopupTheme(R.style.AppTheme);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setElevation(1);
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
            isNew = true;
        }
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
        if(isNew) {
            String iconUrl = mMainPresenter.getSP().getString(oldUrl, "");
            mDrawerPresenter.addSubItem(oldUrl, iconUrl);
            isNew = false;
        }
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

    private void drawerImageLoader () {

        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                GlideApp.with(imageView.getContext()).load(uri).placeholder(placeholder).error(R.drawable.ic_my_news_playstore).into(imageView);
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

    }

    // Боковая панель
    @SuppressLint("ResourceAsColor")
    @Override
    public void setDrawer (Bundle savedInstanceState) {

        // Create a sample profile
        final IProfile profile = new ProfileDrawerItem().withName(uName).withEmail(uEmail).withIcon(uIcon).withIdentifier(100000);

        mAccountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withTranslucentStatusBar(true) //полупрозрачная строка состояния?
                .withHeaderBackground(R.drawable.header) //задник (фон)
                .addProfiles(
                        profile,
                        //don't ask but google uses 14dp for the add account icon in gmail but 20dp for the normal icons (like manage account)
                        new ProfileSettingDrawerItem().withName("Exit Account").withIcon(GoogleMaterial.Icon.gmd_exit_to_app).withIdentifier(100002)
                ).withOnAccountHeaderListener((view, profile1, current) -> {
                    if (profile1 instanceof IDrawerItem && profile1.getIdentifier() == 100002) {
                        mErrorDialog = new AlertDialog.Builder(mContext)
                                .setTitle("Выйти из аккаунта")
                                .setMessage("Вы уверены?")
                                .setPositiveButton("Да", (dialog, which) -> {
                                    FirebaseAuth.getInstance().signOut();
                                    signInClient.signOut();
                                    startActivity(new Intent(this, LoginActivity.class));
                                    finishAffinity();
                                })
                                .setNegativeButton("Нет", (dialog, which) -> {
                                    dialog.dismiss();
                                })
                                .show();
                    }
                    //false if you have not consumed the event and it should close the drawer
                    return false;
                })
                .withSavedInstance(savedInstanceState)
                .build();

        if(!mDrawerPresenter.getUrlRssFeeds().isEmpty()) {
            expDrawItem = new ExpandableBadgeDrawerItem().withName("Новостные ленты").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_book).sizeDp(48)).withTag("-").withIdentifier(50003).withSelectable(false).withBadgeStyle(new BadgeStyle().withTextColorRes(R.color.colorText).withColorRes(R.color.colorFon)).withBadge("!").withSubItems().withTag("новости").withIsExpanded(false);
        } else {
            expDrawItem =  new ExpandableBadgeDrawerItem().withName("Новостные ленты").withIcon(GoogleMaterial.Icon.gmd_book).withIdentifier(50003).withSelectable(false).withBadgeStyle(new BadgeStyle().withTextColorRes(R.color.colorText).withColorRes(R.color.colorFon)).withTag("новости").withBadge("!").withSubItems(
                    new SecondaryDrawerItem()
                            .withName("Нет новостных лент")
                            .withLevel(2)
                            .withTag("-")
                            .withIdentifier(20000).withSetSelected(false).withEnabled(false)).withIsExpanded(false);
        }




        //create the drawer
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withActionBarDrawerToggle(true)
                .withAccountHeader(mAccountHeader)
                .withSliderBackgroundColorRes(R.color.col)
                .addDrawerItems(
                        new DividerDrawerItem().withEnabled(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(50000).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(GoogleMaterial.Icon.gmd_find_in_page).withIdentifier(50001).withSelectable(false),
                        new DividerDrawerItem(),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_offline).withIcon(GoogleMaterial.Icon.gmd_save).withIdentifier(50002).withSelectable(false),
                        new SectionDrawerItem().withName("Новости"),
                        expDrawItem,
                        new SectionDrawerItem().withIdentifier(60000),
                        new PrimaryDrawerItem().withName("Контакты").withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(50004).withSelectable(false)
                        )
                .withOnDrawerItemClickListener((View view, int position, IDrawerItem drawerItem) -> {

                    if(drawerItem != null) {
                        Intent intent = null;
                        switch ((int)drawerItem.getIdentifier()) {
                            case 50001 :
                                intent = new Intent(MainActivity.this, SettingsActivity.class);
                                break;
                            case 50002 :
                                intent = new Intent(MainActivity.this, OfflineActivity.class);
                                break;
                            case 50004:
                                return true;
                            default:
                                break;
                        }
                        if (intent != null) {
                            MainActivity.this.startActivity(intent);
                        }
                        if((int)drawerItem.getIdentifier() < 20000) {
                            mListView.smoothScrollToPosition(0);
                            oldUrl = drawerItem.getTag().toString();
                            changeBackCol(mMainPresenter.getSP().getString(oldUrl, ""));
                            if(mDetailsFrameLayout.getVisibility() == View.VISIBLE) mDetailsFrameLayout.setVisibility(View.GONE);
                            mRepositoriesPresenter.loadRepositories(true, oldUrl, isConnected());
                            if(mMainPresenter.isUrl(oldUrl)) { mMainPresenter.saveSP(oldUrl); }
                            //oldUrl = sp.getString(drawerItem.getTag().toString(), "");
                        }
                    }
                    return false;
                })
                .withOnDrawerItemLongClickListener((view, position, drawerItem) -> {
                    if(drawerItem != null) {
                        if((int)drawerItem.getIdentifier() < 20000) {

                            mErrorDialog = new AlertDialog.Builder(mContext)
                                    .setTitle(drawerItem.getTag().toString() + " " + drawerItem.getIdentifier())
                                    .setMessage("Удалить?")
                                    .setPositiveButton("Да", (dialog, which) -> {
                                        int [] testing = mDrawer.getExpandableExtension().getExpandedItems();
                                        for (int i = testing.length - 1; i >= 0; i--) {
                                            mDrawer.getExpandableExtension().collapse(testing[i]);
                                        }
                                        mDrawerPresenter.deleteSubItem(drawerItem.getTag().toString());
                                        expDrawItem = (ExpandableBadgeDrawerItem) mDrawer.getDrawerItem(50003);
                                        mDrawer.removeItem(drawerItem.getIdentifier());
                                        expDrawItem.getSubItems().remove(drawerItem);
                                        expDrawItem.withBadge(String.valueOf(expDrawItem.getSubItems().size()));
                                        for(int i = 0; i <= testing.length - 1; i++) {
                                            mDrawer.getExpandableExtension().expand(testing[i]);
                                        }
                                        //mDrawer.removeItemByPosition(mDrawer.getDrawerItems().size());
                                        if(oldUrl.equals(drawerItem.getTag().toString())) {
                                            if(!expDrawItem.getSubItems().isEmpty()) {oldUrl = expDrawItem.getSubItems().get(0).getTag().toString();}
                                            else {setZeroItemDrawer ();}
                                        }
                                        dialog.dismiss();
                                        mRepositoriesPresenter.loadRepositories(true, oldUrl, isConnected());
                                    })
                                    .setNegativeButton("Нет", (dialog, which) -> {
                                        dialog.dismiss();
                                    })
                                    .show();
                        }
                        if((int)drawerItem.getIdentifier() > 30000 && (int)drawerItem.getIdentifier() < 50000) {

                            mErrorDialog = new AlertDialog.Builder(mContext)
                                    .setTitle(drawerItem.getTag().toString() + " " + drawerItem.getIdentifier())
                                    .setMessage("Удалить список?")
                                    .setPositiveButton("Да", (dialog, which) -> {
                                        int [] testing = mDrawer.getExpandableExtension().getExpandedItems();
                                        for (int i = testing.length - 1; i >= 0; i--) {
                                            mDrawer.getExpandableExtension().collapse(testing[i]);
                                        }
                                        for(Object item : drawerItem.getSubItems()) {
                                            mDrawerPresenter.deleteSubItem(((IDrawerItem) item).getTag().toString());
                                        }
                                        expDrawItem = (ExpandableBadgeDrawerItem) mDrawer.getDrawerItem(50003);
                                        drawerItem.getSubItems().clear();
                                        mDrawer.removeItem(drawerItem.getIdentifier());
                                        expDrawItem.getSubItems().remove(drawerItem);
                                        expDrawItem.withBadge(String.valueOf(expDrawItem.getSubItems().size()));
                                        mDrawer.updateItem(expDrawItem);
                                        for(int i = 0; i <= testing.length - 1; i++) {
                                            mDrawer.getExpandableExtension().expand(testing[i]);
                                        }

                                        for(Object item : drawerItem.getSubItems()) {
                                            if(oldUrl.equals(((IDrawerItem)item).getTag().toString())) {
                                                if(!expDrawItem.getSubItems().isEmpty()) {oldUrl = expDrawItem.getSubItems().get(0).getTag().toString();}
                                                else {setZeroItemDrawer ();}
                                            }
                                        }
                                        dialog.dismiss();
                                        mRepositoriesPresenter.loadRepositories(true, oldUrl, isConnected());
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
                .withShowDrawerUntilDraggedOpened(true)
                .build();

        mDrawer.getAdapter().withOnPreClickListener((v, adapter, item, position1) -> {
            if (item.getIdentifier() == 50003) {
                if (item.isExpanded()) {
                    int[] temp = mDrawer.getExpandableExtension().getExpandedItems();
                    for (int i = temp.length - 1; i > 0; i--) {
                        mDrawer.getExpandableExtension().collapse(temp[i]);
                    }
                }
            }
            return false;
        });
        if(savedInstanceState == null) {
            mDrawer.setSelection(27, false);
        }
        mDrawerPresenter.setSubItems();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = mDrawer.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = mAccountHeader.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    private void changeBackCol(String iconUrl) {
        if (iconUrl != null) {
            if(!iconUrl.equals("")) {
                GlideApp.with(this).load(iconUrl)
                        .listener(GlidePalette.with(iconUrl)
                                .use(GlidePalette.Profile.VIBRANT_DARK)
                                .intoCallBack(new GlidePalette.CallBack() {
                                    @Override
                                    public void onPaletteLoaded(@Nullable Palette palette) {
                                        int col = ((ColorDrawable) mToolbar.getBackground()).getColor();
                                        int col2 = -1;
                                        assert palette != null;
                                        Palette.Swatch ps = palette.getVibrantSwatch();
                                        if(ps != null) {
                                            int tem = ps.getRgb();
                                            mToolbar.setBackgroundColor(tem);
                                            mToolbarFrame.setBackgroundColor(tem);
                                            int temp = manipulateColor(ps.getRgb());
                                            mListView.setBackgroundColor(temp);
                                            mDetailsFrameLayout.setBackgroundColor(temp);
                                            col2 = ((ColorDrawable) mToolbar.getBackground()).getColor();

                                        }
                                        if(col2 != -1) {
                                            if(col == col2) {
                                                changeBackCol2(iconUrl);
                                            }
                                        }
                                    }
                                })
                                .crossfade(false))
                        .submit();



//                if(color2 == color) {
//                    mToolbar.setBackgroundColor(R.color.colorAppMyNews);
//                    mToolbarFrame.setBackgroundColor(R.color.colorAppMyNews);
//                    mListView.setBackgroundColor(R.color.colorAppMyNews);
//                }
            } else {
                Toast.makeText(this, "IconUrl is empty!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "IconUrl is null!", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeBackCol2(String iconUrl) {
        if (iconUrl != null) {
            if(!iconUrl.equals("")) {

                int colorTemporaly = getResources().getColor(R.color.colorAppMyNews);
                GlideApp.with(this).load(iconUrl)
                        .listener(GlidePalette.with(iconUrl)
                                .use(GlidePalette.Profile.VIBRANT)
                                .intoCallBack(new GlidePalette.CallBack() {
                                    @Override
                                    public void onPaletteLoaded(@Nullable Palette palette) {
                                        int col = ((ColorDrawable) mToolbar.getBackground()).getColor();
                                        int col2 = -1;
                                        assert palette != null;
                                        Palette.Swatch ps = palette.getVibrantSwatch();
                                        if(ps != null) {
                                            int tem = ps.getRgb();
                                            mToolbar.setBackgroundColor(tem);
                                            mToolbarFrame.setBackgroundColor(tem);
                                            int temp = manipulateColor(ps.getRgb());
                                            mListView.setBackgroundColor(temp);
                                            mDetailsFrameLayout.setBackgroundColor(temp);
                                            col2 = ((ColorDrawable) mToolbar.getBackground()).getColor();
                                        }
                                        if(col2 != -1) {
                                            if(col == col2) {
                                                mToolbar.setBackgroundColor(colorTemporaly);
                                                mToolbarFrame.setBackgroundColor(colorTemporaly);
                                                mListView.setBackgroundColor(colorTemporaly);
                                            }
                                        }
                                    }
                                })
                                .crossfade(false))
                        .submit();
            }
        }
    }

    private int manipulateColor(int color) {
        //float factor = 0.5f;
        int r = Math.round(Color.red(color));
        int g = Math.round(Color.green(color));
        int b = Math.round(Color.blue(color));

        return Color.argb(210,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

    private void setZeroItemDrawer () {
        oldUrl = "";
        mMainPresenter.saveSP("");
        expDrawItem.getSubItems().add( new SecondaryDrawerItem()
                .withName("Нет новостных лент")
                .withLevel(2)
                .withTag("-")
                .withIdentifier(20000).withSetSelected(false).withEnabled(false));
        expDrawItem.getBadge().setText("0");
        mDrawer.updateItem(expDrawItem);
    }

    @Override
    public void addSubItem(String url, String iconUrl) {

        boolean isChanged = false;
        int [] testing = mDrawer.getExpandableExtension().getExpandedItems();

        String name = url
                .replaceFirst("[^/]+//(www\\.)*","")
                .replaceFirst("/.+","");

        for (int i = testing.length - 1; i >= 0; i--) {
            mDrawer.getExpandableExtension().collapse(testing[i]);
        }

        expDrawItem = (ExpandableBadgeDrawerItem) mDrawer.getDrawerItem(50003);

        for (int i = 0; i < expDrawItem.getSubItems().size(); i++) {

            String itemTag = expDrawItem.getSubItems().get(i).getTag().toString();

            String itemName = itemTag
                    .replaceFirst("[^/]+//(www\\.)*","")
                    .replaceFirst("/.+","");
            //поиск дублей и создание expandBDI
            if(itemName.equals(name)) {
                ExpandableBadgeDrawerItem expTest = new ExpandableBadgeDrawerItem()
                        .withName(name)
                        .withIcon(GoogleMaterial.Icon.gmd_book)
                        .withTag(" ")
                        .withIdentifier(30000 + identif)
                        .withSelectable(false)
                        .withBadgeStyle(new BadgeStyle().withTextColorRes(R.color.colorText).withColorRes(R.color.colorFon))
                        .withBadge("0")
                        .withSubItems()
                        .withIsExpanded(false);

                CustomUrlPrimaryDrawerItem kvakva = (CustomUrlPrimaryDrawerItem) expDrawItem.getSubItems().get(i);

                if(iconUrl != null && !iconUrl.equals("")) {
                    expTest.getSubItems().add(kvakva);
                    expTest.getSubItems().add(new CustomUrlPrimaryDrawerItem()
                            .withName(name)
                            .withTag(url)
                            .withLevel(3)
                            .withIcon(iconUrl)
                            .withIdentifier(identif).withSelectable(false));
                } else {
                    expTest.getSubItems().add(kvakva);
                    expTest.getSubItems().add(new CustomUrlPrimaryDrawerItem()
                            .withName(name)
                            .withTag(url)
                            .withLevel(3)
                            .withIcon(R.drawable.youtube_icon)
                            .withIdentifier(identif).withSelectable(false));
                }
                expTest.withBadge(String.valueOf(expTest.getSubItems().size()));
                expDrawItem.getSubItems().remove(kvakva);
                expDrawItem.getSubItems().add(expTest);
                isChanged = true;
                identif++;

            } else {
                //поиск созданного expBDI
                if(itemTag.equals(name)) {
                    if(expDrawItem.getSubItems().get(i) instanceof ExpandableBadgeDrawerItem) {
                        //ExpandableBadgeDrawerItem bb = (ExpandableBadgeDrawerItem) expDrawItem.getSubItems().get(i);
                        if(iconUrl != null && !iconUrl.equals("")) {
                            ((ExpandableBadgeDrawerItem) expDrawItem.getSubItems().get(i)).getSubItems().add(new CustomUrlPrimaryDrawerItem()
                                    .withName(name)
                                    .withTag(url)
                                    .withLevel(3)
                                    .withIcon(iconUrl)
                                    .withIdentifier(identif).withSelectable(false));
                        } else {
                            ((ExpandableBadgeDrawerItem) expDrawItem.getSubItems().get(i)).getSubItems().add(new CustomUrlPrimaryDrawerItem()
                                    .withName(name)
                                    .withTag(url)
                                    .withLevel(3)
                                    .withIcon(R.drawable.youtube_icon)
                                    .withIdentifier(identif).withSelectable(false));
                        }
                        isChanged = true;
                        identif++;
                    }
                }
            }
        }

        if(!isChanged) {

            if(!expDrawItem.getSubItems().isEmpty()) {
                if(expDrawItem.getSubItems().get(0).getIdentifier() == 20000) {expDrawItem.getSubItems().remove(0);}
            }

            if(iconUrl != null && !iconUrl.equals("")) {

                expDrawItem.getSubItems().add(new CustomUrlPrimaryDrawerItem()
                        .withName(name)
                        .withTag(url)
                        .withLevel(2)
                        .withIcon(iconUrl)
                        .withIdentifier(identif).withSelectable(false));

            } else {
                expDrawItem.getSubItems().add(new CustomUrlPrimaryDrawerItem()
                        .withName(name)
                        .withTag(url)
                        .withLevel(2)
                        .withIcon(R.drawable.youtube_icon)
                        .withIdentifier(identif).withSelectable(false));
            }
            identif++;
        }

        expDrawItem.withBadge(String.valueOf(expDrawItem.getSubItems().size()));
        mDrawer.updateItem(expDrawItem);

        for(int i = 0; i <= testing.length - 1; i++) {
            mDrawer.getExpandableExtension().expand(testing[i]);
        }

    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setSubItems (List<RSSFeed> mRssFeeds) {
        String name;
        String iconUrl;
        List<String> dub = new ArrayList<>();
        List<RSSFeed> normals = new ArrayList<>();
        List<RSSFeed> dublicates = new ArrayList<>();
        Set<String> set = new HashSet<>();

        expDrawItem = (ExpandableBadgeDrawerItem) mDrawer.getDrawerItem(50003);

        for (RSSFeed r : mRssFeeds) {
            name = r.getUrl()
                    .replaceFirst("[^/]+//(www\\.)*","")
                    .replaceFirst("/.+","");
            if(!set.add(name)) {
                for (RSSFeed rf : normals) {
                    String nameRF = rf.getUrl()
                            .replaceFirst("[^/]+//(www\\.)*","")
                            .replaceFirst("/.+","");
                    if(nameRF.equals(name)) {
                        dublicates.add(rf);
                        normals.remove(rf);
                    }
                }
                dub.add(name);
                dublicates.add(r);
            } else {
                normals.add(r);
            }
        }

        for (RSSFeed normal : normals) {
            name = normal.getUrl()
                    .replaceFirst("[^/]+//(www\\.)*","")
                    .replaceFirst("/.+","");

            if(!expDrawItem.getSubItems().isEmpty()) {
                if(expDrawItem.getSubItems().get(0).getIdentifier() == 20000) {expDrawItem.getSubItems().remove(0);}
            }

            iconUrl = normal.getIconUrl();

            if(iconUrl != null && !iconUrl.equals("")) {

                expDrawItem.getSubItems().add(new CustomUrlPrimaryDrawerItem()
                        .withName(name)
                        .withTag(normal.getUrl())
                        .withLevel(2)
                        .withIcon(iconUrl)
                        .withIdentifier(identif).withSelectable(false));

            } else {
                expDrawItem.getSubItems().add(new CustomUrlPrimaryDrawerItem()
                        .withName(name)
                        .withTag(normal.getUrl())
                        .withLevel(2)
                        .withIcon(R.drawable.youtube_icon)
                        .withIdentifier(identif).withSelectable(false));
            }
            identif++;
        }

        while(!dub.isEmpty()) {
            List<RSSFeed> test;
            name = dub.get(0);
            dub.remove(name);
            test = gangBang(dublicates, name);

            ExpandableBadgeDrawerItem expTest = new ExpandableBadgeDrawerItem()
                    .withName(name)
                    .withIcon(GoogleMaterial.Icon.gmd_book)
                    .withTag(" ")
                    .withIdentifier(30000 + identif)
                    .withSelectable(false)
                    .withLevel(1)
                    .withBadgeStyle(new BadgeStyle().withTextColorRes(R.color.colorText).withColorRes(R.color.colorFon))
                    .withBadge("0")
                    .withSubItems()
                    .withIsExpanded(false);

            for(RSSFeed t : test) {
                iconUrl = t.getIconUrl();
                String title = t.getChannelTitle();

                if(iconUrl != null && !iconUrl.equals("")) {

                    expTest.getSubItems().add(new CustomUrlPrimaryDrawerItem()
                            .withName(title)
                            .withTag(t.getUrl())
                            .withLevel(3)
                            .withIcon(iconUrl)
                            .withIdentifier(identif).withSelectable(false));

                } else {
                    expTest.getSubItems().add(new CustomUrlPrimaryDrawerItem()
                            .withName(title)
                            .withTag(t.getUrl())
                            .withLevel(3)
                            .withIcon(R.drawable.youtube_icon)
                            .withIdentifier(identif).withSelectable(false));
                }
                identif++;
            }
            expTest.withBadge(String.valueOf(expTest.getSubItems().size()));
            expDrawItem.getSubItems().add(expTest);
        }

        if(expDrawItem.getSubItems().get(0).getIdentifier() == 20000) { expDrawItem.getBadge().setText("!"); }
        else {expDrawItem.withBadge(String.valueOf(expDrawItem.getSubItems().size()));}
        mDrawer.updateItem(expDrawItem);
    }

    private List<RSSFeed> gangBang (List<RSSFeed> dublicates, String name) {
        List<RSSFeed> test = new ArrayList<>();
        for(RSSFeed rf : dublicates) {
            String nameRF = rf.getUrl()
                    .replaceFirst("[^/]+//(www\\.)*","")
                    .replaceFirst("/.+","");
            if(nameRF.equals(name)) {
                test.add(rf);
            }
        }
        return test;
    }

    private ExpandableBadgeDrawerItem kadabra (List<RSSFeed> test, String name) {
        ExpandableBadgeDrawerItem expTest = new ExpandableBadgeDrawerItem()
                .withName(name)
                .withIcon(GoogleMaterial.Icon.gmd_book)
                .withSelectable(false)
                .withLevel(3)
                .withBadgeStyle(new BadgeStyle().withTextColorRes(R.color.colorText).withColorRes(R.color.colorFon))
                .withBadge("0")
                .withSubItems()
                .withIsExpanded(false);

        for(RSSFeed t : test) {
            String iconUrl = t.getIconUrl();

            if(iconUrl != null && !iconUrl.equals("")) {

                expTest.getSubItems().add(new CustomUrlPrimaryDrawerItem()
                        .withName(name)
                        .withTag(t.getUrl())
                        .withLevel(3)
                        .withIcon(iconUrl)
                        .withIdentifier(identif).withSelectable(false));

            } else {
                expTest.getSubItems().add(new CustomUrlPrimaryDrawerItem()
                        .withName(name)
                        .withTag(t.getUrl())
                        .withLevel(3)
                        .withIcon(R.drawable.youtube_icon)
                        .withIdentifier(identif).withSelectable(false));
            }
            identif++;
        }
        return expTest;
    }

    @Override
    public void addNewNewsChannel (String name) {
        String one;
        for(int i = 0; i < expDrawItem.getSubItems().size(); i++) {
            one = expDrawItem.getSubItems().get(i).getTag().toString().replaceFirst("[^/]+//(www\\.)*","")
                    .replaceFirst("/.+","");
            if(one.equals(name)) {
                if(expDrawItem.getSubItems().get(i) instanceof ExpandableBadgeDrawerItem) {
                    expDrawItem.getSubItems().get(i).getSubItems().add(new CustomUrlPrimaryDrawerItem()
                            .withName(name + identif)
                            .withTag(name + identif)
                            .withLevel(3)
                            .withIcon(R.drawable.youtube_icon)
                            .withIdentifier(identif).withSelectable(false));
                } else {
                    expDrawItem.getSubItems().remove(expDrawItem.getSubItems().get(i));
                    ExpandableBadgeDrawerItem ex = new ExpandableBadgeDrawerItem()
                            .withName(name)
                            .withTag(name)
                            .withLevel(3)
                            .withIcon(FontAwesome.Icon.faw_newspaper)
                            .withIdentifier(identif).withSelectable(false);
                    ex.getSubItems().add(new CustomUrlPrimaryDrawerItem()
                            .withName(name + identif)
                            .withTag(name + identif)
                            .withLevel(3)
                            .withIcon(R.drawable.youtube_icon)
                            .withIdentifier(identif).withSelectable(false));
                    expDrawItem.getSubItems().add(i, ex);
                }
                identif++;
                expDrawItem.withBadge(String.valueOf(expDrawItem.getSubItems().size()));
                mDrawer.updateItem(expDrawItem);
            }
        }



        String temp = mMainPresenter.getSP().getString(MY_URL,"")
                .replaceFirst("[^/]+//(www\\.)*","")
                .replaceFirst("/.+","");
        if(mMainPresenter.getSP().getString(temp,"").equals("")) {

            mMainPresenter.getEditor().putString(temp, mMainPresenter.getSP().getString(MY_URL,"")).apply();
            if(expDrawItem.getSubItems().get(0).getIdentifier() == 20000) {expDrawItem.getSubItems().remove(0);}

            expDrawItem.getSubItems().add(new SecondaryDrawerItem()
                    .withName(temp)
                    .withTag(mMainPresenter.getSP().getString(MY_URL,""))
                    .withLevel(2)
                    .withIcon(FontAwesome.Icon.faw_newspaper)
                    .withIdentifier(identif)
                    .withSelectable(false));

            expDrawItem.withBadge(String.valueOf(expDrawItem.getSubItems().size()));
            mDrawer.updateItem(expDrawItem);
            identif++;
            oldUrl = mMainPresenter.getSP().getString(temp, "");
        }

    }
}
