package Entity;

import TileMap.*;
import Audio.AudioPlayer;

import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.image.BufferedImage;

public class Champion extends MapObject{

	// player stuff
	private int health;
	private int maxHealth;
	private int fire;
	private int maxFire;
	private boolean dead;
	private boolean taunting;
	
	private boolean disabled;
	private long disabledTimer;
	private long disabledDuration;
	private boolean flinching;
	private long flinchTimer;
	private double dashSpeed;
	private boolean initialDash;
	private float characterScale;
	
	//falcon punch
	private boolean punchCharging;
	private boolean punching;
	
	// dashing
	private boolean dashing;
	
	// fireball
	private boolean firing;
	private int fireCost;
	private int fireBallDamage;
	private ArrayList<FireBall> fireBalls;
	
	// scratch
	private boolean kneeing;
	private int scratchDamage;
	private int scratchRange;
	
	// gliding
	private boolean gliding;
	
	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {
		1, //facing right
		1, //left
		6, //walking
		1, //jumping
		1, //falling
		5,  //dashing
		1, //fpunch charging
		1, //fpunch
		4, //knee
		1, //taunt
		
	};
	
	// animation actions
	private static final int IDLERIGHT = 0;
	private static final int IDLELEFT = 1;
	private static final int WALKING =2;
	private static final int JUMPING = 3; 
	private static final int FALLING = 4;
	private static final int DASHING = 5;
	private static final int FALCONPUNCHINGCHARGEUP = 6;
	private static final int FALCONPUNCHING = 7;
	private static final int KNEEING = 8;
	private static final int TAUNTING = 9;
	private static final long TAUNTDURATION = 1000l;

	private HashMap<Integer, Point2D.Float[]> hitbox;
	private HashMap<Integer, Point2D.Float[]> offsets;
	private HashMap<String, AudioPlayer> sfx;
	
