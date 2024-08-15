package com.guimolinas.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.guimolinas.main.Game;
import com.guimolinas.main.Sound;
import com.guimolinas.world.AStar;
import com.guimolinas.world.Camera;
import com.guimolinas.world.Vector2I;
import com.guimolinas.world.World;

public class Enemy extends Entity{

	private double speed = 0.4;
	public static int rightDir = 0, leftDir = 1;
	public static int dir = rightDir;
	
	private int frames = 0, maxFrames = 20, index = 0, maxIndex = 1;
	
	private boolean isDamaged = false;
	private int damageFrames = 10, damageCurrent = 0;
	
	private BufferedImage[] rightEnemy;
	private BufferedImage[] leftEnemy;
	
	private int life = 5;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		
		rightEnemy = new BufferedImage[2];
		leftEnemy = new BufferedImage[2];
		
		for(int i = 0; i < 2; i++) {
			rightEnemy[i] = Game.spritesheet.getSprite(112 + (i * 16),16,16,16);
		}
		
		for(int i = 0; i < 2; i++) {
			leftEnemy[i] = Game.spritesheet.getSprite(112 + (i * 16),32,16,16);
		}
	}
	
	public void tick() {
		
		depth = 0;
		
		maskX = 5;
		maskY = 5;
		maskWidth = 8;
		maskHeight = 8;
		
		if(!isCollidingWithPlayer()) {
			if(path == null || path.size() == 0) {
				Vector2I start = new Vector2I ((int)(x/16), (int)(y/16));
				Vector2I end = new Vector2I ((int)(Game.player.x/16), (int)(Game.player.y/16));
				
				path = AStar.findPath(Game.world, start, end);
			}
		}
		
		else {
			if(Game.rand.nextInt(100) < 5) {
				Sound.hurt.setVolume(-15.0f);
				Sound.hurt.play();
				Game.player.life-=Game.rand.nextInt(3);
				Game.player.isDamaged = true;
			}
		}
		
		/*
		if(this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 50) {
		
			if(this.isCollidingWithPlayer() == false) {
		
				if((int) x < Game.player.getX() && World.isFree((int)(x + speed), this.getY())
						&& !isColliding((int)(x + speed), this.getY())) {
					dir = rightDir;
					x += speed;
				}
		
				else if((int)x > Game.player.getX() && World.isFree((int)(x - speed), this.getY())
						&& !isColliding((int)(x - speed), this.getY())) {
					dir = leftDir;
					x -= speed;
				}
		
				else if((int) y < Game.player.getY() && World.isFree(this.getX(), (int)(y + speed))
						&& !isColliding(this.getX(), (int)(y + speed))) {
					y += speed;
				}	
		
				else if((int)y > Game.player.getY() && World.isFree(this.getX(), (int)(y - speed))
						&& !isColliding(this.getX(), (int)(y - speed))) {
					y -= speed;
				}
		
			}
		
			else {
				//Perto do player
				if(Game.rand.nextInt(100) < 10) {
					Sound.hurt.play();
					Game.player.life-=Game.rand.nextInt(5);
					Game.player.isDamaged = true;
				
					//System.out.println(Game.player.life);
				}
			}
		}
		*/
		
		if(new Random().nextInt(100) < 60) {
			followPath(path);
		}
		
		if(new Random().nextInt(100) < 5) {
			Vector2I start = new Vector2I ((int)(x/16), (int)(y/16));
			Vector2I end = new Vector2I ((int)(Game.player.x/16), (int)(Game.player.y/16));
			
			path = AStar.findPath(Game.world, start, end);
		}
		
		
		
		frames++;
		if(frames == maxFrames) {
			frames = 0;
			index++;
				
			if(index > maxIndex) {
				index = 0;
			}
		}
		
		collidingBullet();
		
		updateDirection(Game.player);
		
		if(life <= 0) {
			destroySelf();
			return;
		}
		
		if(isDamaged) {
			this.damageCurrent++;
			if(this.damageCurrent == this.damageFrames) {
				this.damageCurrent = 0;
				this.isDamaged = false;
			}
			
		}
	}
	
	public void destroySelf() {
		Sound.kill.setVolume(-15.0f);
		Sound.kill.play();
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
	public void updateDirection(Entity player) {
	    double angle = calculateAngleToPlayer(player);
	    if (Math.cos(angle) > 0) {
	        dir = rightDir;
	    } else {
	        dir = leftDir;
	    }
	}

	
	public void collidingBullet() {
		for(int i = 0; i < Game.bullets.size(); i ++) {
			Entity e = Game.bullets.get(i);
			
			if(e instanceof BulletShoot) {
				
				if(Entity.isColliding(this, e)) {
					Sound.damage.setVolume(-15.0f);
					Sound.damage.play();
					isDamaged = true;
					life--;
					Game.bullets.remove(i);
					return;
				}
			}
		}
		
	}
	
	public boolean isCollidingWithPlayer() {
		Rectangle enemyCurrent = new Rectangle(this.getX() + maskX, this.getY() + maskY, maskWidth, maskHeight);
		Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), 16, 16);
		
		return enemyCurrent.intersects(player);
	}
	
	
	public void render(Graphics g) {
		if(!isDamaged) {
			if(dir == rightDir) {
				g.drawImage(rightEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
			
			else if(dir == leftDir) {
				g.drawImage(leftEnemy[index], this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}
		
		else {
			if(dir == rightDir) {
				g.drawImage(Enemy.ENEMY_FEEDBACK_RIGHT, this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
			
			else if(dir == leftDir) {
				g.drawImage(Enemy.ENEMY_FEEDBACK_LEFT, this.getX() - Camera.x, this.getY() - Camera.y, null);
			}
		}
		
		//g.setColor(Color.RED);
		//g.fillRect(this.getX() + maskX - Camera.x, this.getY() + maskY - Camera.y, maskWidth, maskHeight);
		
	}

}
