import java.awt.*;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public abstract class PianoTile {
	private int width;
	private int height;
	
	private String note;
	private int position;
	private int column;
	
	public PianoTile(String note, int position, int column) {
		this.width = 80;
		this.height = 140;
		
		this.note = note;
		this.position = position;
		this.column = column;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getPosition() {
		return position;
	}
	
	public int getColumn() {
		return column;
	}
	
	public String getNote() {
		return note;
	}
	
	public void move(int dy) {
		position += dy;
	}
	
	public void play() {
		String[] parts = this.getNote().split("/");
		new Thread (new Runnable() {
			public void run () {
				try {	
					String file = "files\\Sounds2\\"
							+ parts[0] + ".wav";
			    	AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(file).getAbsoluteFile());          
			        Clip clip = AudioSystem.getClip();
			        clip.open(audioInputStream);
			        clip.start();
			        Thread.sleep(1000);
			        clip.close();
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
			}
		}).start();	
	}
	
	public abstract void draw(Graphics g);
	
}
