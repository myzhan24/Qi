package Entity;

import TileMap.*;
import Audio.AudioPlayer;

import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends MapObject{

	// player stuff
	private int health;
	private int maxHealth;
	private int fire;
	private int maxFire;
	private boolean dead;
	private boolean flinching;
	private long flinchTimer;
	private double dashSpeed;
	private boolean initialDash;
	
	// dashing
	private boolean dashing;
	
	// fireball
	private boolean firing;
	private int fireCost;
	private int fireBallDamage;
	private ArrayList<FireBall> fireBalls;
	
	// scratch
	private boolean scratching;
	private int scratchDamage;
	private int scratchRange;
	
	// gliding
	private boolean gliding;
	
	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {
		2, 8, 1, 2, 4, 2, 5, 4
	};
	
	// animation actions
	private static final int IDLE = 0;
	private static final int WALKING =1;
	private static final int JUMPING = 2; 
	private static final int FALLING = 3;
	private static final int GLIDING = 4;
	private static final int FIREBALL = 5;
	private static final int SCRATCHING = 6;
	private static final int DASHING = 7;
	
	private HashMap<String, AudioPlayer> sfx;
	
	public Player(TileMap tm)
	{
		super(tm);
		
		width = 30;
		height = 30;
		cwidth = 20; //screen width and height
		cheight = 20;
		
		moveSpeed = 0.3;	//0.3 def
		maxSpeed = 4;		//1.6 def
		stopSpeed = 0.7;	//0.4 def
		fallSpeed = 0.35;	//.15 default
		maxFallSpeed = 5.0;	//4.0 def
		jumpStart = -7.8;	//-4.8 def
		stopJumpSpeed = 1.6;//0.3 def
		dashSpeed = 6;
		
		facingRight = true;
		
		health = maxHealth = 5;
		fire = maxFire = 2500;
		
		fireCost = 200;
		fireBallDamage = 5;
		fireBalls = new ArrayList<FireBall>();
		
		scratchDamage = 8;
		scratchRange = 40;
		
		// load sprite
		try
		{
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/playersprites.gif"));
			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0 ; i < 7; i++)
			{		
				BufferedImage[] bi = new BufferedImage[numFrames[i]];		
				for(int j = 0; j < numFrames[i]; j++)
				{
					if(i != SCRATCHING)		
					{
						bi[j] = spritesheet.getSubimage(j*width, i*height, width, height);
					}
					else if(i == DASHING)
					{
						bi[j] = spritesheet.getSubimage(j*width, GLIDING*height, width, height);
					}
					else
					{
						bi[j] = spritesheet.getSubimage(j*width*2, i*height, width*2, height);	
					}
				}
				sprites.add(bi);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		animation = new Animation();
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(400);
		
		// sfx
		sfx = new HashMap<String, AudioPlayer>();
		sfx.put("jump", new AudioPlayer("/SFX/fall.wav"));
		sfx.put("scratch", new AudioPlayer("/SFX/bash.wav"));
		sfx.put("fireball", new AudioPlayer("/SFX/fire3.wav"));
		sfx.put("wound", new AudioPlayer("/SFX/wound.wav"));
		sfx.put("bump",  new AudioPlayer("/SFX/bump.wav"));
		sfx.put("heal",  new AudioPlayer("/SFX/heal 1.wav"));
		sfx.put("dash",  new AudioPlayer("/SFX/miss.wav"));
		sfx.put("die", new AudioPlayer("/SFX/die.wav"));
	}
	
	public int getHealth() {return health;}
	public int getMaxHealth() { return maxHealth; }
	public int getFire() {return fire;}
	public int getMaxFire() {return maxFire;}
	
	
	public void setFiring()
	{
		firing = true;
		
	}
	public void setDashing()
	{
		dashing = true;
	}
	public void setScratching()
	{
		scratching = true;
	}
	public void setGliding(boolean b)
	{
		gliding = b;
	}
	public void setHealth(int hp)
	{
		sfx.get("heal").play();
		health = hp;
	}
	public void checkAttack(ArrayList<Enemy> enemies)
	{
		for(int i = 0; i < enemies.size(); i++)
		{
			Enemy e= enemies.get(i);
			// check scratch attack
			if(scratching)		
			{
				if(facingRight)
				{
					if(	e.getX() > x &&
						e.getX() < x + scratchRange&&
						e.getY() > y - height/2 &&
						e.getY() < y + height/2)
						{
							e.hit(scratchDamage);
						}
				}
				else
				{
					if(	e.getX() < x &&
						e.getX() > x - scratchRange&&
						e.getY() > y - height/2 &&
						e.getY() < y + height/2)
						{
							e.hit(scratchDamage);
						}
				}
			}
			
			// fire balls
			for(int j = 0; j < fireBalls.size(); j++)
			{
				if(fireBalls.get(j).intersects(e))
				{
					e.hit(fireBallDamage);
					fireBalls.get(j).setHit();
					break;// since nothing else
				}
			}	
			// check enemy collision
			if(intersects(e))
			{	
				hit(e.getDamage());
			}
		}
	}
	public void hit(int damage)
	{
		if(flinching) return;
		
		
		health -= damage;
		
		dx = 5;
		
		if(facingRight)
			dx=-5;
		
		dy = -4;
		
		
		
		if(health < 0) health = 0;
		
		if(health == 0) 
		{
			dead= true;
			sfx.get("die").play();
		}
		else
			sfx.get("bump").play();
		
		flinching= true;
		flinchTimer = System.nanoTime();
	}
	private void getNextPosition()
	{
		// movement
		if(dashing)
		{
			if(!initialDash)
			{
				initialDash = true;
			
				if(facingRight)
					dx= dashSpeed;
				
				else
					dx= -dashSpeed;
			}
		}
		else if(left)
		{
			
			if(dx > 0 && !falling && !jumping)
				dx*=stopSpeed;
				
			if(dx < -maxSpeed)
			{
				//
				if(!falling && !jumping)
				{
					dx*= stopSpeed;
					if(dx > -maxSpeed)
						dx = -maxSpeed;
				}
			}
			else
				dx -= moveSpeed;
		}
		else if(right)
		{
			
			if(dx < 0  && !falling && !jumping)
				dx*=stopSpeed;
		
			
			

			if(dx > maxSpeed)
			{
				//
				if(!falling && !jumping)
				{
					dx*=stopSpeed;
					if(dx < maxSpeed)
						dx = maxSpeed;
				}
			}
			else
				dx += moveSpeed;
		}
		else
		{
			if(dx > 0 && !falling && !jumping)
			{
				/*
				dx -= stopSpeed;
				if(dx< 0)
				{
					dx= 0;
				}
				*/
				dx *= stopSpeed;
				if(dx < .01)
					dx=0;
			}
			else if(dx < 0 && !falling && !jumping)
			{
				/*
				dx += stopSpeed;
				if(dx >0)
				{
					dx= 0;
				}
				*/
				dx *= stopSpeed;
				if(dx > -.01)
					dx=0;
			}	
		}
		
		// cannot move while attacking, except in air
		if(
			(currentAction == SCRATCHING) || (currentAction == FIREBALL) &&
			!(jumping || falling))
			{
				dx = 0;
			}
		
		// jumping
		if(jumping && !falling)
		{
			sfx.get("jump").play();
			dy = jumpStart;
			falling = true;
		}
		
		// falling
		if(falling)
		{
			if(dy > 0 && gliding)
			{
				dy += fallSpeed * 0.1;
			}
			else
				dy += fallSpeed;
			
			if(dy > 0 ) jumping = false;
			
			if(dy < 0 && !jumping) dy += stopJumpSpeed; //longer you hold the jump button the higher you jump
			
			if(dy > maxFallSpeed) dy = maxFallSpeed;
		}
		
		// my fall
		if(dy == 0 && !jumping)
			falling = false;
		
	}
	
	public void update()
	{
		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp,ytemp);
		
		// check attack has stopped
		if(currentAction == SCRATCHING)
		{
			if(animation.hasPlayedOnce())
				scratching =false;
		}
		if(currentAction == FIREBALL)
		{
			if(animation.hasPlayedOnce())
				firing=false;
		}
		
		if(currentAction == DASHING)
		{
			if(animation.hasPlayedOnce() || jumping)
				dashing = false;
		}
		
		// fire ball attack
		fire += 1;
		if(fire > maxFire) fire = maxFire;
		if(firing && currentAction != FIREBALL)
		{
			if(fire > fireCost)
			{
			if(currentAction == DASHING)
			{
				if(animation.hasPlayedOnce())
				{
					fire -=fireCost;
					FireBall fb = new FireBall(tileMap, facingRight);
					fb.setPosition(x, y);
					fireBalls.add(fb);
				}
			}
			else
			{
				
			
				fire -=fireCost;
				FireBall fb = new FireBall(tileMap, facingRight);
				fb.setPosition(x, y);
				fireBalls.add(fb);
			}
			}
		}
		// update fire balls
		for(int i = 0; i < fireBalls.size();i++)
		{
			fireBalls.get(i).update();
			if(fireBalls.get(i).shouldRemove())
			{
				fireBalls.remove(i);
				i--;
			}
		}
		
		// check done flinching
		if(flinching)
		{
			long elapsed = (System.nanoTime() - flinchTimer)/1000000l;
			if(elapsed > 1000)
			{
				flinching = false;
			}
		}
		
		// set animation
		// dash
		if(dashing)	//button is pressed
		{
			if(currentAction == SCRATCHING || currentAction ==FIREBALL)	//action is currently SCRATCHING
			{
				if(animation.hasPlayedOnce())
				{
					if(currentAction != DASHING)
					{
						sfx.get("dash").play();
						currentAction = DASHING;	
						initialDash = false;
						animation.setFrames(sprites.get(GLIDING));
						animation.setDelay(40);
						width = 30;
					}
				}
			}
			else
			{
				if(currentAction != DASHING)
				{
					sfx.get("dash").play();
					currentAction = DASHING;	
					initialDash = false;
					animation.setFrames(sprites.get(GLIDING));
					animation.setDelay(40);
					width = 30;
				}
			}
		}
		else if(scratching)
		{
			if(currentAction != SCRATCHING)
			{
				sfx.get("scratch").play();
				currentAction = SCRATCHING;
				animation.setFrames(sprites.get(SCRATCHING));
				animation.setDelay(50);
				width = 60;//for the sprite
			}	
		}
		else if(firing)
		{
			if(currentAction != FIREBALL)
			{
				sfx.get("fireball").play();
				currentAction = FIREBALL;
				animation.setFrames(sprites.get(FIREBALL));
				animation.setDelay(100);
				width = 30;
			}
		}
		else if(dy > 0)
		{
			if(gliding)
			{
				if(currentAction != GLIDING)
				{
					currentAction = GLIDING;
					animation.setFrames(sprites.get(GLIDING));
					animation.setDelay(100);
					width = 30;
				}
				
			}
			else if(currentAction != FALLING)
			{
				currentAction = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(100);
				width = 30;
			}
		}
		else if(dy < 0)
		{
			if(currentAction != JUMPING)
			{
				currentAction = JUMPING;
				animation.setFrames(sprites.get(JUMPING));
				animation.setDelay(-1);
				width = 30;
			}
		}
		else if (left || right)
		{
			if(currentAction != WALKING)
			{
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay(40);
				width = 30;
			}
		}
		else
		{
			if(currentAction != IDLE)
			{
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(400);
				width = 30;
			}
		}
		
		animation.update();
		
		// set direction
		
		if(currentAction != SCRATCHING && currentAction != FIREBALL)
		{
			if(right)
				facingRight = true;
			
			if(left)
				facingRight = false;
		}
	}
	
	public void draw(Graphics2D g)
	{
		setMapPosition(); //should be the first thing for any mapobject during draw
		
		// draw fireBalls
		for(FireBall fb : fireBalls)
		{
			fb.draw(g);
		}
		
		// draw player
		if(flinching)
		{
			long elapsed = (System.nanoTime() - flinchTimer) / 1000000l;
			if(elapsed/100 % 2 ==0)
			{
				return;
			}
		}
		// left and right
		super.draw(g);
	}
	
	
}
