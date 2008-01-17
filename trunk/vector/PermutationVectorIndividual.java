package ec.vector;

import ec.EvolutionState;

public class PermutationVectorIndividual extends IntegerVectorIndividual 
	{
	/** Davis' order crossover, as described in Bäck, Fogel and Michalewicz, 
	 * Evolutionary Computation 1, page 274 */
	public void defaultCrossover(EvolutionState state, int thread, VectorIndividual ind)
	{
       IntegerVectorSpecies s = (IntegerVectorSpecies) species;
       PermutationVectorIndividual i = (PermutationVectorIndividual) ind;

     int point;

       if (genome.length != i.genome.length)
           state.output.fatal("Genome lengths are not the same for fixed-length vector crossover");
       switch(s.crossoverType)
           {
           case VectorSpecies.C_TWO_POINT: 
               int point0 = state.random[thread].nextInt((genome.length / s.chunksize));
               point = state.random[thread].nextInt((genome.length / s.chunksize));
               if (point0 > point) { int p = point0; point0 = point; point = p; }
               
               int xoversection = (point - point0)*s.chunksize +1;
               int[] xoverchunk = new int [xoversection];
               int j = 0;
 //              System.out.print( "Section: " + xoversection + " chunk: ");
               for(int x=point0*s.chunksize; x<=point*s.chunksize;x++)           	
            	   xoverchunk[j++] = genome[x];

               int x1 = point*s.chunksize;
           	   int x2 = x1;          
               for (int x = 0; x < genome.length - xoversection ; x++){
            	   ++x1;
            	   if(x1 >= genome.length) 
            		   x1 = 0;
            	   ++x2;
            	   if(x2 >= genome.length) 
            		   x2 = 0;
            	   int c;
            	   while (true) { 
            		   c = i.genome[x1];
            		   if (isElementInVector(c, xoverchunk) == false)
            			   break;
            		   ++x1;
                   	   if(x1== genome.length) 
                		   x1= 0; 	   
            	   }
            	   genome[x2] = c;
               }
               break;
           }
	}
	
	public boolean isElementInVector(int element, int[] vec ){
		for (int i = 0; i < vec.length; i++){
			if (vec[i]== element)
				return true;
		}
		return false;
	}
	
	/** Swap mutation */
	public void defaultMutate(EvolutionState state, int thread)
    {
       IntegerVectorSpecies s = (IntegerVectorSpecies) species;

       // Swap two genes
       if (s.mutationProbability>0.0) {
    	   int i = randomValueFromClosedInterval(0, (int)genome.length -1, state.random[thread]);
    	   int j = randomValueFromClosedInterval(0, (int)genome.length -1, state.random[thread]);
    	   swap(i,j);
       }
    }
	/** Swap the values of two genes in the chromosome */
   public void swap(int i, int j)  {
	   if ((i < 0) ||(i >= genome.length) || 
			   (j < 0) ||(j >= genome.length) )
		   System.out.println("i =" + i);
	   if (i != j){
		   int temp = genome[i];
		   genome[i] = genome[j];
		   genome[j] = temp;
	   }  
   }  
   

   /** Initializes the individual as a random permutation. */
   public void reset(EvolutionState state, int thread)
       {
	   //First fill in the vector
       for (int i = 0; i<genome.length; i++) 
    	   genome[i] = i;
       //Then shuffle it
       for (int i = 1; i<genome.length-1; i++) {
    	   int j = randomValueFromClosedInterval(0, i, state.random[thread]);
    	   swap(i,j);
       }
       }


}
