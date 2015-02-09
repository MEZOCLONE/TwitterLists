package com.tierep.twitterlists.ui;

import android.os.AsyncTask;
import android.os.Bundle;

import com.tierep.twitterlists.R;
import com.tierep.twitterlists.Session;
import com.tierep.twitterlists.adapters.ListMembersAdapter;
import com.tierep.twitterlists.twitter4jcache.TwitterCache;

import java.util.Collections;
import java.util.LinkedList;

import twitter4j.PagableResponseList;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.UserList;

/**
 * A fragment representing a single TwitterList detail screen.
 * This fragment is either contained in a {@link ListActivity}
 * in two-pane mode (on tablets) or a {@link ListDetailActivity}
 * on handsets.
 */
public class ListDetailMembersFragment extends ListDetailFragment {
    @Override
    protected void initializeList() {
        // TODO ideaal zou dit zo gebeuren dat de paging gebeurt wanneer er naar beneden gescrolt wordt.
        // TODO dit aantal dat telkens moet opgehaald worden hangt af van tablet/gsm grootte
        new AsyncTask<Void, Void, PagableResponseList<User>>() {
            @Override
            protected PagableResponseList<User> doInBackground(Void... params) {
                TwitterCache twitter = Session.getInstance().getTwitterCacheInstance();
                try {
                    PagableResponseList<User> response = null;
                    do {
                        if (response == null) {
                            response = twitter.getUserListMembers(userList.getId(), -1);
                        } else {

                            PagableResponseList<User> nextPage = twitter.getUserListMembers(userList.getId(), response.getNextCursor());
                            nextPage.addAll(0, response);
                            response = nextPage;
                        }
                    } while (response.hasNext());

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
        setListAdapter(new ListMembersAdapter(getActivity(), userList.getId(), users, actions));
    }

    public static ListDetailMembersFragment newInstance(UserList userList) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(ListDetailFragment.ARG_USERLIST, userList);

        ListDetailMembersFragment frag = new ListDetailMembersFragment();
        frag.setArguments(arguments);

        return frag;
    }
}
