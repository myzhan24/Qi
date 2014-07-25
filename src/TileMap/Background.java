package TileMap;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.image.*;

import Qi.QiPanel;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.swing.ImageIcon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Background {
	
	private BufferedImage image;
	
	private double x;
	private double y;
	private double dx;
	private double dy;
	
	private double moveScale;
	
	public Background(String s, double ms)
	{
		try
		{
			image = ImageIO.read(getClass().getResourceAsStream(s));
			moveScale = ms;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void setPosition(double x, double y)
	{
		this.x = (x * moveScale) % QiPanel.WIDTH;
		this.y = (y * moveScale) % QiPanel.HEIGHT;
	}
	
	public void setVector(double dx, double dy)
	{
		this.dx = dx;
		this.dy = dy;
	}
	
	public void update()
	{
		x += dx;
		y += dy;
	}
	
	public void draw(Graphics2D g)
	{
		g.drawImage(image, (int)x, (int) y, null);
		
		// scrolling the image
		if(image.getWidth() <= QiPanel.WIDTH)
		{
			if(x < 0)
			{
				g.drawImage(image, (int)x + QiPanel.WIDTH, (int)y, null);
			}
			if(x > 0)
			{
				g.drawImage(image, (int)x - QiPanel.WIDTH, (int)y, null);
			}
		}
		
		if(image.getHeight() <= QiPanel.HEIGHT)
		{
			
			if(y < 0)
			{
				g.drawImage(image, (int)x , (int)y-image.getHeight(), null);
			}
			if(y >= (image.getHeight()-QiPanel.HEIGHT -1))
			{
				g.drawImage(image, (int)x , (int)(y+image.getHeight()), null);
			}
		}
		
	}
}
