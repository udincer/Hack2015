package hack;

import com.vaadin.spring.annotation.SpringComponent;
import org.springframework.web.client.RestTemplate;

import java.util.Scanner;

@SpringComponent
public class UniprotClient {

    public String mapToGeneSymbol(String uniprot){
        String geneSymbol = "";

        // GET protein page from Uniprot
        RestTemplate restTemplate = new RestTemplate();
        String s = restTemplate.getForObject("http://www.uniprot.org/uniprot/"+ uniprot +".txt", String.class);

        // Parse file and return Gene Name
        Scanner scanner = new Scanner(s);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.substring(0,2).equals("GN")) {
                geneSymbol = line.substring(line.indexOf("Name=")+5, line.indexOf(";"));
            }
        }
        scanner.close();

        return geneSymbol;
    }

}
