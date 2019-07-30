package com.imra.mynews.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.arellomobile.mvp.presenter.ProvidePresenter;
import com.imra.mynews.R;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.RSSFeed;
import com.imra.mynews.mvp.presenters.RepositoryPresenter;
import com.imra.mynews.mvp.views.RepositoryView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Date: 28.07.2019
 * Time: 14:28
 *
 * @author IMRA027
 */
public class RepositoriesAdapter extends MvpBaseAdapter  {

    private int mSelection = -1;
    private RSSFeed mRSSFeed;
    private List<Article> mArticles;

    private OnScrollToBottomListener mScrollToBottomListener;

    public RepositoriesAdapter(MvpDelegate<?> parentDelegate, OnScrollToBottomListener scrollToBottomListener) {
        super(parentDelegate, String.valueOf(0));

        mScrollToBottomListener = scrollToBottomListener;
        mArticles = new ArrayList<>();

    }

    public void addRepositories (RSSFeed rssFeeds) {
        mRSSFeed = rssFeeds;
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

    @Override
    public int getItemViewType(int position) {
        return position == mArticles.size() ? 1 : 0;
    }

    @Override
    public int getCount() {
        return mArticles.size() + 1;
    }

    @Override
    public Article getItem(int position) {
        return mArticles.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (mArticles.get(position) != null) return position;
        else return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (getItemViewType(position) == 1) {
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

        @BindView(R.id.item_title)
        TextView titleTextView;

        View view;

        private MvpDelegate mMvpDelegate;

        @ProvidePresenter
        RepositoryPresenter provideRepositoryPresenter() {
            return new RepositoryPresenter(mArticle);
        }

        RepositoryHolder(View view) {
            this.view = view;

            ButterKnife.bind(this, view);
        }

        void bind(int position, Article article) {
            if (getMvpDelegate() != null) {
                getMvpDelegate().onSaveInstanceState();
                getMvpDelegate().onDetach();
                getMvpDelegate().onDestroyView();
                mMvpDelegate = null;
            }

            mArticle = article;

            getMvpDelegate().onCreate();
            getMvpDelegate().onAttach();

            view.setBackgroundResource(position == -1 ? R.color.colorAccent : android.R.color.transparent);
        }

        @Override
        public void showRepository(Article article) {
            titleTextView.setText(article.getTitle());
        }


        MvpDelegate getMvpDelegate() {
            if (mRSSFeed == null) {
                return null;
            }

            if (mMvpDelegate == null) {
                mMvpDelegate = new MvpDelegate<>(this);
                mMvpDelegate.setParentDelegate(RepositoriesAdapter.this.getMvpDelegate(), String.valueOf(1));

            }
            return mMvpDelegate;
        }
    }

    public interface OnScrollToBottomListener {
        void onScrollToBottom();
    }

}
