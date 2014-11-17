/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.curator;

import jade.core.Agent;

/**
 *
 * @author nightwish
 */
public class CuratorAgent extends Agent {
        protected void setup() {
        // Printout a welcome message
        System.out.println("Hallo! CuratorAgent"+ getAID().getName()+" is ready.");
    }
    
}
