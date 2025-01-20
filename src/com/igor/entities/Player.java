package com.igor.entities;

import java.awt.Graphics;

import java.awt.image.BufferedImage;

import com.igor.main.Game;
import com.igor.world.Camera;
import com.igor.world.World;

public class Player extends Entity {

	public boolean right,up,left,down,space;
	public int right_dir = 0, left_dir = 1;
	public int dir = right_dir;		
	public double speed = 1.7;

	private int frames = 0, maxframes = 5, index = 0, maxindex = 3;
	private boolean moved = false;
	private BufferedImage[] rightPlayer;
	private BufferedImage[] leftPlayer;
	
	private BufferedImage playerDamaged;
	
	private boolean arma = false;
	
	public int ammo = 0;
	
	public boolean isDamaged = false;
	private int damageFrames = 0;
	
	public boolean shoot = false, mouseShoot = false;
	
	public double life = 100, maxlife = 100;
	public int mx,my;
	
	public Player(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, sprite);
		
		rightPlayer = new BufferedImage[4];
		leftPlayer = new BufferedImage[4];
		playerDamaged = Game.spritesheet.getSprite(0, 16, 16, 16);
		for(int i = 0; i < 4; i++) {
		rightPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 0, 16, 16);
		}
		for(int i = 0; i < 4; i++) {
			leftPlayer[i] = Game.spritesheet.getSprite(32 + (i*16), 16, 16, 16);
			}
		
	}
	public void tick(){ 
		depth = 1;
		moved = false;
		if(right & World.isFree((int)(x+speed),this.getY())) {
			moved = true;
			dir = right_dir;
			x += speed;
		}
		else if(left & World.isFree((int)(x-speed),this.getY())){
			moved = true;
			dir = left_dir;
			x -= speed;
		}
		if(up & World.isFree(this.getX(),(int)(y-speed))) {
			moved = true;
			y -= speed;
		}
		else if(down & World.isFree(this.getX(),(int)(y+speed))){
			moved = true;
			y += speed;
		}
		
		if (moved) {
			frames++;
			if(frames == maxframes) {
				frames = 0;
				index++;
				if(index > maxindex) 
					index = 0;
			}
		}
		
		this.checkIColiddingLifepack();
		this.checkIColiddingAmmo();
		this.checkIColiddingGun();
		
		if(isDamaged){
			this.damageFrames++;
			if(this.damageFrames == 8) {
				this.damageFrames = 0;
				isDamaged = false;
			}
		}
		
		if(shoot ) {
			shoot = false;
			if(arma && ammo>0) {
			ammo--;
			shoot = false;
			int dx = 0; 
			int px = 0;
			int py = 8;
			if(dir == right_dir) {
				px = 18;
				dx = 1;
			}else {
				px = -8;
				 dx = -1;
			}
			
			BulletShoot bullet = new BulletShoot(this.getX()+px,this.getY()+py,3,3,null,dx,0);
			Game.bullets.add(bullet);
			}
		}
		
		if(mouseShoot) {
			mouseShoot = false;
			
			if(arma && ammo > 0) {
				ammo--;
				
				int px = 0;
				int py = 8;
				double angle = 0;
				if(dir == right_dir) {
					px = 18;
					angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + px - Camera.x));
				}else {
					px = -8;
					angle = Math.atan2(my - (this.getY() + py - Camera.y), mx - (this.getX() + px - Camera.x));
				}
				double dx = Math.cos(angle);
				double dy = Math.sin(angle);
				BulletShoot bulletShoot = new BulletShoot(this.getX() + px, this.getY() + py,3,3,null,dx,dy);
				Game.bullets.add(bulletShoot);
			}
		}
		
		if(life <= 0) {
			life = 0;
			// Game Over!
			 Game.gameState ="GAME_OVER";
		}
		
		updateCamera();
	
	}
	
	public void updateCamera() {
		Camera.x = Camera.Clamp(this.getX() - (Game.WIDTH/2),0,World.WIDTH*16 - Game.WIDTH);
		Camera.y = Camera.Clamp(this.getY() - (Game.HEIGHT/2),0,World.HEIGHT*16 - Game.HEIGHT);
	}
	
	public void checkIColiddingGun() {
		for(int i = 0; i < Game.entities.size();i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Weapon) {
				if(Entity.isColidding(this, atual)) {
					arma = true;
					//System.out.println("pegou a arma");
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	public void checkIColiddingAmmo() {
		for(int i = 0; i < Game.entities.size();i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Bullet) {
				if(Entity.isColidding(this, atual)) {
					ammo+=100;
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	public void checkIColiddingLifepack() {
		for(int i = 0; i < Game.entities.size();i++) {
			Entity atual = Game.entities.get(i);
			if(atual instanceof Lifepack) {
				if(Entity.isColidding(this, atual)) {
					life+=10;
					if(life>10) 
						life = 100;
					Game.entities.remove(atual);
				}
			}
		}
	}
	
	public void render(Graphics g) {
		if(!isDamaged) {
		if(dir == right_dir) {
		g.drawImage(rightPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y , null);
		if(arma) {
			//Desenhar arma da direita
			g.drawImage(Entity.GUN_RIGHT,this.getX()+8 - Camera.x, this.getY() - Camera.y , null);
		}
		}else if(dir == left_dir){
			g.drawImage(leftPlayer[index], this.getX() - Camera.x, this.getY() - Camera.y , null);
			if(arma) {
				//Desenhar arma da esquerda
				g.drawImage(Entity.GUN_LEFT,this.getX()-8 - Camera.x, this.getY() - Camera.y , null);
			}
		}
		}else{
			g.drawImage(playerDamaged, this.getX() - Camera.x, this.getY() - Camera.y , null);
			if(arma) {
				if(dir == left_dir) {
					g.drawImage(Entity.GUN_DAMAGED_LEFT, this.getX()-8 - Camera.x, this.getY() - Camera.y , null);
				}else {
					g.drawImage(Entity.GUN_DAMAGED_RIGHT,this.getX()+8 - Camera.x,this.getY() - Camera.y , null);
				}
			}
		}
	}	
}
