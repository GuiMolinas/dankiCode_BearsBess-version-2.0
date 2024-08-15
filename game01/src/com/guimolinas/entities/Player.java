package com.guimolinas.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.guimolinas.graficos.Spritesheet;
import com.guimolinas.main.Game;
import com.guimolinas.main.Sound;
import com.guimolinas.world.Camera;
import com.guimolinas.world.World;

public class Player extends Entity{
	
	public boolean right, left, up, down;
	public int rightDir = 0, leftDir = 1;
	public int dir = rightDir;
	public double speed = 1.5;
	
	private int frames = 0, maxFrames = 5, index = 0, maxIndex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	public double life = 100, maxLife = 100;
	
	public int ammo = 0;
	
	private BufferedImage playerDamage;
	private int damagedFrames = 0;
	
	public boolean isDamaged = false;
	
	private boolean hasGun = false;
	
	public boolean shoot = false, mouseShoot = false;
	
	public int mx, my;
	
	public boolean jump = false;
	
	public boolean isJumping = false;
	
	public static int z = 0;
	
	public int jumpFrames = 50, jumpCur = 0;
	
	public boolean jumpUp = false, jumpDown = false;
	
	public int jumpSpeed = 2;
	

	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		
		playerDamage = Game.spritesheet.getSprite(0, 16, 16, 16);
		
