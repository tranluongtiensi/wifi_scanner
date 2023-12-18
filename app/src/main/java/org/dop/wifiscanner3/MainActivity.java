package org.dop.wifiscanner3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.thanosfisherman.wifiutils.WifiUtils;

import java.util.List;


public class MainActivity extends AppCompatActivity {
    private final static int REQUEST_WIFI_CODE = 1;
    private WifiManager wifiManager;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tv);

        checkWifiPermissions();
    }

    public void checkWifiPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WIFI_CODE);
        } else {
            Toast.makeText(this, "kk", Toast.LENGTH_SHORT).show();
        }
        checkEnableWifi();
    }

    public void checkEnableWifi() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null || !wifiManager.isWifiEnabled()) {
            WifiUtils.withContext(getApplicationContext()).enableWifi(this::checkResult);
        } else {
            WifiUtils.withContext(getApplicationContext()).scanWifi(this::getScanResults).start();
        }
    }

    private void getScanResults(List<android.net.wifi.ScanResult> scanResults) {
        if (scanResults.isEmpty()) {
            Log.i("si11", "SCAN RESULTS IT'S EMPTY");
            return;
        }
        StringBuilder wifiNetworks = new StringBuilder();
        for ( android.net.wifi.ScanResult result : scanResults) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                String ssid = result.SSID;
                String bssid = result.BSSID;

                wifiNetworks.append("SSID: ").append(ssid).append("\n")
                        .append("BSSID: ").append(bssid).append("\n");
            }
            textView.setText(wifiNetworks.toString());

        }
        Log.i("si11", "GOT SCAN RESULTS " + scanResults);
    }

//    private void getScanResults(@NonNull final List<ScanResult> results) {
//        if (results.isEmpty()) {
//            Log.i("si11", "SCAN RESULTS IT'S EMPTY");
//            return;
//        }
//        StringBuilder wifiNetworks = new StringBuilder();
//        for (ScanResult result : results) {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                String ssid = result.getDevice().getName();
//                String bssid = result.getDevice().getAddress();
//
//                wifiNetworks.append("SSID: ").append(ssid).append("\n")
//                        .append("BSSID: ").append(bssid).append("\n");
//            }
//            textView.setText(wifiNetworks.toString());
//
//        }
//        Log.i("si11", "GOT SCAN RESULTS " + results);
//    }
    private void checkResult(boolean isSuccess)
    {
        if (isSuccess)
            Toast.makeText(MainActivity.this, "WIFI ENABLED", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this, "COULDN'T ENABLE WIFI", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_WIFI_CODE){
            checkEnableWifi();
        }
        else{
            Toast.makeText(this, "Permissions should be allow", Toast.LENGTH_LONG).show();
        }
    }
}