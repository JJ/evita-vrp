package ec.app.itp;

import java.util.ArrayList;

import ec.EvolutionState;
import ec.util.Parameter;
import ec.app.vrp1.Shop;
import ec.app.vrp1.Route;


/**Class to solve the VRP using Clarke & Wright algorithm 
 * 
 * @author Anais Martinez Garcia
 *
 */
public class CWLS extends VRPSolver {

	private static final long serialVersionUID = 1L;
	
	/**Internal solution of execution*/
    private ArrayList<Integer> posicionLista;
    private ArrayList<Integer> tiendaLista;
        
        
    /**Number of routes*/   
    private int num_rutas;

    
    
	public CWLS(final EvolutionState state, final Parameter base,ITPdata input, ArrayList<Shop> List, int dayOfWeek) {
		
		super(state, base, input, List, dayOfWeek);
		
		posicionLista = new ArrayList <Integer> ();
		tiendaLista = new ArrayList <Integer> ();
	
		num_rutas = List.size();
		
		
	}

	public void findRoutes(final EvolutionState state, final int thread){
		
		
		/*1. Makes routes (Warehouse, shop_1, Warehouse, shop_2, Warehouse, ..., 
		 * Warehouse, shop_n ,Warehouse), with the shops from the list shops4Today
		 */
       

        for (int i = 0;i<shops4Today.size();i++){            
            posicionLista.add(new Integer(-1));            
            posicionLista.add(new Integer(i));
            
            tiendaLista.add(new Integer(0));
            int t = Integer.valueOf(shops4Today.get(i).shopID).intValue();
            tiendaLista.add(new Integer(t));
        }
        
        posicionLista.add(new Integer(-1));  
        tiendaLista.add(new Integer(0));
        
        double [][] costes = new double [num_rutas][num_rutas];
        
        //2. Calculates initial cost:
        //s(shop_i,shop_j) = coste(shop_i,Wharehouse) + coste(Wharehouse,shop_j) - cost(shop_i,shop_j)
        for (int i=0;i<num_rutas;i++)
            for (int j=0;j<num_rutas;j++){
            	if (i==j)
            		costes[i][j] = 0.0;
            	else{
            		
	            	int ruta_i = 2*i + 1;
	                int ruta_j = 2*j + 1;
	                
	                int tienda_i = tiendaLista.get(ruta_i).intValue();
	                int tienda_j = tiendaLista.get(ruta_j).intValue();
	                
	                costes[i][j] = coste_trayecto(tienda_i,0);
	                costes[i][j] += coste_trayecto(0,tienda_j);
	                costes[i][j] -= coste_trayecto(tienda_i,tienda_j);
            	}
               
            }
        
        
        boolean encontrado = false;
        
        //3. Combining routes wherever possible
        int k = num_rutas;
        int i =0;  
        
        boolean sigue = true;
        while(sigue){
                //Best union
                int [] r_mejores = maximo(costes);
                if (r_mejores[0]== -1  && r_mejores[1] == -1){
                	//All costs are 0
                    sigue = false;
                }
                else{
                    r_mejores[0]++;
                    r_mejores[1]++;
                    //Makes the combination 
                    if (combinacion_posible(r_mejores)){
                        combina(r_mejores);
                         //Forget this combination
                        costes = elimina(r_mejores[1]-1,costes);
                        num_rutas --; 
                    }
                    else{
                        costes[r_mejores[0]-1][r_mejores[1]-1]=0;    
                    }                 
                }
            }
        
           

      
        
        //4. Improves each of the routes
        mejora_rutas();
    
    
        
        //5. Improves exchange shops in routes
        intercambio_rutas();
            
        
        //6. Copies the internal solution to bestSolution
        convertir_a_rutas();
 
   

	}
	
    /**Calculates the maximum of costs of s(i,j)
     * 
     * @param c
     * @return
     */
    private int [] maximo (double [][] c){
        int [] r_mejores = new int[2];
        
        r_mejores[0] = -1;
        r_mejores[1] = -1;
        double max = 0;
        for (int i=0;i<num_rutas;i++)
            for (int j =0;j<num_rutas;j++)
                if (j!=i && c[i][j] > max && c[i][j]!=0){
                        r_mejores[0] = i;
                        r_mejores[1] = j;
                        max = c[i][j];
                    }
           
        
        return r_mejores;
    }

