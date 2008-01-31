package ec.app.itp;

import java.util.ArrayList;

import ec.EvolutionState;
import ec.app.vrp1.Shop;
import ec.app.vrp1.Route;
import ec.util.Parameter;


public class ACO extends VRPSolver {

	
	private static final long serialVersionUID = 1L;
	
	
	/**Number of iterations of algorithm*/
    private int iteraciones;

    /**Number of ants*/
    private int nhormigas;

    /**Matrix of pheromones*/
    private double [][] feromonas;
    private int tamferom;

    /**Initial value for pheromone*/
    private double tau;

    /**Parameters of algorithm
     *<p> parametros[0] = alpha
     *<p> parametros[1] = beta
     *<p> parametros[2] = epsilon
     *<p> parametros[3] = rho
     *<p> parametros[4] = omega
     *<p> parametros[5] = theta
     */
    private double[]parametros;
    
    /**Value for the size of neighborhood 
     * (factor * final number of routes in solution
     */
    private int factor;


    
    /*Solutions and costs of last 10 iterations*/
    /*----------------------------------------*/	

    /**Best solutions for last 10 iterations*/
    private ArrayList <Integer>[] mejores_soluciones;

    /**Costs of Best solutions for last 10 iterations*/
    private double [] mejores_costes;

    /**Initial position in solutions*/
    private int sinic;

    /**Final position in solutions*/
    private int sfin;

    /*Best solution of execution*/
    /*--------------------------*/
    /**Costs of Best solution of execution*/
    private double mejor_coste_ejecucion;
    private ArrayList <Integer> mejor_solucion_ejecucion;

    
    private ArrayList <Integer> tiendaLista;
    private ArrayList <Integer> posicionLista;
    
    private int contador_iguales;

	
	
