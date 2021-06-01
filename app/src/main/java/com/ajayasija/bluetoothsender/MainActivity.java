package com.ajayasija.bluetoothsender;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ajayasija.bluetoothsender.databinding.ActivityMainBinding;
import com.ajayasija.bluetoothsender.listener.ConnectionStatusListener;
import com.ajayasija.bluetoothsender.listener.MsgStatusListener;
import com.ajayasija.bluetoothsender.threads.AcceptThread;
import com.ajayasija.bluetoothsender.threads.TransferThread;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ConnectionStatusListener, MsgStatusListener {

    private static final int REQUEST_ENABLE_BT = 101;
    private ActivityMainBinding binding;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        initUi();
        binding.tvConnect.setOnClickListener(this);
        binding.tvSend.setOnClickListener(this);
    }

    private void initUi() {
        if (bluetoothAdapter==null){
            Toast.makeText(this,"This device does not support bluetooth",Toast.LENGTH_SHORT).show();
        }
        else {
            binding.tvBluetoothname.setText("My Bluetooth:"+bluetoothAdapter.getName());
            if (bluetoothAdapter.isEnabled()){
                binding.switchEnable.setChecked(true);
                binding.switchEnable.setText("On");
            }
        }
        binding.switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                else {
                    binding.switchEnable.setChecked(false);
                    binding.switchEnable.setText("Off");
                    bluetoothAdapter.disable();
                    binding.image.setImageResource(R.drawable.ic_bluetoothoff);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode==RESULT_CANCELED&&requestCode==REQUEST_ENABLE_BT){
            binding.switchEnable.setChecked(false);
            binding.switchEnable.setText("Off");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvConnect:
                binding.connectionStatus.setVisibility(View.VISIBLE);
                new AcceptThread(bluetoothAdapter,this).start();
                break;
            case R.id.tvSend:
                if (binding.etMsg.getText().toString().isEmpty()){
                    Toast.makeText(this, "Enter Message", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (mSocket==null){
                        Toast.makeText(this, "bluetooth not connected to any device", Toast.LENGTH_SHORT).show();
                    }else {
                        new TransferThread(mSocket,this).write(binding.etMsg.getText().toString());
                    }
                }
                break;
        }
    }

    @Override
    public void onError() {

    }

    @Override
    public void onSucces(BluetoothSocket socket) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSocket=socket;
                binding.connectionStatus.setText("Connected To:"+socket.getRemoteDevice().getName());
            }
        });
    }

    @Override
    public void onMsgSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMsgFailure() {
        Toast.makeText(MainActivity.this, "Message Sent Successfully", Toast.LENGTH_SHORT).show();
    }
}