package com.crespos.studio;


import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import javax.xml.soap.Text;

public class GameScreen implements Screen {
    final Drop game;

    Texture dropImage;
    Array<Texture> bucketImage;
    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle bucket;
    Array<Rectangle> raindrops;
    long lastDropTime;
    int dropsGathered;
    Object lastBucket;
    long oneSecond = 500;
    enum states {
            WAITING, OPENING_MOUTH, CLOSING_MOUTH, SMILING, DEAD
    }
    states currentState = states.WAITING;

    long currentTime;
    Animation playerAnimation;
    private float elapsed_time = 0f;
    long animationTime = 0;

    public GameScreen(final Drop game) {
        this.game = game;

        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("shit.png"));
        bucketImage = new Array<Texture>();
        bucketImage.add( new Texture(Gdx.files.internal("bolso_0.png")));
        bucketImage.add( new Texture(Gdx.files.internal("bolso_1.png")));
        bucketImage.add( new Texture(Gdx.files.internal("bolso_2.png")));
        bucketImage.add( new Texture(Gdx.files.internal("bolso_3.png")));
        bucketImage.add( new Texture(Gdx.files.internal("bolso_4.png")));

        TextureRegion tex2 = new TextureRegion(new Texture(Gdx.files.internal("bolso_2.png")));
        TextureRegion tex3 = new TextureRegion(new Texture(Gdx.files.internal("bolso_3.png")));

        playerAnimation = new Animation(0.8f,  tex2, tex3);


        lastBucket = new Texture(Gdx.files.internal("bolso_0.png"));
        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // create a Rectangle to logically represent the bucket
        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2; // center the bucket horizontally
        bucket.y = 20; // bottom left corner of the bucket is 20 pixels above
        // the bottom screen edge
        bucket.width = 64;
        bucket.height = 64;

        // create the raindrops array and spawn the first raindrop
        raindrops = new Array<Rectangle>();
        spawnRaindrop();
        currentTime = System.currentTimeMillis();
    }

    private void spawnRaindrop() {
        if(currentState != states.DEAD){
            Rectangle raindrop = new Rectangle();
            raindrop.x = MathUtils.random(0, 800 - 64);
            raindrop.y = 480;
            raindrop.width = 64;
            raindrop.height = 64;
            raindrops.add(raindrop);
            lastDropTime = System.currentTimeMillis();
        }
    }
    private Object updateBucket(Object lastBucket){
        Object currentBucket = lastBucket;
        boolean overlaps = false;


        /*
        for(Rectangle drop : raindrops){
            if(drop.overlaps(bucket)){
                currentState = states.CLOSING_MOUTH;
                currentTime = System.currentTimeMillis();
                overlaps  = true;
                currentBucket = bucketImage.get(2);

                break;
            }
        }
        */

  //      if(!overlaps){
            switch(currentState){
                case WAITING:
                    elapsed_time = 0f;
                    for(Rectangle drop : raindrops){
                        if((drop.x - bucket.x < 50) && (drop.y - bucket.y < 280)){
                            currentState = states.OPENING_MOUTH;
                            currentTime = System.currentTimeMillis();
                            currentBucket = bucketImage.get(1);
                            break;
                        }
                    }
                    break;
                case OPENING_MOUTH:
                    if(System.currentTimeMillis() - currentTime > oneSecond){
                        currentState = states.WAITING;
                        currentTime = System.currentTimeMillis();
                        currentBucket = bucketImage.get(0);
                    }
                    break;
                case CLOSING_MOUTH:
                    if(System.currentTimeMillis() - animationTime > 1000) {
                        elapsed_time = 0;
                        currentBucket = bucketImage.get(0);
                        currentState = states.WAITING;
                    }else{
                        elapsed_time += Gdx.graphics.getDeltaTime();
                        currentBucket = playerAnimation.getKeyFrame(elapsed_time);
                    }
                    break;
                case SMILING:
                    if(System.currentTimeMillis() - currentTime > oneSecond){
                        currentState = states.WAITING;
                        currentTime = System.currentTimeMillis();
                        currentBucket = bucketImage.get(0);
                    }
                    break;
                case DEAD:
                    if(System.currentTimeMillis() - currentTime > oneSecond){
                        game.fontGameOver.draw(game.batch, "Acabou !!! Merda Total Comida: "
                                + dropsGathered, 100, 240);
                        raindrops.removeAll(raindrops, true);

                    }else{
                        currentBucket = bucketImage.get(4);
                    }
                    break;
            }
       // }

        currentTime = System.currentTimeMillis();

        return currentBucket;
    }
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin();
        game.font.draw(game.batch, "Merda Comida: " + dropsGathered, 10, 480);
        Object currentBucket = updateBucket(lastBucket);
        if(currentBucket instanceof Texture){
            game.batch.draw((Texture)currentBucket, bucket.x, bucket.y, bucket.width, bucket.height);
        }else{
            game.batch.draw((TextureRegion) currentBucket, bucket.x, bucket.y, bucket.width, bucket.height);
        }
        if(currentState == states.DEAD){
            game.fontGameOver.draw(game.batch, "Bem feito!!! Voce comeu muita Merda mesmo! -> "
                    + dropsGathered, 100, 150);
            Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
        lastBucket = currentBucket;
        for (Rectangle raindrop : raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        game.batch.end();

        // process user input
        if (Gdx.input.isTouched() && currentState != states.DEAD) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = touchPos.x - 64 / 2;
        }else if(Gdx.input.isTouched() && currentState == states.DEAD){
            game.setScreen(new MainMenuScreen(game));
            dispose();
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT))
            bucket.x -= 200 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Keys.RIGHT))
            bucket.x += 200 * Gdx.graphics.getDeltaTime();

        // make sure the bucket stays within the screen bounds
        if (bucket.x < 0)
            bucket.x = 0;
        if (bucket.x > 800 - 64)
            bucket.x = 800 - 64;

        // check if we need to create a new raindrop
        if (System.currentTimeMillis() - lastDropTime > 6000)
            spawnRaindrop();



        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0){
                iter.remove();
                System.out.println("DEADDdddddddd d d d d dd ");
                currentState = states.DEAD;
                currentTime = System.currentTimeMillis();
            }

            if (raindrop.overlaps(bucket)) {
                currentState = states.CLOSING_MOUTH;
                currentTime = System.currentTimeMillis();
                lastBucket = bucketImage.get(2);
                dropsGathered++;
                dropSound.play();
                iter.remove();
                animationTime = System.currentTimeMillis();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {

        rainMusic.play();
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
        dropImage.dispose();
        for(Texture texture : bucketImage){
            texture.dispose();
        }
        dropSound.dispose();
        rainMusic.dispose();
    }

}