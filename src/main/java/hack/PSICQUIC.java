package hack;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import hack.model.Protein;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by vincekyi on 5/8/15.
 */
public class PSICQUIC {


    private final static String psicquicURL = "http://www.ebi.ac.uk/Tools/webservices/psicquic/intact/webservices/current/search/query/";


    public static String getJSON(String uri){

        HttpResponse<String> request = null;
        try {
            request = Unirest.get(uri)
                    .asString();
            return request.getBody().toString();

        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getInteractingProteins(String uniprotID){
        Set<String> results = new HashSet<>();

        uniprotID = uniprotID.toUpperCase();
        String[] entries = PSICQUIC.getJSON(psicquicURL+uniprotID).split("\n");
        for(String s: entries){

            try {
                String[] tokens = s.split("\t");

                int firstColon = tokens[0].indexOf(':');
                String firstProtein = tokens[0].substring(firstColon + 1);

                if (!firstProtein.equals(uniprotID)){
                    if (firstProtein.length()<7) {
                        results.add(firstProtein);
                    }
                }

                int secondColon = tokens[1].indexOf(':');
                String secondProtein = tokens[1].substring(secondColon + 1);

                if (!secondProtein.equals(uniprotID)){
                    if (secondProtein.length()<7) {
                        results.add(secondProtein);
                    }
                }
            }
            catch(ArrayIndexOutOfBoundsException ex){
                ex.printStackTrace();
            }
        }

        ArrayList<String> list = new ArrayList<>(results);
        return list;
    }


    public static void main(String args[]){

        for(String s: PSICQUIC.getInteractingProteins("P99999")) {
            System.out.println(s);
        }


    }
}
