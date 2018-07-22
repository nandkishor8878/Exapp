package nandkishor.Exapp.services;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.roughike.bottombar.BottomBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nandkishor.Exapp.entites.Movie;
import nandkishor.Exapp.fragments.HomeFragment;
import nandkishor.Exapp.utils.Constants;
import nandkishor.Exapp.views.FindMoviesViews.FindMoviesAdapter;
import nandkishor.Exapp.views.FavouriteMovieViews.FavouriteMovieAdapter;
import io.socket.client.Socket;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class LiveMoviesServices {
    public static LiveMoviesServices mLiveMoviesServices;



    private final int SERVER_SUCCESS = 6;
    private final int SERVER_FAILURE = 7;

    public static LiveMoviesServices getInstance(){
        if (mLiveMoviesServices ==null){
            return new LiveMoviesServices();
        } else{
            return mLiveMoviesServices;
        }
    }


//    get All favourite movies
    public ValueEventListener getAllFavouriteRequests(final FavouriteMovieAdapter adapter, final RecyclerView recyclerView,

                                                      final TextView textView){

        final List<Movie> movies = new ArrayList<>();

        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movies.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Movie movie = snapshot.getValue(Movie.class);
                    movies.add(movie);
                }

                if (movies.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                    adapter.setmMovies(movies);
                    Log.d("Nandkishor", String.valueOf(movies.get(0).getmovieLotteryTicket()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }


    public Subscription approveDeclineFavouriteRequest(final Socket socket, String userEmail, String friendEmail, String requestCode){
        List<String> details = new ArrayList<>();
        details.add(userEmail);
        details.add(friendEmail);
        details.add(requestCode);

        Observable<List<String>> listObservable = Observable.just(details);

        return listObservable
                .subscribeOn(Schedulers.io())
                .map(new Func1<List<String>, Integer>() {
                    @Override
                    public Integer call(List<String> strings) {
                        JSONObject sendData = new JSONObject();

                        try {
                            sendData.put("userEmail",strings.get(0));
                            sendData.put("friendEmail",strings.get(1));
                            sendData.put("requestCode",strings.get(2));
                            socket.emit("friendRequestResponse",sendData);
                            return SERVER_SUCCESS;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return SERVER_FAILURE;
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                    }
                });
    }



    public Subscription addOrRemoveFavourite(final Socket socket, String userEmail, String movieId, String requestCode){
        List<String> details = new ArrayList<>();
        details.add(userEmail);
        details.add(movieId);
        details.add(requestCode);

        Observable<List<String>> listObservable = Observable.just(details);

        return listObservable
                .subscribeOn(Schedulers.io())
                .map(new Func1<List<String>, Integer>() {
                    @Override
                    public Integer call(List<String> strings) {
                        JSONObject sendData = new JSONObject();
                        try {
                            sendData.put("movieId",strings.get(1));
                            sendData.put("userEmail",strings.get(0));
                            sendData.put("requestCode",strings.get(2));
                            socket.emit("userFavourite",sendData);
                            return SERVER_SUCCESS;
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return SERVER_FAILURE;
                        }

                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {

                    }
                });
    }



    public ValueEventListener getFavouriteMoviesMarked(final FindMoviesAdapter adapter, final HomeFragment fragment){
        final HashMap<String,Movie> movieHashMap = new HashMap<>();

        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                movieHashMap.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Movie movie = snapshot.getValue(Movie.class);
                    movieHashMap.put(movie.getmovieId(),movie);
                }

                adapter.setmFavouriteMoviesMarkedMap(movieHashMap);
                fragment.setmFavouriteMoviesMarkedMap(movieHashMap);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }


    public List<Movie> getMatchingMovies(List<Movie> movies, String searchMovie){
        if (searchMovie.isEmpty()){
        return movies;
        }

        List<Movie> moviesFound = new ArrayList<>();

        for (Movie movie : movies) {
                if (movie.getmovieTitle().toLowerCase().startsWith(searchMovie.toLowerCase())) {
                    moviesFound.add(movie);
                }
            }
        return moviesFound;
    }

}