	public ACO(EvolutionState state, Parameter base, ITPdata input, ArrayList<Shop> List, int dayOfWeek) {
		
		super(state, base, input, List, dayOfWeek);
		
		
		iteraciones = 200;
		nhormigas = 25;
		tau = 0.5;

		feromonas= new double[input.nShops][input.nShops];
		
		tamferom = input.nShops;
		
		
		
		if (modulo(input.nShops,2)==0){
			for (int i =0;i<input.nShops;i+=2){
				for (int j =0;j<input.nShops;j+=2){
					feromonas[i][j] = tau;
					feromonas[i][j+1] = tau;					
				}

				for (int j=0;j<input.nShops;j+=2){
					feromonas[i+1][j] = tau;
					feromonas[i+1][j+1] = tau;
				}
			}
		}
		else{
			for (int i =0;i<input.nShops-1;i+=2){
				for (int j =0;j<input.nShops-1;j+=2){
					feromonas[i][j] = tau;
					feromonas[i][j+1] = tau;					
				}

				for (int j=0;j<input.nShops-1;j+=2){
					feromonas[i+1][j] = tau;
					feromonas[i+1][j+1] = tau;
				}
			}
			
			for (int j =0;j<input.nShops-1;j+=2){
				feromonas[input.nShops-1][j] = tau;
				feromonas[input.nShops-1][j+1] = tau;					
			}

			for (int j=0;j<input.nShops-1;j+=2){
				feromonas[input.nShops-1][j] = tau;
				feromonas[input.nShops-1][j+1] = tau;
			}
			
			feromonas[input.nShops-1][input.nShops-1] = tau;				
				
		}
			
			

	    parametros =  new double[6];
        parametros[0] = 0.2;//alpha
        parametros[1] = 0.8;//beta
        parametros[2] = 0.8;//epsilon
        parametros[3] = 0.2;//rho
        parametros[4] = 0.5;//omega
        parametros[5] = 0.5;//theta
		
		factor = 6;
		 
		mejores_soluciones = new ArrayList[10];
		for (int i =0;i<10;i+=2){
            mejores_soluciones[i] = (ArrayList <Integer>) new ArrayList<Integer>();
            mejores_soluciones[i+1] = (ArrayList <Integer>) new ArrayList<Integer>();
		}

		mejores_costes = new double [10];
        sinic = 0;
        sfin = 0;

        mejor_coste_ejecucion = 0.0;
        mejor_solucion_ejecucion = (ArrayList <Integer>) new ArrayList <Integer> ();
        

        tiendaLista = (ArrayList <Integer>) new ArrayList<Integer>();
        posicionLista = (ArrayList <Integer>) new ArrayList<Integer>();
        
        for (int i =0;i<List.size();i++){        	
        	Integer s = Integer.valueOf(this.shops4Today.get(i).shopID);
        	tiendaLista.add(s);
        	posicionLista.add(i);
        	
        }

		contador_iguales = 0;
		
		
	}

	
	public void findRoutes(EvolutionState state, int thread) {
		
		//Cost of solution of each ant in each iteration
		
        double [] coste_temp = new double[nhormigas];


        //shops not visited yet
        ArrayList [] posicionRestantes = new ArrayList [nhormigas];
        ArrayList [] tiendaRestantes = new ArrayList [nhormigas];

        //shops visited in this moment
        ArrayList [] posicionYaVisitadas = new ArrayList [nhormigas];
        ArrayList [] tiendaYaVisitadas = new ArrayList [nhormigas];


        
        //First iteration
        //------------------
        
        //1. Each ant inserts warehouse		
        for (int h=0;h<nhormigas;h++){
        	posicionRestantes[h]=(ArrayList <Integer>) posicionLista.clone();
            tiendaRestantes[h]=(ArrayList <Integer>) tiendaLista.clone();
        	posicionYaVisitadas[h]=(ArrayList <Integer>) new ArrayList<Integer>();
        	posicionYaVisitadas[h].add(new Integer(-1));
        	
        	tiendaYaVisitadas[h]=(ArrayList <Integer>) new ArrayList<Integer>();
        	tiendaYaVisitadas[h].add(new Integer(0));
        }
        
        //2. Each ant makes a solution        
        
        for (int h=0;h<nhormigas;h++){
            double material = vehicleCapacity;
            double tiempo = maximumWorkTime;
            
            int actual = 0;
            int siguiente = 0;
            
            int t = 0;
            
            int ntiendas = tiendaLista.size();
            while (t<ntiendas){
                //Next shop (position in arrays posicionRestantes y tiendaRestantes)
                //or -1 if warehouse
                siguiente = transicion(state,thread,actual,posicionRestantes[h],tiendaRestantes[h],tiempo,material);
                
                if (siguiente == -1){
                	tiendaYaVisitadas[h].add(new Integer(0));
                	posicionYaVisitadas[h].add(new Integer(-1));
                	
                    //New route     
                    material = vehicleCapacity;
                    tiempo = maximumWorkTime;
                    siguiente = 0;
                    
                }
                else{
                     
                    int siguiente2 = siguiente; 
                    //Removes siguiente from restantes and inserts in YaVisitadas
                    //Updates tiempo, material
                    Integer r = (Integer)posicionRestantes[h].remove(siguiente2);
                    posicionYaVisitadas[h].add(r); 
                    
                    siguiente = r.intValue();
                    
                    material -= shops4Today.get(siguiente).currentDeliverySize;
                    
                    r = (Integer)tiendaRestantes[h].remove(siguiente2);
                    tiendaYaVisitadas[h].add(r);
                    
                    siguiente = r.intValue();
                    tiempo -= tiempo_trayecto(actual,siguiente);
                    

                    //solo si hemos añadido una tienda, incrementamos el contador t
                    t++;
                }
                

                //Updates pheromones locally
                actualiza_feromona(actual,siguiente);
                //Goes to next shop
                actual = siguiente;
            }

            //If we are in a shop, we have to go to warehouse
            int nt = tiendaYaVisitadas[h].size();
            
            if (((Integer)tiendaYaVisitadas[h].get(nt-1)).intValue()!=0){
            	tiendaYaVisitadas[h].add(new Integer(0));
            	posicionYaVisitadas[h].add(new Integer(-1));
            }
            
            coste_temp[h] = this.coste_total(tiendaYaVisitadas[h]);
        }
        
        //3. We choise best solution in this iteration
        double coste_minimo = coste_temp[0]; 
        int minimo = 0;
        for (int h=1; h<nhormigas; h++)
                if(coste_minimo > coste_temp[h]){
                        minimo = h;
                        coste_minimo = coste_temp[h];
                }

        //4. Keep this solution 		
        mejores_soluciones[sfin] = (ArrayList <Integer>) tiendaYaVisitadas[minimo].clone();
        mejores_costes[sfin] = coste_minimo;
        
        
        //4.1 Removes ants' solutions 
        for (int h=0; h<nhormigas; h++){
            posicionYaVisitadas[h].clear();
            tiendaYaVisitadas[h].clear();
        }

        //5. Updates pheromones globally
        actualiza_feromona();

        
        //6. Local search
        ArrayList<Integer> nueva_sol = busqueda_local(state,thread,mejores_soluciones[sfin]);
        if (nueva_sol != null){
            double nuevo_coste = coste_total(nueva_sol);
            
            if (nuevo_coste < coste_minimo){
                    mejores_soluciones[sfin] = (ArrayList)nueva_sol.clone();
                    mejores_costes[sfin] = nuevo_coste;
            }
        }
        
        
        //7. Keep best solution
        mejor_solucion_ejecucion = (ArrayList <Integer>) mejores_soluciones[sfin].clone();
        mejor_coste_ejecucion = mejores_costes[sfin];


        //8. Updates indexes sfin and sinic
        cambiar_configuracion();
        
        
        
      
        
        //Rest of iterations
        //--------------------
        int iter = 1;
        while(contador_iguales <40 && iter<iteraciones){
           
            //1. Each ant inserts warehouse	
            for (int h=0;h<nhormigas;h++){
                coste_temp[h] = 0.0;
                posicionRestantes[h]=(ArrayList <Integer>) posicionLista.clone();
                
                tiendaRestantes[h]=(ArrayList <Integer>) tiendaLista.clone();
                
                
                posicionYaVisitadas[h]=(ArrayList <Integer>) new ArrayList<Integer>();
                posicionYaVisitadas[h].add(new Integer(-1));

                tiendaYaVisitadas[h]=(ArrayList <Integer>) new ArrayList<Integer>();
                tiendaYaVisitadas[h].add(new Integer(0));
            }

            //2. Each ant makes a solution to this iteration      

            for (int h=0;h<nhormigas;h++){
                double material = vehicleCapacity;//cantidad restante de material del reparto 
                double tiempo = maximumWorkTime;//tiempo restante del reparto

                int actual = 0;
                int siguiente = 0;

                int t = 0;

                int ntiendas = tiendaLista.size();
                while (t<ntiendas){
                	//Next shop (position in arrays posicionRestantes y tiendaRestantes)
                    //or -1 if warehouse
                    siguiente = transicion(state,thread,actual,posicionRestantes[h],tiendaRestantes[h],tiempo,material);

                    if (siguiente == -1){
                            tiendaYaVisitadas[h].add(new Integer(0));
                            posicionYaVisitadas[h].add(new Integer(-1));

                        //New route 
                        material = vehicleCapacity;
                        tiempo = maximumWorkTime;
                        siguiente = 0;

                    }
                    else{

                        int siguiente2 = siguiente; 
                        //Removes siguiente from restantes and inserts in YaVisitadas
                        //Updates tiempo, material
                        Integer r = (Integer)posicionRestantes[h].remove(siguiente2);
                        posicionYaVisitadas[h].add(r); 

                        siguiente = r.intValue();

                        material -= shops4Today.get(siguiente).currentDeliverySize;

                        r = (Integer)tiendaRestantes[h].remove(siguiente2);
                        tiendaYaVisitadas[h].add(r);

                        siguiente = r.intValue();
                        tiempo -= tiempo_trayecto(actual,siguiente);


                        //solo si hemos añadido una tienda, incrementamos el contador t
                        t++;
                    }


                  //Updates pheromones locally
                    actualiza_feromona(actual,siguiente);
                    //We goes to next shop 
                    actual = siguiente;
                }

                //If we are in the last shop, we have to go to warehouse
                int nt = tiendaYaVisitadas[h].size();

                if (((Integer)tiendaYaVisitadas[h].get(nt-1)).intValue()!=0){
                    tiendaYaVisitadas[h].add(new Integer(0));
                    posicionYaVisitadas[h].add(new Integer(-1));
                }

                coste_temp[h] = this.coste_total(tiendaYaVisitadas[h]);
            }

            //3. We choise best solution in this iteration
            coste_minimo = coste_temp[0]; 
            minimo = 0;
            for (int h=1; h<nhormigas; h++)
                    if(coste_minimo > coste_temp[h]){
                            minimo = h;
                            coste_minimo = coste_temp[h];
                    }

            //4. We keep the solution in this iteration		
            mejores_soluciones[sfin] = (ArrayList <Integer>) tiendaYaVisitadas[minimo].clone();
            mejores_costes[sfin] = coste_minimo;


            //4.1 Removes the other solutions
            for (int h=0; h<nhormigas; h++){
                posicionYaVisitadas[h].clear();
                tiendaYaVisitadas[h].clear();
            }

            //5. Updates pheromones globally
            actualiza_feromona();


            //6. Local search
            nueva_sol = busqueda_local(state,thread,mejores_soluciones[sfin]);
            if (nueva_sol != null){
                double nuevo_coste = coste_total(nueva_sol);

                if (nuevo_coste < coste_minimo){
                        mejores_soluciones[sfin] = (ArrayList)nueva_sol.clone();
                        mejores_costes[sfin] = nuevo_coste;
                }
            }


            //7. If solution from local search is better than older one, we keep it
            if (mejores_costes[sfin]<mejor_coste_ejecucion){
                mejor_solucion_ejecucion = (ArrayList <Integer>) mejores_soluciones[sfin].clone();
                mejor_coste_ejecucion = mejores_costes[sfin];
                
                //We restart the counter of same solution
                contador_iguales = 0;
            }
            else
            	contador_iguales ++;


            //8. Updates indexes sfin and sinic
            cambiar_configuracion();
            
            //New iteration
            iter++;
        }
        
        
        

        //Copies the global solution to routes4Today
        convertir_a_rutas();
	}
	
	
    /**Total Cost of the solution
     * 
     * @param s: solution
     * @return Total Cost of the solution s
     */
    public double coste_total(ArrayList <Integer> s){
        double c =0.0;

        int actual, siguiente;


        actual = (s.get(0)).intValue();

        for (int i=1;i<s.size();i++){
            siguiente = (s.get(i)).intValue();

            c += coste_trayecto(actual,siguiente);

            actual = siguiente;
        }


        return c;
    }


