package com.inroids.irmultimedia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import inroids.common.MyLog;

public class MultiPlayActivity extends Activity implements OnTouchListener {
	public static final String TAG = "IRMultimedia";
	DisplayMetrics dm = new DisplayMetrics();
	public android.widget.FrameLayout frmMain;

	public AppPublic appMy = null;

	private Timer tmrMain = null;

	public int intCurPos = 0, intFinishValue = 0, intTag = 0;

	// 计时器计时,返回时间计时
	private int intTime = 0, intBackTime = 0;

	// 是否触摸播放 是否触摸中 是否点击操作跳转
	public boolean isCanTouch = false, isTouching = false, isEventTurn = false;

	public boolean isPause = false;

	// 是否过期节目单
	private boolean isOverduePage = false;

	public ImageMemoryCache imgCache;

	public int intTurnId = 0, intEffect = 0;

	private Button hiddenBtn;

	// 获取GPIO信号值参数,低信号值,高信号值, 获取失败值
	private static int GPIO = 114, LGPIOSTATE = 0, HGPIOSTATE = 1,
			FGPIOSTATE = -1;

	// GPIO信号值
	private int iGPIOState = 255;

	// 常量,操作界面返回广告页面时间
	public static final int TOHOMETIME = 60;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.appMy = ((AppPublic) getApplicationContext());
		this.frmMain = new android.widget.FrameLayout(this);
		this.setContentView(this.frmMain);
		this.appMy.actMain = this;
		this.appMy.isUpdating = false;
		this.isPause = false;
		this.imgCache = new ImageMemoryCache();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// this.appMy.activityList.add(this);
		this.setupViews();

