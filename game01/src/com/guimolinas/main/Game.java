package com.guimolinas.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.awt.image.DataBufferInt;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.guimolinas.entities.BulletShoot;
import com.guimolinas.entities.Enemy;
import com.guimolinas.entities.Entity;
import com.guimolinas.entities.Npc;
import com.guimolinas.entities.Player;
import com.guimolinas.graficos.Spritesheet;
import com.guimolinas.graficos.UI;
import com.guimolinas.world.Camera;
import com.guimolinas.world.World;

public class Game extends Canvas implements Runnable, KeyListener, MouseListener, MouseMotionListener {


	private static final long serialVersionUID = 1L;
	public static JFrame frame;
	private Thread thread;
	private boolean isRunning = true;
	//não muda tamanho
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 3;
	
	private int CUR_LEVEL = 1, MAX_LEVEL = 2;
	private BufferedImage image;
	
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<BulletShoot> bullets;
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;
	
	public static Random rand;
	
	public UI ui;
	
	public Menu menu;
	
	public static String gameState = "MENU";
	
	private boolean restartGame = false;
	
	private boolean showMessageGameOver = true;
	private int framesGameOver = 0;
	
	public boolean saveGame = false;
	
	public int mx, my;
	
	
	/*
	public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixel.TTF");
	public Font newfont;
	*/
	
	
	public int[] pixels;
	
	public BufferedImage lightMap;
	public int[] lightMapPixels;
	public static int[] miniMapPixels;
	
	public static BufferedImage miniMap;
	
	public Npc npc;
	
