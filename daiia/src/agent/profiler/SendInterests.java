/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.profiler;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.Iterator;



/**
 *
 * @author Nabil
 */
class SendInterests extends OneShotBehaviour {
    
    private ProfilerAgent profileragent;


    public SendInterests(ProfilerAgent profileragent) {
        this.profileragent = profileragent;        
    }
    
    @Override
    public void action(){
                
        ArrayList<String> listeinterets = profileragent.profile.getInterests();
        Iterator<String> it = listeinterets.iterator();
        String tosend = new String();
        
        while (it.hasNext()) {
               String s = it.next();
               tosend +=s +";";
        }
        
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("tourguide", AID.ISLOCALNAME));
        message.setContent(tosend);
        myAgent.send(message);        
    }
}
