package hack.model;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by umut on 5/8/15.
 */
public class Protein {

    String uniprot;
    String gene;
    String cvd;
    URL link;
    List<Protein> interactingProteins = new ArrayList<>();

    public Protein() {
    }

    public Protein(String uniprot, String gene) {
        this.uniprot = uniprot;
        this.gene = gene;
    }

    public String getUniprot() {
        return uniprot;
    }

    public void setUniprot(String uniprot) {
        this.uniprot = uniprot;
    }

    public String getGene() {
        return gene;
    }

    public void setGene(String gene) {
        this.gene = gene;
    }

    public String getCvd() {
        return cvd;
    }

    public void setCvd(String cvd) {
        this.cvd = cvd;
    }

    public URL getLink() {
        return link;
    }

    public void setLink(URL link) {
        this.link = link;
    }

    public List<Protein> getInteractingProteins() {
        return interactingProteins;
    }

    public void setInteractingProteins(List<Protein> interactingProteins) {
        this.interactingProteins = interactingProteins;
    }

    @Override
    public String toString() {
        return "Protein{" +
                "gene='" + gene + '\'' +
                ", cvd='" + cvd + '\'' +
                ", link=" + link +
                ", interactingProteins=" + interactingProteins +
                '}';
    }
}