    /**Cost to move from one shop to another
     * 
     * @param actual: current shop (0 for depot)
     * @param siguiente: next shop (0 for depot)
     * 			
     * @return Cost in euros
     */
    public double coste_trayecto(int actual, int siguiente){
        
            return distanceTable[actual][siguiente]*costPerKm;

    }
    
    /**Time to move from one shop to another + download Time
     * 
     * @param actual: current shop (0 for depot)
     * @param siguiente: next shop (0 for depot)
     * 
     * @return Cost
     */
    public double tiempo_trayecto(int actual,int siguiente){        
        double t = 0.0;
        
        if (actual != siguiente){
        	t = distanceTable[actual][siguiente]/speed;
        	if (siguiente != 0)
        		t += downloadTime;
        }

     return t;
    }
	

    /**Updates locally matrix of phermones in i,j
     * 
     * @param i: row or 0 for depot
     * @param j: column or 0 for depot
     */
    public void actualiza_feromona(int i, int j){


        feromonas[i][j] *= 1 - parametros[2];
        feromonas[i][j] += parametros[2]*tau;

    }

    /**Updates overall matrix of phermones
     */
    public void actualiza_feromona(){

    	
    	for (int i =0;i<tamferom;i++)
            for (int j=0;j<tamferom;j++){
                    feromonas[i][j] *= 1 - parametros[3];
                    feromonas[i][j] += parametros[3]*coste_trayecto(i,j);
            }
    	
    	
    	

    	
    	
    	
    }
    
