/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.profiler;

import jade.core.behaviours.WakerBehaviour;


/**
 *
 * @author Nabil
 */
class BrowseTheInternet extends WakerBehaviour {

    
    private ProfilerAgent profileragent;
    
    public BrowseTheInternet(ProfilerAgent profileragent, long timeout) {
        super(profileragent, timeout);
        this.profileragent = profileragent;
    }
    
    @Override
    protected void onWake(){
        
        
    }
    
}