		// 启动定时器
		this.startTime();
	}

	private void setupViews() {
		try {
			if (this.appMy.arrJson == null) {
				System.out.println("this.appMy.arrJson==null");
				if (inroids.common.System
						.isNotNullString(this.appMy.strDataFile)
						&& inroids.common.FileManage
						.isExistsFile(this.appMy.strDataFile)) {
					String strData = inroids.common.FileManage
							.getStringFromFile(this.appMy.strDataFile);
					if (inroids.common.System.isNotNullString(strData)) {
						try {
							JSONObject objMain = new JSONObject(strData);
							if (!objMain.isNull("p")) {
								this.appMy.arrJson = objMain.getJSONArray("p");
							}

						} catch (JSONException e) {
							MyLog.e(this.getString(R.string.app_key),
									"MultiPlayer.refreshData:" + e.toString());
						}
					}
				}
			}
			// MyLog.e(TAG, "ActivityMultiPlay.onCreate:" + "arrJson ==null");
			// Start
			if (this.appMy.intJSONId <= 0) {
				this.appMy.startTime();
				boolean isT = false;
				int iTag = 0;
				for (int i = 0; i < this.appMy.arrJson.length(); i++) {
					JSONObject objJson = (JSONObject) this.appMy.arrJson.get(i);
					if (i == 0) {
						iTag = ClassObject.getInt(objJson, "id");
					}
					if (!ClassObject.getBoolean(objJson, "child")) {
						this.intTag = ClassObject.getInt(objJson, "id");
						this.appMy.intJSONId = this.intTag;
						this.appMy.homePageId = this.intTag;
						this.appMy.menuPageId = ClassObject.getInt(objJson,
								"xy");
						String eDate = ClassObject.getString(objJson, "edate");
						if (inroids.common.System.isNotNullString(eDate))
							isOverduePage = inroids.common.DateTime
									.dateOverdue(eDate, "yyyy-MM-dd");
						if (!isOverduePage) {
							String strT = ClassObject.getString(objJson,
									"content");
							if (inroids.common.System.isNotNullString(strT)) {
								this.appMy.intJSONPos = i;
								this.setWidget(objJson.getJSONArray("content"));
								isT = true;
								break;
							}
						} else {
							this.showOverdueDialog();
							break;
						}

					}
				}

				if (iTag > 0 && !isT) {
					this.appMy.intJSONPos = 0;
					this.intTag = iTag;
					this.appMy.homePageId = this.intTag;
					this.appMy.intJSONId = iTag;
					JSONObject objJson = (JSONObject) this.appMy.arrJson.get(0);
					this.appMy.menuPageId = ClassObject.getInt(objJson, "xy");
					String eDate = ClassObject.getString(objJson, "edate");
					if (inroids.common.System.isNotNullString(eDate))
						isOverduePage = inroids.common.DateTime.dateOverdue(
								eDate, "yyyy-MM-dd");
					if (!isOverduePage) {
						this.setWidget(objJson.getJSONArray("content"));
					} else {
						showOverdueDialog();
					}
				}
			} else {
				for (int i = 0; i < this.appMy.arrJson.length(); i++) {
					JSONObject objJson = (JSONObject) this.appMy.arrJson.get(i);
					int iTag = ClassObject.getInt(objJson, "id");
					if (this.appMy.intJSONId == iTag) {
						this.intTag = iTag;
						this.appMy.intJSONId = this.intTag;
						String eDate = ClassObject.getString(objJson, "edate");
						if (inroids.common.System.isNotNullString(eDate))
							isOverduePage = inroids.common.DateTime
									.dateOverdue(eDate, "yyyy-MM-dd");
						if (!isOverduePage) {
							String strT = ClassObject.getString(objJson,
									"content");
							if (inroids.common.System.isNotNullString(strT)) {
								this.appMy.intJSONPos = i;
								this.setWidget(objJson.getJSONArray("content"));
								break;
							}
						} else {
							this.appMy.intJSONPos = i;
							this.showOverdueDialog();
							break;
						}
					}
				}
			}

			// System.out.println("homeId=" + appMy.homePageId + ", menuId=" +
			// appMy.menuPageId);
		} catch (Exception e) {
			MyLog.e(TAG, "ActivityMultiPlay.onCreate:" + e.toString());
		}
		hiddenBtn = new Button(this);
		hiddenBtn.setTag(11);
		hiddenBtn.setBackgroundColor(getResources().getColor(
				R.color.hiddenbuttoncolor));
		Rect rect = new Rect(0, 0, 80, 80);
		this.frmMain.addView(hiddenBtn, ClassObject.getParams(rect));
		hiddenBtn.setOnLongClickListener(lc);

	}

	private Button.OnLongClickListener lc = new Button.OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			final EditText view = new EditText(MultiPlayActivity.this);
			Builder builder = new AlertDialog.Builder(MultiPlayActivity.this)
					.setTitle("输入密码回到系统界面");
			view.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
			view.setHint("请输入密码");
			builder.setView(view);
			AlertDialog alert = builder
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
													int arg1) {

								}
							})
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface arg0,
													int arg1) {
									if (view.getText().toString()
											.equalsIgnoreCase("@123")) {
										Intent i = new Intent();
										i.addCategory("android.intent.category.HOME");
										i.setAction("android.intent.action.MAIN");
										i.setClassName("com.android.launcher",
												"com.android.launcher2.Launcher");
										startActivity(i);
										finish();
									} else {
										Toast.makeText(MultiPlayActivity.this,
												"密码不正确", Toast.LENGTH_LONG)
												.show();
									}
								}
							}).create();
			alert.show();

			return true;
		}
	};

	// Set Widget
	public void setWidget(JSONArray arrT) {
		try {
			for (int i = 0; i < arrT.length(); i++) {
				this.setObject(arrT.getJSONObject(i));
			}
		} catch (Exception e) {
			MyLog.e(TAG, "ActivityMultiPlay.setWidget:" + e.toString());
		}
	}

	public void setObject(JSONObject objT) {
		try {
			int intType = ClassObject.getInt(objT, "type");
			// System.out.println(objT);
			switch (intType) {
				// Back
				case 100:
					ClassPublic.addPageView(this.frmMain, this.appMy.strResPath,
							objT);
					break;
				// Image
				case 1:
					ClassPublic.addImageView(this.frmMain, this.appMy.strResPath,
							objT);
					break;
				// Multi
				case 3:
					ClassPublic.addMulti(this.frmMain, this.appMy.strResPath, objT);
					break;
				// MultiImage
				case 2:
					ClassPublic.addMultiImage(this.frmMain, this.appMy.strResPath,
							objT);
					break;
				// Text
				case 4:
					ClassPublic.addText(this.frmMain, this.appMy.strResPath, objT);
					break;
				// Slideshow
				case 5:
					ClassPublic.addImagesView(this.frmMain, this.appMy.strResPath,
							objT);
					break;
				// Image Text
				case 6:
					ClassPublic.addImageText(this.frmMain, this.appMy.strResPath,
							objT);
					break;
				// Time Label
				case 7:
					ClassPublic.addTimeLabel(this.frmMain, objT);
					break;
				// Marquee View
				case 9:
					ClassPublic.addMarqueeView(this.frmMain, objT);
					break;
				// Web View
				case 10:
					ClassPublic.addWebView(this.frmMain, this.appMy.strResPath,
							objT);
					break;
			}
		} catch (Exception e) {
			MyLog.e(TAG, "ActivityMultiPlay.setObject:" + e.toString());
		}
	}

	// Open File
	public void openFile(String strFile) {
		boolean isOpenSuccess = false;
		isOpenSuccess = inroids.common.OpenFile.autoOpenFile(this, strFile);
		if (!isOpenSuccess)
			MyLog.e(this.getString(R.string.app_key),
					"MultiPlayActivity.openFile:" + strFile);
	}

	// Turn Page
	public void turnPage(int iTurnId, int iEffect) {
		try {
			// Log.e(strTag,"ActivityMultiPlay.turnPage-> TurnId:"+iTurnId);

			boolean isOpen = false;
			if (this.appMy.arrJson != null) {
				if (iTurnId > 0 && iTurnId != this.intTag) {
					for (int i = 0; i < this.appMy.arrJson.length(); i++) {
						JSONObject objJson = (JSONObject) this.appMy.arrJson
								.get(i);
						int iTag = ClassObject.getInt(objJson, "id");
						if (iTag == iTurnId) {
							this.appMy.intJSONId = iTurnId;
							isOpen = true;
							break;
						}
					}
				}
				// open event
				if (isOpen) {
					if (this.isEventTurn == true) {
						MultiPlayActivity.this.appMy.updateVisitCount(iTurnId);
						this.isEventTurn = false;
					}
					this.isCanTouch = false;
					Intent newIntent = new Intent(this, MultiPlayActivity.class);
					this.startActivity(newIntent);
//					this.overridePendingTransition(android.R.anim.fade_in,
//							android.R.anim.fade_out);
					this.finish();

				} else {
					MyLog.e(TAG, "ActivityMultiPlay.turnPage-> TurnId:"
							+ iTurnId + " is not find!");
				}
			} else {
				MyLog.e(TAG, "ActivityMultiPlay.turnPage-> TurnId:" + iTurnId
						+ " is not find!");
			}
		} catch (Exception e) {
			MyLog.e(TAG, "ActivityMultiPlay.turnPage:" + e.toString());
		}
	}

	// finish Turn
	public boolean finishTurn() {
		try {
			if (this.intTurnId > 0) {
				this.turnPage(this.intTurnId, this.intEffect);
			} else {
				boolean isFind = false;
				// 从当前页面往后找起
				for (int i = this.appMy.intJSONPos + 1; i < this.appMy.arrJson
						.length(); i++) {
					JSONObject objJson = (JSONObject) this.appMy.arrJson.get(i);
					if (!ClassObject.getBoolean(objJson, "child")) {
						int iId = ClassObject.getInt(objJson, "id");
						if (iId != this.intTag) {
							this.turnPage(iId, this.intEffect);
							isFind = true;
							break;
						}
					}
				}
				// 如果没有找到, 则从第一个页面遍历找起
				if (!isFind) {
					if (this.appMy.arrJson.length() == 1) {
						this.turnPage(this.intTag, this.intEffect);
						isFind = true;
					} else {
						for (int i = 0; i <= this.appMy.intJSONPos; i++) {
							JSONObject objJson = (JSONObject) this.appMy.arrJson
									.get(i);
							if (!ClassObject.getBoolean(objJson, "child")) {
								int iId = ClassObject.getInt(objJson, "id");
								if (iId != this.intTag) {
									this.turnPage(iId, this.intEffect);
									isFind = true;
									break;
								}
							}
						}
					}
				}

				if (!isFind) {
					MyLog.e(TAG, "ActivityMultiPlay.finishTurn is not find!");
				}
			}

		} catch (Exception e) {
			MyLog.e(TAG, "ActivityMultiPlay.setObject:" + e.toString());
		}

		return false;
	}

	// Add 2014-02-26
	public void startTime() {
		try {
			if (tmrMain == null)
				this.tmrMain = new Timer();
			// time update
			this.tmrMain.schedule(new TimerTask() {
				@Override
				public void run() {
					Message message = new Message();
					message.what = 1;
					MultiPlayActivity.this.handler.sendMessage(message);
				}
			}, 0, 1000);
		} catch (Exception e) {
			MyLog.e(this.getString(R.string.app_key), "AppPublic.startTime:"
					+ e.toString());
		}
	}

	// Add 2014-02-26
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				// System.out.println(AdActivity.this.appMy.isUpdating);

				MultiPlayActivity.this.intTime++;
				if (MultiPlayActivity.this.intTime > 3600)
					MultiPlayActivity.this.intTime = 0;

