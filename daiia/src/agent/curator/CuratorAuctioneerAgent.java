/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.curator;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
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
public class CuratorAuctioneerAgent  extends Agent {
    ACLMessage msgReceived;
    ArrayList<ArtWork> catalog= new ArrayList<>();
    String msgToSend = "";
    AID whomToSend = new AID();
    ArtWork artWorkSelling;
    
    
    @Override
    protected void setup() {
    // Printout a welcome message
    System.out.println("Hallo! CuratorAuctioneerAgent"+ getAID().getName()+" is ready.");
    initialiseCatalog();
    
        addBehaviour(new TickerBehaviour(this, 20000) {

        @Override
        public void onTick() {
            launchAuction();
        }
    });
       
    
}
  private void initialiseCatalog(){
        catalog.add(new ArtWork("Guernica", "a beautiful paint ","Picasso", "painting;20th century;nature",120000));            
        catalog.add(new ArtWork("La liberte guidant le peuple", "a beautiful painting, again !","Delacroix", "war;freedom;nature",80000));            
        catalog.add(new ArtWork("Le baiser", "an awesome picture ","Doisneau", "photography;love;20th century",45000));            
        catalog.add(new ArtWork("La montagne sainte victoire", "a splendid painting ","Cezanne", "painting;nature;mountain;france",70000));            
        catalog.add(new ArtWork("La joconde", "a excellent painting ","De Vinci", "painting;legend;invention;portrait",500000));            
        catalog.add(new ArtWork("The Adoration of the Magi", "a sacral painting ","Paolo di Grazia)", "painting;religion;christian;",250000));            

    };
      
  private void launchAuction(){
       DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("buyer");
                dfd.addServices(sd);
                DFAgentDescription[] result = null;
                try {
                    result = DFService.search(this, dfd);
                    if (result.length == 0) {
                        System.out.println("<" + getLocalName() + ">: **** No services buyer has been found");   
                    }
                    else {
                        int d = 0;
                        for(DFAgentDescription dfad : result){
                            System.out.format("<%s>: **** Service found : %s \r\n" , getLocalName(),  dfad.getName());
                            d++;
                        }
                        for(ArtWork aw  : catalog){
                            System.out.format("<%s>: **** Artwork %d  : %s \r\n" , getLocalName(),catalog.indexOf(aw),  aw.getName());
                            d++;
                        }

                        int awIndex ;
                        Scanner input = new Scanner( System.in );
                        System.out.println("-----------------------------------------------------------------------");
                        System.out.println("Enter the artWork you want to sell using the number given in the list");
                        awIndex = input.nextInt();//Try catch, to do if time
                        while (awIndex >= catalog.size()){
                            System.out.format("Error, the index %d is not in the list, please try again now \r\n", awIndex);
                            awIndex = input.nextInt();
                        }
                        System.out.format("You choose artwork n %s \r\n", catalog.get(awIndex));
                        artWorkSelling = catalog.get(awIndex);
                        System.out.println("-----------------------------------------------------------------------");
                        
                        addBehaviour(new CreateAuctionBehavior(artWorkSelling, result,3000));
                    }
                }
                catch (FIPAException fe) {
                    fe.printStackTrace();
                }
  }
}
