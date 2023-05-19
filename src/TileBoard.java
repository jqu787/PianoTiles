import java.awt.*;
import java.awt.event.*;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.swing.Timer;
import java.time.LocalDateTime;

import java.util.*;
import java.io.*;
import java.nio.file.Paths;

@SuppressWarnings("serial")
public class TileBoard extends JPanel {
	private LinkedList<PianoTile> tiles = new LinkedList<PianoTile>();
	
	public boolean playing = false;
	private JLabel status;
	private int counter;
	private JLabel score;
	
	public static final int COURT_WIDTH = 320;
    public static final int COURT_HEIGHT = 800;
    public static final String SHEET_MUSIC_LOC = "files\\Sheet Music";
    
    public static final String SCORES_LOC = "files\\Scores\\scores.txt";
    private ArrayList<String> high_scores = new ArrayList<String>();
    
    private int interval = 5;
    private int tile_velocity = 1;
    private int time_counter = 0;
    
    public TileBoard(JLabel status, JLabel score) {
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        Timer timer = new Timer(interval, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	time_counter+=5;
            	if(time_counter % 10000 == 0) {
            		tile_velocity++;
            	}
                tick();
            }
        });
        timer.start();
        
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
            	int x = e.getX();
            	int y = e.getY();
            	
            	handleMousePressed(x, y);
            }
            
            public void mouseReleased(MouseEvent e) {
            	handleMouseReleased();
            }
            
        });
        
        addMouseMotionListener(new MouseAdapter() {
        	public void mouseDragged(MouseEvent e) {
        		int x = e.getX();
        		int y = e.getY();
        		
        		handleMouseDragged(x, y);
        	}
        	
        	public void mouseMoved(MouseEvent e) {
        		int x = e.getX();
        		int y = e.getY();
        		
        		handleMouseMoved(x, y);
        	}
        });

        this.status = status;
        this.score = score;
        
        readScores();
    }
    
    public int getScore() {
    	return counter;
    }
    
    public LinkedList<PianoTile> getTiles() {
    	return tiles;
    }
    
    public void setTiles(LinkedList<PianoTile> tiles) {
    	this.tiles = tiles;
    }
    
    public ArrayList<Integer> getHighScores() {
    	ArrayList<Integer> output = new ArrayList<Integer>();
    	for (String s : high_scores) {
    		String[] parts = s.split(" ");
			int high_score = Integer.parseInt(parts[0]);
			output.add(high_score);
    	}
    	return output;
    }
    
    public boolean isPlaying() {
    	return playing;
    }
    
    public void setPlaying(boolean b) {
    	playing = b;
    }
    
    
    public void handleMousePressed(int x, int y) {
    	if (!tiles.isEmpty() && playing) {
    		PianoTile tile = tiles.peek();
    		if (tile instanceof Slider) {
    			Slider s = (Slider) tile;
    			if (x / s.getWidth() == s.getColumn() &&
        			y >= s.getHitboxPosition() &&
        			y <= s.getHitboxPosition() + s.getHitboxHeight()) {
        			s.hitBoxClicked();
    				s.play();
    				counter += 10;
        		}
    			else youLose();
    		}
    		else if (tile instanceof Clicker) {
    			Clicker c = (Clicker) tile;
    			if (x / c.getWidth() == c.getColumn() &&
        			y >= c.getPosition() &&
        			y <= c.getPosition() + c.getHeight()) {
        			c.incrCounter();
        			counter += 10;
        			if (c.getCounter() == 1) {
        				c.play();
        			} 			
        		}
    			else youLose();
    			
    		}
    		else if (tile instanceof Tapper){
    			if (x / tile.getWidth() == tile.getColumn() &&
        			y >= tile.getPosition() &&
        			y <= tile.getPosition() + tile.getHeight()) {
        			tile.play();
        			tiles.remove(tile);
        			counter += 10;
        		}
    			else youLose();
    		}
    	}
    }
    
    public void handleMouseReleased() {
    	if (!tiles.isEmpty() && playing) {
    		PianoTile tile = tiles.peek();
    		if (tile instanceof Slider) {
    			Slider s = (Slider) tile;
    			if (s.clicked()) {
    				if (s.getHitboxPosition() - s.getPosition() > 30) {
    					youLose();
    				}
    			}
    		}
    	}
    }
    
    public void handleMouseDragged(int x, int y) {
    	if (!tiles.isEmpty() && playing) {
    		PianoTile tile = tiles.peek();
    		if (tile instanceof Slider) {
    			Slider s = (Slider) tile;
    			if (x / s.getWidth() == s.getColumn() &&
        			y >= s.getHitboxPosition() &&
        			y <= s.getHitboxPosition() + s.getHitboxHeight()) {
    			}
    			else if (s.clicked() && (s.getHitboxPosition() - s.getPosition() > 30)){
    				youLose();
    			}
    		}
    		else if (tile instanceof Clicker) {
    			Clicker c = (Clicker) tile;
    			if (x / c.getWidth() == c.getColumn() &&
	        			y >= c.getPosition() &&
	        			y <= c.getPosition() + c.getHeight()) {
    			}
    			else {
    				if (c.getCounter() >= c.getThreshold())
    					tiles.remove(tile);
    			}
    		}
        }
    }
    
    public void handleMouseMoved(int x, int y) {
    	if (!tiles.isEmpty() && playing) {
    		PianoTile tile = tiles.peek();
    		if (tile instanceof Clicker) {
    			Clicker c = (Clicker) tile;
    			if (x / c.getWidth() == c.getColumn() &&
	        			y >= c.getPosition() &&
	        			y <= c.getPosition() + c.getHeight()) {
    			}
    			else {
    				if (c.getCounter() >= c.getThreshold())
    					tiles.remove(tile);
    			}
    		}
    	}
    }
    
    private void readScores() {
    	File file = new File(SCORES_LOC);
    	BufferedReader reader = null;
    	try {
    	    reader = new BufferedReader(new FileReader(file));
    	    String line;
    	    while ((line = reader.readLine()) != null) {
    	    	high_scores.add(line);
    	    }
    	} catch (IOException e) {
    	    e.printStackTrace();
    	} finally {
    	    try {
    	        reader.close();
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    	}
    }
      
    private void youLose() {
    	playing = false;
		status.setText("You lose!");
		new Thread (new Runnable() {
			public void run () {
				try {	
					String file = "files\\Sounds2\\fail.wav";
			    	AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(file).getAbsoluteFile());          
			        Clip clip = AudioSystem.getClip();
			        clip.open(audioInputStream);
			        clip.start();
			        Thread.sleep(3000);
			        clip.close();
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
			}
		}).start();	;
		
		if (high_scores.isEmpty() && counter > 0) {
			String output = counter + " (" + LocalDateTime.now() + ")";
			high_scores.add(output);
		}
		
		for (String s : high_scores) {
			String[] parts = s.split(" ");
			int high_score = Integer.parseInt(parts[0]);
			if (counter > high_score) {
				String output = counter + " (" + LocalDateTime.now() + ")";
				high_scores.add(high_scores.indexOf(s), output);
				break;
			}	
		}
		
		if (high_scores.size() < 5) {
			String output = counter + " (" + LocalDateTime.now() + ")";
			high_scores.add(output);
		}
		
		File file = Paths.get(SCORES_LOC).toFile();
		BufferedWriter br = null;
		try {
			br = new BufferedWriter(new FileWriter(file, false));
			for (String s : high_scores) {
				if(s != null) {
					br.write(s);
					br.newLine();
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private int calcDelay(String note) {
    	int delay = 250;
    	String[] parts = note.split("/");
		if (parts.length > 1) {
			double temp = Double.parseDouble(parts[1]);
			if (temp < 0.25) {
				temp = (0.25 - temp) * 0.5 + temp; 
			}
			else if (temp > 0.25) {
				temp = temp - (temp - 0.25) * 0.5; 
			}
			double factor = temp / 0.25;
			delay = (int)((double)delay * factor);
		}
		return delay;
    }
    
    private boolean isLongNote(String note) {
    	String[] parts = note.split("/");
		if (parts.length > 1) {
			double temp = Double.parseDouble(parts[1]);
			double factor = temp / 0.25;
			return factor > 1;
		}
		return false;
    }
    
    private void createTiles() {
    	File dir = new File(SHEET_MUSIC_LOC);
    	FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".txt");
            }
        };
    	File[] files = dir.listFiles(filter);
    	
    	int index = (int)(Math.random() * files.length);
    	File file = files[index];
    	BufferedReader reader = null;
    	try {
    	    reader = new BufferedReader(new FileReader(file));
    	    String line;
    	    int delay = 250;
    	    while ((line = reader.readLine()) != null) {
    	        String[] notes = line.split(" ");
    	        for(String note : notes) {
    	        	int position;
    	        	if (tiles.isEmpty()) {
    	        		position = 0;
    	        	}
    	        	else {
    	        		position = tiles.peekLast().getPosition() - delay;
    	        	}
    	        	int column = (int)(Math.random() * 4);
    	        	
    	        	if (isLongNote(note)) {
    	        		int temp = (int)(Math.random() * 2);
    	        		if (temp == 0) {
    	        			tiles.add(new Slider(note, position, column));
    	        			tiles.peekLast().setPosition(position - (tiles.peekLast().getHeight() - 140));
    	        		}
    	        		else if (temp == 1) {
    	        			tiles.add(new Clicker(note, position, column));
    	        		}
    	        	}
    	        	else {
    	        		tiles.add(new Tapper(note, position, column));  
    	        	}
    	        	
	        		delay = calcDelay(note);
    	        }
    	    }
    	} catch (IOException e) {
    	    e.printStackTrace();
    	} finally {
    	    try {
    	        reader.close();
    	    } catch (IOException e) {
    	        e.printStackTrace();
    	    }
    	}
    }
    
    public void reset() {
    	tiles.clear();
    	
    	createTiles();

        playing = true;
        status.setText("Running...");
        score.setText("Score: 0");
        counter = 0;
        
        tile_velocity = 1;
        time_counter = 0;

        requestFocusInWindow();
    }
    
    void tick() {
    	if (playing) {
    		if (tiles.size() < 10) {
        		createTiles();
    		}	
    		for (PianoTile tile : tiles) {
    			tile.move(tile_velocity);
    		}		
    		PianoTile tile = tiles.peek();
    		if (tile instanceof Slider) {
    			Slider s = (Slider) tile;
    			if (s.getHitboxPosition() < s.getPosition()) {
    				if (s.clicked()) {
    					double factor = (double)(s.getHeight() / 140.0);
    					counter += (int)(factor * 10.0);
    					tiles.remove(tile);
    				}	
    				else {
    					youLose();
    				}
    			}
    			
    			int bottom = s.getHitboxPosition() - s.getPosition() + s.getHitboxHeight();
    			if (tile.getPosition() + bottom > COURT_HEIGHT) {
        			youLose();
    			}
    		}
    		else if (tile instanceof Tapper || tile instanceof Clicker) {
    			if (tile.getPosition() + tile.getHeight() > COURT_HEIGHT) {
    				youLose();
    			}	
    		}
    	}
    	
    	score.setText("Score: " + counter);
    	repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (PianoTile tile : tiles) {
        	tile.draw(g);
        }
        
        g.drawString("High Scores", 415, 20);
        int index = 0;
        if (!high_scores.isEmpty()) {
        	for (String s : high_scores) {
	        	if (index < 5) {
	        		g.drawString(high_scores.indexOf(s) + 1 + ". " + s, 330, 20 + (index + 1) * 20);
	        		index++;
	        	}	
        	}
        }
        
        g.drawString("How To Play", 414, 160);
        ArrayList<String> instructions = new ArrayList<String>();
        instructions.add("Welcome to PianoTiles!! Your goal is to");
        instructions.add("click as many tiles as possible (you can");
        instructions.add("use both the left and right mouse buttons).");
        instructions.add("There is no time limit, but beware, the tiles");
        instructions.add("tiles will speed up over time! Try to crack");
        instructions.add("the leaderboard!");
        instructions.add("");
        instructions.add("You can also add custom songs to the");
        instructions.add("playlist. Simply create a text file in");
        instructions.add("'files/Scores/' folder and start adding notes.");
        instructions.add("Each note is represented by a String");
        instructions.add("containing a capital letter A-Z representing");
        instructions.add("the note and a subsequent number, 0-7,");
        instructions.add("representing the octave. Note that only B");
        instructions.add("and B flat are represented in octave 0. Flats");
        instructions.add("are represented by adding a lowercase 'b'");
        instructions.add("after the capital letter and before the");
        instructions.add("number. For example, D flat in the fourth");
        instructions.add("octave would be written as Db4. There are");
        instructions.add("no sharps. You can also specify long and");
        instructions.add("short notes by adding '/x' at the end of");
        instructions.add("the String, where x is a decimal");
        instructions.add("representing a fraction of a whole note.");  
        instructions.add("");
        instructions.add("Black Tile: Click once (10 pts)");
        instructions.add("Blue Tile: Click black hitbox, hold until end");
        instructions.add("of slider (10 pts for clicking hitbox, bonus");
        instructions.add("depending on length of slider)");
        instructions.add("Red Tile: Click indicated number of times");
        instructions.add("(10 pts for each click, can click more than");
        instructions.add("number of times");
        for (String instruction : instructions) {
        	g.drawString(instruction, 330, 180 + instructions.indexOf(instruction) * 20);
        }
        
         
        g.setColor(Color.black);
        g.drawLine(COURT_WIDTH / 4 * 0, 0, COURT_WIDTH / 4 * 0, COURT_HEIGHT);
        g.drawLine(COURT_WIDTH / 4 * 1, 0, COURT_WIDTH / 4 * 1, COURT_HEIGHT);
        g.drawLine(COURT_WIDTH / 4 * 2, 0, COURT_WIDTH / 4 * 2, COURT_HEIGHT);
        g.drawLine(COURT_WIDTH / 4 * 3, 0, COURT_WIDTH / 4 * 3, COURT_HEIGHT);
        g.drawLine(COURT_WIDTH / 4 * 4, 0, COURT_WIDTH / 4 * 4, COURT_HEIGHT);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COURT_WIDTH + 250, COURT_HEIGHT);
    }
}
