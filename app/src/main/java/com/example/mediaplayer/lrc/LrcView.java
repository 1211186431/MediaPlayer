package com.example.mediaplayer.lrc;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class LrcView extends View{

    private Paint paint;//画笔
    private List<LrcRow>list;//歌词数据源
    private int nowColor=Color.YELLOW;//正在播放的歌词颜色
    private int normalColor=Color.WHITE;//其他歌词的颜色
    private int lineColor=Color.CYAN;//分割线及时间显示的颜色
    private float textSize=40f;//歌词字体的大小
    private float timeSize=30f;//时间显示的大小
    private int lineHeight=4;//分割线的高度
    private int marginHeight=20;//歌词与歌词之间的间隔
    private int height;//自定义视图的高度
    private int width;//自定义视图的宽;
    private int index=0;//正在播放的歌词的行数
    private String tipstr="暂无歌词";//默认情况下的歌词
    private boolean TouchFlag=false;//手指按下的标志：当手指滑动的时候，界面不进行刷新
    //回调接口
    private MedCallBack medCallBack;
    private float lasty=0;//最后手指按下的坐标不

    public LrcView(Context context) {
        super(context);
// TODO Auto-generated constructor stub
    }
    public LrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
//实例化画笔，  抗锯齿
        paint=new Paint(Paint.ANTI_ALIAS_FLAG);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
// TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//getheight:获得界面内的高度
//getgetMeasuredHeight:获得视图实际测量的高度
        height=getMeasuredHeight();
        width=getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
// TODO Auto-generated method stub
        super.onDraw(canvas);

        paint.reset();//重置画笔
        paint.setColor(nowColor);
        paint.setTextSize(textSize);
        paint.setTextAlign(Align.CENTER);

        if (list==null) {
            canvas.drawText(tipstr, width/2, height/2-textSize,paint );
            return;
        }
        if (list.size()==0) {
            canvas.drawText(tipstr, width/2,height/2-textSize, paint);
            return;
        }
//绘制中间正在播放的歌词
        canvas.drawText(list.get(index).row, width/2, height/2-textSize, paint);
//绘制中间的分割线
        paint.reset();
        paint.setColor(lineColor);

        if (TouchFlag) {
            canvas.drawLine(0, height/2-textSize, width, height/2-textSize+lineHeight, paint);
            paint.setTextSize(timeSize);
            paint.setTextAlign(Align.LEFT);
            canvas.drawText(list.get(index).str_timer,0,height/2,paint);
        }
//绘制普通的歌词
        paint.reset();
        paint.setColor(normalColor);
        paint.setTextSize(textSize);
        paint.setTextAlign(Align.CENTER);
//绘制正在播放歌词上面的歌词
        int normalIndex=0;
        int rowY=0;//每行歌词的Y值
        normalIndex=index-1;
        rowY=(int)(height/2-textSize*2-marginHeight);

        while (normalIndex>=0&&rowY>-textSize) {

            canvas.drawText(list.get(normalIndex).row,width/2, rowY, paint);
            normalIndex--;
            rowY=(int) (rowY-textSize-marginHeight);
        }
//2.绘制播放歌词下面的歌词
        normalIndex=index+1;
        rowY=(int) (height/2+marginHeight);
        while(normalIndex<list.size()&&rowY<(height+textSize)){
            canvas.drawText(list.get(normalIndex).row, width/2, rowY, paint);
            normalIndex++;
            rowY=(int) (rowY+marginHeight+textSize);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (list==null) {
            return true;
        }
        if (list.size()==0) {
            return true;
        }
//手指按下
        if (event.getAction()==MotionEvent.ACTION_DOWN) {
            TouchFlag=true;//显示时间和分割线
            lasty=event.getY();//获取你手按下的Y轴坐标

//手指滑动
        }else if (event.getAction()==MotionEvent.ACTION_MOVE) {
            float nowY=event.getY();//当前手指滑动到的Y值
            float disY=nowY-lasty;//计算手指滑动的Y轴的距离

//判断滑动的距离跨越几行歌词
            if (Math.abs(disY)>marginHeight) {
                int num=(int)(Math.abs(disY)/(marginHeight+textSize));
                if (num>=1) {
                    if (disY<0) {
//快进
                        index+=num;
                        index=Math.min(list.size()-1, index);
                    }else if (disY>0) {
//快退
                        index-=num;
                        index=Math.max(0,index);
                    }
                }
            }
            lasty=nowY;
//手指抬起
        }else if (event.getAction()==MotionEvent.ACTION_UP) {
            TouchFlag=false;

//调用接口
            if (medCallBack!=null) {
                medCallBack.call(list.get(index).time);
            }
        }
        invalidate();//刷新布局
        return true;
    }


    //查找歌词的方法,根据播放的进度跟新歌词 ，根据传入的参数进行当前歌曲进度的跳播
    public void LrcToPlayer(long time){
        if (list==null) {
            return;
        }
        if (list.size()==0) {
            return;
        }
        if (TouchFlag) {
            return;
        }

//遍历整个歌词集合，寻找time的插入区间
        for (int i = 0; i < list.size(); i++) {
            LrcRow lrcRow=list.get(i);//当前的歌词对象
            LrcRow lrcRow2=(i+1)>=list.size()?null:list.get(i+1);
            if (time>lrcRow.time&&lrcRow2!=null&&time<lrcRow2.time) {
                index=i;
                break;
            }
            if (lrcRow2==null) {
                index=list.size()-1;
            }
        }
        invalidate();
    }

    public interface MedCallBack{
        //接口回调吧时间回调到主啊抽屉activity中去更新歌曲播放进度
        public void call(long time);
    }
    //设置歌词的方法
    public void setLrc(List<LrcRow>list){
        this.list=list;
    }
    public void setCall(MedCallBack medCallBack){
        this.medCallBack=medCallBack;
    }
}