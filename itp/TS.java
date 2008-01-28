/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.app.itp;

import ec.EvolutionState;
import ec.app.vrp1.Route;
import ec.app.vrp1.Shop;
import ec.util.Parameter;
import es.ugr.evitataboo.IncompatibleSolutionException;
import es.ugr.evitataboo.TabuSearch;
import es.ugr.evitataboo.impl.VRPMoveManager;
import es.ugr.evitataboo.impl.VRPObjectiveFunction;
import es.ugr.evitataboo.impl.VRPSolution;
import es.ugr.evitataboo.impl.VRPTabuList;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ferguson
 */
public class TS extends VRPSolver {

    private ITPdata data;
    private List<Shop> shops;
    private int thisDay;
    private int MAX_ITERATIONS = 60;
    private int TL_SIZE = 12;

    public TS(EvolutionState state, Parameter base, ITPdata input, ArrayList<Shop> List, int dayOfWeek) {

        super(state, base, input, List, dayOfWeek);
        this.data = input;
        this.shops = List;
        this.thisDay = dayOfWeek;

    }

    @Override
    public void findRoutes(EvolutionState state, int thread) {


        VRPSolution initial = new VRPSolution();
        //this.data.printDistanceTable();
        try {
            initial.setAsInitialSolution(this.Depot, shops, data);
        } catch (IncompatibleSolutionException ex) {
            System.out.println("EXCEPTION Could not find an initial solution with" +
                    "the given data (" + ex.getLocalizedMessage() + ")");
            return;
        }


        //Creates the move manager
        VRPMoveManager mmanager = new VRPMoveManager();
        //Creates the objective function
        VRPObjectiveFunction objFunc = new VRPObjectiveFunction(data);
        //Creates the tabu list
        VRPTabuList tabulist = new VRPTabuList(this.TL_SIZE);

        //Creates the Tabu Search object
        TabuSearch ts = new TabuSearch(initial,
                mmanager,
                objFunc,
                tabulist,
                this.MAX_ITERATIONS);

        //Start solving
        ts.start(); //(VRPSolution)ts.getBestNeighbour(initial);
        //Get the solution
        VRPSolution sol = (VRPSolution) ts.getBestSolution();

        System.out.println("Solution: " + sol.toString() + " with cost " + objFunc.evaluate(sol, null));


        //Construct the bestSolution object
        ArrayList<Integer> integerShops = new ArrayList<Integer>();
        for (Shop s : shops) {
            Integer shopId = new Integer(Integer.parseInt(s.shopID));
            integerShops.add(shopId);
        }

        this.bestSolution = new Routes4ADay(integerShops, this.thisDay);
        this.bestSolution.cost = objFunc.evaluate(sol, null);

        this.bestSolution.routes4Today = new ArrayList<Route>();
        for (Route r : sol.getRoutes()) {
            if (r.shopsVisited.size() > 2) //To avoid insert the routes [0, 0]
            {
                this.bestSolution.routes4Today.add(r);
            }

        }
    }
}
