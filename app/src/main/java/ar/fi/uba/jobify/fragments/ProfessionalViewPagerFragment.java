package ar.fi.uba.jobify.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ar.fi.uba.jobify.activities.MyContactsActivity;
import fi.uba.ar.jobify.R;

/**
 * Created by smpiano on 9/28/16.
 */
public class ProfessionalViewPagerFragment extends Fragment {

    private ViewPager viewer;
    private TabLayout tabsLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //inflo la vista de listado de elementos
        View fragmentView = inflater.inflate(R.layout.fragment_professional_pager, container, false);
        viewer = (ViewPager) fragmentView.findViewById(R.id.professional_view_pager);

        //Defino el adapter
        ProfessionalFriendsAdapter adapter = new ProfessionalFriendsAdapter(getFragmentManager());
        //Asocio la listView con el adapter
        viewer.setAdapter(adapter);

        viewer.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                TabLayout tab = ((MyContactsActivity) getActivity()).getTabsLayout();
                tab.getTabAt(position);
                tab.setupWithViewPager(viewer);
            }
        });

        return fragmentView;
    }

    public class ProfessionalFriendsAdapter extends FragmentStatePagerAdapter {

        private final int MY_FRIENDS = 0;
        private ProfessionalListFragment fragment;

        public ProfessionalFriendsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            fragment = new ProfessionalListFragment();
            Bundle args = new Bundle();
            args.putInt("MY_FRIENDS", position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2; // tenemos 2 fragments
        }

        public CharSequence getPageTitle(int position) {
            refresh();
            return getString((position == 0)? R.string.my_contacts_tab_friends : R.string.my_contacts_tab_solicitude);
        }

        public void refresh() {
            fragment.refresh();
        }
    }

}
