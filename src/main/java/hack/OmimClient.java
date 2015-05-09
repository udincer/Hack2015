package hack;

import com.vaadin.spring.annotation.SpringComponent;
import hack.model.Protein;

import java.util.List;

@SpringComponent
public class OmimClient {

    public List<OMIM.Disease> getDiseases(String gene){
        OMIM omim = new OMIM(null);
        System.out.println("Getting diseases");
        List<OMIM.Disease> list = omim.getDiseases(gene);
        System.out.println(list);
        return list;
    }

}
