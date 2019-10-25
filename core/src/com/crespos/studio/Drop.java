package com.crespos.studio;


import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public  class Drop extends Game {

	public SpriteBatch batch;
	public BitmapFont font;
	public BitmapFont fontGameOver;



	public void create() {
		batch = new SpriteBatch();
		//Use LibGDX's default Arial font.
		font = new BitmapFont();
		font.getData().setScale(2);
		fontGameOver = new BitmapFont();
		fontGameOver.getData().setScale(2);
		this.setScreen(new MainMenuScreen(this));
	}

	public void render() {
		super.render(); //important!
	}

	public void dispose() {
		batch.dispose();
		font.dispose();
		fontGameOver.dispose();
	}

}