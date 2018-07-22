package nandkishor.Exapp.views.FavouriteMovieViews;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import nandkishor.Exapp.R;
import nandkishor.Exapp.activites.BaseFragmentActivity;
import nandkishor.Exapp.entites.Movie;
import nandkishor.Exapp.utils.Constants;

public class FavouriteMovieAdapter extends RecyclerView.Adapter{

    private BaseFragmentActivity mActivity;
    private LayoutInflater mInflater;
    private List<Movie> mMovies;
    private OnOptionListener mListener;

    private String videoId;

    public FavouriteMovieAdapter(BaseFragmentActivity mActivity, OnOptionListener mListener) {
        this.mActivity = mActivity;
        this.mListener = mListener;
        mInflater = mActivity.getLayoutInflater();
        mMovies = new ArrayList<>();
    }


    public void setmMovies(List<Movie> movies) {
        mMovies.clear();
        mMovies.addAll(movies);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_favourite_movies,parent,false);
        final FavouriteMovieViewHolder favouriteMovieViewHolder = new FavouriteMovieViewHolder(view);

        favouriteMovieViewHolder.removeFavourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Movie movie = (Movie) favouriteMovieViewHolder.itemView.getTag();
                mListener.OnOptionClicked(movie,"0");
            }
        });

        return favouriteMovieViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        try {
            videoId = Constants.extractYoutubeId(mMovies.get(position).getmovieYoutubeLink());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String imageUrl = "http://img.youtube.com/vi/"+videoId+"/0.jpg";

        ((FavouriteMovieViewHolder) holder).populate(mActivity, mMovies.get(position),imageUrl);

        ((FavouriteMovieViewHolder) holder).myouTubeFavouritePlayerFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final YouTubePlayerFragment youTubePlayerFragment = YouTubePlayerFragment.newInstance();
                mActivity.getFragmentManager().beginTransaction().add(((FavouriteMovieViewHolder) holder).myouTubeFavouritePlayerFragment.getId(),youTubePlayerFragment).commit();
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

    public interface OnOptionListener{
        void OnOptionClicked(Movie movie, String result);
    }
}
