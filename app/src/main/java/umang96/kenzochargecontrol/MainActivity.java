package umang96.kenzochargecontrol;

import android.content.res.AssetManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    TextView out;
    EditText input;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int z;
        z=check_perm();
        if(z==0)
        root();
        addButtonClickListener1();
        addButtonClickListener2();
    }


    private void addButtonClickListener1() {
        Button b1 = (Button) findViewById(R.id.button2);
        assert b1 != null;
        b1.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {

                                      check_rate();

                                  }
                              }
        );
    }

    private void addButtonClickListener2() {
        Button b2 = (Button) findViewById(R.id.button1);
        assert b2 != null;
        b2.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {

                                      set_rate();

                                  }
                              }
        );
    }

    private int check_perm() {
        out = (TextView)findViewById(R.id.textView3);
        ShellExecutor exe = new ShellExecutor();
        String outp = exe.Executor("ls -l sys/module/qpnp_smbcharger/parameters/default_dcp_icl_ma");
        String ker=outp.substring(0,6);
        Log.d("Output", outp);
        String str="-rw-rw";
        if(str.equals(ker))
            return 1;
        else
            return 0;
    }

    private void check_rate() {
        out = (TextView)findViewById(R.id.textView3);
        ShellExecutor exe = new ShellExecutor();
        int x= check_perm();
        if(x==1) {
            String outp = exe.Executor("cat sys/module/qpnp_smbcharger/parameters/default_dcp_icl_ma");
            String ker = outp.substring(0, 4);
            out.setText(ker + "mA");
        }
        else if(x==0)
        {
            Toast.makeText(getApplicationContext(), "Wait, still setting permissions !",
                    Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), "Try after 30 seconds !",
                Toast.LENGTH_SHORT).show();
    }}

    private void set_rate() {
        check_perm();
        input = (EditText)findViewById(R.id.editText2);
        out = (TextView)findViewById(R.id.text_view);
        ShellExecutor exe = new ShellExecutor();
        //int rate= Integer.parseInt(input.getText().toString());
        String ck = input.getText().toString();
        if (input.getText().toString().trim().length() > 0)
        {
            int rate= Integer.parseInt(input.getText().toString());
            int z=check_perm();
            if(z==1) {
                if (rate > 3000 || rate < 1000) {
                    Toast.makeText(getApplicationContext(), "Failed, enter value between 1000-3000 !",
                            Toast.LENGTH_SHORT).show();
                } else {
                    String str = "echo " + rate + " > ~/sys/module/qpnp_smbcharger/parameters/default_dcp_icl_ma";
                    String strr = "echo " + rate + " > ~/sys/module/qpnp_smbcharger/parameters/default_hvdcp_icl_ma";
                    Process p = null;
                    try {
                        p = Runtime.getRuntime().exec("su");
                        p.getOutputStream().write(str.getBytes());
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Failed, do you have root access ?",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    Process q = null;
                    try {
                        q = Runtime.getRuntime().exec("su");
                        q.getOutputStream().write(strr.getBytes());
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "Failed, do you have root access ?",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), "Done, please check rate in about 30 seconds !",
                            Toast.LENGTH_LONG).show();
                }
            }

    else
        { Toast.makeText(getApplicationContext(), "Wait, still setting permissions !",
                    Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), "Try after 30 seconds !",
                Toast.LENGTH_SHORT).show();
    }}
       else {
            Toast.makeText(getApplicationContext(), "You did not enter any value !",
                    Toast.LENGTH_LONG).show();
        }}

    private void root() {

        Process p = null;
        try {
            Toast.makeText(getApplicationContext(), "Wait, setting permissions !",
                    Toast.LENGTH_LONG).show();
            p = Runtime.getRuntime().exec("su");
            p.getOutputStream().write("chmod 666 ~/sys/module/qpnp_smbcharger/parameters/default_dcp_icl_ma".getBytes());
        }  catch(IOException e){
            Toast.makeText(getApplicationContext(), "Failed, do you have root access ?",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        Process q = null;
        try {
            q = Runtime.getRuntime().exec("su");
            q.getOutputStream().write("chmod 666 ~/sys/module/qpnp_smbcharger/parameters/default_hvdcp_icl_ma".getBytes());
            Toast.makeText(getApplicationContext(), "Give me just little more time !",
                    Toast.LENGTH_LONG).show();
            Toast.makeText(getApplicationContext(), "Okay, setting permissions succesful !",
                    Toast.LENGTH_SHORT).show();
        }  catch(IOException e){
            Toast.makeText(getApplicationContext(), "Failed, do you have root access ?",
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(filename);
                File outFile = new File(getExternalFilesDir(null), filename);
                out = new FileOutputStream(outFile);
                copyFile(in, out);
            } catch(IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
            finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }
    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }
















}
