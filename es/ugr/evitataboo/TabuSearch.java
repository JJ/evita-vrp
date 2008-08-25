/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package es.ugr.evitataboo;





/**
 *
 * @author ferguson
 */
public class TabuSearch {

    Solution initialSolution;
    Solution bestSolution;
    Solution actualSolution;
    Solution bestNeighbour;
    MoveManager moveManager;

    
    ObjectiveFunction objFunc;
    TabuList tabuList;
    private double bestSolutionCost;
    int iterations;
    boolean maximize;
    private int maxIterations;

    public TabuSearch(Solution initialSolution,
            MoveManager moveManager,
            ObjectiveFunction objFunc,
            TabuList tabulist,
            int maxIterations) {
        this.initialSolution = initialSolution;
        this.moveManager = moveManager;
        this.objFunc = objFunc;
        this.tabuList = tabulist;
        this.maxIterations = maxIterations;

    }

    public void start() {
        iterations = 0;
        bestSolution = initialSolution;
        actualSolution = initialSolution;
        actualSolution.setCost(objFunc.evaluate(bestSolution, null));
        double actualSolutionCost = Double.MAX_VALUE;
        double bestNeighbourCost = Double.MAX_VALUE;
        this.bestSolutionCost = Double.MAX_VALUE;

        
        while (hasNotFinished()) {
            //System.out.println("\nITERACION "+iterations);
            //System.out.println("Actual\t"+actualSolutionCost);
            //System.out.println("Best  \t"+bestSolutionCost);
            bestNeighbour = getBestNeighbour(actualSolution);
            
            bestNeighbourCost = bestNeighbour.getCost();
            
            
            
//            if(bestNeighbourCost<actualSolutionCost){
//                actualSolution = (Solution)bestNeighbour.clone();
//                actualSolutionCost = bestNeighbourCost;
//            }
            actualSolution = (Solution)bestNeighbour.clone();
            actualSolutionCost = bestNeighbourCost;
            
            if(((int)actualSolutionCost) < ((int)bestSolutionCost)){
                bestSolution = (Solution) actualSolution.clone();
                bestSolutionCost =  actualSolutionCost;
                iterations = 0;
                //System.out.println("NUEVA MEJOR SOLUCION: "+bestSolutionCost);
            }else{
                //System.out.println("NO MEJOR SOLUCION");
                iterations++;
            }
            //iterations++;
            tabuList.updateTenure();
            //System.out.println("LISTA TABU:"+tabuList);

        }
//        try {
//
//            System.in.read();
//        } catch (IOException ex) {
//            System.out.println("Error");
//        }
    }

    boolean hasNotFinished() {
        if(iterations<this.maxIterations)
            return true;
        else
            return false;

        
    }



    private Solution getBestNeighbour(Solution actual) {
        Move[] moves = moveManager.getAllMoves(actual);
        Move bestNeighbourMove = null;
        Solution theBestNeighbour = (Solution) actual.clone();
        double neighbourCost = Double.MAX_VALUE;
        double theBestNeighbourCost = Double.MAX_VALUE;//objFunc.evaluate(theBestNeighbour, bestNeighbourMove); Esto es lo que he cambiado :/
        boolean moved = false;
        
        Solution neighbour = (Solution) actual.clone();
        for (int i = 0; i < moves.length; i++) {
            Move move = moves[i];
            
            
            //System.out.println("Antes  "+neighbour);
            //System.out.println("Cost: "+neighbour.getCost());
            
            move.operateOn(neighbour);
            
            //System.out.println("Despues"+neighbour);
            //neighbourCost = objFunc.evaluate(neighbour, null);
            //System.out.println("La buena"+neighbourCost);
            neighbourCost = neighbour.getCost();
            //System.out.println("La mala "+neighbourCost);
            //Solution temp = (Solution) neighbour.clone();
            
            //System.out.println("Despues"+temp+"\n");
            
            
            //System.out.println(move);
            boolean isTabu = this.tabuList.isTabu(move);
            if ((int)neighbourCost<(int)bestSolutionCost){ //Aspiration criteria
                isTabu=false;
            }
            if (neighbourCost<theBestNeighbourCost && !isTabu) {
                theBestNeighbour = (Solution)neighbour.clone();
                
                //System.out.println("\tMejor vecino: "+neighbourCost+" al mover el "+ move);
                //System.out.println("\t Ac:"+actual);
                //System.out.println("\t Be:"+theBestNeighbour);
                theBestNeighbourCost = neighbourCost;
                bestNeighbourMove = (Move) move.clone();
                moved = true;
                
            }
            move.undo(neighbour);
            
            //System.out.println("DESPUES"+neighbour);
            //System.out.println("Other "+neighbour.getCost());
            
            
            //System.out.println("El mejor vecino es: "+theBestNeighbour);
        }
        if(moved == true){
                tabuList.addMove(bestNeighbourMove);
                //System.out.println("\tAñadiendo: "+bestNeighbourMove);
        }
        //System.out.println("COSTE DEL MEJOR VECINO: "+theBestNeighbourCost+"\t"+theBestNeighbour);
        //System.out.println("Comprobando:            "+objFunc.evaluate(theBestNeighbour, null));
        return (Solution)theBestNeighbour.clone();


    }
    
    public Solution getBestSolution(){
        return this.bestSolution;
    }
}