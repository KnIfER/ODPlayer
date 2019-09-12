package com.knziha.ODPlayer;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;


/**
 * Created by KnIfER on 19-05-03.
 */
public class Toastable_Activity extends AppCompatActivity {
	public boolean systemIntialized;
	protected String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};

	protected int DockerMarginL;
	protected int DockerMarginR;
	protected int DockerMarginT;
	protected int DockerMarginB;
	protected ViewGroup root;

	public VICMainAppOptions opt;
	public DisplayMetrics dm;
	public LayoutInflater inflater;
	public InputMethodManager imm;
	protected int trialCount=-1;

	long FFStamp;
	long SFStamp;
	protected int AppBlack;
	protected int AppWhite;

	protected Dialog d;
	protected View dv;
	private boolean bUseExternalFolders=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		opt = new VICMainAppOptions(getApplicationContext());
		opt.dm = dm = getResources().getDisplayMetrics();
		//getWindowManager().getDefaultDisplay().getMetrics(dm);
		super.onCreate(savedInstanceState);
		FFStamp=opt.getFirstFlag();
		SFStamp=opt.getSecondFlag();
		opt.getThirdFlag();
		inflater=getLayoutInflater();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		if(opt.getUseCustomCrashCatcher()){
			CrashHandler.getInstance(opt).register(getApplicationContext());
		}