	public Champion(TileMap tm)
	{
		super(tm);
		
		width = 43;
		height = 57;
		
		characterScale = 0.56f;
		cwidth = (int) ((width * characterScale *.7f) ); //screen width and height
		cheight = (int) ((height * characterScale*.7f) );
		
		moveSpeed = 0.3;	//0.3 def
		maxSpeed = 4;		//1.6 def
		stopSpeed = 0.7;	//0.4 def
		fallSpeed = 0.25;	//.15 default
		maxFallSpeed = 5.0;	//4.0 def
		jumpStart = -7.8;	//-4.8 def
		stopJumpSpeed = 0.6;//0.3 def
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
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/CaptFalconsheet.gif"));
			sprites = new ArrayList<BufferedImage[]>();
			hitbox = new HashMap<Integer,Point2D.Float[]>();
			offsets = new HashMap<Integer,Point2D.Float[]>();
			
			for(int i = 0 ; i < numFrames.length; i++)
			{		
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				Point2D.Float[] derp = new Point2D.Float[numFrames[i]];
				Point2D.Float[] offs = new Point2D.Float[numFrames[i]];
				for(int k = 0; k < offs.length;k++)
				{
					offs[k] = new Point2D.Float(0,0);
				}
				
				switch(i)
				{	
					case IDLERIGHT: 
					{
						bi[0] = spritesheet.getSubimage(18, 21, 43, 58);
						derp[0] = new Point2D.Float(43,58);
						break;
					}
					case IDLELEFT:
					{
						bi[0] = spritesheet.getSubimage(62,21,43,58);
						derp[0] = new Point2D.Float(43,58);
						break;
					}
					case WALKING: 
					{
						
						bi[0] = spritesheet.getSubimage(367, 111, 48, 55);
						bi[1] = spritesheet.getSubimage(419, 115, 70, 51);
						bi[2] = spritesheet.getSubimage(495, 111, 71, 55);
						bi[3] = spritesheet.getSubimage(578,111, 43, 55);
						bi[4] = spritesheet.getSubimage(209,115, 70, 51);
						bi[5] = spritesheet.getSubimage(286, 114, 71, 52);
						
						
						derp[0] = new Point2D.Float(48, 55);
						derp[1] = new Point2D.Float(70, 51);
						derp[2] = new Point2D.Float(71, 55);
						derp[3] = new Point2D.Float(43, 55);
						derp[4] = new Point2D.Float(70, 51);
						derp[5] = new Point2D.Float(71, 52);

						offs[0] = new Point2D.Float(6, 0);
						offs[1] = new Point2D.Float(0, 0);
						offs[2] = new Point2D.Float(0, 0);
						offs[3] = new Point2D.Float(6, 0);
						offs[4] = new Point2D.Float(0, 0);
						offs[5] = new Point2D.Float(0, 0);
						break;
					}
					case JUMPING:
					{
					
						bi[0] = spritesheet.getSubimage(137, 190, 46, 84);	
						derp[0] = new Point2D.Float(46,84);
						
						break;
					}
					case FALLING:
					{

						bi[0] = spritesheet.getSubimage(319, 23,40, 54);
	
						derp[0] = new Point2D.Float(40, 54);
						break;
					}
					case DASHING:
					{
						bi[0] = spritesheet.getSubimage(367, 111, 48, 55);
						bi[1] = spritesheet.getSubimage(419, 115, 70, 51);
						bi[2] = spritesheet.getSubimage(419, 115, 70, 51);
						bi[3] = spritesheet.getSubimage(419, 115, 70, 51);
						bi[4] = spritesheet.getSubimage(367, 111, 48, 55);
						
					
						derp[0] = new Point2D.Float(48, 55);
						derp[1] = new Point2D.Float(70, 51);
						derp[2] = new Point2D.Float(70, 51);
						derp[3] = new Point2D.Float(70, 51);
						derp[4] = new Point2D.Float(48, 55);
						

						break;
					}
					case FALCONPUNCHINGCHARGEUP:
					{
						bi[0] = spritesheet.getSubimage(26, 111, 54, 52);
						
						derp[0] = new Point2D.Float(54,52);
						break;
					
					}
					case FALCONPUNCHING:
					{
						
						bi[0] = spritesheet.getSubimage(101, 110, 78, 53);
					
						derp[0] = new Point2D.Float(78,53);
						break;
					}
					case KNEEING:	
					{
						bi[0] = spritesheet.getSubimage(width, i*height, width, height);
						derp[0] = new Point2D.Float(width,height);
						break;
					}
					case TAUNTING:
					{
						bi[0] = spritesheet.getSubimage(140, 17, 32, 65);
						derp[0] = new Point2D.Float(32,65);
						break;
					}
					default:
					{
						bi[0] = spritesheet.getSubimage(width, i*height, width, height);
						derp[0] = new Point2D.Float(width,height);
						offs[0] = new Point2D.Float(0,0);
						break;
					}
				
				}
				hitbox.put(i, derp);
				sprites.add(bi);
				offsets.put(i, offs);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		animation = new Animation();
		currentAction = IDLERIGHT;
		animation.setFrames(sprites.get(IDLERIGHT));
		animation.setDelay(400);
		
		// sfx
		sfx = new HashMap<String, AudioPlayer>();
		sfx.put("jump", new AudioPlayer("/SFX/Captain Falcon/captain10.dsp.wav"));
		sfx.put("scratch", new AudioPlayer("/SFX/Captain Falcon/captain05.dsp.wav"));
		sfx.put("fireball", new AudioPlayer("/SFX/EB/fire2.wav"));
		sfx.put("wound", new AudioPlayer("/SFX/EB/wound.wav"));
		sfx.put("bump",  new AudioPlayer("/SFX/EB/bump.wav"));
		sfx.put("heal",  new AudioPlayer("/SFX/EB/heal 1.wav"));
		sfx.put("dash",  new AudioPlayer("/SFX/EB/watershallow.wav"));
		sfx.put("die", new AudioPlayer("/SFX/Captain Falcon/captain13.dsp.wav"));
		sfx.put("taunt", new AudioPlayer("/SFX/Captain Falcon/captain03.dsp.wav"));
		sfx.put("falcon", new AudioPlayer("/SFX/Captain Falcon/captain09.dsp.wav"));
		sfx.put("punch", new AudioPlayer("/SFX/Captain Falcon/captain12.dsp.wav"));
		sfx.put("eagle", new AudioPlayer("/SFX/Captain Falcon/captain1a.dsp.wav"));
	}
	
	public int getHealth() {return health;}
	public int getMaxHealth() { return maxHealth; }
	public int getFire() {return fire;}
	public int getMaxFire() {return maxFire;}
	
	public void setPunchCharging()
	{
		punchCharging = true;
	}
	public void setPunching()
	{
		punching = true;
	}
	public void setDisabled(long t)
	{
		disabled = true;
		disabledTimer = System.nanoTime();
		disabledDuration = t;
	}
	public void setDisabled(boolean b)
	{
		disabled = b;
	}
	public void setTaunting()
	{
		if(!falling && !jumping && !disabled)
			taunting = true;
	}
	public void setJumping()
	{
		jumping=true;
	}
	public void setFiring()
	{
		if(currentAction!=TAUNTING && currentAction!=FALCONPUNCHINGCHARGEUP)
		firing = true;
	}
	public void setDashing()
	{
		if(currentAction!=TAUNTING && currentAction!=FALCONPUNCHINGCHARGEUP)
		dashing = true;
	}
	public void setLeft(boolean b)
	{
		left = b;
	}
	public void setRight(boolean b)
	{
		right = b;
	}
	public void setScratching()
	{
		if(currentAction!=TAUNTING)
		kneeing = true;
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
			if(punching)		
			{
				if(facingRight)
				{
					if(	e.getX() > x &&
						e.getX() < x + scratchRange&&
						e.getY() > y - height/2 &&
						e.getY() < y + height/2)
						{
							e.hit(1,7,-5);
							sfx.get("wound").play();
							sfx.get("fireball").play();
						}
				}
				else
				{
					if(	e.getX() < x &&
						e.getX() > x - scratchRange&&
						e.getY() > y - height/2 &&
						e.getY() < y + height/2)
						{
							e.hit(1,-7,-5);
							sfx.get("wound").play();
							sfx.get("fireball").play();
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
		if(dashing && !disabled)
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
		else if(left && !disabled)
		{
			
			if(dx > 0 && !falling && !jumping)
			{
				dx*=stopSpeed;
				if(currentAction ==WALKING)
					animation.setFrame(0);
			}
				
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
		else if(right && !disabled)
		{
			
			if(dx < 0  && !falling && !jumping)
			{
				dx*=stopSpeed;
				if(currentAction ==WALKING)
					animation.setFrame(0);
			}
			
			

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
			if(dx > 0 && !falling && !jumping && !punching )
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
			else if(dx < 0 && !falling && !jumping && !punching)
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
			(currentAction == KNEEING)&&
			!(jumping || falling))
			{
				dx = 0;
			}
		
		// jumping
		if(jumping && !falling && !disabled && !taunting)
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
			else if (dy > 0 && down && !disabled)
			{
				if(dy < maxFallSpeed*2)
				dy+= fallSpeed*2.2;
			}
			else
			{
				if(dy< maxFallSpeed)
				dy += fallSpeed;
				
			}
			
			if(dy > 0 ) jumping = false;
			
			if(dy < 0 && !jumping) dy += stopJumpSpeed; //longer you hold the jump button the higher you jump
			
			//if(dy > maxFallSpeed && !down) dy = maxFallSpeed;
		}
		
		// my fall
		if(dy == 0 && !jumping)
			falling = false;
		
	}
	
	public void update()
	{
		if(taunting||(punchCharging&&!falling))
			jumping=false;
		
		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp,ytemp);
		
		
		
		// check attack has stopped
		if(currentAction == KNEEING)
		{
			if(animation.hasPlayedOnce())
				kneeing =false;
		}
		if(currentAction == FALCONPUNCHINGCHARGEUP)
		{
			if(animation.hasPlayedOnce())
			{
				punching = true;
				punchCharging = false;
			}
		}
		if(currentAction == FALCONPUNCHING)
		{
			if(animation.hasPlayedOnce())
			{
				punching = false;
			
			}
		}
		if(currentAction == DASHING)
		{
			if(animation.hasPlayedOnce() || jumping)
				dashing = false;
		}
		if(currentAction == TAUNTING)
		{
			if(animation.hasPlayedOnce())
			{
				taunting = false;
				setDisabled(false);
			}
		}
		
		// fire ball attack
		fire += 1;
		if(fire > maxFire) fire = maxFire;
		if(firing && currentAction != FALCONPUNCHING)
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
		
		//check done being disabled
		if(disabled)
		{
			long elapsed = (System.nanoTime() - disabledTimer)/1000000l;
			if(elapsed > disabledDuration)
			{
				disabled = false;
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
		if(taunting)
		{
			if(currentAction != TAUNTING)
			{
				dashing = false;
				sfx.get("taunt").play();
				currentAction = TAUNTING;
				animation.setFrames(sprites.get(TAUNTING));
				animation.setDelay(TAUNTDURATION);
				setDisabled(TAUNTDURATION);
			}
		}
		else if(punchCharging)
		{
			if(currentAction != FALCONPUNCHINGCHARGEUP &&!punching)
			{
				dashing = false;
				sfx.get("falcon").play();
				currentAction = FALCONPUNCHINGCHARGEUP;
				animation.setFrames(sprites.get(FALCONPUNCHINGCHARGEUP));
				animation.setDelay(792);
				setDisabled(1192);
			}
		}
		else if(punching)
		{
			if(currentAction != FALCONPUNCHING)
			{
				sfx.get("punch").play();
				sfx.get("eagle").play();
				currentAction = FALCONPUNCHING;
				animation.setFrames(sprites.get(FALCONPUNCHING));
				animation.setDelay(300);
				if(facingRight)
					dx+=4;
				
				else
					dx-=4;
			}
		}
		// dash
		else if(dashing)	//button is pressed
		{
			if(currentAction == KNEEING || currentAction == FALCONPUNCHING)	//action is currently KNEEING
			{
				if(animation.hasPlayedOnce())
				{
					if(currentAction != DASHING)
					{
						sfx.get("dash").play();
						currentAction = DASHING;	
						initialDash = false;
						animation.setFrames(sprites.get(DASHING));
						animation.setDelay(80);
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
					animation.setFrames(sprites.get(DASHING));
					animation.setDelay(80);
					width = 30;
				}
			}
		}
		else if(kneeing)
		{
			if(currentAction != KNEEING)
			{
				sfx.get("scratch").play();
				currentAction = KNEEING;
				animation.setFrames(sprites.get(KNEEING));
				animation.setDelay(50);
				width = 60;//for the sprite
			}	
		}
		else if(firing)
		{
			if(currentAction != FALCONPUNCHING)
			{
				sfx.get("fireball").play();
				currentAction = FALCONPUNCHING;
				animation.setFrames(sprites.get(FALCONPUNCHING));
				animation.setDelay(100);
				width = 30;
			}
		}
		else if(dy > 0)
		{
			if(currentAction != FALLING)
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
		else if ((left || right))
		{
			if(currentAction != WALKING)
			{
				currentAction = WALKING;
				animation.setFrames(sprites.get(WALKING));
				animation.setDelay((long)(maxSpeed * 15));
				width = 30;
			}
			else if(currentAction == WALKING)
			{
				double deex =  (Math.abs(dx));
				if(deex ==0)
				{
					deex = 4;
				}
				
				animation.setDelay((long)(maxSpeed *60f /deex));
			}
		}
		else
		{
			if(currentAction != IDLELEFT || currentAction != IDLERIGHT)
			{
				if(facingRight)
					currentAction = IDLERIGHT;
				
				else
					currentAction = IDLELEFT;
				
				animation.setFrames(sprites.get(currentAction));
				animation.setDelay(400);
				width = 30;
			}
		
		}
		animation.update();
		
		// set direction
		
		if(currentAction != KNEEING && currentAction != FALCONPUNCHING && currentAction!=DASHING && currentAction!=TAUNTING && currentAction!= FALCONPUNCHINGCHARGEUP)
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
		
		double xoff =0;
		double yoff =0;
		try
		{
			xoff = offsets.get(currentAction)[animation.getFrame()].getX();
			yoff = offsets.get(currentAction)[animation.getFrame()].getY();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		double w = hitbox.get(currentAction)[animation.getFrame()].getX();
		double h = hitbox.get(currentAction)[animation.getFrame()].getY();
		
		//cwidth = (int) (w*characterScale*0.7);
		//cheight = (int) (h*characterScale*0.7);
		
		g.setColor(new Color(255,0,0));
		
		
		// left and right
		if(facingRight || currentAction==IDLELEFT)
		{
			
			g.drawImage(
					animation.getImage(), 
					(int)(x + xmap - (w*characterScale) / 2f +xoff), 
					(int)(y + ymap - (h*characterScale) / 2f +yoff), (int)(w*characterScale), (int)(h*characterScale),
					null
					);
			//draw the player hitbox
			/*
			Rectangle r = getRectangle();
			g.drawRect(
					(int)(r.x + xmap + (r.width) / 2f), 
					(int)(r.y + ymap + (r.height) / 2f), 
					(int)(r.width), 
					(int)(r.height));*/
			
		}
		else
		{
			g.drawImage(
					animation.getImage(), 
					(int)((x + xmap - (w*characterScale) / 2f -xoff)+ (w*characterScale)), 
					(int)(y + ymap - (h*characterScale) / 2f  +yoff),
					-(int)(w*characterScale),
					(int)(h*characterScale),
					null
					);
			//draw the player hitbox
			/*
			Rectangle r = getRectangle();
			g.drawRect(
					(int)(r.x + xmap + (r.width) / 2f), 
					(int)(r.y + ymap + (r.height) / 2f), 
					(int)(r.width), 
					(int)(r.height));*/
			
					
		}
	}
	
	
}
