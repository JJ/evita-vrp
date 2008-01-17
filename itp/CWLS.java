package ec.app.itp;

import java.util.ArrayList;

import ec.EvolutionState;
import ec.util.Parameter;
import ec.app.vrp1.Shop;
import ec.app.vrp1.Route;

public class CWLS extends VRPSolver {

	private static final long serialVersionUID = 1L;
	
	/**Solution of execution*/
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
		
		
		//1. Construimos las rutas de la forma 
        // (Almacen,tienda_1,Almacen,tienda_2,Almacen,...,Almacen,tienda_n ,Almacen)
		// de la lista de tiendas shops4Today
		//
       

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
        
        //1. calculamos los costes iniciales de la forma
        //s(i,j) = coste(i,Almacen) + coste(Almacen,j) - cost(i,j)
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
        
        //2. Hacemos combinaciones de rutas mientras sea posible
        int k = num_rutas;
        int i =0;  
        
        boolean sigue = true;
        while(sigue){
                //obtenemos la mejor union
                int [] r_mejores = maximo(costes);
                if (r_mejores[0]== -1  && r_mejores[1] == -1){
                	//si todos los costes son 0 terminamos
                    sigue = false;
                }
                else{
                    r_mejores[0]++;
                    r_mejores[1]++;
                    //si es posible, hacemos la combinacion
                    if (combinacion_posible(r_mejores)){
                        combina(r_mejores);
                         //no consideramos mas esa combinacion
                        costes = elimina(r_mejores[1]-1,costes);
                        num_rutas --; 
                    }
                    else{
                        costes[r_mejores[0]-1][r_mejores[1]-1]=0;    
                    }                 
                }
            }
        
            //si no se ha producido ninguna combinacion  mas terminamos

      
        
        //3. Mejoramos las rutas por separado
        mejora_rutas();
    
    
        
        //4. Mejoramos haciendo intercambios entre rutas
        intercambio_rutas();
            
        
        //5. Copiamos la solución obtenida a routes4Today
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
        
        //caculamos el coste de unir las dos rutas
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
         
         
         //si rutas[0] está despues que rutas[1], como hemos quitado
         //rutas[1] tenemos que disminuir rutas[0]
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
           
           //Busqueda de los limites de la ruta en la solucion
           int pos_inic = pos_fin;
           
           pos_fin++;
           while (tiendaLista.get(pos_fin).intValue()!=0)
               pos_fin++;
           
           //calculamos el coste incial de la ruta
           double c1 = coste_ruta(pos_inic,pos_fin);
           
           
           for (int j=pos_inic+1;j<pos_fin;j++)
               for (int k=pos_inic+1;k<pos_fin;k++){
                   //probamos a intercambiar dos tiendas
                   Integer tienda = tiendaLista.get(j);
                   tiendaLista.set(j,tiendaLista.get(k));
                   tiendaLista.set(k,tienda);
                   
                   Integer posicion = posicionLista.get(j);
                   posicionLista.set(j,posicionLista.get(k));
                   posicionLista.set(k,posicion);
                   
                   
                   double c2 = coste_ruta(pos_inic,pos_fin);
                   
                   //si el coste resultante es menor dejamos el cambio
                   if (c2 < c1)
                       c1 = c2;
                   else{
                       // si no deshacemos el cambio
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
       
       //posiciones de inicio de cada ruta
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
                       //probamos a intercambiar una a una las tiendas de las rutas
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
                           //si el coste de ambas rutas no es menor, deshacemos el cambio
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
    		 //si llegamos al almacen metemos el almacen y terminamos
    		 //esta ruta
    		     	
    		 int siguiente = tiendaLista.get(i).intValue();
    		 if (siguiente == 0){
    			R.shopsVisited.add(Depot);
    			R.cost += coste_trayecto(siguiente,0);
    			
    			R.time += distanceTable[actual][siguiente]/speed;
    			routes4Today.addRoute(R);
    			
    			//Nueva ruta
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
