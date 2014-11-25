/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.curator;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import static java.lang.Math.round;

/**
 *
 * @author nightwish
 */
public class CreateAuctionBehavior extends Behaviour{
    
    static final double stepPrice = 0.90;
    ArtWork _artworkToSell;
    DFAgentDescription[] _listBuyers;
    boolean _hasEnded = false;
    boolean _isSold = false;
    long _thresholdPrice = 10;
    int _state = 0;
    ACLMessage _messageTemplate = new ACLMessage();
    long _timeOut =2000;
    long _currentPrice=0;
    
    public CreateAuctionBehavior(ArtWork artWorkSelling, DFAgentDescription[] listBuyers, long timeOut){
        _artworkToSell = artWorkSelling;
        _listBuyers = listBuyers;
        _thresholdPrice = artWorkSelling.getPrice();
        System.out.println(listBuyers);
        for (DFAgentDescription dfad : _listBuyers){
            _messageTemplate.addReceiver(dfad.getName());
        }
        _timeOut=timeOut;
    }
    
    @Override
    public void action(){
        switch (_state){
            //sending the information of auction starting
            case 0:
                System.out.println("**** Starting the auction");
                _messageTemplate.setPerformative(ACLMessage.INFORM);
                _state = 1;
                _messageTemplate.setContent(String.format("START_AUCTION#%s", _artworkToSell.getName()));
                myAgent.send(_messageTemplate);
                _currentPrice =_artworkToSell.getPrice()*3;
                break;
            
                
            case 1:
                _messageTemplate.setPerformative(ACLMessage.CFP);
                _messageTemplate.setContent(Long.toString(_currentPrice));
                
                System.out.format("Sending cfp for %d \r\n", _currentPrice);
                _state = 2;
                myAgent.send(_messageTemplate);
               break;
                
            case 2:
                //System.out.println("Adding waker behavior");
                myAgent.addBehaviour(new OneShotBehaviour() {
                    @Override
                    public void action(){
                        ACLMessage msg ;
                        boolean queueEnded = false;
                        
                        while(!(queueEnded)){
                            msg = myAgent.blockingReceive(MessageTemplate.MatchAll(),2000);
                            if (msg!=null){
                                System.out.println("message received from buyer");
                                if (msg.getPerformative()==ACLMessage.PROPOSE){
                                    ACLMessage msgReply = msg.createReply();
                                    if(!_isSold){
                                        System.out.format("**** SomeOne has agreed to buy the artWork at %d \r\n", _currentPrice);
                                        _isSold = true;
                                        msgReply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                                        msgReply.setContent(Long.toString(_currentPrice));
                                        myAgent.send(msgReply);
                                    }
                                    else {
                                        msgReply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                                        msgReply.setContent(Long.toString(_currentPrice));
                                        myAgent.send(msgReply);
                                    }
                                   
                                }
                                else if (msg.getPerformative()==ACLMessage.NOT_UNDERSTOOD){
                                    _messageTemplate.removeReceiver(msg.getSender());
                                }

                            }
                            else {
                                queueEnded = true;
                            }
                        }
                        if (_isSold){
                            //We end the auction
                            _state=5;
                        }
                        else {
                            //We reduce the price
                            _currentPrice = round(_currentPrice*stepPrice);
                            System.out.format(" **** Lowering the price to %d \r\n", _currentPrice);
                            if (_currentPrice < _thresholdPrice)
                            {
                                //We end the auction
                                _state = 5;
                            }
                            else{
                                _state = 1;
                            }
                            
                        }
                        
                    }
                });
                break;
            
            //ENDING
            case 5:
                _hasEnded = true;
                _messageTemplate.setPerformative(ACLMessage.INFORM);
                if(_isSold){
                    System.out.println("**** ArtWork has been sold, auction ends"); 
                }
                else{
                    System.out.println("**** Not sold, Auction ends");
                }
                _messageTemplate.setContent("NO_BIDS");
                myAgent.send(_messageTemplate);
                
            default:
                break;
        }
        
        
        
    }
    
    @Override
    public boolean done(){
        return _hasEnded;

    }
    
}
