package org.saiko.games.tetris;

import java.util.Arrays;

/**
 * class of tetris-piece definition
 */
public class TetrisPiece {

    /** type of the piece, may be TetrisConfiguration.PIECE_TYPE_NORMAL,PIECE_TYPE_HYBRID, PIECE_TYPE_BOMB */
    final int type;

    /** image name of the piece block **/
    final String image;

    /** array of piece rotations **/
    final TetrisPieceRotation[] rotations;

	public TetrisPiece(int type, String image, TetrisPieceRotation[] rotations) {
		super();
		this.type = type;
		this.image = image;
		this.rotations = rotations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((image == null) ? 0 : image.hashCode());
		result = prime * result + Arrays.hashCode(rotations);
		result = prime * result + type;
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
		TetrisPiece other = (TetrisPiece) obj;
		if (image == null) {
			if (other.image != null)
				return false;
		} else if (!image.equals(other.image))
			return false;
		if (!Arrays.equals(rotations, other.rotations))
			return false;
		if (type != other.type)
			return false;
		return true;
	}
}
