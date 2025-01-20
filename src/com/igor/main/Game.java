package com.igor.main;

import java.awt.Canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
//import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
//import java.io.IOException;
//import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import com.igor.entities.BulletShoot;
import com.igor.entities.Enemy;
import com.igor.entities.Entity;
import com.igor.entities.Player;
import com.igor.graficos.Spritesheet;
import com.igor.graficos.UI;
import com.igor.world.World;

public class Game extends Canvas implements Runnable,KeyListener,MouseListener,MouseMotionListener{

	
	private static final long serialVersionUID = 1L;
	//variables
	//janela e run Game
	private static JFrame frame;
	private Thread thread; 
	private boolean isRunning = true;
	public static final int WIDTH = 240;
	public static final int HEIGHT = 160;
	public static final int SCALE = 3;

	//Imagens e graficos
	private int CUR_LEVEL = 1,MAX_LEVEL = 2;
	private BufferedImage image;
	//Entities
	public static List<Entity> entities;
	public static List<Enemy> enemies;
	public static List<BulletShoot> bullets;
	public static Spritesheet spritesheet;
	
	public static World world;
	
	public static Player player;
	
	public static Random rand;
	
	public UI ui;
	
	//public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("pixelfont.ttf");
	//public Font newfont;
	
	public static String gameState = "MENU";
	private boolean showMassageGameOver = true;
	private int frameGameOver = 0;
	private boolean restartGame = false;
	
	public Menu menu;
	
	public static boolean saveGame = false;

	public int mx,my;
	
	public int[] pixels;
	public int xx,yy;
	
	public BufferedImage lightmap;
	public int [] lightmapPixel;
	
	public Game() { 
		Sound.musicBackground.loop();
		rand = new Random();
		addKeyListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(WIDTH * SCALE,HEIGHT * SCALE));
		iniFrame();
		//inicializado objetos
		ui = new UI();
		image = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		
		try {
			lightmap = ImageIO.read(getClass().getResource("/lightmap.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		lightmapPixel = new int[lightmap.getWidth() * lightmap.getHeight()];
		lightmap.getRGB(0, 0, lightmap.getWidth(), lightmap.getHeight(), lightmapPixel, 0, lightmap.getWidth());
		pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
		entities = new ArrayList<Entity>();
		enemies = new ArrayList<Enemy>();
		bullets = new ArrayList<BulletShoot>();
		
		spritesheet = new Spritesheet("/spritesheet.png");
		player = new Player(0, 0, 16, 16,spritesheet.getSprite(32, 0, 16, 16)) ;
		entities.add(player);
		world = new World("/level1.png");
		
		/*
		try {
			newfont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(70f);
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		*/
		menu = new Menu();
	}
	
	public void iniFrame() {
		frame = new JFrame("Game#1");
		frame.add(this);
		frame.setResizable(false);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		game.start();
	}
	
	public void tick() {
		xx++;
		if(gameState == "NORMAL") {
			if(saveGame) {
				saveGame = false;
				String[] opt1 = {"level"};
				int[] opt2 = {this.CUR_LEVEL};
				menu.saveGame(opt1,opt2,10);
				System.out.println("jogo salvo");
			}
			this.restartGame = false;
		for(int i = 0; i< entities.size(); i++) {
			Entity e = entities.get(i);
			e.tick();
		}
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).tick();
		}
	
		if(enemies.size() == 0) {
			//avança para o proximo level
			CUR_LEVEL++;
			if(CUR_LEVEL > MAX_LEVEL) {
				CUR_LEVEL = 1;
			}
			String newWorld = "level"+CUR_LEVEL+".png";
			World.restatGame(newWorld);
		}
		}else if(gameState == "GAME_OVER") {
			this.frameGameOver++;
			if(this.frameGameOver == 30) {
				this.frameGameOver = 0;
				if(this.showMassageGameOver)
					this.showMassageGameOver = false;
				else
					this.showMassageGameOver = true;
			}
			if(restartGame) {
				this.restartGame = false;
				this.gameState = "NORMAL";
				CUR_LEVEL =1;
				String newWorld = "level"+CUR_LEVEL+".png";
				World.restatGame(newWorld);
			}
		}else if(gameState == "MENU") {
			player.updateCamera();
			menu.tick();
		}
		
	}
	
	/*
	public void drawRectangle(int xoff, int yoff) {
		for(int xx = 0; xx < 32; xx++) {
			for(int yy = 0; yy < 32; yy++) {
				int xOff = xx + xoff;
				int yOff = yy + yoff;
				if(xOff<0 || yOff < 0|| xOff >= WIDTH || yOff >= HEIGHT) 
					continue;
				pixels[xOff+(yOff*WIDTH)] = 0xff0000;
			}
		}
	}
	*/
	
