package com.tierep.twitterlists.ui;

import android.os.AsyncTask;
import android.os.Bundle;

import com.tierep.twitterlists.R;
import com.tierep.twitterlists.Session;
import com.tierep.twitterlists.adapters.ListMembersAdapter;
import com.tierep.twitterlists.adapters.UserView;

import java.util.LinkedList;
import java.util.List;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
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
        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... params) {
                Twitter twitter = Session.getInstance().getTwitterInstance();
                List<User> result = new LinkedList<User>();
                try {
                    PagableResponseList<User> response = null;
                    do {
                        if (response == null) {
                            response = twitter.getUserListMembers(userList.getId(), -1);
                            result.addAll(response);
                        } else {
                            response = twitter.getUserListMembers(userList.getId(), response.getNextCursor());
                            result.addAll(response);
                        }
                    } while (response.hasNext());

                    return result;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<User> users) {
                if (users != null) {
                    LinkedList<UserView> userViews = UserView.convertFromUsers(users, R.drawable.member_delete);
                    usersInList = userViews;
                    makeListAdapter(userViews);
                }
                // TODO hier nog de case afhandelen dat userLists null is.
                // TODO ook speciaal geval afhandelen dat de user geen lijsten heeft (count = 0).
            }
        }.execute();
    }

    @Override
    protected void makeListAdapter(LinkedList<UserView> objects) {
        ListMembersAdapter adapter = new ListMembersAdapter(getActivity(), userList.getId(), objects);
        setListAdapter(adapter);
    }

    public static ListDetailMembersFragment newInstance(UserList userList) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(ListDetailFragment.ARG_USERLIST, userList);

        ListDetailMembersFragment frag = new ListDetailMembersFragment();
        frag.setArguments(arguments);

        return frag;
    }
}
