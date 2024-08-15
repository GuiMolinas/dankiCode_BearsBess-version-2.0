package com.guimolinas.world;

public class Vector2I {

	public int x, y;
	
	public Vector2I(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	//Comapara dois objetos
	public boolean equals(Object object) {
		Vector2I vec = (Vector2I) object;
		
		if(vec.x == this.x && vec.y == this.y) {
			return true;
		}
		
		return false;
	}
	
}