    /**Calculates next shop to visit
     * 
     * @param actual: current shop
     * @param lista_tiendas: shops have not been able to visit yet
     * @param tiempo: remaining time until end of work
     * @param carga: remaining goods in the vehicle
     * 
     * @return next shop (position in lista_tiendas) or -1 to depot
     */
    public int transicion(EvolutionState state, int thread,int actual, 
    		ArrayList<Integer> posicionR, ArrayList<Integer> tiendaR,
    		double tiempo, double carga){
    	
    	
        int siguiente = -1;

        
        if (tiempo > 0 && carga > 0){
            ArrayList <Integer> posiciones = new ArrayList <Integer> ();  
            ArrayList <Double> tiempos = new ArrayList <Double> (); 
            ArrayList <Double> probs_asociadas = new ArrayList <Double> ();
            double total = 0.0;
            
            //We select shops which are near (smaller than tiempo) and demand is not very big (smaller than carga)
            
            int tam = tiendaR.size();
            for (int i =0;i<tam;i++){
                int tienda = tiendaR.get(i).intValue();
                int ptienda = posicionR.get(i).intValue();
                
                double t1 = tiempo_trayecto(actual,0);
                double t2 = tiempo_trayecto(actual,tienda);
                
                double c1 = shops4Today.get(ptienda).currentDeliverySize;
                
                if ((t1+t2) <= tiempo && c1 <= carga){
                    posiciones.add(new Integer(i));
                    double temp = Math.pow(feromonas[actual][tienda],parametros[0]);
                    temp *=  Math.pow(heuristica(actual,ptienda),parametros[1]);
                    probs_asociadas.add(new Double(temp));
                    tiempos.add(new Double(t1+t2));
                    
                    total += temp;                            
                }
            }
      
            //If they exist, we order them by probability and choose one
            if (total !=0.0){
                tam = posiciones.size();
                for (int i =0;i<tam;i++){
                    double k = posiciones.get(i).doubleValue();
                    probs_asociadas.set(i,new Double(k/total));
                }
                
                ordenar_lista(posiciones,probs_asociadas);
                
                double p = probs_asociadas.get(tam-1).doubleValue()-probs_asociadas.get(0).doubleValue();
                p*=state.random[thread].nextDouble();
                
                p+=probs_asociadas.get(0).doubleValue();
                
                int s = 0;
                while(p>probs_asociadas.get(s).doubleValue() && s < tam){
                    s++;                    
                }
                
                if (s == tam)
                    s--;
                
                siguiente = posiciones.get(s);
                
            }
        
        }
  

        return siguiente;

    }
    /** Says how good it's to move from actual to siguiente
     * @param actual: current shop
     * @param siguiente: position of possible next shop in shops4Today
     */
     public double heuristica (int actual, int siguiente){

         double d = tiempo_trayecto(actual,siguiente)*parametros[4];
         d += shops4Today.get(siguiente).currentDeliverySize*parametros[5];

         return d;
     }

