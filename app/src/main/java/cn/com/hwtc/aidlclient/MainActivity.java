package cn.com.hwtc.aidlclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Pattern;

import cn.com.hwtc.aidlserver.CalculateInterface;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AidlClient " + MainActivity.class.getSimpleName();
    private EditText etNumOne = null;
    private EditText etNumTwo = null;
    private TextView calculateResult = null;
    private CalculateInterface calculateInterface = null;
    private boolean bindService = false;
    private Context mContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (mContext == null) {
            mContext = getApplicationContext();
        }
        TextView tvBindRemoteService = findViewById(R.id.tv_bind_remote_service);
        TextView tvUnbindRemoteService = findViewById(R.id.tv_unbind_remote_service);
        etNumOne = findViewById(R.id.et_num_one);
        etNumTwo = findViewById(R.id.et_num_two);
        calculateResult = findViewById(R.id.calculate_result);
        TextView tvCalculateAdd = findViewById(R.id.tv_calculate_add);
        TextView tvCalculateSubtract = findViewById(R.id.tv_calculate_subtract);
        TextView tvCalculateMultiply = findViewById(R.id.tv_calculate_multiply);
        tvBindRemoteService.setOnClickListener(this);
        tvUnbindRemoteService.setOnClickListener(this);
        tvCalculateAdd.setOnClickListener(this);
        tvCalculateSubtract.setOnClickListener(this);
        tvCalculateMultiply.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_bind_remote_service:
                Intent intent = createExplicitIntent(new Intent("cn.com.hwtc.aidlserver.CalculateService"));
                if (intent != null) {
                    if (!bindService) {
                        bindService = bindService(intent, mConnection, BIND_AUTO_CREATE);
                        Log.d(TAG, "bindService:" + bindService);
                    }
                }
                break;
            case R.id.tv_unbind_remote_service:
                if (bindService) {
                    unbindService(mConnection);
                    bindService = false;
                    calculateInterface = null;
                    Log.d(TAG, "bindService:" + false);
                }
                break;
            case R.id.tv_calculate_add:
                if (null == calculateInterface) {
                    Toast.makeText(mContext, "请先绑定远程服务", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (etNumOne.getText().toString().trim().equals("") || etNumTwo.getText().toString().trim().equals("") || isNotNumeric(etNumOne.getText().toString().trim()) || isNotNumeric(etNumTwo.getText().toString().trim())) {
                        Toast.makeText(mContext, "请输入数字", Toast.LENGTH_SHORT).show();
                    } else {
                        int i = calculateInterface.doCalculateAdd(Integer.parseInt(etNumOne.getText().toString()), Integer.parseInt(etNumTwo.getText().toString()));
                        calculateResult.setText(String.valueOf("计算结果:" + i));
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_calculate_subtract:
                if (null == calculateInterface) {
                    Toast.makeText(mContext, "请先绑定远程服务", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (etNumOne.getText().toString().trim().equals("") || etNumTwo.getText().toString().trim().equals("") || isNotNumeric(etNumOne.getText().toString().trim()) || isNotNumeric(etNumTwo.getText().toString().trim())) {
                        Toast.makeText(mContext, "请输入数字", Toast.LENGTH_SHORT).show();
                    } else {
                        int i = calculateInterface.doCalculateSubtract(Integer.parseInt(etNumOne.getText().toString()), Integer.parseInt(etNumTwo.getText().toString()));
                        calculateResult.setText(String.valueOf("计算结果:" + i));
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_calculate_multiply:
                if (null == calculateInterface) {
                    Toast.makeText(mContext, "请先绑定远程服务", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (etNumOne.getText().toString().trim().equals("") || etNumTwo.getText().toString().trim().equals("") || isNotNumeric(etNumOne.getText().toString().trim()) || isNotNumeric(etNumTwo.getText().toString().trim())) {
                        Toast.makeText(mContext, "请输入数字", Toast.LENGTH_SHORT).show();
                    } else {
                        int i = calculateInterface.doCalculateMultiply(Integer.parseInt(etNumOne.getText().toString()), Integer.parseInt(etNumTwo.getText().toString()));
                        calculateResult.setText(String.valueOf("计算结果:" + i));
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    private Intent createExplicitIntent(Intent intent) {
        PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(intent, 0);
        if (resolveInfo != null && resolveInfo.size() == 1) {
            ResolveInfo serviceInfo = resolveInfo.get(0);
            String packageName = serviceInfo.serviceInfo.packageName;
            String className = serviceInfo.serviceInfo.name;
            ComponentName component = new ComponentName(packageName, className);
            Intent explicitIntent = new Intent(intent);
            explicitIntent.setComponent(component);
            return explicitIntent;
        } else {
            return null;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "ServiceConnection onServiceConnected");
            calculateInterface = CalculateInterface.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            calculateInterface = null;
            Log.d(TAG, "ServiceConnection onServiceDisconnected");
        }
    };

    /**
     * 判断字符串是否全由数字组成
     *
     * @param str 字符串源
     * @return 返回isNumeric布尔值
     */
    public boolean isNotNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return !pattern.matcher(str).matches();
    }

}
