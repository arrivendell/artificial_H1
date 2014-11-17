/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.profiler;

import jade.core.AID;
import jade.core.Agent;
import java.util.ArrayList;

/**
 *
 * @author nightwish
 */
public class ProfilerAgent extends Agent {
    
    public AID id = new AID("profiler", AID.ISLOCALNAME);
    private Profile profile;
    public ArrayList<String> museums = new ArrayList<String>();

    
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

        
        
    }
        private void SearchingTheWeb(){
            
            //ici add du behaviour cyclic pour recherche sur internet
        }
        
        private void RequestInformationArtefact(String museumname, String artefactname){
            
            //ici ajouter le behaviour qui demande (au curator) les infos sur l'artefact
        }
        
        private void RequestTour(){
            
            //ici add behaviour qui envoie les interests au tour guide
        }
    
}
