package com.tierep.twitterlists.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.tierep.twitterlists.R;

/**
 * A DialogFragment for creating or editing a new Twitter list.
 *
 * Created by pieter on 03/02/15.
 */
public class ManageListDialogFragment extends DialogFragment {

    public static final String ARG_LIST_NAME = "list_name";
    public static final String ARG_LIST_DESCRIPTION = "list_description";
    public static final String ARG_LIST_ISPUBLICLIST = "list_ispubliclist";

    public interface ManageListDialogListener {
        public void onManageListDialogPositiveClick(ManageListModel model);
        public void onManageListDialogNegativeClick(DialogFragment dialog);
    }

    ManageListDialogListener mListener;

    /**
     * Override the Fragment.onAttach() method to instantiate the NewListDialogListener.
     *
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            // Instantiate the NewListDialogListener so we can send events to the host
            mListener = (ManageListDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NewListDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_twitterlist_manage, null);
        final EditText listName = (EditText) view.findViewById(R.id.twitter_list_dialog_new_name);
        final EditText listDescription = (EditText) view.findViewById(R.id.twitter_list_dialog_new_description);
        final RadioButton listPrivacyPublic = (RadioButton) view.findViewById(R.id.twitter_list_dialog_new_privacy_public);
        final RadioButton listPrivacyPrivate = (RadioButton) view.findViewById(R.id.twitter_list_dialog_new_privacy_private);

        int resourceStringIdTitle;
        int resourceStringIdPosBtn;
        Bundle args = getArguments();
        if (args != null && args.containsKey(ARG_LIST_NAME)
                && args.containsKey(ARG_LIST_DESCRIPTION) && args.containsKey(ARG_LIST_ISPUBLICLIST)) {
            listName.setText(args.getString(ARG_LIST_NAME));
            listDescription.setText(args.getString(ARG_LIST_DESCRIPTION));
            listPrivacyPublic.setChecked(args.getBoolean(ARG_LIST_ISPUBLICLIST));
            listPrivacyPrivate.setChecked(!args.getBoolean(ARG_LIST_ISPUBLICLIST));

            resourceStringIdTitle = R.string.twitter_list_dialog_manage_title_edit;
            resourceStringIdPosBtn = R.string.twitter_list_dialog_manage_pos_btn_edit;
        } else {
            resourceStringIdTitle = R.string.twitter_list_dialog_manage_title_create;
            resourceStringIdPosBtn = R.string.twitter_list_dialog_manage_pos_btn_create;
        }

        builder.setTitle(resourceStringIdTitle);
        builder.setView(view);
        builder.setPositiveButton(resourceStringIdPosBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO een list naam moet aan constraints voldoen (validation !!)
                String name = listName.getText().toString();
                String description = listDescription.getText().toString();
                boolean privacyPublic = listPrivacyPublic.isChecked();

                mListener.onManageListDialogPositiveClick(new ManageListModel(name, description, privacyPublic));
            }
        });
        builder.setNegativeButton(R.string.twitter_list_dialog_manage_neg_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mListener.onManageListDialogNegativeClick(ManageListDialogFragment.this);
            }
        });

        return builder.create();
    }

    public class ManageListModel {
        public String name;
        public String description;
        public boolean isPublicList;

        public ManageListModel(String name, String description, boolean isPublicList) {
            this.name = name;
            this.description = description;
            this.isPublicList = isPublicList;
        }
    }

    public static ManageListDialogFragment newInstance(String name, String description, boolean isPublicList) {
        ManageListDialogFragment dialog = new ManageListDialogFragment();

        Bundle args = new Bundle();
        args.putString(ARG_LIST_NAME, name);
        args.putString(ARG_LIST_DESCRIPTION, description);
        args.putBoolean(ARG_LIST_ISPUBLICLIST, isPublicList);
        dialog.setArguments(args);

        return dialog;
    }
}
