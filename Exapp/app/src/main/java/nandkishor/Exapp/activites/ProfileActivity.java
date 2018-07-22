package nandkishor.Exapp.activites;

import android.support.v4.app.Fragment;

import nandkishor.Exapp.fragments.ProfileFragment;

public class ProfileActivity extends BaseFragmentActivity {
    @Override
    Fragment createFragment() {
        return ProfileFragment.newInstance();
    }
}
