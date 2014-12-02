/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agent.queen;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author nightwish
 */
public class QueenAgent extends Agent {
    
    static int RECEIVED_MOVE = 1;
    static int END_OF_CHAIN = 2;
    static int FORWARD_CHAIN = 3;
    static int GO_WAITING = 0;
    static int RECEIVED_BACKTRACK = 4;    
    static int FOUND_FREE_POSITION = 5;
    static int POSITION_NOT_FOUND = 6;
    static int PRINT_CHESS = 7;

    static String EMPTY_POSITION = "empty";
    static String CHESS_SEPARATOR = "#";
    
    private int _sizeChess;
    private boolean _isFirst;
    private boolean _isLast;
    private int _positionLine;
    private int currentPosition = 0;
    private String[] _chess;
    //private AID _follower;
    private String _nextQueen="";
    private AID _previousQueen;
    ACLMessage _lastMessage;
    
    private boolean _chainIsOk = false;
    private boolean _solutionFound = false;
    
    public List<Integer> listPossiblePositions = new ArrayList<Integer>();
    
    @Override
    protected void setup() {
        System.out.format("Hello, queen %s is ready !", getAID().getLocalName() );
        
        
        // We get the parameters
        Object[] args = getArguments();
        if (args != null && args.length > 2){
            _sizeChess = (int) args[0];
            
           _chess = new String[(_sizeChess*_sizeChess)+1]; // We begin the chess at the index 1
           Arrays.fill(_chess, EMPTY_POSITION);//fill with empty positions
           
            _positionLine = (int) args[1];
             if (_positionLine == 1){
                 _isFirst = true;
             }
             else{
                 _isFirst = false;
             }
            if (args[1] != args[0])
            {
                _nextQueen =  (String) args[2];
                 _isLast = false;
            }
            else{
                _nextQueen =  (String) args[2];
                _isLast = true;
            }
           
            //sending first propagation message
            if (_isFirst){
            ACLMessage msgChaining = new ACLMessage(ACLMessage.PROPAGATE);
            msgChaining.addReceiver(new AID(_nextQueen, AID.ISLOCALNAME));
            send(msgChaining);
            }
            
            //Set the state machine
            FSMBehaviour fsm = new FSMBehaviour(this){
                @Override
                public int onEnd() {
                    reset();
                    myAgent.addBehaviour(this);
                    //System.out.format("<%s> Fsm exit \r\n", myAgent.getLocalName());
                    return super.onEnd();
                }
            };




            //State definitions
            fsm.registerFirstState(new intitialWaitingBehaviour(), "Initial");
            fsm.registerState(new HandleBacktrackBehaviour(), "handleBacktrack");
            fsm.registerState(new HandleMoveBehaviour(), "handleMove");
            fsm.registerState(new MoveQueenBehaviour(), "moveQueen");
            fsm.registerState(new BacktrackQueenBehaviour(), "backtrackQueen");
            fsm.registerState(new ForwardChainBehaviour(), "forwardChain");
            fsm.registerState(new InitiateMoveBehaviour(), "initiateMove");
            fsm.registerLastState(new EndBehaviour(), "end");


            //transitions definition
            fsm.registerTransition("Initial", "Initial", GO_WAITING);
            fsm.registerTransition("Initial", "handleBacktrack", RECEIVED_BACKTRACK);
            fsm.registerTransition("Initial", "handleMove", RECEIVED_MOVE);
            fsm.registerTransition("Initial", "forwardChain", FORWARD_CHAIN);
            fsm.registerTransition("Initial", "initiateMove", END_OF_CHAIN);
            fsm.registerTransition("handleMove", "moveQueen", FOUND_FREE_POSITION);
            fsm.registerTransition("handleBacktrack", "moveQueen", FOUND_FREE_POSITION);
            fsm.registerTransition("handleMove", "backtrackQueen", POSITION_NOT_FOUND);
            fsm.registerTransition("handleBacktrack", "backtrackQueen", POSITION_NOT_FOUND);
            fsm.registerTransition("moveQueen", "Initial", GO_WAITING);
            fsm.registerTransition("moveQueen", "end", PRINT_CHESS);
            fsm.registerTransition("backtrackQueen", "Initial", GO_WAITING);  
            
            fsm.registerTransition("forwardChain", "Initial", GO_WAITING);  
            fsm.registerTransition("initiateMove", "Initial", GO_WAITING);  
            fsm.registerTransition("initiateMove", "end", PRINT_CHESS);  
            
            

            addBehaviour(fsm);
        }//endif args
        else{
            doDelete();
        }
        
    
    }
    
    
    private class intitialWaitingBehaviour extends OneShotBehaviour{
        int result = 0;

        @Override 
        public void action() {
            //System.out.println("<queen> Initial state");
            ACLMessage msgReceived = myAgent.receive();

            if (msgReceived != null) {
                _lastMessage = msgReceived;
                switch(msgReceived.getPerformative()){
                    
                    case ACLMessage.PROPAGATE :{
                        _chainIsOk = true;
                        if (_isFirst){
                            result = END_OF_CHAIN;
                        }
                        else {
                            result = FORWARD_CHAIN;
                            
                        
                        }
                        
                    }
                    case ACLMessage.REQUEST:{
                        result = RECEIVED_MOVE;
                    }
                    case ACLMessage.REFUSE:{
                        result = RECEIVED_BACKTRACK;
                    
                    }
                        
                }

            }
            else {
                result=0;
                block();
            }
            
        }
        
        public int onEnd(){
            //System.out.format("<initialReceiveBehaviour> ended with transition %d \r\n", result);
            return result;
        }
    }
    
