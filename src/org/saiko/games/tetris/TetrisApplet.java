package org.saiko.games.tetris;

import java.applet.Applet;
import java.awt.Color;

public class TetrisApplet extends Applet {

	private static final long	serialVersionUID	= 1L;

	@Override
	public void init() {
		super.init();
		setBackground(Color.decode("#333439"));
	}
	
	@Override
	public void start() {
		super.start();
        String configFile = "org/saiko/games/tetris/tetris_normal.xml";
		try {
			Tetris t = new Tetris(configFile, "en");
			add(t.getContentPane());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void stop() {
		super.stop();
	}
	
	
	@Override
	public void destroy() {
		super.destroy();
	}
}