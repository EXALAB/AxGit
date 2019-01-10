package exa.free.ag;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import exa.free.security.PermissionVerifier;

public class Method1 extends Fragment {

    Context context;
    DownloadTask downloadTask;
    Extract extract;
    PermissionVerifier permissionVerifier;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Button button;
    Button button2;
    Button button3;
    TextView textView2;
    ProgressDialog mProgressDialog;
    ProgressDialog mProgressDialog2;
    InterstitialAd mInterstitialAd;
    int version;
    String s;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.method1, container, false);

        context = getActivity().getApplicationContext();

        permissionVerifier = new PermissionVerifier(context);

        sharedPreferences = context.getSharedPreferences("GlobalPreferences", 0);

        editor = sharedPreferences.edit();

        version = sharedPreferences.getInt("Version", 0);

        button = view.findViewById(R.id.button);
        button2 = view.findViewById(R.id.button2);
        button3 = view.findViewById(R.id.button3);
        textView2 = view.findViewById(R.id.textView2);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage("Connecting...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);

        mProgressDialog2 = new ProgressDialog(getActivity());
        mProgressDialog2.setMessage("Extracting...");
        mProgressDialog2.setIndeterminate(true);
        mProgressDialog2.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog2.setCancelable(false);

        if(Build.VERSION.SDK_INT >= 21){
            s = Build.SUPPORTED_ABIS[0];
        }else{
            s = Build.CPU_ABI;
        }

        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId("ca-app-pub-5748356089815497/2595876004");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        textView2.setText("Step 2 : Copy the command to clipboard :\n\n" + "export PATH=$PATH:" + context.getFilesDir());

        int i = Integer.valueOf(getString(R.string.version));

        if(version == 0){
            button.setText("Install");
        }else if(version == i){
            button.setText("Reinstall");
            Toast.makeText(context, "Git Installed. Version : " + getString(R.string.version_string), Toast.LENGTH_LONG).show();
        }else if(version < i){
            button.setText("Update");
            Toast.makeText(context, "Git Installed. Version : " + getString(R.string.version_string) + " , Update available", Toast.LENGTH_LONG).show();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!donationInstalled() && mInterstitialAd != null && mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                }else{
                    downloadTask = new DownloadTask(context);
                    extract = new Extract(context);
                    if(s.equals("arm64-v8a")){
                        downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/arm64/git_arm64_zip");
                    }else if (s.contains("arm")){
                        downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/arm/git_arm_zip");
                    }else if(s.equals("x86")){
                        downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/i386/git_i386_zip");
                    }else if(s.equals("x86_64")){
                        downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/amd64/git_amd64_zip");
                    }else if(s.equals("mips")){
                        downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/mipsel/git_mipsel_zip");
                    }else if(s.equals("mips64")){
                        downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/mips64el/git_mips64el_zip");
                    }else{
                        Toast.makeText(context, "Sorry, your device is not supported !", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Command", "export PATH=$PATH:" + context.getFilesDir());
                clipboard.setPrimaryClip(clip);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = context.getPackageManager().getLaunchIntentForPackage("jackpal.androidterm");
                if(isPackageInstalled("jackpal.androidterm", context.getPackageManager())){
                    startActivity(intent);
                }else{
                    notifyUserForInstallTerminal();
                }
            }
        });

        if(!permissionVerifier.verifyApplication().equals("PASSED")){
            Toast.makeText(context, "Please download genuine version from play store", Toast.LENGTH_LONG).show();
            ActivityCompat.finishAffinity(getActivity());
        }

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                downloadTask.cancel(true);
            }
        });
        mProgressDialog2.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                extract.cancel(true);
            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                downloadTask = new DownloadTask(context);
                extract = new Extract(context);
                if(s.equals("arm64-v8a")){
                    downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/arm64/git_arm64_zip");
                }else if (s.contains("arm")){
                    downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/arm/git_arm_zip");
                }else if(s.equals("x86")){
                    downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/i386/git_i386_zip");
                }else if(s.equals("x86_64")){
                    downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/amd64/git_amd64_zip");
                }else if(s.equals("mips")){
                    downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/mipsel/git_mipsel_zip");
                }else if(s.equals("mips64")){
                    downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/mips64el/git_mips64el_zip");
                }else{
                    Toast.makeText(context, "Sorry, your device is not supported !", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setMessage("Downloading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected String doInBackground(String... sUrl) {
            DeleteGit();
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(context.getFilesDir() + "/git.zip");

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            if(Method1.this.isVisible()){
                mProgressDialog.dismiss();
            }
            if (result != null) {
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(context, "Download Completed !", Toast.LENGTH_SHORT).show();
                editor.putInt("Version", Integer.valueOf(getString(R.string.version)));
                editor.apply();
                new Extract(context).execute();
                if(button.getText().toString().equalsIgnoreCase("INSTALL")){
                    button.setText("REINSTALL");
                    notifyUserForInstall();
                }else if(button.getText().toString().equalsIgnoreCase("UPDATE")){
                    button.setText("REINSTALL");
                    notifyUserForUpdate();
                }else if(button.getText().toString().equalsIgnoreCase("REINSTALL")){
                    notifyUserForReinstall();
                }
            }
        }
    }
    private class Extract extends AsyncTask<Integer, Integer, Void> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;
        private int i = 0;

        public Extract(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog2.show();
            try {
                ZipFile zip = new ZipFile(context.getFilesDir() + "/git.zip");
                mProgressDialog2.setMax(zip.size());
            }catch(Exception e){
                Log.e("error", "Failed to read file");
            }
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog2.setIndeterminate(false);
            mProgressDialog2.setProgress(progress[0]);
        }

        @Override
        protected Void doInBackground(Integer... param) {
            // UNZIP YOUR FILE HERE
            try {
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(context.getFilesDir() + "/git.zip")));
                ZipEntry ze;
                int count;
                byte[] buffer = new byte[8192];
                while ((ze = zis.getNextEntry()) != null) {
                    File file = new File(context.getFilesDir(), ze.getName());
                    File dir = ze.isDirectory() ? file : file.getParentFile();
                    if (!dir.isDirectory() && !dir.mkdirs()) {
                        throw new FileNotFoundException("Failed to ensure directory: " + dir.getAbsolutePath());
                    }
                    if (ze.isDirectory())
                        continue;
                    FileOutputStream fout = new FileOutputStream(file);
                    try {
                        while ((count = zis.read(buffer)) != -1) {
                            i++;
                            publishProgress(i);
                            fout.write(buffer, 0, count);
                        }
                    } finally {
                        fout.close();
                    }
                }
                zis.close();
            }catch (Exception e){
                Log.e("Error", "Error while extracting.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mWakeLock.release();
            if(Method1.this.isVisible()){
                mProgressDialog2.dismiss();
            }
        }
    }
    private void DeleteGit() {
        try {
            DataOutputStream os = new DataOutputStream(Runtime.getRuntime().exec("sh").getOutputStream());
            os.writeBytes("rm -rf " + context.getFilesDir() + "/*");
            os.writeBytes("exit\n");
            os.flush();
        } catch (IOException e2) {
            Log.e("error", "DeleteGit failed to execute");
        }
    }
    public void notifyUserForInstall(){
        final ViewGroup nullParent = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.notify1, nullParent);
        TextView textView = view.findViewById(R.id.textView);

        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
        textView.setText("Git Installed\n\n" + "Version : " + getString(R.string.version_string));
    }
    public void notifyUserForUpdate(){
        final ViewGroup nullParent = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.notify1, nullParent);
        TextView textView = view.findViewById(R.id.textView);

        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
        textView.setText("Git Updated\n\n" + "Version : " + getString(R.string.version_string));
    }
    public void notifyUserForReinstall(){
        final ViewGroup nullParent = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.notify1, nullParent);
        TextView textView = view.findViewById(R.id.textView);

        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
        textView.setText("Git Reinstalled\n\n" + "Version : " + getString(R.string.version_string));
    }
    public void notifyUserForInstallTerminal(){
        final ViewGroup nullParent = null;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.notify1, nullParent);
        TextView textView = view.findViewById(R.id.textView);

        alertDialog.setView(view);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Uri uri = Uri.parse("market://details?id=jackpal.androidterm");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if(Build.VERSION.SDK_INT >= 21){
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                }else{
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                }
                try{
                    startActivity(intent);
                }catch(ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=jackpal.androidterm")));
                }
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
        textView.setText("Terminal Emulator for Android is not installed, do you want to install it now ?");
    }
    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    private boolean donationInstalled() {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.checkSignatures(context.getPackageName(), "exa.ag.d") == PackageManager.SIGNATURE_MATCH;
    }
}
