package com.tierep.twitterlists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import twitter4j.User;

/**
 * An adapter that is used for displaying the members of a twitter list.
 * <p/>
 * Created by pieter on 31/01/15.
 */
public class TwitterListMembersAdapter extends ArrayAdapter<User> {

    long listId;
    int actionDrawableId;

    /**
     * Constructor
     *
     * @param context The current context.
     * @param objects The objects to represent in the ListView.
     * @param listId  The Id of the twitter list that this adapter represents.
     */
    public TwitterListMembersAdapter(Context context, List<User> objects, long listId, int actionDrawableId) {
        super(context, 0, objects);
        this.listId = listId;
        this.actionDrawableId = actionDrawableId;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.twitter_list_member, parent, false);

            holder = new ViewHolder();
            holder.image = (ImageView) view.findViewById(R.id.twitter_list_member_image);
            holder.name = (TextView) view.findViewById(R.id.twitter_list_member_name);
            holder.twitterName = (TextView) view.findViewById(R.id.twitter_list_member_twittername);
            holder.description = (TextView) view.findViewById(R.id.twitter_list_member_description);
            holder.action = (ImageView) view.findViewById(R.id.twitter_list_member_action);
            holder.action.setImageResource(actionDrawableId);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final User user = this.getItem(position);
        // TODO juiste size nog bepalen van het te downloaden profile pic
        new DownloadImageTask(holder.image).execute(user.getProfileImageURLHttps());
        //Log.d("USER", "Profile image url: " + user.getProfileImageURL());
        //Log.d("USER", "Profile image url https: " + user.getProfileImageURLHttps());
        holder.name.setText(user.getName());
        holder.twitterName.setText("@" + user.getScreenName());
        holder.description.setText(user.getDescription());
        holder.action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMemberClick(listId, user);
            }
        });
        return view;
    }

    /**
     * Method that can be overriden in a base class to implement a click behaviour when the special
     * action on the member is invoked.
     *
     * @param listId
     * @param user
     */
    protected void onMemberClick(long listId, final User user) {

    }

    private static class ViewHolder {
        ImageView image;
        TextView name;
        TextView twitterName;
        TextView description;
        ImageView action;
    }
}
