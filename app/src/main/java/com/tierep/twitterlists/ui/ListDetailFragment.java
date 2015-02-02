package com.tierep.twitterlists.ui;

import android.os.Bundle;
import android.app.ListFragment;

/**
 * An abstract class that serves as an abstraction for fragments that represents the details of a
 * list. Subclasses can be for example for fragments that contain the member or non-members of a
 * list.
 *
 * Created by pieter on 02/02/15.
 */
public abstract class ListDetailFragment extends ListFragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_LIST_ID = "item_id";
    public static final String ARG_LIST_NAME = "list_name";

    /**
     * The list this fragment is presenting.
     */
    protected long listId;
    protected String listName;

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

        if (args.containsKey(ARG_LIST_ID) && args.containsKey(ARG_LIST_NAME)) {
            listId = args.getLong(ARG_LIST_ID);
            listName = args.getString(ARG_LIST_NAME);

            this.initializeList();
        }
    }

    protected abstract void initializeList();
}
