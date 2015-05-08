package hack;

import com.vaadin.spring.annotation.SpringComponent;

import java.util.List;

@SpringComponent
public class OmimClient {

    public List<String> getDiseases(String uniprot){
        System.out.println("Getting diseases");
        return null;
    }

}
