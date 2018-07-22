package nandkishor.Exapp.activites;

import android.support.v4.app.Fragment;

import nandkishor.Exapp.fragments.HomeFragment;


public class HomeActivity extends BaseFragmentActivity {

    @Override
    Fragment createFragment() {
        return HomeFragment.newInstance();
    }
}
