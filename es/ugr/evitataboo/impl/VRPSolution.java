/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ugr.evitataboo.impl;

import ec.app.itp.ITPdata;
import ec.app.vrp1.Route;
import ec.app.vrp1.Shop;
import es.ugr.evitataboo.IncompatibleSolutionException;
import es.ugr.evitataboo.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ferguson
 */
public class VRPSolution implements Solution {

    ArrayList<Route> routes;
    private int MAX_ITERATIONS = 100000; //To create an initial random solution
    private double cost = -1;

    public VRPSolution() {
        this.routes = new ArrayList<Route>();

    }

    public VRPSolution(VRPSolution copy) {
        this.routes = (ArrayList<Route>) copy.routes.clone();

    }

    @Override
    public Object clone() {
        VRPSolution copy = new VRPSolution();
        //copy.routes = (ArrayList<Route>)this.routes.clone();
        
       /* copy.routes = new ArrayList<Route>();
        for (int i = 0; i < this.routes.size(); i++) {
            Route r = new Route(this.routes.get(i));
            r.shopsVisited.clear();
            for (int j = 0; j < this.routes.get(i).shopsVisited.size(); j++) {
                r.shopsVisited.add(this.routes.get(i).shopsVisited.get(j));
            }
            copy.routes.add(r);
        }*/
        copy.routes = new ArrayList<Route>();
        for (int i = 0; i < this.routes.size(); i++) {
            Route r = new Route(this.routes.get(i));
            r.shopsVisited.clear();
            r.shopsVisited = (ArrayList<Shop>)this.routes.get(i).shopsVisited.clone();
            copy.routes.add(r);
        }    
        return copy;
    }   // end clone

    /**
     * Creates an initial solution 
     * @param data
     */
    public void setAsInitialSolution(Shop theDepot, List<Shop> theShops, ITPdata data) throws IncompatibleSolutionException {
       // Shop theStore = data.shopList.get(0);
        Shop theStore = theDepot;
        boolean solutionInvalid = true;
        for (int i = 1; i < theShops.size(); i++) {
            Shop shop = theShops.get(i);
            Route r = new Route();
            r.shopsVisited.add(theStore);
            r.shopsVisited.add(shop);
            r.shopsVisited.add(theStore);
            this.routes.add(r);
        }
        
        /* List<Shop> theShops = new ArrayList<Shop>();
        for (int i = 1; i < data.shopList.size(); i++) {
        theShops.add(data.shopList.get(i));
        }*/


        ArrayList<Route> newRoutes = new ArrayList<Route>();


        int iterations = 0;
        do {
            this.routes.clear();
            newRoutes.clear();

           for (int i = 0; i < theShops.size(); i++) {
                Route r = new Route();
                newRoutes.add(r);
            }

            solutionInvalid = false;
            //Mejor habría que clonar antes!
            Collections.shuffle(theShops);
            for (Shop s : theShops) {
                int numRoute = (int) (Math.random() * theShops.size());
                newRoutes.get(numRoute).shopsVisited.add(s);
            }

            double cost = 0.0;
            double time = 0.0;
            double outatime = 0.0;
            for (Route r : newRoutes) {
                outatime = 0.0;
                r.shopsVisited.add(0, theStore);
                r.shopsVisited.add(theStore);

                if (r.shopsVisited.size() != 2) {

                    double distance = r.calculateDistance(data);

                    cost += r.calculateCost(data);
                    outatime += r.calculateTime(data);
                    time += outatime;
                    if (r.demand > data.vehicleCapacity || outatime > data.maximumWorkTime) {
                        solutionInvalid = true;
                    }
                    this.routes.add(r);
                }
            }
            
            Route emptyRoute = new Route();
            emptyRoute.shopsVisited.add(theStore);
            emptyRoute.shopsVisited.add(theStore);
            this.routes.add(0, emptyRoute);

            //this.routes = newRoutes;
            
            iterations++;
            
            if (iterations > MAX_ITERATIONS) {
                throw new IncompatibleSolutionException("Could not create an " +
                        "initial solution in " + MAX_ITERATIONS + " iterations");
            }
            
        } while (solutionInvalid);

        //System.out.println("INITIAL: "+this.toString());

    }

    @Override
    public String toString() {
        int i = 0;
        String chain = "";
        for (Route r : this.routes) {
            chain += "[" + (i++) + ": ";
            for (Shop s : r.shopsVisited) {
                chain += s.shopID + " ";
            }
            chain += "]";

        }
        return chain;
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    
    
    
    
}
