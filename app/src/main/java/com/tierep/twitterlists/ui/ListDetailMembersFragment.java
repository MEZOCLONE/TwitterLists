package com.tierep.twitterlists.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tierep.twitterlists.R;
import com.tierep.twitterlists.Session;
import com.tierep.twitterlists.TwitterListMembersAdapter;

import java.util.LinkedList;
import java.util.List;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;

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
                            response = twitter.getUserListMembers(listId, -1);
                            result.addAll(response);
                        } else {
                            response = twitter.getUserListMembers(listId, response.getNextCursor());
                            result.addAll(response);
                        }
                    } while (response.hasNext());
                    // TODO alle users nog ophalen en duplicates eruit filteren.

                    return result;
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<User> users) {
                if (users != null) {
                    TwitterListMembersAdapter adapter = new TwitterListMembersAdapter(getActivity(), users, listId, R.drawable.member_delete) {
                        /**
                         * Method that can be overriden in a base class to implement a click behaviour when the special
                         * action on the member is invoked.
                         *
                         * @param listId
                         * @param user
                         */
                        @Override
                        protected void onMemberClick(long listId, final User user) {
                            super.onMemberClick(listId, user);
                            new AsyncTask<Long, Void, Void>() {
                                @Override
                                protected Void doInBackground(Long... params) {
                                    long listId = params[0];
                                    long userId = params[1];
                                    Twitter twitter = Session.getInstance().getTwitterInstance();
                                    try {
                                        twitter.destroyUserListMember(listId, userId);
                                    } catch (TwitterException e) {
                                        // TODO internationalize string resource
                                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                        Log.e("ERROR", "Error deleting member from list", e);
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void aVoid) {
                                    super.onPostExecute(aVoid);
                                    remove(user);
                                    notifyDataSetChanged();
                                }
                            }.execute(listId, user.getId());
                        }
                    };

                    setListAdapter(adapter);
                }
                // TODO hier nog de case afhandelen dat userLists null is.
                // TODO ook speciaal geval afhandelen dat de user geen lijsten heeft (count = 0).
            }
        }.execute();
    }

    public static ListDetailMembersFragment newInstance(long listId, String listName) {
        Bundle arguments = new Bundle();
        arguments.putLong(ListDetailMembersFragment.ARG_LIST_ID,listId);
        arguments.putString(ListDetailMembersFragment.ARG_LIST_NAME, listName);

        ListDetailMembersFragment frag = new ListDetailMembersFragment();
        frag.setArguments(arguments);

        return frag;
    }
}
