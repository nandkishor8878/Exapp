package nandkishor.Exapp.activites;


import android.support.v4.app.Fragment;

import nandkishor.Exapp.fragments.LoginFragment;

public class LoginActivity extends BaseFragmentActivity {

    @Override
    Fragment createFragment() {
        return LoginFragment.newInstance();
    }
}
