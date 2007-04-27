/**
 * Java Tetris sample application
 * all of this code in all packages under org.saiko.games.tetris 
 * is realesed under the GNU public licence, which is included in the source
 * code folder.
 * 
 * author: Dusan Saiko
 * dusan@saiko.cz 
 */

package org.saiko.games.tetris;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;

import org.saiko.games.tetris.err.ErrorHandler;



/**
 * Tetris.java
 * @author  Saiko Dusan
 * @version 0.1
 * created on 27. 09. 2002, 21:44
 * last date modified: 2004-09-08
 * 
 *
 */
public class Tetris extends JFrame  {

    /**
     * static inicialization
     */
    static {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch(Exception e) {
            ErrorHandler.handleError(e);
        }
    }

    String language;
    
    
  /**
   * flag if the game is running - used when manipulating with action buttons
   */
  boolean running;
    
  /** the type of choosen tetris pieces set */
  byte CHOOSEN_SET_NORMAL=1;
  byte CHOOSEN_SET_HYBRID=2;
  int choosenSet=CHOOSEN_SET_NORMAL;

  /** current speed of falling pieces - time of delay in ms **/
  int currentSpeed;
   
  /**
   * the tetris plane board
   * the board consists of one string per field in the plane,
   * the field represents the image name of block laying on the field.
   * if field value==null, than no piece is laying on it.
   * the board contains only pieces, which are not faling down.
   */
  String board[][];

  /**
   * this is the plane with  background image and laying pieces
   */
  Component tetrisPlane;

  /*
   * this is the falling plane where mooving pieces are drawn
   * it lays on the tetrisPlane where the background and layed pieces are drawn
   */
  Component fallingPlane;

  /*
   * the plane where current speed is displayed
   */ 
  JLabel speedBoard;
  
  /*
   * this is the falling plane where mooving pieces are drawn
   * it lays on the tetrisPlane where the background and layed pieces are drawn
   */
  Component scoreBoard;

  /**
   * lock for drawing the scoreboard
   */
  Object scoreBoardLock=new String();
  
  /**
   * the current score counter
   */
  int currentScore;
  
  /*
   * component for previewing the next piece info
   */
  Component nextPlane;
  
  /**
   * falling piece parameters
   */
  TetrisPiece           fallingPiece=null;
  int   fallingPieceRotation;
  int   fallingPieceCenterX;
  int   fallingPieceCenterY;

  /**
   * two command buttons of the game - clear/randomize =b1 and start/stop=b2
   */
  JButton b1;
  JButton b2;

  /**
   * The GameOver object for implementing some text animations on the end of the game
   */
    class GameOver extends JLabel {
		private static final long serialVersionUID = 1L;

        boolean  isGameOver = false;
        boolean  disapearing=false;

        int angle;
        String text;

        AlphaComposite transparency05=AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        
        GameOver() {
            isGameOver=false;
            disapearing=false;
        }

        public void setText(String aText) {
            text=aText;
            repaint();
        }

        void gameOver() {
            isGameOver=true;
            for(int i=1;i<=50; i+=4) {
              angle=(int)(7.2*i);
              setFont(new Font("arial, helvetica",Font.BOLD|Font.ITALIC,i));
              repaint();
              try {Thread.sleep(15);} catch(Throwable e) {}
            }
        }

        synchronized void disapear() {
            if(isGameOver) {
                isGameOver=false;
                new Thread() {
                    public void run() {
                        disapearing=true;
                        for(int i=50;i>=0; i-=4) {
                          angle=(int)(7.2*i);
                          setFont(new Font("arial, helvetica",Font.BOLD|Font.ITALIC,i));
                          repaint();
                          try {Thread.sleep(15);} catch(Throwable e) {}
                        }
                        disapearing=false;
                        repaint();
                    }
                }.start();
            }
        }
        
