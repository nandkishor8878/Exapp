package nandkishor.Exapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;

import nandkishor.Exapp.R;
import nandkishor.Exapp.R2;
import nandkishor.Exapp.services.LiveMoviesServices;
import nandkishor.Exapp.utils.Constants;
//import nandkishor.Exapp.views.FriendsViewPagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FavouriteFragment extends BaseFragment {
    @BindView(R2.id.bottomBar)
    BottomBar mBottombar;

//    @BindView(R2.id.fragment_friends_tabLayout)
//    TabLayout mTabLayout;

//    @BindView(R2.id.fragment_friends_viewPager)
//    ViewPager mViewpager;

    @BindView(R2.id.fragment_favourite_recyclerView)
    RecyclerView favouriteRecyclerView;

    @BindView(R2.id.fragment_favourite_noResults)
    TextView mTextView;



    private LiveMoviesServices mLiveMovieService;
    private DatabaseReference mAllFavouriteMovieReference;
    private ValueEventListener mAllFavouriteMovieListener;

    private static String mUserEmailString;



    public static FavouriteFragment newInstance(){
        return new FavouriteFragment();
    }

    private Unbinder mUnbinder;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLiveMovieService = LiveMoviesServices.getInstance();
        mUserEmailString = mSharedPreferences.getString(Constants.USER_EMAIL,"");
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourite,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);
        mBottombar.selectTabWithId(R.id.tab_friends);
        setUpBottomBar(mBottombar,2);

//        mUsersNewMessagesReference = FirebaseDatabase.getInstance().getReference()
//                .child(Constants.FIRE_BASE_PATH_USER_NEW_MESSAGES).child(Constants.encodeEmail(mUserEmailString));
//        mUsersNewMessagesListener = mLiveMovieService.getAllNewMessages(mBottombar,R.id.tab_messages);
//
//        mUsersNewMessagesReference.addValueEventListener(mUsersNewMessagesListener);

//        FriendsViewPagerAdapter friendsViewPagerAdapter = new FriendsViewPagerAdapter(getActivity().getSupportFragmentManager());
//        mViewpager.setAdapter(friendsViewPagerAdapter);
//        mTabLayout.setupWithViewPager(mViewpager);

        mAllFavouriteMovieReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIRE_BASE_PATH_USER_FAVOURTS).child(Constants.encodeEmail(mUserEmailString));
//        mAllFavouriteMovieListener = mLiveMovieService.getFriendRequestBottom(mBottombar,R.id.tab_friends);
        mAllFavouriteMovieReference.addValueEventListener(mAllFavouriteMovieListener);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();

        if (mAllFavouriteMovieReference!=null){
            mAllFavouriteMovieReference.removeEventListener(mAllFavouriteMovieListener);
        }

//        if (mUsersNewMessagesListener!=null){
//            mUsersNewMessagesReference.removeEventListener(mUsersNewMessagesListener);
//        }
    }
}
