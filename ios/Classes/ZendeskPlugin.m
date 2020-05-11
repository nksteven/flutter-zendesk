#import "ZendeskPlugin.h"

#import <ChatSDK/ChatSDK.h>
#import <ChatProvidersSDK/ChatProvidersSDK.h>
#import <MessagingSDK/MessagingSDK.h>

#define ARGB_COLOR(c) [UIColor colorWithRed:((c>>16)&0xFF)/255.0 green:((c>>8)&0xFF)/255.0 blue:((c)&0xFF)/255.0  alpha:((c>>24)&0xFF)/255.0]


@implementation ZendeskPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"com.codeheadlabs.zendesk"
            binaryMessenger:[registrar messenger]];
  ZendeskPlugin* instance = [[ZendeskPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"init" isEqualToString:call.method]) {
      [ZDKChat initializeWithAccountKey:call.arguments[@"accountKey"] queue:dispatch_get_main_queue()];
    result(@(true));
  } else if ([@"setVisitorInfo" isEqualToString:call.method]) {
      NSString *email = call.arguments[@"email"];
      NSString *phoneNumber = call.arguments[@"phoneNumber"];
      NSString *name = call.arguments[@"name"];
      NSString *note = call.arguments[@"note"];

      ZDKChatAPIConfiguration *chatAPIConfiguration = [[ZDKChatAPIConfiguration alloc] init];
      chatAPIConfiguration.department = @"Department Name";
      if ([phoneNumber isKindOfClass:[NSNull class]]) {
          phoneNumber = @"";
      }
      if ([email isKindOfClass:[NSNull class]]) {
          email = @"";
      }

      if ([name isKindOfClass:[NSNull class]]) {
          name = @"";
      }
      chatAPIConfiguration.visitorInfo = [[ZDKVisitorInfo alloc] initWithName:name
                                                                        email:email
                                                                  phoneNumber:phoneNumber];
      ZDKChat.instance.configuration = chatAPIConfiguration;
      
      result(@(true));
  } else if ([@"startChat" isEqualToString:call.method]) {
      UINavigationController *navVc = [[UINavigationController alloc] init];

      
      // Name for Bot messages
      ZDKMessagingConfiguration *messagingConfiguration = [[ZDKMessagingConfiguration alloc] init];
      messagingConfiguration.name = @"";

      ZDKChatConfiguration *chatConfiguration = [[ZDKChatConfiguration alloc] init];
      chatConfiguration.isPreChatFormEnabled = YES;

      // Build view controller
      NSError *error = nil;
      ZDKChatEngine* chatEngine = [ZDKChatEngine engineAndReturnError: &error];
      UIViewController *viewController = [ZDKMessaging.instance buildUIWithEngines:@[chatEngine]
                                                                           configs:@[messagingConfiguration, chatConfiguration]
                                                                             error:&error];
      // Present view controller
      [navVc pushViewController:viewController animated:YES];

    result(@(true));
  } else if ([@"version" isEqualToString:call.method]) {
      result(@"SDK V2");
  } else {
    result(FlutterMethodNotImplemented);
  }
}

- (void)close:(id)sender {
    [[UIApplication sharedApplication].keyWindow.rootViewController dismissViewControllerAnimated:true completion:nil];
}

@end
