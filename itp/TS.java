/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package itp;

import ec.EvolutionState;
import ec.app.itp.ITPdata;
import ec.app.itp.VRPSolver;
import ec.app.vrp1.Shop;
import ec.util.Parameter;
import es.ugr.evitataboo.TabuSearch;
import es.ugr.evitataboo.impl.VRPMoveManager;
import es.ugr.evitataboo.impl.VRPObjectiveFunction;
import es.ugr.evitataboo.impl.VRPSolution;
import es.ugr.evitataboo.impl.VRPTabuList;
import java.util.ArrayList;

/**
 *
 * @author ferguson
 */
public class TS extends VRPSolver {
    
    private ITPdata data;
    private int MAX_ITERATIONS = 100;
    private int TL_SIZE = 12;
    
    public TS(EvolutionState state, Parameter base, ITPdata input, ArrayList<Shop> List, int dayOfWeek) {
	
	super(state, base, input, List, dayOfWeek);
        this.data = input;
        
    }
    
    @Override
    public void findRoutes(EvolutionState state, int thread) {
        
        
        
        

        VRPSolution initial = new VRPSolution();
        
        initial.setAsInitialSolution(data);
        
        
        
        VRPMoveManager mmanager = new VRPMoveManager();
        VRPObjectiveFunction objFunc = new VRPObjectiveFunction(data);
        
        VRPTabuList tabulist = new VRPTabuList(this.TL_SIZE);
        
        TabuSearch ts = new TabuSearch(initial, 
                mmanager, 
                objFunc, 
                tabulist,
                this.MAX_ITERATIONS);
        
        ts.start(); //(VRPSolution)ts.getBestNeighbour(initial);
        VRPSolution sol = (VRPSolution) ts.getBestSolution();
        System.out.println("Solution: "+sol.toString()+" with cost "+objFunc.evaluate(sol, null));
    }
    
    

}
