package com.imra.mynews.ui.adapters;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.PresenterType;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.imra.mynews.R;
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

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private OnScrollToBottomListener mScrollToBottomListener;

    public RepositoriesAdapter(MvpDelegate<?> parentDelegate, OnScrollToBottomListener scrollToBottomListener) {
        super(parentDelegate, String.valueOf(0));
        mScrollToBottomListener = scrollToBottomListener;
        mArticles = new ArrayList<>();
    }

    public void setRepositories(RSSFeed rssFeeds) {
        mArticles = new ArrayList<>(rssFeeds.getArticleList());
        notifyDataSetChanged();
    }

    public void addRepositories (RSSFeed rssFeeds) {
        mArticles.addAll(rssFeeds.getArticleList());
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


        @BindView(R.id.item_image_button)
        ImageButton imageButton;

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

            view.setBackgroundResource(position == mSelection ? R.color.colorAccent : android.R.color.transparent);

            //Сохранить в Room
            //imageButton.setOnClickListener(v -> );
        }

        @TargetApi(Build.VERSION_CODES.O)
        @Override
        public void showRepository(int position, Article article) {
            titleTextView.setText(mArticle.getTitle());

//            if(mArticle.getPubDate() != null) {
//                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US).withZone(ZoneOffset.UTC);
//                TemporalAccessor date = fmt.parse(mArticle.getPubDate());
//                Instant time = Instant.from(date);
//
//                DateTimeFormatter fmtOut = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneOffset.UTC);
//                DateTimeFormatter fmtOut2 = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.getDefault()).withZone(ZoneOffset.UTC);
//
//                mTimeTextView.setText(fmtOut.format(time));
//                mPubDate.setText(fmtOut2.format(time));
//
//            }

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
