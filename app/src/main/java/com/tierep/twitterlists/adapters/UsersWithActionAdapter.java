package com.tierep.twitterlists.adapters;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.tierep.twitterlists.R;

import java.util.List;

import twitter4j.User;

/**
 * An adapter that is used for displaying the members of a twitter list.
 * <p/>
 * Created by pieter on 31/01/15.
 */
public class UsersWithActionAdapter extends ArrayAdapter<UserView> {

    long listId;

    /**
     * Constructor
     *
     * @param context The current context.
     * @param objects The objects to represent in the ListView.
     * @param listId  The Id of the twitter list that this adapter represents.
     */
    public UsersWithActionAdapter(Context context, long listId, List<UserView> objects) {
        super(context, 0, objects);
        this.listId = listId;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @param position
     * @param convertView
     * @param parent
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.twitter_list_member, parent, false);

            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.twitter_list_member_image);
            holder.name = (TextView) view.findViewById(R.id.twitter_list_member_name);
            holder.description = (TextView) view.findViewById(R.id.twitter_list_member_description);
            holder.action = (ImageView) view.findViewById(R.id.twitter_list_member_action);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final UserView userView = this.getItem(position);
        final User user = userView.user;
        final int actionDrawableId = userView.actionDrawableId;

        Ion.with(holder.image)
                .placeholder(R.drawable.member_default_avatar)
                .load(determineProfileImageUrl(user));
        holder.name.setText(createTwitterNameString(user));
        holder.description.setText(user.getDescription());
        holder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMemberClick(position, listId, userView);
            }
        });
        holder.action.setImageResource(actionDrawableId);
        return view;
    }

    /**
     * Determines which size of the profile image should be downloaded. On mdpi (baseline) the space
     * reserved in the layout is 48px x 48px.
     *
     * Twitter profile pictures are available in the following sizes:
     * - mini: 24px x 24px
     * - normal: 48px x 48px
     * - bigger: 73px x 73px
     *
     * @param user
     * @return
     */
    private String determineProfileImageUrl(final User user) {
        String result;

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        switch (metrics.densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                result = user.getMiniProfileImageURLHttps();
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                result = user.getProfileImageURLHttps();
                break;
            default:
                result = user.getBiggerProfileImageURLHttps();
        }
        return result;
    }

    private Spanned createTwitterNameString(final User user) {
        return Html.fromHtml("<b>" + user.getName() + "</b>" + " @" + user.getScreenName());
    }

    /**
     * Method that can be overriden in a base class to implement a click behaviour when the special
     * action on the member is invoked.
     *
     * @param listId
     * @param userView
     */
    protected void onMemberClick(int position, long listId, final UserView userView) {

    }

    private static class ViewHolder {
        ImageView image;
        TextView name;
        TextView description;
        ImageView action;
    }
}
