package com.tierep.twitterlists.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.tierep.twitterlists.R;
import com.tierep.twitterlists.Session;
import com.tierep.twitterlists.TwitterListsAdapter;

import java.util.LinkedList;

import twitter4j.PagableResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UserList;

// TODO eventueel terug backwards compatiblitly onder api level 11 invoeren

/**
 * A list fragment representing a list of TwitterLists. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link ListDetailMembersFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ListsFragment extends ListFragment {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The serialization (saved instance state) Bundle key representing the
     * lists of the current user.
     */
    private static final String STATE_USERLISTS = "ListsFragment.userLists";

    private LinkedList<UserList> userLists;

    /**
     * The fragment's current callback object, which is notified of list item
     * clicks.
     */
    private Callbacks mCallbacks = sDummyCallbacks;

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callbacks {
        /**
         * Callback for when an item has been selected.
         */
        public void onItemSelected(UserList userList);
    }

    /**
     * A dummy implementation of the {@link Callbacks} interface that does
     * nothing. Used only when this fragment is not attached to an activity.
     */
    private static Callbacks sDummyCallbacks = new Callbacks() {
        @Override
        public void onItemSelected(UserList userList) {
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.userLists = (LinkedList<UserList>) savedInstanceState.getSerializable(STATE_USERLISTS);
            setListAdapter(new TwitterListsAdapter(getActivity(), userLists));
        } else {
            new AsyncTask<Void, Void, LinkedList<UserList>>() {
                @Override
                protected LinkedList<UserList> doInBackground(Void... params) {
                    Twitter twitter = Session.getInstance().getTwitterInstance();
                    try {
                        LinkedList<UserList> result = new LinkedList<>();
                        PagableResponseList<UserList> response = null;
                        do {
                            if (response == null) {
                                response = twitter.getUserListsOwnerships(Session.getInstance().getUserId(), 1000, -1);
                                result.addAll(response);
                            } else {
                                response = twitter.getUserListsOwnerships(Session.getInstance().getUserId(), 1000, response.getNextCursor());
                                result.addAll(response);
                            }
                        } while (response.hasNext());

                        return result;
                    } catch (TwitterException e) {
                        Log.e("ERROR", "Error during fetching lists of the user.", e);
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(LinkedList<UserList> result) {
                    if (result != null && result.size() == 0) {
                        ListsFragment.this.userLists = result;
                        setListAdapter(new ArrayAdapter<String>(getActivity(), 0, new String[] { "" }) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                return inflater.inflate(R.layout.twitter_lists_empty, parent, false);
                            }

                            @Override
                            public boolean isEnabled(int position) {
                                return false;
                            }
                        });
                    } else if (result != null) {
                        ListsFragment.this.userLists = result;
                        setListAdapter(new TwitterListsAdapter(getActivity(), result));
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
                    }
                    // TODO in case of error permanemente melding geven (loading.. balk weghalen !!)
                }
            }.execute();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_content, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Activities containing this fragment must implement its callbacks.
        if (!(activity instanceof Callbacks)) {
            throw new IllegalStateException("Activity must implement fragment's callbacks.");
        }

        mCallbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Reset the active callbacks interface to the dummy implementation.
        mCallbacks = sDummyCallbacks;
    }



    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        // Notify the active callbacks interface (the activity, if the
        // fragment is attached to one) that an item has been selected.
        mCallbacks.onItemSelected((UserList) getListAdapter().getItem(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
        outState.putSerializable(STATE_USERLISTS, userLists);
    }

    /**
     * Turns on activate-on-click mode. When this mode is on, list items will be
     * given the 'activated' state when touched.
     */
    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        getListView().setChoiceMode(activateOnItemClick
                ? ListView.CHOICE_MODE_SINGLE
                : ListView.CHOICE_MODE_NONE);
    }

    public void addUserList(UserList userList) {
        ListAdapter adapter = getListAdapter();
        if (adapter instanceof TwitterListsAdapter) {
            TwitterListsAdapter listAdapter = (TwitterListsAdapter) getListAdapter();
            listAdapter.add(userList);
            listAdapter.notifyDataSetChanged();
        } else { // The adapter is the adapter used for displaying the empty results.
            LinkedList<UserList> resultList = new LinkedList<>();
            resultList.add(userList);
            setListAdapter(new TwitterListsAdapter(getActivity(), resultList));
        }
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
    }
}
