package org.saiko.games.tetris.err;

/**
 * ErrorHandler.java
 * @author  dusan.saiko@gmail.com
 * @version 0.1
 * created on 12. 09. 2002, 19:14
 * last date modified: 2002-12-11
 * 
 * Class to handle the errors
 *
 * History:
 *  2002-12-10  code revision, code documentation
 *
 * TODO:
 *  2002-12-10 handle the errors somehow
 */
public class ErrorHandler {
    
    /**
     * handle the errors in common way
     */
    public static void handleError(Throwable e) {
        e.printStackTrace();
    }
    
}
