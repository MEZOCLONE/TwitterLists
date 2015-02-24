package com.tierep.twitterlists.ui;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
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
 * An activity representing a single TwitterList detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ListDetailMembersFragment}.
 */
public class ListDetailActivity extends BaseActivity implements DeleteListDialogFragment.DeleteListDialogListener, ManageListDialogFragment.ManageListDialogListener {

    private static final String STATE_USERLIST = "ListDetailActivity_UserList";

    UserList userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userList = (UserList) getIntent().getSerializableExtra(ListDetailFragment.ARG_USERLIST);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(userList.getName());
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putSerializable(ListDetailFragment.ARG_USERLIST,
                    getIntent().getSerializableExtra(ListDetailFragment.ARG_USERLIST));
            ListDetailViewPagerFragment fragment = new ListDetailViewPagerFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .add(R.id.twitterlist_detail_container, fragment)
                    .commit();
        } else {
            userList = (UserList) savedInstanceState.getSerializable(STATE_USERLIST);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_twitterlist_detail;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpTo(this, new Intent(this, ListActivity.class));
                return true;
            case R.id.twitter_list_edit:
                editList();
                return true;
            case R.id.twitter_list_delete:
                deleteList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void editList() {
        DialogFragment dialog = ManageListDialogFragment.newInstance(userList.getName(), userList.getDescription(), userList.isPublic());
        dialog.show(getFragmentManager(), "dialog_edit_twitter_list");
    }

    private void deleteList() {
        DialogFragment dialog = new DeleteListDialogFragment();
        dialog.show(getFragmentManager(), "dialog_delete_twitter_list");
    }

    @Override
    public void onDeleteListDialogPositiveClick(DialogFragment dialog) {
        new AsyncTask<Long, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Long... params) {
                long listId = params[0];
                Twitter twitter = Session.getInstance().getTwitterInstance();
                try {
                    twitter.destroyUserList(listId);
                    return true;
                } catch (TwitterException e) {
                    Log.e("ERROR", "Error during deleting of list", e);
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                super.onPostExecute(result);
                if (result) {
                    NavUtils.navigateUpTo(ListDetailActivity.this, new Intent(ListDetailActivity.this, ListActivity.class));
                } else {
                    Toast.makeText(ListDetailActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        }.execute(userList.getId());
    }

    @Override
    public void onDeleteListDialogNegativeClick(DialogFragment dialog) {
        // Do nothing
    }

    @Override
    public void onManageListDialogPositiveClick(final ManageListDialogFragment.ManageListModel model) {
        new AsyncTask<Void, Void, UserList>() {
            @Override
            protected UserList doInBackground(Void... params) {
                Twitter twitter = Session.getInstance().getTwitterInstance();
                try {
                    // TODO Het UserList object in fragment moet nu ook ge-upate worden, want anders krijgen we bug als we bijv. opnieuw een edit uitvoeren.
                    return twitter.updateUserList(userList.getId(), model.name, model.isPublicList, model.description);
                } catch (TwitterException e) {
                    Log.e("ERROR", "Error during updating list.", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(UserList result) {
                super.onPostExecute(result);
                if (result != null) {
                    getSupportActionBar().setTitle(model.name);
                    userList = result;
                } else {
                    Toast.makeText(ListDetailActivity.this, getString(R.string.text_something_went_wrong), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    @Override
    public void onManageListDialogNegativeClick(DialogFragment dialog) {
        // Do nothing
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_USERLIST, userList);
    }
}
