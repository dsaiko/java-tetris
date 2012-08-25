package org.saiko.games.tetris;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * TetrisConfiguration.java
 * @author  dusan.saiko@gmail.com
 * @version 0.1
 * created on 16.12.2002, 00:10
 * last date modified: 2002-12-09
 *
 * The data structure of configuration of Tetris game
 * the structure is loaded from Tetris.XML
 */
public class GameSettings {
    
    static final int TETRIS_PIECE_TYPE_NORMAL=0;
    static final int TETRIS_PIECE_TYPE_HYBRID=1;
    static final int TETRIS_PIECE_TYPE_BOMB=2;
    
    /**
     * image of the Tetris plane
     */
    final String tetrisImage;
    
    /** image path to the colored square block from which the Tetris piece is created */
    final String imagePath;
    
    /** prefix for images of square block from which the Tetris piece is created */
    final String imagePrefix;
    
    /** postfix for images of square block from which the Tetris piece is created */
    final String imagePostfix;
    
    /** piece width  in pixels for images of square block from which the Tetris piece is created*/
    final int pieceWidth;
    
    /** image height in pixels for images of square block from which the Tetris piece is created */
    final int pieceHeight;
    
    /** width in pieces of Tetris plane*/
    final int planeWidth;
    
    /** height in pieces of Tetris plane*/
    final int planeHeight;
    
    /** x position of Tetris plane **/
    final int planeX;
    
    /** y position of Tetris plane **/
    final int planeY;
    
    /** path to the background images
     *  the background images should be of propper size */
    final String backgroundPath;
    
    /** prefix for background images */
    final String backgroundPrefix;
    
    /** postfix for background images */
    final String backgroundPostfix;
    
    /** number of background images available */
    final int backgrounds;
    
    /** path to digit images **/
    final String digitPath;
    
    /** prefix for digit image files **/
    final String digitPrefix;
    
    /** postfix for digit image files **/
    final String digitPostfix;
    
    /** array of tetris pieces */
    final TetrisPiece[] piecesAll;
    final TetrisPiece[] piecesNormal;
    
    /** initial delay of falling pieces **/
    final int initSpeed;

    
    
	public GameSettings(String tetrisImage, String imagePath,
			String imagePrefix, String imagePostfix, int pieceWidth,
			int pieceHeight, int planeWidth, int planeHeight, int planeX,
			int planeY, String backgroundPath, String backgroundPrefix,
			String backgroundPostfix, int backgrounds, String digitPath,
			String digitPrefix, String digitPostfix, TetrisPiece[] piecesAll,
			TetrisPiece[] piecesNormal, int initSpeed) {
		super();
		this.tetrisImage = tetrisImage;
		this.imagePath = imagePath;
		this.imagePrefix = imagePrefix;
		this.imagePostfix = imagePostfix;
		this.pieceWidth = pieceWidth;
		this.pieceHeight = pieceHeight;
		this.planeWidth = planeWidth;
		this.planeHeight = planeHeight;
		this.planeX = planeX;
		this.planeY = planeY;
		this.backgroundPath = backgroundPath;
		this.backgroundPrefix = backgroundPrefix;
		this.backgroundPostfix = backgroundPostfix;
		this.backgrounds = backgrounds;
		this.digitPath = digitPath;
		this.digitPrefix = digitPrefix;
		this.digitPostfix = digitPostfix;
		this.piecesAll = piecesAll;
		this.piecesNormal = piecesNormal;
		this.initSpeed = initSpeed;
	}