     /**Sorts ascending the two lists according to probs_asociadas
      * 
      * @param tiendas_accesibles
      * @param posiciones
      * @param probs_asociadas
      */
     public void ordenar_lista(ArrayList <Integer>posiciones, ArrayList <Double>probs_asociadas){
         
         int k = posiciones.size();
         
         for (int i =0;i<k;i++){
             for (int j=i+1;j<k;j++){
                 if (probs_asociadas.get(i).doubleValue()> probs_asociadas.get(j).doubleValue()){
                     
                     Integer p = posiciones.get(i);
                     posiciones.set(i, posiciones.get(j));
                     posiciones.set(j,p);
                     
                     Double d = probs_asociadas.get(i);
                     probs_asociadas.set(i, probs_asociadas.get(j));
                     probs_asociadas.set(j,d);                
                 }
             }
             
             
         }
     }


     /**Updates counters sfin and sinic
      * 
      */
     public void cambiar_configuracion(){


         if (modulo(sfin+1,10)==sinic)
                 sinic = modulo(sinic+1, 10);
         
         sfin = modulo(sfin+1,10);

     }
     
     /**Calculates the rest of the division of D/d
      * 
      */
     private int modulo(int D, int d){
         int R = 0;
         
         int C = D/d;
         
         if (D<0)
             R = D +(1-C)*d;
         else
             R = D-C*d;
         
         return R;
     }
     
     
     
