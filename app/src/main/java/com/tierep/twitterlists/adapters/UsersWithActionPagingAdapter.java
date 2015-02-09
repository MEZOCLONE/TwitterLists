package com.tierep.twitterlists.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tierep.twitterlists.R;

import java.util.LinkedList;

import twitter4j.PagableResponseList;
import twitter4j.User;

/**
 * Created by pieter on 08/02/15.
 */
public abstract class UsersWithActionPagingAdapter extends UsersWithActionAdapter {

    public static final int SCROLLING_OFFSET = 5;

    /**
     * Constructor
     *
     * @param context The current context.
     * @param listId  The Id of the twitter list that this adapter represents.
     * @param users   The objects to represent in the ListView.
     * @param actions
     */
    public UsersWithActionPagingAdapter(Context context, long listId, PagableResponseList<User> users, LinkedList<Integer> actions) {
        super(context, listId, users, actions);
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        if (users.hasNext()) {
            return users.size() + 1;
        } else {
            return users.size();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @param position
     * @param convertView
     * @param parent
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (users.hasNext() && (SCROLLING_OFFSET == (users.size() - position))) {
            fetchNewData();
            return super.getView(position, convertView, parent);
        } else if (users.hasNext() && position == users.size()) {
            return inflateLoadingView(convertView, parent);
        } else {
            return super.getView(position, convertView, parent);
        }
    }

    public abstract void fetchNewData();

    private View inflateLoadingView(View convertView, ViewGroup parent) {
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

        holder.name.setText("");
        holder.description.setText(R.string.pagination_loading_more_people);
        holder.action.setVisibility(View.INVISIBLE);
        holder.image.setVisibility(View.INVISIBLE);

        return view;
    }
}
