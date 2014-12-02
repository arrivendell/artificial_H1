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
import java.util.Random;

/**
 *
 * @author nightwish
 */
public class QueenAgent2 extends Agent {
    
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
    
    static int MAX_COUNTS=1000;
    
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
    private int counter = 0;
    Random rand = new Random();

    public List<Integer> listPossiblePositions = new ArrayList<Integer>();
    private ArrayList<String> _Solutions = new ArrayList<>();
    
    @Override
    protected void setup() {
        System.out.format("Hello, queen %s is ready ! \r\n", getAID().getLocalName() );
        
        
        // We get the parameters
        Object[] args = getArguments();
        if (args != null && args.length > 2){
            _sizeChess = Integer.parseInt((String) args[0]);
            
           _chess = new String[(_sizeChess*_sizeChess)+1]; // We begin the chess at the index 1
           
           
            _positionLine = Integer.parseInt( (String) args[1]);
             if (_positionLine == 1){
                 _isFirst = true;
             }
             else{
                 _isFirst = false;
             }
            if (((String) args[1]).equals((String) args[0]) )
            {
                _nextQueen =  (String) args[2];
                 _isLast = true;
            }
            else{
                _nextQueen =  (String) args[2];
                _isLast = false;
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
            fsm.registerState(new RepeaterBehaviour(), "repeateSolution");
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
            fsm.registerTransition("repeateSolution", "Initial", GO_WAITING);  
            fsm.registerTransition("repeateSolution", "end", PRINT_CHESS);  
            fsm.registerTransition("backtrackQueen", "end", PRINT_CHESS);  
            
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
                //System.out.format("mesgReceived <%s> \r\n", msgReceived.getPerformative());
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
                        break;
                        
                    }
                    case ACLMessage.REQUEST:{
                        result = RECEIVED_MOVE;
                        break;

                    }
                    case ACLMessage.REFUSE:{
                        //System.out.println(getName() + " received backtrack from " + msgReceived.getSender().getName());
                        result = RECEIVED_BACKTRACK;
                        break;
                    }
                    default:
                        break;
                        
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
           // System.out.println("<queen> forward behaviour");
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
            Arrays.fill(_chess, EMPTY_POSITION);//fill with empty positions
            //System.o  ut.println("<queen> Initial state");
            currentPosition = ((_positionLine-1) * (_sizeChess)) +1;
            listPossiblePositions = findFreePosition(_chess);
            _solutionFound = false;
             //add here the choice of random position
            int randPosition = rand.nextInt(listPossiblePositions.size());
            currentPosition = listPossiblePositions.get(randPosition);
            listPossiblePositions.remove(randPosition);
            
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
            //System.out.println("<queen> Move queen");
            _chess[currentPosition] = EMPTY_POSITION;
            
            //add here the choice of random position
            int randPosition = rand.nextInt(listPossiblePositions.size());
            currentPosition = listPossiblePositions.get(randPosition);
            listPossiblePositions.remove(randPosition);
            
            _chess[currentPosition] = myAgent.getLocalName();
            //printChess();
            
            if(_isLast){
                _solutionFound = true;
                printChess(_chess);
                result = GO_WAITING;
                ACLMessage msgToSend = new ACLMessage(ACLMessage.REFUSE);
                msgToSend.addReceiver(getAID());
                send(msgToSend);
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
            //System.out.println(getName() + "   Backtracking");
            if(_isFirst ){ //&& (listPossiblePositions.size()==0 || _solutionFound
                result = PRINT_CHESS;
            }
            else{
                ACLMessage requestMoveMessage = new ACLMessage(ACLMessage.REFUSE);
                requestMoveMessage.addReceiver(_previousQueen);
                //System.out.println(getName() + " send backtrack to " + _previousQueen.getName());
                send(requestMoveMessage);
                
                result = GO_WAITING;
            }
            
             
        }
        
        public int onEnd(){
            //System.out.format("<initialReceiveBehaviour> ended with transition %d \r\n", result);
            return result;
        }
    }
    
        
     private class RepeaterBehaviour extends OneShotBehaviour{
        int result = 0;
        
        @Override 
        public void action() {
            
            if(_solutionFound){
                //System.out.println("SOLUTION FOUND");
                //printChess();
            }
            else{
                //System.out.println("NO SOLUTIONS FOUND");
            }
            
            String solution = parseChessToString(_chess);
            if (!_Solutions.contains(solution))
            {
                _Solutions.add(solution);
            }

            //System.out.println(_Solutions.size());
            
            if(counter < MAX_COUNTS){
                result = GO_WAITING;
                counter++;
            }
            else{
                result = PRINT_CHESS;
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
                System.out.println("SOLUTION FOUND : " + _Solutions.size());
                for(String s : _Solutions){
                    printChess(parsePositions(s));
                    System.out.println("");
                }
                
            }
            else{
                System.out.println("NO SOLUTIONS FOUND");
            }
            
             doDelete();
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
         
         List<Integer> result = new ArrayList<Integer>();
         
         for(int pos = ((_positionLine - 1) * _sizeChess+ 1) ; pos<((_positionLine) * _sizeChess+ 1) ; pos++ ){
             boolean isNotFree = false;
             
             for(int i = (pos%_sizeChess==0)?_sizeChess : pos%_sizeChess; !isNotFree && i< _chess.length ; i+=_sizeChess){
                 if ( !_chess[i].equals(EMPTY_POSITION) ){
                     isNotFree = true;
                 }
             }
             int row = _positionLine;
             int column = (pos%_sizeChess==0)?_sizeChess : pos%_sizeChess;
             int i = pos;
             while (!isNotFree && i< _chess.length && column<=_sizeChess && row<=_sizeChess){
                 if ( !_chess[i].equals(EMPTY_POSITION) ){
                     isNotFree = true;
                 }
                 else{
                     row++;
                     column++;
                     i = ((row-1)*_sizeChess)+column;
                 }
             }
             
             i=pos;
             row = _positionLine;
             column = (pos%_sizeChess==0)?_sizeChess : pos%_sizeChess;
             while (!isNotFree && i> 0){
                 if ( !_chess[i].equals(EMPTY_POSITION) ){
                     isNotFree = true;
                 }
                 else{
                     row--;
                     column--;
                     i = ((row-1)*_sizeChess)+column;
                 }
             }
             
             i=pos;
             row = _positionLine;
             column = (pos%_sizeChess==0)?_sizeChess : pos%_sizeChess;
             while (!isNotFree && i> 0 && column<=_sizeChess ){
                 if ( !_chess[i].equals(EMPTY_POSITION) ){
                     isNotFree = true;
                 }
                 else{
                     row--;
                     column++;
                     i = ((row-1)*_sizeChess)+column;
                 }
             }
              row = _positionLine;
              column = (pos%_sizeChess==0)?_sizeChess : pos%_sizeChess;
              i = pos;
             while (!isNotFree && i< _chess.length && row<=_sizeChess){
                 if ( !_chess[i].equals(EMPTY_POSITION) ){
                     isNotFree = true;
                 }
                 else{
                     row++;
                     column--;
                     i = ((row-1)*_sizeChess)+column;
                 }
             }
             if(!isNotFree){
                result.add(pos);
             }
         }
         //System.out.println(result);
         return result;
     }
     
     private void printChess(String[] chess){
         for(int i = 1 ; i<= chess.length-_sizeChess; i+=_sizeChess){
                for (int j=i; j< i+_sizeChess; j++)
                {
                    System.out.print(((chess[j].equals(EMPTY_POSITION)) ? "_" : "X") + " | ");
                }
                System.out.println("");
            }
                         System.out.println("");
                System.out.println("");

     }
}
