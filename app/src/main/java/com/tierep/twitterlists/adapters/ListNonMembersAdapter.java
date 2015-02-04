package com.tierep.twitterlists.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.tierep.twitterlists.R;
import com.tierep.twitterlists.Session;

import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by pieter on 03/02/15.
 */
public class ListNonMembersAdapter extends UsersWithActionAdapter {
    public ListNonMembersAdapter(Context context, long listId, List<UserView> objects) {
        super(context, listId, objects);
    }

    @Override
    protected void onMemberClick(final int position, long listId, final UserView userView) {
        super.onMemberClick(position, listId, userView);

        switch (userView.actionDrawableId) {
            case R.drawable.member_add:
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
                            remove(userView);
                            UserView newUserView = new UserView(userView.user, R.drawable.member_added);
                            insert(newUserView, position);
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute(listId, userView.user.getId());
                break;
            case R.drawable.member_added:
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
                            remove(userView);
                            UserView newUserView = new UserView(userView.user, R.drawable.member_add);
                            insert(newUserView, position);
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                }.execute(listId, userView.user.getId());
                break;
        }
    }
}
