
package agent.profiler;

import jade.core.behaviours.WakerBehaviour;


/**
 * Here to simulate an agent searching for museum on the internet and getting one
 * to communicate with.
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
