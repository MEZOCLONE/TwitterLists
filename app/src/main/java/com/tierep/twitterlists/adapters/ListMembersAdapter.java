package com.tierep.twitterlists.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.tierep.twitterlists.R;
import com.tierep.twitterlists.Session;

import java.util.LinkedList;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Created by pieter on 04/02/15.
 */
public class ListMembersAdapter extends UsersWithActionAdapter {

    /**
     * Constructor
     *
     * @param context The current context.
     * @param listId  The Id of the twitter list that this adapter represents.
     * @param users   The objects to represent in the ListView.
     * @param actions
     */
    public ListMembersAdapter(Context context, long listId, PagableResponseList<User> users, LinkedList<Integer> actions) {
        super(context, listId, users, actions);
    }

    /**
     * Method that can be overriden in a base class to implement a click behaviour when the special
     * action on the member is invoked.
     *
     * @param position
     * @param listId
     * @param user
     */
    @Override
    protected void onMemberClick(final int position, long listId, final User user, final int actionDrawableId) {
        super.onMemberClick(position, listId, user, actionDrawableId);

        switch (actionDrawableId) {
            case R.drawable.member_delete_touch:
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
                            actions.remove(position);
                            actions.add(position, R.drawable.member_add_touch);
                            notifyDataSetChanged();
                        } else {
                            // TODO internationalize string resource
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute(listId, user.getId());
                break;
            case R.drawable.member_add_touch:
                new AsyncTask<Long, Void, Boolean>() {
                    @Override
                    protected Boolean doInBackground(Long... params) {
                        long listId = params[0];
                        long userId = params[1];
                        Twitter twitter = Session.getInstance().getTwitterInstance();
                        try {
                            twitter.createUserListMember(listId, userId);
                            return true;
                        } catch (TwitterException e) {
                            Log.e("ERROR", "Error adding member to list", e);
                            return false;
                        }
                    }

                    @Override
                    protected void onPostExecute(Boolean result) {
                        super.onPostExecute(result);
                        if (result) {
                            actions.remove(position);
                            actions.add(position, R.drawable.member_delete_touch);
                            notifyDataSetChanged();
                        } else {
                            // TODO internationalize string resource
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute(listId, user.getId());
                break;
        }


    }
}
