/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.profiler;

import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;
import java.util.Arrays;

/**
 *
 * @author Nabil
 */
//class ReceiveInfoArtefact extends MsgReceiver {
//    
//    private ProfilerAgent profileragent;
//
//    public ReceiveInfoArtefact(ProfilerAgent profileragent) {
//        this.profileragent = profileragent;
//    }
//    
//    @Override
//    protected void handleMessage(ACLMessage msg) {
//            if (msg == null) {
//                System.out.println("<" + myAgent.getLocalName() + ">: Message NULL");
//            } else {
//            System.out.println("<" + myAgent.getLocalName() + ">: Message recu, contient les infos sur l'artefact");
//        
//            String received = msg.getContent();
//
//            String temp = new String();
//            int j=0;
//            int entry=0;
//            int length = received.length();
//            for(int i = 0; i<=length; i++){
//                if ((received.charAt(i)=='/')||(i==length)){
//                    temp = received.substring(j, i-1);
//                    j = i+1;
//                    switch(entry){
//                        case 0:
//                            System.out.println("<" + myAgent.getLocalName() + ">: Le nom de l'oeuvre est : "+temp);
//                            break;
//                        case 1 : 
//                            System.out.println("<" + myAgent.getLocalName() + ">: L'auteur de l'oeuvre est : "+temp);
//                            break;
//                        case 2 : 
//                            System.out.println("<" + myAgent.getLocalName() + ">: Voici plus de détails : "+temp);
//                            break;
//                        default : 
//                            System.out.println("<" + myAgent.getLocalName() + ">: ERROR DANS LE NB D'ENTRY");
//                            break;
//                    }
//                    entry++;
//                }
//            }
//            }     
//    }
//    
//}

class ReceiveInfoArtefact extends OneShotBehaviour{
    
    private ProfilerAgent profileragent;
    public ReceiveInfoArtefact(ProfilerAgent profileragent) {
        this.profileragent = profileragent;
    }
    
    @Override
    public void action() {
            
        System.out.println("<" + myAgent.getLocalName() + ">: On est dans HANDLE MESSAGE");
        
        ACLMessage msg = myAgent.blockingReceive(MessageTemplate.MatchSender(new AID("curator", AID.ISLOCALNAME)));
        
        if (msg != null) {
            System.out.println("<" + myAgent.getLocalName() + ">: Message recu, contient les infos sur l'artefact");
        
            String received = msg.getContent();
            
            int entry=0;
            //int length = received.length();
            
            for(String s : Arrays.asList(received.split("/")))
            {
                    //System.out.println(s);
                    switch(entry){
                        case 0:
                            System.out.println("<" + myAgent.getLocalName() + ">: Le nom de l'oeuvre est : "+s);
                            break;
                        case 1 : 
                            System.out.println("<" + myAgent.getLocalName() + ">: L'auteur de l'oeuvre est : "+s);
                            break;
                        case 2 : 
                            System.out.println("<" + myAgent.getLocalName() + ">: Voici plus de détails : "+s);
                            break;
                        default : 
                            System.out.println("<" + myAgent.getLocalName() + ">: ERROR DANS LE NB D'ENTRY");
                            break;
                    }
                    entry++;
            }
        }
        else {
             System.out.println("<" + myAgent.getLocalName() + ">: Message NULL");
            block();
        }
        
        
    }
    
    
    
}
