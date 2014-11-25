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
    AID whomToSend = new AID();

    @Override
    protected void setup() {
        // Printout a welcome message
        System.out.println("Hallo! CuratorAgent"+ getAID().getName()+" is ready.");
        initialiseCatalog();
      
        // The curator messages handle is done in a FSM behavior
        FSMBehaviour fsm = new FSMBehaviour(this){
            @Override
            public int onEnd() {
                reset();
                myAgent.addBehaviour(this);
                //System.out.format("<%s> Fsm exit \r\n", myAgent.getLocalName());
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

        //System.out.println("<curator> adding fsm");

        addBehaviour(fsm);
    }
    
    //Initialisation of the catalog with some French artwork.
    private void initialiseCatalog(){
        catalog.add(new ArtWork("Guernica", "a beautiful paint ","Picasso", "painting;20th century;nature",1000));            
        catalog.add(new ArtWork("La liberte guidant le peuple", "a beautiful painting, again !","Delacroix", "war;freedom;nature",1000));            
        catalog.add(new ArtWork("Le baiser", "an awesome picture ","Doisneau", "photography;love;20th century",1000));            
        catalog.add(new ArtWork("La montagne sainte victoire", "a splendid painting ","Cezanne", "painting;nature;mountain;france",1000));            
        catalog.add(new ArtWork("La joconde", "a excellent painting ","De Vinci", "painting;legend;invention;portrait",1000));            
        catalog.add(new ArtWork("The Adoration of the Magi", "a sacral painting ","Paolo di Grazia)", "painting;religion;christian;",1000));            

    };
        
    //Initial state of the FSM, consisting in receiving messages. Blocks behavior until receiving message
    private class initialReceiveBehaviour extends OneShotBehaviour {
        int result = 0;

        @Override 
        public void action() {
            //System.out.println("<curator> Initial state");

            msgReceived = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));

            if (msgReceived != null) {
                msgToSend = "";

                System.out.println("<curator> Message received");

                final String name = msgReceived.getContent();
                String senderName = msgReceived.getSender().getLocalName();
                //System.out.format("sender : <%s> \r\n", senderName);
                whomToSend = new AID(senderName, AID.ISLOCALNAME);


                if( senderName.contains("profiler") ) {
                    result = 1;
                    System.out.format("<curator> Profiler requested details of %s \r\n", msgReceived.getContent());
                } 
                else if (senderName.contains("tourguide")){
                    result = 2;
                    System.out.format("<curator> Profiler requested catalog with interest of %s \r\n ", msgReceived.getContent());
                }
                else {
                    System.out.println("<curator> Message unknown received");
                    result = 0;
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

    /** In case of receiving a message from an agent whose name contains 
    *   "tourguide", we search for a subCatalog of Artwork with tags matching the 
    *   interests of the user.
    */
    private class getCatalogBehavior extends OneShotBehaviour {
        int result = 0;

        @Override 
        public void action() {
            System.out.println("<curator> getting catalog");
            String interests = msgReceived.getContent();
            ArrayList<ArtWork> catalogToSend = new ArrayList<>();
            List<String> listInterests;
            if (interests.length() != 0) {
                listInterests  = parseInterest(interests);
            }
            else {
                listInterests = new ArrayList<>();
            }

            for (ArtWork aw : catalog ){
                List<String> common = new ArrayList<>(aw.getTags());
                common.retainAll(listInterests);
                if (common.size() > 0) {
                    catalogToSend.add(aw);
                }
            }

            if (catalogToSend.size() == 0){
                result = 9;

            }
            else {
                result = 12;
                for (ArtWork aw : catalogToSend){
                    msgToSend += aw.getName()+ ";";
                }
            }
        }

        public int onEnd(){
            //System.out.format("<getCatalogBehavior> ended with transition %d \r\n", result);
            return result;
        }
    }
    
    /**
     * Hee, profiler asks us to detail an Artwork, given a name.
     */
    private class getDetailsBehavior extends OneShotBehaviour {
        int result = 0;

        @Override 
        public void action() {
            System.out.println("<curator> getting details");
            String nameArtWork = msgReceived.getContent();

            ArtWork toFind = new ArtWork(nameArtWork);
            if (catalog.contains(toFind)) {
                result = 10;
                msgToSend = catalog.get(catalog.indexOf(toFind)).toString();
            }
            else {
                result = 11;
            }

        }

        public int onEnd(){
            System.out.format("<getDetailsBehavior> ended with transition %d\r\n", result);
            return result;
        }
    }
    /**
     * Sending the catalog to the tourguide who asked us the catalog.
     */
    private class sendCatalogBehaviour extends OneShotBehaviour{
        @Override 
        public void action() {
            System.out.println("<curator> send catalog");

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent(msgToSend);
            msg.addReceiver(whomToSend);
            send(msg);

        }

    }
    /**
     * Sending the details to the profiler who asked us the details.
     */
    private class sendDetailsBehavior extends OneShotBehaviour{
        @Override 
        public void action() {
            System.out.println("<curator> send details");

            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent(msgToSend);
            msg.addReceiver(whomToSend);
            send(msg);
        }
    }
    /**
     * In case we don't find any artwork, we consider it as "not_understood"
     */
    private class sendNUProfilerBehavior extends OneShotBehaviour{
        @Override 
        public void action() {
            System.out.println("<curator> send not understood");

            ACLMessage msg = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
            msg.addReceiver(whomToSend);
            send(msg);
        }
    }
    /**
     * In case we don't find any artwork, we consider it as "not_understood"
     */
    private class sendNUTourguideBehavior extends OneShotBehaviour{
        @Override 
        public void action() {
            System.out.println("<curator> send not understood");
            ACLMessage msg = new ACLMessage(ACLMessage.NOT_UNDERSTOOD);
            msg.addReceiver(whomToSend);
            send(msg);
        }
    }
    
    /**
     * Parse the string interests from "interest1;interest2;...." to a list
     * @param interests
     */
    private List<String> parseInterest(String interests){
        return Arrays.asList(interests.split(";"));
    }
}
