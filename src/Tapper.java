import java.awt.*;

public class Tapper extends PianoTile {
	public Tapper(String note, int position, int column) {
		super(note, position, column);
	}
	
	@Override
    public void draw(Graphics g) {
		g.setColor(Color.black);
        g.fillRect(this.getColumn() * this.getWidth(), this.getPosition(), this.getWidth(), this.getHeight());
    }
}