		for(int i = 0; i < 4; i++) {
			rightPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16),0,16,16);
		}
		
		for(int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i * 16),16,16,16);
		}
		
	}
	
	public void reviewMap() {
		int xx = (int) (x / 16);
		int yy = (int) (y / 16);
		
		World.tiles[xx - 1 + yy * World.WIDTH].show = true;
		World.tiles[xx + yy * World.WIDTH].show = true;
		World.tiles[xx + 1 + yy * World.WIDTH].show = true;
		
		World.tiles[xx + (yy + 1) * World.WIDTH].show = true;
		World.tiles[xx + (yy - 1) * World.WIDTH].show = true;
		
		World.tiles[xx - 1 + (yy - 1) * World.WIDTH].show = true;
		World.tiles[xx + 1 + (yy - 1) * World.WIDTH].show = true;
		
		World.tiles[xx - 1 + (yy + 1) * World.WIDTH].show = true;
		World.tiles[xx + 1 + (yy + 1) * World.WIDTH].show = true;
	}
	
	public void tick() {
		depth = 1;
		//reviewMap();
		if(jump) {
			if(isJumping == false) {
				Sound.jump.setVolume(-25.0f);
				Sound.jump.play();
				jump = false;
				isJumping = true;
				jumpUp = true;
			}
		}
		
		if(isJumping == true) {
			if(jumpUp) {
				jumpCur+=jumpSpeed;
			}
				
			else if(jumpDown) {
				jumpCur-=jumpSpeed;
			}
			
			if(jumpCur <= 0) {
				isJumping = false;
				jumpUp = false;
				jumpDown = false;
			}
	
 				
			z = jumpCur;
			if(jumpCur >= jumpFrames) {
				jumpUp = false;
				jumpDown = true;
			}
		}
		
		moved = false;
		if(right && World.isFree((int)(x+speed), this.getY())) {
			moved = true;
			dir = rightDir;
			x +=speed;
		}
		
		else if(left && World.isFree((int)(x-speed), this.getY())) {
			moved = true;
			dir = leftDir;
			x -=speed;
		}
		
		if(up && World.isFree(this.getX(), (int)(y-speed))) {
			moved = true;
			y-=speed;
		}
		
		else if(down && World.isFree(this.getX(), (int)(y+speed))) {
			moved = true;
			y+=speed;
		}
		
		if(moved) {
			frames++;
			if(frames == maxFrames) {
				frames = 0;
				index++;
				
				if(index > maxIndex) {
					index = 0;
				}
			}
		}
		
		checkColissionLifePack();
		checkColissionGun();
		checkColissionAmmo();
		
		if(isDamaged) {
			this.damagedFrames++;
			if(this.damagedFrames == 8) {
				this.damagedFrames = 0;
				isDamaged = false;
			}
		}
		
		if(shoot) {
			shoot = false;
			if(hasGun && ammo > 0) {
				ammo--;
				//Cria bala e atira
			
				//System.out.println("Fogo!");
				int dx = 0;
				int px = 0;
				int py = 5;
				if(dir == rightDir) {
					dx = 1;
					px = 18;
				}
			
				else {
					dx = -1;
					px = -8;
				}
			
				BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 3,3,null, dx, 0);
				Game.bullets.add(bullet);
				}
			
			}
		
		if(mouseShoot) {
			mouseShoot = false;
			
			
			if(hasGun && ammo > 0) {
				ammo--;
				//Cria bala e atira
			
				//System.out.println("Fogo!");
				
				
				int px = 0;
				int py = 8;
				double angle = 0;
				
				if(dir == rightDir) {
					px = 18;
					angle = Math.atan2(my - (this.getY()+py - Camera.y),mx - (this.getX()+px - Camera.x));
				}
			
				else {
					px = -8;
					angle = Math.atan2(my - (this.getY()+py - Camera.y),mx - (this.getX()+px - Camera.x));
				}
				
				double dx = Math.cos(angle);
				double dy = Math.sin(angle);
			
				BulletShoot bullet = new BulletShoot(this.getX() + px, this.getY() + py, 3,3,null, dx, dy);
				Game.bullets.add(bullet);
				}
				
		}
		
		if(life <= 0) {
			//Game Over
			life = 0;
			Game.gameState = "GAME_OVER";
		}
		
		updateCamera();
	}
	
	public void updateCamera() {
		Camera.x = Camera.clamp(this.getX() - (Game.WIDTH/2), 0, (World.WIDTH*16) - Game.WIDTH);
		Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT/2), 0, (World.HEIGHT*16) - Game.HEIGHT);
	}
	
	public void checkColissionAmmo() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Bullet) {
				if(Entity.isColliding(this, e)) {
					Sound.ammo.setVolume(-15.0f);
					Sound.ammo.play();
					ammo+=20;
					//System.out.println("Ammo: " + ammo);
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	public void checkColissionGun() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof Weapon) {
				if(Entity.isColliding(this, e)) {
					Sound.ammo.setVolume(-15.0f);
					Sound.ammo.play();
					hasGun = true;
					//System.out.println("Pegou arma!");
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	public void checkColissionLifePack() {
		for(int i = 0; i < Game.entities.size(); i++) {
			Entity e = Game.entities.get(i);
			if(e instanceof LifePack) {
				if(Entity.isColliding(this, e)) {
					Sound.heal.setVolume(-15.0f);
					Sound.heal.play();
					life += 8;
					if(life >= 100) {
						life = 100;
					}
					Game.entities.remove(i);
					return;
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if(!isDamaged) {
			if(dir == rightDir) {
				g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if(hasGun) {
					//Arma para a direita
					g.drawImage(Entity.WEAPON_EN, this.getX() + 8 - Camera.x, this.getY() + 2 - Camera.y - z, null);
				}
			}	
		
			else if(dir == leftDir) {
				g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y - z, null);
				if(hasGun) {
					//Arma para a esquerda
					g.drawImage(Entity.GUN_LEFT, this.getX() - 8 - Camera.x, this.getY() + 2 - Camera.y - z, null);
				}
			}
		}
		
		else {
			g.drawImage(playerDamage, this.getX() - Camera.x, this.getY() - Camera.y - z, null);
			
			if(hasGun) {
				if(dir == leftDir) {
					g.drawImage(Entity.GUN_DAMAGE, this.getX() - 8 - Camera.x, this.getY() - Camera.y - z, null);
				}
				
				else {
					g.drawImage(Entity.GUN_DAMAGE, this.getX() + 8 - Camera.x, this.getY() - Camera.y - z, null);
				}
			}
		}
		
		if(isJumping) {
			g.setColor(Color.BLACK);
			g.fillOval(this.getX() - Camera.x + 4, this.getY() - Camera.y + 16, 8, 8);
		}
		
	}

}
