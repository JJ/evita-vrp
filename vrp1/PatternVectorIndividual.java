package ec.app.vrp1;

import ec.EvolutionState;
import ec.vector.IntegerVectorIndividual;
//import ec.app.vrp.itp;
import ec.app.vrp1.itp;

public class PatternVectorIndividual extends IntegerVectorIndividual {
	private static final long serialVersionUID = 1L;
	//¿Para qué carajo sirve esto?
    public static final String P_PATTERNVECTORINDIVIDUAL = "pat-vect-ind";
    
    /** Initializes the individual using only values from the admissible patterns table. */
    public void reset(EvolutionState state, int thread) {
    	
    	if (!(state.evaluator.p_problem instanceof itp))
    		state.output.fatal("Whoa!  It's not an Inventory and Transportation Problem!!!",null);
    	itp p =  (itp) state.evaluator.p_problem;
    	//How many admissible patterns are there?
    	int numPatterns = p.input.admissiblePatterns.length;
    	//Fill in the vector with a pattern for each shop
    	for (int i = 0; i<genome.length; i++) {
    		//Get a random index from the list of admissible patterns
    		int j = randomValueFromClosedInterval(0, numPatterns, state.random[thread]);
    		// Find out the frequency for that pattern
    		int freq =  ITPdata.calculateFrequencyForPattern(p.input.admissiblePatterns[j]);
    		// If the frequency is not admissible for that shop, keep repeating
    		while (p.input.shopList.get(i).isFrequencyAdmissible(freq) == false){
    			j = randomValueFromClosedInterval(0, numPatterns, state.random[thread]);
    			freq =  ITPdata.calculateFrequencyForPattern(p.input.admissiblePatterns[j]);
    		}
    		// Assign the pattern to the shop
    		genome[i] = p.input.admissiblePatterns[j];
    		// Assign the current values of frequency, delivery size
    		// and inventory cost for that shop
    		p.input.shopList.get(i).calculateCurrentValues(freq);
    	}
	}
}
