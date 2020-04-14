package com.codeheadlabs.zendesk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

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
public class ZendeskPlugin implements FlutterPlugin, ActivityAware  {

  private MethodChannel channel;
  private MethodCallHandlerImpl methodCallHandler = new MethodCallHandlerImpl();
  private Activity activity;

  public ZendeskPlugin(Activity activity) {
    this.activity=activity;
    initApplicationLifeCallback();
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

  public void initApplicationLifeCallback(){
    activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
      @Override
      public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

      }

      @Override
      public void onActivityStarted(Activity activity) {

      }

      @Override
      public void onActivityResumed(Activity activity) {

      }

      @Override
      public void onActivityPaused(Activity activity) {

      }

      @Override
      public void onActivityStopped(Activity activity) {

      }

      @Override
      public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

      }

      @Override
      public void onActivityDestroyed(Activity activity) {
        Log.d("zendesk","onActivityDestroyed");
        ChatWidgetService.disable();
      }
    });
  }

}
