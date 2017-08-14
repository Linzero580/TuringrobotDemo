package com.linzero.turingrobotdemo;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.linzero.turingrobotdemo.bean.ChatMessage;
import com.linzero.turingrobotdemo.utils.HttpUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mMsg;
    private ChatMessageAdapter mAdapter;
    private List<ChatMessage> mDatas;

    private EditText mInputMsg;
    private Button mSendMsg;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //等待接收，子线程完成数据返回
            ChatMessage fromMessade = (ChatMessage) msg.obj;
            mDatas.add(fromMessade);
            mAdapter.notifyDataSetChanged();
            //让聊天界面一直处于最新消息的状态
            mMsg.setSelection(mMsg.getCount() - 1);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        initView();
        initDatas();

        //初始化事件
        initListener();
    }

    private void initListener() {
        mSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String toMsg = mInputMsg.getText().toString();
                if (TextUtils.isEmpty(toMsg)) {
                    Toast.makeText(MainActivity.this, "请输入文字", Toast.LENGTH_SHORT).show();
                    return;
                }

                ChatMessage toMessage = new ChatMessage();
                toMessage.setDate(new Date());
                toMessage.setMsg(toMsg);
                toMessage.setType(ChatMessage.Type.OUTCOMING);
                mDatas.add(toMessage);
                mAdapter.notifyDataSetChanged();

                mInputMsg.setText("");

                //让聊天界面一直处于最新消息的状态
                mMsg.setSelection(mMsg.getCount() - 1);

                new Thread() {
                    @Override
                    public void run() {
                        ChatMessage fromMessage = HttpUtils.sendMessag(toMsg);
                        Message m = Message.obtain();
                        m.obj = fromMessage;
                        mHandler.sendMessage(m);
                    }
                }.start();


            }
        });
    }

    private void initDatas() {
        mDatas = new ArrayList<ChatMessage>();
        mDatas.add(new ChatMessage("你好，小Z为您服务", ChatMessage.Type.INCOMING, new Date()));
//        mDatas.add(new ChatMessage("你好", ChatMessage.Type.OUTCOMING, new Date()));
        mAdapter = new ChatMessageAdapter(this, mDatas);

        mMsg.setAdapter(mAdapter);
    }

    private void initView() {

        mMsg = (ListView) findViewById(R.id.id_listview_msgs);
        mInputMsg = (EditText) findViewById(R.id.id_input_msg);
        mSendMsg = (Button) findViewById(R.id.id_send_msg);
    }
}
