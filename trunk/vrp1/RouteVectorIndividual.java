package ec.app.vrp1;

import java.util.ArrayList;

import ec.EvolutionState;
import ec.util.Code;
import ec.vector.PermutationVectorIndividual;

public class RouteVectorIndividual extends PermutationVectorIndividual {
	private static final long serialVersionUID = 1L;
	public ArrayList<Route> allRoutes;
	public int pointsToVisit[]; 
	
	  /** Initializes the individual as a random permutation
	   * but using only the values given in pointsToVisit. */
	public void reset(EvolutionState state, int thread)
	{
		//Figure out how to fill this 
		// the genome length must come from the higher level EC
		pointsToVisit = new int[genome.length];
		
	//First fill in the vector
		for (int i = 0; i<genome.length; i++) 
	    	   genome[i] = pointsToVisit[i]; 
	       //Then shuffle it
	       for (int i = 1; i<genome.length-1; i++) {
	    	   int j = randomValueFromClosedInterval(0, i, state.random[thread]);
	    	   swap(i,j);
	       }
	       }
	   
	   public void printIndividualForHumans(final EvolutionState state,
               final int log, 
               final int verbosity)
	   {
		   super.printIndividualForHumans(state, log, verbosity);
	   }
}
