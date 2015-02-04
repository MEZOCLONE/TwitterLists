package com.tierep.twitterlists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import twitter4j.UserList;

/**
 * An adapter that is used for displaying the twitter lists of the user on the main activity.
 *
 * Created by pieter on 30/01/15.
 */
public class TwitterListsAdapter extends ArrayAdapter<UserList> {
    /**
     * Constructor
     *
     * @param context The current context.
     * @param objects The objects to represent in the ListView.
     */
    public TwitterListsAdapter(Context context, List<UserList> objects) {
        super(context, 0, objects);
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
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.twitter_list, parent, false);

            holder = new ViewHolder();
            holder.imagePrivacy = (ImageView) view.findViewById(R.id.twitter_list_privacy);
            holder.listName = (TextView) view.findViewById(R.id.twitter_list_name);
            holder.listDescription = (TextView) view.findViewById(R.id.twitter_list_description);
            holder.listMemberCount = (TextView) view.findViewById(R.id.twitter_list_member_count);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        UserList userList = this.getItem(position);
        if (userList.isPublic()) {
            holder.imagePrivacy.setVisibility(View.GONE);
        } else {
            holder.imagePrivacy.setVisibility(View.VISIBLE);
        }
        holder.listName.setText(userList.getName());
        setListDescription(holder.listDescription, userList.getDescription());
        holder.listMemberCount.setText(getMemberCountString(userList));
        return view;
    }

    private void setListDescription(TextView textView, String description) {
        if (description == null || description.equals("")) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setText(description);
            textView.setVisibility(View.VISIBLE);
        }
    }

    private String getMemberCountString(UserList userList) {
        int memberCount = userList.getMemberCount();
        String result = String.valueOf(memberCount) + " ";
        if (memberCount == 1) {
            result += getContext().getResources().getString(R.string.member);
        } else {
            result += getContext().getResources().getString(R.string.members);
        }
        return result;
    }

    private static class ViewHolder {
        ImageView imagePrivacy;
        TextView listName;
        TextView listDescription;
        TextView listMemberCount;
    }
}
