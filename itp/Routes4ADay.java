/*
 * Routes4ADay.java
 *
 * Created on 15 de enero de 2008, 10:10
 *
 */

package ec.app.itp;

import java.util.ArrayList;
import ec.app.vrp1.Route;


/**
 *
 * @author Anais Martinez Garcia
 */
public class Routes4ADay {
    
    /** List of shops to visit this day*/
    public ArrayList <Integer> shops4Today;
    
    /**Identificator for this day*/
    public int dayOfWeek;
    
    /**Set of routes for this day (dayOfWeek)
     * and these shops (shops4Today)*/
    public ArrayList <Route> routes4Today;
    
    
    /**Total cost of routes in allRoutes*/
    public double cost;
    
    
    
    
    /** Creates a new instance of Routes4ADay:
     *      routes4Today = null.
     *      cost = 0.0
     *      dayOfWeek = day
     *      shops4Today = shops
     *
     */
    public Routes4ADay(ArrayList <Integer> shops, int day) {
        
        this.shops4Today = (ArrayList <Integer>) shops.clone();
        
        this.dayOfWeek = day;
        
        this.cost = 0.0;
        
        routes4Today = (ArrayList <Route>)  new ArrayList <Route> ();
        
    }
    
    /**Adds a new route (r) to routes4Today and updates cost*/
    public void addRoute(Route r){
        
        routes4Today.add(r);
        cost +=r.cost;
                       
    }
    
    
    /**Removes a route (r) from routes4Today and updates cost. 
     * If r isn't in routes4Today, it happens nothing*/
    public void deleteRoute(Route r){
        
    	int k = routes4Today.indexOf(r); 
        
    	if (k >=0){
    		routes4Today.remove(r);     
    		cost -= r.cost;
    	}
                       
    }
 
    
    
}
