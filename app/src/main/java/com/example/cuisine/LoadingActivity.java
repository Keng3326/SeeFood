package com.example.cuisine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class LoadingActivity extends AppCompatActivity {
    Bitmap bitmap;
    String result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Bundle bundle = this.getIntent().getExtras();
        if(bundle!=null){
            bitmap = bundle.getParcelable("bitmap");
            Thread thread = new Thread(clientSocket);
            thread.start();
        }
    }

    Runnable clientSocket = new Runnable(){

        @Override
        public void run() {
            try {
                Socket socket = new Socket("路徑",8989);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ByteArrayOutputStream outPut = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outPut);
                byte[] bytes = outPut.toByteArray();
                out.write(bytes);
                out.flush();
                Thread.sleep(4000);
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
                printWriter.write("Done\r\n");
                printWriter.flush();
                while (socket.isConnected()) {
                    String tmp = in.readLine();
                    if(tmp != null){
                        result = tmp;
                        break;
                    }
                }
                printWriter.close();
                out.close();
                in.close();
                socket.close();
                goToResultActivity();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    };

    //將結果傳入RecognitionResultActivity
    public void goToResultActivity(){
        Intent intent = new Intent(LoadingActivity.this, RecognitionResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("result", result);
        bundle.putParcelable("bitmap",bitmap);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
