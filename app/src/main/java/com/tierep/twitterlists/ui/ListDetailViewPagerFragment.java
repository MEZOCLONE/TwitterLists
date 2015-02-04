package com.tierep.twitterlists.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tierep.twitterlists.R;

import twitter4j.UserList;

/**
 * Created by pieter on 02/02/15.
 */
public class ListDetailViewPagerFragment extends Fragment {

    private UserList userList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null && args.containsKey(ListDetailFragment.ARG_USERLIST)) {
            userList = (UserList) args.getSerializable(ListDetailFragment.ARG_USERLIST);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_twitterlist_detail, container, false);

        ViewPager pager = (ViewPager) view.findViewById(R.id.twitter_list_pager);
        pager.setAdapter(new ListDetailPagerAdapter(getFragmentManager()));

        return view;
    }

    public class ListDetailPagerAdapter extends FragmentPagerAdapter {

        private Fragment fragListMembers;
        private Fragment fragListNonMembers;

        public ListDetailPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * This method may be called by the ViewPager to obtain a title string
         * to describe the specified page. This method may return null
         * indicating no title for this page. The default implementation returns
         * null.
         *
         * @param position The position of the title requested
         * @return A title for the requested page
         */
        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return getActivity().getResources().getString(R.string.list_detail_pager_title_members).toUpperCase();
            } else if (position == 1) {
                return getActivity().getResources().getString(R.string.list_detail_pager_title_nonmembers).toUpperCase();
            } else {
                return "";
            }
        }

        /**
         * Return the Fragment associated with a specified position.
         *
         * @param position
         */
        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (fragListMembers == null) {
                    fragListMembers = ListDetailMembersFragment.newInstance(userList);
                }
                return fragListMembers;
            } else if (position == 1) {
                if (fragListNonMembers == null) {
                    fragListNonMembers = ListDetailNonMembersFragment.newInstance(userList);
                }
                return fragListNonMembers;
            } else {
                return null;
            }
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return 2;
        }
    }
}
