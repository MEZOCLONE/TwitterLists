package com.tierep.twitterlists.adapters;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.tierep.twitterlists.R;

import java.util.LinkedList;

import twitter4j.PagableResponseList;
import twitter4j.User;

/**
 * An adapter that is used for displaying the members of a twitter list.
 * <p/>
 * Created by pieter on 31/01/15.
 */
public class UsersWithActionAdapter extends BaseAdapter {

    protected Context context;
    long listId;
    protected PagableResponseList<User> users;
    protected LinkedList<Integer> actions;

    /**
     * Constructor
     *
     * @param context The current context.
     * @param listId  The Id of the twitter list that this adapter represents.
     * @param users The objects to represent in the ListView.
     */
    public UsersWithActionAdapter(Context context, long listId, PagableResponseList<User> users, LinkedList<Integer> actions) {
        super();
        this.context = context;
        this.listId = listId;
        this.users = users;
        this.actions = actions;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return users.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
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
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        final User user = this.users.get(position);
        final int actionDrawableId = this.actions.get(position);

        holder.image.setVisibility(View.VISIBLE);
        Ion.with(holder.image)
                .placeholder(R.drawable.member_default_avatar)
                .load(determineProfileImageUrl(user));
        holder.name.setText(createTwitterNameString(user));
        holder.description.setText(user.getDescription());
        holder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMemberClick(position, listId, user, actionDrawableId);
            }
        });
        holder.action.setImageResource(actionDrawableId);
        holder.action.setVisibility(View.VISIBLE);
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

        DisplayMetrics metrics = this.context.getResources().getDisplayMetrics();
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
     * @param user
     * @param actionDrawableId
     */
    protected void onMemberClick(int position, long listId, final User user, final int actionDrawableId) {

    }

    public PagableResponseList<User> getUsers() {
        return users;
    }

    public LinkedList<Integer> getActions() {
        return actions;
    }


    protected static class ViewHolder {
        ImageView image;
        TextView name;
        TextView description;
        ImageView action;
    }
}
