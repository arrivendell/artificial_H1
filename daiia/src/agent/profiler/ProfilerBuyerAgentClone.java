/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.profiler;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Location;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Nabil
 */
public class ProfilerBuyerAgentClone extends Agent {
    
    public AID id = getAID();
    public Profile profile;
    public AID curator;    
    private long _lowPrice = 0;
    Random rand = new Random();
    ACLMessage _messageReceived;
    Location l;
    
    @Override
    protected void setup() {
        // Printout a welcome message
        System.out.println("Hallo! ProfilerBuyerAgent"+ getAID().getName()+" is ready.");
                        
        InitialiseProfile();
        l = this.here();
        doClone(l, "clone1");
        doClone(l, "clone2");
        
        
        
         
        
        
        
        
        
                
    }
    
    void init(){
        
        FSMBehaviour fsm = new FSMBehaviour(this){
            @Override
            public int onEnd() {
                reset();
                myAgent.addBehaviour(this);
                //System.out.format("<%s> Fsm exit \r\n", myAgent.getLocalName());
                return super.onEnd();
            }
        };
        
        
        _lowPrice = rand.nextInt((500000 - 50000) + 1) + 50000;

        //State definitions
        fsm.registerFirstState(new initialReceiveBehaviour(), "Initial");
        fsm.registerState(new CFPWaitBehavior(), "waitCFP");
        fsm.registerState(new HandleCFPBehavior(), "handleCFP");
        fsm.registerLastState(new EndBehavior(), "end");
       
        //transitions definition
        fsm.registerTransition("Initial", "Initial", 0);
        fsm.registerTransition("Initial", "waitCFP", 1);
        fsm.registerTransition("waitCFP", "end", 10);
        fsm.registerTransition("waitCFP", "handleCFP", 3);
        fsm.registerTransition("handleCFP", "end", 10);        
        fsm.registerTransition("handleCFP", "handleCFP", 3);

        fsm.registerTransition("waitCFP", "Initial", 0);
        fsm.registerTransition("waitCFP", "waitCFP", 1);
        
        addBehaviour(fsm);
        
        
    }
    
    protected void afterClone() {
// ----------------------------
        //register buyer services
        DFAgentDescription dfd = new DFAgentDescription();
        
        dfd = new DFAgentDescription();
        dfd.setName(id);
        ServiceDescription sd = new ServiceDescription();
        sd.setType("buyer");
        sd.setName(getLocalName());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        
	 init();
   }

    

    private void InitialiseProfile(){
        ArrayList<String> interests = new ArrayList<>();
        interests.add("photography");
        interests.add("20th century");
        interests.add("religion");
        interests.add("middle-age");
        profile = new Profile("Michel", 46, "teacher", "Male", interests);
    }
    
    private class initialReceiveBehaviour extends OneShotBehaviour{
        int result = 0;

        @Override 
        public void action() {
            //System.out.println("<profiler> Initial state");

            ACLMessage msgReceived = myAgent.receive();

            if (msgReceived != null) {
                if (msgReceived.getPerformative()== ACLMessage.INFORM){
                    if (msgReceived.getContent().startsWith("START_AUCTION")){
                        System.out.println("Curator sent begin of Auction");
                        result = 1;
                    }
                }

            }
            else {
                result=0;
                block();
            }
    }    
        
        public int onEnd(){
            //System.out.format("<initialReceiveBehaviour> ended with transition %d \r\n", result);
            return result;
        }
   
        }
     private class CFPWaitBehavior extends OneShotBehaviour{
        int result = 0;

        @Override 
        public void action() {

            ACLMessage msgReceived = myAgent.receive();

            if (msgReceived != null) {
                if (msgReceived.getPerformative()== ACLMessage.CFP){
                    System.out.println("cfp received");
                    _messageReceived = msgReceived;
                    if (Integer.parseInt(msgReceived.getContent())<_lowPrice){
                        ACLMessage msgReply = msgReceived.createReply();
                        msgReply.setPerformative(ACLMessage.PROPOSE);
                        myAgent.send(msgReply);
                        result = 3;
                    }
                    else {
                        result = 1;
                    }
                }
                else if(msgReceived.getPerformative() == ACLMessage.INFORM) {
                    if (msgReceived.getContent().startsWith("NO_BIDS")){
                         //System.out.println("The Auction has been stopped");
                         result = 10;
                    }
                }

            }
            else {
                result=1;
                block();
            }
        }    

            public int onEnd(){
                //System.out.format("<initialReceiveBehaviour> ended with transition %d \r\n", result);
                return result;
            }
        }
    
    private class HandleCFPBehavior extends OneShotBehaviour{
        int result = 0;

        @Override 
        public void action() {
            //System.out.println("<profiler> Initial state");
            ACLMessage msgReceived = myAgent.receive();
            
            if (msgReceived != null) {
                if (msgReceived.getPerformative()== ACLMessage.ACCEPT_PROPOSAL){
                    System.out.format("**** ArtWork is won by Agent %s",myAgent.getLocalName());
                    result = 10;
                }
                else if(msgReceived.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                         System.out.println("**** Offer has been refused :(");
                         result = 10;
                }

            }
            else {
                result=3;
                block();
            }
        }    

            public int onEnd(){
                //System.out.format("<initialReceiveBehaviour> ended with transition %d \r\n", result);
                return result;
            }
    }
    
    private class EndBehavior extends OneShotBehaviour{
        @Override 
        public void action() {
            //System.out.println("<profiler> Initial state");
            System.out.println("Ending  profiler");
        }    
    }
}

    
