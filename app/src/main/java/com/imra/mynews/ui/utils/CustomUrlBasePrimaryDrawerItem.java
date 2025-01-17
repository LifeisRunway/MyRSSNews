package com.imra.mynews.ui.utils;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.mikepenz.materialdrawer.holder.ColorHolder;
import com.mikepenz.materialdrawer.holder.ImageHolder;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.BaseDrawerItem;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerUIUtils;

import static com.mikepenz.materialdrawer.util.DrawerUIUtils.themeDrawerItem;

/**
 * Date: 09.08.2020
 * Time: 19:47
 *
 * @author IMRA027
 */
public abstract class CustomUrlBasePrimaryDrawerItem<T, VH extends RecyclerView.ViewHolder> extends BaseDrawerItem<T, VH> {
    public T withIcon(String url) {
        this.icon = new ImageHolder(url);
        return (T) this;
    }

    public T withIcon(Uri uri) {
        this.icon = new ImageHolder(uri);
        return (T) this;
    }

    private StringHolder description;
    private ColorHolder descriptionTextColor;

    public T withDescription(String description) {
        this.description = new StringHolder(description);
        return (T) this;
    }

    public T withDescription(@StringRes int descriptionRes) {
        this.description = new StringHolder(descriptionRes);
        return (T) this;
    }

    public T withDescriptionTextColor(@ColorInt int color) {
        this.descriptionTextColor = ColorHolder.fromColor(color);
        return (T) this;
    }

    public T withDescriptionTextColorRes(@ColorRes int colorRes) {
        this.descriptionTextColor = ColorHolder.fromColorRes(colorRes);
        return (T) this;
    }

    public StringHolder getDescription() {
        return description;
    }

    public ColorHolder getDescriptionTextColor() {
        return descriptionTextColor;
    }

    /**
     * a helper method to have the logic for all secondaryDrawerItems only once
     *
     * @param viewHolder
     */
    protected void bindViewHelper(CustomBaseViewHolder viewHolder) {
        Context ctx = viewHolder.itemView.getContext();

        //set the identifier from the drawerItem here. It can be used to run tests
        viewHolder.itemView.setId(hashCode());

        //set the item selected if it is
        viewHolder.itemView.setSelected(isSelected());

        //get the correct color for the background
        int selectedColor = getSelectedColor(ctx);
        //get the correct color for the text
        int color = getColor(ctx);
        int selectedTextColor = getSelectedTextColor(ctx);

        //set the background for the item
        themeDrawerItem(ctx, viewHolder.view, selectedColor, isSelectedBackgroundAnimated());
        //set the text for the name
        StringHolder.applyTo(this.getName(), viewHolder.name);
        //set the text for the description or hide
        StringHolder.applyToOrHide(this.getDescription(), viewHolder.description);

        //set the colors for textViews
        viewHolder.name.setTextColor(getTextColorStateList(color, selectedTextColor));
        //set the description text color
        ColorHolder.applyToOr(getDescriptionTextColor(), viewHolder.description, getTextColorStateList(color, selectedTextColor));

        //define the typeface for our textViews
        if (getTypeface() != null) {
            viewHolder.name.setTypeface(getTypeface());
            viewHolder.description.setTypeface(getTypeface());
        }

        //we make sure we reset the image first before setting the new one in case there is an empty one
        DrawerImageLoader.getInstance().cancelImage(viewHolder.icon);
        viewHolder.icon.setImageBitmap(null);
        //get the drawables for our icon and set it
        ImageHolder.applyTo(icon, viewHolder.icon, "customUrlItem");

        //for android API 17 --> Padding not applied via xml
        DrawerUIUtils.setDrawerVerticalPadding(viewHolder.view);
    }
}