    /**Indicates if combination of r_mejores[0],r_mejores[1]
     * if feasible
     */
    private boolean combinacion_posible(int [] r_mejores){
        boolean p = true;
        
        
        int [] ruta_i = posiciones_ruta(r_mejores[0]);
        int [] ruta_j = posiciones_ruta(r_mejores[1]);
        
        
        double tiempo = 0;
        
        double carga = 0;
        
        //Calculates cost of join two routes
        int actual = tiendaLista.get(ruta_i[0]).intValue();
        for (int pos = ruta_i[0] + 1;pos<ruta_i[1];pos++){            
            int siguiente = tiendaLista.get(pos).intValue();            
            tiempo += tiempo_trayecto(actual,siguiente);
            
            int siguiente2 = posicionLista.get(pos).intValue();
            carga += shops4Today.get(siguiente2).currentDeliverySize;            
            actual = siguiente;
        }
        
        tiempo += tiempo_trayecto(actual,0);
        
        actual = tiendaLista.get(ruta_j[0]).intValue();
        
        for (int pos = ruta_j[0] + 1;pos<ruta_j[1];pos++){            
        	int siguiente = tiendaLista.get(pos).intValue(); 
            
            tiempo += tiempo_trayecto(actual,siguiente);
            int siguiente2 = posicionLista.get(pos).intValue();
            
            carga += shops4Today.get(siguiente2).currentDeliverySize;      
            actual = siguiente;
        }
        
        tiempo += tiempo_trayecto(actual,0);
        
        if (tiempo > maximumWorkTime || carga > vehicleCapacity)
            p=false;
        
        return p;
    }
    
    
    /**Adds rutas[1] to rutas[0]
     * 
     * @param rutas
     */  
    private void combina(int [] rutas){
         
         ArrayList <Integer> tiendaTemp = new ArrayList <Integer>();
         ArrayList <Integer> posicionTemp = new ArrayList <Integer>();
                
         
         int [] ruta_j = posiciones_ruta(rutas[1]);
         
         
         int k = ruta_j[1] - ruta_j[0];
         
         for (int i=0;i<k;i++){
             Integer t = tiendaLista.remove(ruta_j[0]);
             tiendaTemp.add(t);
             
             t = posicionLista.remove(ruta_j[0]);
             posicionTemp.add(t);
         }
         
         
         //if rutas[0] is behind rutas[1], we have to reduce rutas[0]
         if (rutas[1]<rutas[0])
        	 rutas[0]--;
         
         int [] ruta_i = posiciones_ruta(rutas[0]);
         for (int i=1;i<k;i++){
        	 tiendaLista.add(ruta_i[1]+i-1,tiendaTemp.get(i));
        	 posicionLista.add(ruta_i[1]+i-1,posicionTemp.get(i));             
         }
           
         
         
     }

