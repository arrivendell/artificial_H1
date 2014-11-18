package agent.profiler;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/**
 *
 * @author Nabil
 */
class SendNameArtefact extends OneShotBehaviour {
    
    private String nameArtefact = new String();
    private ProfilerAgent profileragent;

    public SendNameArtefact(ProfilerAgent profileragent, String nameArtefact) {
        this.profileragent = profileragent;
        this.nameArtefact = nameArtefact;
    }

    @Override
    public void action() {
        System.out.println("<" + myAgent.getLocalName() + ">: Artwork name sending to the curator");
        //System.out.println("<" + myAgent.getLocalName() + ">:here is the message: @curator "+nameArtefact);
        ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(new AID("curator", AID.ISLOCALNAME));
        message.setContent(nameArtefact);
        myAgent.send(message);
    }

}
