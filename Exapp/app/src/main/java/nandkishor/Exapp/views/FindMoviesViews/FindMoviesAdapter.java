package nandkishor.Exapp.views.FindMoviesViews;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import nandkishor.Exapp.R;
import nandkishor.Exapp.R2;
import nandkishor.Exapp.activites.BaseFragmentActivity;
import nandkishor.Exapp.entites.Movie;
import nandkishor.Exapp.services.LiveMoviesServices;
import nandkishor.Exapp.utils.Constants;

public class FindMoviesAdapter extends RecyclerView.Adapter {


    private BaseFragmentActivity mActivity;
    private List<Movie> mMovies;
    private LayoutInflater mInflater;
    private MovieListener mMovieListener;

    private String videoId;


    private HashMap<String,Movie> mFavouriteMovieMap;


    public FindMoviesAdapter(BaseFragmentActivity mActivity, MovieListener mMovieListener) {
        this.mActivity = mActivity;
        this.mMovieListener = mMovieListener;
        mInflater = mActivity.getLayoutInflater();
        mMovies = new ArrayList<>();
        mFavouriteMovieMap = new HashMap<>();
    }

    public void setmMovies(List<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    public void setmFavouriteMoviesMarkedMap(HashMap<String, Movie> friendRequestSentMap) {
        mFavouriteMovieMap.clear();
        mFavouriteMovieMap.putAll(friendRequestSentMap);
        notifyDataSetChanged();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View movieView = mInflater.inflate(R.layout.list_movies,parent,false);

        final FindMoviesViewHolder findMoviesViewHolder = new FindMoviesViewHolder(movieView);
        findMoviesViewHolder.mMovieFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Movie movie  = (Movie) findMoviesViewHolder.itemView.getTag();
                mMovieListener.OnMovieClicked(movie,findMoviesViewHolder.itemView);
            }
        });
        findMoviesViewHolder.radioButtonRatting1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Movie movie = (Movie) findMoviesViewHolder.itemView.getTag();
                mMovieListener.OnRadioButtonClicked(movie,view);
            }
        });
        findMoviesViewHolder.radioButtonRatting2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Movie movie = (Movie) findMoviesViewHolder.itemView.getTag();
                mMovieListener.OnRadioButtonClicked(movie,view);
            }
        });
        findMoviesViewHolder.radioButtonRatting3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Movie movie = (Movie) findMoviesViewHolder.itemView.getTag();
                mMovieListener.OnRadioButtonClicked(movie,view);
            }
        });
        findMoviesViewHolder.radioButtonRatting4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Movie movie = (Movie) findMoviesViewHolder.itemView.getTag();
                mMovieListener.OnRadioButtonClicked(movie,view);
            }
        });



        return findMoviesViewHolder;
    }



    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        try {
            videoId = Constants.extractYoutubeId(mMovies.get(position).getmovieYoutubeLink());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String imageUrl = "http://img.youtube.com/vi/"+videoId+"/0.jpg";

        ((FindMoviesViewHolder) holder).populate(mActivity,mMovies.get(position)
                ,mFavouriteMovieMap,imageUrl);

        ((FindMoviesViewHolder) holder).myouTubePlayerFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final YouTubePlayerFragment youTubePlayerFragment = YouTubePlayerFragment.newInstance();
                mActivity.getFragmentManager().beginTransaction().add(((FindMoviesViewHolder) holder).myouTubePlayerFragment.getId(),youTubePlayerFragment).commit();
                youTubePlayerFragment.initialize(Constants.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                        youTubePlayer.cueVideo(videoId);

                    }

                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }


    public interface MovieListener{
    void OnMovieClicked(Movie movie,View view);

    void OnRadioButtonClicked(Movie movie, View view);
    }

//    https://www.youtube.com/watch?v=9wY_vb4pkLs&feature=youtu.be
}
