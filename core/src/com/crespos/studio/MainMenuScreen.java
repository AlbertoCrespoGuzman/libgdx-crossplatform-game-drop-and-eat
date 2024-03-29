package com.crespos.studio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MainMenuScreen  implements Screen {

    final Drop game;

    OrthographicCamera camera;
    float waiting_in_screen = 0;

    public MainMenuScreen(final Drop game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Bienvenidos al puto juego del siglo!!! ", 100, 150);
        game.font.draw(game.batch, "Toca la pantalla para empezar a fliparlo!", 100, 100);
        game.batch.end();

        waiting_in_screen += Gdx.graphics.getDeltaTime();
        System.out.println(Gdx.graphics.getDeltaTime());
        if (Gdx.input.isTouched() && waiting_in_screen > 0.1) {
            game.setScreen(new GameScreen(game));
            dispose();
        }
    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }

}