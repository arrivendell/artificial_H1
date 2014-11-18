/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.profiler;

import jade.core.AID;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;
import java.util.Arrays;

/**
 *
 * @author Nabil
 */
/*class ReceiveTour extends MsgReceiver {

    ProfilerAgent profileragent;
    
    public ReceiveTour(ProfilerAgent profileragent, MessageTemplate mt, long deadline, DataStore ds, Object key) {
        super(profileragent, mt, deadline, ds, key);
        this.profileragent = profileragent;
    }
    
    @Override
    protected void handleMessage(ACLMessage msg) {
            System.out.println("<" + myAgent.getLocalName() + ">: On est dans HANDLE MESSAGE");

            if (msg == null) {
                System.out.println("<" + myAgent.getLocalName() + ">: Message NULL");
            } else {
            System.out.println("<" + myAgent.getLocalName() + ">: Message recu, contient la liste des oeuvres du tour");
        
            String received = msg.getContent();

            String temp = new String();
            int j=0;
            int length = received.length();
            for(int i = 0; i<=length; i++){
                if ((received.charAt(i)==';')||(i==length)){
                    temp = received.substring(j, i-1);
                    j = i+1;
                    profileragent.tour.add(temp);
                }
            }
            }     
    }    
}*/


class ReceiveTour extends OneShotBehaviour {

    ProfilerAgent profileragent;
    
    public ReceiveTour(ProfilerAgent profileragent) {
        super(profileragent);
        this.profileragent = profileragent;
    }
    
    @Override
    public void action() {
            
        System.out.println("<" + myAgent.getLocalName() + ">: On est dans HANDLE MESSAGE");
        ACLMessage msg = myAgent.blockingReceive(MessageTemplate.MatchSender(profileragent.tourGuide));
        
        if (msg != null) {
            System.out.println("<" + myAgent.getLocalName() + ">: Message recu, contient la liste des oeuvres du tour");
        
            String received = msg.getContent();
            
            int j=0;
            int length = received.length();
            
            for(String s : Arrays.asList(received.split(";")))
            {
                    System.out.println(s);
                    profileragent.tour.add(s);
            }
        }
        else {
             System.out.println("<" + myAgent.getLocalName() + ">: Message NULL");
            block();
        }
    }
    }    