    private class HandleMoveBehaviour extends OneShotBehaviour{
        int result = 0;
        
        @Override 
        public void action() {
            //System.o  ut.println("<queen> Initial state");
            
            _chess = parsePositions(_lastMessage.getContent());            
            listPossiblePositions = findFreePosition(_chess);
            if (listPossiblePositions.size() >0){
                result = FOUND_FREE_POSITION;
            }
            else{
                result = POSITION_NOT_FOUND;
            }

        }
        
        public int onEnd(){
            //System.out.format("<initialReceiveBehaviour> ended with transition %d \r\n", result);
            return result;
        }
    }
    
    private class ForwardChainBehaviour extends OneShotBehaviour{
        int result = 0;
        
        @Override 
        public void action() {
            //System.o  ut.println("<queen> Initial state");
            _previousQueen = _lastMessage.getSender();
            ACLMessage msgChaining = new ACLMessage(ACLMessage.PROPAGATE);
            msgChaining.addReceiver(new AID(_nextQueen, AID.ISLOCALNAME));
            send(msgChaining);
            
            result= GO_WAITING;
        }
        
        public int onEnd(){
            //System.out.format("<initialReceiveBehaviour> ended with transition %d \r\n", result);
            return result;
        }
    }
    
  private class InitiateMoveBehaviour extends OneShotBehaviour{
        int result = 0;
        
        @Override 
        public void action() {
            //System.o  ut.println("<queen> Initial state");
            currentPosition = ((_positionLine-1) * (_sizeChess)) +1;
            //TODO add here the choice of random position
            _chess[currentPosition] = myAgent.getLocalName();
            
            if(_isLast){
                _solutionFound = true;
                result = PRINT_CHESS;
            } 
            else{
                ACLMessage requestMoveMessage = new ACLMessage(ACLMessage.REQUEST);
                requestMoveMessage.setContent(parseChessToString(_chess));
                requestMoveMessage.addReceiver(new AID(_nextQueen, AID.ISLOCALNAME));

                send(requestMoveMessage);

                result = GO_WAITING;
             
            }
            
        }
        
        public int onEnd(){
            //System.out.format("<initialReceiveBehaviour> ended with transition %d \r\n", result);
            return result;
        }
    }
    
    
    private class MoveQueenBehaviour extends OneShotBehaviour{
        int result = 0;
        
        @Override 
        public void action() {
            //System.o  ut.println("<queen> Initial state");
            _chess[currentPosition] = EMPTY_POSITION;
            //TODO add here the choice of random position
            currentPosition = listPossiblePositions.get(0);
            listPossiblePositions.remove(0);
            _chess[currentPosition] = myAgent.getLocalName();
            
            
            if(_isLast){
                _solutionFound = true;
                result = PRINT_CHESS;
            } 
            else{
                ACLMessage requestMoveMessage = new ACLMessage(ACLMessage.REQUEST);
                requestMoveMessage.setContent(parseChessToString(_chess));
                requestMoveMessage.addReceiver(new AID(_nextQueen, AID.ISLOCALNAME));

                send(requestMoveMessage);

                result = GO_WAITING;
             
            }
            
        }
        
        public int onEnd(){
            //System.out.format("<initialReceiveBehaviour> ended with transition %d \r\n", result);
            return result;
        }
    }
    
        private class HandleBacktrackBehaviour extends OneShotBehaviour{
        int result = 0;
        
        @Override 
        public void action() {
            //System.o  ut.println("<queen> Initial state");
            
            if(listPossiblePositions.size() > 0){
                result = FOUND_FREE_POSITION;
            }
            else{
                result = POSITION_NOT_FOUND;
            }
            //TODO add here the choice of random position
           
        }
        
        public int onEnd(){
            //System.out.format("<initialReceiveBehaviour> ended with transition %d \r\n", result);
            return result;
        }
    }
    
        private class BacktrackQueenBehaviour extends OneShotBehaviour{
        int result = 0;
        
        @Override 
        public void action() {
            
            if(_isFirst){
                result = PRINT_CHESS;
            }
            else{
                ACLMessage requestMoveMessage = new ACLMessage(ACLMessage.REFUSE);
                requestMoveMessage.addReceiver(_previousQueen);

                send(requestMoveMessage);

                result = GO_WAITING;
            }
            
             
        }
        
        public int onEnd(){
            //System.out.format("<initialReceiveBehaviour> ended with transition %d \r\n", result);
            return result;
        }
    }
    
        
        
        
        
    private class EndBehaviour extends OneShotBehaviour{
        int result = 0;
        
        @Override 
        public void action() {
            
            if(_solutionFound){
                System.out.println("SOLUTION FOUND");
            }
            else{
                System.out.println("NO SOLUTIONS FOUND");
            }
            
             
        }
        
        public int onEnd(){
            //System.out.format("<initialReceiveBehaviour> ended with transition %d \r\n", result);
            return result;
        }
    } 
    
     private String[] parsePositions(String contentMessage){
        
        String[] chess = new String[ (this._sizeChess * this._sizeChess) +1];
        
        String[] chessToParse= contentMessage.split(CHESS_SEPARATOR);
        //begin with 1 in the result chess
        for(int i=0; i<chessToParse.length ; i++){
            chess[i+1] = chessToParse[i] ;
        }
        
        return chess;
    }
     
    private String parseChessToString(String[] chess){
        
        String chessInString = "";
        
        for(int i =1; i< _chess.length ; i++){
            chessInString += _chess[i] + CHESS_SEPARATOR;
        }
        
        return chessInString;
    }
   
     //cases without a queen contains "empty"
     private List<Integer> findFreePosition(String[] chess){
         
         return new ArrayList<Integer>();
     }
}
