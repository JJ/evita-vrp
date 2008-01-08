package ec.app.vrp1;


import java.util.ArrayList;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
//import ec.vector.PermutationVectorIndividual;

public class itp extends Problem implements SimpleProblemForm {
	private static final long serialVersionUID = 1L;
	public ITPdata input;
//	public VRPdata VRPinput;
	public ArrayList<Route> allRoutes;
	public double totalCost;

	public void setup(final EvolutionState state, final Parameter base)
	{
		// very important, remember this
		super.setup(state,base);
	// Load all the problem data;	
		input = new ITPdata();
//		VRPinput = new VRPdata();
		input.setup(state, base);
//		VRPinput.setup(state,base);

	}  
	
	public double calculateInventoryCost(){
		double cost = 0.0;
		
		for (int i = 0; i < input.nShops; i++){
			cost += input.shopList.get(i).currentInventoryCost;
		}
		
		return cost;
	}
	public double calculateTransportCost(){
		double cost = 0.0;
		return cost;
	}
	public double calculateCost(){
		double cost = 0.0;
		
		return cost;
	}
	public void evaluate(EvolutionState state, Individual ind, int threadnum) {
	   	if (ind.evaluated) return;

    	if (!(ind instanceof PatternVectorIndividual))
    		state.output.fatal("Whoa!  It's not a PatternVectorIndividual!!!",null);
        PatternVectorIndividual ind2 = (PatternVectorIndividual)ind;
        
        
        //Instead of this, i should try to minimise the cost
        double myfit = 1/(1- calculateCost());


    	if (!(ind.fitness instanceof SimpleFitness))
    		state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
    	((SimpleFitness)ind.fitness).setFitness(state,
                                    /// ...the fitness...
                                    (float) myfit,
                                    ///... is the individual ideal?  Indicate here...
                                    myfit == 1);
    	ind2.evaluated = true;       
	}

	public void describe(Individual ind, EvolutionState state, int threadnum,
			int log, int verbosity) {
		// TODO Auto-generated method stub

	}

}
