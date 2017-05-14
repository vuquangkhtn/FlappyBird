package com.develop.vuquang.flappybird;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FlappybirdActivity extends AppCompatActivity {

    public final int speed = 3;
    public int HEIGHT;
    public int WIDTH;

    public int yMotion,
            score=0;

    Matrix matrix = new Matrix();

    Timer timer;
    ValueAnimator animator;

    Random rand = new Random();
    TextView txtScore;
    ImageView bird;
    List<ImageView> pipes = new ArrayList<>();
    List<FrameLayout.LayoutParams> params;

    private Handler progressBarHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String returnedValue = (String)msg. obj;
            if(returnedValue.equals("Action")) {
                bird.setY(bird.getY() + speed);
                //temp
                List<ImageView> tempPipe = new ArrayList<>(pipes);

                Rect rc1 = new Rect();
                bird.getDrawingRect(rc1);

                for(ImageView tube:pipes) {
                    tube.setX(tube.getX() - speed);

                    //Ktra co bi dung cot khong
                    if(isViewOverlapping(bird,tube)) {
                        timer.cancel();
                        animator.cancel();
                        finish();
                        loginAct();
                    }

                    //Neu cot qua roi thi xoa
                    if(tube.getX()+ 100<=0) {
                        tempPipe.remove(tube);
                        txtScore.setText(String.valueOf((++score)/2));

                    }
                }

                if(pipes.size() != tempPipe.size()) {
                    pipes = tempPipe;
                    createPipe();
                }

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_flappybird);
        //Get width and heigth screen
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        HEIGHT = displayMetrics.heightPixels;
        WIDTH = displayMetrics.widthPixels;

        yMotion = 0;
        score = 0;

        findViewById(R.id.background).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                jump();
                return true;
            }
        });

        createAnimateGround();
        createBird();
        createScore();

        createPipe();
        createPipe();
        createPipe();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                progressBarHandler.sendMessage(progressBarHandler.obtainMessage(
                        1,
                        "Action"));
            }
        };
        timer = new Timer();
        timer.schedule(task,0L,20L);

    }

    public void jump() {
        bird.setY(bird.getY()-40);
    }

    private void createAnimateGround() {
        final ImageView ground = (ImageView) findViewById(R.id.ground);
        final ImageView ground2 = (ImageView) findViewById(R.id.ground2);

        animator = ValueAnimator.ofFloat(0.0f, -1.0f);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.setDuration(4000L);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float progress = (float) animation.getAnimatedValue();
                final float width = ground.getWidth();
                final float translationX = width * progress;
                ground.setTranslationX(translationX);
                ground2.setTranslationX(translationX + width);
            }
        });
        animator.start();
    }

    private void createScore() {
        txtScore = new TextView(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        txtScore.setX(10);
        txtScore.setY(10);
        txtScore.setTextColor(Color.WHITE);
        txtScore.setTextSize(30);
        txtScore.setText(String.valueOf(0));
        this.addContentView(txtScore,lp);
    }

    private void createBird() {
        bird = new ImageView(this);
        bird.setImageResource(R.drawable.bird);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                50,
                50);
        bird.setX(50);
        bird.setY(250);
        this.addContentView(bird,lp);
    }

    private void createPipe() {
        int space = 350;
        int width = 200;
        int height = 50 + rand.nextInt(250);
        ImageView upperTube = new ImageView(this);
        upperTube.setImageResource(R.drawable.toptube);
        upperTube.setX(WIDTH+ width*pipes.size()-100);
        upperTube.setY(-(320-height));

        FrameLayout.LayoutParams lpTop = new FrameLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);

        ImageView bottomTube = new ImageView(this);
        bottomTube.setImageResource(R.drawable.bottomtube);
        bottomTube.setX(WIDTH+ width*pipes.size()-100);
        bottomTube.setY(height+space);

        pipes.add(upperTube);
        pipes.add(bottomTube);

        this.addContentView(upperTube,lpTop);
        this.addContentView(bottomTube,lpTop);

    }

    private boolean isViewOverlapping(View firstView, View secondView) {

        final int[] location = new int[2];

        firstView.getLocationInWindow(location);
        Rect rect1 = new Rect(location[0], location[1],location[0] + firstView.getWidth(), location[1] + firstView.getHeight());

        secondView.getLocationInWindow(location);
        Rect rect2 = new Rect(location[0], location[1],location[0] + secondView.getWidth(), location[1] + secondView.getHeight());

        return rect1.intersect(rect2);
//        return  (rect1.contains(rect2)) || (rect2.contains(rect1));
    }

    private void loginAct() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
    }

}
