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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author nightwish
 */
public class CyclicReceiveBehavior extends CyclicBehaviour{
   
    @Override
    public void action(){
        ACLMessage msg = myAgent.receive();
        
        if (msg != null) {
            String interests = msg.getContent();
            myAgent.addBehaviour(new OneShotBehaviour() {

                @Override
                public void action() {
                    ACLMessage msgToSend = new ACLMessage(ACLMessage.REQUEST);
                    msgToSend.setContent(interests);
                    msgToSend.addReceiver(new AID("curator", AID.ISLOCALNAME));
                    System.out.println("Sending message to curator");
                    myAgent.send(msgToSend);
                }
            });
        }
        else {
            block();
        }
    }
    
    private List<String> parseInterest(String interests){
        return Arrays.asList(interests.split("#")[1].split(";"));
    }
    
}