package com.imra.mynews.ui.adapters;

import android.annotation.TargetApi;
import android.os.Build;
import androidx.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.imra.mynews.R;
import com.imra.mynews.di.modules.GlideApp;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;
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
import io.reactivex.disposables.Disposable;
import moxy.MvpDelegate;
import moxy.presenter.InjectPresenter;
import moxy.presenter.ProvidePresenter;

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
    private RSSFeed rssFeed;

    private String checkRssTitle = "";
    private String checkRssTitle2 = "";

    private OnScrollToBottomListener mScrollToBottomListener;

    public RepositoriesAdapter(MvpDelegate<?> parentDelegate, OnScrollToBottomListener scrollToBottomListener) {
        super(parentDelegate, String.valueOf(0));
        mScrollToBottomListener = scrollToBottomListener;
        mArticles = new ArrayList<>();
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
        //holder.setSize();
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

        @BindView(R.id.llOne)
        LinearLayout llOne;

        View view;

        private MvpDelegate mMvpDelegate;

        int maxWidth;
        int widthIV;
        int heightIV;
        LinearLayout.LayoutParams param;
        Disposable disposable;

        @ProvidePresenter
        RepositoryPresenter provideRepositoryPresenter() {
            return new RepositoryPresenter(mPosition, mArticle);
        }

        RepositoryHolder(View view) {
            this.view = view;

            ButterKnife.bind(this, view);
            setSize();
        }

        void setSize() {
            view.post(() -> {
                param = (LinearLayout.LayoutParams) llOne.getLayoutParams();
                maxWidth = view.getMeasuredWidth();
                widthIV = (int) (maxWidth * 0.375);
                heightIV = (int) (widthIV * 0.75);
                param.height = heightIV;
                param.width = widthIV;
                llOne.setLayoutParams(param);});
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

            //Выделение выбранного элемента
            //view.setBackgroundResource(position == mSelection ? R.color.colorAppMyNews : android.R.color.transparent);
//            if(position == mSelection) {
//                mRepositoryPresenter.testMyIdea(rssFeed, article);
//            }
            //Сохранить в Room
            //imageButton.setOnClickListener(v -> );
        }

        @TargetApi(Build.VERSION_CODES.O)
        @Override
        public void showRepository(int position, Article article) {


            if(mArticle.getEnclosure() == null) {

                if(mArticle.getEclos() != null) {
                    GlideApp
                            .with(view)
                            .asBitmap()
                            .load(mArticle.getEclos())
                            //.transition(withCrossFade())
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .centerCrop()
                            .override(480,360)
                            //.thumbnail(0.5f)
                            .into(imageView);
                    //System.out.println(1 + " " + position + " " + mArticle.getEclos());
                } else {
                    if(mArticle.getDescription() != null) {
                        Pattern p2 = Pattern.compile("https*://[^\"']+\\.(png|jpg|jpeg|gif)");
                        //Pattern p3 = Pattern.compile("https*://coub[^\"']+");

                        Matcher m2 = p2.matcher(mArticle.getDescription());

                        if(m2.find()) {
                            GlideApp
                                    .with(view)
                                    .asBitmap()
                                    .load(m2.group())
                                    //.transition(withCrossFade())
                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                    .centerCrop()
                                    .override(480,360)
                                    //.thumbnail(0.5f)
                                    .into(imageView);
                            //System.out.println(2 + " " + position + " " + m2.group());
                            if(llOne.getVisibility() == View.GONE) llOne.setVisibility(View.VISIBLE);
                        } else llOne.setVisibility(View.GONE);
                    } else llOne.setVisibility(View.GONE);
                }
            } else {
                if(llOne.getVisibility() == View.GONE) llOne.setVisibility(View.VISIBLE);
                if(mArticle.getEclos() == null) mArticle.setEclos(mArticle.getEnclosure().getUrl());

                GlideApp
                        .with(view)
                        .asBitmap()
                        .load(mArticle.getEclos())
                        //.transition(withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .centerCrop()
                        .override(480,360)
                        //.thumbnail(0.5f)
                        .into(imageView);
                //System.out.println(3 + " " + position + " " + mArticle.getEclos());
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
                mMvpDelegate.setParentDelegate(RepositoriesAdapter.this.getMvpDelegate(), String.valueOf(mPosition));
            }
            return mMvpDelegate;
        }
    }

    public interface OnScrollToBottomListener {
        void onScrollToBottom();
    }

}
