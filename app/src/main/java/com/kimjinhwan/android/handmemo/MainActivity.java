package com.kimjinhwan.android.handmemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FrameLayout layout;
    RadioGroup color;                  // 색상조절 옵션
    SeekBar stroke;                    // 두께조절 옵션

    Board board;                       // 그림판
    ImageView imageView;               // 캡쳐한 이미지를 썸네일로 화면에 표시

    int opt_brush_color = Color.BLACK; // 브러쉬 색상 기본값
    float opt_brush_width = 10f;       // 브러쉬 두께 기본값 1
    /* 브러쉬는 값을 조절할때 마다 그림판에 새로 생성됨 */

    // 캡쳐한 이미지를 저장하는 변수
    Bitmap captured = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        // 그림판이 들어가는 프레임 레이아웃
        layout = (FrameLayout) findViewById(R.id.layout);
        // 색상선택
        color = (RadioGroup) findViewById(R.id.color);
        color.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.rdBlack:
                        setBrushColor(Color.BLACK);
                        break;
                    case R.id.rdBlue:
                        setBrushColor(Color.BLUE);
                        break;
                    case R.id.rdGreen:
                        setBrushColor(Color.GREEN);
                        break;
                    case R.id.rdRed:
                        setBrushColor(Color.RED);
                        break;
                    case R.id.rdWhite:
                        setBrushColor(Color.WHITE);
                        break;
                }
            }
        });

        // 두께 선택
        stroke = (SeekBar) findViewById(R.id.stroke);
        stroke.setProgress(10);
        stroke.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                opt_brush_width = progress + 1; // seekbar 가 0부터 시작하므로 1을 더해준다.
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            // 터치가 종료되었을 때만 값을 세팅해준다.
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                setBrushStroke(opt_brush_width);
            }
        });
        // 썸네일 이미지뷰
        imageView = (ImageView) findViewById(R.id.imageView);
        // 캡쳐를 할 뷰의 캐쉬를 사용한다.
        //layout.setDrawingCacheEnabled(true);


        //저장버튼
        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 드로잉 캐쉬를 지워주고
                layout.destroyDrawingCache();
                // 다시 만들고
                layout.buildDrawingCache();
                // 레이아웃의 그려진 내용을 Bitmap 형태로 가져온다.
                captured = layout.getDrawingCache();
                // 캡쳐한 이미지를 썸네일에 보여준다.
                imageView.setImageBitmap(captured);
            }
        });

        // 1. 그림판을 새로 생성한다.
        board = new Board(getBaseContext());
        // 2. 생성된 보드를 화면에 세팅한다.
        layout.addView(board);
        // 3. 기본 브러쉬 세팅
        setBrush();
    }

    /*
        컬러와 두께는 조절 할 때마다 새로운 브러쉬를 생성하여 그림판에 담는다.
        * 사용하지 않은 브러쉬는 그냥 버려진다 *
     */
    // 컬러 옵션값 조절
    private void setBrushColor(int colorType){
        opt_brush_color = colorType;
        setBrush();
    }
    // 두께 옵션값 조절
    private void setBrushStroke(float width){
        opt_brush_width = width;
        setBrush();
    }

    // 현재 설정된 옵션값을 사용하여 브러쉬를 새로 생성하고 그림판에 담는다.
    private void setBrush(){
        Brush brush = new Brush();
        brush.color = opt_brush_color;
        brush.stroke = opt_brush_width;
        board.setBrush(brush);
    }

    /*
        그림판
     */
    class Board extends View {
        Paint paint;
        List<Brush> brushes;
        Brush current_brush;
        Path current_path;

        // 브러쉬의 속성값 변경 여부를 판단하기 위한 플래그 > 브러쉬의 속성값이 바뀌면 Path를 다시 생성한다.
        boolean newBrush = true;

        public Board(Context context) {
            super(context);
            setPaint();
            brushes = new ArrayList<>();
        }
        // 처음 한번만 기본 페인트 속성을 설정해둔다.
        private void setPaint(){
            // Paint 의 기본속성만 적용해 두고, color 와 두께는 Brush에서 가져다가 그린다.
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setDither(true);
        }
        // 브러쉬를 새로 생성한다.
        public void setBrush(Brush brush) {
            current_brush = brush;
            newBrush = true;
        }
        // Path를 새로 생성한다.
        private void createPath(){
            if(newBrush) { // 브러쉬가 변경되었을 때만 Path를 생성해준다.
                current_path = new Path();
                newBrush = false; // 브러쉬를
                current_brush.addPath(current_path);
                brushes.add(current_brush);
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            for(Brush brush : brushes) {
                // 브러쉬에서 속성값을 꺼내서 Paint 에 반영한다.
                paint.setStrokeWidth(brush.stroke);
                paint.setColor(brush.color);
                // 속성값이 반영된 Paint 와 Path를 화면에 그려준다.
                canvas.drawPath(brush.path, paint);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // 내가 터치한 좌표를 꺼낸다
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()){
                // 터치가 시작되면 Path 를 생성하고 현재 지정된 브러쉬와 함께 저장소에 담아둔다.
                case MotionEvent.ACTION_DOWN :
                    createPath();
                    current_path.moveTo(x,y); // 이전점과 현재점 사이를 그리지 않고 이동한다.
                    break;
                case MotionEvent.ACTION_MOVE :
                    current_path.lineTo(x,y); // 바로 이전점과 현재점사이에 줄을 그어준다.
                    break;
                case MotionEvent.ACTION_UP :
                    // none
                    break;
            }

            // 화면을 갱신해서 위에서 그린 Path를 반영해 준다.
            invalidate();

            // 리턴 false 일 경우 touch 이벤트를 연속해서 발생시키지 않는다.
            // 즉, 드래그시 onTouchEvent 가 호출되지 않는다
            return true;
        }
    }

    /*
        브러쉬
     */
    class Brush {
        Path path;      // 브러쉬로 그리는 경로를 같이 담아둔다
        int color;
        float stroke;

        public void addPath(Path path){
            this.path = path;
        }
    }
}