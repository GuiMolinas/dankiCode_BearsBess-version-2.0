package com.guimolinas.world;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.guimolinas.entities.*;
import com.guimolinas.graficos.Spritesheet;
import com.guimolinas.main.Game;

public class World {
	
	public static Tile[] tiles;
	public static int WIDTH, HEIGHT;
	public static final int TILE_SIZE = 16;
	

	public World(String path) {
		
		/*
		//Criando random map
		Game.player.setX(0);
		Game.player.setY(0);
		
		WIDTH = 100;
		HEIGHT = 100;
		
		tiles = new Tile[WIDTH * HEIGHT];
		
		for(int xx = 0; xx < WIDTH; xx++) {
			for(int yy = 0; yy < HEIGHT; yy++) {
				tiles[xx + (yy * WIDTH)] = new WallTile(xx * 16,yy * 16, Tile.TILE_WALL);
			}
		}
		
		int dir = 0;
		
		int xx = 0;
		int yy = 0;
		
		for(int i = 0; i < 200; i++) {
			
			tiles[xx + (yy * WIDTH)] = new FloorTile(xx * 16,yy * 16, Tile.TILE_FLOOR);
			
			if(dir == 0) {
				//controle para a direita
				if(xx < WIDTH) {
					xx++;
				}
			}
			
			else if(dir == 1) {
				//esquerda
				if(xx > 0) {
					xx--;
				}
			}
			
			else if(dir == 2) {
				//baixo
				if(yy < HEIGHT) {
					yy++;
				}
			}
			
			else if(dir == 3) {
				//cima
				if(yy > 0) {
					yy--;
				}
			}
			
			if(Game.rand.nextInt(100) < 30) {
				dir = Game.rand.nextInt(4);
			}
		}
		
		*/
		
		
		try {
			BufferedImage map = ImageIO.read(getClass().getResource(path));
			int[] pixels = new int[map.getWidth() * map.getHeight()];
			WIDTH = map.getWidth();
			HEIGHT = map.getHeight();
			map.getRGB(0, 0, map.getWidth(), map.getHeight(), pixels, 0, map.getWidth());
			tiles = new Tile[map.getWidth() * map.getHeight()];
			for(int xx = 0; xx < map.getWidth(); xx++) {
				for(int yy = 0; yy < map.getHeight(); yy++) {
					int pixelAtual = pixels[xx + (yy * map.getWidth())];
					
					//Chão
					tiles[xx + (yy * WIDTH)] = new FloorTile(xx*16, yy*16, Tile.TILE_FLOOR);
					
					if(pixelAtual == 0xFF000000) {
						//Chão
						tiles[xx + (yy * WIDTH)] = new FloorTile(xx*16, yy*16, Tile.TILE_FLOOR);
					}
					
					else if(pixelAtual == 0xFFFFFFFF) {
						//Parede
						tiles[xx + (yy * WIDTH)] = new WallTile(xx*16, yy*16, Tile.TILE_WALL);
					}
					
					else if(pixelAtual == 0xFF0026FF) {
						//Player
						Game.player.setX(xx*16);
						Game.player.setY(yy*16);
					}
					
					else if(pixelAtual == 0xFFFF0000) {
						//Enemy
						Enemy en = new Enemy(xx*16, yy*16,16,16, Entity.ENEMY_EN);
						Game.entities.add(en);
						Game.enemies.add(en);
					}
					
					else if(pixelAtual == 0xFFB200FF) {
						//Weapon
						Game.entities.add(new Weapon(xx*16, yy*16,16,16, Entity.WEAPON_EN));
					}
					
					else if(pixelAtual == 0xFFFFDD00) {
						//Munição
						Game.entities.add(new Bullet(xx*16, yy*16,16,16, Entity.BULLET_EN));
					}
					
					else if(pixelAtual == 0xFF00FF21) {
						//Life
						LifePack pack = new LifePack(xx*16, yy*16,16,16, Entity.LIFEPACK_EN);
						Game.entities.add(pack);
					}
					
					
					/*
					else if(pixelAtual == 0xFF267F00) {
						//Arbusto
						tiles[xx + (yy * WIDTH)] = new FloorTile(xx*16, yy*16, Entity.BUSH);
					}
					*/
					
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void restartGame(String level) {
		Game.entities.clear();
		Game.enemies.clear();
		Game.bullets.clear();
		
		Game.entities = new ArrayList<Entity>();
		Game.enemies = new ArrayList<Enemy>();
		Game.spritesheet = new Spritesheet("/spritesheet.png");
		
		Game.player = new Player(0,0,16,16,Game.spritesheet.getSprite(32, 0, 16, 16));
		Game.entities.add(Game.player);
		
		Game.world = new World("/"+level);
		
		return;
	}
	
	public static void generateParticles(int amount, int x, int y) {
		for(int i = 0; i < amount; i++) {
			Game.entities.add(new Particle(x, y, 1, 1, null));
		}
	}
	
	public static boolean isFreeDynamic(int xNext, int yNext, int width, int height) {
		int x1 = xNext / TILE_SIZE;
		int y1 = yNext / TILE_SIZE;
		
		int x2 = (xNext + width - 1) / TILE_SIZE;
		int y2 = yNext / TILE_SIZE;
		
		int x3 = xNext / TILE_SIZE;
		int y3 = (yNext + height - 1) / TILE_SIZE;
		
		int x4 = (xNext + width - 1) / TILE_SIZE;
		int y4 = (yNext + height - 1) / TILE_SIZE;
		
		if(!((tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile) ||
				(tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile) ||
				(tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile) ||
				(tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile))) {
			return true;
		}
		
		if(Player.z > 0) {
			return true;
		}
		
		return false;
	}
	
	
	public static boolean isFree(int xNext, int yNext) {
		int x1 = xNext / TILE_SIZE;
		int y1 = yNext / TILE_SIZE;
		
		int x2 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
		int y2 = yNext / TILE_SIZE;
		
		int x3 = xNext / TILE_SIZE;
		int y3 = (yNext + TILE_SIZE - 1) / TILE_SIZE;
		
		int x4 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
		int y4 = (yNext + TILE_SIZE - 1) / TILE_SIZE;
		
		if(!((tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile) ||
				(tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile) ||
				(tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile) ||
				(tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile))) {
			return true;
		}
		
		if(Player.z > 0) {
			return true;
		}
		
		return false;
	}
	
	public void render(Graphics g) {
		int xStart = Camera.x >> 4;
		int yStart = Camera.y >> 4;
		
		int xFinal = xStart + (Game.WIDTH >> 4);
		int yFinal = yStart + (Game.HEIGHT >> 4);
		
		for(int xx = xStart; xx <= xFinal; xx++) {
			for(int yy = yStart; yy <= yFinal; yy++) {
				if(xx < 0 || yy < 0 || xx >= WIDTH || yy >= HEIGHT) {
					continue;
				}
				Tile tile = tiles[xx + (yy*WIDTH)];
				tile.render(g);
			}
		}
	}
	
	public static void renderMiniMap() {
		for(int i = 0; i < Game.miniMapPixels.length; i++) {
			Game.miniMapPixels[i] = 0;
		}
		
		for(int xx = 0; xx < World.WIDTH; xx++) {
			for(int yy = 0; yy < World.HEIGHT; yy++) {
				int index = xx + (yy * World.WIDTH);
				if(index < Game.miniMapPixels.length && tiles[xx + (yy*WIDTH)] instanceof WallTile) {
					Game.miniMapPixels[index] = 0xFF0000;
				}
			}
		}
		
		int xPlayer = Game.player.getX() / 16;
		int yPlayer = Game.player.getY() / 16;
		int playerIndex = xPlayer + (yPlayer * World.WIDTH);
		
		if (playerIndex < Game.miniMapPixels.length) {
	        Game.miniMapPixels[playerIndex] = 0x0000FF;
	    }
	}
	
}
