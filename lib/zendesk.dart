import 'dart:async';
import 'dart:ui';

import 'package:flutter/services.dart';

typedef Future<dynamic> UnReadListener(int unRead);

class Zendesk {
  static const MethodChannel _channel =
  const MethodChannel('com.codeheadlabs.zendesk');

  UnReadListener _unReadListener;

  Future<void> init(String accountKey,
      {String department, String appName}) async {
    await _channel.invokeMethod('init', <String, String>{
      'accountKey': accountKey,
      'department': department,
      'appName': appName,
    });
  }

  Future<void> setVisitorInfo(
      {String name, String email, String phoneNumber, String note}) async {
    await _channel.invokeMethod('setVisitorInfo', <String, String>{
      'name': name,
      'email': email,
      'phoneNumber': phoneNumber,
      'note': note,
    });
  }

  Future<void> setToken(String token) async {
    await _channel.invokeMethod('setToken', <String, String>{
      'firebase_token': token,
    });
  }

  Future<void> startChat({
    Color iosNavigationBarColor,
    Color iosNavigationTitleColor,
  }) async {
    await _channel.invokeMethod('startChat', {
      'iosNavigationBarColor': iosNavigationBarColor?.value,
      'iosNavigationTitleColor': iosNavigationTitleColor?.value,
    });
  }

  Future<bool> checkSystemAlertPermission() async {
    return _channel.invokeMethod<bool>('checkSystemAlertPermission');
  }

  Future<String> closeSystemAlert() async {
    return _channel.invokeMethod<String>('closeChatWidget');
  }

  Future<String> openSystemAlert() async {
    return _channel.invokeMethod<String>('openSystemAlert');
  }

  Future<String> version() async {
    return _channel.invokeMethod<String>('version');
  }

  void addUnreadListener(UnReadListener unReadListener) {
    _unReadListener = unReadListener;
    _channel.setMethodCallHandler(_handleMethod);
  }

  Future<dynamic> _handleMethod(MethodCall call) async {
    switch (call.method) {
      case "UnreadListener":
        int unRead = call.arguments;
        if (_unReadListener != null) {
          _unReadListener(unRead);
        }
    }
  }

  Future<void> onReceiveMessage(String data) async {
    print("flutter:onReceiveMessage");
    await _channel.invokeMethod('onReceivedChatMessage',{
      "data":data,
    });
  }
}
