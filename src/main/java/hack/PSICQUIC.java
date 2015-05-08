package hack;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

/**
 * Created by vincekyi on 5/8/15.
 */
public class PSICQUIC {


    private final static String psicquicURL = "http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/query/";


    public static String getJSON(String uri){

        HttpResponse<String> request = null;
        try {
            request = Unirest.get(uri)
                    .header("accept", "text/plain")
                    .asString();
            return request.getBody().toString();

        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String args[]){

    System.out.println(PSICQUIC.getJSON(psicquicURL+"p99999"));


    }
}
