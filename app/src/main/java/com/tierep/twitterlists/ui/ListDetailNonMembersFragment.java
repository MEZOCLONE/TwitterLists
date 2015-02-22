package com.tierep.twitterlists.ui;

import android.os.AsyncTask;
import android.os.Bundle;

import com.tierep.twitterlists.R;
import com.tierep.twitterlists.Session;
import com.tierep.twitterlists.adapters.ListNonMembersAdapter;
import com.tierep.twitterlists.twitter4jcache.TwitterCache;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import twitter4j.PagableResponseList;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserList;

/**
 * A fragment representing a single TwitterList detail screen.
 * This fragment is either contained in a {@link ListActivity}
 * in two-pane mode (on tablets) or a {@link ListDetailActivity}
 * on handsets.
 *
 * Created by pieter on 02/02/15.
 */
public class ListDetailNonMembersFragment extends ListDetailFragment {
    @Override
    protected void initializeList() {
        new AsyncTask<Void, Void, PagableResponseList<User>>() {
            @Override
            protected PagableResponseList<User> doInBackground(Void... params) {
                TwitterCache twitter = Session.getInstance().getTwitterCacheInstance();
                List<User> listMembers = new LinkedList<>();
                try {
                    PagableResponseList<User> response = null;

                    do {
                        if (response == null) {
                            response = twitter.getUserListMembers(userList.getId(), -1);
                            listMembers.addAll(response);
                        } else {
                            response = twitter.getUserListMembers(userList.getId(), response.getNextCursor());
                            listMembers.addAll(response);
                        }
                    } while (response.hasNext());
                } catch (TwitterException e) {
                    e.printStackTrace();
                }

                // The friend list is paged, the next response is fetched in the adapter.
                try {
                    PagableResponseList<User> response = twitter.getFriendsList(Session.getInstance().getUserId(), -1);

                        for (User user : listMembers) {
                            response.remove(user);
                        }
                    return response;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(PagableResponseList<User> users) {
                if (users != null) {
                    makeListAdapter(users, new LinkedList<>(Collections.nCopies(users.size(), R.drawable.member_add_touch)));
                }
                // TODO hier nog de case afhandelen dat userLists null is.
                // TODO ook speciaal geval afhandelen dat de user geen lijsten heeft (count = 0).
            }
        }.execute();
    }

    @Override
    protected void makeListAdapter(PagableResponseList<User> users, LinkedList<Integer> actions) {
        setListAdapter(new ListNonMembersAdapter(getActivity(), userList.getId(), users, actions));
    }

    public static ListDetailNonMembersFragment newInstance(UserList userList) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(ListDetailFragment.ARG_USERLIST, userList);

        ListDetailNonMembersFragment frag = new ListDetailNonMembersFragment();
        frag.setArguments(arguments);

        return frag;
    }
}
