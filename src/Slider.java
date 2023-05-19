import java.awt.*;

public class Slider extends PianoTile {
	private int hitbox_position; //position of the hitbox
	private int hitbox_height;
	private boolean hitbox_clicked = false;
	
	public Slider(String note, int position, int column) {
		super(note, position, column);
		
		this.hitbox_position = this.getPosition() + 40;
		this.hitbox_height = 100;
		
		String[] parts = note.split("/");
		double temp =  Double.parseDouble(parts[1]);
		double factor = temp / 0.25;
		this.setHeight((int) ((double)this.getHeight() * factor)); 
	}
	
	public int getHitboxHeight() {
		return hitbox_height;
	}
	
	public int getHitboxPosition() {
		return hitbox_position;
	}
	
	public boolean clicked() {
		return hitbox_clicked;
	}
	
	public void hitBoxClicked() {
		hitbox_clicked = true;
	}
	
	@Override
	public void move(int dy) {
		this.setPosition(this.getPosition() + dy);
		if (!hitbox_clicked) {
			hitbox_position += dy;
		}
		
	}
	
	@Override
    public void draw(Graphics g) {
		g.setColor(Color.blue);
		int bottom = this.getHitboxPosition() - this.getPosition() + this.getHitboxHeight();
        g.fillRect(this.getColumn() * this.getWidth(), this.getPosition(), this.getWidth(), bottom);
        g.setColor(Color.black);
        g.drawRect(this.getColumn() * this.getWidth(), this.getHitboxPosition(), this.getWidth(), this.getHitboxHeight());
    }
}
