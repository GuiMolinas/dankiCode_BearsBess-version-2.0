package com.guimolinas.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Comparator;
import java.util.List;

import com.guimolinas.main.Game;
import com.guimolinas.world.Camera;
import com.guimolinas.world.Node;
import com.guimolinas.world.Vector2I;

public class Entity {
	
	public static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(6*16, 0, 16, 16);
	public static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(7*16, 0, 16, 16);
	public static BufferedImage BULLET_EN = Game.spritesheet.getSprite(6*16, 16, 16, 16);
	public static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(7*16, 16, 16, 16);
	public static BufferedImage ENEMY_FEEDBACK_RIGHT = Game.spritesheet.getSprite(144, 16, 16, 16);
	public static BufferedImage ENEMY_FEEDBACK_LEFT = Game.spritesheet.getSprite(144, 32, 16, 16);
	public static BufferedImage GUN_LEFT = Game.spritesheet.getSprite(128, 0, 16, 16);
	public static BufferedImage GUN_DAMAGE = Game.spritesheet.getSprite(144, 0, 16, 16);
	public static BufferedImage BUSH = Game.spritesheet.getSprite(16, 16, 16, 16);

	public double x;
	public double y;
	protected double z;
	
	protected double width;
	protected double height;
	
	protected List<Node> path;
	
	private BufferedImage sprite;
	
	public int maskX, maskY, maskWidth, maskHeight;
	
	public int depth;
	
	public Entity(int x, int y, int width, int height, BufferedImage sprite) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.sprite = sprite;
		
		this.maskX = 0;
		this.maskY = 0;
		this.maskWidth = width;
		this.maskHeight = height;
		
	}
	
	public static Comparator<Entity> nodeSorter = new Comparator<Entity>() {
		
		@Override
		public int compare(Entity n0, Entity n1) {
			if(n1.depth < n0.depth) {
				return +1;
			}
			
			if(n1.depth > n0.depth) {
				return -1;
			}
			
			return 0;
		}
	};
	
	public boolean isColliding(int xNext, int yNext) {
		Rectangle enemyCurrent = new Rectangle(xNext + maskX, yNext + maskY, maskWidth, maskHeight);
	
		for(int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			
			if(e == this) {
				continue;
			}
			
			Rectangle targetEnemy = new Rectangle(e.getX() + maskX, e.getY() + maskY, maskWidth, maskHeight);
			
			if(enemyCurrent.intersects(targetEnemy)) {
				return true;
			}	
			
		}
		
		return false;
	}
	
	public void setMask(int maskX, int maskY, int maskWidth, int maskHeight) {
		this.maskX = maskX;
		this.maskY = maskY;
		this.maskWidth = maskWidth;
		this.maskHeight = maskHeight;
	}
	
	public void setX(int newX) {
		this.x = newX;
	}
	
	public void setY(int newY) {
		this.y = newY;
	}
	
	public int getX() {
		return (int)this.x;
	}
	
	public int getY() {
		return (int)this.y;
	}
	
	public int getWidth() {
		return (int)this.width;
	}
	
	public int getHeight() {
		return (int)this.height;
	}
	
	public void tick() {
		
	}
	
	public void followPath(List<Node> path) {
		if(path != null) {
			if(path.size() > 0) {
				Vector2I target = path.get(path.size() - 1).tile;
				
				//xprev = x;
				//yprev = y;
				
				if(x < target.x * 16 /*&& !isColliding(this.getX() + 1, this.getY())*/) {
					Enemy.dir = Enemy.rightDir;
					x++;
				}
				
				else if(x > target.x * 16 /*&& !isColliding(this.getX() - 1, this.getY())*/) {
					Enemy.dir = Enemy.leftDir;
					x--;
				}
				
				if(y < target.y * 16 /*&& !isColliding(this.getX(), this.getY() + 1)*/) {
					y++;
				}
				
				else if(y > target.y * 16 /*&& !isColliding(this.getX(), this.getY() - 1)*/) {
					y--;
				}
				
				if(x == target.x * 16 && y == target.y * 16) {
					path.remove(path.size() - 1);
				}
				
			}
		}
	}
	
	public double calculateAngleToPlayer(Entity player) {
	    double dx = player.getX() - this.getX();
	    double dy = player.getY() - this.getY();
	    return Math.atan2(dy, dx);
	}

	public double calculateDistance(int x1, int y1, int x2, int y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}
	
	public static boolean isColliding(Entity e1, Entity e2) {
		Rectangle e1Mask = new Rectangle(e1.getX() + e1.maskX, e1.getY() + e1.maskY, e1.maskWidth, e1.maskHeight);
		Rectangle e2Mask = new Rectangle(e2.getX() + e2.maskX, e2.getY() + e2.maskY, e2.maskWidth, e2.maskHeight);
		
		if(e1Mask.intersects(e2Mask) && e1.z == e2.z) {
			return true;
		}
		
		return false;
	}
	
	public void render(Graphics g) {
		g.drawImage(sprite, this.getX() - Camera.x, this.getY() - Camera.y, this.getWidth(), this.getHeight(), null);
		//g.setColor(Color.RED);
		//g.fillRect(this.getX() + maskX - Camera.x, this.getY() + maskY - Camera.y, maskWidth, maskHeight);
	}
	
}
