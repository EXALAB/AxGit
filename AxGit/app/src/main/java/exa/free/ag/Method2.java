package exa.free.ag;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import exa.free.interfaces.AppSelector;

public class Method2 extends Fragment implements AppSelector {

    Context context;
    List<ApplicationAdapterListItem> applicationAdapterListItems;
    TerminalChooserAdapter terminalChooserAdapter;
    DownloadTask downloadTask;
    SharedPreferences sharedPreferences;
    ListView listView;
    AlertDialog.Builder alertDialog;
    AlertDialog alert;
    Button button;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    TextView textView3;
    TextView textView5;
    ProgressDialog mProgressDialog;
    ProgressDialog mProgressDialog2;
    InterstitialAd mInterstitialAd;
    String s;
    String s2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.method2, container, false);

        context = getActivity().getApplicationContext();

        sharedPreferences = context.getSharedPreferences("GlobalPreferences", 0);
        s = sharedPreferences.getString("ChoosenTerminal", "None");

        button = view.findViewById(R.id.button);
        button2 = view.findViewById(R.id.button2);
        button3 = view.findViewById(R.id.button3);
        button4 = view.findViewById(R.id.button4);
        button5 = view.findViewById(R.id.button5);
        textView3 = view.findViewById(R.id.textView3);
        textView5 = view.findViewById(R.id.textView5);

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

        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId("ca-app-pub-5748356089815497/1385797738");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        if(Build.VERSION.SDK_INT >= 21){
            s2 = Build.SUPPORTED_ABIS[0];
        }else{
            s2 = Build.CPU_ABI;
        }

        if(s.equals("None")){
            textView3.setText("Step 3 : Please Choose a Terminal Emulator App first");
            textView5.setText("Step 5 : Please Choose a Terminal Emulator App first");
            button3.setEnabled(false);
            button5.setEnabled(false);
        }else{
            if(s2.equals("arm64-v8a")){
                textView3.setText("Step 3 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && && chmod 755 *");
                textView5.setText("Step 5 : Copy the command to clipboard :\n\n" + "cd /data/data" + s);
            }else if (s2.contains("arm")){
                textView3.setText("Step 3 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && && chmod 755 *");
                textView5.setText("Step 5 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s);
            }else if(s2.equals("x86")){
                textView3.setText("Step 3 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && && chmod 755 *");
                textView5.setText("Step 5 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s);
            }else if(s2.equals("x86_64")){
                textView3.setText("Step 3 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && && chmod 755 *");
                textView5.setText("Step 5 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s);
            }else if(s2.equals("mips")){
                textView3.setText("Step 3 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && && chmod 755 *");
                textView5.setText("Step 5 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s);
            }else if(s2.equals("mips64")){
                textView3.setText("Step 3 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && chmod 755 *");
                textView5.setText("Step 5 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadTask = new DownloadTask(context);
                if(s2.equals("arm64-v8a")){
                    downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/arm64/git_arm64_zip");
                }else if (s2.contains("arm")){
                    downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/arm/git_arm_zip");
                }else if(s2.equals("x86")){
                    downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/i386/git_i386_zip");
                }else if(s2.equals("x86_64")){
                    downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/amd64/git_amd64_zip");
                }else if(s2.equals("mips")){
                    downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/mipsel/git_mipsel_zip");
                }else if(s2.equals("mips64")){
                    downloadTask.execute("https://raw.githubusercontent.com/EXALAB/AxGit/master/Archive/mips64el/git_mips64el_zip");
                }else{
                    Toast.makeText(context, "Sorry, your device is not supported !", Toast.LENGTH_LONG).show();
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAppsDialog();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                if(!donationInstalled() && mInterstitialAd != null && mInterstitialAd.isLoaded()){
                    mInterstitialAd.show();
                }else{
                    if(s2.equals("arm64-v8a")){
                        ClipData clip = ClipData.newPlainText("Command", "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && chmod 755 *");
                        clipboard.setPrimaryClip(clip);
                    }else if (s2.contains("arm")){
                        ClipData clip = ClipData.newPlainText("Command", "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && chmod 755 *");
                        clipboard.setPrimaryClip(clip);
                    }else if(s2.equals("x86")){
                        ClipData clip = ClipData.newPlainText("Command", "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && chmod 755 *");
                        clipboard.setPrimaryClip(clip);
                    }else if(s2.equals("x86_64")){
                        ClipData clip = ClipData.newPlainText("Command", "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && chmod 755 *");
                        clipboard.setPrimaryClip(clip);
                    }else if(s2.equals("mips")){
                        ClipData clip = ClipData.newPlainText("Command", "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && chmod 755 *");
                        clipboard.setPrimaryClip(clip);
                    }else if(s2.equals("mips64")){
                        ClipData clip = ClipData.newPlainText("Command", "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && chmod 755 *");
                        clipboard.setPrimaryClip(clip);
                    }
                }
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s = sharedPreferences.getString("ChoosenTerminal", "None");
                Intent intent = context.getPackageManager().getLaunchIntentForPackage(s);
                if(isPackageInstalled(s, context.getPackageManager())){
                    startActivity(intent);
                }else{
                    Toast.makeText(context, "Oops, looks like the application has been uninstalled or hidden, please reinstall/enable it, or choose another Terminal Emulator App", Toast.LENGTH_LONG).show();
                }
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                if(s2.equals("arm64-v8a")){
                    ClipData clip = ClipData.newPlainText("Command", "cd /data/data/" + s);
                    clipboard.setPrimaryClip(clip);
                }else if (s2.contains("arm")){
                    ClipData clip = ClipData.newPlainText("Command", "cd /data/data/" + s);
                    clipboard.setPrimaryClip(clip);
                }else if(s2.equals("x86")){
                    ClipData clip = ClipData.newPlainText("Command", "cd /data/data/" + s);
                    clipboard.setPrimaryClip(clip);
                }else if(s2.equals("x86_64")){
                    ClipData clip = ClipData.newPlainText("Command", "cd /data/data/" + s);
                    clipboard.setPrimaryClip(clip);
                }else if(s2.equals("mips")){
                    ClipData clip = ClipData.newPlainText("Command", "cd /data/data/" + s);
                    clipboard.setPrimaryClip(clip);
                }else if(s2.equals("mips64")){
                    ClipData clip = ClipData.newPlainText("Command", "cd /data/data/" + s);
                    clipboard.setPrimaryClip(clip);
                }
            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

        return view;
    }
    @Override
    public void selectApp(final String packageName){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ChoosenTerminal", packageName);
        editor.apply();
        s = sharedPreferences.getString("ChoosenTerminal", "None");
        if(s2.equals("arm64-v8a")){
            textView3.setText("Step 3 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && && chmod 755 *");
            textView5.setText("Step 5 : Copy the command to clipboard :\n\n" + "cd /data/data" + s);
        }else if (s2.contains("arm")){
            textView3.setText("Step 3 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && && chmod 755 *");
            textView5.setText("Step 5 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s);
        }else if(s2.equals("x86")){
            textView3.setText("Step 3 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && && chmod 755 *");
            textView5.setText("Step 5 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s);
        }else if(s2.equals("x86_64")){
            textView3.setText("Step 3 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && && chmod 755 *");
            textView5.setText("Step 5 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s);
        }else if(s2.equals("mips")){
            textView3.setText("Step 3 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && && chmod 755 *");
            textView5.setText("Step 5 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s);
        }else if(s2.equals("mips64")){
            textView3.setText("Step 3 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s + " && mv " + context.getExternalFilesDir(null) + "/* " + "/data/data/" + s + " && chmod 755 *");
            textView5.setText("Step 5 : Copy the command to clipboard :\n\n" + "cd /data/data/" + s);
        }
        button3.setEnabled(true);
        button5.setEnabled(true);
        alert.dismiss();
    }
    @Override
    public void removeApp(String packageName){
    }
    @Override
    public boolean isSelected(String packageName){
        return false;
    }
    public void showAppsDialog(){
        final ViewGroup nullParent = null;
        alertDialog = new AlertDialog.Builder(getActivity());
        alert = alertDialog.create();
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View view = layoutInflater.inflate(R.layout.notify2, nullParent);
        listView = view.findViewById(R.id.listView);

        alert.setView(view);
        alert.show();
        new InitializeApps().execute();
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
                output = new FileOutputStream( context.getExternalFilesDir(null) + "/git.zip");

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
            if(Method2.this.isVisible()){
                mProgressDialog.dismiss();
            }
            if (result != null) {
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(context, "Download Completed !", Toast.LENGTH_SHORT).show();
                new Extract(context).execute();
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
                ZipFile zip = new ZipFile(context.getExternalFilesDir(null) + "/git.zip");
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
                ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(context.getExternalFilesDir(null) + "/git.zip")));
                ZipEntry ze;
                int count;
                byte[] buffer = new byte[8192];
                while ((ze = zis.getNextEntry()) != null) {
                    File file = new File(context.getExternalFilesDir(null), ze.getName());
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
                DeleteGit();
            }catch (Exception e){
                Log.e("Error", "Error while extracting.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mWakeLock.release();
            if(Method2.this.isVisible()){
                mProgressDialog2.dismiss();
            }
        }
    }
    private class InitializeApps extends AsyncTask<Void, Void, Void> {
        final ViewGroup nullParent = null;
        private AlertDialog.Builder builder;
        private AlertDialog alertDialog;
        private ProgressDialog dialog;
        private boolean shouldShowSystemApps;

        private InitializeApps() {
            this.builder = null;
            this.alertDialog = null;
            this.dialog = null;
        }

        protected void onPreExecute() {
            if(Build.VERSION.SDK_INT >= 26){
                this.builder = new AlertDialog.Builder(getActivity());
                this.alertDialog = builder.create();
                LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
                View view = layoutInflater.inflate(R.layout.progress_bar, nullParent);
                this.alertDialog.setView(view);
                this.alertDialog.setCancelable(false);
                this.alertDialog.show();
                TextView textView = view.findViewById(R.id.textView);
                textView.setText("Working");
                shouldShowSystemApps = sharedPreferences.getBoolean("ShouldShowSystemApps", false);
            }else{
                this.dialog = new ProgressDialog(getActivity());
                this.dialog.setMessage("Working");
                this.dialog.setIndeterminate(true);
                this.dialog.setCancelable(false);
                this.dialog.show();
            }
        }
        protected Void doInBackground(Void... params) {
            if(shouldShowSystemApps){
                List<ApplicationAdapterListItem> applicationAdapterListItems = getAllApps();
                terminalChooserAdapter = new TerminalChooserAdapter(context, Method2.this, applicationAdapterListItems);
            }else{
                List<ApplicationAdapterListItem> applicationAdapterListItems = getUserApps();
                terminalChooserAdapter = new TerminalChooserAdapter(context, Method2.this, applicationAdapterListItems);
            }
            return null;
        }
        protected void onPostExecute(Void result) {
            if(Method2.this.isVisible()){
                if(Build.VERSION.SDK_INT >= 26){
                    this.alertDialog.dismiss();
                }else{
                    this.dialog.dismiss();
                }
                listView.setAdapter(terminalChooserAdapter);
            }
        }
    }
    private List<ApplicationAdapterListItem> getUserApps(){
        applicationAdapterListItems = new ArrayList<>();
        List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(0);
        final PackageItemInfo.DisplayNameComparator comparator = new PackageItemInfo.DisplayNameComparator(context.getPackageManager());
        Collections.sort(packageInfos, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo one, PackageInfo two) {
                return comparator.compare(one.applicationInfo, two.applicationInfo);
            }
        });
        for(int i = 0; i < packageInfos.size(); i++){
            PackageInfo packageInfo = packageInfos.get(i);
            if(!isSystemPackage(packageInfo)){
                if(isApplicationExistOnLauncher(packageInfo.applicationInfo.packageName)){
                    String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                    String packageName = packageInfo.applicationInfo.packageName;
                    Drawable icon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
                    applicationAdapterListItems.add(new ApplicationAdapterListItem(appName, packageName, icon));
                }
            }
        }
        return applicationAdapterListItems;
    }
    private List<ApplicationAdapterListItem> getAllApps(){
        applicationAdapterListItems = new ArrayList<>();
        List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(0);
        final PackageItemInfo.DisplayNameComparator comparator = new PackageItemInfo.DisplayNameComparator(context.getPackageManager());
        Collections.sort(packageInfos, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo one, PackageInfo two) {
                return comparator.compare(one.applicationInfo, two.applicationInfo);
            }
        });
        for(int i = 0; i < packageInfos.size(); i++){
            PackageInfo packageInfo = packageInfos.get(i);
            if(isApplicationExistOnLauncher(packageInfo.applicationInfo.packageName)){
                String appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                String packageName = packageInfo.applicationInfo.packageName;
                Drawable icon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
                applicationAdapterListItems.add(new ApplicationAdapterListItem(appName, packageName, icon));
            }
        }
        return applicationAdapterListItems;
    }
    private boolean isSystemPackage(PackageInfo packageInfo) {
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
    private boolean isApplicationExistOnLauncher(String packageName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resolveInfos) {
            if(packageName.equals(resolveInfo.activityInfo.packageName)){
                return true;
            }
        }
        return false;
    }
    private void DeleteGit() {
        File file = new File(context.getExternalFilesDir(null) + "/git.zip");
        if(file.exists() && file.isFile()){
            file.delete();
        }
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
