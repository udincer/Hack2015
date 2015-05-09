package hack;

import hack.model.Protein;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by vincekyi on 5/9/15.
 */
public class CVD2Protein {

    public static int sampleSize = 0;
        private String name;
        private Set<String> proteins;
        private double pValue;
        private double adjustedPValue;

    public CVD2Protein(String name) {
        this.name = name;
        this.proteins = new HashSet<String>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getProteins() {
        return proteins;
    }

    public void addProtein(String protein) {
        this.proteins.add(protein);
    }

    public double getpValue() {
        return pValue;
    }

    public void calculatePvalue(String psNumber){

        AccessDiseaseDB.createConnection();
        int numGenes = AccessDiseaseDB.getNumGenes(psNumber);
        this.pValue = HypergeometricCalculator.calculate(sampleSize, numGenes);

        BigDecimal bd = new BigDecimal(this.pValue);
        bd = bd.round(new MathContext(3));
        this.pValue = bd.doubleValue();
    }

    public double calculateAdjustedPValue(int size) {
        this.adjustedPValue = this.pValue * size;

        BigDecimal bd = new BigDecimal(this.adjustedPValue);
        bd = bd.round(new MathContext(5));
        this.adjustedPValue = bd.doubleValue();

        return this.adjustedPValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CVD2Protein that = (CVD2Protein) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
