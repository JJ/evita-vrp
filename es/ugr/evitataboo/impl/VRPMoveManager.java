/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ugr.evitataboo.impl;

import ec.app.vrp1.Route;
import ec.app.vrp1.Shop;
import es.ugr.evitataboo.Move;
import es.ugr.evitataboo.MoveManager;
import es.ugr.evitataboo.Solution;
import java.util.ArrayList;


/**
 *
 * @author ferguson
 */
public class VRPMoveManager implements MoveManager {

    public Move[] getAllMoves(Solution solution) {
        VRPSolution vrpsolution = (VRPSolution) solution;
        ArrayList<Move> moves = new ArrayList<Move>();
        
        //For each route of the solution
        int route = 0;
        for(Route r:vrpsolution.getRoutes()){
            //for each shop of the route
            for(int k = 1; k<r.shopsVisited.size()-1; k++){ //Only the shops, not the store
                int shopPosition = k;
                Shop s = r.shopsVisited.get(k);
                
                //We insert that shop in the other routes
                for(int j=0; j<vrpsolution.getRoutes().size();j++){
                    Route r2 = vrpsolution.getRoutes().get(j);
                    for(int i=1; i<r2.shopsVisited.size(); i++){
                        VRPMove m = new VRPMove(s.shopID, j, i);
                        //To avoid create the same neighbour
                        if(!(route == j && (shopPosition == i || shopPosition == (i-1)))){
                            moves.add(m);
                        }else{
                            //System.out.println("NOP");
                        }
                        //System.out.println(m.hashCode());
                    }
                    
                }
            }
            route++;
        }
        Move [] re = moves.toArray(new VRPMove[0]);
        //System.out.println(re.length);
        return re;//(Move[]) moves.toArray(new VRPMove[0]);
    }

}
