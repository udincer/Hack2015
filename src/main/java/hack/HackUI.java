package hack;

import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Created by umut on 5/8/15.
 */

@SpringUI
@Theme("valo")
public class HackUI extends UI {

    public final Label label = new Label("BD2K Hackathon 2015!");
    public final TextArea textArea = new TextArea("Enter proteins here");
    public final Button button = new Button("Go!");
    VerticalLayout layout = new VerticalLayout();

    @Autowired
    public OmimClient omimClient;

    @Autowired
    public UniprotClient uniprotClient;

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                String proteins = textArea.getValue();
                String[] p = proteins.split("\\r?\\n");
                List<String> geneNameList = Arrays.asList(p);

                for (String geneName : geneNameList) {
                    String gene = uniprotClient.mapToGeneSymbol(geneName);
                    Notification.show(gene);
                    System.out.println(gene);
                }
            }
        });

        layout.addComponent(label);
        layout.addComponent(textArea);
        layout.addComponent(button);
        setContent(layout);
    }

}
