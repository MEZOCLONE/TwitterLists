package com.tierep.twitterlists.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.tierep.twitterlists.R;

/**
 * A DialogFragment for getting confirmation for deleting a Twitter list.
 *
 * Created by pieter on 03/02/15.
 */
public class DeleteListDialogFragment extends DialogFragment {

    public interface DeleteListDialogListener {
        public void onDeleteListDialogPositiveClick(DialogFragment dialog);
        public void onDeleteListDialogNegativeClick(DialogFragment dialog);
    }

    DeleteListDialogListener mListener;

    /**
     * Override the Fragment.onAttach() method to instantiate the DeleteListDialogListener.
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Instantiate the DeleteListDialogListener so we can send events to the host
            mListener = (DeleteListDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DeleteListDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.twitter_list_dialog_delete_msg);
        builder.setPositiveButton(R.string.twitter_list_dialog_delete_pos_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDeleteListDialogPositiveClick(DeleteListDialogFragment.this);
            }
        });
        builder.setNegativeButton(R.string.twitter_list_dialog_delete_neg_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onDeleteListDialogNegativeClick(DeleteListDialogFragment.this);
            }
        });

        return builder.create();
    }
}
