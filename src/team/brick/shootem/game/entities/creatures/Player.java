package team.brick.shootem.game.entities.creatures;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import team.brick.shootem.game.Handler;
import team.brick.shootem.game.gfx.Animation;
import team.brick.shootem.game.gfx.Assets;
import team.brick.shootem.game.states.State;
import team.brick.shootem.game.tiles.Tile;

/**
 *	Player is a Creature controlled by the user. This class takes input from the user
 *	for movement, and is able to shoot projectiles.
 * 	
 *	@author 
 *	@version 1.0
 *	@since version 1.0
 */
public class Player extends Creature {
	
	//Animations
	private Animation animDown, animUp, animLeft, animRight;
	private boolean readyFire;
	private int counter;
	private int score = 1000;
	
	public Player(Handler handler, float x, float y) {
		super(handler, x, y, Creature.DEFAULT_CREATURE_WIDTH, Creature.DEFAULT_CREATURE_HEIGHT);
		
		bounds.x = 16;
		bounds.y = 22;
		bounds.width = 32;
		bounds.height = 12;
		counter = 0;
		readyFire = true;
		health = 100000;
		handler.setPlayerHealth(health);
		handler.setPlayerScore(score);
		
		//Animatons
		animDown = new Animation(500, Assets.player_down);
		animUp = new Animation(500, Assets.player_up);
		animLeft = new Animation(500, Assets.player_left);
		animRight = new Animation(500, Assets.player_right);
	}

	@Override
	public void tick() {
		//Animations
		animDown.tick();
		animUp.tick();
		animRight.tick();
		animLeft.tick();
		
		//Movement
		getInput();
		move();
		if(!readyFire)
			counter++;
		
		if(counter == 20){
			readyFire = true;
			counter = 0;
		}
		
		collisionWithGoal((int)x,(int)y);
		
		handler.getGameCamera().centerOnEntity(this);
		//handler.getGameCamera().staticCamera(this);
		
		handler.setPlayerScore(this.score);
		handler.setPlayerHealth(health);
	}
	
	/**
	 *  Gets input from the user and sets the players yMove and
	 *  xMove according to which key is pressed.
	 */
	private void getInput(){
		xMove = 0;
		//yMove = -2;
		yMove = -(handler.getGameCamera().getCamSpeed());
		
		if(handler.getKeyManager().up)
		{
			if (y >= (((handler.getGameCamera().getyOffset() + 1))))
			{
				yMove += -speed;
			}
			else
				yMove += 0;
		}
		
		if(handler.getKeyManager().down)
		{
			if (y < (((handler.getGameCamera().getyOffset() + 650))))
			{	
			yMove += speed;
			}
			else
				yMove += 0;
		}
		
		if(handler.getKeyManager().left)
		{
			xMove = -speed;
		}
			if(handler.getKeyManager().right)
			{
				xMove = speed;
			}		
		// A player is only allowed to fire a projectile whenever readyFire is true 
		// and they hit the fire key.
		if(handler.getKeyManager().fire && readyFire){
			// Spawns a projectile above the player moving upwards
			handler.getWorld().getEntityManager().addEntity(new Projectile(handler, this, 0));
			// Every time a player fires a projectile they lose 10 score (accuracy is important)
			// and their guns go on cooldown (they are not ready to fire).
			score -=10;
			readyFire = false;
		}
	}
	
	/**
	 * Checks if the player is colliding with a Goal Tile.
	 * 
	 * @param x the x position of the Tile
	 * @param y the y position of the Tile
	 * @return true if the Tile is not solid
	 * @return false if the Tile is is solid
	 */
	protected void collisionWithGoal(int x, int y){
		int ty = (int) (y + yMove + bounds.y) / Tile.TILEHEIGHT;
		int tx = (int) (x + bounds.x) / Tile.TILEWIDTH;
		if(handler.getWorld().getTile(tx, ty).isGoal()){
			handler.setPlayerScore(score);
			State.setState(handler.getGame().GameOverState);
		}
	}
	

	@Override
	public void render(Graphics g) {
		posX = (int)(x - handler.getGameCamera().getxOffset());
		posY = (int) (y - handler.getGameCamera().getyOffset());
		g.drawImage(getCurrentAnimationFrame(), posX, posY, width, height, null);
		
//		g.drawRect(posX, posY, width, height);
//		g.fillRect((int) (x + bounds.x - handler.getGameCamera().getxOffset()),
//				(int) (y + bounds.y - handler.getGameCamera().getyOffset()),
//				bounds.width, bounds.height);
	}
	
	/**
	 *  @return the current animation frame based on which direction the player moves.
	 */
	private BufferedImage getCurrentAnimationFrame(){
		if(xMove < 0){
			return animLeft.getCurrentFrame();
		}else if(xMove > 0){
			return animRight.getCurrentFrame();
		}else if(yMove < 0){
			return animUp.getCurrentFrame();
		}else{
			return animDown.getCurrentFrame();
		}
	}

	@Override
	public void die() {
		//This method will most likely just call the game over state/function.
		
	}
	
	
	/**
	 * The hurt method of the Player must be overridden so that 
	 * every time the player takes damage, the handler can update 
	 * the player health.
	 * @Override
	 */
	public void hurt(int amt){
		health -= amt;
		if(health <= 0){
			active = false;
			die();
		}
	}

	
	/**
	 * Add integer to the players score.
	 * @param score
	 */
	public void addScore(int score){
		this.score += score;
	}
	
	/**
	 * 
	 * @return player score
	 */
	public int getScore(){
		return score;
	}
	
}
