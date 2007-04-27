package org.saiko.games.tetris;

/**
 * class of tetris-piece definition
 */
public class TetrisPiece {

    /** type of the piece, may be TetrisConfiguration.PIECE_TYPE_NORMAL,PIECE_TYPE_HYBRID, PIECE_TYPE_BOMB */
    int type;

    /** image name of the piece block **/
    String image;

    /** array of piece rotations **/
    TetrisPieceRotation[] rotations;
}
