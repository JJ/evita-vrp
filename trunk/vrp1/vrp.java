/*
 * vrp.java
 * Author: Anna I Esparcia
 * Created: Nov 2007
 */
package ec.app.vrp1;

//import java.io.*;



import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.vector.PermutationVectorIndividual;
//import ec.app.vrp1.VRPdata;
import ec.app.itp.ITPdata;
import ec.util.*;
import java.util.ArrayList;

public class vrp extends Problem implements SimpleProblemForm{
	private static final long serialVersionUID = 1L;
	public ITPdata input;
//	public VRPdata input;
	public ArrayList<Route> allRoutes;
	public double totalCost;

	public void setup(final EvolutionState state, final Parameter base)
	{
		// very important, remember this
		super.setup(state,base);
	// Load all the problem data;	
		input = new ITPdata();
//		input = new VRPdata();
		input.setup(state, base);

	}     
    /**
     * Calculate the routes associated to a chromosome. Each route starts at the warehouse
     * and shops are added while there is time and capacity in the vehicle
     * @param ind
     */	
	public void calculateRoutes(final Individual ind){
		allRoutes = new ArrayList<Route> ();
		Route r = new Route(); 

        PermutationVectorIndividual ind2 = (PermutationVectorIndividual)ind;
        int numStops = 0;
    	int i= 0;
    	double curcapacity = 0.0;
    	double dist = 0.0;
    	double timeSoFar = 0.0;
    	double previousBackTime = 0.0;
       	while (i<ind2.genome.length){  
       		if (numStops == 0){// i.e. it's the beginning of a route
//       		 Calculate distance of first shop to the warehouse
           		dist=input.distanceTable[ind2.genome[i]][0];
           		previousBackTime = dist/input.speed;
           		timeSoFar = previousBackTime + input.downloadTime;
           		// Add warehouse to the route
           		r.shopsVisited.add(input.shopList.get(0)); 	
           		// Add first shop to the route
           		r.shopsVisited.add(input.shopList.get(ind2.genome[i]));
           		curcapacity = input.shopList.get(ind2.genome[i]).currentDeliverySize; 
           		numStops++;
       		}
       		if (i == ind2.genome.length -1) break;
       		// Calculate distance from current to next shop
       		double nextdist = input.distanceTable[ind2.genome[i]][ind2.genome[i+1]];
       		// Calculate distance from next shop back to the warehouse
       		double backdist = input.distanceTable[0][ind2.genome[i+1]];
  			double nextcap = input.shopList.get(ind2.genome[i+1]).currentDeliverySize; 
  			double nextTime = nextdist/input.speed + input.downloadTime;
  			double backTime = backdist /input.speed ;

       		//If time or capacity exceeded, close the route
       		if (( timeSoFar + nextTime + backTime> input.maximumWorkTime) 
       				|| (curcapacity + nextcap > input.vehicleCapacity))
       		{
           		// Add warehouse to the route
           		r.shopsVisited.add(input.shopList.get(0)); 
           		// Add the return distance from shop i
       			r.distanceTravelled = dist + input.distanceTable[ind2.genome[i]][0];
       			r.demand = curcapacity;
       			r.time = timeSoFar + previousBackTime ;
       			r.calculateCost(input);
           		Route rcopy = new Route(r);       		
       			allRoutes.add(rcopy);
       			r.clear();
       			numStops =0;
       			curcapacity = 0.0;
       			dist = 0.0;
       			timeSoFar = 0.0;
       		}
       		else {// add next shop to route
       			r.shopsVisited.add(input.shopList.get(ind2.genome[i+1]));
       			curcapacity += nextcap; 
       			dist += nextdist;
       			timeSoFar += nextTime;
       			previousBackTime = backTime;
       			numStops++;
       		}
       		i++;
       	}
       	if (r.shopsVisited.size()>0 ) {
       		// Add warehouse to close the final route
       		r.shopsVisited.add(input.shopList.get(0));
       		r.distanceTravelled = dist + input.distanceTable[ind2.genome[ind2.genome.length-1]][0];
       		r.demand = curcapacity;
       		//r.calculateDistance(input);
       		r.calculateTime(input);
       		r.calculateCost(input);
       		allRoutes.add(r); //add the last route;
       	}
 //      	printRoutesForChromosome(ind);
	}
	public double calculateTotalCost(){
		totalCost =0.0;
		for (int i= 0; i < allRoutes.size(); i++){
			totalCost += allRoutes.get(i).cost;
		}
		return totalCost;
	}
	
	public void printRoutesForChromosome(final Individual ind){
		//Print the chromosome first
        PermutationVectorIndividual ind2 = (PermutationVectorIndividual)ind;
		System.out.print( "Chrom:  ");    
		for (int j= 0; j<ind2.genome.length; j++){	
			System.out.print(ind2.genome[j]+ " ");
		}
		System.out.println(" Routes: ");
		for (int i= 0; i < allRoutes.size(); i++){
			System.out.print(i + " ");	
			allRoutes.get(i).printRoute();
			System.out.println();
		}
		System.out.println();
	}
	
	public void printRoutesForChromosome(final EvolutionState state,
			final Individual ind, final int log,
            final int verbosity){
		
		//Print the chromosome first
        PermutationVectorIndividual ind2 = (PermutationVectorIndividual)ind;
		state.output.print( "Chrom:  ", verbosity, log);    
		for (int j= 0; j<ind2.genome.length; j++){	
			state.output.print(ind2.genome[j]+ " ", verbosity, log);
		}
		state.output.println(" Routes: ", verbosity, log);
		for (int i= 0; i < allRoutes.size(); i++){
			state.output.print(i + " ", verbosity, log);	
			allRoutes.get(i).printRoute(state, log, verbosity);
			state.output.println(" ", verbosity, log);
		}
		state.output.println(" ", verbosity, log);
	}
	
	
    public void evaluate(final EvolutionState state,
            final Individual ind,
            final int threadnum)
    {
    	if (ind.evaluated) return;

    	if (!(ind instanceof PermutationVectorIndividual))
    		state.output.fatal("Whoa!  It's not a PermutationVectorIndividual!!!",null);
        PermutationVectorIndividual ind2 = (PermutationVectorIndividual)ind;
        
        // la madre del cordero
        calculateRoutes(ind);
        calculateTotalCost();
        //Instead of this, i should try to minimise the cost
        double myfit = 1/(1+totalCost);

        	
/* Toy code for testing
 *  		int sum=0;
 * // the Hamming distance to an ordered permutation
    	for(int x=0; x<ind2.genome.length; x++)
            sum += ((ind2.genome[x] == x+1)? 0 : 1);
    	float myfit = (1 - ((float)sum)/ind2.genome.length);*/

    	if (!(ind.fitness instanceof SimpleFitness))
    		state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
    	((SimpleFitness)ind.fitness).setFitness(state,
                                    /// ...the fitness...
                                    (float) myfit,
                                    ///... is the individual ideal?  Indicate here...
                                    myfit == 1);
    	ind2.evaluated = true;
    }

    public void describe(final Individual ind, 
            final EvolutionState state, 
            final int threadnum, final int log,
            final int verbosity)
    {
    	calculateRoutes(ind);
    	calculateTotalCost();
    	printRoutesForChromosome(state,ind, log, verbosity);
    	String msg = "Total cost: " + Double.toString(totalCost);
    	state.output.println( msg , verbosity, log);
    }
}
