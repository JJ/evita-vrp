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
    private int MAX_ITERATIONS = 1000; //To create an initial random solution

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
        copy.routes = new ArrayList<Route>();
        for (int i = 0; i < this.routes.size(); i++) {
            Route r = new Route(this.routes.get(i));
            r.shopsVisited.clear();
            for (int j = 0; j < this.routes.get(i).shopsVisited.size(); j++) {
                r.shopsVisited.add(this.routes.get(i).shopsVisited.get(j));
            }
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
        for (int i = 1; i < data.shopList.size(); i++) {
            Shop shop = data.shopList.get(i);
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
            newRoutes.clear();

            for (int i = 0; i < theShops.size(); i++) {
                Route r = new Route();
                newRoutes.add(r);
            }

            solutionInvalid = false;

            Collections.shuffle(theShops);
            for (Shop s : theShops) {
                int numRoute = (int) (Math.random() * theShops.size());
                newRoutes.get(numRoute).shopsVisited.add(s);
            }

            double cost = 0.0;
            double time = 0.0;
            double outatime = 0.0;
            for (Route r : newRoutes) {
                r.shopsVisited.add(0, theStore);
                r.shopsVisited.add(theStore);

                if (r.shopsVisited.size() != 2) {

                    double distance = r.calculateDistance(data);

                    cost += r.calculateCost(data);
                    outatime += r.calculateTime(data);
                    time += outatime;
                    if (r.demand > data.vehicleCapacity) {
                        solutionInvalid = true;
                    }
                }
            }

            if (time > data.maximumWorkTime) {
                solutionInvalid = true;
            }


            this.routes = newRoutes;
            iterations++;
            
            if (iterations > MAX_ITERATIONS) {
                throw new IncompatibleSolutionException("Could not create an " +
                        "initial solution in " + MAX_ITERATIONS + " iterations");
            }
            
        } while (solutionInvalid);


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
}
