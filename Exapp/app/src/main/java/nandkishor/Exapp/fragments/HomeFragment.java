package nandkishor.Exapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import nandkishor.Exapp.R;
import nandkishor.Exapp.R2;
import nandkishor.Exapp.activites.BaseFragmentActivity;
import nandkishor.Exapp.entites.Movie;
import nandkishor.Exapp.services.LiveMoviesServices;
import nandkishor.Exapp.utils.Constants;
import nandkishor.Exapp.views.FindMoviesViews.FindMoviesAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.socket.client.IO;
import io.socket.client.Socket;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

public class HomeFragment extends BaseFragment implements FindMoviesAdapter.MovieListener {

    @BindView(R2.id.bottomBar)
    BottomBar mBottomBar;
    @BindView(R2.id.fragment_find_friends_searchBar)
    EditText mSearchBarEt;

    @BindView(R2.id.fragment_find_friends_recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R2.id.fragment_find_friends_noResults)
    TextView mTextView;


    private Unbinder mUnbinder;

    private DatabaseReference mGetAllMoviesReference;
    private ValueEventListener mGetAllMoviesListener;


    private DatabaseReference mGetAllFavouriteMoviesMarkedReference;
    private ValueEventListener mGetAllFavouriteMoviesMarkedListener;



    public String mUserEmailString;
    private FindMoviesAdapter mAdapter;


    private List<Movie> mAllMovies;

    private LiveMoviesServices mLiveMoviesService;

    public HashMap<String,Movie> mFavouriteMoviesMarkedMap;


    private Socket mSocket;


    private PublishSubject<String> mSearchBarString;



    public static HomeFragment newInstance(){
        return new HomeFragment();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mSocket = IO.socket(Constants.IP_LOCAL_HOST);
        } catch (URISyntaxException e) {
            Log.i(RegisterFragment.class.getSimpleName(),e.getMessage());
            Toast.makeText(getActivity(),"Can't connect to the server",Toast.LENGTH_SHORT).show();
        }
        mSocket.connect();


