/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ugr.evitataboo.impl;

import ec.app.itp.ITPdata;
import ec.app.vrp1.Route;
import es.ugr.evitataboo.Move;
import es.ugr.evitataboo.ObjectiveFunction;
import es.ugr.evitataboo.Solution;
import java.util.ArrayList;


/**
 *
 * @author ferguson
 */
public class VRPObjectiveFunction implements ObjectiveFunction{

    private ITPdata itpdata;
    
    public VRPObjectiveFunction( ITPdata data ) 
    {   
        this.itpdata = data;
    }   // end constructor
    
    public double evaluate(Solution solution, Move themove) {
        
        // If move is null, calculate distance from scratch
//       if( themove == null )
//        {
            
           ArrayList<Route> tour = ((VRPSolution)solution).routes;
            double cost = 0.0;
            double time = 0.0;
            
            for(Route r:tour){
                if(r.shopsVisited.size()!=2){
                    
                    double distance = r.calculateDistance(itpdata);
                    
                    cost += r.calculateCost(itpdata);
                    time = r.calculateTime(itpdata);
                    if(r.demand > itpdata.vehicleCapacity 
                            || time>itpdata.maximumWorkTime)
                        return Double.MAX_VALUE;
                    

                }

            }

           
            
            return  cost ;

    }
        

}