	public Game() {
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		//tamanho da janela
		//setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		initFrame();
		//largura, altura, tipo
		//Iniciando objetos
		
		ui = new UI();
		
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		
		try {
			lightMap = ImageIO.read(getClass().getResource("/light.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		lightMapPixels = new int[lightMap.getWidth() * lightMap.getHeight()];
		
		lightMap.getRGB(0, 0, lightMap.getWidth(), lightMap.getHeight(), lightMapPixels, 0, lightMap.getWidth());
		
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		spritesheet = new Spritesheet("/spritesheet.png");
		
		player = new Player(0,0,16,16,spritesheet.getSprite(32, 0, 16, 16));
		entities.add(player);
		
		world = new World("/level1.png");
		
		miniMap = new BufferedImage(World.WIDTH, World.HEIGHT, BufferedImage.TYPE_INT_RGB);
		miniMapPixels = ((DataBufferInt)miniMap.getRaster().getDataBuffer()).getData();
		
		npc = new Npc(32, 32, 16, 16, spritesheet.getSprite(0, 32, 16, 16));
		
		entities.add(npc);
		
		menu = new Menu();
		
		/*
		try {
			newfont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(16f);
		} catch (FontFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}
	
	public void initFrame() {
		frame = new JFrame("Bear & Bees");
		frame.add(this);
		//Tirar barra
		//frame.setUndecorated(true);
		//Não pode redimensionar a janela
		frame.setResizable(false);
		//Calcula dimensões
		frame.pack();
		
		//Icon janela
		Image img = null;
		
		try {
			img = ImageIO.read(getClass().getResource("/icon.png"));
		}
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Image image = toolkit.getImage(getClass().getResource("/cursor.png"));
		
		//Cursor diferente
		Cursor c = toolkit.createCustomCursor(image, new Point(0, 0), "img");
		
		frame.setCursor(c);
		
		frame.setIconImage(img);
		frame.setAlwaysOnTop(true);
		
		//Janela no centro
		frame.setLocationRelativeTo(null);
		//Ao fechar, finaliza
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//Ao iniciar, fica visivel
		frame.setVisible(true);
	}
	
	public synchronized void start() {
		thread = new Thread(this);
		isRunning = true;
		thread.start();
	}
	
	public synchronized void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//lógica do jogo
		public void tick() {
			if(gameState == "NORMAL") {
				if(this.saveGame) {
					this.saveGame = false;
					
					String[] opt1 = {"level"};
					int[] opt2 = {this.CUR_LEVEL};
					
					Menu.saveGame(opt1,opt2,10);
					
					System.out.println("O Jogo foi salvo!");
				}
				
				this.restartGame = false;
				for(int i = 0; i < entities.size();i++) {
					Entity e = entities.get(i);
					e.tick();
				}
			
			
				for(int i = 0; i < bullets.size();i++) {
				bullets.get(i).tick();
				}
			
				if(enemies.size() == 0) {
					//Ir para proxima fase
					CUR_LEVEL++;
					if(CUR_LEVEL > MAX_LEVEL) {
					CUR_LEVEL = 1;
					}
				
					String newWorld = "level"+CUR_LEVEL+".png";
					World.restartGame(newWorld);
				}
				
				
			} 
			
			else if(gameState == "GAME_OVER") {
				this.framesGameOver++;
				
				if(this.framesGameOver == 30) {
					this.framesGameOver = 0;
					if(this.showMessageGameOver) {
						this.showMessageGameOver = false;
					}
					else {
						this.showMessageGameOver = true;
					}
				}
				
				if(restartGame) {
					this.restartGame = false;
					this.gameState = "NORMAL";
					CUR_LEVEL = 1;
					String newWorld = "level"+CUR_LEVEL+".png";
					World.restartGame(newWorld);
				}
			}
			
			else if(gameState == "MENU") {
				//Aparece menu
				player.updateCamera();
				menu.tick();
			}
		}
		
		
		/*
		public void drawRectangleExample(int xff, int yff) {
			for(int xx = 0; xx < 32; xx++) {
				for(int yy = 0; yy < 32; yy++) {
					int xOff = xx + xff;
					int yOff = yy + yff;
					
					if(xOff < 0 || yOff < 0 || xOff >= WIDTH || yOff >= HEIGHT) {
						continue;
					}
					
					pixels[xOff + (yOff * WIDTH)] = 0xff0000;
				}
			}
		}
		*/
		
		public void applyLight() {
			for(int xx = 0; xx < Game.WIDTH; xx++) {
				for(int yy = 0; yy < Game.HEIGHT; yy++) {
					if(lightMapPixels[xx + (yy * Game.WIDTH)] == 0xffffffff) {
						int pixel  = Pixel.getLightBlend(pixels[xx + (yy * WIDTH)], 0x808080, 0);
						pixels[xx + (yy * WIDTH)] = pixel;
					}
				}
			}
		}
		
		public void render() {
			//Lida com gráficos de forma mais profissional, visando desempeenho
			BufferStrategy bs = this.getBufferStrategy();
			if(bs == null) {
				this.createBufferStrategy(3);
				return;
			}
			
			//usado para renderizar
			Graphics g = image.getGraphics();
			g.setColor(new Color(0,0,0));
			//x, y, largura, altura
			g.fillRect(0, 0, WIDTH, HEIGHT);
			
			//Renderizando jogo
			world.render(g);
			
			Collections.sort(entities, Entity.nodeSorter);
			
			for(int i = 0; i < entities.size();i++) {
				Entity e = entities.get(i);
				e.render(g);
			}
			
			for(int i = 0; i < bullets.size();i++) {
				bullets.get(i).render(g);
			}
			
			applyLight();
			
			ui.render(g);
			
			//Desenhando player - Imagem, Posicao que eu quero que apareça
			//Rotação - Cast: Transforma variavel em outro tipo. Colocar ponto no meio
			//Graphics2D g2 = (Graphics2D) g;
			//Criando layer para por em cima
			//g2.setColor(new Color(0,0,0,100));
			
			//Renderizando jogo, otimização
			g.dispose();
			//Primeiro desenho, depois aplico imagem
			g = bs.getDrawGraphics();
			//drawRectangleExample(xx, yy);
			g.drawImage(image, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			g.setColor(Color.WHITE);
			g.drawString("Ammo: " + player.ammo, 580, 20);
			
			//g.setFont(newfont);
			
			if(gameState == "GAME_OVER") {
				Graphics2D g2 = (Graphics2D) g;
				
				g2.setColor(new Color(0,0,0,100));
				g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
				
				g.setFont(new Font("Arial", Font.BOLD, 40));
				g.setColor(Color.WHITE);
				g.drawString("GAME OVER!", (WIDTH * SCALE) / 2 - 110, (HEIGHT * SCALE) / 2 - 20);
				g.setFont(new Font("Arial", Font.BOLD, 25));
				
				if(showMessageGameOver) {
					g.drawString("Press Enter to restart", (WIDTH * SCALE) / 2 - 115, (HEIGHT * SCALE) / 2 + 40);
				}
			}
			
			else if(gameState == "MENU") {
				menu.render(g);
			}
			
			//World.renderMiniMap();
			//g.drawImage(miniMap, 615, 80, World.WIDTH * 2, World.HEIGHT * 2, null);
			
			/*
			
			//Rotacionando objeto
			
			Graphics2D g2 = (Graphics2D) g;
			
			double angleMouse = Math.atan2(200+25 - my, 200+25 - mx);
			
			g2.rotate(angleMouse, 200+25, 200+25);
			
			g.setColor(Color.RED);
			g.fillRect(200, 200, 50, 50);
			
			*/
			
			bs.show();
		}
		
		public static void main(String [] args) {
			Game game = new Game();
			game.start();
		}
		
		
		@Override
		public void run() {
			requestFocus();
			//Limitando FPS
			long lastTime = System.nanoTime();
			double amountOfTicks = 60.0;
			double ns = 1000000000/amountOfTicks;
			double delta = 0;
			//Verifica os 60 FPS
			int frames = 0;
			double timer = System.currentTimeMillis();
			requestFocus();
			while(isRunning) {
				long now = System.nanoTime();
				delta += (now - lastTime) / ns;
				lastTime = now;
				if(delta >=1) {
					tick();
					render();
					frames++;
					delta--;
				}
				
				if(System.currentTimeMillis() - timer >= 1000) {
					System.out.println(frames);
					frames = 0;
					timer += 1000;
				}
			}
			
			stop();
			
		}

		//Eventos de teclado
		
		@Override
		public void keyTyped(KeyEvent e) {
			
		}

		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_SPACE) {
				player.jump = true;
			}
			
			if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() ==  KeyEvent.VK_D) {
				player.right = true;
			}
			
			else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() ==  KeyEvent.VK_A) {
				player.left = true;
			}
			
			if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() ==  KeyEvent.VK_W) {
				player.up = true;
			}
			
			else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() ==  KeyEvent.VK_S) {
				player.down = true;
			}
			
			if(e.getKeyCode() == KeyEvent.VK_X) {
				player.shoot = true;
			}
			
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				this.restartGame = true;
				
				npc.showMessage = false;
				
				if(gameState == "MENU") {
					menu.enter = true;
				}
			}
			
			if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				gameState = "MENU";
				menu.pause = true;
			}
			
			if(e.getKeyCode() == KeyEvent.VK_P) {
				if(Game.gameState == "NORMAL") {
					this.saveGame = true;
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() ==  KeyEvent.VK_D) {
				player.right = false;
			}
			
			else if(e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() ==  KeyEvent.VK_A) {
				player.left = false;
			}
			
			if(e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() ==  KeyEvent.VK_W) {
				player.up = false;
				
				if(gameState == "MENU") {
					menu.up = true;
				}
				
				
			}
			
			else if(e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() ==  KeyEvent.VK_S) {
				player.down = false;
				
				if(gameState == "MENU") {
					menu.down = true;
				}
			}
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			player.mouseShoot = true;
			player.mx = (e.getX() / 3);
			player.my = (e.getY() / 3);
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			this.mx = e.getX();
			this.my = e.getY();
			
		}
	
}
