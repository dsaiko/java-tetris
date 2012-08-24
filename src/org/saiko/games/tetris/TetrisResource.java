package org.saiko.games.tetris;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.saiko.games.tetris.err.ErrorHandler;
import org.saiko.games.tetris.img.ImageLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Resource.java
 * @author  dusan.saiko@gmail.com
 * @version 0.1
 * created on 08. 09. 2002, 0:51
 * last date modified: 2002-12-10
 * 
 * Util class to support loading and cashing of resources with respect
 * to language-sensitivity
 *
 * History:
 *  2002-16-10  add support for XML
 *  2002-12-10  code revision, code documentation
 *  2002-12-10  all the code reorganized
 */
public class TetrisResource {
    
    /** Hashtable to cache already loaded images */
    final Hashtable images=new Hashtable();
    
    /** Hashtable to cache already used properties */
    final Hashtable properties=new Hashtable();

    Properties langProperties;
    
    /** Currently selected language */
    String currentLanguage=null;

    
    /** directory with language resources */
    static final String resPath= "org/saiko/games/tetris/res/";

    /** directory with image resources */
    static final String imgPath = resPath+"img/";

    /** directory with image resources */
    static final String langPath = resPath;
    
    
    /** non-public Resource object constructor */
    TetrisResource(String language) {
        setLanguage(language);
    } //Resource Constructor
    

    /*
     * sets new language for the resource
     * called by configurationChanged when
     * @see ConfigurationChanged
     */
    public void setLanguage(String aLanguage) 
    {
        currentLanguage=aLanguage;
        if(langProperties==null)
        	langProperties=new Properties();
        	
        langProperties.clear();
        
        try {
        	String resource=langPath+currentLanguage+"/tetris.properties";
        	langProperties.load(getClass().getClassLoader().getResourceAsStream(resource));
        } catch(Throwable e){
            ErrorHandler.handleError(e);
        }
    }
    
    
    /**
     * returns string from specified section.
     * section is the name of property file in the language directory eg. ajc
     * or calculator ...
     * @see setLanguage
     * @param aSection (String) - name of the property file
     * @param aKey (String) - name of the property
     */
    public String getString(String aKey) {
        return langProperties.getProperty(aKey);
    }
        
    
    /**
     * gets Image object from image resources 
     * @aName (String) - the path to the resource.
     * if not specified as absolute path, the prefix
     * patht defined in imgPath  is added
     * handles .jpg, .gif, .png ...
     * @see clas saduc.ajc.img.ImageLoader for supported file types
     */
    public Image getImage(String aName) {
        Image image = (Image) images.get(aName);
        if(image==null) {
        	//workaround - need to have always full path
            if(aName.length()<32) aName = imgPath+aName;
            image = ImageLoader.getImage(aName);
            if(image!=null) {
                images.put(aName,image);
            }
        }
        return image;
    }

   /**
     * gets ImageIcon object from image resources 
     * @aName (String) - the path to the resource.
     * if not specified as absolute path, the prefix
     * patht defined in imgPath is added
     * handles .jpg, .gif, .png ...
     * @see clas saduc.ajc.img.ImageLoader for supported file types
     */
    public ImageIcon getImageIcon(String aName) {
        Image image = getImage(aName);
        if(image!=null) {
            return new ImageIcon(image);
        }
        return null;
    }
    
    /**
     * loads property file from resources
     * @param aName (String) -  the path to the resource.
     * if not specified as absolute path, the prefix
     * patht defined in resPath is added
     */
    public Properties getProperties(String aName) {
        Properties prop = (Properties) properties.get(aName);
        if(prop==null) {
            prop = new Properties();
            try {
                if(!aName.startsWith("/")) aName = imgPath+aName;
                prop.load(this.getClass().getResourceAsStream(aName));
            } catch(Exception e) {
                ErrorHandler.handleError(e);
                prop=null;
            }
            if(prop!=null) {
                properties.put(aName,prop);
            }
        }
        return prop;
    }
    
    /**
     * general function to return resource as input stream.
     * placed here to be able to do central modification
     * of loading resources
     */
    public InputStream getResourceAsStream(String aResName) {
        if(aResName.length()<32) aResName = resPath+aResName;
        InputStream resource=TetrisResource.class.getClassLoader().getResourceAsStream(aResName);
        return resource;
    }

    /**
     * loads XML resource and returns it's root element
     * @throws SAXException, IOException
     */
    public Element getXMLDocumentElement(String aResourceName) throws SAXException, IOException, ParserConfigurationException 
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document document = factory.newDocumentBuilder().parse(new InputSource(getResourceAsStream(aResourceName)));
        
        return document.getDocumentElement();
    }
    
    /**
     * returns the text of particular child element
     * throws NullPointerException if element is not found
     */
    public static String getXMLElementChildValue(Element aParent, String aChildName) 
    {
        NodeList childNodes=aParent.getElementsByTagName(aChildName).item(0).getChildNodes();
        for(int i=0; i<childNodes.getLength(); i++) {
            Node node=childNodes.item(i);
            if(node.getNodeType()==Node.TEXT_NODE) {
                return node.getNodeValue();
            }
        }
        return null;
    }

    /**
     * returns the text of particular child element converted to integer value
     * throws NullPointerException if element is not found
     */
    public static int getXMLElementChildIntValue(Element aParent, String aChildName) {
         return Integer.parseInt(getXMLElementChildValue(aParent, aChildName));
    }
    
}
