package hack.model;

import hack.HackUtils;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GeneWikiLink {

    String uniprot;
    String wikiTitle;
    URL link;

    public String getUniprot() {
        return uniprot;
    }

    public void setUniprot(String uniprot) {
        this.uniprot = uniprot;
    }

    public String getWikiTitle() {
        return wikiTitle;
    }

    public void setWikiTitle(String wikiTitle) {
        this.wikiTitle = wikiTitle;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public void populate(){
        Map<String, Integer> map = HackUtils.mapUniprotToAccession(Arrays.asList(uniprot));
        Integer acc = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            acc = entry.getValue();
            System.out.println(acc);
        }
        HackUtils.mapToWiki(""+acc);
    }

}
