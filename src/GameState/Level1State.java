package GameState;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import Audio.AudioPlayer;
import Entity.*;
import Entity.Enemies.*;
import Qi.QiPanel;
import TileMap.Background;
import TileMap.TileMap;

public class Level1State extends GameState{
	
	private TileMap tileMap;
	private Background bg;
	
	private Player player;
	private HUD hud;
	
	private AudioPlayer bgm;
	
	private ArrayList<Enemy> enemies;
	private ArrayList<Explosion> explosions;
	
	public Level1State(GameStateManager gsm)
	{
		this.gsm = gsm;
		init();
	}

	public void init() {
		
		tileMap = new TileMap(30);
		tileMap.loadTiles("/Tilesets/grasstileset.gif");
		tileMap.loadMap("/Maps/test.map");
		tileMap.setPosition(0,0);
		
		bg = new Background("/Backgrounds/creek.gif",0.1);
		
		player = new Player(tileMap);
		player.setPosition(100, 100);
		
		populateEnemies();
		
	
		
		explosions = new ArrayList<Explosion>();
		
		
		hud = new HUD(player);
		
		bgm = new AudioPlayer("/SFX/wound.wav");
	
	}

	private void populateEnemies()
	{
		enemies = new ArrayList<Enemy>();
		
		Point[] derp= new Point[]{
			new Point(200, 100),
			new Point(860, 200),
			new Point(1525, 200),
			new Point(1680, 200),
			new Point(1800, 200)
		};
		Slugger s;
		for(Point p : derp)
		{
			s = new Slugger(tileMap);
			s.setPosition(p.x,p.y);
			enemies.add(s);
		}
	}
	public void update() {
		// update player
		player.update();
		if(player.getY() > tileMap.getHeight())
			player.hit(9999);
		
		else		
			tileMap.setPosition( QiPanel.WIDTH / 2 - player.getX(),  QiPanel.HEIGHT / 2 - player.getY()); //keep the camera centered on the player
		
		if(player.getHealth() ==0)
		{
			player.setHealth(player.getMaxHealth());
			player.setPosition(200,200);
		}
		// set background
		bg.setPosition(tileMap.getX(), tileMap.getY());
		
		// attack enemies
		player.checkAttack(enemies);
		
		// update all enemies
		for(int i = 0; i < enemies.size();i++)
		{
			Enemy e = enemies.get(i);
			e.update();
			if(e.isDead())
			{
				enemies.remove(i);
				i--;
				explosions.add(new Explosion(e.getX(), e.getY()));
			}
		}
		
		// update all explosions
		for(int i = 0 ; i < explosions.size(); i++)
		{
			Explosion e = explosions.get(i);
			e.setMapPosition((int)tileMap.getX(), (int)tileMap.getY());
			e.update();
			if(e.shouldRemove())
			{
				explosions.remove(i);
				i--;
			}
		}

	}

	public void draw(Graphics2D g) {
		/*
		//clear the screen
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, QiPanel.WIDTH, QiPanel.HEIGHT);
		
		*/
		
		// draw bg
		bg.draw(g);
		
		// draw the tile map
		tileMap.draw(g);
		
		//draw player
		player.draw(g);
		
		//draw enemies
		for(Enemy e : enemies)
		{
			e.draw(g);
		}
		
		// draw explosions
		for(Explosion e: explosions)
		{	
			e.draw(g);
		}
		
		// draw hud
		hud.draw(g);
	}

	public void keyPressed(int k) {
		if(k == KeyEvent.VK_LEFT) player.setLeft(true);	
		if(k == KeyEvent.VK_RIGHT) player.setRight(true);
		if(k == KeyEvent.VK_UP) player.setUp(true);	
		if(k == KeyEvent.VK_DOWN) player.setDown(true);	
		if(k == KeyEvent.VK_W) player.setJumping(true);	
		if(k == KeyEvent.VK_E) player.setGliding(true);
		if(k == KeyEvent.VK_R) player.setScratching();
		if(k == KeyEvent.VK_F) player.setFiring();
		if(k == KeyEvent.VK_Q) player.setDashing();
	}

	public void keyReleased(int k) {
		if(k == KeyEvent.VK_LEFT) player.setLeft(false);	
		if(k == KeyEvent.VK_RIGHT) player.setRight(false);
		if(k == KeyEvent.VK_UP) player.setUp(false);	
		if(k == KeyEvent.VK_DOWN) player.setDown(false);	
		if(k == KeyEvent.VK_W) player.setJumping(false);	
		if(k == KeyEvent.VK_E) player.setGliding(false);
	}
	
}
