package com.imra.mynews.ui.adapters;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.imra.mynews.R;
import com.imra.mynews.di.modules.GlideApp;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.models.ItemHtml;
import com.imra.mynews.mvp.presenters.RepositoryPresenter;
import com.imra.mynews.mvp.views.RepositoryView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import moxy.MvpDelegate;
import moxy.presenter.InjectPresenter;
import moxy.presenter.ProvidePresenter;

/**
 * Date: 16.05.2020
 * Time: 22:47
 *
 * @author IMRA027
 */
public class SearchRSSAdapter extends MvpBaseAdapter {

    private static final int REPOSITORY_VIEW_TYPE = 0;
    private static final int PROGRESS_VIEW_TYPE = 1;

    private List<ItemHtml> mItemHtml;
    private int mSelection = -1;

    public SearchRSSAdapter(MvpDelegate<?> parentDelegate, String childId) {
        super(parentDelegate, String.valueOf(0));
        mItemHtml = new ArrayList<>();
    }

    public void setRepositories(List<ItemHtml> itemHtml) {
        mItemHtml = new ArrayList<>(itemHtml);
        notifyDataSetChanged();
    }

    public void addRepositories (List<ItemHtml> itemHtml) {
        mItemHtml.addAll(itemHtml);
        notifyDataSetChanged();
    }

    public void setSelection(int selection) {
        mSelection = selection;
        notifyDataSetChanged();
    }


    //Тип макета по позиции
    @Override
    public int getItemViewType(int position) {
        return position == mItemHtml.size() ? PROGRESS_VIEW_TYPE : REPOSITORY_VIEW_TYPE;
    }

    @Override
    public int getCount() {
        return mItemHtml.size();
    }

    @Override
    public Object getItem(int position) {
        return mItemHtml.get(position);
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
        if(mItemHtml.isEmpty()) {return null;}

        if (getItemViewType(position) == PROGRESS_VIEW_TYPE) {
            return new ProgressBar(parent.getContext());
        }

        RepositoryHolder holder;
        if (convertView != null) {
            holder = (RepositoryHolder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_rss_item_layout, parent, false);
            holder = new RepositoryHolder(convertView);
            convertView.setTag(holder);
        }

        final ItemHtml item = (ItemHtml) getItem(position);

        holder.bind(position, item);

        return convertView;


    }

    public class RepositoryHolder implements RepositoryView {

        @InjectPresenter
        RepositoryPresenter mRepositoryPresenter;

        private ItemHtml mItemHtml;
        private int mPosition;

        @BindView(R.id.item_title)
        TextView titleTextView;

        @BindView(R.id.tvPubDate)
        TextView urlTextView;

        @BindView(R.id.item_image_view)
        ImageView imageView;

        View view;

        private MvpDelegate mMvpDelegate;

        @ProvidePresenter
        RepositoryPresenter provideRepositoryPresenter() {
            return new RepositoryPresenter(mPosition, new Article()); //ставим заглушку Артикль
        }

        RepositoryHolder(View view) {
            this.view = view;

            ButterKnife.bind(this, view);
        }

        void bind(int position, ItemHtml itemHtml) {

            mPosition = position;

            if (getMvpDelegate() != null) {
                getMvpDelegate().onSaveInstanceState();
                getMvpDelegate().onDetach();
                getMvpDelegate().onDestroyView();
                mMvpDelegate = null;
            }

            mItemHtml = itemHtml;

            getMvpDelegate().onCreate();
            getMvpDelegate().onAttach();

            view.setBackgroundResource(position == mSelection ? R.color.colorAccent : android.R.color.transparent);

            //Сохранить в Room
            //imageButton.setOnClickListener(v -> );
        }

        @TargetApi(Build.VERSION_CODES.O)
        @Override
        public void showRepository(int position, Article article) {
            titleTextView.setText(mItemHtml.getTitle());
            urlTextView.setText(mItemHtml.getHref());
            GlideApp
                    .with(view)
                    .load(mItemHtml.getIcon_url())
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .fitCenter()
                    .into(imageView);
        }

        @Override
        public void saveOrDelete(boolean isSave) {

        }

        @Override
        public void greenOrNot(boolean isSave) {

        }


        MvpDelegate getMvpDelegate() {
            if (mItemHtml == null) {
                return null;
            }

            if (mMvpDelegate == null) {
                mMvpDelegate = new MvpDelegate<>(this);
                mMvpDelegate.setParentDelegate(SearchRSSAdapter.this.getMvpDelegate(), String.valueOf(mPosition));
            }
            return mMvpDelegate;
        }
    }
}
