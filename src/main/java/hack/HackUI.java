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

    final static String TITLE = "<h1>PPICardio</h1>" +
            "<h2>Protein-Protein Interaction to<br/>Cardiovascular Disease<h2/>";
    public static final String RED_HEART = "<span style=\"color:red\">♥ </span>";
    public static final String RED_HEART_INVISIBLE = "<span style=\"visibility:hidden\">♥ </span>";
    public static final String DEFAULTS = "P38398\nP19429\nQ6UWE0";

    final VerticalLayout rootLayout = new VerticalLayout();
    final Panel mainPagePanel = new Panel();
    final VerticalLayout mainPageLayout = new VerticalLayout();
    final Label titleLabel = new Label(TITLE);
    final TextArea textArea = new TextArea("Enter proteins here");
    final Button button = new Button("Go!");
    final CheckBox phenotypicSeriesCheckbox = new CheckBox("Display phenotypic series");
    final Label infoLabel = new Label("BD2K/NoB Hackathon 2015");

    final VerticalLayout resultPageLayout = new VerticalLayout();
    final Button returnButton = new Button("New query!");
    final Button goToDiseaseTableButton = new Button("Disease to Protein Table");
    final TreeTable resultsTable = new TreeTable("Disease associations");

    final VerticalLayout diseaseTableLayout = new VerticalLayout();
    final Table diseaseTable = new Table("Disease associations");
    final Button returnToMainPageButton = new Button("New query!");
    final Button goToResultsViewButton = new Button("Protein to Disease Table");

    @Autowired
    public OmimClient omimClient;

    @Autowired
    public UniprotClient uniprotClient;

    final Map<String, Protein> proteinMap = new HashMap<>();

    final Set<CVD2Protein> cvd2proteins = new HashSet<>();

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        initMainPage();
        initResultsPage();
        initDiseaseTablePage();

        loadMainPage();
    }

    public void initMainPage(){

        rootLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        rootLayout.addComponent(mainPagePanel);
        rootLayout.setMargin(true);
        rootLayout.setSpacing(true);

        infoLabel.setSizeUndefined();
        rootLayout.addComponent(infoLabel);

        mainPagePanel.setSizeUndefined();
        mainPagePanel.setContent(mainPageLayout);

        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                String proteins = textArea.getValue();
                String[] p = proteins.split("\\r?\\n");
                List<String> proteinUniprots = Arrays.asList(p);

                for (String proteinUniprot : proteinUniprots) {
                    CVD2Protein.sampleSize++;
                    Protein protein = new Protein(proteinUniprot);
                    protein.populateGene(uniprotClient);
                    protein.populateDisease(omimClient);
                    protein.populateInteractingProteins(uniprotClient, omimClient);
                    proteinMap.put(proteinUniprot, protein);
                }
                loadResultsPage();
            }
        });

        textArea.setValue(DEFAULTS);
        textArea.setWidth(300f, Unit.PIXELS);

        titleLabel.setContentMode(ContentMode.HTML);

        mainPageLayout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        mainPageLayout.addComponent(titleLabel);
        mainPageLayout.addComponent(textArea);
        mainPageLayout.addComponent(phenotypicSeriesCheckbox);
        mainPageLayout.addComponent(button);
        mainPageLayout.setSpacing(true);
        mainPageLayout.setMargin(true);
    }

    public void initResultsPage(){
        resultPageLayout.addComponent(new Label("Results page!"));

        resultsTable.addContainerProperty("Uniprot", Label.class, null);
        resultsTable.addContainerProperty("Gene Name", String.class, null);
        resultsTable.addContainerProperty("Associated disease", Label.class, null);
        //resultsTable.addContainerProperty("P-value", String.class, null);
        resultsTable.setHeight(500f, Unit.PIXELS);
        resultsTable.setWidth(1200f, Unit.PIXELS);

        returnButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                loadMainPage();
            }
        });

        goToDiseaseTableButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                loadDiseaseTablePage();
            }
        });

        resultPageLayout.addComponent(resultsTable);
        resultPageLayout.addComponent(returnButton);
        resultPageLayout.addComponent(goToDiseaseTableButton);
        resultPageLayout.setSpacing(true);
        resultPageLayout.setMargin(true);
    }

    public void initDiseaseTablePage(){
        diseaseTableLayout.addComponent(new Label("Disease table page"));
        diseaseTable.addContainerProperty("Cardiovascular Disease", String.class, null);
        diseaseTable.addContainerProperty("Protein(s)", String.class, null);
        diseaseTable.addContainerProperty("P-value", String.class, null);
        diseaseTable.addContainerProperty("Adjusted P-value", String.class, null);

        diseaseTable.setHeight(500f, Unit.PIXELS);
        diseaseTable.setWidth(1200f, Unit.PIXELS);

        goToResultsViewButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                loadResultsPage();
            }
        });

        returnToMainPageButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                loadMainPage();
            }
        });

        diseaseTableLayout.addComponent(diseaseTable);
        diseaseTableLayout.addComponent(returnToMainPageButton);
        diseaseTableLayout.addComponent(goToResultsViewButton);
        diseaseTableLayout.setMargin(true);
        diseaseTableLayout.setSpacing(true);
    }

    public void loadMainPage(){
        proteinMap.clear();
        setContent(rootLayout);
    }

    public void loadDiseaseTablePage(){
        int i = 0;
        for (CVD2Protein protein : cvd2proteins) {
            String adjustedP = "" + protein.calculateAdjustedPValue(cvd2proteins.size());
            String proteinsSet = "";
            for (String s : protein.getProteins()) {
                proteinsSet += s + " ";
            }
            diseaseTable.addItem(new Object[] {protein.getName(), proteinsSet, ""+protein.getpValue(), adjustedP}, i+1);
            i++;
        }
        setContent(diseaseTableLayout);
    }

    public void loadResultsPage(){
        goToDiseaseTableButton.setVisible(phenotypicSeriesCheckbox.getValue());

        int i = 0;
        resultsTable.removeAllItems();
        AccessDiseaseDB.createConnection();

        for (Map.Entry<String, Protein> entry : proteinMap.entrySet()) {
            Protein protein = entry.getValue();
            String diseasesString = "";
            for (OMIM.Disease disease : protein.getCvd()) {
                AccessDiseaseDB.PSDisease psDisease = AccessDiseaseDB.getPSNumber(disease.getId());
                if (psDisease != null) {
                    if (phenotypicSeriesCheckbox.getValue()) {
                        diseasesString += RED_HEART + psDisease.toString() + "<br/>";
                        CVD2Protein diseaseProtein = new CVD2Protein(psDisease.name);
                        diseaseProtein.addProtein(protein.getUniprot());
                        diseaseProtein.calculatePvalue(psDisease.psNumber);
                        cvd2proteins.add(diseaseProtein);
                    }
                    else{
                        diseasesString += RED_HEART + disease.toString() + "<br/>";
                    }
                }
                else {
                    if (phenotypicSeriesCheckbox.getValue()) {
                        diseasesString += "";
                    }
                    else{
                        diseasesString += RED_HEART_INVISIBLE + disease.toString() + "<br/>";
                    }
                }
            }
            Label diseasesLabel = new Label(diseasesString);
            diseasesLabel.setContentMode(ContentMode.HTML);
            Label uniprotLabel = new Label(protein.getUniprot());
            uniprotLabel.setContentMode(ContentMode.HTML);
            Object[] mainProteinCells = {uniprotLabel, protein.getGene(), diseasesLabel};
            Object mainProteinRow = resultsTable.addItem(mainProteinCells, i + 1);
            resultsTable.setCollapsed(mainProteinRow, false);
            i++;
            for (Protein interactingProtein : protein.getInteractingProteins()) {
                String diseasesStringInteracting = "";
                for (OMIM.Disease disease : interactingProtein.getCvd()) {
                    AccessDiseaseDB.PSDisease psDisease = AccessDiseaseDB.getPSNumber(disease.getId());
                    if (psDisease != null) {
                        if (phenotypicSeriesCheckbox.getValue()) {
                            diseasesStringInteracting += RED_HEART + psDisease.toString() + "<br/>";
                            CVD2Protein diseaseProtein = new CVD2Protein(psDisease.name);
                            diseaseProtein.addProtein(protein.getUniprot()+" ("+interactingProtein.getUniprot()+")");
                            diseaseProtein.calculatePvalue(psDisease.psNumber);
                            cvd2proteins.add(diseaseProtein);
                        }
                        else{
                            diseasesStringInteracting += RED_HEART + disease.toString() + "<br/>";
                        }
                    }
                    else {
                        if (phenotypicSeriesCheckbox.getValue()) {
                            diseasesStringInteracting += "";
                        }
                        else{
                            diseasesStringInteracting += RED_HEART_INVISIBLE + disease.toString() + "<br/>";
                        }
                    }
                }
                Label diseaseLabelInteracting = new Label(diseasesStringInteracting);
                diseaseLabelInteracting.setContentMode(ContentMode.HTML);
                Label uniprotLabelInteracting = new Label("\t(" + interactingProtein.getUniprot() + ")");
                uniprotLabelInteracting.setContentMode(ContentMode.HTML);
                Object[] cells = {uniprotLabelInteracting, interactingProtein.getGene(),
                        diseaseLabelInteracting};
                Object interactionProteinRow = resultsTable.addItem(cells, i + 1);
                resultsTable.setParent(interactionProteinRow, mainProteinRow);
                resultsTable.setChildrenAllowed(interactionProteinRow, false);
                resultsTable.setCollapsed(interactionProteinRow, false);
                i++;
            }
        }
        AccessDiseaseDB.closeConnection();
        resultsTable.setPageLength(resultsTable.size());
        setContent(resultPageLayout);
    }

}
