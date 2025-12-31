# WechatLuckyMoney  适配Lsposed ,FPA

xposed微信抢红包，适配微信最新版本8.0.58
支持微信版本 8.0.57 ,8.0.58

1.增加防撤回功能.

2.静默抢红包

3.朋友圈防删除

4.运行步数修改

5.朋友圈广告（默认支持,未开启设置选项）

6.新增转圈功能（聊天页面，长按消息）

7.复制群ID （用于消息发送，红包设置等）

9.自动收款，接收他人转账

## 待办事项

- [x] 1.TODO 抢红包增加设置界面，比如 自己发的不抢，抢红包延时,指定群不抢  
- [ ] 2.TODO 增加群群发功能
- [x] 3.TODO 自动适配新版本
- [x] 4.TODO 朋友圈防删除
- [x] 5.TODO 增加朋友圈转发功能 
- [x] 6.TODO 自动收款，接收他人转账
- [] 7.TODO 适配谷歌版本微信

感谢大佬：

| @LuckyPray | [DexKit](https://github.com/LuckyPray/DexKit)                |
| ---------- | ------------------------------------------------------------ |
| @rarnu     | [wechat_no_revoke](https://github.com/rarnu/wechat_no_revoke) |
|            |                                                              |
|            |                                                              |



### 使用说明

1.  本APP是为了学习研究用，不得进行任何形式的转发，发布，传播。
2. 请于24小时内卸载本APP。若使用期间造成任何损失，作者不负任何责任。
3. 本APP不篡改，不修改，不获取任何个人信息及其微信信息。
4. 本APP使用者因为违反本声明的规定而触犯中华人民共和国法律的，一切后果自负，作者不承担任何责任。
5. 凡以任何方式直接、间接使用APP者，视为自愿接受本声明的约束。
6. 本APP如无意中侵犯了某个媒体或个人的知识产权，请来信或来电告之，作者将立即删除。







### 微信长安菜单逆向8.0.57
   try {

            XposedHelpers.findAndHookMethod(
                    "android.view.View",
                    hookParam.getAppClassLoader(),
                    "setOnLongClickListener",
                    "android.view.View$OnLongClickListener",
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            android.view.View.OnLongClickListener originalListener = (View.OnLongClickListener) param.args[0];
                            View.OnLongClickListener newListener = v -> {
                                // 在这里处理长按事件
                                XposedBridge.log("长按事件发生在View: " + v);
                                XposedBridge.log("长按事件发生在getClass: " + originalListener.getClass().getName());
                                XposedDebugUtil.printFilteredStackTrace();
                                // 调用原始监听器
                                return originalListener != null && originalListener.onLongClick(v);
                            };
                            param.args[0] = newListener;

                        }
                    });

            XposedHelpers.findAndHookMethod("com.tencent.mm.ui.widget.MMNeat7extView", hookParam.getAppClassLoader(), "setOnLongClickListener", "android.view.View$OnLongClickListener", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object arg = param.args[0];
                    XposedBridge.log("setOnLongClickListener: " + arg.getClass().getName());
                }

            });

            // Hook nv4.t's constructor to log f281947g's class
            XposedHelpers.findAndHookConstructor(
                    "nv4.t",
                    hookParam.getAppClassLoader(),
                    "nv4.m",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Object menuController = XposedHelpers.getObjectField(param.thisObject, "g");
                            XposedBridge.log("f281947g class: " + (menuController != null ? menuController.getClass().getName() : "null"));
                        }
                    }
            );

            XposedHelpers.findAndHookMethod("com.tencent.mm.ui.chatting.viewitems.s0", hookParam.getAppClassLoader(), "c", "android.view.View", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Object selectableTextHelper = param.thisObject;
                    XposedBridge.log("viewitems.s0: " + param.args[0]);

                }

            });


            //创建菜单的地方,文本

            /**
             * com.tencent.mm.ui.chatting.viewitems.lq  MicroMsg.ChattingItemTextFromBase
             *                                          com/tencent/mm/ui/chatting/viewitems/ChattingItemTextBase$ChattingItemTextFromBase
             *
             *z3  z3 implements ContextMen
             *    filed  ArrayList<Object> d;
             *
             * com.tencent.mm.storage.l9
             *    MMRevoke.RevokeMsgManager
             *    MicroMsg.MsgInfo
             */
            XposedHelpers.findAndHookMethod("com.tencent.mm.ui.chatting.viewitems.lq", hookParam.getAppClassLoader(), "h0", "sn4.z3", "android.view.View", "android.view.ContextMenu$ContextMenuInfo", "com.tencent.mm.storage.l9", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            // 获取菜单控制器 sn4.z3

                }
            });

            /**
             *              *z3  z3 implements ContextMen
             *              *    filed  ArrayList<Object> d;
             */
            XposedHelpers.findAndHookMethod("com.tencent.mm.ui.chatting.viewitems.l0", hookParam.getAppClassLoader(), "a", "sn4.z3", "android.view.View", "android.view.ContextMenu$ContextMenuInfo", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    XposedBridge.log("viewitems.l0.a: " + param.args[0]);
                    Object z3Instance =   param.args[0];
                    MenuItem   item =  (MenuItem)XposedHelpers.callMethod(
                            z3Instance,
                            "c",
                            0,  //groupid
                            200,  //itemID
                            0,   // 位置
                            "发圈",
                            android.R.drawable.ic_menu_slideshow  //图标
                    );
                    item.setIcon(XposedInit.MODULE_RESOURCES.getDrawable(R.drawable.wx_share));
                }
            });

            //MicroMsg.ChattingItem", "context item select failed, null dataTag
            XposedHelpers.findAndHookMethod("com.tencent.mm.ui.chatting.viewitems.p0", hookParam.getAppClassLoader(), "onMMMenuItemSelected", "android.view.MenuItem", int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    MenuItem menuItem =(MenuItem)param.args[0];
                    int itemId = menuItem.getItemId();
                    int position = (int) param.args[1];
                    if (itemId == 200) {
                        XposedBridge.log("我被点击了: "+ itemId+",=" +position);
                    }
                }

            });
