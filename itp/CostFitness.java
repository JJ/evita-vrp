/*
  Copyright 2008 by Anaís Martínez, Instituto Tecnológico de Informática
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


package ec.app.itp;
import ec.Fitness;
import ec.simple.SimpleFitness;



import ec.EvolutionState;
import ec.util.*;
//import java.io.*;

/*
 * CostFitness.java
 *
 * Created: Thurs Jan 24 2008
 * By: Anaís Martínez
 */

/**
 * A fitness for cost minimisation problems   

 * @author Anaís Martínez
 * @version 1.0
 */

public class CostFitness extends SimpleFitness
    {
	
	private static final long serialVersionUID = 1L;
	
	public Routes4ADay[] bestRoutes;
	public double inventoryCost;
	public double transportCost;
	public double totalCost;

	

    public void setup(final EvolutionState state, Parameter base) 
        {
        super.setup(state,base);  // unnecessary but what the heck
        
        bestRoutes = new Routes4ADay[5];
        inventoryCost = 0.0;
        transportCost = 0.0;
        totalCost = 0.0;
        }


    public boolean equivalentTo(final Fitness _fitness)
        {
    	boolean result=false;
    	
    	CostFitness other = (CostFitness) _fitness;
    	
    	   	
    	if (other.totalCost == this.totalCost )
    		result = true;
    	
        return result;
        }

    public boolean betterThan(final Fitness _fitness)
        {
    	boolean result = true;
    	
    	CostFitness other = (CostFitness) _fitness;
    	if (other.totalCost <= this.totalCost)
    		result = false;
    	    	
        return result;
        }

    }