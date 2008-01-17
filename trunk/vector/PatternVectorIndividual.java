package ec.vector;

import ec.EvolutionState;
//import ec.app.itp.ITPdata;
import ec.app.itp.itp;
//import ec.app.vrp.itp;

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
		// Beware of shop 0; it's the warehouse in the shopList but not in the genome
    	for (int i = 0; i<genome.length; i++) {
    		//Get a random index from the list of admissible patterns
    		int j = randomValueFromClosedInterval(0, numPatterns-1, state.random[thread]);
    		// Find out the frequency for that pattern
    		int freq =  p.input.calculateFrequencyForPattern(p.input.admissiblePatterns[j]);
    		// If the frequency is not admissible for that shop, keep repeating
    		while (p.input.shopList.get(i+1).isFrequencyAdmissible(freq) == false){
    			j = randomValueFromClosedInterval(0, numPatterns -1, state.random[thread]); 
    			freq =  p.input.calculateFrequencyForPattern(p.input.admissiblePatterns[j]);
    		}
    		// Assign the pattern to the shop
    		genome[i] = p.input.admissiblePatterns[j];
    		//System.out.print(genome[i] + " ");
    		// Assign the current values of frequency, delivery size
    		// and inventory cost for that shop
    		p.input.shopList.get(i+1).calculateCurrentValues(freq);
    	}
    	//System.out.println();
	}
    
	/** Restricted mutation 
	 * Only admissible pattern values are selected
	 * and they must have a frequency that is admissible for the shop
	 * */
	public void defaultMutate(EvolutionState state, int thread)
    {
//		System.out.printf("defaultMutate- begin ");
       IntegerVectorSpecies s = (IntegerVectorSpecies) species;
       itp p =  (itp) state.evaluator.p_problem;
       int i =0;
       // Pick a shop to mutate
       if (s.mutationProbability>0.0) {
    	   i = randomValueFromClosedInterval(0, (int)genome.length -1, state.random[thread]);
//       }
//		System.out.printf("- middle");
    	//How many admissible patterns are there?
       int numPatterns = p.input.admissiblePatterns.length;
		//Get a random index from the list of admissible patterns
		int j = randomValueFromClosedInterval(0, numPatterns-1, state.random[thread]);
		// Find out the frequency for that pattern
		int freq =  p.input.calculateFrequencyForPattern(p.input.admissiblePatterns[j]);
		// If the frequency is not admissible for that shop, keep repeating
		// Beware of shop 0; it's the warehouse in the shopList but not in the genome
		while (p.input.shopList.get(i+1).isFrequencyAdmissible(freq) == false){
			j = randomValueFromClosedInterval(0, numPatterns -1, state.random[thread]); 
			freq =  p.input.calculateFrequencyForPattern(p.input.admissiblePatterns[j]);
 // 		    System.out.printf(" j= "+j + " Pat= " + p.input.admissiblePatterns[j] +  "(" +freq+")");
  		    //p.input.shopList.get(i).printShopData();
		}
		// Assign the pattern to the shop
		genome[i] = p.input.admissiblePatterns[j];
//		System.out.println("- end");
       }
    }
}
