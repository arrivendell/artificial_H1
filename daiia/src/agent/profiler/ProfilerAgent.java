/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.profiler;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;

/**
 *
 * @author nightwish
 */
public class ProfilerAgent extends Agent {
    
    public AID id = new AID("profiler", AID.ISLOCALNAME);
    public Profile profile;
    public AID curator;
    public ArrayList<String> museums = new ArrayList<String>();
    public ArrayList<String> tour = new ArrayList<String>();
    
    @Override
        protected void setup() {
        // Printout a welcome message
        System.out.println("Hallo! ProfilerAgent"+ getAID().getName()+" is ready.");
        
        
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("proposition de tour");
        dfd.addServices(sd);
        DFAgentDescription[] result = null;
        try {
            result = DFService.search(this, dfd);
            if (result.length>0) 
                System.out.println("<" + getLocalName() + ">: Service trouvé : " + result[0].getName());
            else
                System.out.println("<" + getLocalName() + ">: Aucun service proposition de tour n'a été trouvé " + result[0].getName());                
            } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        
        
        
        
        ArrayList<String> interests = new ArrayList<>();
        interests.add("photography");
        interests.add("20th century");
        interests.add("religion");
        interests.add("middle-age");
        profile = new Profile("Michel", 46, "teacher", "Male", interests);
       // SearchingMuseum();
        RequestTour();
        /*OneShotBehaviour startVisit = new OneShotBehaviour() {

            @Override
            public void action() {
                StartVisitAndRequestInformationArtefact("","");
            }
        };
        addBehaviour(startVisit);*/
                
    }
        private void SearchingMuseum(){
            
            addBehaviour(new BrowseTheInternet(this, 1000));
        }
        
        private void StartVisitAndRequestInformationArtefact(String museumname, String artefactname){
            
            System.out.println(this.tour.toString());
            addBehaviour(new TickerAskInfo(this, 2000, tour));
            
            //ici ajouter le behaviour qui demande (au curator) les infos sur l'artefact
        }
        
        private void RequestTour(){
            SequentialBehaviour sequencebehaviour = new SequentialBehaviour(this);
            sequencebehaviour.addSubBehaviour(new SendInterests(this));            
            ReceiveTour rt = new ReceiveTour(this);
            //rt.setTemplate(mt);
            sequencebehaviour.addSubBehaviour(rt);
            sequencebehaviour.addSubBehaviour(new TickerAskInfo(this, 6000, tour));
            addBehaviour(sequencebehaviour);
            
            //ici add behaviour qui envoie les interests au tour guide
        }
    
}
