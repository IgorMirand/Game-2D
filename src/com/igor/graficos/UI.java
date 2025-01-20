package com.igor.graficos;

import java.awt.Color;


import java.awt.Font;
import java.awt.Graphics;

import com.igor.main.Game;

//import co.igor.entities.Player;

public class UI {

	public void render(Graphics g) {
		g.setColor(Color.red);
		g.fillRect(8,4,70,8);
		g.setColor(Color.green);
		g.fillRect(8,4,(int)((Game.player.life/Game.player.maxlife)*70),8);
		g.setColor(Color.white);
		g.setFont(new Font("arial",Font.BOLD,8));
		g.drawString((int)Game.player.life+"/"+(int)Game.player.maxlife,30,11);
	}
}
