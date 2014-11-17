/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.tourGuide;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;

/**
 *
 * @author nightwish
 */
public class TourGuideAgent extends Agent {
        protected void setup() {
        // Printout a welcome message
        System.out.println("Hallo! TourGuideAgent"+ getAID().getName()+" is ready.");
        System.out.println("Adding parallel behavior");
        ParallelBehaviour ParBehavior = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL);
        ParBehavior.addSubBehaviour(new CyclicReceiveBehavior());
        ParBehavior.addSubBehaviour(new CyclicTourSetBehavior());
        addBehaviour(ParBehavior);

    }
    
}