        public void paint(Graphics g1) {
            super.paint(g1);

            if(isGameOver || disapearing) {
              Graphics2D  g=(Graphics2D) g1;
              g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

              FontMetrics fm=g.getFontMetrics();
              Rectangle r = getBounds();

              int width=fm.stringWidth(text);
              int height=fm.getHeight()*4/5;
              int cx=g.getFont().getSize()/10;
              int x=(r.width-width)/2;
              int xx=fm.charWidth('i');
              
              g.rotate(angle*Math.PI/180,config.planeWidth*config.pieceWidth/2,100+height/2);
              g.setColor(Color.black);
              g.setComposite(transparency05);
              g.fillRect(x-xx,110-height,width+2*xx,height);
              g.setComposite(AlphaComposite.SrcOver);
              g.drawString(text,x+cx,100+cx);
              g.setColor(Color.red);
              g.drawString(text,x,100);
            }
        }
    }  
  GameOver gameOver;
  
  /**
   * next piece parameters
   */
  TetrisPiece nextPiece=null;
  boolean showPreview;
  
  /**
   * lock object for synchronization
   */
  static final Object lock = new String();
  
  //the flag, that the game is finished;
  boolean finished = false; 
  
  /**
   * global random object
   */ 
   final Random rnd = new Random(System.currentTimeMillis());
    
  /** object with tetris configuration filled from TETRIS.XML **/
  final GameSettings config;

  TetrisResource resource=null;
  
  /**
   * @return resource object with cashing capabilities
   */
  public TetrisResource getResource() {
  	if(resource==null) {
  		resource=new TetrisResource(language);
  	}
  	return resource;
  }
  