        mUserEmailString = mSharedPreferences.getString(Constants.USER_EMAIL,"");
        mLiveMoviesService = LiveMoviesServices.getInstance();
        mFavouriteMoviesMarkedMap = new HashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_movies,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);
        mBottomBar.selectTabWithId(R.id.tab_messages);
        setUpBottomBar(mBottomBar,1);
        mAllMovies = new ArrayList<>();

        mAdapter = new FindMoviesAdapter((BaseFragmentActivity) getActivity(),this);

        mGetAllMoviesListener = getAllMovies(mAdapter);

        mGetAllMoviesReference= FirebaseDatabase.getInstance().getReference().child(Constants.FIRE_BASE_PATH_MOVIES);

        mGetAllMoviesReference.addValueEventListener(mGetAllMoviesListener);

        mGetAllFavouriteMoviesMarkedReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIRE_BASE_PATH_USER_FAVOURTS)
                .child(Constants.encodeEmail(mUserEmailString));
        mGetAllFavouriteMoviesMarkedListener = mLiveMoviesService.getFavouriteMoviesMarked(mAdapter,this);
        mGetAllFavouriteMoviesMarkedReference.addValueEventListener(mGetAllFavouriteMoviesMarkedListener);


        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);


        mCompositeSubscription.add(createSearchBarSubscription());
        listenToSearchBar();

        return rootView;
    }

    private Subscription createSearchBarSubscription(){
        mSearchBarString = PublishSubject.create();

        return mSearchBarString
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, List<Movie>>() {
                    @Override
                    public List<Movie> call(String searchString) {
                        return mLiveMoviesService.getMatchingMovies(mAllMovies,searchString);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Movie>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Movie> movies) {
                        if (movies.isEmpty()){
                            mTextView.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        } else{
                            mTextView.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }
                        mAdapter.setmMovies(movies);
                    }
                });
    }

    private void listenToSearchBar(){
        mSearchBarEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchBarString.onNext(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setmFavouriteMoviesMarkedMap(HashMap<String, Movie> favouriteMoviesMarkedMap) {
        mFavouriteMoviesMarkedMap.clear();
        mFavouriteMoviesMarkedMap.putAll(favouriteMoviesMarkedMap);
    }

    public ValueEventListener getAllMovies(final FindMoviesAdapter adapter){
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mAllMovies.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    final Movie movies = snapshot.getValue(Movie.class);

                    if (!Constants.isIncludedInMap(mFavouriteMoviesMarkedMap,movies)){
                        mAllMovies.add(movies);
                    }
                }
                adapter.setmMovies(mAllMovies);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Can't Load Movie",Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();

        if (mGetAllMoviesListener !=null){
            mGetAllMoviesReference.removeEventListener(mGetAllMoviesListener);
        }

        if (mGetAllFavouriteMoviesMarkedListener!=null){
            mGetAllFavouriteMoviesMarkedReference.removeEventListener(mGetAllFavouriteMoviesMarkedListener);
        }


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }


    @Override
    public void OnMovieClicked(final Movie movie, View view) {
        RelativeLayout relativeLayout = ((RelativeLayout) view);
        StringBuilder lottery = new StringBuilder();
        for (int i = 0; i < relativeLayout.getChildCount();i++){
            View v = relativeLayout.getChildAt(i);
            if(v.getId()==R.id.checkBoxLottery1){
                if(((CheckBox)v).isChecked()){
                    lottery.append("1");
                }
                else {
                    lottery.append("0");

                }
            }
            if(v.getId()==R.id.checkBoxLottery2){
                if(((CheckBox)v).isChecked()){
                    lottery.append("2");
                }
                else {
                    lottery.append("0");
                }
            }
            if(v.getId()==R.id.checkBoxLottery3){
                if(((CheckBox)v).isChecked()){
                    lottery.append("3");
                }
                else {
                    lottery.append("0");
                }
            }
            if(v.getId()==R.id.checkBoxLottery4){
                if(((CheckBox)v).isChecked()){
                    lottery.append("4");
                }
                else {
                    lottery.append("0");
                }
            }
            if(v.getId()==R.id.checkBoxLottery5){
                if(((CheckBox)v).isChecked()){
                    lottery.append("5");
                }
                else {
                    lottery.append("0");
                }
            }
        }
        final String finalLottery = lottery.toString();
        final DatabaseReference lotteryDatabaseReference;
        lotteryDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIRE_BASE_PATH_MOVIES).child(movie.getmovieId()).child("movieLotteryTicket");

        lotteryDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(Constants.encodeEmail(mUserEmailString)).getValue()!=null){
                    Toast.makeText(getActivity(),"Already bought Lottery for this movie ",Toast.LENGTH_SHORT).show();
                }
                else {
                    dataSnapshot.getRef().child(Constants.encodeEmail(mUserEmailString)).setValue(finalLottery);
                    Toast.makeText(getActivity(),"Thanks for buying lottery tickets....",Toast.LENGTH_SHORT).show();
                }

                final DatabaseReference updatedFavouriteMovieReference;
                         updatedFavouriteMovieReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIRE_BASE_PATH_USER_FAVOURTS)
                        .child(Constants.encodeEmail(mUserEmailString));



                if (Constants.isIncludedInMap(mFavouriteMoviesMarkedMap,movie)){
                    updatedFavouriteMovieReference.child(movie.getmovieId())
                            .removeValue();

                 mGetAllMoviesReference.child(movie.getmovieId()).child("movieLotteryTicket").child(Constants.encodeEmail(mUserEmailString)).removeValue();

                    mCompositeSubscription.add(mLiveMoviesService.addOrRemoveFavourite(mSocket,mUserEmailString,
                            movie.getmovieId(),"1"));
                } else{
                    updatedFavouriteMovieReference.child(movie.getmovieId())
                            .setValue(movie);
                    updatedFavouriteMovieReference.child(movie.getmovieId()).child("movieLotteryTicket").child(Constants.encodeEmail(mUserEmailString)).setValue(finalLottery);

                    mCompositeSubscription.add(mLiveMoviesService.addOrRemoveFavourite(mSocket,mUserEmailString,
                            movie.getmovieId(),"0"));
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void OnRadioButtonClicked(Movie movie, final View view) {
        boolean checked = ((RadioButton) view).isChecked();

        final DatabaseReference rattingDatabaseReference;
        rattingDatabaseReference = FirebaseDatabase.getInstance().getReference().child("movies").child(movie.getmovieId()).child("movieRatting");

        String ratting = "0";
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioButtonRatting1:
                if (checked){

                    ratting = "1";

                }
                break;
            case R.id.radioButtonRatting2:
                if (checked){

                    ratting = "2";
                }
                break;
            case R.id.radioButtonRatting3:
                if (checked){

                    ratting = "3";
                }
                break;
            case R.id.radioButtonRatting4:
                if (checked){

                    ratting = "4";
                }
                break;
        }

        final  String finalRatting = ratting;
        rattingDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Nandkishor", String.valueOf(dataSnapshot.child(Constants.encodeEmail(mUserEmailString))));
                if(dataSnapshot.child(Constants.encodeEmail(mUserEmailString)).getValue()!=null){
                    Toast.makeText(getActivity(),"Already ratted this movie...",Toast.LENGTH_SHORT).show();
                }
                else {
                    dataSnapshot.getRef().child(Constants.encodeEmail(mUserEmailString)).setValue(finalRatting);
                    Toast.makeText(getActivity(),"Thanks for ratting....",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(getActivity(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });
    }
}
