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
        System.out.println("<" + myAgent.getLocalName() + ">: Interest list is going to be sent to the tour guide");
        ArrayList<String> listeinterets = profileragent.profile.getInterests();
        Iterator<String> it = listeinterets.iterator();
        String tosend = new String();
        
        while (it.hasNext()) {
               String s = it.next();
               tosend +=s +";";
        }
        //System.out.println("<" + myAgent.getLocalName() + ">: here is the message: @guide"+tosend);

        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(profileragent.tourGuide);
        message.setContent(tosend);
        myAgent.send(message);        
    }
}
