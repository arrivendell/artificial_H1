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
        ACLMessage msg = myAgent.receive(MessageTemplate.MatchSender(new AID("profiler", AID.ISLOCALNAME)));
        
        if (msg != null) {
            final String interests = msg.getContent();
            myAgent.addBehaviour(new OneShotBehaviour() {

                @Override
                public void action() {
                    ACLMessage msgToSend = new ACLMessage(ACLMessage.REQUEST);
                    msgToSend.setContent(interests);
                    msgToSend.addReceiver(new AID("curator", AID.ISLOCALNAME));
                    System.out.printf("Sending message %s to curator", interests);
                    myAgent.send(msgToSend);
                }
            });
        }
        else {
            block();
        }
    }
    

    
}
