/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.profiler;

import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.TickerBehaviour;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Nabil
 */
class TickerAskInfo extends TickerBehaviour {
    
    private ProfilerAgent profileragent;
    private ArrayList<String> tour = new ArrayList<>();
    private Iterator<String> it = tour.iterator();
    private String temp = new String();

    public TickerAskInfo(ProfilerAgent profileragent, long period, ArrayList<String> tour) {
        super(profileragent, period);
        this.profileragent = profileragent;
        this.tour = new ArrayList<>(tour);
        this.it = this.tour.iterator();
    }
    
    
    protected void onTick(){
        if (it.hasNext()){
            System.out.println("<" + myAgent.getLocalName() + ">: On arrive devant un nouvel artefact");

            temp = it.next();
            SequentialBehaviour sequencebehaviour = new SequentialBehaviour(this.profileragent);
            sequencebehaviour.addSubBehaviour(new SendNameArtefact(this.profileragent, temp));
            sequencebehaviour.addSubBehaviour(new ReceiveInfoArtefact(this.profileragent));
            this.profileragent.addBehaviour(sequencebehaviour);
        }
        System.out.println("<" + myAgent.getLocalName() + ">: Il n'y a plus rien à visiter dans ce tour");
        
    }
}
