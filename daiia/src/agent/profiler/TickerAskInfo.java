package agent.profiler;

import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Simulation of a "tour" where a guide switch from one artwork to another every period
 * @author Nabil
 */
class TickerAskInfo extends TickerBehaviour {
    
    private ProfilerAgent profileragent;
    private ArrayList<String> tour = new ArrayList<>();
    private String temp = new String();
    private int counterTour = 0;
    
    public TickerAskInfo(ProfilerAgent profileragent, long period, ArrayList<String> tour) {
        super(profileragent, period);
        this.profileragent = profileragent;
        this.tour = tour;
       // System.out.println("CONSTRUCTOR TICKERASKINFO ");
    }
    
    
    protected void onTick(){
        if (counterTour < this.tour.size()){
            System.out.println("<" + myAgent.getLocalName() + ">: ** Arriving in front of a new artwork");
            temp = this.tour.get(counterTour);
            SequentialBehaviour sequencebehaviour = new SequentialBehaviour(this.profileragent);
            sequencebehaviour.addSubBehaviour(new SendNameArtefact(this.profileragent, temp));
            sequencebehaviour.addSubBehaviour(new ReceiveInfoArtefact(this.profileragent));
            this.profileragent.addBehaviour(sequencebehaviour);
            counterTour++;
        }
        else {
            System.out.println("<" + myAgent.getLocalName() + ">: ** The tour is over !");
            this.stop();
        }
        
    }
}
