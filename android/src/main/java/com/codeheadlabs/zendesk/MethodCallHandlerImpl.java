package com.codeheadlabs.zendesk;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zendesk.service.ZendeskCallback;
import com.zopim.android.sdk.api.ZopimChat;
import com.zopim.android.sdk.api.ZopimChatApi;
import com.zopim.android.sdk.data.LivechatAgentsPath;
import com.zopim.android.sdk.data.LivechatChatLogPath;
import com.zopim.android.sdk.data.observers.ChatLogObserver;
import com.zopim.android.sdk.model.ChatLog;
import com.zopim.android.sdk.model.PushData;
import com.zopim.android.sdk.model.VisitorInfo;
import com.zopim.android.sdk.prechat.ZopimChatActivity;
import com.zopim.android.sdk.util.AppInfo;
import com.zopim.android.sdk.widget.ChatWidgetService;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.RequiresApi;
import io.flutter.Log;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class MethodCallHandlerImpl implements MethodCallHandler {

  @Nullable
  private Activity activity;
  int initCount= 0;
  private MethodChannel methodCallHandler;

  Handler mhandler=new Handler(){
    @Override
    public void dispatchMessage(Message msg) {
      methodCallHandler.invokeMethod("UnreadListener",msg.what);
    }
  };

  void setMethodCall(MethodChannel methodCallHandler){
    this.methodCallHandler=methodCallHandler;
  }
  void setActivity(@Nullable Activity activity) {
    this.activity = activity;
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    switch (call.method) {
      case "init":
        handleInit(call, result);
        break;
      case "setVisitorInfo":
        handleSetVisitorInfo(call, result);
        break;
      case "setToken":
        handleSetToken(call, result);
        break;
      case "startChat":
        handleStartChat(call, result);
        break;
      case "version":
        handleVersion(result);
        break;
      case "openSystemAlert":
        openSystemAlert(result);
        break;
      case "checkSystemAlertPermission":
        checkSystemAlertPermission(result);
        break;
      case "closeChatWidget":
        closeChatWidget(result);
        break;
      default:
        try{
          result.notImplemented();
        }
        catch (IllegalStateException e){
          e.printStackTrace();
        }
        break;
    }
  }


  private void closeChatWidget(Result result) {
    Log.d("closeChatWidget","closeChatWidget");
    ChatWidgetService.stopService(activity);
    result.success("");
  }


  private void handleInit(MethodCall call, Result result) {
    ZopimChat.DefaultConfig zopimConfig = ZopimChat.init((String) call.argument("accountKey"));
    if (call.hasArgument("department")) {
      zopimConfig.department((String) call.argument("department"));
    }
    if (call.hasArgument("appName")) {
      zopimConfig.visitorPathOne((String) call.argument("appName"));
    }
    result.success(true);
  }

  public void getInitCountMessage(){
    initCount=LivechatChatLogPath.getInstance().countMessages(new ChatLog.Type[]{ChatLog.Type.CHAT_MSG_AGENT});
    Log.d("onActivity","initCount=="+initCount);
  }


  private void handleSetVisitorInfo(MethodCall call, Result result) {
    VisitorInfo.Builder builder = new VisitorInfo.Builder();
    if (call.hasArgument("name")) {
      builder = builder.name((String) call.argument("name"));
    }
    if (call.hasArgument("email")) {
      if(call.argument("email")!=null&&call.argument("email")!=""){
        Log.d("email","email="+call.argument("email").toString());
        builder = builder.email((String) call.argument("email"));
      }
    }
    if (call.hasArgument("phoneNumber")) {
      if(call.argument("phoneNumber")!=null&&call.argument("phoneNumber")!=""){
        builder = builder.phoneNumber((String) call.argument("phoneNumber"));
      }
    }
    if (call.hasArgument("note")) {
      builder = builder.note((String) call.argument("note"));
    }
    if (call.hasArgument("firebase_token")) {
      if(call.argument("firebase_token")!=null&&call.argument("firebase_token")!=""){
        ZopimChat.setPushToken((String) call.argument("firebase_token"));
      }
    }
    ZopimChat.setVisitorInfo(builder.build());

    result.success(true);
  }

  private void handleSetToken(MethodCall call, Result result) {
    if (call.hasArgument("firebase_token")) {
      if(call.argument("firebase_token")!=null&&call.argument("firebase_token")!=""){
        Log.d("firebase_token","firebasetoken="+call.argument("firebase_token").toString());
        ZopimChat.setPushToken((String) call.argument("firebase_token"));
      }
    }
    result.success(true);
  }

  private void handleStartChat(MethodCall call, Result result) {
    ZopimChatApi.getDataSource().addChatLogObserver(mChannelLogObserver);
    if (activity != null) {
      Intent intent = new Intent(activity, ZopimChatActivity.class);
      activity.startActivity(intent);
    }

    result.success(true);
  }

  private void handleVersion(Result result) {
    result.success(AppInfo.getChatSdkVersionName());
  }

  private void openSystemAlert(Result result){

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

      if(!Settings.canDrawOverlays(activity)){

        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);

        intent.setData(Uri.parse("package:" + activity.getPackageName()));

        activity.startActivity(intent);

      }
    }
    result.success("");
  }

  private void checkSystemAlertPermission(Result result) {
    try{
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        boolean check=Settings.canDrawOverlays(activity);

        if(result!=null){
          result.success(check);
          result=null;
        }

      }else{
        if(result!=null){
          result.success(true);
          result=null;
        }
      }
    }catch (IllegalStateException e){

      if(result!=null){
        result.success(true);
        result=null;
      }
    }
  }

  ChatLogObserver mChannelLogObserver = new ChatLogObserver() {
    public void update(LinkedHashMap<String, ChatLog> chatLog) {
      if(!ZendeskPlugin.isFore){
        int currentCount = LivechatChatLogPath.getInstance().countMessages(new ChatLog.Type[]{ChatLog.Type.CHAT_MSG_AGENT});
        if(initCount==0){
          initCount=LivechatChatLogPath.getInstance().countMessages(new ChatLog.Type[]{ChatLog.Type.CHAT_MSG_AGENT});
        }
        final int unread = currentCount-initCount;
        mhandler.sendEmptyMessage(unread);
      }
    }
  };

}
