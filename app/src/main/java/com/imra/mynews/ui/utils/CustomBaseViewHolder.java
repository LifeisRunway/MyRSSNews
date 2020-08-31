package com.imra.mynews.ui.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.imra.mynews.R;

/**
 * Date: 09.08.2020
 * Time: 19:49
 *
 * @author IMRA027
 */
class CustomBaseViewHolder extends RecyclerView.ViewHolder {
    protected View view;
    protected ImageView icon;
    protected TextView name;
    protected TextView description;

    public CustomBaseViewHolder(View view) {
        super(view);

        this.view = view;
        this.icon = (ImageView) view.findViewById(R.id.material_drawer_icon);
        this.name = (TextView) view.findViewById(R.id.material_drawer_name);
        this.description = (TextView) view.findViewById(R.id.material_drawer_description);
    }
}
