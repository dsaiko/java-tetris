package org.saiko.games.tetris.img;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.swing.ImageIcon;

import org.saiko.games.tetris.err.ErrorHandler;

/**
 * AJC.java
 * @author  Saiko Du�an
 * @version 0.1
 * created on 7. 09. 2002, 22:41
 * last date modified: 2002-12-10
 * 
 * The main class for the AJC project
 *
 * History:
 *  2002-12-10  code revision, code documentation
 *  2002-12-10  all the code reorganized
 *
 * TODO:
 *  2002-12-10  Add more supported file types ...
 */
public class ImageLoader {
    
    /**
     * load image from its resource path
     * @aPath (String) full resource path
     */
    public static Image getImage(String aPath) {
        ImageIcon icon = null;
        try {
            InputStream input = ImageLoader.class.getClassLoader().getResourceAsStream(aPath);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte b[]= new byte[1024*4];
            int size=0;
            while((size=input.read(b))>0) {
                buffer.write(b,0,size);
            }
            input.close();

            icon = new ImageIcon(buffer.toByteArray());
        } catch(Throwable e) {
            ErrorHandler.handleError(e);
            e.printStackTrace();
            icon = null;
        }
        
        Image image = null;
        if(icon!=null) image=icon.getImage();
        return image;
    }
}
