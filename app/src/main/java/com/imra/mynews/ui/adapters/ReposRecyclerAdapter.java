package com.imra.mynews.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.util.Preconditions;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.imra.mynews.R;
import com.imra.mynews.di.modules.GlideApp;
import com.imra.mynews.di.modules.GlideRequest;
import com.imra.mynews.di.modules.GlideRequests;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.presenters.RepositoryPresenter;
import com.imra.mynews.mvp.views.RepositoryView;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import moxy.MvpDelegate;
import moxy.presenter.InjectPresenter;
import moxy.presenter.ProvidePresenter;

/**
 * Date: 13.09.2020
 * Time: 14:34
 *
 * @author IMRA027
 */
public class ReposRecyclerAdapter extends MvpBaseRecyclerAdapter<ReposRecyclerAdapter.ListViewHolder> implements ListPreloader.PreloadSizeProvider<Article>, ListPreloader.PreloadModelProvider<Article> {

    private static final int REPOSITORY_VIEW_TYPE = 0;
    private static final int PROGRESS_VIEW_TYPE = 1;
    private int mSelection = -1;
    private List<Article> mArticles;
    private final GlideRequest<Drawable> request;
    private final int screenWidth;
    private Context mContext;

    private int[] actualDimensions;

    private ReposRecyclerAdapter.OnScrollToBottomListener mScrollToBottomListener;

    public ReposRecyclerAdapter (Context context, MvpDelegate<?> parentDelegate, ReposRecyclerAdapter.OnScrollToBottomListener scrollToBottomListener, GlideRequests glideRequests) {
        super(parentDelegate, String.valueOf(0));
        mScrollToBottomListener = scrollToBottomListener;
        mArticles = new ArrayList<>();
        request = glideRequests.asDrawable().centerCrop();
        setHasStableIds(true);
        screenWidth = getScreenWidth(context);
        mContext = context;
    }

    public void setRepositories(@NonNull RSSFeed rssFeeds) {
        if(rssFeeds.getArticleList() != null) {
            mArticles = new ArrayList<>(rssFeeds.getArticleList());
        }
        notifyDataSetChanged();
    }

    public void addRepositories (@NonNull RSSFeed rssFeeds) {
        if(rssFeeds.getArticleList() != null) {
            mArticles.addAll(rssFeeds.getArticleList());
        }
        notifyDataSetChanged();
    }

    public void setSelection(int selection) {
        mSelection = selection;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position == mArticles.size() ? PROGRESS_VIEW_TYPE : REPOSITORY_VIEW_TYPE;
    }

    public Article getItem (int position) {
        return mArticles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    private static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        @SuppressLint("RestrictedApi") Display display = Preconditions.checkNotNull(wm).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View view = inflater.inflate(R.layout.item_layout, parent, false);
        view.getLayoutParams().width = screenWidth;

        //Log.e("Таг", "viewType " + viewType);

        if (actualDimensions == null) {
            view.getViewTreeObserver()
                    .addOnPreDrawListener(
                            new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    if (actualDimensions == null) {
                                        actualDimensions = new int[] {view.getWidth(), view.getHeight()};
                                    }
                                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                                    return true;
                                }
                            });
        }

        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        Article a = mArticles.get(position);
        holder.bind(a,position);
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder implements RepositoryView{

        @InjectPresenter
        RepositoryPresenter mRepositoryPresenter;

        private Article mArticle;
        private int mPosition;

        @BindView(R.id.item_title)
        TextView titleTextView;

        @BindView(R.id.tvTime)
        TextView mTimeTextView;

        @BindView(R.id.tvPubDate)
        TextView mPubDate;

        @BindView(R.id.item_view_main)
        ImageView imageView;

        @BindView(R.id.llOne)
        LinearLayout llOne;

        private MvpDelegate mMvpDelegate;

        @ProvidePresenter
        RepositoryPresenter provideRepositoryPresenter() {
            return new RepositoryPresenter(mPosition, mArticle);
        }

        private View view;

        ListViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            ButterKnife.bind(this, view);
        }

        void bind (Article article, int position) {
            mPosition = position;

            if (getMvpDelegate() != null) {
                getMvpDelegate().onSaveInstanceState();
                getMvpDelegate().onDetach();
                getMvpDelegate().onDestroyView();
                mMvpDelegate = null;
            }

            mArticle = article;

            getMvpDelegate().onCreate();
            getMvpDelegate().onAttach();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void showRepository(int position, Article article) {

            if(llOne.getVisibility() == View.GONE) llOne.setVisibility(View.VISIBLE);
            if(mArticle.getEclos() != null) {
                request.clone().load(mArticle.getEclos()).override(480,360).into(imageView);
            } else { llOne.setVisibility(View.GONE);}

            titleTextView.setText(Html.fromHtml(mArticle.getTitle()));
            DateTimeFormatter fmt;
            if(mArticle.getPubDate() != null) {
                String temp = mArticle.getPubDate();
                if(temp.substring(temp.length()-3, temp.length()).equals("GMT")) {
                    fmt = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US).withZone(ZoneOffset.UTC);
                } else {
                    fmt = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss Z", Locale.US).withZone(ZoneOffset.UTC);
                }

                TemporalAccessor date = fmt.parse(temp);
                Instant time = Instant.from(date);

                DateTimeFormatter fmtOut = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC);
                DateTimeFormatter fmtOut2 = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault()).withZone(ZoneOffset.UTC);

                mTimeTextView.setText(fmtOut.format(time));
                mPubDate.setText(fmtOut2.format(time));
            }
        }

        @Override
        public void saveOrDelete(boolean isSave) {

        }

        @Override
        public void greenOrNot(boolean isSave) {

        }

        MvpDelegate getMvpDelegate() {
            if (mArticle == null) {
                return null;
            }

            if (mMvpDelegate == null) {
                mMvpDelegate = new MvpDelegate<>(this);
                mMvpDelegate.setParentDelegate(ReposRecyclerAdapter.this.getMvpDelegate(), String.valueOf(mPosition));
            }
            return mMvpDelegate;
        }
    }

    @NonNull
    @Override
    public List<Article> getPreloadItems(int position) {
        return mArticles.isEmpty() ?
                Collections.<Article>emptyList() :
                Collections.singletonList(mArticles.get(position));
    }

    @Nullable
    @Override
    public RequestBuilder<Drawable> getPreloadRequestBuilder(@NonNull Article item) {
        return request.clone().load(item.getEclos()).override(480,360);
    }

    @Nullable
    @Override
    public int[] getPreloadSize(@NonNull Article item, int adapterPosition, int perItemPosition) {
        return actualDimensions;
    }

    public interface OnScrollToBottomListener {
        void onScrollToBottom();
    }
}
