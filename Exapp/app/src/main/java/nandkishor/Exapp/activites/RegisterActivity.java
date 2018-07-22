package nandkishor.Exapp.activites;

import android.support.v4.app.Fragment;

import nandkishor.Exapp.fragments.RegisterFragment;

public class RegisterActivity extends BaseFragmentActivity {
    @Override
    Fragment createFragment() {
        return RegisterFragment.newInstance();
    }
}
