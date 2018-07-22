package nandkishor.Exapp.views.FavouriteMovieViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import nandkishor.Exapp.R2;
import nandkishor.Exapp.entites.Movie;
import butterknife.BindView;
import butterknife.ButterKnife;
import nandkishor.Exapp.fragments.BaseFragment;
import nandkishor.Exapp.utils.Constants;

public class FavouriteMovieViewHolder extends RecyclerView.ViewHolder {

    private long milliSeconds = 0,startTime= 0;

    private CountDownTimer mCountDownTimer;

    @BindView(R2.id.list_favourite_movie_moviePlayer)
    public FrameLayout myouTubeFavouritePlayerFragment;


    @BindView(R2.id.list_favourite__movie_movieThumbnail)
    ImageView favouriteMovieThumbnail;

    @BindView(R2.id.list_favourite_movie_movieTitle)
    TextView favouriteMovieTitle;

    @BindView(R2.id.list_favourite_movie_removeFavourite)
    public Button removeFavourite;

    @BindView(R2.id.list_favourite_movie_countDown)
    public TextView favouriteMovieCountDown;

    @BindView(R2.id.favourite_movie_release_date)
    TextView favouriteMovieRealeseDate;

    @BindView(R2.id.favourite_movie_ratting)
    TextView favouriteMovieRatting;

    @BindView(R2.id.favourite_movie_lottery)
    TextView favouriteMovieLottery;



    public FavouriteMovieViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    @SuppressLint("SetTextI18n")
    public void populate(Context context, Movie movie,String imageUrl){
        itemView.setTag(movie);

        SharedPreferences sharedPreferences = BaseFragment.getmSharedPreferences();
        String mUserEmail = sharedPreferences.getString(Constants.USER_EMAIL,"");

        favouriteMovieTitle.setText(movie.getmovieTitle());
        favouriteMovieRealeseDate.setText("Release Date: " + movie.getmovieReleaseDate());
        if (movie.getmovieRatting().containsKey(Constants.encodeEmail(mUserEmail))){
            favouriteMovieRatting.setText("Your ratting to this movie: " + Constants.populateRatting(movie.getmovieRatting().get(Constants.encodeEmail(mUserEmail))));
        }
        else {
            favouriteMovieRatting.setText("Not ratted this movie");
        }

        if (movie.getmovieLotteryTicket().containsKey(Constants.encodeEmail(mUserEmail))){
            favouriteMovieLottery.setText(new StringBuilder().append("Your Lotteries: ").append(Constants.populateLotteryTickets(movie.getmovieLotteryTicket().get(Constants.encodeEmail(mUserEmail)))));
        }
        else {
            favouriteMovieLottery.setText("No any Lottery");
        }



        Picasso.with(context)
                .load(imageUrl)
                .into(favouriteMovieThumbnail);


        String ReleaseTime = movie.getmovieReleaseDate();
        ReleaseTime = ReleaseTime.replace('/','.');
        ReleaseTime= ReleaseTime + ", 00:00:00";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss", Locale.getDefault());
        simpleDateFormat.setLenient(false);

        Date endDate;

        try {
            endDate = Constants.convertJavaDateToSqlDate(simpleDateFormat.parse(ReleaseTime));
            milliSeconds = endDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        startTime = System.currentTimeMillis();

        if (mCountDownTimer!=null){
            mCountDownTimer.cancel();
        }
        mCountDownTimer = new CountDownTimer(milliSeconds,1000){

            @Override
            public void onTick(long l) {

                startTime = startTime - 1;

                long serverUptimeSeconds = (l-startTime)/1000;
                String daysLeft = String.format("%d", serverUptimeSeconds / 86400);

                String hoursLeft = String.format("%d", (serverUptimeSeconds % 86400) / 3600);

                String minutesLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60);

                String secondsLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60);

                String finalCountDown = "Count Down: " + daysLeft + "D " + hoursLeft + "H " + minutesLeft + "M " + secondsLeft + "S  Left";
                favouriteMovieCountDown.setText(finalCountDown);
            }

            @Override
            public void onFinish() {

                favouriteMovieCountDown.setText("Movie has been released!");

            }
        }.start();
    }

}
