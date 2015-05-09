package hack;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vincekyi on 5/8/15.
 */
public class OMIM {
    List<Integer> validDiseases;

    private final String apiKey = "030D6F97830E4C3BB0EB93407A1EC93F66887C80";
    private final String format = "json";
    private final String omimURL = "http://api.omim.org/api/search/geneMap?search=";

    public class Disease{

        public String name = "";
        public int id = 0;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return name + ", OMIM ID: " + id;
        }
    }

    public OMIM(List<Integer> validDiseases){
//        this.validDiseases = new ArrayList<Integer>();
//        this.validDiseases.addAll(validDiseases);
    }

    public List<Integer> getValidDiseases(){
        return this.validDiseases;
    }

    public JSONObject getJSON(String uri){
        uri+="&apiKey="+apiKey+"&format="+format;

        HttpResponse<JsonNode> request = null;
        try {
            request = Unirest.get(uri)
                    .header("accept", "application/json")
                    .asJson();

            return request.getBody().getObject();

        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Disease> getDiseases(String gene){
        ArrayList<Disease> diseases = new ArrayList<Disease>(0);
        if (gene.isEmpty()) {
            return diseases;
        }

        JSONObject json = getJSON(omimURL+gene);

        JSONObject omim = json.getJSONObject("omim");
        JSONObject searchResponse = omim.getJSONObject("searchResponse");
        JSONArray geneMapList = searchResponse.getJSONArray("geneMapList");
        for(int k = 0; k < geneMapList.length(); k++) {
            JSONObject geneMap = geneMapList.getJSONObject(k).getJSONObject("geneMap");
            if (geneMap.has("phenotypeMapList")) {
                JSONArray phenotypeMapList = geneMap.getJSONArray("phenotypeMapList");
                for (int i = 0; i < phenotypeMapList.length(); i++) {
                    JSONObject j = phenotypeMapList.getJSONObject(i);
                    JSONObject phenotypeMap = j.getJSONObject("phenotypeMap");

                    if (validDiseases != null) {
                        if(!phenotypeMap.has("phenotypeMimNumber") || !this.validDiseases.contains(phenotypeMap.getInt("phenotypeMimNumber")))
                            continue;
                    }

                    Disease disease = new Disease();

                    if (phenotypeMap.has("phenotype"))
                        disease.name = phenotypeMap.getString("phenotype");

                    if (phenotypeMap.has("phenotypeMimNumber"))
                        disease.id = phenotypeMap.getInt("phenotypeMimNumber");


                    diseases.add(disease);
                }
            }
        }

        return diseases;
    }

    public static void main(String args[]){

        ArrayList<Integer> arr=new ArrayList<Integer>();
        arr.add(188470);
        arr.add(158350);

        OMIM om = new OMIM(arr);

        for(int i: om.getValidDiseases()){
            System.out.println(i);
        }


        for(Disease d: om.getDiseases("pten")) {
            System.out.println(d.id+" "+d.name);
        }

    }

}
