package Entity.Enemies;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import TileMap.TileMap;
import Entity.Animation;
import Entity.Enemy;

public class Slugger extends Enemy{

	
	private BufferedImage[] sprites;
	public Slugger(TileMap tm)
	{
		super(tm);
		
		moveSpeed= 0.1;
		maxSpeed = 0.3;
		fallSpeed = 0.2;
		
		maxFallSpeed = 10.0;
		
		width = 30;		//30x30 squares in the images
		height = 30;
		
		cwidth = 20;	//real width
		cheight = 20;
		health = maxHealth = 500;
		
		damage = 1;
		
		// load sprite
		try
		{
			BufferedImage spritesheet = ImageIO.read(
					getClass().getResourceAsStream(
							"/Sprites/Enemies/slugger.gif"
							));
			
			sprites = new BufferedImage[3];
			for(int i  =0 ; i < sprites.length;i++)
			{
				sprites[i] = spritesheet.getSubimage(
						i*width, 
						0, 
						width, 
						height
						);
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		animation  = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(300);
		
		facingRight = true;
		right = true;
		
	}
	
	public Slugger(TileMap tm,double s, double a)
	{
		super(tm);
		
		moveSpeed= s;
		maxSpeed = a;
		fallSpeed = 0.2;
		
		maxFallSpeed = 10.0;
		
		width = 30;		//30x30 squares in the images
		height = 30;
		
		cwidth = 20;	//real width
		cheight = 20;
		health = maxHealth = 500;
		
		damage = 1;
		
		// load sprite
		try
		{
			BufferedImage spritesheet = ImageIO.read(
					getClass().getResourceAsStream(
							"/Sprites/Enemies/slugger.gif"
							));
			
			sprites = new BufferedImage[3];
			for(int i  =0 ; i < sprites.length;i++)
			{
				sprites[i] = spritesheet.getSubimage(
						i*width, 
						0, 
						width, 
						height
						);
			}
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		animation  = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(300);
		
		facingRight = true;
		right = true;
		
	}
	
	private void getNextPosition()
	{
		// movement
		if(left)
		{
			
			
			if(dx > -maxSpeed)
			{
				dx -= moveSpeed;
			}
			
		}
		else if(right)
		{
			
			if(dx < maxSpeed)
			{
				dx += moveSpeed;
			}
		}
		
		//falling
		if(falling)
		{
			dy+=fallSpeed;
		}
	}
	public void update()
	{
		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		// check flinching
		if(flinching)
		{
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000l;
			
			if(elapsed > 400)
			{
				flinching = false;
			}
		}
		
		//if it hits a wall, go other direction
		if(right && dx == 0)
		{
			right = false;
			left = true;
			facingRight = false;
		} 
		else if(left && dx == 0)
		{
			right = true;
			left = false;
			facingRight = true;
		}
		
		//update animation
		animation.update();
	}
	
	public void draw(Graphics2D g)
	{
		
		
		setMapPosition();
		
		if(notOnScreen()) return;
		
		super.draw(g);
		
		/*Rectangle r = getRectangle();
		g.drawRect(
				(int)(r.x + xmap + (r.width) / 2f), 
				(int)(r.y + ymap + (r.height) / 2f), 
				(int)(r.width), 
				(int)(r.height));*/
		
	}
}
