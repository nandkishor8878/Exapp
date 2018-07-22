package nandkishor.Exapp.entites;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Movie {

    private String movieId;
    private String movieTitle;
    private String movieDescription;
    private String movieThumbnail;
    private String movieReleaseDate;
    private String movieYoutubeLink;
    private Map<String,String> movieRatting = new HashMap<>();
    private Map<String,String> movieLotteryTicket = new HashMap<>();

    public Movie(){

    }

    public Movie(String movieId, String movieTitle, String movieDescription, String movieThumbnail,
                 String movieReleaseDate, String movieYoutubeLink, Map<String,String> movieRatting, Map<String,String> movieLotteryTicket) {

        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.movieDescription = movieDescription;
        this.movieThumbnail = movieThumbnail;
        this.movieReleaseDate = movieReleaseDate;
        this.movieYoutubeLink = movieYoutubeLink;
        this.movieRatting =  movieRatting;
        this.movieLotteryTicket = movieLotteryTicket;
    }
//Getters
    public String getmovieId() {
        return movieId;
    }

    public String getmovieTitle() {
        return movieTitle;
    }

    public String getmovieDescription() {
        return movieDescription;
    }

    public String getmovieThumbnail() {
        return movieThumbnail;
    }

    public String getmovieReleaseDate() {
        return movieReleaseDate;
    }

    public String getmovieYoutubeLink() {
        return movieYoutubeLink;
    }

    public Map<String,String> getmovieRatting() {
        return movieRatting;
    }

    public Map<String,String> getmovieLotteryTicket() {
        return movieLotteryTicket;
    }
//Setters
    public void setMovieRatting(Map<String,String> movieRatting) {
        this.movieRatting = movieRatting;
    }

    public void setMovieLotteryTicket(Map<String,String> movieLotteryTicket) {
        this.movieLotteryTicket = movieLotteryTicket;
    }
}
