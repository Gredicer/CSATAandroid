package com.aliyun.alink.devicesdk.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;



import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.aliyun.alink.dm.model.RequestModel;
import com.aliyun.alink.linkkit.api.LinkKit;

import com.aliyun.alink.linksdk.cmp.connect.channel.MqttPublishRequest;
import com.aliyun.alink.linksdk.cmp.connect.channel.MqttSubscribeRequest;
import com.aliyun.alink.linksdk.cmp.core.base.AMessage;
import com.aliyun.alink.linksdk.cmp.core.base.ARequest;
import com.aliyun.alink.linksdk.cmp.core.base.AResponse;
import com.aliyun.alink.linksdk.cmp.core.base.ConnectState;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectNotifyListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSendListener;
import com.aliyun.alink.linksdk.cmp.core.listener.IConnectSubscribeListener;
import com.aliyun.alink.linksdk.tmp.api.MapInputParams;
import com.aliyun.alink.linksdk.tmp.api.OutputParams;
import com.aliyun.alink.linksdk.tmp.device.payload.ValueWrapper;
import com.aliyun.alink.linksdk.tmp.devicemodel.Service;
import com.aliyun.alink.linksdk.tmp.listener.IPublishResourceListener;
import com.aliyun.alink.linksdk.tmp.listener.ITResRequestHandler;
import com.aliyun.alink.linksdk.tmp.listener.ITResResponseCallback;
import com.aliyun.alink.linksdk.tmp.utils.ErrorInfo;
import com.aliyun.alink.linksdk.tools.AError;
import com.aliyun.alink.linksdk.tools.ALog;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.aliyun.alink.linksdk.tmp.connect.IConnect.a;

/*
 * Copyright (c) 2014-2016 Alibaba Group. All rights reserved.
 * License-Identifier: Apache-2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

/**
 * 注意！！！！
 * 1.该示例只共快速接入使用，只适用于有 Status、Data属性的快速接入测试设备；
 * 2.真实设备可以参考 ControlPanelActivity 里面有数据上下行示例；
 */
public class LightExampleActivity extends BaseActivity {

    private final static int REPORT_MSG = 0x100;

    private final int[] checkLight=new int[256];

    TextView consoleTV;
    String consoleStr;
    private InternalHandler mHandler = new InternalHandler();

    private static String checkStatusTopic = "/" + "a1jRcr7T6Or" + "/" + "AndroidTest" + "/user/update";
    private static String checkStatusTopic1 = "/" + "a1jRcr7T6Or" + "/" + "AndroidTest" + "/user/get";
    private static String subInfo = "";


    private static int warmSendnum=0;
    private static int coldSendnum=0;

