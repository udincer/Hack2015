package hack;

import org.apache.commons.math3.distribution.HypergeometricDistribution;

/**
 * Created by vincekyi on 5/8/15.
 */
public class HypergeometricCalculator {

    private final static int population = 19000;


    //totalInput is the number of genes from database
    //numProteinsInput is number that user submits
    public static double calculate(int numProteinsInput, int totalInput){

        HypergeometricDistribution h = new HypergeometricDistribution(population, totalInput, numProteinsInput);
        return h.upperCumulativeProbability(totalInput);

    }

    public static void main(String[] args) {
        System.out.println(HypergeometricCalculator.calculate(100, 50));

    }

}
