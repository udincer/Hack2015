package hack;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HackUtils {

    private static final String MAPPING_URL = "http://genewiki.sulab.org/map/wiki/";
    private static final String ACC_TO_ENTREZ_MAPPING_URL = "http://www.uniprot.org/mapping/?from=ACC&to=P_ENTREZGENEID&format=tab&query=";

    public static String mapToWiki(String geneId){
        String wikiTitleName = null;
        HttpGet httpGet = new HttpGet(MAPPING_URL + geneId + "/");
        httpGet.setHeader("Cache-Control", "no-cache");
        CloseableHttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            String redirectUrl = response.getFirstHeader("Location").getValue();
            System.out.println(redirectUrl);
            EntityUtils.consume(response.getEntity());
            wikiTitleName = redirectUrl.substring(redirectUrl.lastIndexOf('/')+1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (wikiTitleName.equals("")){
            wikiTitleName = null;
        }

        return wikiTitleName;
    }

    public static Map<String,Integer> mapUniprotToAccession(List<String> uniprots){
        System.out.println("Accessing uniprot");
        String table = null;
        String query = StringUtils.join(uniprots,"+");
        HttpGet httpGet = new HttpGet(ACC_TO_ENTREZ_MAPPING_URL + query);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpResponse response = httpClient.execute(httpGet);
            table = IOUtils.toString(response.getEntity().getContent());
            EntityUtils.consume(response.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(table);

        Map<String,Integer> map = new HashMap<String, Integer>();
        String[] lines = table.split("\n");
        for (String line : lines) {
            String[] entries = line.split("\t");
            String from = entries[0];
            String to = entries[1];
            try {
                map.put(from, Integer.parseInt(to));
            }
            catch(NumberFormatException nfe){
                // we go here at the "from to" line
            }
        }

        return map;
    }
}
