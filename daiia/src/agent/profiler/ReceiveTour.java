/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.profiler;

import jade.core.behaviours.DataStore;
import jade.lang.acl.ACLMessage;
import jade.proto.states.MsgReceiver;

/**
 *
 * @author Nabil
 */
class ReceiveTour extends MsgReceiver {

    ProfilerAgent profileragent;
    
    public ReceiveTour(ProfilerAgent profileragent) {
    this.profileragent = profileragent;
    }
    
    @Override
    protected void handleMessage(ACLMessage msg) {
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
}