     /**Finds the best neighbor of a possible neighborhood
      * for a given solution, with lambda-interchanges.
      * 
      * @param mejorSolucion: Best solution found in a iteration (number of shops, not positions)
      * 
      * @return The best neighbor (better or worse than mejorSolucion)
      * */
     public ArrayList busqueda_local(EvolutionState state, int thread,ArrayList <Integer> mejorSolucion){
         ArrayList <Integer> mejorVecino = null;
         
         
         double coste_mejorVecino = Double.MAX_VALUE;

         ArrayList <Integer> p = (ArrayList) new ArrayList <Integer>();

      
         //Searches number of routes        
         for (int j=1;j<mejorSolucion.size();j++)
             if (((Integer)mejorSolucion.get(j)).intValue() == 0)
                 p.add(new Integer(j));
         
         int nrutas = p.size();

     
         
         if (nrutas==1){
             //Only one route-> exchange shops
             ArrayList<Integer> temp = (ArrayList <Integer>) mejorSolucion.clone();  
             double c = mejor_coste_ejecucion;
             for (int s1=1;s1<temp.size()-1;s1++)
                 for (int s2=1;s2<temp.size()-1;s2++){
                     if (s1!=s2){
                         Integer k = temp.get(s1);
                         temp.set(s1, temp.get(s2));
                         temp.set(s2,k);
                         
                         corregir(temp); 
                         
                         double c2 = coste_total(temp);
                         if (c2< c)
                             c =c2;
                         else{
                             k = temp.get(s1);
                             temp.set(s1, temp.get(s2));
                             temp.set(s2,k);
                         }
                     }
                 }
             
             if (c<mejor_coste_ejecucion)
                 mejorVecino = (ArrayList <Integer>) temp.clone();
        }
         else{
             if (nrutas == 2){
            	//Only two routes -> it depends on the size of routes
                 ArrayList<Integer> temp = (ArrayList <Integer>) mejorSolucion.clone();  
                 double c = mejor_coste_ejecucion;
                 
                 
                 int s1 = p.get(0).intValue();
                 int s2 = p.get(1).intValue()-s1;
                 
                 s1--;
                 s2--;
                 
                 
                 int explotacion = factor*Math.max(s1,s2);
                 for (int i = 0; i<explotacion;i++){
                     
                     //Exchanges two shops each iteration

                     int t1 = (int) ((double) s1*state.random[thread].nextDouble()) + 1;                     
                     int t2 = (int) ((double) s2*state.random[thread].nextDouble()) + p.get(0) + 1;
                     
                                          
                     Integer t = temp.get(t1);                     
                     temp.set(t1,temp.get(t2));
                     temp.set(t2,t);
                     
                     corregir(temp); 
                     
                     double c2 =  coste_total(temp);
                     if (c2<c)
                         c = c2;
                     else{
                         t = temp.get(t1);  
                         temp.set(t1,temp.get(t2));
                         temp.set(t2,t);
                     }
                 }
                 if (c<mejor_coste_ejecucion)
                      mejorVecino = (ArrayList <Integer>) temp.clone();
                     
             }
             else{
                 //More than two routes -> exchanges parts of routes
                 
                 int explotacion = factor*nrutas;
                 //Size of neighborhood: factor*nrutas
                 for(int i = 0; i<explotacion;i++){
                     
                     //Searches of routes 
                     p.clear();
                     for (int j=1;j<mejorSolucion.size();j++)
                         if (((Integer)mejorSolucion.get(j)).intValue() == 0)
                             p.add(new Integer(j));
                     
                     //We choose two routes 
                     int Ruta1 = 0;
                     int Ruta2 = 0;
                     
                     
                     Ruta1 =(int) (((double) p.size()) * state.random[thread].nextDouble());
                     while (Ruta1 ==0)
                         Ruta1 =(int) (((double) p.size()) * state.random[thread].nextDouble());
                     
                     Ruta2=Ruta1;
                     while (Ruta1 == Ruta2){
                         Ruta2 = (int) (((double) p.size()) * state.random[thread].nextDouble());
                         while (Ruta2 == 0)
                             Ruta2 =(int) (((double) p.size()) * state.random[thread].nextDouble());
                     }



                     ArrayList<Integer> temp = (ArrayList <Integer>) mejorSolucion.clone();            
                     //We choose half of size of route 1 (removes them) and we copy the shops

                     int k2 = (p.get(Ruta1)).intValue();
                     int k1 = (p.get(Ruta1-1)).intValue(); 

                     int num_elems = k2-k1;                

                     int el = (int) num_elems/2;
                     ArrayList segmento1 = (ArrayList) new ArrayList();
                     while (el > 0){
                         int pos = (int) (state.random[thread].nextDouble()*((double)num_elems));
                         while(pos == 0)
                             pos = (int) (state.random[thread].nextDouble()*((double)num_elems));

                         Integer v = (Integer) temp.get(k1+pos);
                         segmento1.add((Object) v);                    
                         temp.remove(k1+pos);
                         el--;
                         num_elems--;
                         //updates positions in p
                         for (int j = Ruta1;j<p.size();j++){
                             k2 = ((Integer)p.get(j)).intValue()-1;
                             p.set(j, new Integer(k2));
                         }

                     }

                   //We choose half of size of route 2 (removes them) and we copy the shops


                     k2 = (p.get(Ruta2)).intValue();
                     k1 = (p.get(Ruta2-1)).intValue(); 

                     num_elems = k2-k1;                

                     el = num_elems/2;
                     ArrayList segmento2 = (ArrayList) new ArrayList();
                     while (el > 0){
                         int pos = (int) (state.random[thread].nextDouble()*((double)num_elems));
                         while(pos == 0)
                             pos = (int) (state.random[thread].nextDouble()*((double)num_elems));

                         Integer v = (Integer) temp.get(k1+pos);
                         segmento2.add((Object) v);                    
                         temp.remove(k1+pos);
                         el--;
                         num_elems--;

                       //updates positions in p
                         for (int j = Ruta2;j<p.size();j++){
                             k2 = ((Integer)p.get(j)).intValue()-1;
                             p.set(j, new Integer(k2));
                         }
                     }

                   
                     //We makes the copies of shops
                     
                     //route 1 to route 2
                     for (int j = 0; j<segmento1.size(); j++){
                         Integer v = (Integer) segmento1.get(j);                    
                         int pos = ((Integer)p.get(Ruta2-1)).intValue() + 1;                    
                         temp.add(pos,v);

                         for(int k = Ruta2; k<p.size(); k++){
                             k2 = ((Integer) p.get(k)).intValue() +1;
                             p.set(k, new Integer(k2));
                         }
                     }

                     //route 2 to route 1
                     for (int j = 0;j< segmento2.size();j++){
                         Integer v = (Integer) segmento2.get(j);                    
                         int pos = ((Integer)p.get(Ruta1-1)).intValue() + 1;                    
                         temp.add(pos,v); 
                     }



                     //If the new solution is not valid, we have to correct it
                     corregir(temp);                



                     double c = coste_total(temp);

                     if (c<coste_mejorVecino){
                         int k = sinic;

                         boolean igual = false;
                         while (k!=sfin && !igual){
                             igual = temp.equals(mejores_soluciones[k]);

                             k= modulo(k+1,10);
                         }
                         if (!igual){
                             coste_mejorVecino = c;
                             mejorVecino = (ArrayList <Integer>) temp.clone();
                         }
                     }
                 }
             
             }
         
         }
         
         
    
         return mejorVecino;   	 
    	 
    	 
    	 
     }
     
