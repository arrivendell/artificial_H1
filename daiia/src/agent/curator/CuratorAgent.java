/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.curator;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.FSMBehaviour;
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
public class CuratorAgent extends Agent {
        ACLMessage msgReceived;
        ArrayList<ArtWork> catalog= new ArrayList<>();
        String msgToSend = "";
        
        
        @Override
        protected void setup() {
        // Printout a welcome message
        System.out.println("Hallo! CuratorAgent"+ getAID().getName()+" is ready.");
        initialiseCatalog();
        FSMBehaviour fsm = new FSMBehaviour(this){
            @Override
            public int onEnd() {
                System.out.println("Fsm exit");
                return super.onEnd();
            }
        };
        
        //State definitions
        fsm.registerFirstState(new initialReceiveBehaviour(), "Initial");
        fsm.registerState(new getCatalogBehavior(), "getcatalog");
        fsm.registerState(new getDetailsBehavior(), "getdetails");
        fsm.registerLastState(new sendCatalogBehaviour(), "sendcatalog");
        fsm.registerLastState(new sendDetailsBehavior(), "senddetails");
        fsm.registerLastState(new sendNUProfilerBehavior(), "sendnuprofiler");
        fsm.registerLastState(new sendNUTourguideBehavior(), "sendnutourguide");
        
        //transitions definition
        fsm.registerTransition("Initial", "getcatalog", 2);
        fsm.registerTransition("Initial", "getdetails", 1);
        fsm.registerTransition("getcatalog", "sendcatalog", 12);
        fsm.registerTransition("getcatalog", "sendnutourguide", 9);
        fsm.registerTransition("getdetails", "senddetails", 10);
        fsm.registerTransition("getdetails", "sendnuprofiler", 11);
        fsm.registerTransition("Initial", "Initial", 0);
        
        System.out.println("<curator> adding fsm");
                
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                myAgent.addBehaviour(fsm);
            }
        });
        
    }
        
        private void initialiseCatalog(){
            catalog.add(new ArtWork("Guernica", "a beautiful paint ","Picasso", "painting;20th century;nature"));            
            catalog.add(new ArtWork("La liberte guidant le peuple", "a beautiful painting, again !","Delacroix", "war;freedom;nature"));            
            catalog.add(new ArtWork("Le baiser", "an awesome picture ","Doisneau", "photography;love;20th century"));            
            catalog.add(new ArtWork("La montagne sainte victoire", "a splendid painting ","Cezanne", "painting;nature;mountain;france"));            
            catalog.add(new ArtWork("La joconde", "a excellent painting ","De Vinci", "painting;legend;invention;portrait"));            
            catalog.add(new ArtWork("The Adoration of the Magi", "a sacral painting ","Paolo di Grazia)", "painting;religion;christian;"));            

        };
        
        
        private class initialReceiveBehaviour extends OneShotBehaviour {
            int result = 0;
            
            @Override 
            public void action() {
                System.out.println("<curator> Initial state");
                
                msgReceived = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                
                if (msgReceived != null) {
                    System.out.println("<curator> Message received");
                    
                    final String name = msgReceived.getContent();
                    String senderName = msgReceived.getSender().getLocalName();
                    System.out.format("sender : <%s>", senderName);
                    if( senderName == "profiler" ) {
                        result = 1;
                        System.out.format("<curator> Profiler requested details of %s", msgReceived.getContent());
                    } 
                    else if (senderName == "tourguide"){
                        result = 2;
                        System.out.format("<curator> Profiler requested catalog with interest of %s", msgReceived.getContent());
                    }
                    else {
                        System.out.println("<curator> Message unknown received");
                        result = 0;
                    }

                }
                else {
                    block();
                }
            }
            
            public int onEnd(){
                System.out.format("<initialReceiveBehaviour> ended with transition %d ", result);
                return result;
            }
        }
        
        
        private class getCatalogBehavior extends OneShotBehaviour {
            int result = 0;
            
            @Override 
            public void action() {
                System.out.println("<curator> get catalog");
                String interests = msgReceived.getContent();
                ArrayList<ArtWork> catalogToSend = new ArrayList<>();
                List<String> listInterests = parseInterest(interests);
                
                for (ArtWork aw : catalog ){
                    List<String> common = new ArrayList<>(aw.getTags());
                    common.retainAll(listInterests);
                    if (common.size() > 0) {
                        catalogToSend.add(aw);
                    }
                }
                
                if (catalogToSend.size() == 0){
                    result = 9;
                    for (ArtWork aw : catalogToSend){
                        msgToSend += aw.toString() + ";";
                    }
                }
                else {
                    result = 12;
                }
            }
            
            public int onEnd(){
                System.out.format("<getCatalogBehavior> ended with transition %d", result);
                return result;
            }
        }
        
        private class getDetailsBehavior extends OneShotBehaviour {
            int result = 0;
            
            @Override 
            public void action() {
                System.out.println("<curator> get details");
                String nameArtWork = msgReceived.getContent();
                            
                if (catalog.contains(new ArtWork(nameArtWork))) {
                    result = 10;
                }
                else {
                    result = 11;
                }

            }
            
            public int onEnd(){
                System.out.format("<getDetailsBehavior> ended with transition %d", result);
                return result;
            }
        }
            
        private class sendCatalogBehaviour extends OneShotBehaviour{
            @Override 
            public void action() {
                System.out.println("<curator> send catalog");
                
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent(msgToSend);
                msg.addReceiver(new AID("tourguide", AID.ISLOCALNAME));
                send(msg);

            }
            
        }
               
        private class sendDetailsBehavior extends OneShotBehaviour{
            @Override 
            public void action() {
                System.out.println("<curator> send details");
                
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent(msgToSend);
                msg.addReceiver(new AID("profiler", AID.ISLOCALNAME));
                send(msg);
            }
        }
        
        private class sendNUProfilerBehavior extends OneShotBehaviour{
            @Override 
            public void action() {
                System.out.println("<curator> send not understood");
                
                ACLMessage msg = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
                msg.addReceiver(new AID("profiler", AID.ISLOCALNAME));
                send(msg);
            }
        }
        private class sendNUTourguideBehavior extends OneShotBehaviour{
            @Override 
            public void action() {
                System.out.println("<curator> send not understood");
                ACLMessage msg = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
                msg.addReceiver(new AID("tourgudie", AID.ISLOCALNAME));
                send(msg);
            }
        }
        
    private List<String> parseInterest(String interests){
        return Arrays.asList(interests.split("#")[1].split(";"));
    }
}
