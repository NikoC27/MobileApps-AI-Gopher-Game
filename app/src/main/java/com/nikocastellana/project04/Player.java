package com.nikocastellana.project04;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import androidx.annotation.NonNull;
import java.util.Stack;


public class Player extends Thread{

    // Tips:
    // Worker thread should not modify UI
    // Pass results back to UI thread so UI can update interface
    // Must have job queue, looper, and handler
    private final int strategy;
    private Pair<Integer,Integer> Q1 = new Pair(2,2);
    private Pair<Integer,Integer> Q2 = new Pair(2,7);
    private Pair<Integer,Integer> Q3 = new Pair(7,2);
    private Pair<Integer,Integer> Q4 = new Pair(7,7);
    private Stack<Pair<Integer, Integer>> quads;
    private Stack<Pair<Integer, Integer>> moves;
    private Stack<Pair<Integer,Integer>> dummyMoves;
    private static final int[] nearMissRow = {0, -1, -1, -1, 0, 1, 1, 1};
    private static final int[] nearMissCol = {-1, -1, 0, 1, 1, 1, 0, -1};
    private static final int[] closeGuessRow = {0, -1, -2, -2, -2, -2, -2, -1, 0, 1, 2, 2, 2, 2, 2, 1};
    private static final int[] closeGuessCol = {-2, -2, -2, -1, 0, 1, 2, 2, 2, 2, 2, 1, 0, -1, -2, -2};
    public static Handler moveHandlerOne;
    public static Handler moveHandlerTwo;
    private boolean movesInitialized = false;
    private final String quad = "QUAD";
    private final String close = "CLOSE";
    private final String near = "NEAR";
    private final String miss = "MISS";
    private int quadRow;
    private int quadCol;

    Player(int strategy){
        this.strategy = strategy;
        quads = new Stack<>();

        // Middle positions of 4 quadrants on matrix
        quads.push(Q4);
        quads.push(Q3);
        quads.push(Q2);
        quads.push(Q1);

        // Initialize the dummy moves for player 2
        initDummyMoves();
    }

    private Pair<Integer, Integer> quadGuess(){
        return quads.pop();
    }

    private Pair<Integer, Integer> playerGuess(){
        return moves.pop();
    }

    private Pair<Integer, Integer> dummyGuess(){
        return dummyMoves.pop();
    }

    private boolean initMoves(int status, int quadRow, int quadCol){
        moves = new Stack<>();

        if (status == Result.NEAR_MISS){
            int size = 8;
            for(int i = 0; i < size; i++){
                moves.push(new Pair<>(nearMissRow[i] + quadRow, nearMissCol[i] + quadCol));
            }
        }

        if (status == Result.CLOSE_GUESS){
            int size = 16;
            for(int i = 0; i < size; i++){
                moves.push(new Pair<>(closeGuessRow[i] + quadRow, closeGuessCol[i] + quadCol));
            }
        }

        return true;
    }

    private void initDummyMoves(){
        dummyMoves = new Stack<>();

        for(int i = 9; i >= 0; i--){
            for(int j = 9; j >= 0; j--){
                dummyMoves.push(new Pair<>(i,j));
            }
        }
    }

    private Handler strategyOne(){

        return new Handler(Looper.getMainLooper()){

            Pair<Integer,Integer> guess;

            @Override
            public void handleMessage(@NonNull Message msg) {
                int what = msg.what;
                Log.i("Message received in player thread", "" + what);
                switch (what){
                    case Result.COMPLETE_MISS:
                        Log.i("COMPLETE MISS", "Player one made his move");
                        Message msgQuad = UiThread.UiHandler.obtainMessage(Result.PLAYER_ONE_MOVE);
                        guess = quadGuess();
                        quadRow = guess.first;
                        quadCol = guess.second;
                        msgQuad.arg1 = guess.first;
                        msgQuad.arg2 = guess.second;
                        msgQuad.obj = quad;
                        UiThread.UiHandler.sendMessage(msgQuad);
                        try { Thread.sleep(2000); }
                        catch (InterruptedException e) { System.out.println("Thread interrupted!") ; }
                        break;
                    case Result.CLOSE_GUESS:
                        if(!movesInitialized)
                            movesInitialized = initMoves(Result.CLOSE_GUESS, quadRow, quadCol);
                        Log.i("CLOSE GUESS", "Player one made his move");
                        Message msgClose = UiThread.UiHandler.obtainMessage(Result.PLAYER_ONE_MOVE);
                        guess = playerGuess();
                        msgClose.arg1 = guess.first;
                        msgClose.arg2 = guess.second;
                        msgClose.obj = close;
                        UiThread.UiHandler.sendMessage(msgClose);
                        try { Thread.sleep(2000); }
                        catch (InterruptedException e) { System.out.println("Thread interrupted!") ; }
                        break;
                    case Result.NEAR_MISS:
                        if(!movesInitialized)
                            movesInitialized = initMoves(Result.NEAR_MISS, quadRow, quadCol);
                        Log.i("NEAR MISS", "Player one made his move");
                        Message msgNear = UiThread.UiHandler.obtainMessage(Result.PLAYER_ONE_MOVE);
                        guess = playerGuess();
                        msgNear.arg1 = guess.first;
                        msgNear.arg2 = guess.second;
                        msgNear.obj = near;
                        UiThread.UiHandler.sendMessage(msgNear);
                        try { Thread.sleep(2000); }
                        catch (InterruptedException e) { System.out.println("Thread interrupted!") ; }
                        break;
                    case Result.SUCCESS:
//                        moveHandlerOne.getLooper().quit();
                        break;

                }
            }
        };
    }