     /**Changes a path. If for the next shop there is insufficient time or 
      * merchandise, it insertes the depot as next stop.
      *
      *@param ruta: the path to correct.
      **/
     public void corregir(ArrayList <Integer> ruta){
    	 
    	 int k = ruta.size();
         double tiempo = maximumWorkTime;            
         double carga = vehicleCapacity;   

         int actual = (ruta.get(0)).intValue();
         int i=1;
         while(i<k){
             int siguiente = (ruta.get(i)).intValue();


             if(siguiente == 0){                	
                 //if next shop is warehouse, we renew the counters
                 tiempo = maximumWorkTime;            
                 carga = vehicleCapacity;  
             }
             else{
                 //if is other shop we have to see if is possible to go to this shop
                 double tt = tiempo_trayecto(actual,siguiente);
                 double tt2 = tiempo_trayecto(siguiente,0);
                 
                 int p=0;
                 
                 while(Integer.valueOf(shops4Today.get(p).shopID).intValue()!=siguiente)
                    p++;
                 

                 double c = shops4Today.get(p).currentDeliverySize;

                 if (c > carga || tt + tt2 > tiempo){
                         //It's not possible-> we renew the counters
                     ruta.add(i,new Integer(0));
                     tiempo = maximumWorkTime;
                     carga = vehicleCapacity;                         
                     siguiente = 0; 
                     k++;                 
                 }
                 else{
                     //It's possible-> we decrement the counters
                     tiempo -= tiempo_trayecto(actual,siguiente);
                     carga -= c;
                     }
                 }
             actual = siguiente;
             i++;
             }


     }

