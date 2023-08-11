package com.example.appchat;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.Window;

// giúp lắng nghe trạng thái, action của hệ thống
public class checkNetwork extends BroadcastReceiver

{

    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public Dialog dialog = new Dialog(context);
    public AlertDialog.Builder builder = new AlertDialog.Builder(context);

    @Override
    public void onReceive(Context context, Intent intent)
    {
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.load_dialog);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setTitle("Thông báo");
        builder.setMessage("Mất kết nối");
        builder.setPositiveButton("Thử lại", (dialog, which) -> {

                if(check(context))
                {
                    this.dialog.dismiss();
                }
                else
                {
                    this.dialog.show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            builder.show();
                        }
                    },1000);
                }


        });
        builder.setNegativeButton("Thoát", (dialog, which) -> {
              dialog.dismiss();
              android.os.Process.killProcess(android.os.Process.myPid());
              System.exit(1);
        });


        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction()))
        {
            if (!check(context))
            {
                dialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        builder.show();
                    }
                },2000);
            }
            else
            {
                dialog.dismiss();
            }
        }
    }




    private boolean check (Context context)
    {
      ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      if (connectivityManager == null)
          return false;
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) // cách check android trên 6.0
      {
       Network network = connectivityManager.getActiveNetwork();
       if(network == null)
           return false;
       else
       {
           NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
           return  capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
       }

      }
      else
      {
          // cách check cho những androi dưới 6.0
          NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
          return networkInfo != null && networkInfo.isConnected();
      }
    }
}