    private Handler strategyTwo(){

        return new Handler(Looper.getMainLooper()){
            Pair<Integer,Integer> dummyMove;
            @Override
            public void handleMessage(@NonNull Message msg) {
                int what = msg.what;
                switch (what){
                    case Result.COMPLETE_MISS:
                        Message msgMiss = UiThread.UiHandler.obtainMessage(Result.PLAYER_TWO_MOVE);
                        dummyMove = dummyGuess();
                        msgMiss.arg1 = dummyMove.first;
                        msgMiss.arg2 = dummyMove.second;
                        msgMiss.obj = miss;
                        UiThread.UiHandler.sendMessage(msgMiss);
                        try { Thread.sleep(2000); }
                        catch (InterruptedException e) { System.out.println("Thread interrupted!") ; }
                        break;
                    case Result.CLOSE_GUESS:
                        Message msgClose = UiThread.UiHandler.obtainMessage(Result.PLAYER_TWO_MOVE);
                        dummyMove = dummyGuess();
                        msgClose.arg1 = dummyMove.first;
                        msgClose.arg2 = dummyMove.second;
                        msgClose.obj = close;
                        UiThread.UiHandler.sendMessage(msgClose);
                        try { Thread.sleep(2000); }
                        catch (InterruptedException e) { System.out.println("Thread interrupted!") ; }
                        break;
                    case Result.NEAR_MISS:
                        Message msgNear = UiThread.UiHandler.obtainMessage(Result.PLAYER_TWO_MOVE);
                        dummyMove = dummyGuess();
                        msgNear.arg1 = dummyMove.first;
                        msgNear.arg2 = dummyMove.second;
                        msgNear.obj = near;
                        UiThread.UiHandler.sendMessage(msgNear);
                        try { Thread.sleep(2000); }
                        catch (InterruptedException e) { System.out.println("Thread interrupted!") ; }
                        break;
                    case Result.SUCCESS:
                        //moveHandlerTwo.getLooper().quit();
                        break;

                }
            }
        };
    }


    @Override
    public void run(){

        if(this.strategy == 1){
            Looper.prepare();

            // Player One is ready to start game and sends first guess
            Message msg = UiThread.UiHandler.obtainMessage(Result.PLAYER_ONE_MOVE);
            Pair<Integer,Integer> firstQuad = quadGuess();
            quadRow = firstQuad.first;
            quadCol = firstQuad.second;
            msg.arg1 = firstQuad.first;
            msg.arg2 = firstQuad.second;
            msg.obj = quad;
            UiThread.UiHandler.sendMessage(msg);

            // Define move handler
            moveHandlerOne = strategyOne();
            Looper.loop();
        }

        if(this.strategy == 2){
            Looper.prepare();

            // Player Two is ready to start game and sends first guess
            Message msg = UiThread.UiHandler.obtainMessage(Result.PLAYER_TWO_MOVE);
            Pair<Integer,Integer> dummyMove = dummyGuess();
            msg.arg1 = dummyMove.first;
            msg.arg2 = dummyMove.second;
            msg.obj = miss;
            UiThread.UiHandler.sendMessage(msg);

            // Define move handler
            moveHandlerTwo = strategyTwo();
            Looper.loop();
        }
    }
}
