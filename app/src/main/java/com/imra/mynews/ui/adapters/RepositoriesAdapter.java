package com.imra.mynews.ui.adapters;

import android.annotation.TargetApi;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.imra.mynews.R;
import com.imra.mynews.di.modules.GlideApp;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.presenters.RepositoryLikesPresenter;
import com.imra.mynews.mvp.presenters.RepositoryPresenter;
import com.imra.mynews.mvp.views.RepositoryView;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * Date: 28.07.2019
 * Time: 14:28
 *
 * @author IMRA027
 *
 * 
 */
public class RepositoriesAdapter extends MvpBaseAdapter  {

    private static final int REPOSITORY_VIEW_TYPE = 0;
    private static final int PROGRESS_VIEW_TYPE = 1;

//    @InjectPresenter(type = PresenterType.WEAK, tag = RepositoryLikesPresenter.TAG)
//    RepositoryLikesPresenter mRepositoryLikesPresenter;

    private int mSelection = -1;
    private List<Article> mArticles;

    private String checkRssTitle = "";
    private String checkRssTitle2 = "";

    private OnScrollToBottomListener mScrollToBottomListener;

    public RepositoriesAdapter(MvpDelegate<?> parentDelegate, OnScrollToBottomListener scrollToBottomListener) {
        super(parentDelegate, String.valueOf(0));
        mScrollToBottomListener = scrollToBottomListener;
        mArticles = new ArrayList<>();
    }

    public void setRepositories(RSSFeed rssFeeds) {
       // if (rssFeeds.getChannelTitle() != null) {
            mArticles = new ArrayList<>(rssFeeds.getArticleList());
            if(checkRssTitle.equals("")) {checkRssTitle = rssFeeds.getChannelTitle();}
            checkRssTitle2 = rssFeeds.getChannelTitle();
        //}
        notifyDataSetChanged();
    }

    public void addRepositories (RSSFeed rssFeeds) {
        //if (rssFeeds.getChannelTitle() != null) {
            mArticles.addAll(rssFeeds.getArticleList());
            if(checkRssTitle.equals("")) {checkRssTitle = rssFeeds.getChannelTitle();}
            checkRssTitle2 = rssFeeds.getChannelTitle();
        //}
        notifyDataSetChanged();
    }

    private boolean oldRssTit () {
        return checkRssTitle.equals(checkRssTitle2);
    }

    public void setSelection(int selection) {
        mSelection = selection;

        notifyDataSetChanged();
    }

    public int getRepositoriesCount() {
        return mArticles.size();
    }

    //Тип макета по позиции
    @Override
    public int getItemViewType(int position) {
        return position == mArticles.size() ? PROGRESS_VIEW_TYPE : REPOSITORY_VIEW_TYPE;
    }

    @Override
    public int getCount() {
        return mArticles.size();
    }

    @Override
    public Article getItem(int position) {
        return mArticles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //Количество типов строк в списке
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(mArticles.isEmpty()) return null;

        if (getItemViewType(position) == PROGRESS_VIEW_TYPE) {
            if (mScrollToBottomListener != null) {
                mScrollToBottomListener.onScrollToBottom();
            }
            return new ProgressBar(parent.getContext());
        }

        RepositoryHolder holder;

        if (convertView != null) {
            holder = (RepositoryHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            holder = new RepositoryHolder(convertView);
            convertView.setTag(holder);
        }

        final Article item = getItem(position);

        holder.bind(position, item);

        return convertView;

    }

    public class RepositoryHolder implements RepositoryView {

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

        View view;

        private MvpDelegate mMvpDelegate;

        @ProvidePresenter
        RepositoryPresenter provideRepositoryPresenter() {
            return new RepositoryPresenter(mPosition, mArticle);
        }

        RepositoryHolder(View view) {
            this.view = view;

            ButterKnife.bind(this, view);
        }

        void bind(int position, Article article) {

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

            view.setBackgroundResource(position == mSelection ? R.color.colorAppMyNews : android.R.color.transparent);

            //Сохранить в Room
            //imageButton.setOnClickListener(v -> );
        }

        @TargetApi(Build.VERSION_CODES.O)
        @Override
        public void showRepository(int position, Article article) {

            if(mArticle.getEnclosure() == null) {
                if (mArticle.getDescription() != null) {
                    Pattern p2 = Pattern.compile("https*://[^\"']+\\.(png|jpg|jpeg|gif)");
                    //Pattern p3 = Pattern.compile("https*://coub[^\"']+");

                    Matcher m2 = p2.matcher(mArticle.getDescription());

                    if(m2.find()) {
                        GlideApp
                                .with(view)
                                .asBitmap()
                                .load(m2.group())
                                //.transition(withCrossFade())
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .centerCrop()
                                .override(480,360)
                                //.thumbnail(0.5f)
                                .into(imageView);
                        if(imageView.getVisibility() == View.GONE) imageView.setVisibility(View.VISIBLE);
                    } else imageView.setVisibility(View.GONE);
                } else imageView.setVisibility(View.GONE);
            } else {
                GlideApp
                        .with(view)
                        .asBitmap()
                        .load(mArticle.getEnclosure().getUrl())
                        //.transition(withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .centerCrop()
                        .override(480,360)
                        //.thumbnail(0.5f)

                        .into(imageView);
                if(imageView.getVisibility() == View.GONE) imageView.setVisibility(View.VISIBLE);
            }

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


        MvpDelegate getMvpDelegate() {
            if (mArticle == null) {
                return null;
            }

            if (mMvpDelegate == null) {
                mMvpDelegate = new MvpDelegate<>(this);
                mMvpDelegate.setParentDelegate(RepositoriesAdapter.this.getMvpDelegate(), String.valueOf(mPosition));
            }
            return mMvpDelegate;
        }
    }

    public interface OnScrollToBottomListener {
        void onScrollToBottom();
    }

}
