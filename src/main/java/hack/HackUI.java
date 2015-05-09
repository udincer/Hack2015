package hack;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import hack.model.Protein;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@SpringUI
@Theme("valo")
public class HackUI extends UI {

    final VerticalLayout mainPageLayout = new VerticalLayout();
    final Label label = new Label("BD2K Hackathon 2015!");
    final TextArea textArea = new TextArea("Enter proteins here");
    final Button button = new Button("Go!");

    final VerticalLayout resultPageLayout = new VerticalLayout();
    final Button returnButton = new Button("New query!");
    final TreeTable resultsTable = new TreeTable("Disease associations");

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
                    Protein protein = new Protein(proteinUniprot);
                    protein.populateGene(uniprotClient);
                    protein.populateDisease(omimClient);
                    protein.populateInteractingProteins(uniprotClient, omimClient);
                    proteinMap.put(proteinUniprot, protein);
                }
                loadResultsPage();
            }
        });

        textArea.setValue("P38398\nP99999\nP60484");

        mainPageLayout.addComponent(label);
        mainPageLayout.addComponent(textArea);
        mainPageLayout.addComponent(button);
        mainPageLayout.setSpacing(true);
        mainPageLayout.setMargin(true);
    }

    public void initResultsPage(){
        resultPageLayout.addComponent(new Label("Results page!"));

        resultsTable.addContainerProperty("Uniprot", Label.class, null);
        resultsTable.addContainerProperty("Gene Name", String.class, null);
        resultsTable.addContainerProperty("Associated disease", Label.class, null);
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

            String diseasesString = "";
            for (OMIM.Disease disease : protein.getCvd()) {
                diseasesString += disease.toString() + "\n";
            }
            Label diseasesLabel = new Label(diseasesString);
            diseasesLabel.setContentMode(ContentMode.PREFORMATTED);
            Label uniprotLabel = new Label(protein.getUniprot());
            uniprotLabel.setContentMode(ContentMode.PREFORMATTED);
            Object[] mainProteinCells = {uniprotLabel, protein.getGene(), diseasesLabel,
                    protein.getLink()};
            Object mainProteinRow = resultsTable.addItem(mainProteinCells, i + 1);
            resultsTable.setCollapsed(mainProteinRow, false);
            i++;
            for (Protein interactingProtein : protein.getInteractingProteins()) {
                String diseasesStringInteracting = "";
                for (OMIM.Disease disease : interactingProtein.getCvd()) {
                    diseasesStringInteracting += disease.toString() + "\n";
                }
                Label diseaseLabelInteracting = new Label(diseasesStringInteracting);
                diseaseLabelInteracting.setContentMode(ContentMode.PREFORMATTED);
                Label uniprotLabelInteracting = new Label("\t(" + interactingProtein.getUniprot() + ")");
                uniprotLabelInteracting.setContentMode(ContentMode.PREFORMATTED);
                Object[] cells = {uniprotLabelInteracting, interactingProtein.getGene(),
                        diseaseLabelInteracting, interactingProtein.getLink()};
                Object interactionProteinRow = resultsTable.addItem(cells, i + 1);
                resultsTable.setParent(interactionProteinRow, mainProteinRow);
                resultsTable.setChildrenAllowed(interactionProteinRow, false);
                resultsTable.setCollapsed(interactionProteinRow, false);
                i++;
            }
        }
        resultsTable.setPageLength(20);
        setContent(resultPageLayout);
    }

}
