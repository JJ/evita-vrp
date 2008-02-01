/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ugr.evitataboo.impl;

import ec.app.vrp1.Route;
import ec.app.vrp1.Shop;
import es.ugr.evitataboo.Move;
import es.ugr.evitataboo.Solution;
import java.util.ArrayList;


/**
 *
 * @author ferguson
 */
public class VRPMove implements Move{

    String shopId;
    int newRoute;
    int newPos;
    
    public VRPMove(String shopId, int newRoute, int newPos){
        this.shopId = shopId;
        this.newRoute = newRoute;
        this.newPos = newPos;
    }
    
    public void operateOn(Solution solution) {
        ArrayList<Route> routes = ((VRPSolution)solution).getRoutes();
        
        Route initialRoute = null;
        Shop theShop = null;
        int i = 0;
        int routeId = 0;
        int shopPosition = 0;
        int p = 0;
        
        //Obtains the shop to move
        for(Route r:routes){
            p = 0;
            for(Shop s:r.shopsVisited){
                if(s.shopID.equals(shopId)){
                    initialRoute = r;
                    theShop = s;
                    routeId = i;
                    shopPosition = p;
                }
                p++;
            }
            i++;
            
        }
        
        if(routeId!=newRoute){ //The routes are different
            routes.get(newRoute).shopsVisited.add(newPos, theShop);
        
            initialRoute.shopsVisited.remove(theShop);
            
            if(initialRoute.shopsVisited.size()==2)
                routes.remove(initialRoute);
        }else{ //The routes are the same
            if(newPos< shopPosition){
                routes.get(newRoute).shopsVisited.add(newPos,theShop);
                routes.get(newRoute).shopsVisited.remove(shopPosition+1);
            }else if (newPos>shopPosition){
                routes.get(newRoute).shopsVisited.add(newPos,theShop);
                routes.get(newRoute).shopsVisited.remove(shopPosition);
            }
        }
        
        if(routes.get(0).shopsVisited.size()!=2){
            Shop theStore = routes.get(0).shopsVisited.get(0);
            
            Route r = new Route();
            r.shopsVisited.add(theStore);
            r.shopsVisited.add(theStore);
            
            routes.add(0,r);
        }
            
        
        //Habrá que tocar esto? (Creo que no)
        /*if(initialRoute.shopsVisited.size() == 2)
            routes.remove(initialRoute);*/
        
        
    }

    @Override
    public String toString() {
        return ("Shop: "+this.shopId+" to route "+this.newRoute+" and pos "+this.newPos);
    }
    
   public Integer getHash(){
       
        return new Integer(Integer.parseInt(shopId));
       //return newRoute;
    }
   
    @Override
   public Object clone(){
    return new VRPMove(shopId, newRoute, newPos);
   }
    
    

}
