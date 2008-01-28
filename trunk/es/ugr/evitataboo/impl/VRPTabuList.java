/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package es.ugr.evitataboo.impl;

import es.ugr.evitataboo.Move;
import es.ugr.evitataboo.TabuList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ferguson
 */
public class VRPTabuList implements TabuList{

    private HashMap<Integer,Integer> list;
    private int maxTenure;
    
    public VRPTabuList(int maxTenure){
        this.maxTenure = maxTenure;
        list = new HashMap<Integer,Integer>();
    }
    
    public boolean isTabu(Move m) {
        
        if(list.containsKey(m.getHash()) )
            return true;
        else
            return false;
    }

    public void addMove(Move m) {
        list.put(m.getHash(), new Integer(maxTenure));
        
    }

    public synchronized void  updateTenure() {
        List<Integer> deleted = new ArrayList<Integer>();
        for(Integer k:list.keySet()){
            Integer value = list.get(k);
            if(value > 0)
                list.put(k, (value-1));
            else
                deleted.add(k);
            
        }
        
        for(Integer d:deleted){
            list.remove(d);
        }
    }
    
    @Override
    public String toString(){
        String chain = "";
        for (Integer k:list.keySet())
            chain += "["+k+","+list.get(k)+"]"; 
        return chain;
    }

}