    public static String hexStr2Str(String hexStr)
    {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++)
        {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 在初始化的时候可以设置 灯的初始状态，或者等初始化完成之后 上报一次设备所有属性的状态
        // 注意在调用云端接口之前确保初始化完成了
        Log.d("LightExampleActivity","onCreate execute");
        setContentView(R.layout.activity_light_example);
        consoleTV = (TextView) findViewById(R.id.textview_console);
        setDownStreamListener();
       // showToast("已启动每5秒上报一次状态");
       // log("已启动每5秒上报一次状态");
       // mHandler.sendEmptyMessageDelayed(REPORT_MSG, 2 * 1000);

        final SeekBar warmLight=findViewById( R.id.warmLight );
        final TextView warmLightnum=findViewById( R.id.warmLight_num );
        warmLightnum.setText( "暖光：" );

        final SeekBar coldLight=findViewById( R.id.coldLight );
        final TextView coldLightnum=findViewById( R.id.coldLight_num );
        coldLightnum.setText( "冷光：" );

        warmLight.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                warmSendnum= progress;
                String str="暖光："+String.valueOf( progress);
                warmLightnum.setText( str );

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                String LEDorder_a="@CATZK00000000LEC01Z";
                String LEDorder_b="";
                for (int i=0;i<256;i++){
                    LEDorder_b=LEDorder_b+checkLight[i];
                }

                LEDorder_b=binaryString2hexString(LEDorder_b).toUpperCase();
                String LEDorder_c="BV";
                String LEDorder_d=Integer.toHexString(warmSendnum).toUpperCase();
                if(warmSendnum<16) LEDorder_d="0"+LEDorder_d;
                String LEDorder_e=Integer.toHexString(coldSendnum).toUpperCase();
                if(coldSendnum<16) LEDorder_e="0"+LEDorder_e;
                String LEDorder_f="@END";
                String SendLightdata=LEDorder_a+LEDorder_b+LEDorder_c+LEDorder_d+LEDorder_e+LEDorder_f;
                publish( checkStatusTopic,"{\"data\":\""+SendLightdata+"\"}" );


            }

        });

        coldLight.setOnSeekBarChangeListener( new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                coldSendnum= progress;
                String str="冷光："+String.valueOf( progress);
                coldLightnum.setText( str );

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                String LEDorder_a="@CATZK00000000LEC01Z";
                String LEDorder_b="";
                for (int i=0;i<256;i++){
                    LEDorder_b=LEDorder_b+checkLight[i];
                }
                LEDorder_b=binaryString2hexString(LEDorder_b).toUpperCase();
                String LEDorder_c="BV";
                String LEDorder_d=Integer.toHexString(warmSendnum).toUpperCase();
                if(warmSendnum<16) LEDorder_d="0"+LEDorder_d;
                String LEDorder_e=Integer.toHexString(coldSendnum).toUpperCase();
                if(coldSendnum<16) LEDorder_e="0"+LEDorder_e;
                String LEDorder_f="@END";
                String SendLightdata=LEDorder_a+LEDorder_b+LEDorder_c+LEDorder_d+LEDorder_e+LEDorder_f;

                publish( checkStatusTopic,"{\"data\":\""+SendLightdata+"\"}" );

            }

        });



        final Button button= (Button)findViewById(R.id.button_1);
        final Button button1=(Button)findViewById( R.id.button_2 );
        final Button button2= (Button)findViewById(R.id.button_3);
        final Button button3=(Button)findViewById( R.id.button_4 );
        final Button button4= (Button)findViewById(R.id.button_5);
        final Button button5=(Button)findViewById( R.id.button_6 );
        final Button button6= (Button)findViewById(R.id.button_7);
        final Button button7=(Button)findViewById( R.id.button_8);
        final Button button8= (Button)findViewById(R.id.button_9);
        final Button button9=(Button)findViewById( R.id.button_10);
        final Button button10=(Button)findViewById( R.id.button_11);
        for (int i=0;i<11;i++){
            checkLight[i]=0;
        }






        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // setDownStreamListener();
                //reportHelloWorld();

                if(checkLight[0]==0)
                {

                    button.setBackground( getResources().getDrawable(R.drawable.light));
                    checkLight[0]=1;
                }
                else if(checkLight[0]==1)
                {

                    button.setBackground( getResources().getDrawable(R.drawable.down));
                    checkLight[0]=0;
                }



            }
        });
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setDownStreamListener();
                //reportHelloWorld();
                if(checkLight[1]==0)
                {
                    button1.setBackground( getResources().getDrawable(R.drawable.light));
                    checkLight[1]=1;
                }
                else if(checkLight[1]==1)
                {
                    button1.setBackground( getResources().getDrawable(R.drawable.down));
                    checkLight[1]=0;
                }

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setDownStreamListener();
                //reportHelloWorld();
                if(checkLight[2]==0)
                {
                    button2.setBackground( getResources().getDrawable(R.drawable.light));
                    checkLight[2]=1;
                }
                else if(checkLight[2]==1)
                {
                    button2.setBackground( getResources().getDrawable(R.drawable.down));
                    checkLight[2]=0;
                }

            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setDownStreamListener();
                //reportHelloWorld();
                if(checkLight[3]==0)
                {
                    button3.setBackground( getResources().getDrawable(R.drawable.light));
                    checkLight[3]=1;
                }
                else if(checkLight[3]==1)
                {
                    button3.setBackground( getResources().getDrawable(R.drawable.down));
                    checkLight[3]=0;
                }

            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setDownStreamListener();
                //reportHelloWorld();
                if(checkLight[4]==0)
                {
                    button4.setBackground( getResources().getDrawable(R.drawable.light));
                    checkLight[4]=1;
                }
                else if(checkLight[4]==1)
                {
                    button4.setBackground( getResources().getDrawable(R.drawable.down));
                    checkLight[4]=0;
                }

            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setDownStreamListener();
                //reportHelloWorld();
                if(checkLight[5]==0)
                {
                    button5.setBackground( getResources().getDrawable(R.drawable.light));
                    checkLight[5]=1;
                }
                else if(checkLight[5]==1)
                {
                    button5.setBackground( getResources().getDrawable(R.drawable.down));
                    checkLight[5]=0;
                }

            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setDownStreamListener();
                //reportHelloWorld();
                if(checkLight[6]==0)
                {
                    button6.setBackground( getResources().getDrawable(R.drawable.light));
                    checkLight[6]=1;
                }
                else if(checkLight[6]==1)
                {
                    button6.setBackground( getResources().getDrawable(R.drawable.down));
                    checkLight[6]=0;
                }

            }
        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setDownStreamListener();
                //reportHelloWorld();
                if(checkLight[7]==0)
                {
                    button7.setBackground( getResources().getDrawable(R.drawable.light));
                    checkLight[7]=1;
                }
                else if(checkLight[7]==1)
                {
                    button7.setBackground( getResources().getDrawable(R.drawable.down));
                    checkLight[7]=0;
                }

            }
        });
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setDownStreamListener();
                //reportHelloWorld();
                if(checkLight[8]==0)
                {
                    button8.setBackground( getResources().getDrawable(R.drawable.light));
                    checkLight[8]=1;
                }
                else if(checkLight[8]==1)
                {
                    button8.setBackground( getResources().getDrawable(R.drawable.down));
                    checkLight[8]=0;
                }

            }
        });
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setDownStreamListener();
                //reportHelloWorld();
                if(checkLight[9]==0)
                {
                    button9.setBackground( getResources().getDrawable(R.drawable.light));
                    checkLight[9]=1;
                }
                else if(checkLight[9]==1)
                {
                    button9.setBackground( getResources().getDrawable(R.drawable.down));
                    checkLight[9]=0;
                }

            }
        });

        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setDownStreamListener();
                //reportHelloWorld();
                if(checkLight[10]==0)
                {
                    button10.setBackground( getResources().getDrawable(R.drawable.light));
                    checkLight[10]=1;
                }
                else if(checkLight[10]==1)
                {
                    char i=110;



                    button10.setBackground( getResources().getDrawable(R.drawable.down));
                    checkLight[10]=0;
                }

            }
        });
    }

    public static String binaryString2hexString(String binaryString) {
        if (TextUtils.isEmpty(binaryString) || binaryString.length() % 4 != 0) {
            return null;
        }
        String hexString = "";
        int bit = 0;
        for (int i = 0; i < binaryString.length(); i += 4) {
            bit = 0;
            for (int j = 0; j < 4; j++) {
                String x = binaryString.substring(i + j, i + j + 1);
                bit += Integer.parseInt(x) << (4 - j - 1);
            }
            hexString += Integer.toHexString(bit);
        }
        return hexString.toString();
    }





    public void publish(String topic, String payload) {
        MqttPublishRequest request = new MqttPublishRequest();
        request.topic = topic;
        request.payloadObj = payload;
        request.qos = 0;
        LinkKit.getInstance().publish(request, new IConnectSendListener() {
            @Override
            public void onResponse(ARequest aRequest, AResponse aResponse) {
            }

            @Override
            public void onFailure(ARequest aRequest, AError aError) {
            }
        });
    }

    public void subscribe(String topic) {
        MqttSubscribeRequest request = new MqttSubscribeRequest();
        request.topic = topic;
        LinkKit.getInstance().subscribe(request, new IConnectSubscribeListener() {
            @Override
            public void onSuccess() {
                showToast( "接受成功！" );
            }

            @Override
            public void onFailure(AError aError) {
            }
        });
    }









    /**
     * 数据上行
     * 上报灯的状态
     */
    public void reportHelloWorld() {
        log("上报 Hello, World！");
        try {
            Map<String, ValueWrapper> reportData = new HashMap<>();
            reportData.put("Status", new ValueWrapper.BooleanValueWrapper(1)); // 1开 0 关
            reportData.put("Data", new ValueWrapper.StringValueWrapper("Hello World!")); //
            LinkKit.getInstance().getDeviceThing().thingPropertyPost(reportData, new IPublishResourceListener() {
                @Override
                public void onSuccess(String s, Object o) {
                    Log.d(TAG, "onSuccess() called with: s = [" + s + "], o = [" + o + "]");
                    showToast("设备上报状态成功");
                    log("上报 Hello, World! 成功。");
                }

                @Override
                public void onError(String s, AError aError) {
                    Log.d(TAG, "onError() called with: s = [" + s + "], aError = [" + aError + "]");
                    showToast("设备上报状态失败");
                    log("上报 Hello, World! 失败。");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDownStreamListener(){
        LinkKit.getInstance().registerOnPushListener(notifyListener);
    }

    private IConnectNotifyListener notifyListener = new IConnectNotifyListener() {
        @Override
        public void onNotify(String s, String s1, AMessage aMessage) {
            try {
                if (s1 != null && s1.contains("service/property/set")) {
                    String result = new String((byte[]) aMessage.data, "UTF-8");
                    RequestModel<String> receiveObj = JSONObject.parseObject(result, new TypeReference<RequestModel<String>>() {
                    }.getType());
                    log("Received a message: " + (receiveObj==null?"":receiveObj.params));
                    Toast ts=Toast.makeText(LightExampleActivity.this,receiveObj.params,Toast.LENGTH_LONG);
                    ts.show();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public boolean shouldHandle(String s, String s1) {
            Log.d(TAG, "shouldHandle() called with: s = [" + s + "], s1 = [" + s1 + "]");
            return true;
        }

        @Override
        public void onConnectStateChange(String s, ConnectState connectState) {
            Log.d(TAG, "onConnectStateChange() called with: s = [" + s + "], connectState = [" + connectState + "]");
        }
    };

    private void log(final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ALog.d(TAG, "log(), " + str);
                if (TextUtils.isEmpty(str))
                    return;
                consoleStr = consoleStr + "\n \n" + (getTime()) + " " + str;
                consoleTV.setText(consoleStr);
            }
        });

    }

    private void clearMsg() {
        consoleStr = "";
        consoleTV.setText(consoleStr);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mHandler != null) {
            mHandler.removeMessages(REPORT_MSG);
            mHandler.removeCallbacksAndMessages(null);
            showToast("停止定时上报");
        }
        LinkKit.getInstance().unRegisterOnPushListener(notifyListener);
        clearMsg();
    }

    private class InternalHandler extends Handler {
        public InternalHandler() {
            super(Looper.getMainLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg == null) {
                return;
            }
            int what = msg.what;
            switch (what) {
                case REPORT_MSG:
                    reportHelloWorld();
                    mHandler.sendEmptyMessageDelayed(REPORT_MSG, 5*1000);
                    break;
            }

        }
    }

}
