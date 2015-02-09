package com.tierep.twitterlists.ui;

import android.app.ListFragment;
import android.os.Bundle;

import com.tierep.twitterlists.adapters.UsersWithActionAdapter;

import java.util.LinkedList;

import twitter4j.PagableResponseList;
import twitter4j.User;
import twitter4j.UserList;

/**
 * An abstract class that serves as an abstraction for fragments that represents the details of a
 * list. Subclasses can be for example for fragments that contain the member or non-members of a
 * list.
 *
 * Created by pieter on 02/02/15.
 */
public abstract class ListDetailFragment extends ListFragment {
    /**
     * The fragment argument representing the UserList that this fragment
     * represents.
     */
    public static final String ARG_USERLIST = "userList";

    /**
     * The serialization (saved instance state) Bundle keys representing the
     * members in the current list.
     */
    private static final String STATE_USERSINLIST = "usersInList";
    private static final String STATE_USERSINLISTACTIONS = "usersInListActions";

    /**
     * The list this fragment is presenting.
     */
    protected UserList userList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ListDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_USERLIST)) {
            userList = (UserList) args.getSerializable(ARG_USERLIST);

            if (savedInstanceState != null) {
                PagableResponseList<User> users = (PagableResponseList<User>) savedInstanceState.getSerializable(STATE_USERSINLIST);
                LinkedList<Integer> actions = (LinkedList<Integer>) savedInstanceState.getSerializable(STATE_USERSINLISTACTIONS);
                makeListAdapter(users, actions);
            } else {
                this.initializeList();
            }
        }
    }

    /**
     * Fetches the list from the remote server, sets the usersInList member variable
     * and makes the list adapter.
     */
    protected abstract void initializeList();

    protected abstract void makeListAdapter(PagableResponseList<User> users, LinkedList<Integer> actions);

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // TODO moet userList niet opgeslaan worden ?

        UsersWithActionAdapter adapter = (UsersWithActionAdapter) getListAdapter();
        outState.putSerializable(STATE_USERSINLIST, adapter.getUsers());
        outState.putSerializable(STATE_USERSINLISTACTIONS, adapter.getActions());
    }
}
