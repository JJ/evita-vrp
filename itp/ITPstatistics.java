package ec.app.itp;

import ec.EvolutionState;
import ec.app.itp.itp;
import ec.simple.SimpleStatistics;
import ec.steadystate.SteadyStateStatisticsForm;
import ec.util.Output;

public class ITPstatistics extends SimpleStatistics implements
		SteadyStateStatisticsForm {
	private static final long serialVersionUID = 1L;
    /** Logs the best individual of the run. */
    public void finalStatistics(final EvolutionState state, final int result){
    	
        super.finalStatistics(state,result);
        
        // Print the best individual 
        
        state.output.println("\nBest Individual of Run:",Output.V_NO_GENERAL,statisticslog);
        
        String costline = "Cost: "+ ((itp)(state.evaluator.p_problem)).minCost; 
        for(int x=0;x<state.population.subpops.length;x++ ) {       
        	((itp)(state.evaluator.p_problem)).describe(best_of_run[x],
        		state,x, statisticslog,Output.V_NO_GENERAL);
        	state.output.println(costline,
        			Output.V_NO_GENERAL,statisticslog);
        }
    }
}
