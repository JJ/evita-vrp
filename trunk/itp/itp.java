package ec.app.itp;


import java.util.ArrayList;
import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
//import ec.app.vrp1.Route;
import ec.app.vrp1.Shop;
import ec.simple.SimpleFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.PatternVectorIndividual;
//import ec.vector.PermutationVectorIndividual;

public class itp extends Problem implements SimpleProblemForm {
	private static final long serialVersionUID = 1L;
	public ITPdata input;
	public Routes4ADay weeklyRoutes;
	public VRPmethod whichVRPmethod;
	private static double minCost = 2147483647;
	public static final int  MON = 16; 
	public static final int  TUES = 8;
	public static final int  WED = 4;
	public static final int  THURS = 2;
	public static final int  FRI = 1;
	public int WEEK[] = {MON,TUES, WED, THURS, FRI};
	public enum VRPmethod { CWLS, ACO, TS, EC };

	
	public void setup(final EvolutionState state, final Parameter base)
	{
		// very important, remember this
		super.setup(state,base);
	// Load all the problem data;	
		input = new ITPdata();
		input.setup(state, base);
		//Decide which VRP algorithm will be used
		selectVRPalgorithm();
		
	}  
	
	private void selectVRPalgorithm(){
		if (input.whichVRP.equals("CWLS")){
			whichVRPmethod = VRPmethod.CWLS;
//			System.out.printf("CWLS");
		}
		else if (input.whichVRP.equals("ACO")){
			whichVRPmethod = VRPmethod.ACO;
//			System.out.printf("ACO");
		}
		else if (input.whichVRP.equals("TS")){
			whichVRPmethod = VRPmethod.TS;
//			System.out.printf("TS");
		}
		else {//(input.whichVRP.equals("EC"))
			whichVRPmethod = VRPmethod.EC;
//		System.out.printf("Lo que hay:(" + input.whichVRP+ ")");
	}
	}
	
	public double calculateInventoryCost(PatternVectorIndividual ind){
		//System.out.printf("invCost..");
		double cost = 0.0;
		// Beware of shop 0; it's the warehouse in the shopList but not in the genome
		// nShops is including it
		for (int i = 0; i < input.nShops -1; i++){
//			System.out.print("shop " + i + " pat " );
//			System.out.print( ind.genome[i] + " ");
			int curFreq = input.calculateFrequencyForPattern(ind.genome[i]);			
			input.shopList.get(i).calculateCurrentValues(curFreq);
			cost += input.shopList.get(i).currentInventoryCost;
		}
		//System.out.printf("done ");
		return cost;
	}
	public double calculateTransportCost(EvolutionState state,
			PatternVectorIndividual ind, int threadnum){
		double cost = 0.0;
		ArrayList<ArrayList<Shop>> allWeek;
		allWeek = new ArrayList<ArrayList<Shop>>();
		
		for (int i= 0; i < WEEK.length; i++) {
			ArrayList<Shop> shops4Today = new ArrayList<Shop>();
			shops4Today = whichShopsToday(ind, WEEK[i]);
			allWeek.add(shops4Today);
			Parameter base = defaultBase(); //out of the sleeve
			switch (whichVRPmethod){
				case CWLS: 
					CWLS solver =  new CWLS(state, base, input,shops4Today, WEEK[i]);
					solver.findRoutes(state, threadnum);
					break;
				case ACO: 
					break;
				case TS:
					break;
				default:
				//	System.out.printf(".");
					break;
			}
			cost += shops4Today.size();
		}
		
		return cost;
	}
	public double calculateCost(EvolutionState state,PatternVectorIndividual ind,int threadnum){
		double i_cost = calculateInventoryCost(ind);
		double t_cost = calculateTransportCost(state, ind, threadnum);
		//System.out.println("inventory cost "+i_cost + " transport cost= " + t_cost );
		  
		return (i_cost + t_cost);
	}
	
	public double getMinCost(){
		return minCost;
	}
	
	public void evaluate(EvolutionState state, Individual ind, int threadnum) {
	   	if (ind.evaluated) return;

    	if (!(ind instanceof PatternVectorIndividual))
    		state.output.fatal("Whoa!  It's not a PatternVectorIndividual!!!",null);
        PatternVectorIndividual ind2 = (PatternVectorIndividual)ind;
        
        
        //Instead of this, i should try to minimise the cost
        double cost = calculateCost(state, ind2, threadnum);
        if (cost < minCost) minCost = cost;
        double myfit = 1/(1+ cost);


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
		System.out.printf("Minimum Cost " + minCost);
	}

	/**
	 * Make a list of the shops that are visited on a given day of the week
	 * MON = 16, TUES = 8, WED = 4, THURS = 2, FRI = 1
	 * Remember that shop 0 is the warehouse
	 * @param ind
	 * @param day
	 * @return
	 */
	public ArrayList<Shop> whichShopsToday(Individual ind, int day){
		ArrayList<Shop> shops4Today = new ArrayList<Shop>();
		PatternVectorIndividual ind2 = (PatternVectorIndividual)ind;
		
		int pattern = 0;
//		System.out.printf("Shops for day "+ day +" are: ");
		for (int i = 0; i < ind2.genomeLength(); i++){
			pattern = ind2.genome[i];
			if ((pattern & day)!= 0){
				Shop s = new Shop();
				s = input.shopList.get(i+1); // Reminder: shop 0 is the warehouse
				shops4Today.add(s);
				//shops4Today.add(i+1);
//				System.out.printf(" " + (i+1) +" ("+ pattern+ ") ");
			}
		}
//		System.out.println();
		return shops4Today;
	}
}
