/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ugr.evitataboo;

/**
 *
 * @author ferguson
 */
public interface MoveManager {

    /**
     * Returns a list of all possible movements from an initial solution. It 
     * includes even the not valid moves (i.e. moves that violate any restriction)
     * @param solution The solution to generate the list of moves
     * @return A list with all possible moves. 
     */
    public Move[] getAllMoves(Solution solution);
}
