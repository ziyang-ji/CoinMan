package com.ziyangji.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import org.w3c.dom.css.Rect;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	Texture coin, bomb;
	Texture dizzy;
	int manState = 0;
	int pause = 0;
	float velocity = 0.0f;
	float gravity = 1.0f;
	int manY;
	Random random;
	int coinCount = 0;
	int bombCount = 0;
	BitmapFont font;
	int gameState = 0;
    Rectangle manRectangle;
	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangle = new ArrayList<>();
	ArrayList<Rectangle> bombRectangle = new ArrayList<>();
	int score = 0;

	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		manY = Gdx.graphics.getHeight() / 2 - man[manState].getHeight() / 2;
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();
		dizzy = new Texture("dizzy-1.png");

		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
	}

	public void makeCoin() {
		float coinY = random.nextFloat() * Gdx.graphics.getHeight();
		coinXs.add(Gdx.graphics.getWidth());
		coinYs.add((int) coinY);
	}

	public void makeBomb() {
		float bombY = random.nextFloat() * Gdx.graphics.getHeight();
		bombXs.add(Gdx.graphics.getWidth());
		bombYs.add((int) bombY);
	}

	@Override
	public void render () {
		batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		if (gameState == 0) {
		    // Game waiting to start
            if (Gdx.input.justTouched()) {
                gameState = 1;
            }
        } else if (gameState == 1) {
		    // Game starts
            if (coinCount < 100) {
                coinCount++;
            } else {
                coinCount = 0;
                makeCoin();
            }

            if (bombCount < 250) {
                bombCount++;
            } else {
                bombCount = 0;
                makeBomb();
            }

            coinRectangle.clear();
            for (int i = 0; i < coinXs.size(); ++i) {
                batch.draw(coin, coinXs.get(i), coinYs.get(i));
                coinRectangle.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
                coinXs.set(i, coinXs.get(i) - 15);
            }

            bombRectangle.clear();
            for (int i = 0; i < bombXs.size(); ++i) {
                batch.draw(bomb, bombXs.get(i), bombYs.get(i));
                bombRectangle.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
                bombXs.set(i, bombXs.get(i) - 20);
            }

            if (Gdx.input.justTouched()) {
                velocity = -25;
            }

            if (pause < 4) {
                pause++;
            } else {
                pause = 0;
                if (manState < 3) {
                    manState++;
                } else {
                    manState = 0;
                }
            }
            velocity += gravity;
            manY -= velocity;
            if (manY <= 0) {
                manY = 0;
            }

        } else if (gameState == 2) {
		    // Game over
            if (Gdx.input.justTouched()) {
                gameState = 1;
                score = 0;
                velocity = 0;
                manY = Gdx.graphics.getHeight() / 2 - man[manState].getHeight() / 2;
                coinXs.clear();
                coinYs.clear();
                bombXs.clear();
                bombYs.clear();
                coinRectangle.clear();
                bombRectangle.clear();
                coinCount = 0;
                bombCount = 0;
            }
        }

		if (gameState == 2) {
		    batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
        } else {
            batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
        }
        manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());

        for (int i = 0; i < coinRectangle.size(); ++i) {
            if (Intersector.overlaps(manRectangle, coinRectangle.get(i))) {
                score++;
                coinRectangle.remove(i);
                coinXs.remove(i);
                coinYs.remove(i);
                break;
            }
        }

        for (int i = 0; i < bombRectangle.size(); ++i) {
            if (Intersector.overlaps(manRectangle, bombRectangle.get(i))) {
                gameState = 2;
            }
        }
        font.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
