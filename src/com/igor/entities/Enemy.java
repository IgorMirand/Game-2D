package com.igor.entities;

import java.awt.Graphics;



import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.igor.main.Game;
import com.igor.main.Sound;
import com.igor.world.AStar;
import com.igor.world.Camera;
import com.igor.world.Vector2i;

public class Enemy extends Entity{
	
	//private double speed = 0.4;
	
	//private int maskx = 8, masky = 8, maskw = 10, maskh = 10;
	
	private int frames = 0, maxframes = 5, index = 0, maxindex = 1;
	
	private BufferedImage[] sprites;
	
	private int life = 3;
	
	private boolean isDamaged = false;
	private int damagedFrame = 10, damagedCurrent = 0;
	
	public Enemy(int x, int y, int width, int height, BufferedImage sprite) {
		super(x, y, width, height, null);
		sprites = new BufferedImage[2];
		sprites[0] = Game.spritesheet.getSprite(112,16,16,16);
		sprites[1] = Game.spritesheet.getSprite(112+16,16,16,16);
		
	}

	public void tick() {
		depth = 0;
		maskx = 5;
		masky = 5;
		mwidth = 8;
		mheight = 8;
		if(!isColiddinWithPlayer()) {
		if(path == null || path.size() == 0) {
			Vector2i start = new Vector2i ((int)(x/16),(int)(y/16));
			Vector2i end = new Vector2i ((int)(Game.player.x/16),(int)(Game.player.y/16));
			path = AStar.findPath(Game.world, start, end);
		}
	}else {
		if(new Random().nextInt(100) < 10) {
			//Sound.hurtEffect.play();
			Game.player.life-=Game.rand.nextInt(3);
			Game.player.isDamaged = true;
		}
	}
		
		if(new Random().nextInt(100) < 50)
		followPath(path);
		if(path == null || path.size() == 10) {
			Vector2i start = new Vector2i ((int)(x/16),(int)(y/16));
			Vector2i end = new Vector2i ((int)(Game.player.x/16),(int)(Game.player.y/16));
			path = AStar.findPath(Game.world, start, end);
		}
		//animation
		frames++;
		if(frames == maxframes) {
			frames = 0;
			index++;
			if(index > maxindex) 
				index = 0;
		}
		
		 collidingBullet();
		 if(life <= 0) {
			 destroySelf();
			 return ;
		 }
		 if(isDamaged) {
			 this.damagedCurrent++;
			 if(this.damagedCurrent == this.damagedFrame) { 
				 this.damagedCurrent = 0;
				 this.isDamaged = false;
			 }
				 
		 }
	}
	
	public void destroySelf() {
		Game.enemies.remove(this);
		Game.entities.remove(this);
	}
	
		public void collidingBullet() {
			for(int i = 0; i < Game.bullets.size(); i++) {
				Entity e = Game.bullets.get(i);
				if(e instanceof BulletShoot) {
					if(Entity.isColidding(this, e)) {
						isDamaged = true;
						life--;
						Game.bullets.remove(i);
						return;
					}
				}
			}
		}
	
		public boolean isColiddinWithPlayer() {
			Rectangle enemyCurrent = new Rectangle(this.getX()+ maskx, this.getY()+masky, mwidth ,mheight);
			Rectangle player = new Rectangle(Game.player.getX(),Game.player.getY(),16,16);
			 
			return enemyCurrent.intersects(player);
		}
		
	/*
		public boolean isColidding(int xnext,int ynext) {
			Rectangle enemyCurrent = new Rectangle(xnext + maskx,ynext + masky, mwidth, mheight);
			for(int i = 0; i < Game.enemies.size(); i++) {
			Enemy e = Game.enemies.get(i);
			if(e == this)
				continue;
			Rectangle targetEnemy = new Rectangle(e.getX() + maskx,e.getY() + masky, mwidth, mheight);
			if(enemyCurrent.intersects(targetEnemy)) {
			return true;
			}
		}	
			return false;
	}
		*/
		
		public void render(Graphics g) {
			if(!isDamaged) 
			g.drawImage(sprites[index],this.getX() - Camera.x,this.getY() - Camera.y, null);
				else 
					g.drawImage(Entity.ENEMY_FEEDBACK,this.getX() - Camera.x,this.getY() - Camera.y, null);
		
	}
			
}
