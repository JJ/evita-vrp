/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ugr.evitataboo.impl;

import ec.app.itp.ITPdata;
import ec.app.vrp1.Route;
import ec.app.vrp1.Shop;
import es.ugr.evitataboo.Move;
import es.ugr.evitataboo.Solution;
import java.util.ArrayList;

/**
 *
 * @author ferguson
 */
public class VRPMove implements Move {

    String shopId;
    int newRoute;
    int newPos;
    int oldPos;
    int oldRoute;
    boolean removed;
    double oldCost;
    
    double firstDistance;
    double secondDistance;

    public VRPMove(String shopId, int newRoute, int newPos) {
        this.shopId = shopId;
        this.newRoute = newRoute;
        this.newPos = newPos;
    }

    public void operateOn(Solution solution) {
        removed = false;
        this.oldCost = solution.getCost();

        ArrayList<Route> routes = ((VRPSolution) solution).getRoutes();

        ITPdata data = VRPSolution.getITPdata();
        Route initialRoute = null;
        Route finalRoute = null;
        Shop theShop = null;
        int beforePre;
        int beforePost = 1;
        int afterPre;
        int afterPost = 1;

        int i = 0;
        int routeId = 0;
        int shopPosition = 0;
        int p = 0;

        //Obtains the shop to move
        for (Route r : routes) {
            p = 0;
            for (Shop s : r.shopsVisited) {

                if (s.shopID.equals(shopId)) {
                    initialRoute = r;
                    theShop = s;
                    routeId = i;
                    this.oldRoute = i;
                    shopPosition = p;
                    this.oldPos = p;
                }
                p++;
            }
            i++;

        }

        //
        
        finalRoute = routes.get(newRoute);
        //System.out.println("AT "+finalRoute+" "+finalRoute.time);
        beforePre = Integer.parseInt(initialRoute.shopsVisited.get(shopPosition - 1).shopID);
        afterPre = Integer.parseInt(initialRoute.shopsVisited.get(shopPosition + 1).shopID);

        
        if (routeId != newRoute) { //The routes are different


            finalRoute.shopsVisited.add(newPos, theShop);
            finalRoute.demand += theShop.currentDeliverySize;

            initialRoute.shopsVisited.remove(theShop);
            initialRoute.demand -= theShop.currentDeliverySize;




        } else { //The routes are the same
            if (newPos < shopPosition) {
                finalRoute.shopsVisited.add(newPos, theShop);
                finalRoute.shopsVisited.remove(shopPosition + 1);

            } else if (newPos > shopPosition) {
                finalRoute.shopsVisited.add(newPos, theShop);
                finalRoute.shopsVisited.remove(shopPosition);

                newPos--;
            }

        }

        //Update the cost

        beforePost = Integer.parseInt(finalRoute.shopsVisited.get(newPos - 1).shopID);
        afterPost = Integer.parseInt(finalRoute.shopsVisited.get(newPos + 1).shopID);

        int shopIdInt = Integer.parseInt(shopId);
        double initialCost = ((VRPSolution) solution).getCost();

         double firstRouteDistance =
                data.distanceTable[beforePre][afterPre] -
                data.distanceTable[shopIdInt][afterPre] - data.distanceTable[beforePre][shopIdInt];

         double secondRouteDistance =
                -data.distanceTable[beforePost][afterPost] + data.distanceTable[shopIdInt][afterPost] + data.distanceTable[beforePost][shopIdInt];

        firstDistance = initialRoute.distanceTravelled;
        secondDistance = finalRoute.distanceTravelled;
        
        initialRoute.distanceTravelled += firstRouteDistance;
        finalRoute.distanceTravelled += secondRouteDistance;
        
        initialRoute.calculateTime(data);
        finalRoute.calculateTime(data);
        

        //Finally we set the cost
        if (finalRoute.demand < data.vehicleCapacity && finalRoute.time < data.maximumWorkTime) {
            ((VRPSolution) solution).setCost(initialCost +
                    firstRouteDistance * data.costPerKm + secondRouteDistance * data.costPerKm);
        } else {
            ((VRPSolution) solution).setCost(Double.MAX_VALUE);
        }
        ///     
        if (initialRoute.shopsVisited.size() == 2) {
            routes.remove(initialRoute);

            
            removed = true;
        }
        
        if (routes.get(0).shopsVisited.size() != 2) {
                Shop theStore = routes.get(0).shopsVisited.get(0);

                Route r = new Route();
                r.shopsVisited.add(theStore);
                r.shopsVisited.add(theStore);

                routes.add(0, r);
            }


    //Habrá que tocar esto? (Creo que no)
        /*if(initialRoute.shopsVisited.size() == 2)
    routes.remove(initialRoute);*/


    }

    @Override
    public String toString() {
        return ("Shop: " + this.shopId + " to route " + this.newRoute + " and pos " + this.newPos);
    }

    public Integer getHash() {

        return new Integer(Integer.parseInt(shopId));
    //return newRoute;
    }

    @Override
    public Object clone() {
        return new VRPMove(shopId, newRoute, newPos);
    }

    public void undo(Solution solution) {
        ArrayList<Route> routes = ((VRPSolution) solution).getRoutes();
        //System.out.println("Pre    " + solution);

        if (this.newRoute == 0) {
            routes.remove(0);
        }
        if (removed == true) {

            Shop theStore = routes.get(0).shopsVisited.get(0);

            Route r = new Route();
            r.shopsVisited.add(theStore);
            r.shopsVisited.add(theStore);


            ((VRPSolution) solution).getRoutes().add(this.oldRoute, r);
        }

        Shop shop = routes.get(this.newRoute).shopsVisited.get(this.newPos);
        routes.get(this.newRoute).shopsVisited.remove(newPos);
        routes.get(this.oldRoute).shopsVisited.add(oldPos, shop);

        solution.setCost(oldCost);
        
        routes.get(this.oldRoute).distanceTravelled = firstDistance;
        
        routes.get(this.newRoute).distanceTravelled = secondDistance;
        
        routes.get(this.oldRoute).calculateTime(VRPSolution.getITPdata());
        routes.get(this.newRoute).calculateTime(VRPSolution.getITPdata());
        
        if(this.newRoute != this.oldRoute){
            routes.get(this.oldRoute).demand += shop.currentDeliverySize;
            routes.get(this.newRoute).demand -= shop.currentDeliverySize;
        }
        //System.out.println("BT "+routes.get(this.newRoute)+" "+routes.get(this.newRoute).distanceTravelled);
       

    }
}
