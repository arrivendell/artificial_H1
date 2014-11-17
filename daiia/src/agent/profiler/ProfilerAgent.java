/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.profiler;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.SequentialBehaviour;
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
        
        
        ArrayList<String> interests = new ArrayList<>();
        interests.add("photography");
        interests.add("20th century");
        interests.add("religion");
        interests.add("middle-age");
        profile = new Profile("Michel", 46, "teacher", "Male", interests);
       // SearchingMuseum();
        RequestTour();
        
        
    }
        private void SearchingMuseum(){
            
            addBehaviour(new BrowseTheInternet(this, 1000));
        }
        
        private void RequestInformationArtefact(String museumname, String artefactname){
            
            
            
            //ici ajouter le behaviour qui demande (au curator) les infos sur l'artefact
        }
        
        private void RequestTour(){
            SequentialBehaviour sequencebehaviour = new SequentialBehaviour(this);
            sequencebehaviour.addSubBehaviour(new SendInterests(this));
            DataStore datastore = new DataStore();
            sequencebehaviour.addSubBehaviour(new ReceiveTour(this));
            addBehaviour(sequencebehaviour);
            //ici add behaviour qui envoie les interests au tour guide
        }
    
}
