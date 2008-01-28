/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ugr.evitataboo;

/**
 * This exception is thrown when an incompatible solution is found.
 * @author ferguson
 */
public class IncompatibleSolutionException extends Exception {
    
    public IncompatibleSolutionException(String cause){
        super(cause);
    }

}
