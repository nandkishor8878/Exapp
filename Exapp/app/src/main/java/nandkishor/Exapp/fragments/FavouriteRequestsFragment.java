package nandkishor.Exapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;

import java.net.URISyntaxException;

import nandkishor.Exapp.R;
import nandkishor.Exapp.R2;
import nandkishor.Exapp.activites.BaseFragmentActivity;
import nandkishor.Exapp.entites.Movie;
import nandkishor.Exapp.services.LiveMoviesServices;
import nandkishor.Exapp.utils.Constants;
import nandkishor.Exapp.views.FavouriteMovieViews.FavouriteMovieAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.socket.client.IO;
import io.socket.client.Socket;

public class FavouriteRequestsFragment extends BaseFragment implements FavouriteMovieAdapter.OnOptionListener {


    @BindView(R2.id.bottomBar)
    BottomBar mBottombar;

    @BindView(R2.id.fragment_favourite_recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R2.id.fragment_favourite_noResults)
    TextView mTextView;


    private LiveMoviesServices mLiveMoviesServices;

    private DatabaseReference mGetAllFavouriteMovieRequestsReference;
    private ValueEventListener mGetAllFavouriteMovieRequestsListener;

    private Unbinder mUnbinder;

    private String mUserEmailString;

    private Socket mSocket;


    public static FavouriteRequestsFragment newInstance(){
        return new FavouriteRequestsFragment();
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mSocket = IO.socket(Constants.IP_LOCAL_HOST);
        } catch (URISyntaxException e) {
            Log.i(LoginFragment.class.getSimpleName(),e.getMessage());
            Toast.makeText(getActivity(),"Can't connect to the server",Toast.LENGTH_SHORT).show();
        }

        mSocket.connect();
        mLiveMoviesServices = LiveMoviesServices.getInstance();
        mUserEmailString = mSharedPreferences.getString(Constants.USER_EMAIL,"");
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favourite,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);
        mBottombar.selectTabWithId(R.id.tab_friends);
        setUpBottomBar(mBottombar,2);


        FavouriteMovieAdapter adapter = new FavouriteMovieAdapter((BaseFragmentActivity) getActivity(),this);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mGetAllFavouriteMovieRequestsReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIRE_BASE_PATH_USER_FAVOURTS).child(Constants.encodeEmail(mUserEmailString));

        mGetAllFavouriteMovieRequestsListener = mLiveMoviesServices.getAllFavouriteRequests(adapter,mRecyclerView,mTextView);

        mGetAllFavouriteMovieRequestsReference.addValueEventListener(mGetAllFavouriteMovieRequestsListener);

        mRecyclerView.setAdapter(adapter);

        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();

        if(mGetAllFavouriteMovieRequestsListener !=null){
            mGetAllFavouriteMovieRequestsReference.removeEventListener(mGetAllFavouriteMovieRequestsListener);
        }
    }

    @Override
    public void OnOptionClicked(Movie movie, String result) {
        if (result.equals("0")){
            DatabaseReference favouriteMovieReference = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.FIRE_BASE_PATH_USER_FAVOURTS).child(Constants.encodeEmail(mUserEmailString))
                    .child(movie.getmovieId());
            favouriteMovieReference.setValue(movie);
            mGetAllFavouriteMovieRequestsReference.child(movie.getmovieId())
                    .removeValue();
            mCompositeSubscription.add(mLiveMoviesServices.approveDeclineFavouriteRequest(mSocket,mUserEmailString,
                    movie.getmovieId(),"0"));

            DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIRE_BASE_PATH_MOVIES)
                    .child(movie.getmovieId()).child("movieLotteryTicket").child(Constants.encodeEmail(mUserEmailString));
            mDatabaseReference.removeValue();


        } else{
            mGetAllFavouriteMovieRequestsReference.child(movie.getmovieId())
                    .removeValue();
            mCompositeSubscription.add(mLiveMoviesServices.approveDeclineFavouriteRequest(mSocket,mUserEmailString,
                    movie.getmovieId(),"1"));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
}
