
package agent.profiler;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author nightwish
 */
public class ProfilerAgent extends Agent {
    
    public AID id = new AID("profiler", AID.ISLOCALNAME);
    public Profile profile;
    public AID curator;    
    public AID tourGuide= new AID("tourguide", AID.ISLOCALNAME);

    public ArrayList<String> museums = new ArrayList<String>();
    public ArrayList<String> tour = new ArrayList<String>();
    
    @Override
    protected void setup() {
        // Printout a welcome message
        System.out.println("Hallo! ProfilerAgent"+ getAID().getName()+" is ready.");
                        
        InitialiseProfile();
        
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("tour proposal");
        dfd.addServices(sd);
        SearchConstraints sc = new SearchConstraints();

        send(DFService.createSubscriptionMessage(this, getDefaultDF(), dfd, sc));
        System.err.format("New subscribtion to : %s \r\n", sd.getType());
        addBehaviour( new SubscriptionInitiator( this, 
            DFService.createSubscriptionMessage( this, getDefaultDF(),dfd, null)) 
           {
                protected void handleInform(ACLMessage inform) {
                    try {
                        DFAgentDescription[] dfds = DFService.decodeNotification(inform.getContent()); 
                        int d=0;
                        for(DFAgentDescription dfad : dfds){
                            System.out.format("<%s>: **** Notification : a new subscirbed service had been added by : %s \r\n" , getLocalName(),d,  dfad.getName());
                        }
                        //do something with dfds
                    }
              catch (FIPAException fe) {fe.printStackTrace(); }
            }
            });

        RequestTour();
                
    }
    
    
    /*private void SearchingMuseum(){

        addBehaviour(new BrowseTheInternet(this, 1000));
    }*/

    /*private void StartVisitAndRequestInformationArtefact(String museumname, String artefactname){

        System.out.println(this.tour.toString());
        addBehaviour(new TickerAskInfo(this, 2000, tour));

    }*/

    private void InitialiseProfile(){
        ArrayList<String> interests = new ArrayList<>();
        interests.add("photography");
        interests.add("20th century");
        interests.add("religion");
        interests.add("middle-age");
        profile = new Profile("Michel", 46, "teacher", "Male", interests);
    }
    private void RequestTour(){

        WakerBehaviour searchForService = new WakerBehaviour(this, 5000) {
            @Override
            protected void onWake(){
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("tour proposal");
                dfd.addServices(sd);
                DFAgentDescription[] result = null;
                try {
                    result = DFService.search(myAgent, dfd);
                    if (result.length == 0) {
                        System.out.println("<" + getLocalName() + ">: **** No services tour proposal has been found");   
                    }
                    else {
                        int d = 0;
                        for(DFAgentDescription dfad : result){
                            System.out.format("<%s>: **** Service %d found : %s \r\n" , getLocalName(),d,  dfad.getName());
                            d++;
                        }

                        int agentIndex ;
                        Scanner input = new Scanner( System.in );
                        System.out.println("-----------------------------------------------------------------------");
                        System.out.println("Enter the agent you want to contact using the number given in the list");
                        agentIndex = input.nextInt();//Try catch, to do if time
                        while (agentIndex >= result.length){
                            System.out.format("Error, the index %d is not in the list, please try again now \r\n", agentIndex);
                            agentIndex = input.nextInt();
                        }
                        System.out.format("You choose : %s \r\n", result[agentIndex]);
                        tourGuide = result[agentIndex].getName();
                        System.out.println("-----------------------------------------------------------------------");

                    }
                }
                catch (FIPAException fe) {
                    fe.printStackTrace();
                }
            }
        };

        SequentialBehaviour sequencebehaviour = new SequentialBehaviour(this){
            @Override
            public int onEnd(){
                reset();
                myAgent.addBehaviour(this);
                return super.onEnd();                            
            }
        };
        sequencebehaviour.addSubBehaviour(searchForService);            
        sequencebehaviour.addSubBehaviour(new SendInterests(this));            
        ReceiveTour rt = new ReceiveTour(this);
        
        sequencebehaviour.addSubBehaviour(rt);
        sequencebehaviour.addSubBehaviour(new TickerAskInfo(this, 6000, tour));
        addBehaviour(sequencebehaviour);
    }
    
}
