package com.example.wins.quadcopter;


import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {

    MediaPlayer mp;         //定义MediaPlayer

    //起飞和降落线程
    Thread thread_tf;
    Thread thread_ld;

    Socket socket;
    OutputStream out;
    byte[] data = new byte[34]; //定义通信数组
    boolean flag = false;
    boolean flag2 = false,flag3 = false;
    int x = 0;  //油门
    //默认方向值
    int forward=1500;
    int backward=1400;
    int leftward=1850;
    int rightward=1500;
    int clockwise=1500;
    int anticlockwise=1500;

    TextView uptext, downtext, lefttext, righttext;
    TextView youtext;
    RelativeLayout rl;
    Button bt_setdt;
    Button bt_conn;
    Button bt_forward;
    Button bt_backward;
    Button bt_leftward;
    Button bt_rightward;
    Button bt_clockwise;
    Button bt_anticlockwise;
    Button bt_tf;
    Button bt_ld;

    Button btup, btdown;

    SeekBar sb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置成全屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//强制为横屏
        initdata();


    //    tv_ci = (TextView) findViewById(R.id.tv_ci);
     //   tv_dr= (TextView) findViewById(R.id.tv_dr);
     //   rl = (RelativeLayout) findViewById(R.id.rl);

        uptext = (TextView) findViewById(R.id.uptext);
        downtext = (TextView) findViewById(R.id.downtext);
        lefttext = (TextView) findViewById(R.id.lefttext);
        righttext = (TextView) findViewById(R.id.righttext);

        youtext = (TextView) findViewById(R.id.youtext);


    //    bt_setdt = (Button) findViewById(R.id.bt_setdt);
        bt_conn = (Button) findViewById(R.id.wifi_btn);
        bt_forward = (Button) findViewById(R.id.up_btn);
        bt_backward = (Button) findViewById(R.id.down_btn);
        bt_leftward = (Button) findViewById(R.id.left_btn);
        bt_rightward = (Button) findViewById(R.id.right_btn);
        /*
        bt_clockwise = (Button) findViewById(R.id.bt_clockwise_rotation);
        bt_anticlockwise = (Button) findViewById(R.id.bt_anticlockwise_rotation);

        */
        bt_tf = (Button) findViewById(R.id.start_btn);
        bt_ld = (Button) findViewById(R.id.stop_btn);

        btup = (Button) findViewById(R.id.turnLeft);
        btdown = (Button) findViewById(R.id.turnRight);

        sb = (SeekBar) findViewById(R.id.verticalSeekBar);

        sb_setgas();
    }

    /**
     * 本方法作用：
     * 指定并加载音频文件
     */
    public void initmusic(int music)
    {
        mp=MediaPlayer.create(MainActivity.this, music);                    //调用MediaPlayer指定要播放的音频文件
        try
        {
            mp.prepare();                                                                       //加载音频文件
        }
        catch (IllegalStateException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //初始化控制数据
    public void initdata()
    {
        //协议规定的固定值
        data[0]=(byte) 0xAA;
        data[1]=(byte) 0xC0;
        data[2]=(byte) 0x1c;

        //控制上下方向
        data[3]=(byte) (0>>8);        //给油门赋值，把二进制拆成高八位
        data[4]=(byte) (0&0xff);      //给油门赋值，把二进制拆成低八位

        //控制左右旋转
        data[5]=(byte) (1500>>8);        //给航向赋值，把二进制拆成高八位
        data[6]=(byte) (1500&0xff);      //给航向赋值，把二进制拆成低八位

        //控制左右方向
        data[7]=(byte) (1500>>8);        //给横滚赋值，把二进制拆成高八位
        data[8]=(byte) (1500&0xff);      //给横滚赋值，把二进制拆成低八位

        //控制前后方向
        data[9]=(byte) (1500>>8);        //给俯仰赋值，把二进制拆成高八位
        data[10]=(byte) (1500&0xff);      //给俯仰赋值，把二进制拆成低八位

        //协议规定的固定值
        data[31]=(byte) 0x1c;
        data[32]=(byte) 0x0D;
        data[33]=(byte) 0x0A;
    }

    //点击按钮连接并启动无人机
    public void bt_conn(View view)
    {
        //连接启动
        if(flag==false)
        {
            flag = true;
            Thread t1 = new Thread(new connThread());    //调用线程类并指定要启动的线程类
            t1.start();
        }else {
            youtext.setText("already started!!");
            bt_conn.setEnabled(false);  //禁止再次启动
        }

    }

    //创建连接启动线程
    public class connThread implements Runnable
    {

        @Override
        public void run() {
            try {
                //发送连接请求
                socket = new Socket("192.168.4.1",333);
                //调用输出流给无人机发连接信息
                out = socket.getOutputStream();
                //调用write方法发送连接信息"GEC\r\n"
                out.write("GEC\r\n".getBytes());
                //发送数据
                out.flush();

                //每隔5毫秒发送数据
                do {
                    out.write(data);        //调用write发通信数组给无人机
                    out.flush();
                    Thread.sleep(5);
                }while (flag);

            }catch (UnknownHostException e){
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //重置方向
    public void resetDerection()
    {
        forward=1500;
        backward=1500;
        leftward=1500;
        rightward=1500;
        clockwise=1500;
        anticlockwise=1500;
    }


    //复位
    public void bt_reset(View view)
    {
        resetDerection();       //重置方向
        ShowCurrentInfo();      //显示当前信息
        try {
            socket.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

    //youtextyoutext    tv_dr.setText("当前方向：中");      //显示重置后方向信息
    }

    //停止
    public void bt_stop(View view)
    {
        x=0;                           //把油门设为0
        data[3]=(byte) (x>>8);        //给油门赋值，把二进制拆成高八位
        data[4]=(byte) (x&0xff);      //给油门赋值，把二进制拆成低八位
        flag=false;
        bt_conn.setEnabled(true);
        sb.setProgress(x);      //设置油门滑动条

    }



    //点击加油
    public void bt_up(View view)
    {
        if(mp!=null)                                                                              //如果播放器不等于空
        {
            mp.reset();                                                                             //就停止
        }
        initmusic(R.raw.vc_up);                                                         //调用initmusic方法，并指定要播放的文件。
        mp.start();

        Log.d("aaaaa","sdfsdfsdfsdf");

        if(x<981)
        {
            x += 20;
            sb.setProgress(x);      //设置油门滑动条
            data[3]=(byte) (x>>8);        //给油门赋值，把二进制拆成高八位
            data[4]=(byte) (x&0xff);      //给油门赋值，把二进制拆成低八位
            ShowCurrentInfo();
        }else {
            ShowCurrentInfo();
        }
    }

    //点击减油
    public void bt_down(View view)
    {
       if(mp!=null)                                                                              //如果播放器不等于空
        {
            mp.reset();                                                                             //就停止
        }
        initmusic(R.raw.vc_down);                                                         //调用initmusic方法，并指定要播放的文件。
        mp.start();

        if(x>19)
        {
            x -= 20;
            sb.setProgress(x);      //设置油门滑动条
            data[3]=(byte) (x>>8);        //给油门赋值，把二进制拆成高八位
            data[4]=(byte) (x&0xff);      //给油门赋值，把二进制拆成低八位
            ShowCurrentInfo();
        }else {
            ShowCurrentInfo();
        }
    }



    //用滑动条设置油门参数
    public void sb_setgas()
    {
        sb.setMax(1000);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                x = progress;
                data[3]=(byte) (x>>8);        //给油门赋值，把二进制拆成高八位
                data[4]=(byte) (x&0xff);      //给油门赋值，把二进制拆成低八位
                ShowCurrentInfo();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //设置模块是否可见
 /*   public void bt_setVisibility(View view)
    {

        rl = (RelativeLayout) findViewById(R.id.rl);
        if (rl.getVisibility()==View.INVISIBLE)
        {
            rl.setVisibility(View.VISIBLE);
        }else {
            rl.setVisibility(View.INVISIBLE);
        }

    }*/




    //显示设置向前参数滑动条
 /*   public void bt_forward(View view)
    {
        if(sb_forward.getVisibility()==View.INVISIBLE)
        {
            sb_clockwise.setVisibility(View.INVISIBLE);
            sb_anticlockwise.setVisibility(View.INVISIBLE);
            sb_forward.setVisibility(View.VISIBLE);
            sb_backward.setVisibility(View.INVISIBLE);
            sb_leftward.setVisibility(View.INVISIBLE);
            sb_rightward.setVisibility(View.INVISIBLE);
            sb_setforward();

        }else {
            sb_forward.setVisibility(View.INVISIBLE);
        }

    }*/


    //设置向前参数
  /*  public void sb_setforward()
    {
        sb_forward.setMax(1500);

        sb_forward.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                forward = progress+1500;             //加1500即设置滚动条最小值为1500,最大为1500+1500
                data[9]=(byte) (forward>>8);        //给俯仰赋值，把二进制拆成高八位
                data[10]=(byte) (forward&0xff);      //给俯仰赋值，把二进制拆成低八位
                ShowCurrentInfo();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }*/

    //显示设置向后参数滑动条
 /*   public void bt_backward(View view)
    {
        if(sb_backward.getVisibility()==View.INVISIBLE)
        {
            sb_clockwise.setVisibility(View.INVISIBLE);
            sb_anticlockwise.setVisibility(View.INVISIBLE);
            sb_backward.setVisibility(View.VISIBLE);
            sb_forward.setVisibility(View.INVISIBLE);
            sb_leftward.setVisibility(View.INVISIBLE);
            sb_rightward.setVisibility(View.INVISIBLE);
            sb_setbackward();

        }else {
            sb_backward.setVisibility(View.INVISIBLE);
        }

    }

    //设置向后参数
    public void sb_setbackward()
    {
        sb_backward.setMax(1500);

        sb_backward.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                backward = progress;                //最大值为1500
                data[9]=(byte) (backward>>8);        //给俯仰赋值，把二进制拆成高八位
                data[10]=(byte) (backward&0xff);      //给俯仰赋值，把二进制拆成低八位
                ShowCurrentInfo();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //显示设置向左参数滑动条
    public void bt_leftward(View view)
    {
        if(sb_leftward.getVisibility()==View.INVISIBLE)
        {
            sb_clockwise.setVisibility(View.INVISIBLE);
            sb_anticlockwise.setVisibility(View.INVISIBLE);
            sb_leftward.setVisibility(View.VISIBLE);
            sb_backward.setVisibility(View.INVISIBLE);
            sb_forward.setVisibility(View.INVISIBLE);
            sb_rightward.setVisibility(View.INVISIBLE);
            sb_setleftward();

        }else {
            sb_leftward.setVisibility(View.INVISIBLE);
        }

    }

    //设置向左参数
    public void sb_setleftward()
    {
        sb_leftward.setMax(1500);

        sb_leftward.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                leftward = progress+1500;             //加1500即设置滚动条最小值为1500
                data[7]=(byte) (leftward>>8);        //给横滚赋值，把二进制拆成高八位
                data[8]=(byte) (leftward&0xff);      //给横滚赋值，把二进制拆成低八位
                ShowCurrentInfo();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //显示设置向右参数滑动条
    public void bt_rightward(View view)
    {
        if(sb_rightward.getVisibility()==View.INVISIBLE)
        {
            sb_clockwise.setVisibility(View.INVISIBLE);
            sb_anticlockwise.setVisibility(View.INVISIBLE);
            sb_rightward.setVisibility(View.VISIBLE);
            sb_backward.setVisibility(View.INVISIBLE);
            sb_leftward.setVisibility(View.INVISIBLE);
            sb_forward.setVisibility(View.INVISIBLE);
            sb_setrightward();

        }else {
            sb_rightward.setVisibility(View.INVISIBLE);
        }

    }

    //设置向右参数
    public void sb_setrightward()
    {
        sb_rightward.setMax(1500);

        sb_rightward.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rightward = progress;             //最大值为1500
                data[9]=(byte) (rightward>>8);        //给横滚赋值，把二进制拆成高八位
                data[10]=(byte) (rightward&0xff);      //给横滚赋值，把二进制拆成低八位
                ShowCurrentInfo();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //显示设置顺时针参数滑动条
    public void bt_clockwise(View view)
    {
        if(sb_clockwise.getVisibility()==View.INVISIBLE)
        {
            sb_clockwise.setVisibility(View.VISIBLE);
            sb_anticlockwise.setVisibility(View.INVISIBLE);
            sb_rightward.setVisibility(View.INVISIBLE);
            sb_backward.setVisibility(View.INVISIBLE);
            sb_leftward.setVisibility(View.INVISIBLE);
            sb_forward.setVisibility(View.INVISIBLE);
            sb_clockwise();

        }else {
            sb_clockwise.setVisibility(View.INVISIBLE);
        }

    }

    //设置顺时针旋转参数
    public void sb_clockwise()
    {
        sb_clockwise.setMax(1500);

        sb_clockwise.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                clockwise = progress;             //加1500即设置滚动条最小值为1500
                data[5]=(byte) (clockwise>>8);        //给航向赋值，把二进制拆成高八位
                data[6]=(byte) (clockwise&0xff);      //给航向赋值，把二进制拆成低八位
                ShowCurrentInfo();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //显示设置逆时针参数滑动条
    public void bt_anticlockwise(View view)
    {
        if(sb_anticlockwise.getVisibility()==View.INVISIBLE)
        {
            sb_clockwise.setVisibility(View.INVISIBLE);
            sb_anticlockwise.setVisibility(View.VISIBLE);
            sb_rightward.setVisibility(View.INVISIBLE);
            sb_backward.setVisibility(View.INVISIBLE);
            sb_leftward.setVisibility(View.INVISIBLE);
            sb_forward.setVisibility(View.INVISIBLE);
            sb_anticlockwise();

        }else {
            sb_anticlockwise.setVisibility(View.INVISIBLE);
        }

    }

    //设置逆时针旋转参数
    public void sb_anticlockwise()
    {
        sb_anticlockwise.setMax(1500);

        sb_anticlockwise.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                anticlockwise = progress+1500;             //最大值为1500
                data[5]=(byte) (anticlockwise>>8);        //给航向赋值，把二进制拆成高八位
                data[6]=(byte) (anticlockwise&0xff);      //给航向赋值，把二进制拆成低八位
                ShowCurrentInfo();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }  */

    //点击向前
    public void bt_fw(View view)
    {

        if(mp!=null)                                                                              //如果播放器不等于空
        {
            mp.reset();                                                                             //就停止
        }
        initmusic(R.raw.vc_forward);                                                         //调用initmusic方法，并指定要播放的文件。
        mp.start();

        while (backward<1500)
        {
            backward += 1;
        }
        if(forward==3000)
        {
            forward -= 50;
        }
        else
        {
            forward += 50;
            data[9]=(byte) (forward>>8);        //给俯仰赋值，把二进制拆成高八位
            data[10]=(byte) (forward&0xff);      //给俯仰赋值，把二进制拆成低八位
            ShowCurrentInfo();
        //    ShowDerection(1);
        }

    }

    //点击向后
    public void bt_bw(View view)
    {
        if(mp!=null)                                                                              //如果播放器不等于空
        {
            mp.reset();                                                                             //就停止
        }
        initmusic(R.raw.vc_backward);                                                         //调用initmusic方法，并指定要播放的文件。
        mp.start();

        while (forward>1500)
        {
            forward -= 1;
        }
        if(backward==0)
        {
            backward += 50;
        }
        else
        {
            backward -= 50;
            data[9]=(byte) (backward>>8);        //给俯仰赋值，把二进制拆成高八位
            data[10]=(byte) (backward&0xff);      //给俯仰赋值，把二进制拆成低八位
            ShowCurrentInfo();
          //  ShowDerection(2);
        }
    }


    //点击向左
    public void bt_lw(View view)
    {
        if(mp!=null)                                                                              //如果播放器不等于空
        {
            mp.reset();                                                                             //就停止
        }
        initmusic(R.raw.vc_leftward);                                                         //调用initmusic方法，并指定要播放的文件。
        mp.start();

        while (rightward<1500)
        {
            rightward += 1;
        }
        if(leftward==3000)
        {
            leftward -= 50;
        }else {
            leftward += 50;
            data[7]=(byte) (leftward>>8);
            data[8]=(byte) (leftward&0xff);
            ShowCurrentInfo();
          //  ShowDerection(3);
        }
    }

    //点击向右
    public void bt_rw(View view)
    {
        if(mp!=null)                                                                              //如果播放器不等于空
        {
            mp.reset();                                                                             //就停止
        }
        initmusic(R.raw.vc_rightward);                                                         //调用initmusic方法，并指定要播放的文件。
        mp.start();

        while (leftward>1500)
        {
            leftward -= 1;
        }
        if(rightward==0)
        {
            rightward += 50;
        }else {
            rightward -= 50;
            data[7]=(byte) (rightward>>8);
            data[8]=(byte) (rightward&0xff);
            ShowCurrentInfo();
          //  ShowDerection(4);
        }
    }

    //点击起飞
    public void bt_tf(View view)
    {

        if(mp!=null)                                           //如果播放器不等于空
        {
            mp.reset();                                        //就停止
        }
       initmusic(R.raw.vc_takeoff);                           //调用initmusic方法，并指定要播放的文件。
        mp.start();

        //重置方向
        resetDerection();
      //  tv_dr.setText("当前方向：中");

        //起飞到悬停值
        if(x<500)
        {
            //中断降落
            flag2 = true;
            flag3 = false;
            if (thread_ld!=null)
            {
                thread_ld.interrupt();
            }

            //创建起飞线程
            thread_tf = new Thread(){
                @Override
                public void run() {
                    while (x<500 && flag2)
                    {
                        x += 1;

                        //控制上下方向
                        data[3]=(byte) (x>>8);        //给油门赋值，把二进制拆成高八位
                        data[4]=(byte) (x&0xff);      //给油门赋值，把二进制拆成低八位

                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {     //在子线程中开启子线程
                                ShowCurrentInfo();      //显示实时信息
                                sb.setProgress(x);      //设置油门滑动条
                            }
                        });
                    }
                }
            };
            thread_tf.start();      //启动线程

        } else {
            youtext.setText("已经起飞！");
        }

    }

    //点击降落
    public void bt_ld(View view) {

        if (mp != null)                            //如果播放器不等于空
        {
            mp.reset();                          //就停止
        }
        initmusic(R.raw.vc_landing);            //调用initmusic方法，并指定要播放的文件。
        mp.start();

        //重置方向
        resetDerection();
      //  tv_dr.setText("当前方向：中");

        //开始降落
        if (x > 0) {

            //中断起飞
            flag2 = false;
            flag3 = true;
            if (thread_tf != null) {
                thread_tf.interrupt();
            }

            //创建开始降落线程
            thread_ld = new Thread() {
                @Override
                public void run() {
                    while (x > 0 && flag3) {
                        x -= 1;

                        //控制上下方向
                        data[3] = (byte) (x >> 8);        //给油门赋值，把二进制拆成高八位
                        data[4] = (byte) (x & 0xff);      //给油门赋值，把二进制拆成低八位

                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {     //在子线程中开启子线程
                                ShowCurrentInfo();      //显示实时信息
                                sb.setProgress(x);      //设置油门滑动条
                            }
                        });
                    }
                }
            };
            thread_ld.start();      //启动线程


        } else {
            youtext.setText("已经降落！");
          //  tv_dr.setText("当前方向：中");
        }
    }


    //显示实时数据
   public void ShowCurrentInfo()
    {
      /*  tv_ci.setText(
                "油门："+x+
                        "\n前："+forward+
                        "   后："+backward+
                        "\n左："+leftward+
                        "   右："+rightward+
                        "\n顺："+clockwise+
                        "   逆"+anticlockwise);

        */
        uptext.setText("前：" + forward);
        downtext.setText("后：" + backward);
        lefttext.setText("左：" + leftward);
        righttext.setText("右：" + rightward);

        youtext.setText("油门：" + x);
    }

    //显示方向信息
  /*  public void ShowDerection(int dr)
    {
        switch (dr)
        {
            case 1 : tv_dr.setText("当前方向：前");   break;
            case 2 : tv_dr.setText("当前方向：后");   break;
            case 3 : tv_dr.setText("当前方向：左");   break;
            case 4 : tv_dr.setText("当前方向：右");   break;
            default: break;
        }
    }*/

}