     /**Copies the ArrayLists posicionLista and tiendaLista in
      * routes4Today
      */ 
    private void convertir_a_rutas(){
     	
  
   	 Route R = new Route();
   	 R.shopsVisited.add(Depot);
   	 R.cost = 0.0;
   	 R.demand = 0.0;
   	 R.distanceTravelled = 0.0;
   	 R.time = 0.0;
   	 
   	 int actual = 0;
   	 for (int i=1; i<mejor_solucion_ejecucion.size();i++){
   		     	
   		 int siguiente = mejor_solucion_ejecucion.get(i).intValue();
   		 if (siguiente == 0){
   			 //Is the depot
   			 R.shopsVisited.add(Depot);
   			 R.cost += coste_trayecto(actual,siguiente);
   			 
   			 R.time += distanceTable[actual][siguiente]/speed;
   			 R.distanceTravelled += distanceTable[actual][siguiente];
   			 bestSolution.addRoute(R);
   			 //New route
   			 R = new Route();
   			 R.shopsVisited.add(Depot);
   		 }
   		 else{
   			 //A shop
   			 int p = mejor_solucion_ejecucion.get(i).intValue();
   			 int j = 0;
   			 while(Integer.valueOf(shops4Today.get(j).shopID).intValue()!=p)
   				 j++;
   			 
   			 Shop tienda = shops4Today.get(j);
   			 R.shopsVisited.add(tienda);
   			 R.cost += coste_trayecto(actual,siguiente);
   			 R.demand += tienda.currentDeliverySize;
   			 R.time += tiempo_trayecto(actual,siguiente);
   			 R.distanceTravelled += distanceTable[actual][siguiente];
   			 
   		 }
   		 
   		 actual = siguiente;
   		 
   	 }
   	 
   	 
     }

	

}
