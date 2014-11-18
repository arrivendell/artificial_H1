/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.tourGuide;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

/**
 *
 * @author nightwish
 */
public class TourGuideAgent extends Agent {
    
        private AID id = getAID();

    
        protected void setup() {
        // Printout a welcome message
        System.out.println("Hallo! TourGuideAgent"+ getAID().getName()+" is ready.");
        System.out.println("Adding parallel behavior");
        ParallelBehaviour ParBehavior = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL);
        ParBehavior.addSubBehaviour(new CyclicReceiveBehavior());
        ParBehavior.addSubBehaviour(new CyclicTourSetBehavior());
        addBehaviour(ParBehavior);
        
        DFAgentDescription dfd = new DFAgentDescription();
        
        dfd = new DFAgentDescription();
        dfd.setName(id);
        ServiceDescription sd = new ServiceDescription();
        sd.setType("tour proposal");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

    }
    
}