	/**
	 * reads the configuration from XML and returns it as TetrisConfiguration object
	 * 
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	public static GameSettings getTetrisConfiguration(Tetris tetris, String configFile) throws SAXException, IOException, ParserConfigurationException {
		// this object will be filled with apropriate informations

		Element rootElement = tetris.getResource().getXMLDocumentElement(configFile);
		NodeList childNodes = rootElement.getChildNodes();

		List<TetrisPiece> piecesAll = new ArrayList<TetrisPiece>();
		List<TetrisPiece> piecesNormal = new ArrayList<TetrisPiece>();

		String tetrisImage 		= null;
		String imagePath 		= null;
		String digitPath 		= null;
		String digitPrefix 		= null;
		String digitPostfix 	= null;
		String imagePrefix 		= null;
		String imagePostfix		= null;
		String backgroundPath 	= null;
		String backgroundPrefix = null;
		String backgroundPostfix= null;
		int initSpeed 			= 0;
		int pieceWidth 			= 0;
		int pieceHeight 		= 0;
		int planeWidth 			= 0;
		int planeHeight 		= 0;
		int planeX 				= 0;
		int planeY 				= 0;
		int backgrounds 		= 0;
		
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);

			int type = node.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				String nodeName = node.getNodeName();

				if (nodeName.equals("config")) {
					// load the configuration
					Element element = (Element) node;
					
			        tetrisImage			=	TetrisResource.getXMLElementChildValue(element, 	"tetris_image");
			        digitPath			=	TetrisResource.getXMLElementChildValue(element, 	"digit_path");
			        digitPrefix			=	TetrisResource.getXMLElementChildValue(element, 	"digit_prefix");
			        digitPostfix		=	TetrisResource.getXMLElementChildValue(element, 	"digit_postfix");
			        imagePath			=	TetrisResource.getXMLElementChildValue(element, 	"image_path");
			        imagePrefix			=	TetrisResource.getXMLElementChildValue(element, 	"image_prefix");
			        imagePostfix		=	TetrisResource.getXMLElementChildValue(element, 	"image_postfix");
			        pieceWidth			=	TetrisResource.getXMLElementChildIntValue(element, 	"piece_width");
			        pieceHeight			=	TetrisResource.getXMLElementChildIntValue(element, 	"piece_height");
			        planeWidth			=	TetrisResource.getXMLElementChildIntValue(element, 	"plane_width");
			        planeHeight			=	TetrisResource.getXMLElementChildIntValue(element, 	"plane_height");
			        planeX				=	TetrisResource.getXMLElementChildIntValue(element, 	"plane_x");
			        planeY				=	TetrisResource.getXMLElementChildIntValue(element, 	"plane_y");
			        backgroundPath		=	TetrisResource.getXMLElementChildValue(element, 	"background_path");
			        backgrounds			=	TetrisResource.getXMLElementChildIntValue(element, 	"backgrounds");
			        backgroundPrefix	=	TetrisResource.getXMLElementChildValue(element, 	"background_prefix");
			        backgroundPostfix	=	TetrisResource.getXMLElementChildValue(element, 	"background_postfix");
			        initSpeed			=	TetrisResource.getXMLElementChildIntValue(element, 	"init_speed");					
				}
				else if (nodeName.equals("piece")) {
					// load the pieces
					TetrisPiece piece = getTetrisPiece((Element) node);
					piecesAll.add(piece);
					if (piece.type == TETRIS_PIECE_TYPE_NORMAL) {
						piecesNormal.add(piece);
					}
				}
			} // element
		} // traversing the configuration


		
		return new GameSettings(
				tetrisImage, imagePath, imagePrefix, imagePostfix, pieceWidth, pieceHeight, planeWidth, planeHeight, 
				planeX, planeY, backgroundPath, backgroundPrefix, backgroundPostfix, backgrounds, digitPath,
				digitPrefix, digitPostfix, 
				piecesAll.toArray(new TetrisPiece[] {}), 
				piecesNormal.toArray(new TetrisPiece[] {}), 
				initSpeed);
	}
    
	/** loads the piece information from XML to objects */
	private static TetrisPiece getTetrisPiece(Element aPieceElement) {

		int type = TETRIS_PIECE_TYPE_NORMAL;
		String image = null;
		
		String pieceType = TetrisResource.getXMLElementChildValue(aPieceElement, "type");
		if ("NORMAL".equalsIgnoreCase(pieceType)) {
			type = TETRIS_PIECE_TYPE_NORMAL;
		}
		else if ("BOMB".equalsIgnoreCase(pieceType)) {
			type = TETRIS_PIECE_TYPE_BOMB;
		}
		else {
			type = TETRIS_PIECE_TYPE_HYBRID;
		}

		image = TetrisResource.getXMLElementChildValue(aPieceElement, "image");

		List<TetrisPieceRotation> rotations = new ArrayList<TetrisPieceRotation>();

		NodeList list = aPieceElement.getElementsByTagName("rotation");
		for (int i = 0; i < list.getLength(); i++) {
			Element rotationElement = (Element) list.item(i);
			int width = TetrisResource.getXMLElementChildIntValue(rotationElement, "width");
			int height = TetrisResource.getXMLElementChildIntValue(rotationElement, "height");
			int centerX = TetrisResource.getXMLElementChildIntValue(rotationElement, "center_x");
			int centerY = TetrisResource.getXMLElementChildIntValue(rotationElement, "center_y");
			char map[] = TetrisResource.getXMLElementChildValue(rotationElement, "map").toCharArray();
			rotations.add(new TetrisPieceRotation(width, height, centerX, centerY, map));
		}

		return new TetrisPiece(type, image, rotations.toArray(new TetrisPieceRotation[] {}));
	}
}