	public void applyLight() {
		for(int xx = 0; xx < Game.WIDTH; xx++) {
			for(int yy = 0; yy < Game.HEIGHT; yy++) {
				if(lightmapPixel[xx+(yy * Game.WIDTH)]== 0xffffffff) {
					pixels[xx+(yy*Game.WIDTH)] = 0; 
				}
			}
		}
	}
	
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if (bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		
		Graphics g = image.getGraphics();
		g.setColor(new Color(0,0,0));
		g.fillRect(0, 0,WIDTH,HEIGHT);
		
		//rederização do jogo
		world.render(g);
		Collections.sort(entities,Entity.nodeSorter);
		for(int i = 0; i< entities.size(); i++) {
			Entity e = entities.get(i);
			e.render(g);
		}
		for(int i = 0; i < bullets.size(); i++) {
			bullets.get(i).render(g);
		}
		
		applyLight();
		
		ui.render(g);
		/***/
		
		g.dispose();
		//drawRectangle(xx, yy);
		g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, WIDTH*SCALE, HEIGHT*SCALE,null);
		g.setFont(new Font("arial",Font.BOLD,20));
		g.setColor(Color.white);
		g.drawString("munição: " + player.ammo, 590, 20);
		
		if(gameState == "GAME_OVER") {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(new Color(0,0,0,100));
			g2.fillRect(0, 0, WIDTH*SCALE, HEIGHT*SCALE);
			g2.setFont(new Font("arial",Font.BOLD,28));
			g2.setColor(Color.white);
			g2.drawString("Game Over",(WIDTH*SCALE)/2 -60 ,(HEIGHT*SCALE)/2 -20);
			g2.setFont(new Font("arial",Font.BOLD,32));
			if(showMassageGameOver)
			g2.drawString("pass enter reciniar",(WIDTH*SCALE)/2 -130 ,(HEIGHT*SCALE)/2 +20);
		}else if(gameState == "MENU") {
			menu.render(g);
		}
		
		/*
		rotação de objetos
		Graphics2D g2 = (Graphics2D) g;
		double angleMouse = Math.atan2(200+25 - my, 200+25 - mx);
		g2.rotate(angleMouse, 200+25, 200+25);
		// System.out.println(Math.toDegrees(angleMouse));
		g.setColor(Color.red);
		g.fillRect(200, 200, 50, 50);
		*/
		
		/*
		renderização da nova font
		g.setFont(newfont);
		g.setColor(Color.red);
		g.drawString("teste com a nova font",90,90);
		*/
		bs.show();
	}
	
	public void run() {
		requestFocus();
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		int frames = 0;
		double time = System.currentTimeMillis();
		while(isRunning){
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1) {
			tick(); 
			render();
			frames++;
			delta--;
		}
		if(System.currentTimeMillis() - time >= 1000) {
			System.out.println("FPS: " + frames);
			frames = 0;
			time+=1000;
			}
		
		}
		
		stop();
	}
	
	public void keyTyped(KeyEvent e) {
		
	}
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT||
		e.getKeyCode() == KeyEvent.VK_D) {
			player.right = true;
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT||
				 e.getKeyCode() == KeyEvent.VK_A) {
			player.left = true;
		}
	
		if(e.getKeyCode() == KeyEvent.VK_UP||
				e.getKeyCode() == KeyEvent.VK_W) {
				player.up = true;
				
				if(gameState == "MENU") {
					menu.up = true;
				}
	}
		
		else  if(e.getKeyCode() == KeyEvent.VK_DOWN||
			 e.getKeyCode() == KeyEvent.VK_S) {
				player.down = true;
				
				if(gameState == "MENU") {
					menu.down = true;
				}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_X) {
			player.shoot = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			restartGame = true;
			if(gameState == "MENU") {
				menu.enter = true;
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			gameState ="MENU";
			menu.pause = true;
		}
		
		if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			if(gameState == "NORMAL") 
			saveGame = true;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_RIGHT||
				e.getKeyCode() == KeyEvent.VK_D) {
					player.right = false;
				}else if(e.getKeyCode() == KeyEvent.VK_LEFT||
						 e.getKeyCode() == KeyEvent.VK_A) {
					player.left = false;
				}
			
				if(e.getKeyCode() == KeyEvent.VK_UP||
						e.getKeyCode() == KeyEvent.VK_W) {
						player.up = false;
			}else  if(e.getKeyCode() == KeyEvent.VK_DOWN||
					 e.getKeyCode() == KeyEvent.VK_S) {
						player.down = false;
			}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		player.mouseShoot = true;
		player.mx = (e.getX() /3);
		player.my = (e.getY() /3);
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
