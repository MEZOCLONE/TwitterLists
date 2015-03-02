package com.tierep.twitterlists.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.tierep.twitterlists.R;
import com.tierep.twitterlists.Session;
import com.tierep.twitterlists.twitter4jcache.TwitterCache;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

/**
 * Created by pieter on 03/02/15.
 */
public class ListNonMembersAdapter extends UsersWithActionPagingAdapter {

    /**
     * Constructor
     *
     * @param context The current context.
     * @param listId  The Id of the twitter list that this adapter represents.
     * @param users   The objects to represent in the ListView.
     * @param actions
     */
    public ListNonMembersAdapter(Context context, long listId, PagableResponseList<User> users, LinkedList<Integer> actions) {
        super(context, listId, users, actions);
    }

    @Override
    protected void onMemberClick(final int position, long listId, final User user, final int actionDrawableId) {
        super.onMemberClick(position, listId, user, actionDrawableId);

        switch (actionDrawableId) {
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
                            actions.add(position, R.drawable.member_added_touch);
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, context.getString(R.string.text_something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute(listId, user.getId());
                break;
            case R.drawable.member_added_touch:
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
                            Log.e("ERROR", "Error removing member from list", e);
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
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute(listId, user.getId());
                break;
        }
    }

    @Override
    public void fetchNewData() {
        new AsyncTask<Void, Void, PagableResponseList<User>>() {
            @Override
            protected PagableResponseList<User> doInBackground(Void... params) {
                TwitterCache twitter = Session.getInstance().getTwitterCacheInstance();
                List<User> listMembers = new LinkedList<>();
                try {
                    PagableResponseList<User> response = null;

                    do {
                        if (response == null) {
                            response = twitter.getUserListMembers(listId, -1);
                            listMembers.addAll(response);
                        } else {
                            response = twitter.getUserListMembers(listId, response.getNextCursor());
                            listMembers.addAll(response);
                        }
                    } while (response.hasNext());
                } catch (TwitterException e) {
                    e.printStackTrace();
                }

                try {
                    PagableResponseList<User> responseList = twitter.getFriendsList(Session.getInstance().getUserId(), users.getNextCursor());

                    for (User user : listMembers) {
                            responseList.remove(user);
                    }

                    return responseList;
                } catch (TwitterException e) {
                    Log.e("ERROR", "Error during fetching new data of friends list.", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(PagableResponseList<User> respons) {
                if (users != null) {
                    respons.addAll(0, users);
                    actions.addAll(Collections.nCopies(respons.size(), R.drawable.member_add_touch));
                    users = respons;
                    notifyDataSetChanged();
                    finishedFetchingNewData();
                }
                // TODO error value ( = null) beter afhandelen
            }
        }.execute();
    }
}
