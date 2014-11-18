/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.tourGuide;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 * This cyclic behavior wait for a message and creates a tour
 * @author nightwish
 */
public class CyclicTourSetBehavior extends CyclicBehaviour{
    
    @Override
    public void action(){
          ACLMessage msg = myAgent.receive(MessageTemplate.MatchSender(new AID("curator", AID.ISLOCALNAME)));
        
        if (msg != null) {
            final String listPieces = CreateTour(msg.getContent());
            myAgent.addBehaviour(new OneShotBehaviour() {

                @Override
                public void action() {
                    ACLMessage msgToSend = new ACLMessage(ACLMessage.REQUEST);
                    msgToSend.setContent(listPieces);
                    msgToSend.addReceiver(new AID("profiler", AID.ISLOCALNAME));
                    System.out.format("<%s> : Sending message <%s> to profiler \r\n", myAgent.getLocalName(), listPieces);
                    myAgent.send(msgToSend);
                }
            });
        }
        else {
            block();
        }
    }
    
    /**
     * Here the tour should be created, trivial no transformation for the moment
     * @param interests
     * @return 
     */
    private String CreateTour(String interests){
        return interests;
    }
    
}
