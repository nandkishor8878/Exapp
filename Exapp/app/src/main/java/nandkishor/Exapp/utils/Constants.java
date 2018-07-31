package nandkishor.Exapp.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import nandkishor.Exapp.entites.Movie;

public class Constants {

    public static final String IP_LOCAL_HOST = "http://<place your ip here>:3000";

    public static final String USER_INFO_PREFERENCE = "USER_INFO_PREFERENCE";
    public static final String USER_EMAIL = "USER_EMAIL";
    public static final String USER_NAME = "USER_NAME";
    public static final String USER_PICTURE = "USER_PICTURE";


    public static final String FIRE_BASE_PATH_USERS = "users";
    public static final String FIRE_BASE_PATH_USER_TOKEN = "userToken";

    public static final String FIRE_BASE_PATH_MOVIES = "movies";
    public static final String FIRE_BASE_PATH_USER_FAVOURTS = "userFavourites";

    public static final String YOUTUBE_API_KEY = "<Place your youtube API ket here>";


    public static String encodeEmail(String email){
        return email.replace(".",",");
    }

    public static boolean isIncludedInMap(HashMap<String,Movie> userHashMap, Movie movie){
     return userHashMap!=null && userHashMap.size() !=0 &&
             userHashMap.containsKey(movie.getmovieId());
    }

    public static String populateRatting(String ratting){
        switch (ratting) {
            case "1":
                return "Hit";
            case "2":
                return "Average";
            case "3":
                return "Flop";
            case "4":
                return "Waste ! but will Get Box Office collections ";
        }
        return "Not ratted";
    }

    public static String populateLotteryTickets(String tickets){

        if (tickets.equals("00000")){
            return "No any ticket";
        }

        String mTickets = "\n";
        if (tickets.charAt(0)=='1'){
            mTickets += "Send me the Poll Results\n";
        }
        if (tickets.charAt(1)=='2'){
            mTickets += "Yes want to Win ! Lottery ticket to 1st Day 1st show of this movie\n";
        }
        if (tickets.charAt(2)=='3'){
            mTickets += "Yes! Want to Meet Crew\n";
        }
        if (tickets.charAt(3)=='4'){
            mTickets += "Yes! want to meet the actors\n";
        }
        if (tickets.charAt(4)=='5'){
            mTickets += "Yes! I am Film Critic, I want to win The Last Golden Ticket\n";
        }
        return mTickets;
    }

    public static String extractYoutubeId(String url) throws MalformedURLException {
        String query = new URL(url).getQuery();
        String[] param = query.split("&");
        String id = null;
        for (String row : param) {
            String[] param1 = row.split("=");
            if (param1[0].equals("v")) {
                id = param1[1];
            }
        }
        return id;
    }

    public static java.sql.Date convertJavaDateToSqlDate(java.util.Date date) {
        return new java.sql.Date(date.getTime());
    }

}
