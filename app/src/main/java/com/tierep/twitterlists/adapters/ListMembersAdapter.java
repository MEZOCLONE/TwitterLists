package com.tierep.twitterlists.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.tierep.twitterlists.Session;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by pieter on 04/02/15.
 */
public class ListMembersAdapter extends UsersWithActionAdapter {
    /**
     * Constructor
     *
     * @param context The current context.
     * @param listId  The Id of the twitter list that this adapter represents.
     * @param objects The objects to represent in the ListView.
     */
    public ListMembersAdapter(Context context, long listId, List<UserView> objects) {
        super(context, listId, objects);
    }

    /**
     * Method that can be overriden in a base class to implement a click behaviour when the special
     * action on the member is invoked.
     *
     * @param position
     * @param listId
     * @param userView
     */
    @Override
    protected void onMemberClick(int position, long listId, final UserView userView) {
        super.onMemberClick(position, listId, userView);

        new AsyncTask<Long, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Long... params) {
                long listId = params[0];
                long userId = params[1];
                Twitter twitter = Session.getInstance().getTwitterInstance();
                try {
                    twitter.destroyUserListMember(listId, userId);
                    return true;
                } catch (TwitterException e) {
                    Log.e("ERROR", "Error deleting member from list", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result) {
                    remove(userView);
                    notifyDataSetChanged();
                } else {
                    // TODO internationalize string resource
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(listId, userView.user.getId());
    }
}
