/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ugr.evitataboo;

/**
 *
 * @author ferguson
 */
public interface ObjectiveFunction {
    /**
     * Returns the fitness of a solution.
     * @param solution The solution to obtain its fitness
     * @param themove The move that has been performed. If its null calculate 
     * the fitness from scratch, else uses the move data to increase the perfor-
     * mance.
     * @return The fitness of the solution
     */
    public double evaluate(Solution solution, Move themove);

}
