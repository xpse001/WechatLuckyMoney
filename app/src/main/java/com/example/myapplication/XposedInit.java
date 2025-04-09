package com.example.myapplication;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;
import static de.robv.android.xposed.XposedHelpers.newInstance;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.myapplication.model.RedPackContent;
import com.example.myapplication.model.RedPackDTO;
import com.example.myapplication.utils.XmlToBeanUtil;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class XposedInit implements IXposedHookLoadPackage {

    private static final String HOOK_APP_NAME = "com.tencent.mm";

    //微信日志级别
    public static final int LEVEL_VERBOSE = 0;
    public static final int LEVEL_DEBUG = 1;
    public static final int LEVEL_INFO = 2;
    public static final int LEVEL_WARNING = 3;
    public static final int LEVEL_ERROR = 4;
    public static final int LEVEL_FATAL = 5;
    public static final int LEVEL_NONE = 6;

    //微信日志过滤
    public static final String LOG_VERBOSE = "LOG_VERBOSE";
    public static final String LOG_DEBUG = "LOG_DEBUG";
    public static final String LOG_INFO = "LOG_INFO";
    public static final String LOG_WARNING = "LOG_WARNING";
    public static final String LOG_ERROR = "LOG_ERROR";
    public static final String LOG_FATAL = "LOG_FATAL";
    public static final String LOG_NONE = "LOG_NONE ";

    /**
     * com.tencent.mm.ui.LauncherUI
     */
    private static Activity launcherUiActivity;

    /**
     * LuckyMoneyNotHookReceiveUI
     */
    private static Activity currentActivity = null;


    private static RedPackDTO redPackDTO;

    /**
     * 拆红包方法   可能不会嗲用
     *
     * @param param
     * @throws Throwable
     */
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam param) throws Throwable {
        if (!param.packageName.equals(HOOK_APP_NAME)) {
            return;
        }
        XposedBridge.log("Loaded app: " + param.packageName);
        ClassLoader classLoader = param.classLoader;
        hookXposedLoadClass();
        weChatLogOpen(classLoader);
        weChatLog(classLoader);
        hookLauncherUI(classLoader);
        hookSQLiteDatabase(classLoader);
        hooke2onGYNetEnd(classLoader);
        hookRedPacketMethod(classLoader);
        hookRedPacketNetworkRequest(classLoader);


        // 监控int i15, int i16, String str, String str2, String str3, String str4, String str5, String str6, String str7
        XposedHelpers.findAndHookConstructor("com.tencent.mm.plugin.luckymoney.model.e2",
                classLoader,
                int.class,
                int.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        // 构造方法执行前
                        XposedBridge.log("即将创建对象，参数: " + Arrays.toString(param.args));

                        // 打印堆栈
                        //printFilteredStackTrace();
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        // 构造方法执行后
                        XposedBridge.log("对象创建完成，实例: " + param.thisObject);
                        // 获取构造后的对象属性（如果有getter）
                        Object instance = param.thisObject;
                    }
                });

    }


    public void hookXposedLoadClass() {
        // 过防止调用loadClass加载 de.robv.android.xposed.
        XposedHelpers.findAndHookMethod(
                ClassLoader.class,
                "loadClass",
                String.class,
                new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.args != null && param.args[0] != null && param.args[0].toString()
                                .startsWith("de.robv.android.xposed.")
                        ) {
                            // 改成一个不存在的类
                            param.args[0] = "com.tencent.cndy";
                        }
                        super.beforeHookedMethod(param);
                    }
                });
    }


    public void hookLauncherUI(ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> hookLauncherUIClass = classLoader.loadClass("com.tencent.mm.ui.LauncherUI");
        // hook 微信主界面的onCreate方法，获得主界面对象
        XposedHelpers.findAndHookMethod(
                hookLauncherUIClass,
                "onCreate",
                Bundle.class,
                new XC_MethodHook() {

                    protected void afterHookedMethod(MethodHookParam param) {
                        launcherUiActivity = (Activity) param.thisObject;
                    }
                });


    }


    public static Activity findLuckyMoneyUI() {
        try {
            // 获取 ActivityThread 实例
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = callStaticMethod(
                    activityThreadClass, "currentActivityThread");

            // 获取 mActivities 字段（存储所有 Activity）
            Map<?, ?> activities = (Map<?, ?>) XposedHelpers.getObjectField(
                    activityThread, "mActivities");

            // 遍历查找目标 Activity
            for (Object activityRecord : activities.values()) {
                Activity activity = (Activity) XposedHelpers.getObjectField(
                        activityRecord, "activity");

                XposedBridge.log("activity,clazz: " + activity.getClass().getName());
                if (activity != null && activity.getClass().getName().contains("LuckyMoney")) {
                    return activity;
                }
            }
        } catch (Exception e) {
            Log.e("Xposed", "遍历 Activity 堆栈失败: " + e.getMessage());
        }
        return null;
    }


    public void hooke2onGYNetEnd(ClassLoader classLoader) {

        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.luckymoney.model.j2",
                classLoader,
                "onGYNetEnd",
                int.class,
                String.class,
                org.json.JSONObject.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        // 构造方法执行前
                        XposedBridge.log("onGYNetEnd,前参数: " + Arrays.toString(param.args));
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        // 构造方法执行后
                        XposedBridge.log("onGYNetEnd,后参数: " + Arrays.toString(param.args));
                        // 获取构造后的对象属性（如果有getter）
                        JSONObject jsonObject = (JSONObject) param.args[2];
                        //防止重入标识
                        String timingIdentifier = jsonObject.optString("timingIdentifier");
                        int hbType = jsonObject.optInt("hbType");
                        int hbStatus = jsonObject.optInt("hbStatus");

                        if (hbStatus != 2) {
                            return;
                        }
                        XposedBridge.log("onGYNetEnd,timingIdentifier: " + timingIdentifier);
                        XposedBridge.log("onGYNetEnd,hbType: " + hbType);


                        Object thisObject = param.thisObject;
                        Field[] fields = thisObject.getClass().getDeclaredFields();
                        XposedBridge.log("thisObject： " + thisObject.getClass().getName());
                        for (Field field : fields) {
                            field.setAccessible(true); // 突破私有权限
                            Object value = field.get(thisObject);
                            XposedBridge.log("j2方法 field： " + field.getName() + ",value=" + value);
                        }


                        if (redPackDTO == null) {
                            XposedBridge.log("redPackDTO is null: ");
                            return;
                        }

                        String nativeUrl = (String) getObjectField(thisObject, "n");
                        String sendId = (String) getObjectField(thisObject, "j");
                        Integer isSender = (Integer) getObjectField(thisObject, "x");
                        Integer channelId = (Integer) getObjectField(thisObject, "i");
                        Integer msgType = (Integer) getObjectField(thisObject, "h");
                        //int msgType = redPackDTO.getMsgType();
                        // int channelId = redPackDTO.getChannelId();
                        // String sendId = redPackDTO.getSendId();
                        //String nativeUrl = redPackDTO.getNativeUrl();
                        String headImg = redPackDTO.getHeadImg();
                        String nickName = redPackDTO.getNickName();
                        String sessionUserName = redPackDTO.getTalker();
                        String ver = redPackDTO.getVer();
                        String leftButtonContinue = redPackDTO.getLeftButtonContinue();


                        android.content.Context context = (Context) callStaticMethod(findClass("com.tencent.mm.sdk.platformtools.MMApplicationContext", classLoader), "getContext");
                        final Object d1 = newInstance(findClass("com.tencent.mm.plugin.luckymoney.model.d1", classLoader), context, currentActivity);


                        //e2Var = new com.tencent.mm.plugin.luckymoney.model.e2(j2Var3.f128089h, j2Var3.f128090i, j2Var3.f128091j, j2Var3.f128092n, com.tencent.mm.plugin.luckymoney.model.l1.g(), kd0.u0.l(), stringExtra, "v1.0", this.W0.P, str2);
                        final Object modele2 = newInstance(findClass("com.tencent.mm.plugin.luckymoney.model.e2", classLoader),
                                msgType, channelId, sendId, nativeUrl, headImg, nickName, sessionUserName, ver, timingIdentifier, leftButtonContinue);

                        if (true) {
                            Thread.sleep(1000);
                        }
                        //初始化D1
                        if (currentActivity != null) {
                            //拆红包方法
                            callMethod(d1, "d", modele2, false);
                        } else {
                            XposedBridge.log("currentActivity is null: ");
                        }


                        super.afterHookedMethod(param);
                    }
                });
    }

    public void hookRedPacketNetworkRequest(ClassLoader classLoader) {
        try {
            final Class<?> d1Class = findClass("com.tencent.mm.plugin.luckymoney.model.d1", classLoader);
            XposedHelpers.findAndHookMethod(d1Class, "onSceneEnd", int.class, int.class, String.class, findClass("vd0.y", classLoader), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    int reqType = (int) param.args[0];
                    int errCode = (int) param.args[1];
                    String errMsg = (String) param.args[2];
                    Object response = param.args[3];

                    // 判断是否是红包相关的请求类型 (需要通过分析微信代码确定具体的请求类型)
                    // 例如，假设某个特定的 reqType 值代表接收到新红包
                    int RECEIVE_HONGBAO_REQ_TYPE = 123; // 替换为真实的请求类型
                    // 获取 d1 类的实例
                    Object d1Instance = param.thisObject;

                    XposedBridge.log("reqType==" + reqType + ",errCode=" + errCode + ",errMsg=" + errMsg + ",response=" + response + ",d1Instance=" + d1Instance);

                    Field[] fields = d1Instance.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true); // 突破私有权限
                        Object value = field.get(d1Instance);
                        XposedBridge.log("onSceneEnd field： " + field.getName() + ",value=" + value);
                    }

                }
            });


            XposedHelpers.findAndHookMethod(d1Class, "b", findClass("vd0.y", classLoader), boolean.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object vd0y = param.args[0];
                    boolean show = (boolean) param.args[1];
                    XposedBridge.log("b方法" + vd0y.getClass() + ",show=" + show);

                }
            });


            XposedHelpers.findAndHookMethod(d1Class, "d", findClass("vd0.y", classLoader), boolean.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object vd0y = param.args[0];
                    boolean show = (boolean) param.args[1];
                    XposedBridge.log("d方法" + vd0y.getClass() + ",show=" + show);
                    Field[] fields = vd0y.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true); // 突破私有权限
                        Object value = field.get(vd0y);
                        XposedBridge.log("d方法 field： " + field.getName() + ",value=" + value);
                    }

                }
            });


        } catch (Exception e) {
            XposedBridge.log("解析或打开红包失败:" + e.getMessage());
        }

    }


    public void hookSQLiteDatabase(ClassLoader classLoader) throws ClassNotFoundException {
        Class<?> hookClass = classLoader.loadClass("com.tencent.wcdb.database.SQLiteDatabase");
        XposedHelpers.findAndHookMethod(hookClass,
                "insertWithOnConflict",
                String.class,
                String.class,
                ContentValues.class,
                int.class,
                new XC_MethodHook() {

                    protected void afterHookedMethod(MethodHookParam param) {
                        // 打印插入数据信息
                        ContentValues contentValues = (ContentValues) param.args[2];
                        String title = "";
                        // 检查表名
                        String tableName = (String) param.args[0];
                        if (tableName == null || !tableName.equals("message")) {
                            return;
                        }
                        // 检查消息类型
                        Integer type = contentValues.getAsInteger("type");
                        if (type == null) return;
                        XposedBridge.log("接收到消息:类型= " + type + ",标题=" + title);

                        if (type == 436207665 || type == 469762097) {

//                            for (String key : contentValues.keySet()) {
//                                XposedBridge.log("Xposed:" + "Key: " + key + " | Value: " + contentValues.get(key));
//                            }
                            // 提取关键参数
                            String sendId = contentValues.getAsString("sendid");
                            String nativeUrl = contentValues.getAsString("native_url");
                            //keyUsername
                            String talker = contentValues.getAsString("talker");
                            String headImg = "";
                            String nickName = "";
                            String sessionUserName = "";
                            String ver = "v1.0";
                            String timingIdentifier = "";
                            String left_button_continue = "";
                            int channelId = 1;
                            int msgType = 1;
                            int keyWay = 1;
                            String content = contentValues.getAsString("content");
                            if (content == null || content == "") {
                                return;
                            }
                            try {

                                XposedBridge.log("Xposed:" + "redPackContent: " + content);
                                RedPackContent redPackContent = XmlToBeanUtil.convertXmlToBean(content, RedPackContent.class);
                                //XposedBridge.log("Xposed:" + "redPackContent2: " + redPackContent);

                                String url = redPackContent.getAppMsg().getUrl();
                                Uri parse = Uri.parse(url);
                                msgType = Integer.parseInt(parse.getQueryParameter("msgtype"));
                                channelId = Integer.parseInt(parse.getQueryParameter("channelid"));
                                sendId = parse.getQueryParameter("sendid");
                                nativeUrl = redPackContent.getAppMsg().getWcpayinfo().getNativeurl();

                                //com.tencent.mm.plugin.luckymoney.model.l1.g()
                                headImg = (String) callStaticMethod(findClass("com.tencent.mm.plugin.luckymoney.model.l1", classLoader), "g");
                                nickName = (String) callStaticMethod(findClass("kd0.u0", classLoader), "l");


                                XposedBridge.log("Xposed:" + "head=" + headImg);
                                XposedBridge.log("Xposed:" + "nickName=" + nickName);
                                XposedBridge.log("Xposed:" + "msgType=" + msgType);
                                XposedBridge.log("Xposed:" + "channelId=" + channelId);
                                XposedBridge.log("Xposed:" + "sendId=" + sendId);
                                XposedBridge.log("Xposed:" + "nativeUrl=" + nativeUrl);
                                XposedBridge.log("Xposed:" + "talker=" + talker);
                                redPackDTO = new RedPackDTO(sendId, nativeUrl, talker, headImg, nickName, sessionUserName, ver, timingIdentifier, left_button_continue, channelId, msgType, keyWay);

                                //查询红包信息new com.tencent.mm.plugin.luckymoney.model.j2(1, i15, this.N, this.P, getIntent().getIntExtra("key_way", 0), "v1.0", stringExtra)
                                final Object j2 = newInstance(findClass("com.tencent.mm.plugin.luckymoney.model.j2", classLoader),
                                        msgType, channelId, sendId, nativeUrl, keyWay, ver, talker);
                                //发起网络请求n1.d(vd0.y yVar, boolean z15) 获取到  timingIdentifier
                                Activity activity = findLuckyMoneyUI();
                                if (activity != null) {
                                    currentActivity = activity;
                                }
                                android.content.Context context = (Context) callStaticMethod(findClass("com.tencent.mm.sdk.platformtools.MMApplicationContext", classLoader), "getContext");
                                final Object d1 = newInstance(findClass("com.tencent.mm.plugin.luckymoney.model.d1", classLoader), context, currentActivity);
                                callMethod(d1, "d", j2, false);

                            } catch (Exception e) {
                                e.printStackTrace();
                                XposedBridge.log("调用 O7 失败: " + e.getMessage());
                            }


                        }
                    }
                });

    }

    /**
     * 拆红包方法 基于微信8.0.41
     *
     * @param classLoader
     */
    private void hookRedPacketMethod(final ClassLoader classLoader) {
        // hook领取红包页面的onCreate方法，打印Intent中的参数（只起到调试作用）
        XposedHelpers.findAndHookMethod(
                "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyNotHookReceiveUI",
                classLoader,
                "O7",
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Object arg = param.args[0];
                        XposedBridge.log("拆红包入参=" + arg);
                        super.beforeHookedMethod(param);
                    }

                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("调用拆包=" + param.args[0]);
                        super.afterHookedMethod(param);
                    }
                });
    }


    public static void printFilteredStackTrace() {
        RuntimeException e = new RuntimeException("<Start dump Stack !>");
        e.fillInStackTrace();
        //Log.i("DumpStack:", "++++++++++++", e);
        XposedBridge.log(e);
    }


    private void weChatLogOpen(final ClassLoader classLoader) {
        //isLogcatOpen
        XposedHelpers.findAndHookMethod("com.tencent.mm.xlog.app.XLogSetup", classLoader, "keep_setupXLog",
                boolean.class, String.class, String.class, Integer.class, Boolean.class, Boolean.class, String.class,
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[5] = true;
                        Log.i("Xposed", "keep_setupXLog before : " + true);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.args[5] = true;
                        super.afterHookedMethod(param);
                        Log.i("Xposed", "keep_setupXLog参数isLogcatOpen: " + param.args[5]);
                    }
                });
    }


    private void weChatLog(final ClassLoader classLoader) {
        XposedHelpers.findAndHookMethod("com.tencent.mars.xlog.Xlog", classLoader, "logWrite2",
                int.class, String.class, String.class, String.class, int.class, int.class, long.class, long.class, String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        int logType = (int) param.args[0];
                        String str2 = (String) param.args[1];
                        String str3 = (String) param.args[2];
                        String str4 = (String) param.args[3];
                        int num4 = (int) param.args[4];
                        int num5 = (int) param.args[5];
                        long long6 = (long) param.args[6];
                        long long7 = (long) param.args[7];
                        String str8 = (String) param.args[8];
                        //根据值来过过滤日志级别
                        String wechatLogType = getWechatLogType(logType);

                        Log.d(wechatLogType, "LogType===" + logType);
                        Log.d(wechatLogType, str2);
                        Log.d(wechatLogType, str3);
                        Log.d(wechatLogType, str4);
                        Log.d(wechatLogType, "" + num4);
                        Log.d(wechatLogType, "" + num5);
                        Log.d(wechatLogType, "" + long6);
                        Log.d(wechatLogType, "" + long7);
                        Log.d(wechatLogType, str8);
                        super.beforeHookedMethod(param);
                    }
                });

    }

    //根据logWrite2方法里面传过来的参数来过滤字段，默认值
    private String getWechatLogType(int logType) {
        String TAG = null;
        if (logType == LEVEL_VERBOSE) {
            TAG = LOG_VERBOSE;
        } else if (logType == LEVEL_DEBUG) {
            TAG = LOG_DEBUG;
        } else if (logType == LEVEL_INFO) {
            TAG = LOG_INFO;
        } else if (logType == LEVEL_WARNING) {
            TAG = LOG_WARNING;
        } else if (logType == LEVEL_ERROR) {
            TAG = LOG_ERROR;
        } else if (logType == LEVEL_FATAL) {
            TAG = LOG_FATAL;
        } else if (logType == LEVEL_NONE) {
            TAG = LOG_NONE;
        }
        return TAG;
    }
}