//				iGPIOState = MultiPlayActivity.this.appMy.PicService
//						.SetGPIOInputToGetGPIOStatus(GPIO);
				// 检测到低信号
				if (iGPIOState == LGPIOSTATE) {
					// 当前页面为广告页面
					if (appMy.homePageId == intTag) {
						iGPIOState = 255;
						MultiPlayActivity.this.intCurPos = 0;
						// 跳转到查询页
						if (MultiPlayActivity.this.appMy.menuPageId > 0) {
							if (MultiPlayActivity.this.tmrMain != null) {
								MultiPlayActivity.this.tmrMain.cancel();
								MultiPlayActivity.this.tmrMain = null;
							}
							MultiPlayActivity.this
									.turnPage(appMy.menuPageId, 1);
						}
					} else if (appMy.homePageId != intTag) {
						MultiPlayActivity.this.intBackTime = 0;
					}
				}
				// 检测到高信号
				else if (iGPIOState == HGPIOSTATE) {
					// 当前页面非广告页面
					if (appMy.homePageId != intTag) {
						if (!isTouching) {
							MultiPlayActivity.this.intBackTime++;
							if (MultiPlayActivity.this.intBackTime == TOHOMETIME) {
								iGPIOState = 255;
								MultiPlayActivity.this.intBackTime = 0;
								MultiPlayActivity.this.intCurPos = 0;
								MultiPlayActivity.this.turnPage(
										appMy.homePageId, 1);
							}
						}
					}
				}
				// 获取信号异常
				else {
					// MyLog.e(MultiPlayActivity.this.appMy.getString(R.string.app_key),
					// "appMy.intJSONId:"
					// + appMy.intJSONId + ", iGPIOState=" + iGPIOState);
				}

			} catch (Exception e) {
				MyLog.e(MultiPlayActivity.this.appMy
								.getString(R.string.app_key),
						"AppPublic.handler:" + e.toString());
			}
			super.handleMessage(msg);
		}
	};

	// Touch Event
	@Override
	public boolean onTouch(View v, MotionEvent e) {
		this.clearCurPos();

		if (e.getAction() == MotionEvent.ACTION_DOWN
				|| e.getAction() == MotionEvent.ACTION_MOVE) {
			isTouching = true;
		} else if (e.getAction() == MotionEvent.ACTION_UP) {
			isTouching = false;
		}

		return false;
	}

	// 清除数据
	public void clearCurPos() {
		MultiPlayActivity.this.intBackTime = 0;
		this.intCurPos = 0;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		MultiPlayActivity.this.appMy.intJSONId = 0;
		if (MultiPlayActivity.this.tmrMain != null) {
			MultiPlayActivity.this.tmrMain.cancel();
			MultiPlayActivity.this.tmrMain = null;
		}
	}

	private void showOverdueDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				MultiPlayActivity.this);
		builder.setMessage("节目单已过期，请联系管理员!").setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							// 将mShowing变量设为false，表示对话框已关闭
							field.set(dialog, true);
							dialog.dismiss();
							// System.out.println("intJSONPos=" +
							// MultiPlayActivity.this.appMy.intJSONPos);
							finishTurn();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

}
