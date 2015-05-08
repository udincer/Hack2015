package hack;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import hack.model.Protein;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringUI
@Theme("valo")
public class HackUI extends UI {

    final VerticalLayout mainPageLayout = new VerticalLayout();
    final Label label = new Label("BD2K Hackathon 2015!");
    final TextArea textArea = new TextArea("Enter proteins here");
    final Button button = new Button("Go!");

    final VerticalLayout resultPageLayout = new VerticalLayout();
    final Button returnButton = new Button("New query!");
    final Table resultsTable = new Table("Disease associations");

    @Autowired
    public OmimClient omimClient;

    @Autowired
    public UniprotClient uniprotClient;

    final Map<String, Protein> proteinMap = new HashMap<>();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        initMainPage();
        initResultsPage();

        loadMainPage();
    }

    public void initMainPage(){
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                String proteins = textArea.getValue();
                String[] p = proteins.split("\\r?\\n");
                List<String> proteinUniprots = Arrays.asList(p);

                for (String proteinUniprot : proteinUniprots) {
                    String gene = uniprotClient.mapToGeneSymbol(proteinUniprot);
                    System.out.println(gene);
                    Protein protein = new Protein();
                    protein.setUniprot(proteinUniprot);
                    protein.setGene(gene);
                    proteinMap.put(proteinUniprot, protein);
                }
                loadResultsPage();
            }
        });

        mainPageLayout.addComponent(label);
        mainPageLayout.addComponent(textArea);
        mainPageLayout.addComponent(button);
        mainPageLayout.setSpacing(true);
        mainPageLayout.setMargin(true);
    }

    public void initResultsPage(){
        resultPageLayout.addComponent(new Label("Results page!"));

        resultsTable.addContainerProperty("Uniprot", String.class, null);
        resultsTable.addContainerProperty("Gene Name", String.class, null);
        resultsTable.addContainerProperty("Associated disease", String.class, null);
        resultsTable.addContainerProperty("GeneWiki link", String.class, null);

        returnButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                loadMainPage();
            }
        });
        resultPageLayout.addComponent(resultsTable);
        resultPageLayout.addComponent(returnButton);
        resultPageLayout.setSpacing(true);
        resultPageLayout.setMargin(true);
    }

    public void loadMainPage(){
        proteinMap.clear();
        setContent(mainPageLayout);
    }

    public void loadResultsPage(){
        int i = 0;
        resultsTable.removeAllItems();
        for (Map.Entry<String, Protein> entry : proteinMap.entrySet()) {
            Protein protein = entry.getValue();
            resultsTable.addItem(new Object[]{protein.getUniprot(), protein.getGene(), protein.getCvd(),
                    protein.getLink()}, i + 1);
            i++;
            for (Protein interactingProtein : protein.getInteractingProteins()) {
                resultsTable.addItem(new Object[]{interactingProtein.getUniprot(), interactingProtein.getGene(),
                        interactingProtein.getCvd(), interactingProtein.getLink()}, i + 1);
                i++;
            }
        }
        resultsTable.setPageLength(resultsTable.size());
        setContent(resultPageLayout);
    }

}
