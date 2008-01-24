package ec.app.itp;

import ec.EvolutionState;
import ec.Problem;
import ec.util.Parameter;
import ec.app.vrp1.Shop;

import java.util.ArrayList;

public abstract class VRPSolver extends Problem{
	
	
	
	/**List of shops to visit*/
	public ArrayList <Shop> shops4Today;
	
	/**Solution of the problem*/
	public Routes4ADay bestSolution;
	
	/**The depot ^_^ */
	public Shop Depot;
	
	/**Data of ITPdata*/
	public double speed;
	public double costPerKm;
	public double downloadTime;
	public double vehicleCapacity;
	public double maximumWorkTime;
	public double[][] distanceTable;
	
	
	public VRPSolver(final EvolutionState state, final Parameter base,ITPdata input, ArrayList<Shop> List, int dayOfWeek){
		
		super.setup(state,base);
		
		//We copy the shop list 
		shops4Today = (ArrayList <Shop>) List.clone();
		
		//we create a void solution
		ArrayList <Integer> ListaTiendas = (ArrayList <Integer>) new ArrayList <Integer>();		
		for (int i =0;i<List.size();i++){
			Integer I = Integer.valueOf(List.get(i).shopID);
			ListaTiendas.add(I);
		}	
		
		bestSolution = new Routes4ADay(ListaTiendas, dayOfWeek);
		
		
		//we create a shop for warehouse
		Depot = input.shopList.get(0);
		
		
		speed = input.speed;
		
		costPerKm = input.costPerKm;
		downloadTime = input.downloadTime;
		vehicleCapacity = input.vehicleCapacity;
		maximumWorkTime = input.maximumWorkTime;
		distanceTable = input.distanceTable.clone();
		
		
	}
	
	
	public abstract void findRoutes(final EvolutionState state, final int thread);
	

}
