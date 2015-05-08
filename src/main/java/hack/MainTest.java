package hack;

import hack.model.GeneWikiLink;

/**
 * Created by umut on 5/8/15.
 */
public class MainTest {

    public static void main(String[] args){
        GeneWikiLink link = new GeneWikiLink();
        link.setUniprot("P99999");
        link.populate();
    }
}
