package com.example.zachcheu.platformer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends Activity implements SurfaceHolder.Callback {

    private Handler handlerApplication;
    private SurfaceHolder _surfaceHolder;
    private SurfaceView _surfaceView;
    private Bitmap random_texture_block;
    private Bitmap dirt_texture_block;
    private Bitmap water_texture_block;
    private Bitmap poll;
    private GameLoopThread thread;
    private float position, velocity;
    private Paint p;
    private static final int FRAMES_PER_SECOND = 61;
    private static final float SKIP_TICKS = 1000 / FRAMES_PER_SECOND;
    public static final int BLOCK_SIZE = 150;
    public static rectangle missile1;
    public static rectangle missile2;
    public static rectangle sideObject1;
    public static rectangle sideObject2;
    public static rectangle fallingObject1;
    public static rectangle fallingObject2;
    public int rvalue = 0;
    public int randObj = 0;
    public int missileAlt = 1;
    public int sideObjectAlt = 0;
    public int rSideObject;
    public float a = 0;
    public float b = 0;
    public float v = 0;
    static final long FPS_GAME = 60;
    Random r = new Random();
    Random c = new Random();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Display display = getWindowManager().getDefaultDisplay();
        RAM.SCREEN_HEIGHT = display.getHeight();
        RAM.SCREEN_WIDTH = display.getWidth();
        _surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        _surfaceHolder = _surfaceView.getHolder();
        _surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread = new GameLoopThread(_surfaceHolder, new Handler() {
            @Override
            public void close() {

            }

            @Override
            public void flush() {

            }

            @Override
            public void publish(LogRecord logRecord) {

            }
        });
        thread.setRunning(true);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
        thread.setSurfaceSize(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        boolean retry = true;
        thread.setRunning(false);
        while(retry){
            try{
                thread.join();
                retry = false;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    class GameLoopThread extends Thread{
        private int canvasWidth;
        private int canvasHeight;
        private boolean run  = false;

        public GameLoopThread(SurfaceHolder surfaceHolder, Handler handler){
            _surfaceHolder = surfaceHolder;
            handlerApplication = handler;
        }

        public void doStart(){
            synchronized (_surfaceHolder){
                missile1 = new missile(-10,1000,20,20,"left");
                missile2= new missile(10+canvasWidth,1000,20,20,"right");
                sideObject1 = new sideObject(10,750,20,20,"left");
                sideObject2 = new sideObject(10+canvasWidth,750,20,20,"right");
                fallingObject1 = new fallingObject(canvasWidth/4,-10,20,20);
                fallingObject2 = new fallingObject(3*canvasWidth/4,-10,20,20);
                position = 10f;
                velocity = 1.0f;
                p = new Paint();
                p.setColor(Color.WHITE);
            }
        }

        public void updateGame(){
            if(position >= 1000){
                position = 10;
            }else {
                position += 5;
            }
        }

        public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

            int width = bm.getWidth();

            int height = bm.getHeight();

            float scaleWidth = ((float) newWidth) / width;

            float scaleHeight = ((float) newHeight) / height;

            // CREATE A MATRIX FOR THE MANIPULATION

            Matrix matrix = new Matrix();

            // RESIZE THE BIT MAP

            matrix.postScale(scaleWidth, scaleHeight);

            // RECREATE THE NEW BITMAP

            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);

            return resizedBitmap;

        }

        public void run(){
            long ticksFPS = 1000/FPS_GAME;
            long startTime;
            long sleepTime;
            while(run){
                Canvas c = null;
                startTime = System.currentTimeMillis();
                try{
                    c = _surfaceHolder.lockCanvas(null);
                    synchronized (_surfaceHolder){
                        updateGame();
                        doDraw(c);
                    }
                }finally {
                    if(c != null){
                        _surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
                sleepTime = ticksFPS - (System.currentTimeMillis() - startTime);
                try{
                    if(sleepTime > 0){
                        sleep(sleepTime);
                    }else{
                        sleep(1);
                    }
                }catch (Exception e){

                }
            }
        }

        public void setRunning(boolean b){
            run = b;
        }

        public void setSurfaceSize(int width, int height){
            synchronized (_surfaceHolder){
                canvasHeight = height;
                canvasWidth = width;
                doStart();
            }
        }

        private void doDraw(final Canvas canvas){
            if(run){
                canvas.save();
                canvas.drawColor(Color.parseColor("#FFFFFF"));
                ++randObj;
                if(randObj>=120) {
                    rvalue = r.nextInt(3);
                    rSideObject = (c.nextInt(4) + 1);
                    randObj =0;
                }
                else if(rvalue == 0) {
                    if(missileAlt == 0) {
                        missile1.draw(canvas);
                        a++;
                        missile1.setX(missile1.getX() + (float) (2 * Math.sqrt(a)));
                        if (missile1.getX() > RAM.SCREEN_WIDTH) {
                            missile1.setX(10);
                            a = 0;
                            randObj = 190;
                            missileAlt = 1;
                        }
                    }
                    else if(missileAlt == 1){
                        missile2.draw(canvas);
                        a++;
                        missile2.setX(missile2.getX() - (float) (2 * Math.sqrt(a)));
                        if (missile2.getX() < -10) {
                            missile2.setX(canvasWidth+10);
                            a = 0;
                            randObj = 190;
                            missileAlt = 0;
                        }
                    }
                }
                else if(rvalue == 1)
                {
                    if(sideObjectAlt == 0) {
                        sideObject1.draw(canvas);
                        b++;
                        sideObject1.setX(sideObject1.getX() + ((rSideObject) + 3));
                        sideObject1.setY(sideObject1.getY() + (float) ((-18 + 3 * (Math.sqrt(b)))));
                        if (sideObject1.getY() > 1100) {
                            b = 0;
                            randObj = 190;
                            sideObject1.setX(10);
                            sideObject1.setY(700+(r.nextInt(9)*30));
                            sideObjectAlt = 1;
                        }
                    }
                    else if(sideObjectAlt == 1){
                        sideObject2.draw(canvas);
                        b++;
                        sideObject2.setX(sideObject2.getX() - ((rSideObject) + 3));
                        sideObject2.setY(sideObject2.getY() + (float) ((-18 + 3 * (Math.sqrt(b)))));
                        if (sideObject2.getY() > 1100) {
                            b = 0;
                            randObj = 190;
                            sideObject2.setX(10 + canvasWidth);
                            sideObject2.setY(700+(r.nextInt(9)*30));
                            sideObjectAlt = 0;
                        }
                    }
                }
                else if(rvalue == 2) {
                    if(/*player on first half*/) {
                        fallingObject1.draw(canvas);
                        v++;
                        fallingObject1.setY(fallingObject1.getY() + (float) (3 * (Math.sqrt(v))));
                        if (fallingObject1.getY() > 1100) {
                            v = 0;
                            randObj = 190;
                            fallingObject1.setX(r.nextInt(365) + 150);
                            fallingObject1.setY(-10);
                        }
                    }
                    if(/*player on second half*/){
                        fallingObject2.draw(canvas);
                        v++;
                        fallingObject2.setY(fallingObject2.getY() + (float) (3 * (Math.sqrt(v))));
                        if (fallingObject2.getY() > 1100) {
                            v = 0;
                            randObj = 190;
                            fallingObject2.setX(r.nextInt(365) + 515);
                            fallingObject2.setY(-10);
                        }
                    }
                }

            }
            canvas.restore();
        }
    }
}