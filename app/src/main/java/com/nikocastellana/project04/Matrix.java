package com.nikocastellana.project04;

import android.util.Log;

import java.util.Random;

public class Matrix {

    private int[][] board;
    private int gopher;
    private int move;
    private static int rowSpot;
    private static int colSpot;

    static {
        Random rand = new Random();
        rowSpot = rand.nextInt(10);
        colSpot = rand.nextInt(10);
    }

    Matrix(){
        board = new int[10][10];
        gopher = -999;
        move = 1;

        for(int row = 0; row < board.length; row++){
            for(int col = 0; col < board[row].length; col++){
                board[row][col] = 0;
            }
        }

        // Initialize gopher
        board[rowSpot][colSpot] = gopher;
    }

    void markBoard(int row, int col){
        board[row][col] = move;
        move++;
    }

    int[][] getBoard(){
        return board;
    }

    int getMove(){return move;}

    int getGopherCoordX(){
        return rowSpot;
    }

    int getGopherCoordY(){
        return colSpot;
    }
}
