/*
* 文件名：MainActivity
* 描    述：Socket测试
* 作    者：Ace
* 时    间：2017-01-23
* 版    权：Ace版权
*/
package ace.know.you.ado.mysockettest;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements Runnable {


    private static final String HOST = "10.10.100.254";
    private static final int PORT = 8899;
    private Socket socket = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String content = "";
    private TextView tv_msg = null;
    private Button btn_send = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        tv_msg = (TextView) findViewById(R.id.tv_msg);
        btn_send = (Button) findViewById(R.id.btn_send);



        try {
            socket = new Socket(HOST, PORT);
            in = new BufferedReader(new InputStreamReader(socket
                    .getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    socket.getOutputStream())), true);
        } catch (IOException ex) {
            ex.printStackTrace();
            ShowDialog("login exception" + ex.getMessage());
        }

        btn_send.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

//                (1).串口设备给 Wifi 模块连续发送“+++”,模块收到“+++”后,给设备返回一个确认码‘a’。
//                (2).设备接收到模块返回的‘a’后,必须在 3 秒内给模块再发送一个确认码‘a’。
//                (3).模块在接收到确认码‘a’后,给设备发送“+ok”确认,并进入“串口 AT 命令模式”。
//                (4).设备接收到“+ok”后,即可向 Wifi 模块发送 AT 指令,进行参数查询和设置。
//                从串口 AT 命令模式切换为透明传输模式、串口指令模式、HTTPD Client 的时序:
//                (1).串口设备给 Wifi 模块发送指令“AT+ENTM”。
//                (2).模块在接收到指令后,回显“+ok”,并回到原工作模式。
                for (int i = 0; i < 3; i++) {
                    char msg = '+';//ed_msg.getText().toString();
                    if (socket.isConnected()) {
                        if (!socket.isOutputShutdown()) {
                            out.print(msg);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
        //启动线程，接收服务器发送过来的数据
        new Thread(MainActivity.this).start();


    }


    /**
     * 如果连接出现异常，弹出AlertDialog！
     */
    public void ShowDialog(String msg) {
        new AlertDialog.Builder(this).setTitle("notification").setMessage(msg)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }





    //接收线程发送过来信息，并用TextView显示
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_msg.setText(content);
        }
    };


    @Override
    public void run() {
        try {

            while (true) {

                if (!socket.isClosed()) {
                    if (socket.isConnected()) {
                        if (!socket.isInputShutdown()) {
                            char [] rec=new char[1024];
                            if (in.read(rec)>0) {

                                content =  new String(rec);
                                mHandler.sendMessage(mHandler.obtainMessage());
                            } else {

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
