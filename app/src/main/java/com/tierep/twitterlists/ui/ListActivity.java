package com.tierep.twitterlists.ui;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.tierep.twitterlists.R;
import com.tierep.twitterlists.Session;

import twitter4j.Twitter;
import twitter4j.TwitterException;
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
public class ListActivity extends BaseActivity
        implements ListsFragment.Callbacks, ManageListDialogFragment.ManageListDialogListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_twitterlist_list;
    }

    /**
     * Callback method from {@link ListsFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(UserList userList) {

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable(ListDetailFragment.ARG_USERLIST, userList);
            ListDetailViewPagerFragment fragment = new ListDetailViewPagerFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.twitterlist_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, ListDetailActivity.class);
            detailIntent.putExtra(ListDetailFragment.ARG_USERLIST, userList);
            startActivity(detailIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.twitter_list_new:
                createNewList();
                return true;
            case R.id.log_out:
                logOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewList() {
        DialogFragment dialog = new ManageListDialogFragment();
        dialog.show(getFragmentManager(), "dialog_new_twitter_list");
    }

    private void logOut() {
        Session.getInstance().clearSession(this);
        finish();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onManageListDialogPositiveClick(final ManageListDialogFragment.ManageListModel model) {
        new AsyncTask<Void, Void, UserList>() {
            @Override
            protected UserList doInBackground(Void... params) {
                Twitter twitter = Session.getInstance().getTwitterInstance();
                try {
                    return twitter.createUserList(model.name, model.isPublicList, model.description);
                } catch (TwitterException e) {
                    Log.e("ERROR", "Error while creating new list", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(UserList result) {
                super.onPostExecute(result);
                if (result != null) { // Success
                    ListsFragment listsFragment = (ListsFragment) getFragmentManager().findFragmentById(R.id.twitterlist_list);
                    listsFragment.addUserList(result);
                } else {
                    Toast.makeText(ListActivity.this, getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    @Override
    public void onManageListDialogNegativeClick(DialogFragment dialog) {
        // Do nothing
    }
}
