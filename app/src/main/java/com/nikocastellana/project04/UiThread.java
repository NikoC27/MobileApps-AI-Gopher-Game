package com.nikocastellana.project04;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Adapter;
import android.widget.GridView;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class UiThread extends AppCompatActivity {

    private Player playerOne = new Player(1);
    private Player playerTwo = new Player(2);
    private Matrix boardOne = new Matrix();
    private Matrix boardTwo = new Matrix();
    private int[] boardOneItems = new int[100];
    private int[] boardTwoItems = new int[100];
    private GridView gridOne;
    private GridView gridTwo;
    private final int[] nearMissRow = {0, -1, -1, -1, 0, 1, 1, 1};
    private final int[] nearMissCol = {-1, -1, 0, 1, 1, 1, 0, -1};
    private final int[] closeGuessRow = {0, -1, -2, -2, -2, -2, -2, -1, 0, 1, 2, 2, 2, 2, 2, 1};
    private final int[] closeGuessCol = {-2, -2, -2, -1, 0, 1, 2, 2, 2, 2, 2, 1, 0, -1, -2, -2};
    private final String quad = "QUAD";
    private final String close = "CLOSE";
    private final String near = "NEAR";
    private final String miss = "MISS";
    private String playerOneGuess;
    private String playerTwoGuess;
    private int dummyCount = 0;
    public static Handler UiHandler;


    private Handler getUiHandler(){
        return new Handler(Looper.getMainLooper()){
            int row;
            int col;
            int result;

            @Override
            public void handleMessage(@NonNull Message msg) {
                int what = msg.what;
                switch (what) {
                    case Result.PLAYER_ONE_MOVE:

                        row = msg.arg1;
                        col = msg.arg2;

                        Log.i("PlayerONE guessed", "(" + row + ", " + col + ")");
                        if(msg.obj.equals(quad)){
                            result = checkMatrix(row, col);
                        }
                        else if(msg.obj.equals(near)){
                            result = markAndCheckNear(row,col);
                        }
                        else if(msg.obj.equals(close)){
                            result = markAndCheckClose(row,col);
                        }

                        boardOneItems = Stream.of(boardOne.getBoard()) .flatMapToInt(IntStream::of).toArray();
                        gridOne.setAdapter(gridOne.getAdapter());
//                        Log.i("Result in UI Thread is ", "" + result);
                        Log.i("Gopher Coordinates", "(" + boardOne.getGopherCoordX() + ", " + boardOne.getGopherCoordY() + ")");
                        Log.i("Board after guess", Arrays.deepToString(boardOne.getBoard()));
                        Message msg1 = playerOne.moveHandlerOne.obtainMessage(result);
                        playerOne.moveHandlerOne.sendMessage(msg1);

                        break;
                    case Result.PLAYER_TWO_MOVE:
                        row = msg.arg1;
                        col = msg.arg2;
                        Log.i("PlayerTWO guessed", "(" + row + ", " + col + ")");

                        if(msg.obj.equals(miss)){
                            result = markAndCheckDummy(row, col);
                        }
                        else if(msg.obj.equals(near)){
                            result = markAndCheckDummy(row,col);
                        }
                        else if(msg.obj.equals(close)){
                            result = markAndCheckDummy(row,col);
                        }

                        boardTwoItems = Stream.of(boardTwo.getBoard()) .flatMapToInt(IntStream::of).toArray();
                        gridTwo.setAdapter(gridTwo.getAdapter());
                        Log.i("Gopher Coordinates", "(" + boardTwo.getGopherCoordX() + ", " + boardTwo.getGopherCoordY() + ")");
                        Log.i("Board after guess", Arrays.deepToString(boardTwo.getBoard()));
                        Message msg2 = playerTwo.moveHandlerTwo.obtainMessage(result);
                        playerTwo.moveHandlerTwo.sendMessage(msg2);
                        break;
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ui_thread);


        UiHandler = getUiHandler();
        playerOne.start();
        playerTwo.start();

        gridOne = (GridView) findViewById(R.id.grid1);

        gridOne.setAdapter(new GridAdapterOne(this, boardOneItems, playerOneGuess));

        gridTwo = (GridView) findViewById(R.id.grid2);

        // Create a new AnimalAdapter and set it as the Adapter for this GridView
        gridTwo.setAdapter(new GridAdapterTwo(this, boardTwoItems, playerTwoGuess));


    }

    // Grid one check for quadrant guess
    private int checkMatrix(int row, int col){

        if(boardOne.getBoard()[row][col] == -999){
            boardOne.markBoard(row,col);
            playerOneGuess = "Success";
            return Result.SUCCESS;
        }
        else if(checkNear(row,col)){
            boardOne.markBoard(row,col);
            playerOneGuess = "Near Miss";
            return Result.NEAR_MISS;
        }
        else if(checkClose(row,col)){
            boardOne.markBoard(row,col);
            playerOneGuess = "Close Miss";
            return Result.CLOSE_GUESS;
        }

        playerOneGuess = "Miss";
        boardOne.markBoard(row,col);
        return Result.COMPLETE_MISS;
    }

    // Check close helper for checkMatrix
    private boolean checkClose(int row, int col){
        int size = closeGuessRow.length;
        for(int i = 0; i < size; i++){
            if(boardOne.getBoard()[row + closeGuessRow[i]][col + closeGuessCol[i]] == -999){
                return true;
            }
        }

        return false;
    }

    // Check near helper for checkMatrix
    private boolean checkNear(int row, int col){
        int size = nearMissRow.length;
        for(int i = 0; i < size; i++){
            if(boardOne.getBoard()[row + nearMissRow[i]][col + nearMissCol[i]] == -999){
                return true;
            }
        }

        return false;
    }

    // Mark and check grid one near miss spots
    private int markAndCheckNear(int row, int col){
        if(boardOne.getBoard()[row][col] == -999){
            boardOne.markBoard(row,col);
            playerOneGuess = "Success";
            return Result.SUCCESS;
        }

        playerOneGuess = "Near Miss";
        boardOne.markBoard(row,col);
        return Result.NEAR_MISS;
    }

    // Mark and check grid one close miss spots
    private int markAndCheckClose(int row, int col){
        if(boardOne.getBoard()[row][col] == -999){
            boardOne.markBoard(row,col);
            playerOneGuess = "Success";
            return Result.SUCCESS;
        }

        playerOneGuess = "Close Miss";
        boardOne.markBoard(row,col);
        return Result.CLOSE_GUESS;
    }


    private int markAndCheckDummy(int row, int col){
        if(boardTwo.getBoard()[row][col] == -999){
            boardTwo.markBoard(row,col);
            playerTwoGuess = "Success";
            dummyCount++;
            return Result.SUCCESS;
        }
        else if(checkDummyNear(row,col)){
            boardTwo.markBoard(row,col);
            playerTwoGuess = "Near Miss";
            dummyCount++;
            return Result.NEAR_MISS;
        }
        else if(checkDummyClose(row,col)){
            boardTwo.markBoard(row,col);
            playerTwoGuess = "Close Miss";
            dummyCount++;
            return Result.CLOSE_GUESS;
        }

        dummyCount++;
        playerTwoGuess = "Miss";
        boardTwo.markBoard(row,col);
        return Result.COMPLETE_MISS;
    }


    private boolean checkDummyClose(int row, int col){
        if(dummyCount % 2 == 0){
            return true;
        }

        return false;
    }

    private boolean checkDummyNear(int row, int col){
        if(dummyCount % 2 != 0){
            return true;
        }

        return false;
    }


    private void updateGridOne(){

    }

    private void updateGridTwo(){

    }
}