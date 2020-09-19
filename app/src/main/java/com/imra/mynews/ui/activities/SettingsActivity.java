package com.imra.mynews.ui.activities;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.imra.mynews.R;
import com.imra.mynews.mvp.models.Article;
import com.imra.mynews.mvp.presenters.MainPresenter;
import com.imra.mynews.mvp.views.MainInterface;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import moxy.MvpAppCompatActivity;
import moxy.presenter.InjectPresenter;

/**
 * Date: 15.09.2020
 * Time: 20:24
 *
 * @author IMRA027
 */
public class SettingsActivity extends MvpAppCompatActivity implements MainInterface {

    @InjectPresenter
    MainPresenter mMainPresenter;

    @BindView(R.id.toolbar_contacts)
    Toolbar mToolbar;

    @BindView(R.id.color_text)
    TextView colorText;

    @BindView(R.id.color_switch)
    Switch colorSwitch;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle(R.string.settings);
        if(mMainPresenter.getColorModSP()) {
            colorSwitch.setChecked(true);
        }
        colorSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                mMainPresenter.saveColorModSP(true);
            } else {
                mMainPresenter.saveColorModSP(false);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void setSelection(int position) {

    }

    @Override
    public void showDetailsContainer(int position) {

    }

    @Override
    public void showDetails(int position, Article article) {

    }
}
