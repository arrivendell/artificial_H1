
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
class ReceiveTour extends MsgReceiver {

    ProfilerAgent profileragent;
    
    public ReceiveTour(ProfilerAgent profileragent) { 
        super();
        this.profileragent = profileragent;
    }
    
    @Override
    public void action() {
        //System.out.println("<" + myAgent.getLocalName() + ">: HANDLE MESSAGE");
        ACLMessage msg = myAgent.blockingReceive(MessageTemplate.MatchSender(profileragent.tourGuide));
        
        if (msg != null) {
            System.out.println("<" + myAgent.getLocalName() + ">: Message received, with the list of the artworks of tour");
        
            String received = msg.getContent();
            
            int j=0;
            int length = received.length();
            
            for(String s : Arrays.asList(received.split(";")))
            {
                    //System.out.println(s);
                    profileragent.tour.add(s);
            }
        }
        else {
             System.out.println("<" + myAgent.getLocalName() + ">: Message NULL");
            block();
        }
    }    
    @Override
    public boolean done() {
        return true;
    }
}