     /**Removes the row and the column given by ruta from the matrix coste_antiguo
      * and returns it in the result 
      * @param ruta
      * @param coste_antiguo
      * @return
      */
    private double[][] elimina(int ruta,double [][] coste_antiguo){
         double[][] nuevo_coste = new double[num_rutas-1][num_rutas-1];
         
         for (int i=0;i<ruta;i++){
             for (int j=0;j<ruta;j++)
                 nuevo_coste[i][j] = coste_antiguo[i][j];
             
             for (int j=ruta+1;j<num_rutas;j++)
                 nuevo_coste[i][j-1] = coste_antiguo[i][j];
         }

         for (int i=ruta+1;i<num_rutas;i++){
             for (int j=0;j<ruta;j++)
                 nuevo_coste[i-1][j] = coste_antiguo[i][j];
             
             for (int j=ruta+1;j<num_rutas;j++)
                 nuevo_coste[i-1][j-1] = coste_antiguo[i][j];
         }
  
         
         return nuevo_coste;
     }

    
     /**It improves separately each route 
      * 
      */
    private void mejora_rutas(){
         
       int pos_fin = 0;
       for (int i=0;i<num_rutas;i++){
           
           //search the limits of the route in solution
           int pos_inic = pos_fin;
           
           pos_fin++;
           while (tiendaLista.get(pos_fin).intValue()!=0)
               pos_fin++;
           
           //Calculates initial cost in route
           double c1 = coste_ruta(pos_inic,pos_fin);
           
           
           for (int j=pos_inic+1;j<pos_fin;j++)
               for (int k=pos_inic+1;k<pos_fin;k++){
                   //Tests to exchange two shops
                   Integer tienda = tiendaLista.get(j);
                   tiendaLista.set(j,tiendaLista.get(k));
                   tiendaLista.set(k,tienda);
                   
                   Integer posicion = posicionLista.get(j);
                   posicionLista.set(j,posicionLista.get(k));
                   posicionLista.set(k,posicion);
                   
                   
                   double c2 = coste_ruta(pos_inic,pos_fin);
                   
                   //If final cost is better than old result, we accept the change
                   if (c2 < c1)
                       c1 = c2;
                   else{
                       //if final cost is worse than old result, we don't accept the change
                	   tienda = tiendaLista.get(j);
                       tiendaLista.set(j,tiendaLista.get(k));
                       tiendaLista.set(k,tienda);
                       
                       posicion = posicionLista.get(j);
                       posicionLista.set(j,posicionLista.get(k));
                       posicionLista.set(k,posicion);                    
                   }
               }
       }
     
     }

     
     /**Exchages shops between routes
      * 
      */
    private void intercambio_rutas(){
         
       int [] inicio = new int[num_rutas+1];
       
       inicio[0] = 0;
       
       //Initial positions in each route
       int nr = 1;
       for (int j=1;j<tiendaLista.size();j++)
           if (tiendaLista.get(j).intValue() == 0){
               inicio[nr]=j;
               nr++;
           }
       
       for (int i=0;i<num_rutas;i++){
           int pos_inic_i = inicio[i];
           int pos_fin_i = inicio[i+1];      
           
           double c_i = coste_ruta(pos_inic_i,pos_fin_i);
           
                   
           for(int j=i+1;j<num_rutas;j++){
               int pos_inic_j = inicio[j];
               int pos_fin_j = inicio[j+1];
               
               double c_j = coste_ruta(pos_inic_j,pos_fin_j);
               
               for (int a = pos_inic_i+1;a<pos_fin_i;a++){                  
                   for (int b = pos_inic_j+1;b<pos_fin_j;b++){
                       //We probe to excange the shops of routes
                	   Integer tienda = tiendaLista.get(a);
                       tiendaLista.set(a,tiendaLista.get(b));
                       tiendaLista.set(b,tienda);
                       
                       Integer posicion = posicionLista.get(a);
                       posicionLista.set(a,posicionLista.get(b));
                       posicionLista.set(b,posicion);
                       
                       double c_i2 = coste_ruta(pos_inic_i,pos_fin_i);
                       double c_j2 = coste_ruta(pos_inic_j,pos_fin_j);
                       
                       if (c_i2<c_i && c_j2 < c_j){
                           c_i = c_i2;
                           c_j = c_j2;                          
                       }
                       else{
                           //if the cost of both routes isn't less than old routes, we don't accept the change
                    	   tienda = tiendaLista.get(a);
                           tiendaLista.set(a,tiendaLista.get(b));
                           tiendaLista.set(b,tienda);
                           
                           posicion = posicionLista.get(a);
                           posicionLista.set(a,posicionLista.get(b));
                           posicionLista.set(b,posicion); 
                       
                       }
                           
                       
                   }
                   
               }
             }
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
    	 for (int i=1; i<posicionLista.size();i++){
    		 //if this shop is warehouse, we insert warehouse and we finish this route
    		     	
    		 int siguiente = tiendaLista.get(i).intValue();
    		 if (siguiente == 0){
    			R.shopsVisited.add(Depot);
    			R.cost += coste_trayecto(siguiente,0);
    			
    			R.time += distanceTable[actual][siguiente]/speed;
    			bestSolution.addRoute(R);
    			
    			//new route
    			R = new Route();
    			R.shopsVisited.add(Depot);    			
    		 }
    		 else{
    			 int p = posicionLista.get(i).intValue();
    			 Shop tienda = shops4Today.get(p);
    			 R.shopsVisited.add(tienda);
    			 
    			 R.cost += coste_trayecto(actual,siguiente);
    			 R.demand += tienda.currentDeliverySize;
    			 R.time += tiempo_trayecto(actual,siguiente);
    			 R.distanceTravelled += distanceTable[actual][siguiente];    			 
    		 }
    		 
    		 actual = siguiente;
    		 
    	 }
    	 
    	 
    	 
     }

     
     
     /**It calculates the total cost of a route in solucion
      * 
      * @param pos_inic: initial position
      * @param pos_fin: final position
      * @return
      */
    private double coste_ruta(int pos_inic, int pos_fin){
         double c = 0.0;
         
         int actual = tiendaLista.get(pos_inic).intValue();
         
         for (int pos = pos_inic + 1;pos<=pos_fin;pos++){            
             int siguiente = tiendaLista.get(pos).intValue();
             
             c+= coste_trayecto(actual,siguiente);
             actual = siguiente;
         }
         
         return c;
         
     }

     
     

    /**It calculates the start and end of the route ruta_seleccionada in ArrayList
     * posicionLista and tiendaLista
     * 
     * @param ruta_seleccionada
     * @return [0] = start (this component is the depot),[0] = end (this component is the depot)
     */
    private int [] posiciones_ruta(int ruta_seleccionada){
         
         int [] pos = new int [2];
         int i = 0;
         int p=0;
         while (i<ruta_seleccionada){
             if(tiendaLista.get(p).intValue()==0)
                 i++;
             p++;
         }
         p--;
         pos[0] = p;
         p++;
         
         while(tiendaLista.get(p).intValue()!=0)
             p++;
         
         pos[1] = p;
         
         
         
         return pos;
     }

	
	
	
    /**Cost to move from one shop to another
     * 
     * @param actual: current shop (0 for depot)
     * @param siguiente: next shop (0 for depot)
     * @return Cost in euros
     */
	
    private double coste_trayecto(int actual,int siguiente){
    	return distanceTable[actual][siguiente]*costPerKm;
    }

    
    /**Time to move from one shop to another + download Time
     * 
     * @param i: current shop (0 for depot)
     * @param j: next shop (0 for depot)
     * @return Cost
     */
    private double tiempo_trayecto(int actual,int siguiente){

     return downloadTime + distanceTable[actual][siguiente]/speed;
    }

    

    

}
