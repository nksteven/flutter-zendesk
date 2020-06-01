package com.codeheadlabs.zendesk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.zopim.android.sdk.api.ZopimChatApi;
import com.zopim.android.sdk.prechat.ZopimChatActivity;
import com.zopim.android.sdk.widget.ChatWidgetService;

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.ActivityLifecycleListener;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * ZendeskPlugin
 */
public class ZendeskPlugin implements FlutterPlugin, ActivityAware , Application.ActivityLifecycleCallbacks {

  private MethodChannel channel;
  private MethodCallHandlerImpl methodCallHandler = new MethodCallHandlerImpl();
  private Activity activity;
  public static boolean isFore=false;

  public ZendeskPlugin(Activity activity) {
    this.activity=activity;
    activity.getApplication().registerActivityLifecycleCallbacks(this);
  }

  public static void registerWith(Registrar registrar) {
    ZendeskPlugin plugin = new ZendeskPlugin(registrar.activity());
    plugin.startListening(registrar.messenger());
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    startListening(binding.getBinaryMessenger());
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
    channel = null;
  }

  private void startListening(BinaryMessenger messenger) {
    channel = new MethodChannel(messenger, "com.codeheadlabs.zendesk");
    channel.setMethodCallHandler(methodCallHandler);
    methodCallHandler.setActivity(activity);
    methodCallHandler.setMethodCall(channel);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    methodCallHandler.setActivity(binding.getActivity());
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    methodCallHandler.setActivity(null);
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    methodCallHandler.setActivity(binding.getActivity());
  }

  @Override
  public void onDetachedFromActivity() {
    methodCallHandler.setActivity(null);
  }


  @Override
  public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

  }

  @Override
  public void onActivityStarted(Activity activity) {
    if(activity instanceof ZopimChatActivity){
      isFore=true;
    }
    Log.d("onActivity","onActivityStarted,activity="+activity);
  }

  @Override
  public void onActivityResumed(Activity activity) {
    if(activity instanceof ZopimChatActivity){
      isFore=true;
    }
    Log.d("onActivity","onActivityResumed,activity="+activity);
  }

  @Override
  public void onActivityPaused(Activity activity) {
    if(activity instanceof ZopimChatActivity){
      isFore=false;
      methodCallHandler.getInitCountMessage();
    }
  }

  @Override
  public void onActivityStopped(Activity activity) {
    Log.d("onActivity","onActivityStopped,activity="+activity);
    if(activity instanceof ZopimChatActivity){
      isFore=false;
    }
  }

  @Override
  public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    Log.d("onActivity","onActivitySaveInstanceState,activity="+activity);
  }

  @Override
  public void onActivityDestroyed(Activity activity) {
    Log.d("onActivity","onActivityDestroyed,activity="+activity);
  }
}
