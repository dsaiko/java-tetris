package org.saiko.games.tetris;

import java.io.IOException;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * TetrisConfiguration.java
 * @author  Saiko Du¹an
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
     * image of the tetris plane
     */
    String tetrisImage;
    
    /** image patht to the colorod square block from which whe tetris piece is created */
    String imagePath;
    
    /** prefix for images of square block from which whe tetris piece is created */
    String imagePrefix;
    
    /** postfix for images of square block from which whe tetris piece is created */
    String imagePostfix;
    
    /** piece width  in pixels for images of square block from which whe tetris piece is created*/
    int pieceWidth;
    
    /** image height in pixels for images of square block from which whe tetris piece is created */
    int pieceHeight;
    
    /** width in pieces of Tetris plane*/
    int planeWidth;
    
    /** height in pieces of Tetris plane*/
    int planeHeight;
    
    /** x position of Tetris plane **/
    int planeX;
    
    /** y position of Tetris plane **/
    int planeY;
    
    /** path to the background images
     *  the background images should be of propper size */
    String backgroundPath;
    
    /** prefix for background images */
    String backgroundPrefix;
    
    /** postfix for background images */
    String backgroundPostfix;
    
    /** number of background images available */
    int backgrounds;
    
    /** path to digit images **/
    String digitPath;
    
    /** prefix for digit image files **/
    String digitPrefix;
    
    /** postfix for digit image files **/
    String digitPostfix;
    
    /** array of tetris pieces */
    TetrisPiece[] piecesAll;
    TetrisPiece[] piecesNormal;
    
    /** init delay of falling pieces **/
    int initSpeed;
    
    //private constructor - this class is gonna be created by static method
    private GameSettings() {
    }
    
    /**
     * reads the configuration from XML and returns it as TetrisConfiguration
     * object
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static GameSettings getTetrisConfiguration(Tetris tetris, String configFile)
    throws SAXException, IOException, ParserConfigurationException {
        //this object will be filled with apropriate informations
        GameSettings config = new GameSettings();
        
        Element rootElement=tetris.getResource().getXMLDocumentElement(configFile);
        NodeList childNodes=rootElement.getChildNodes();
        
        Vector piecesAll = new Vector();
        Vector piecesNormal = new Vector();
        
        for(int i=0; i<childNodes.getLength(); i++) {
            Node node=childNodes.item(i);
            
            int type = node.getNodeType();
            if (type == Node.ELEMENT_NODE) {
                String nodeName=node.getNodeName();
                
                if(nodeName.equals("config")) {
                    //load the configuration
                    loadConfiguration((Element) node,config);
                } else if (nodeName.equals("piece")) {
                    //load the pieces
                    TetrisPiece piece = getTetrisPiece((Element)node);
                    piecesAll.add(piece);
                    if(piece.type==TETRIS_PIECE_TYPE_NORMAL) {
                        piecesNormal.add(piece);
                    }
                }
            } //element
        } //traversing the configuration

        //convert vector of Pieces to array
        config.piecesAll=(TetrisPiece[])piecesAll.toArray(new TetrisPiece[]{});
        config.piecesNormal=(TetrisPiece[])piecesNormal.toArray(new TetrisPiece[]{});
        
        return config;
    }
    
    /**
     * method loads the configuration part of TETRIS.XML
     */
    private static void loadConfiguration(Element aConfigElement, GameSettings aConfig) {
        aConfig.tetrisImage=TetrisResource.getXMLElementChildValue(aConfigElement,"tetris_image");
        aConfig.digitPath=TetrisResource.getXMLElementChildValue(aConfigElement,"digit_path");
        aConfig.digitPrefix=TetrisResource.getXMLElementChildValue(aConfigElement,"digit_prefix");
        aConfig.digitPostfix=TetrisResource.getXMLElementChildValue(aConfigElement,"digit_postfix");
        aConfig.imagePath=TetrisResource.getXMLElementChildValue(aConfigElement,"image_path");
        aConfig.imagePrefix=TetrisResource.getXMLElementChildValue(aConfigElement,"image_prefix");
        aConfig.imagePostfix=TetrisResource.getXMLElementChildValue(aConfigElement,"image_postfix");
        aConfig.pieceWidth=TetrisResource.getXMLElementChildIntValue(aConfigElement,"piece_width");
        aConfig.pieceHeight=TetrisResource.getXMLElementChildIntValue(aConfigElement,"piece_height");
        aConfig.planeWidth=TetrisResource.getXMLElementChildIntValue(aConfigElement,"plane_width");
        aConfig.planeHeight=TetrisResource.getXMLElementChildIntValue(aConfigElement,"plane_height");
        aConfig.planeX=TetrisResource.getXMLElementChildIntValue(aConfigElement,"plane_x");
        aConfig.planeY=TetrisResource.getXMLElementChildIntValue(aConfigElement,"plane_y");
        aConfig.backgroundPath=TetrisResource.getXMLElementChildValue(aConfigElement,"background_path");
        aConfig.backgrounds=TetrisResource.getXMLElementChildIntValue(aConfigElement,"backgrounds");
        aConfig.backgroundPrefix=TetrisResource.getXMLElementChildValue(aConfigElement,"background_prefix");
        aConfig.backgroundPostfix=TetrisResource.getXMLElementChildValue(aConfigElement,"background_postfix");
        aConfig.initSpeed=TetrisResource.getXMLElementChildIntValue(aConfigElement,"init_speed");
    }
    
    /** loads the piece information from XML to objects */
    private static TetrisPiece getTetrisPiece(Element aPieceElement) {
        TetrisPiece piece = new TetrisPiece();
        String pieceType = TetrisResource.getXMLElementChildValue(aPieceElement,"type");
        if("NORMAL".equalsIgnoreCase(pieceType)) {
            piece.type=TETRIS_PIECE_TYPE_NORMAL;
        } else if("BOMB".equalsIgnoreCase(pieceType)) {
            piece.type=TETRIS_PIECE_TYPE_BOMB;
        } else {
            piece.type=TETRIS_PIECE_TYPE_HYBRID;
        }

        piece.image=TetrisResource.getXMLElementChildValue(aPieceElement,"image");
        
        Vector rotations = new Vector();
        NodeList list=aPieceElement.getElementsByTagName("rotation");
        for(int i=0; i<list.getLength(); i++) {
            TetrisPieceRotation rotation = new TetrisPieceRotation();
            Element rotationElement = (Element) list.item(i);
            rotation.width=TetrisResource.getXMLElementChildIntValue(rotationElement,"width");
            rotation.height=TetrisResource.getXMLElementChildIntValue(rotationElement,"height");
            rotation.centerX=TetrisResource.getXMLElementChildIntValue(rotationElement,"center_x");
            rotation.centerY=TetrisResource.getXMLElementChildIntValue(rotationElement,"center_y");
            rotation.map=TetrisResource.getXMLElementChildValue(rotationElement,"map");
            rotations.add(rotation);
        }
        
        //convert the piece rotations into array
        piece.rotations=(TetrisPieceRotation[]) rotations.toArray(new TetrisPieceRotation[]{});
        
        return piece;
    }
}


