package app.com.trethtzer.popularmovies.utilities;

/**
 * Created by Trethtzer on 07/03/2017.
 *
 * Esta clase contiene funciones auxiliares.
 */

public class Utility {

    public static String getAuthor(String s){
        String result = "";
        boolean b = false;
        for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if(c == '-'){
                b = true;
            }else if(c == ';' && b == true){
                return result;
            }else{
                result = result + c;
            }
        }
        return result;
    }
    public static String getUrl(String s){
        String result = "";
        boolean b = false;
        for(int i = 0; i < s.length(); i++){
            char c = s.charAt(i);
            if(c == '-'){
                b = true;
            }else if(c == ';' && b == true){
                result = "";
            }else{
                result = result + c;
                b = false;
            }
        }
        return result;
    }
}
