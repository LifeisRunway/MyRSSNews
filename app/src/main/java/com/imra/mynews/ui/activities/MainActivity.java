package com.imra.mynews.ui.activities;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.florent37.glidepalette.GlidePalette;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.imra.mynews.R;
import com.imra.mynews.di.modules.GlideApp;
import com.imra.mynews.di.modules.GlideRequests;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.presenters.DrawerPresenter;
import com.imra.mynews.mvp.presenters.MainPresenter;
import com.imra.mynews.mvp.presenters.RepositoriesPresenter;
import com.imra.mynews.mvp.views.DrawerView;
import com.imra.mynews.mvp.views.MainInterface;
import com.imra.mynews.mvp.views.RepositoriesView;
import com.imra.mynews.ui.adapters.ReposRecyclerAdapter;
import com.imra.mynews.ui.fragments.Fragment;
import com.imra.mynews.ui.utils.CustomDividerDrawerItem;
import com.imra.mynews.ui.utils.CustomUrlPrimaryDrawerItem;
import com.imra.mynews.ui.utils.ItemClickSupport;
import com.imra.mynews.ui.views.FrameSwipeRefreshLayout;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.itemanimators.ScaleYAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
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
import java.util.HashMap;
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


public class MainActivity extends MvpAppCompatActivity implements MainInterface, RepositoriesView, DrawerView, ReposRecyclerAdapter.OnScrollToBottomListener{

    @InjectPresenter
    MainPresenter mMainPresenter;

    @InjectPresenter
    RepositoriesPresenter mRepositoriesPresenter;

    @InjectPresenter
    DrawerPresenter mDrawerPresenter;

