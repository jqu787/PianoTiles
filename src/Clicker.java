import java.awt.*;

public class Clicker extends PianoTile {
	private int threshold;
	private int counter;
	
	public Clicker(String note, int position, int column) {
		super(note, position, column);
		
		String[] parts = note.split("/");
		double temp =  Double.parseDouble(parts[1]);
		double factor = temp / 0.25;
		this.threshold = (int)(1.0 * factor);
		this.counter = 0;
	}
	
	public int getThreshold() {
		return threshold;
	}
	
	public int getCounter() {
		return counter;
	}
	
	public void resetCounter() {
		counter = 0;
	}
	
	public void incrCounter() {
		counter += 1;
	}
	
	@Override
    public void draw(Graphics g) {
		g.setColor(Color.red);
        g.fillRect(this.getColumn() * this.getWidth(), this.getPosition(), this.getWidth(), this.getHeight());
        g.setColor(Color.black);
        g.drawString(counter + "/" + threshold, this.getColumn() * this.getWidth() + this.getWidth() / 2, this.getPosition() + this.getHeight() / 2);
    }
}
