
package org.saiko.games.tetris;



/**
 * class for defining particular face of one piece rotation
 */
public class TetrisPieceRotation {

    /** width of the piece in blocks */
    int width;

    /** height of the piece in blocks */
    int height;

    /** center of rotation of the piece in blocks */
    int centerX;

    /** center of rotation of the piece in blocks */
    int centerY;

    /** map of the piece face - it is array of width X height characters,
     *  where 0 specifies that the block is not there
     *  1 is the piece-block*/
    String map;
}
