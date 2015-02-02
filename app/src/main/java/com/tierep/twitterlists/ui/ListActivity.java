package com.tierep.twitterlists.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tierep.twitterlists.R;

import twitter4j.UserList;


/**
 * An activity representing a list of TwitterLists. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ListDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ListsFragment} and the item details
 * (if present) is a {@link ListDetailMembersFragment}.
 * <p/>
 * This activity also implements the required
 * {@link ListsFragment.Callbacks} interface
 * to listen for item selections.
 */
public class ListActivity extends Activity
        implements ListsFragment.Callbacks {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitterlist_list);

        if (findViewById(R.id.twitterlist_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((ListsFragment) getFragmentManager()
                    .findFragmentById(R.id.twitterlist_list))
                    .setActivateOnItemClick(true);
        }

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link ListsFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(UserList userList) {
        long id = userList.getId();
        String name = userList.getName();

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putLong(ListDetailFragment.ARG_LIST_ID, id);
            arguments.putString(ListDetailFragment.ARG_LIST_NAME, name);
            ListDetailViewPagerFragment fragment = new ListDetailViewPagerFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.twitterlist_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ListDetailActivity.class);
            detailIntent.putExtra(ListDetailMembersFragment.ARG_LIST_ID, id);
            detailIntent.putExtra(ListDetailMembersFragment.ARG_LIST_NAME, name);
            startActivity(detailIntent);
        }
    }
}
