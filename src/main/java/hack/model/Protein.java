package hack.model;

import hack.OMIM;
import hack.OmimClient;
import hack.PSICQUIC;
import hack.UniprotClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Protein {

    public static final int POOL_SIZE = 40;
    String uniprot;
    String gene;
    List<OMIM.Disease> cvd;
    URL link;
    List<Protein> interactingProteins = new CopyOnWriteArrayList<>();

    public Protein(String uniprot) {
        this.uniprot = uniprot;
    }

    public void populateGene(UniprotClient uniprotClient){
        gene = uniprotClient.mapToGeneSymbol(uniprot);
    }

    public void populateInteractingProteins(final UniprotClient uniprotClient, final OmimClient omimClient){

        final List<String> interactingUniprots = PSICQUIC.getInteractingProteins(uniprot);
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(POOL_SIZE);
        executor.initialize();
        for (final String interactingUniprot : interactingUniprots) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    Protein p = new Protein(interactingUniprot);
                    p.populateGene(uniprotClient);
                    p.populateDisease(omimClient);
                    interactingProteins.add(p);
                }
            };
            executor.execute(r);
//            Protein p = new Protein(interactingUniprot);
//            p.populateGene(uniprotClient);
//            p.populateDisease(omimClient);
//            interactingProteins.add(p);
        }
        for(;;) {
            //System.out.println(executor.getActiveCount());
            if (executor.getActiveCount() == 0) {
                break;
            }
        }
    }

    public void populateDisease(OmimClient omimClient){
        cvd = omimClient.getDiseases(gene);
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

    public List<OMIM.Disease> getCvd() {
        return cvd;
    }

    public void setCvd(List<OMIM.Disease> cvd) {
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
