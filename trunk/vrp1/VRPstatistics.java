package ec.app.vrp1;

import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleStatistics;
import ec.steadystate.SteadyStateStatisticsForm;
import ec.util.Output;

public class VRPstatistics extends SimpleStatistics implements SteadyStateStatisticsForm
{
	private static final long serialVersionUID = 1L;
    /** Logs the best individual of the run. */
    public void finalStatistics(final EvolutionState state, final int result){
    	
        super.finalStatistics(state,result);
        
        // Print the routes for the best individual 
        
        state.output.println("\nBest Individual of Run:",Output.V_NO_GENERAL,statisticslog);
        
        for(int x=0;x<state.population.subpops.length;x++ )        
        	((vrp)(state.evaluator.p_problem)).describe(best_of_run[x],
        		state,x, statisticslog,Output.V_NO_GENERAL);
    }
        
}