//	   AppBlack=opt.getInDarkMode()?Color.WHITE:Color.BLACK;
//	   AppWhite=opt.getInDarkMode()?Color.BLACK:Color.WHITE;

	}



	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public File getDatabasePath(String pathname){
		return new File(pathname);
	}

	protected void checkLog(Bundle savedInstanceState){
		boolean[] launching=new boolean[]{false};
		if(DoesActivityCheckLog()){
			if(opt.getLogToFile()){
				try {
					File log=new File(CrashHandler.getInstance(opt).getLogFile());
					File lock;
					if(log.exists()){
						if((lock=new File(log.getParentFile(),"lock")).exists()){
							byte[] buffer = new byte[Math.min((int) log.length(), 4096)];
							int len = new FileInputStream(log).read(buffer);
							String message=new String(buffer,0,len);
							launching[0]=true;
							Dialog d = new androidx.appcompat.app.AlertDialog.Builder(this)
									.setMessage(message)
									.setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
										lock.delete();
										dialog.dismiss();
										checkLaunch(savedInstanceState);
									})
									.setTitle("检测到异常捕获。（如发现仍不能启动，可尝试重新初始化）")
									.setCancelable(false)
									.show();
							//.create();
							((TextView)d.findViewById(R.id.alertTitle)).setSingleLine(false);
							((TextView)d.findViewById(android.R.id.message)).setTextIsSelectable(true);
							//FilePickerDialog.stylize_simple_message_dialog(d, getApplicationContext());
						}
					}
				} catch (Exception e) { CMN.Log(e); }finally {
					if(!launching[0])
						checkLaunch(savedInstanceState);
				}
			}else{
				checkLaunch(savedInstanceState);
			}
		}else{
			File lock;
			File log=new File(CrashHandler.getInstance(opt).getLogFile());
			if((lock=new File(log.getParentFile(),"lock")).exists())
				lock.delete();
		}
	}

	protected boolean DoesActivityCheckLog() {
		return true;
	}

	protected void checkLaunch(Bundle savedInstanceState) {
		onLaunch(savedInstanceState);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//大于 23 时
			if (checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED) {
				File trialPath = getExternalFilesDir("Trial");
				boolean b1=trialPath.exists()&&trialPath.isDirectory();
				if(b1) {
					File[] fs = trialPath.listFiles();
					for(File mf:fs) {
						if(!mf.isDirectory()) {
							String fn=mf.getName();
							if(!fn.contains("."))
								try {
									trialCount=Integer.valueOf(fn);
									break;
								}catch(NumberFormatException ignored){}
						}
					}
				}
				if(trialCount>=2) {
					opt.rootPath=getExternalFilesDir("").getAbsolutePath();
					//showX(R.string.trialing, Toast.LENGTH_SHORT);
					AskExternalPermissionSnack(null, R.string.trialing);
					pre_further_loading(savedInstanceState);
					return;
				}
				showDialogTipUserRequestPermission();
			}else {pre_further_loading(savedInstanceState);}
		}else {pre_further_loading(savedInstanceState);}
	}

	protected void onLaunch(Bundle savedInstanceState) {

	}

	// 动态获取权限
	@RequiresApi(api = Build.VERSION_CODES.M)
	protected void showDialogTipUserRequestPermission() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.stg_require)
				.setMessage(R.string.stg_statement)
				.setPositiveButton(R.string.stg_grantnow, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						requestPermissions(permissions, 321);
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						File trialPath = getExternalFilesDir("Trial");
						int trialCount=-1;
						boolean b1=trialPath.exists()&&trialPath.isDirectory();
						if(b1) {
							File[] fs = trialPath.listFiles();
							if(fs!=null) for(File mf:fs) {
								if(!mf.isDirectory()) {
									String fn=mf.getName();
									if(!fn.contains("."))
										try {
											trialCount=Integer.valueOf(fn);
											break;
										}catch(NumberFormatException ignored){}
								}
							}
						}
						if(b1 || trialPath.mkdirs()) {
							opt.rootPath=getExternalFilesDir("").getAbsolutePath();
							try {
								if(trialCount!=-1) {
									new File(trialPath,String.valueOf(trialCount))
											.renameTo(new File(trialPath,String.valueOf(++trialCount)));
								}else
									new File(trialPath,String.valueOf(trialCount=0)).createNewFile();
							} catch (IOException ignored) {}
							pre_further_loading(null);
							showT(getResources().getString(R.string.trialin)+trialCount);
						}
						else {
							Toast.makeText(Toastable_Activity.this, R.string.stgerr_fail, Toast.LENGTH_SHORT).show();
							finish();
						}
					}
				}).setCancelable(false).show().getWindow().setBackgroundDrawableResource(R.drawable.popup_shadow_l);
	}


	protected void scanSettings() {

	}

	protected void AskExternalPermissionSnack(View snv, int id){};

	protected void pre_further_loading(final Bundle savedInstanceState) {
		CMN.Log(opt.rootPath, "pre_further_loading");
		if(opt.rootPath==null){
			File trialPath = getExternalFilesDir("Trial");
			if(trialPath.exists()) {
				File[] fs = trialPath.listFiles();
				if(fs!=null) for(File mf:fs) mf.delete();
				trialPath.delete();
			}
			opt.rootPath=getExternalFilesDir("").getAbsolutePath();
		}
		further_loading(savedInstanceState);
		return;
	}

	protected void further_loading(Bundle savedInstanceState) {
		scanSettings();
	}

	Toast m_currentToast;
	TextView toastTv;
	View toastV;
	public void showX(int ResId,int len, Object...args) {
		showT(getResources().getString(ResId,args),len);
	}
	public void show(int ResId,Object...args) {
		showT(getResources().getString(ResId,args),Toast.LENGTH_LONG);
	}
	public void showT(Object text)
	{
		showT(String.valueOf(text),Toast.LENGTH_LONG);
	}
	public void showT(String text,int len)
	{
		if(m_currentToast != null) {}//cancel个什么劲？？m_currentToast.cancel();
		else {
			toastV = getLayoutInflater().inflate(R.layout.toast,null);
			toastTv = toastV.findViewById(R.id.message);
			m_currentToast = new Toast(this);

			m_currentToast.setGravity(Gravity.BOTTOM, 0, 135);
			m_currentToast.setView(toastV);
		}
		m_currentToast.setDuration(len);
		//m_currentToast = Toast.makeText(getContext(), text, Toast.LENGTH_SHORT);
		toastTv.setText(text);
		m_currentToast.show();
		CMN.Log("showed!!!????");
	}




	protected static final float[] NEGATIVE = {
			-1.0f,     0,     0,    0, 255, // red
			0, -1.0f,     0,    0, 255, // green
			0,     0, -1.0f,    0, 255, // blue
			0,     0,     0, 1.0f,   0  // alpha
	};



	public static void setStatusBarColor(Window window){
		window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
				| WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		if(Build.VERSION.SDK_INT>=21) {
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
			//window.setNavigationBarColor(Color.TRANSPARENT);
		}
	}



	protected void checkMargin() {
		//File additional_config = new File(opt.pathToMain()+"appsettings.txt");
		File additional_config = new File(Environment.getExternalStorageDirectory(),"PLOD/appsettings.txt");
		if(additional_config.exists()) {
			try {
				BufferedReader in = new BufferedReader(new FileReader(additional_config));
				String line;
				while ((line = in.readLine()) != null) {
					String[] arr = line.split(":", 2);
					if (arr.length == 2) {
						switch (arr[0]) {
							case "window margin":
							case "窗体边框":
								arr = arr[1].split(" ");
								if (arr.length == 4) {
									try {
										ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) root.getLayoutParams();
										DockerMarginL = lp.leftMargin = Integer.valueOf(arr[2]);
										DockerMarginR = lp.rightMargin = Integer.valueOf(arr[3]);
										DockerMarginT = lp.topMargin = Integer.valueOf(arr[0]);
										DockerMarginB = lp.bottomMargin = Integer.valueOf(arr[1]);
										if(root!=null){
											root.setTag(false);
											root.setLayoutParams(lp);
										}
									} catch (Exception e) {//CMN.Log(e);
									}
								}
								break;
						}
					}
				}
			} catch (Exception e) {
			}
		}
	}
}




