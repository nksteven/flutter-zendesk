import 'dart:async';
import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class Zendesk {
  static const MethodChannel _channel =
      const MethodChannel('com.codeheadlabs.zendesk');

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
    Color iosNavigationBarColor = const Color.fromRGBO(245, 245, 245, 255),
    Color iosNavigationTitleColor = Colors.black,
    Color iosNavigationBackButtonTitleColor =
        const Color.fromRGBO(10, 93, 247, 255),
  }) async {
    await _channel.invokeMethod('startChat', {
      'iosNavigationBarColor': iosNavigationBarColor?.value,
      'iosNavigationTitleColor': iosNavigationTitleColor?.value,
      'iosNavigationBackButtonTitleColor':
          iosNavigationBackButtonTitleColor?.value,
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
}
