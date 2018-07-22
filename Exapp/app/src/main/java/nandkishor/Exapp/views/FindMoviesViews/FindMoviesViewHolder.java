package nandkishor.Exapp.views.FindMoviesViews;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;

import nandkishor.Exapp.R;
import nandkishor.Exapp.R2;
import nandkishor.Exapp.entites.Movie;
import butterknife.BindView;
import butterknife.ButterKnife;
import nandkishor.Exapp.utils.Constants;

public class FindMoviesViewHolder extends RecyclerView.ViewHolder {


    private long milliSeconds = 0,startTime= 0;

    private CountDownTimer mCountDownTimer;

    @BindView(R2.id.list_movie_moviePlayer)
    public FrameLayout myouTubePlayerFragment;

    @BindView(R2.id.list_movie_movieThumbnail)
    ImageView mMovieThumbnail;

    @BindView(R2.id.list_movie_addFavourite)
    public Button mMovieFavourite;

    @BindView(R2.id.list_movie_movieTitle)
    TextView mMovieTitle;

    @BindView(R2.id.list_movie_countDown)
    public TextView mMovieCountDown;

    @BindView(R2.id.movie_release_date)
    TextView mMovieReleaseDate;

    @BindView(R2.id.radioButtonRatting1)
    public RadioButton radioButtonRatting1;

    @BindView(R2.id.radioButtonRatting2)
    public RadioButton radioButtonRatting2;

    @BindView(R2.id.radioButtonRatting3)
    public RadioButton radioButtonRatting3;

    @BindView(R2.id.radioButtonRatting4)
    public RadioButton radioButtonRatting4;

    @BindView(R2.id.checkBoxLottery1)
    CheckBox checkBoxLottery1;

    @BindView(R2.id.checkBoxLottery2)
    CheckBox checkBoxLottery2;

    @BindView(R2.id.checkBoxLottery3)
    CheckBox checkBoxLottery3;

    @BindView(R2.id.checkBoxLottery4)
    CheckBox checkBoxLottery4;

    @BindView(R2.id.checkBoxLottery5)
    CheckBox checkBoxLottery5;



    public FindMoviesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }


    public void populate(Context context, Movie movie, HashMap<String, Movie> FavouriteMovieMap,String imageUrl) {
        itemView.setTag(movie);
        mMovieTitle.setText(movie.getmovieTitle());
        mMovieReleaseDate.setText(movie.getmovieReleaseDate());

        Picasso.with(context)
                .load(imageUrl)
                .into(mMovieThumbnail);

        if (Constants.isIncludedInMap(FavouriteMovieMap, movie)) {
            mMovieFavourite.setText("Cancel My Tickets");
            mMovieFavourite.setBackgroundResource(R.drawable.favourite_request_decline);

        } else {
            mMovieFavourite.setText("Buy Lottery Tickets");
            mMovieFavourite.setBackgroundResource(R.drawable.favourite_request_background);
        }

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
                mMovieCountDown.setText(finalCountDown);
            }

            @Override
            public void onFinish() {

                mMovieCountDown.setText("Movie has been released!");

            }
        }.start();
    }
}
