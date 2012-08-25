
package org.saiko.games.tetris;

import java.util.Arrays;



/**
 * class for defining particular face of one piece rotation
 */
public class TetrisPieceRotation {

    /** width of the piece in blocks */
    final int width;

    /** height of the piece in blocks */
    final int height;

    /** center of rotation of the piece in blocks */
    final int centerX;

    /** center of rotation of the piece in blocks */
    final int centerY;

    /** map of the piece face - it is array of width X height characters,
     *  where 0 specifies that the block is not there
     *  1 is the piece-block*/
    final char[] map;

	public TetrisPieceRotation(int width, int height, int centerX, int centerY, char[] map) {
		super();
		this.width = width;
		this.height = height;
		this.centerX = centerX;
		this.centerY = centerY;
		this.map = map;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + centerX;
		result = prime * result + centerY;
		result = prime * result + height;
		result = prime * result + Arrays.hashCode(map);
		result = prime * result + width;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TetrisPieceRotation other = (TetrisPieceRotation) obj;
		if (centerX != other.centerX)
			return false;
		if (centerY != other.centerY)
			return false;
		if (height != other.height)
			return false;
		if (!Arrays.equals(map, other.map))
			return false;
		if (width != other.width)
			return false;
		return true;
	}
}
