package com.guimolinas.entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.guimolinas.main.Game;

public class Npc extends Entity{

	public String[] phrases = new String[2];
	
	public boolean showMessage = false;
	public boolean showed = false;
	
	public int curIndexMsg = 0;
	
	public int phrasesIndex = 0;
	
	public int time = 0;
	public int maxTime = 5;
	
	public Npc(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		// TODO Auto-generated constructor stub
		phrases[0] = "Olá que bom que você chegou";
		phrases[1] = "Precisamos da sua ajuda!";
	}
	
	public void tick() {
		int xPlayer = Game.player.getX();
		int yPlayer = Game.player.getY();
		
		int xNpc = (int)x;
		int yNpc = (int)y;
		
		depth = 2;
		
		if(Math.abs(xPlayer - xNpc) < 20 && Math.abs(yPlayer - yNpc ) < 20) {
			if(showed == false) {
				showMessage = true;
				showed = true;
			}
		}
		
		else {
			showMessage = false;
		}
		
		this.maxTime = 5;
		
		time++;
		
		if(showMessage) {
			if(this.time >= this.maxTime) {
				this.time = 0;
				if(curIndexMsg < phrases[phrasesIndex].length() - 1) {
					curIndexMsg++;
				}
			
				else {
					if(phrasesIndex < phrases.length - 1) {
						phrasesIndex++;
						curIndexMsg = 0;
					}
				
				}
			
			}
		}
		
	}
	
	public void render(Graphics g) {
		super.render(g);
		
		if(showMessage) {
			g.setColor(Color.WHITE);
			g.fillRect(9, 9, Game.WIDTH - 18, Game.HEIGHT - 18);
			
			g.setColor(Color.BLUE);
			g.fillRect(10, 10, Game.WIDTH - 20, Game.HEIGHT - 20);
			g.setFont(new Font("Arial", Font.BOLD, 9));
			
			g.setColor(Color.WHITE);
			g.drawString(phrases[phrasesIndex].substring(0, curIndexMsg), (int)x, (int)y);
			
			g.drawString("> Press ENTER to close < ", (int)x + 10, (int)y + 13);
		}
	}

}