    @BindView(R.id.activity_home_swipe_refresh_layout)
    FrameSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.activity_home_progress_bar_repositories)
    ProgressBar mRepositoriesProgressBar;
    @BindView(R.id.activity_home_list_view_repositories)
    RecyclerView mRecyclerView;
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
    private boolean isNew;

    private ReposRecyclerAdapter mReposRecyclerAdapter;

    private Drawer mDrawer = null;
    private AccountHeader mAccountHeader = null;
    View view ;

    private String oldUrl;

    Disposable disposable;
    Context mContext;
    int identif = 1;

    ExpandableBadgeDrawerItem expDrawItem;
    Bundle mBundle;
    FirebaseUser user;
    String uName;
    String uEmail;
    Uri uIcon;
    private GoogleSignInOptions gso;
    private GoogleSignInClient signInClient;
    private static final int REQUEST_CODE_SEARCH_ACTIVITY = 1;
    private FirebaseFirestore db;
    private FirebaseAnalytics mFirebaseAnalytics;
    DocumentReference docRefUserChannels;

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
        db = FirebaseFirestore.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        unbinder = ButterKnife.bind(this);
        drawerImageLoader();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mDetailsFrameLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        }
        createExpDrItem();
        mRepositoriesPresenter.forDrawer();
        //Перенести это дерьмо в какой-нибудь Presenter
        //user = FirebaseAuth.getInstance().getCurrentUser();
        if (mDrawerPresenter.getUser() != null) {
            uName = mDrawerPresenter.getUser().getDisplayName();
            uEmail = mDrawerPresenter.getUser().getEmail();
            uIcon = mDrawerPresenter.getUser().getPhotoUrl() == null ?
                    Uri.parse("android.resource://com.imra.mynews/" + R.drawable.ic_user_svg) :
                    mDrawerPresenter.getUser().getPhotoUrl();
        } else {
            uName = "Unknown user";
            uEmail = "Unknown email";
            uIcon = Uri.parse("android.resource://com.imra.mynews/" + R.drawable.ic_user_svg);
        }
        docRefUserChannels = db.collection("userChannels").document(uEmail);
        mToolbar.setPopupTheme(R.style.AppTheme);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setElevation(1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDetailsFrameLayout.setVisibility(View.GONE);

        oldUrl = mMainPresenter.getUrlSP();

        GlideRequests glideRequests = GlideApp.with(this);
        mReposRecyclerAdapter = new ReposRecyclerAdapter(mContext, getMvpDelegate(), this, glideRequests);
        mReposRecyclerAdapter.setHasStableIds(true);
        RecyclerViewPreloader<Article> preload = new RecyclerViewPreloader<Article>(glideRequests, mReposRecyclerAdapter,mReposRecyclerAdapter,6);
        if(this.getResources().getConfiguration().orientation == 1) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
        mRecyclerView.addOnScrollListener(preload);
        mRecyclerView.setAdapter(mReposRecyclerAdapter);
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener((recyclerView, position, v) -> {
            mMainPresenter.onRepositorySelection(position, mReposRecyclerAdapter.getItem(position));
        });

        mSwipeRefreshLayout.setListViewChild(mRecyclerView);
        mSwipeRefreshLayout.setOnRefreshListener(() -> mRepositoriesPresenter.loadRepositories(true, oldUrl, "", isConnected()));

    }

    private void sendAnalyticsReadArticle() {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "article");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        if(disposable != null) disposable.dispose();
        mMainPresenter.saveSP(oldUrl);
    }


    @Override
    public void showDetailsContainer(int position) {
        mDetailsFrameLayout.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn)
                .duration(800)
                .playOn(mDetailsFrameLayout);
    }

    @Override
    public void setSelection(int position) {
        mReposRecyclerAdapter.setSelection(position);
    }

    @Override
    public void showDetails(int position, Article article) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_home_frame_layout_details, Fragment.getInstance(position, article))
                .commit();
        if(isConnected()) {
            sendAnalyticsReadArticle();}
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
        mRecyclerView.setVisibility(View.GONE);
        mNoRepositoriesTextView.setVisibility(View.GONE);
        mRepositoriesProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideListProgress() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mRepositoriesProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void setRepositories(RSSFeed repositories) {
        if(repositories.getArticleList().isEmpty()) {
            mNoRepositoriesTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mNoRepositoriesTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        Objects.requireNonNull(mRecyclerView.getLayoutManager()).scrollToPosition(0);
        mReposRecyclerAdapter.setRepositories(repositories);
    }

    @Override
    public void setChannelTitle(RSSFeed rssFeed) {
        mChannelTitle.setText(rssFeed.getChannelTitle());
        if(rssFeed.getChannelDescription() != null) {
            mChannelDescription.setText(rssFeed.getChannelDescription());
            if(mChannelDescription.getVisibility() == View.GONE) { mChannelDescription.setVisibility(View.VISIBLE); }
        } else mChannelDescription.setVisibility(View.GONE);
        if(isNew) {
            mDrawerPresenter.addSubItem(rssFeed.getUrl(), rssFeed.getIconUrl());
            addInFirestone(rssFeed.getUrl(), rssFeed.getIconUrl());
            changeBackCol(rssFeed.getIconUrl());
            oldUrl = rssFeed.getUrl();
            mMainPresenter.saveSP(oldUrl);
            isNew = false;
        }
    }

    private void addInFirestone (String key, String value) {
        Map<String, Object> docData = new HashMap<>();
        docData.put(key, value);
        docRefUserChannels
                .set(docData, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("Сэйв_прошел", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Ошибка_сэйва", "Error writing document", e);
                    }
                });
    }

    private void delInFirestone (String key) {
        docRefUserChannels.update(FieldPath.of(key), FieldValue.delete()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.e("Сэйв_удален", "DocumentSnapshot successfully deleted!");
            }
        });
    }

    @Override
    public void addRepositories(RSSFeed repositories) {
        if(repositories.getArticleList().isEmpty()) {
            mNoRepositoriesTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mNoRepositoriesTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
        mReposRecyclerAdapter.addRepositories(repositories);
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
    public void hideError () {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.hide();
        }
    }

    @Override
    public void onScrollToBottom() {
        //mRepositoriesPresenter.loadNextRepositories(oldUrl, isConnected());
    }

    private void drawerImageLoader () {

        //initialize and create the image loader logic
        DrawerImageLoader.init(new AbstractDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder, String tag) {
                GlideApp.with(imageView.getContext()).load(uri).placeholder(placeholder).error(R.drawable.ic_my_news_playstore).diskCacheStrategy(DiskCacheStrategy.AUTOMATIC).into(imageView);
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
                    return new IconicsDrawable(ctx).iconText(" ").backgroundColorRes(R.color.md_white_1000).sizeDp(56);
                }
                //we use the default one for
                //DrawerImageLoader.Tags.PROFILE_DRAWER_ITEM.name()
                return super.placeholder(ctx, tag);
            }
        });

    }

    private void createExpDrItem () {
        if(!mDrawerPresenter.getUrlRssFeeds().isEmpty()) {
            expDrawItem = new ExpandableBadgeDrawerItem().withName("Новостные ленты").withIcon(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_list).sizeDp(48).color(getResources().getColor(R.color.colorAccent))).withTag("-").withIdentifier(50003).withSelectable(false).withBadgeStyle(new BadgeStyle().withTextColorRes(R.color.colorText).withColorRes(R.color.colorFon)).withBadge("!").withSubItems().withTag("новости").withIsExpanded(false);
        } else {
            expDrawItem =  new ExpandableBadgeDrawerItem().withName("Новостные ленты").withIcon(GoogleMaterial.Icon.gmd_book).withIdentifier(50003).withSelectable(false).withBadgeStyle(new BadgeStyle().withTextColorRes(R.color.colorText).withColorRes(R.color.colorFon)).withTag("новости").withBadge("!").withSubItems(
                    new SecondaryDrawerItem()
                            .withName("Нет новостных лент")
                            .withLevel(2)
                            .withTag("-")
                            .withIdentifier(20000).withSetSelected(false).withEnabled(false)).withIsExpanded(false);
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void setDrawer (Bundle savedInstanceState) {

        // Create a sample profile
        final IProfile profile = new ProfileDrawerItem().withName(uName).withEmail(uEmail).withIcon(uIcon).withIdentifier(100000);

        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mAccountHeader = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withCompactStyle(true)
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
        } else {
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
        }

        //create the drawer
        mDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withHasStableIds(true)
                .withItemAnimator(new ScaleYAnimator())
                .withActionBarDrawerToggle(true)
                .withAccountHeader(mAccountHeader)
                .withSliderBackgroundColorRes(R.color.md_white_1000)
                .addDrawerItems(
                        new CustomDividerDrawerItem().withEnabled(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(GoogleMaterial.Icon.gmd_settings).withIdentifier(50000).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(GoogleMaterial.Icon.gmd_find_in_page).withIdentifier(50001).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_offline).withIcon(GoogleMaterial.Icon.gmd_save).withIdentifier(50002).withSelectable(false),
                        new PrimaryDrawerItem().withName(R.string.Contacts).withIcon(GoogleMaterial.Icon.gmd_info).withIdentifier(50004).withSelectable(false),
                        new SectionDrawerItem().withName("Новости"),
                        expDrawItem
                        //new SectionDrawerItem().withIdentifier(60000)
                        )
                .withOnDrawerItemClickListener((View view, int position, IDrawerItem drawerItem) -> {

                    if(drawerItem != null) {
                        Intent intent = null;
                        switch ((int)drawerItem.getIdentifier()) {
                            case 50001 :
                                intent = new Intent(MainActivity.this, FindRSSActivity.class);
                                break;
                            case 50002 :
                                intent = new Intent(MainActivity.this, SavedNewsActivity.class);
                                break;
                            case 50004:
                                intent = new Intent(MainActivity.this, ContactsActivity.class);
                                break;
                            default:
                                break;
                        }
                        if (intent != null) {
                            if((int)drawerItem.getIdentifier() == 50001) {
                                startActivityForResult(intent,REQUEST_CODE_SEARCH_ACTIVITY);
                            } else {
                                MainActivity.this.startActivity(intent);
                            }
                        }
                        if((int)drawerItem.getIdentifier() < 20000) {
                            //mListView.setSelectionAfterHeaderView();
                            oldUrl = drawerItem.getTag().toString();
                            changeBackCol(mDrawerPresenter.getIconUrl(oldUrl));
                            if(mDetailsFrameLayout.getVisibility() == View.VISIBLE) mDetailsFrameLayout.setVisibility(View.GONE);
                            mRepositoriesPresenter.loadRepositories(true, oldUrl, "", isConnected());
                            //oldUrl = sp.getString(drawerItem.getTag().toString(), "");
                            return false;
                        }
                    }
                    return true;
                })
                .withOnDrawerItemLongClickListener((view, position, drawerItem) -> {
                    if(drawerItem != null) {
                        //удаляем 1 элемент
                        if((int)drawerItem.getIdentifier() < 20000) {

                            mErrorDialog = new AlertDialog.Builder(mContext)
                                    .setTitle(drawerItem.getTag().toString()  + " " + drawerItem.getIdentifier())
                                    .setMessage("Удалить?")
                                    .setPositiveButton("Да", (dialog, which) -> {
                                        int [] testing = mDrawer.getExpandableExtension().getExpandedItems();
                                        if(oldUrl.equals(drawerItem.getTag().toString())) {
                                            if(!expDrawItem.getSubItems().isEmpty()) {
                                                if(expDrawItem.getSubItems().get(0) instanceof ExpandableBadgeDrawerItem) {
                                                    oldUrl = ((ExpandableBadgeDrawerItem)expDrawItem.getSubItems().get(0)).getSubItems().get(0).getTag().toString();
                                                } else {
                                                    oldUrl = expDrawItem.getSubItems().get(0).getTag().toString();
                                                }
                                                mMainPresenter.saveSP(oldUrl);
                                            } else {setZeroItemDrawer ();}
                                        }
                                        mDrawer.getExpandableExtension().collapse();
                                        mDrawerPresenter.deleteSubItem(drawerItem.getTag().toString());
                                        delInFirestone(drawerItem.getTag().toString());
                                        expDrawItem = (ExpandableBadgeDrawerItem) mDrawer.getDrawerItem(50003);
                                        //mDrawer.removeItem(drawerItem.getIdentifier());
                                        for(Object o : expDrawItem.getSubItems()) {
                                            if(o instanceof ExpandableBadgeDrawerItem) {
                                                ((ExpandableBadgeDrawerItem) o).getSubItems().remove(drawerItem);
                                                if(((ExpandableBadgeDrawerItem) o).getSubItems().isEmpty()) {
                                                    expDrawItem.getSubItems().remove(o);
                                                } else {
                                                    ((ExpandableBadgeDrawerItem) o).withBadge(String.valueOf(((ExpandableBadgeDrawerItem) o).getSubItems().size()));
                                                }
                                            }
                                        }
                                        expDrawItem.getSubItems().remove(drawerItem);
                                        expDrawItem.withBadge(String.valueOf(expDrawItem.getSubItems().size()));
                                        mDrawer.updateItem(expDrawItem);
                                        for(int i = 0; i <= testing.length - 1; i++) {
                                            mDrawer.getExpandableExtension().expand(testing[i]);
                                        }
                                        if(expDrawItem.getSubItems().isEmpty()) {
                                            setZeroItemDrawer ();
                                        }
                                        dialog.dismiss();
                                        mRepositoriesPresenter.loadRepositories(true, oldUrl, "", isConnected());
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
                                        mDrawer.getExpandableExtension().collapse();
                                        List <String> list = new ArrayList<>();
                                        for(Object item : drawerItem.getSubItems()) {
                                            list.add(((IDrawerItem) item).getTag().toString());
                                        }
                                        mDrawerPresenter.deleteManySubItems(list);
                                        for(String key : list) {
                                            delInFirestone(key);
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

                                        for(String tag : list) {
                                            if(oldUrl.equals(tag)) {
                                                if(!expDrawItem.getSubItems().isEmpty()) {
                                                    if(expDrawItem.getSubItems().get(0) instanceof ExpandableBadgeDrawerItem) {
                                                        oldUrl = ((ExpandableBadgeDrawerItem)expDrawItem.getSubItems().get(0)).getSubItems().get(0).getTag().toString();
                                                    } else {
                                                        oldUrl = expDrawItem.getSubItems().get(0).getTag().toString();
                                                    }
                                                    mMainPresenter.saveSP(oldUrl);
                                                }
                                                else {setZeroItemDrawer ();}
                                            }
                                        }

                                        if(expDrawItem.getSubItems().isEmpty()) {
                                            setZeroItemDrawer ();
                                        }

                                        dialog.dismiss();
                                        mRepositoriesPresenter.loadRepositories(true, oldUrl, "", isConnected());
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
        getInFirestone();
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
                                            mRecyclerView.setBackgroundColor(temp);
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
                                            mRecyclerView.setBackgroundColor(temp);
                                            mDetailsFrameLayout.setBackgroundColor(temp);
                                            col2 = ((ColorDrawable) mToolbar.getBackground()).getColor();
                                        }
                                        if(col2 != -1) {
                                            if(col == col2) {
                                                mToolbar.setBackgroundColor(colorTemporaly);
                                                mToolbarFrame.setBackgroundColor(colorTemporaly);
                                                mRecyclerView.setBackgroundColor(colorTemporaly);
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
        expDrawItem.getSubItems().add( new SecondaryDrawerItem()
                .withName("Нет новостных лент")
                .withLevel(2)
                .withTag("-")
                .withIdentifier(20000).withSetSelected(false).withEnabled(false));
        expDrawItem.getBadge().setText("!");
        mDrawer.updateItem(expDrawItem);
    }

    @Override
    public void addSubItem (RSSFeed rssFeed) {
        String url = rssFeed.getUrl();
        String iconUrl = rssFeed.getIconUrl();
        String tag = rssFeed.getTag();
        String title = rssFeed.getChannelTitle();

        boolean isChanged = false;
        int [] testing = mDrawer.getExpandableExtension().getExpandedItems();

        mDrawer.getExpandableExtension().collapse();

        expDrawItem = (ExpandableBadgeDrawerItem) mDrawer.getDrawerItem(50003);

        String itemTag;
        String itemName;
        for (int i = 0; i < expDrawItem.getSubItems().size(); i++) {
            itemTag = expDrawItem.getSubItems().get(i).getTag().toString();
            itemName = itemTag
                    .replaceFirst("[^/]+//(www\\.)*", "")
                    .replaceFirst("/.+", "");
            //поиск созданных ExpandItem
            if(itemName.equals(tag)) {
                if (expDrawItem.getSubItems().get(i) instanceof ExpandableBadgeDrawerItem) {
                    ExpandableBadgeDrawerItem exp = (ExpandableBadgeDrawerItem) expDrawItem.getSubItems().get(i);
                    boolean needAdd = true;
                    if (!exp.getSubItems().isEmpty()) {
                        for (Object item : exp.getSubItems()) {
                            if (((IDrawerItem) item).getTag().toString().equals(url)) {
                                needAdd = false;
                            }
                        }
                    }
                    if (needAdd) {
                        ((ExpandableBadgeDrawerItem) expDrawItem.getSubItems().get(i)).getSubItems().add(addCUPDrawerItem(title, url, iconUrl));
                        ((ExpandableBadgeDrawerItem) expDrawItem.getSubItems().get(i)).withBadge(String.valueOf(((ExpandableBadgeDrawerItem) expDrawItem.getSubItems().get(i)).getSubItems().size()));
                    }
                    isChanged = true;
                }

                if (expDrawItem.getSubItems().get(i) instanceof CustomUrlPrimaryDrawerItem) {

                    if(!itemTag.equals(url)) {
                        ExpandableBadgeDrawerItem expTest = new ExpandableBadgeDrawerItem()
                                .withName(tag)
                                .withIcon(GoogleMaterial.Icon.gmd_note)
                                .withTag(tag)
                                .withIdentifier(30000 + identif)
                                .withSelectable(false)
                                .withBadgeStyle(new BadgeStyle().withTextColorRes(R.color.colorText).withColorRes(R.color.colorFon))
                                .withBadge("0")
                                .withSubItems()
                                .withIsExpanded(false);

                        //CustomUrlPrimaryDrawerItem removedItem = (CustomUrlPrimaryDrawerItem) expDrawItem.getSubItems().get(i);
                        List<RSSFeed> twoTags = mDrawerPresenter.getRssForTag(tag);
                        //expTest.getSubItems().add(removedItem.withName(title));
                        for(RSSFeed r : twoTags) {
                            expTest.getSubItems().add(addCUPDrawerItem(r.getChannelTitle(), r.getUrl(), r.getIconUrl()));
                        }
                        expTest.withBadge(String.valueOf(expTest.getSubItems().size()));
                        expDrawItem.getSubItems().remove(expDrawItem.getSubItems().get(i));
                        expDrawItem.getSubItems().add(expTest);
                    }
                    isChanged = true;
                }
            }
        }

        if(!isChanged) {
            if(!expDrawItem.getSubItems().isEmpty()) {
                if(expDrawItem.getSubItems().get(0).getIdentifier() == 20000) {expDrawItem.getSubItems().remove(0);}
            }
            expDrawItem.getSubItems().add(addCUPDrawerItem(title,url,iconUrl));
        }

        expDrawItem.withBadge(String.valueOf(expDrawItem.getSubItems().size()));
        mDrawer.updateItem(expDrawItem);
        for(int i = 0; i <= testing.length - 1; i++) {
            mDrawer.getExpandableExtension().expand(testing[i]);
        }
    }

    private CustomUrlPrimaryDrawerItem addCUPDrawerItem (String name, String tag, String iconUrl) {
        CustomUrlPrimaryDrawerItem c;
        if(iconUrl != null && !iconUrl.equals("")) {
            c = new CustomUrlPrimaryDrawerItem()
                    .withName(name)
                    .withTag(tag)
                    .withIcon(iconUrl)
                    .withIdentifier(identif).withSelectable(false);

        } else {
            c = new CustomUrlPrimaryDrawerItem()
                    .withName(name)
                    .withTag(tag)
                    .withIcon(R.drawable.youtube_icon)
                    .withIdentifier(identif).withSelectable(false);
        }
        identif++;
        return c;
    }

    private void getInFirestone () {
        Map<String, Object> userChannels = new HashMap<>();
        if(mDrawerPresenter.getUser() != null) {
            docRefUserChannels
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if(task.getResult() != null && task.getResult().getData() != null) {
                                //mRepositoriesPresenter.forDrawer(task.getResult().getData());
                                mDrawerPresenter.setSubItems(task.getResult().getData());
                                //Log.e("getInFire task not null", task.getResult().getId() + " => " + task.getResult().getData());
                            } else {
                                mDrawerPresenter.setSubItems(userChannels);
                            }
                        } else {
                            Log.w("ДОК_ОШИБКА", "Error getting documents.", task.getException());
                        }
                    });
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setSubItems (List<String> tags) {

        List<RSSFeed> normals;

        expDrawItem = (ExpandableBadgeDrawerItem) mDrawer.getDrawerItem(50003);
        if(!tags.isEmpty()) {
            for(String tag : tags) {
                normals = mDrawerPresenter.getRssForTag(tag);

                if(normals.size() == 1) {

                    if(!expDrawItem.getSubItems().isEmpty()) {
                        if(expDrawItem.getSubItems().get(0).getIdentifier() == 20000) {expDrawItem.getSubItems().remove(0);}
                    }

                    expDrawItem.getSubItems().add(addCUPDrawerItem(
                            normals.get(0).getChannelTitle(),
                            normals.get(0).getUrl(),
                            normals.get(0).getIconUrl()));

                } else {

                    ExpandableBadgeDrawerItem expTest = new ExpandableBadgeDrawerItem()
                            .withName(tag)
                            .withIcon(GoogleMaterial.Icon.gmd_note)
                            .withTag(tag)
                            .withIdentifier(30000 + identif)
                            .withSelectable(false)
                            .withLevel(1)
                            .withBadgeStyle(new BadgeStyle().withTextColorRes(R.color.colorText).withColorRes(R.color.colorFon))
                            .withBadge("0")
                            .withSubItems()
                            .withIsExpanded(false);

                    for (RSSFeed r : normals) {
                        expTest.getSubItems().add(addCUPDrawerItem(r.getChannelTitle(),r.getUrl(),r.getIconUrl()));
                    }
                    expTest.withBadge(String.valueOf(expTest.getSubItems().size()));
                    expDrawItem.getSubItems().add(expTest);
                }
            }
            if(expDrawItem.getSubItems().get(0).getIdentifier() == 20000) { expDrawItem.getBadge().setText("!"); }
            else {expDrawItem.withBadge(String.valueOf(expDrawItem.getSubItems().size()));}

            mDrawer.updateItem(expDrawItem);
            if ((expDrawItem.getSubItems().get(0) instanceof ExpandableBadgeDrawerItem)) {
                oldUrl =  ((ExpandableBadgeDrawerItem)expDrawItem.getSubItems().get(0)).getSubItems().get(0).getTag().toString();
            } else {
                oldUrl = expDrawItem.getSubItems().get(0).getTag().toString();
            }
        } else {
            setZeroItemDrawer();
        }
        mRepositoriesPresenter.loadRepositories(false, oldUrl, "", isConnected());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CODE_SEARCH_ACTIVITY) {
                String url = data.getStringExtra("url");
                String iconUrl = data.getStringExtra("iconUrl");
                mRepositoriesPresenter.loadRepositories(true, url, iconUrl, isConnected());
                isNew = true;
            }
        }
    }

    @Override
    public void addNewNewsChannel (String name) {

    }

}