  /** Constructor
   * Creates modalles dialog box with calculator over its parent frame
   */
  public Tetris(String configFile, String language) throws Exception {
        // call super constructor, set parent and modal=false
        this.setResizable(false);
        this.language=language;

        //set closing action
        this.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				dispose();
				System.exit(0);
			}
        });
        
        //set title and main icon
        this.setTitle(getResource().getString("main_title"));
        this.setIconImage(getResource().getImage("tetris_icon.gif"));

        //get the configuration
        config=GameSettings.getTetrisConfiguration(this, configFile);

        //set the current speed according to the configuration
        currentSpeed=config.initSpeed;
        
        
        setResizable(false);
        
        //put the background there as iconed label
        Icon iconBackground=getResource().getImageIcon(config.tetrisImage);
        JLabel background = new JLabel(iconBackground);
        background.setBounds(0,0,600,480);
        
        background.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        getContentPane().setBackground(null);
        getContentPane().add(background, java.awt.BorderLayout.CENTER);
        
        //pack the parent window to fit the background
        pack();        

        //place the window somewhere programmatically
        int width=iconBackground.getIconWidth();
        int height=iconBackground.getIconHeight();
        Rectangle screenRect = getGraphicsConfiguration().getBounds();
        int x = ((screenRect.width-width))/2;
        int y = (screenRect.height-height)/2;
        setLocation(x,y);

        showPreview=true;
        
        //preload all needed images
        preloadResources();
        
        //add components to the backround panel
        addComponents(background);
        
        createBoard();
  }

  /**
   * preloads all resources so it can be used during the game
   */
  void preloadResources()
  {
      //preload the scoreboard digit images
      String img=config.digitPath+config.digitPrefix;
      for(int i=0; i<10; i++) {
          getResource().getImage(img+i+config.digitPostfix);
      }
      getResource().getImage(img+"null"+config.digitPostfix);
      
      //preload the backgrounds
      img=config.backgroundPath+config.backgroundPrefix;
      for(int i=0; i<config.backgrounds; i++) {
      	getResource().getImage(img+(i+1)+config.backgroundPostfix);
      }
      
      //preload the pieces
      img=config.imagePath+config.imagePrefix;
      for(int i=0; i<config.piecesAll.length; i++) {
          TetrisPiece piece = config.piecesAll[i];
          getResource().getImage(img+piece.image+config.imagePostfix);
      }
      
      getResource().getImage(config.tetrisImage);
      getResource().getImage("button.gif");
      getResource().getImage("button_checked.gif");
      getResource().getImage("b2.gif");
      getResource().getImage("b2_down.gif");
      getResource().getImage("b2_disabled.gif");
      getResource().getImage("b1.gif");
      getResource().getImage("b1_disabled.gif");
      getResource().getImage("b1_down.gif");
 }
  
  public synchronized void start() {
    finished=false;

    currentSpeed=config.initSpeed;
    
    setScore(0);
    fallingPlane.requestFocus();
      
    new Thread(new Runnable() {
          public void run() {
              game();
          }
    }).start();
  }
  
  /*
   * creates the board according to the configuration and initializates it
   */ 
  void createBoard() {
      synchronized(lock) {
        board = new String[config.planeWidth][config.planeHeight];
      }
  }
  
  /** add GUI component to the background of calculator  */
   void addComponents(Container parent) {
       //well, we will add the components directly to it's position
       parent.setLayout(null);

       final JCheckBox isNextPiece=new JCheckBox(getResource().getImageIcon("button.gif"));
       Icon ico=getResource().getImageIcon("button_checked.gif");
       isNextPiece.setSelectedIcon(ico);
       isNextPiece.setBackground(Color.blue);
       isNextPiece.setMargin(new Insets(0,0,0,0));
       isNextPiece.setBorderPainted(false);
       isNextPiece.setBorder(null);
       isNextPiece.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
       isNextPiece.setBounds(115,16,ico.getIconWidth(),ico.getIconHeight());
       isNextPiece.setSelected(showPreview);
       parent.add(isNextPiece);
       isNextPiece.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent aEvent) {
               showPreview=isNextPiece.isSelected();
               nextPlane.repaint();
               fallingPlane.requestFocus();
           }
       });

       /* "show next piece" label */
       final JLabel labelNextPiece=new JLabel();
       labelNextPiece.setFont(new Font("helvetica",Font.BOLD,14));
       labelNextPiece.setBounds(20,15,130,22);
       labelNextPiece.setForeground(Color.cyan);
       parent.add(labelNextPiece);

       
       /*place some cool image on the tetris plane*/
       String imageName=config.backgroundPath+config.backgroundPrefix+String.valueOf(rnd.nextInt(config.backgrounds)+1)+config.backgroundPostfix;
       
       /** inner class to handle drawing of items on the background */
       class TetrisPlane extends JLabel {
           TetrisPlane(Icon aIco) {
               super(aIco);
           }
           
            public void paint(Graphics g) {
                super.paint(g);
                synchronized(lock) {
                    drawBoard(g);
                }
            } //paint
       } //TetrisPlane
       
       //falling pane is component over the tetris plane where falling pieces are shown
       class FallingPlane extends JLabel {
           
		private static final long serialVersionUID = 1L;

		FallingPlane() {
               addKeyListener(new KeyAdapter() {
                   public void keyPressed(KeyEvent e) {
                       gameKeyPressed(e);
                   }
               });
           }
           
           //set focusable using 1.4 feature method
            public boolean isFocusable() { return true; } 
            
            public void paint(Graphics g) {
                super.paint(g);
                synchronized(lock) {
                    if(fallingPiece!=null) {
                        drawPiece(this,g,fallingPieceCenterX, fallingPieceCenterY, fallingPiece, fallingPieceRotation);
                    }
                }
            } //paint
       } //FallingPlane
       
       gameOver = new  GameOver();
       gameOver.setBounds(config.planeX,config.planeY,config.planeWidth*config.pieceWidth,config.planeHeight*config.pieceHeight);
       parent.add(gameOver);

       tetrisPlane = new  TetrisPlane(getResource().getImageIcon(imageName));
       tetrisPlane.setBounds(config.planeX,config.planeY,config.planeWidth*config.pieceWidth,config.planeHeight*config.pieceHeight);
       parent.add(tetrisPlane);

       fallingPlane = new  FallingPlane();
       fallingPlane.setBounds(0,0,config.planeWidth*config.pieceWidth,config.planeHeight*config.pieceHeight);
       ((TetrisPlane)tetrisPlane).add(fallingPlane);


       //nextPlane is component where next piece is shown
       class NextPlane extends JLabel {
            public void paint(Graphics g) {
                super.paint(g);
                if(showPreview && nextPiece!=null) {
                    synchronized(nextPiece) {
                        drawPiece(this,g,-1, -1, nextPiece, 0);
                    }
                }
            } //paint
       } //NextPlane
       
       nextPlane=new NextPlane();
       nextPlane.setBounds(36,50,100,80);
       parent.add(nextPlane);
       
       
       /* "score" label */
       final JLabel labelScore=new JLabel();
       labelScore.setFont(new Font("helvetica",Font.BOLD,14));
       labelScore.setBounds(20,130,200,22);
       labelScore.setForeground(Color.cyan);
       parent.add(labelScore);
       
       class ScoreBoard extends JLabel {
            public void paint(Graphics g) {
                super.paint(g);
                  synchronized(scoreBoardLock) {
                        drawScoreBoard(g, currentScore);
                  }
            } //paint
       } //NextPlane
       
       scoreBoard=new ScoreBoard();
       scoreBoard.setBounds(30,160,150,50);
       parent.add(scoreBoard);

       /* "score" label */
       final JLabel labelSpeed=new JLabel();
       labelSpeed.setFont(new Font("helvetica",Font.BOLD,14));
       labelSpeed.setBounds(20,220,65,22);
       labelSpeed.setForeground(Color.cyan);
       parent.add(labelSpeed);

       class SpeedBoard extends JLabel {
            public void paint(Graphics g) {
                super.paint(g);
                  synchronized(scoreBoardLock) {
                        drawScoreBoard(g,1+(config.initSpeed-currentSpeed)/50);
                  }
            } //paint
       } //NextPlane
       
       speedBoard=new SpeedBoard();
       speedBoard.setBounds(30,250,150,50);
       parent.add(speedBoard);
       
       
       running=false;
       /**
        * add the game-operating buttons
        */

       b1 = new JButton(getResource().getImageIcon("b1.gif"));
       b1.setDisabledIcon(getResource().getImageIcon("b1_disabled.gif"));
       ico=getResource().getImageIcon("b1_down.gif");
       b1.setPressedIcon(ico);
       b1.setBorder(null);
       b1.setBorderPainted(false);
       b1.setMargin(new Insets(0,0,0,0));
       b1.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
       b1.setBounds(25,330,ico.getIconWidth(),ico.getIconHeight());
       b1.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent aEvent) {
               gameOver.disapear();
               randomizeTetrisPlane();
               synchronized(lock) {
                    currentScore=0;
                    currentSpeed=config.initSpeed;
               }
               scoreBoard.repaint();
               speedBoard.repaint();
               b2.setEnabled(true);
           }
       });       
       parent.add(b1);

       b2 = new JButton(getResource().getImageIcon("b2.gif"));
       b2.setDisabledIcon(getResource().getImageIcon("b2_disabled.gif"));
       ico=getResource().getImageIcon("b2_down.gif");
       b2.setPressedIcon(ico);
       b2.setBorder(null);
       b2.setBorderPainted(false);
       b2.setMargin(new Insets(0,0,0,0));
       b2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
       b2.setBounds(105,330,ico.getIconWidth(),ico.getIconHeight());
       b2.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent aEvent) {
               running=!running;
               if(!running) {
                   synchronized(lock) {
                       finished=true;
                   }
                   b1.setEnabled(true);
               } else {
                   b1.setEnabled(false);
                   start();
               }
               fallingPlane.requestFocus();
           }
       });       
       parent.add(b2);
       b2.requestFocus();
       
       //set text labels
       labelScore.setText(getResource().getString("score"));
       labelSpeed.setText(getResource().getString("speed"));
       b2.setToolTipText(getResource().getString("play_stop"));
       b1.setToolTipText(getResource().getString("clear"));
       gameOver.setText(getResource().getString("game_over"));

       isNextPiece.setToolTipText(getResource().getString("show_preview"));
       String showPreview=getResource().getString("show_pieces");
       labelNextPiece.setText(showPreview);
       FontMetrics fm=labelNextPiece.getFontMetrics(labelNextPiece.getFont());
       isNextPiece.setLocation(20+fm.stringWidth(showPreview)+fm.charWidth('w'),16);
       
   }

   /**
    * randomizes the TetrisPlane 
    * if the plane is filled with something, the plane is cleared
    * otherwise it is filled with some garbage
    **/
   synchronized void randomizeTetrisPlane() 
   {
       //empty the board. if was not empty, do not do anything else
       boolean empty=true;
       for(int x=0; x<config.planeWidth; x++) {
           for(int y=0; y<config.planeHeight; y++) {
               if(board[x][y]!=null) empty=false;
               board[x][y]=null;
           }
       }
       if(empty) {
           //place some new background image
           String imageName=config.backgroundPath+config.backgroundPrefix+String.valueOf(rnd.nextInt(config.backgrounds)+1)+config.backgroundPostfix;
           ((JLabel)tetrisPlane).setIcon(getResource().getImageIcon(imageName));
           
           TetrisPiece[] pieces = (choosenSet==CHOOSEN_SET_NORMAL ? config.piecesNormal : config.piecesAll);
           
           //fill 2/3 of the height of the plane as maximum
           int height=rnd.nextInt((config.planeHeight*2/3)-3)+3;
           for(int y=0; y<height; y++) {
               String[] row=new String[config.planeWidth];
               for(int i=0; i<row.length; i++) row[i]=null;
               //there will be from 1 to 15 holes on the line
               int holes=rnd.nextInt((config.planeWidth*2)/3)+3;
               for(int i=0; i<holes; i++) {
                   row[rnd.nextInt(config.planeWidth)]="";
               }
               //fill the rest with image blocks
               for(int i=0; i<row.length; i++) {
                   if(row[i]==null) {
                       row[i]=pieces[rnd.nextInt(pieces.length)].image;
                   } else {
                       row[i]=null;
                   }
               }
               //copy the prepared row to the board
               for(int i=0; i<row.length; i++) {
                   board[i][config.planeHeight-1-y]=row[i];
               }
           }
       }
       tetrisPlane.repaint();
   }
   
   /**
    * sets the score and repaints the scoreboard
    */
   void setScore(int aScore) {
       int oldScore;
       boolean speedRepaint=false;
       
       //the breakpoints where speed changes
       int speedScoreBreakpoints[] = new int[] {1000,3000,7000,15000,31000};
       
       synchronized(scoreBoardLock) {
           oldScore=currentScore;
           currentScore=aScore;
           
           //if there is change in 1000 than change speed
           for(int i=0; i<speedScoreBreakpoints.length;i++) {
               //this way we handle the case that for example from 0 points
               //player makes 5000 points at once
               if(oldScore<speedScoreBreakpoints[i] && currentScore>=speedScoreBreakpoints[i]) {
                    currentSpeed=Math.max(50,currentSpeed-50);
                    speedRepaint=true;
               }
           }
       }
       scoreBoard.repaint();
       if(speedRepaint) {
           speedBoard.repaint();
       }
   }
   
   /**
    * add points to score
    */
   void addScore(int aIncrement) {
       int newScore;
       synchronized(scoreBoardLock) {
          newScore=currentScore+aIncrement;
       }
       
       //call setScore to handle some common actions when setting the score
       setScore(newScore);
   }
   
   /**
    * draws the right score on scoreboard
    * if aScore is <0, then blank scoreboard is drawn
    */
   public void drawScoreBoard(Graphics aG, int aScore) {
       //let d be the array of displayed 5 digits. if d contains null,
       //image digits_null will be displayed
       String d[]=new String[5];
       
       //adjust the score string to be just 5 digits
       String score=(aScore>=0 ? String.valueOf(aScore) : ""); 
       while(score.length()<5) {
           score=" "+score;
       }
       if(score.length()>5) {
           score=score.substring(score.length()-5);
       }
       
       //init the array of digits and fill it with current score digits
       for(int i=0; i<d.length; i++) d[i]=null;
       for(int i=0; i<score.length(); i++) {
           char c=score.charAt(i);
           if(c==' ') {
              d[i]="null";
           } else {
              d[i]=String.valueOf(c);
           }
       }
       
       for(int i=0; i<d.length; i++) {
           Image img=getResource().getImage(config.digitPath+config.digitPrefix+d[i]+config.digitPostfix);
           aG.drawImage(img,i*(img.getWidth(null)+3),0,null);
       }
   }
   
   /** deaws the laying pieces **/
   void drawBoard(Graphics g) {
       if(board==null) return;
       
       String imgPath=config.imagePath+config.imagePrefix;
       for(int y=0; y<config.planeHeight; y++) {
            for(int x=0; x<config.planeWidth; x++) {
                if(board[x][y]!=null) {
                   Image blockImg = getResource().getImage(imgPath+board[x][y]+config.imagePostfix);
                   g.drawImage(blockImg,x*config.pieceWidth,y*config.pieceHeight,null);
                }
            }
       }
   } //drawBoard
   
   /**
    * draws the tetris piece at specific position
    */
   void drawPiece(Component component, Graphics g, int centerX, int centerY, TetrisPiece piece, int rotationIndex) {
       TetrisPieceRotation rotation = piece.rotations[rotationIndex];
       
       int xStart;
       int y=0;
       
       if(centerX==-1 && centerY==-1) {
           //center the graphics
           Rectangle bounds=component.getBounds();
           int cx=rotation.width*config.pieceWidth;
           xStart = (bounds.width-cx)/2;
       } else {
            xStart=(centerX-rotation.centerX)*config.pieceWidth;
            y=(centerY-rotation.centerY)*config.pieceHeight;
       }
       
       int x=xStart;
       int dataPos=0;

       Image blockImg = getResource().getImage(config.imagePath+config.imagePrefix+piece.image+config.imagePostfix);
       
       //for rows
       for(int r=0; r<rotation.height; r++) {
           //for columns
           for(int c=0; c<rotation.width; c++) {
               char flag = rotation.map.charAt(dataPos);
               if(flag=='1') {
                   g.drawImage(blockImg,x,y,null);
               }
               x+=config.pieceWidth;
               dataPos++;
           }
           x=xStart;
           y+=config.pieceHeight;
       }
   } //drawPiece
   

 
  //the game by itself
  public void game() {
      currentSpeed=config.initSpeed;
      speedBoard.repaint();

      // get choosen set
      TetrisPiece[] pieces = (choosenSet==CHOOSEN_SET_NORMAL ? config.piecesNormal : config.piecesAll);

      class MergingThread extends Thread {
          TetrisPiece piece;
          int rotation;
          int centerX;
          int centerY;
          
          void mergeFirst() {
            synchronized(lock) {
                merge(piece,rotation,centerX,centerY);
            }
            tetrisPlane.repaint();
          }
          
          public void run() {
              doMerging(piece, rotation, centerX, centerY);
          }
          
          MergingThread(TetrisPiece aPiece, int aRotation, int aCenterX, int aCenterY) {
              piece=aPiece;
              rotation=aRotation;
              centerX=aCenterX;
              centerY=aCenterY;
          }
      };
 
while(!finished) {      
      //select random piece and its random rotation
      TetrisPiece piece=null;
      if(nextPiece==null) {
         piece= pieces[rnd.nextInt(pieces.length)];
         nextPiece= pieces[rnd.nextInt(pieces.length)];
      } else {
         synchronized(nextPiece) {
            piece=nextPiece;
            nextPiece= pieces[rnd.nextInt(pieces.length)];
         }
      }
      nextPlane.repaint();
        
      int rotation = rnd.nextInt(piece.rotations.length);
      
      //current y position of the center point is so the component fits
      //current x position is random
      int centerY=piece.rotations[rotation].centerY;

      int centerX=rnd.nextInt(config.planeWidth-piece.rotations[rotation].width+1)+piece.rotations[rotation].centerX;
      synchronized(lock) {
          fallingPiece=piece;
          fallingPieceRotation=rotation;
          fallingPieceCenterX=centerX;
          fallingPieceCenterY=centerY;
      }

      /* zmena ZM */
      addScore((config.initSpeed - currentSpeed) / 50 + 1);

      boolean falling=true;
      if(!canExists(fallingPiece.rotations[fallingPieceRotation],fallingPieceCenterX,fallingPieceCenterY)) {
          finished=true;
          running=false;
          synchronized(lock) {
              fallingPiece=null;
          }
          b1.setEnabled(false);
          b2.setEnabled(false);
          gameOver.gameOver();
          b1.setEnabled(true);
      }
      while(falling && !finished) {
          fallingPlane.repaint();
          try {
            Thread.sleep(currentSpeed);
          } catch(Throwable e) {}
          synchronized(lock) {
              falling=moveDown();
          }          
      } //while falling

      //the piece has fall on the ground, merge it with board
      //start the merging process
      //prepare merging theread
      //the merging pieces with background is OK,
      //but if the line is going to be deleted, 
      //it involves some more animation
      if(!finished) {
          MergingThread mt;
          synchronized(lock) {
              mt=new MergingThread(fallingPiece,fallingPieceRotation,fallingPieceCenterX,fallingPieceCenterY);
          }
          mt.mergeFirst();
          mt.start();
      }
}
      synchronized(lock) {
          fallingPiece=null;
      }
      fallingPlane.repaint();
  }

  /*
   * the merging thread. it merges an piece to the background and checks
   * which lines are full and deletes these lines
   * this method hav to be synchronized, otherwise it could happen, it is run for two times
   */
  synchronized void doMerging(TetrisPiece aPiece, int aRotation, int aCenterX, int aCenterY) {
     
       Random rnd2 = new Random(System.currentTimeMillis());
       int line=-1;
       int lines=-1;
       do {
           lines++;
           line=-1;
           synchronized(lock) {
               for(int y=config.planeHeight-1; y>=0; y--) {
                  boolean all=true;
                  for(int x=0; x<config.planeWidth; x++) {
                      if(board[x][y]==null) { all=false; break; }
                  }
                  if(all) {
                      line=y;
                      break;
                  }
               }
           } //synchronized block
           
           if(line!=-1) {
               //delete line LINE - choose effect
               switch(rnd2.nextInt(2)) {
                   case 0: //deleting from left to the right
                           for(int x=0; x<config.planeWidth; x++) {
                               synchronized(lock) {
                                   board[x][line]=null;
                               }
                               tetrisPlane.repaint();
				/* Zmena ZM */	
                               addScore(((config.initSpeed - currentSpeed + 50) / 50) * 2 * (lines + 1));
                               try { Thread.sleep(15); } catch(Throwable e) {}
                           }
                           break;
                   default: //deleting from right to the left
                           for(int x=config.planeWidth-1; x>=0; x--) {
                               synchronized(lock) {
                                   board[x][line]=null;
                               }
                               tetrisPlane.repaint();
				/* Zmena ZM */	
                               addScore(((config.initSpeed - currentSpeed + 50) / 50) * 2 * (lines + 1));
                               try { Thread.sleep(15); } catch(Throwable e) {}
                           }
                           break;
               } //switch
               //shift the rest downward
               synchronized(lock) {
                   for(int y=line; y>=0; y--) {
                       for(int x=0; x<config.planeWidth; x++) {
                          board[x][y]=(y<1 ? null : board[x][y-1]);
                       }
                   }
               }
               tetrisPlane.repaint();
           } //line !=-1
       } while(line!=-1);
  }
  
  /**
   * merge the specified tetris piece with background
   */
  void merge(TetrisPiece aPiece, int aRotation, int aCenterX, int aCenterY) {
      
      String img=aPiece.image;
      TetrisPieceRotation rotation = aPiece.rotations[aRotation];
      
      int xStart=(aCenterX-rotation.centerX);
      int x=xStart;
      int y=(aCenterY-rotation.centerY);
      int dataPos=0;

      //for rows
      for(int r=0; r<rotation.height; r++) {
         //for columns
         for(int c=0; c<rotation.width; c++) {
            char flag = rotation.map.charAt(dataPos);
            if(flag=='1') {
                board[x][y]=img;
            }
            x++;
            dataPos++;
         }
         x=xStart;
         y++;
      }
  }
  
  /**
   * rotate the falling piece
   */
  boolean rotate() {
      int r=fallingPieceRotation+1;
      if(fallingPiece.rotations.length<=r) r=0;
      //is left side OK ?
      if(fallingPieceCenterX-fallingPiece.rotations[r].centerX>=0) {
          //is right side OK ?
          if(fallingPieceCenterX+(fallingPiece.rotations[r].width-fallingPiece.rotations[r].centerX)<=config.planeWidth) {
              //is bottom OK ?
              if(fallingPieceCenterY+(fallingPiece.rotations[r].height-fallingPiece.rotations[r].centerY)<=config.planeHeight) {
                  //is there placed anything ?
                  if(canExists(fallingPiece.rotations[r],fallingPieceCenterX,fallingPieceCenterY)) {
                      fallingPieceRotation=r;
                      return true;
                  }
              }
          }
      }
      return false;
  }
  
  /**
   * move left, if possible
   */
  boolean moveLeft() {
      if(fallingPieceCenterX-fallingPiece.rotations[fallingPieceRotation].centerX>0) {
          if(canExists(fallingPiece.rotations[fallingPieceRotation],fallingPieceCenterX-1,fallingPieceCenterY)) {
              fallingPieceCenterX--;
              return true;
          }
      }
      return false;
  }

  /**
   * move right, if possible
   */
  boolean moveRight() {
      if(fallingPieceCenterX+(fallingPiece.rotations[fallingPieceRotation].width-fallingPiece.rotations[fallingPieceRotation].centerX)<config.planeWidth) {
          if(canExists(fallingPiece.rotations[fallingPieceRotation],fallingPieceCenterX+1,fallingPieceCenterY)) {
              fallingPieceCenterX++;
              return true;
          }
      }
      return false;
  }  
  
  /**
   * move down, if possible
   */
  boolean moveDown() {
      if(fallingPiece==null) return false;
      if(fallingPieceCenterY+(fallingPiece.rotations[fallingPieceRotation].height-fallingPiece.rotations[fallingPieceRotation].centerY)<config.planeHeight) {
          if(canExists(fallingPiece.rotations[fallingPieceRotation],fallingPieceCenterX,fallingPieceCenterY+1)) {
              fallingPieceCenterY++;
              return true;
          }
      }
      return false;
  }  
  
  /**
   * checks, if the moving piece can lie on the specific position - if there is not
   * another piece already
   */
  boolean canExists(TetrisPieceRotation aRotation, int aX, int aY) {
      if(board==null) return false;
      
      int xStart=(aX-aRotation.centerX);
      int x=xStart;
      int y=(aY-aRotation.centerY);
      int dataPos=0;

      //for rows
      for(int r=0; r<aRotation.height; r++) {
         //for columns
         for(int c=0; c<aRotation.width; c++) {
            //I do not check for top boundary when rotating the piece
            //it may happend, that the positions of some part gets below
            //zero, but it does not matter and it is OK to ignore it.
            if(x>=0 && y>=0) {
                char flag = aRotation.map.charAt(dataPos);
                if(flag=='1') {
                    if(board[x][y]!=null) return false;
                }
            }
            x++;
            dataPos++;
         }
         x=xStart;
         y++;
      }
      return true;
  }
  
  /** handlerfor key events when palying **/
  void gameKeyPressed(KeyEvent e) {
      boolean change=false;
      synchronized(lock) {
          if(fallingPiece!=null) {
              switch(e.getKeyCode()) {
                  case KeyEvent.VK_LEFT: change=moveLeft(); break;
                  case KeyEvent.VK_RIGHT:  change=moveRight(); break;
                  case KeyEvent.VK_DOWN:  change=moveDown(); break;
                  case KeyEvent.VK_UP: change=rotate(); break;
              }
          }
      } //synchronized
      if(change) 
        fallingPlane.repaint();
  }
  
   /**
    * main method to run the tetris application
    * by itself
    */
   static public void main(String[] args) {
       try {
        //look for english command line argument
        boolean czech=false;
        if(args!=null) {
        	for(int i=0; i<args.length; i++) {
        		if(args[i].toUpperCase().equals("CZ")) {
        			czech=true;
        			break;
        		}
        	}
        }
        String language=(czech ? "cz" : "en");
            	
        String configFile = "org/saiko/games/tetris/tetris_normal.xml";
        Tetris tetris=new Tetris(configFile, language);
           
        tetris.show();
       } catch(Throwable e) {
           ErrorHandler.handleError(e);
       }
   }
}