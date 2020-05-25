#import "ZendeskPlugin.h"

#import <ChatSDK/ChatSDK.h>
#import <ChatProvidersSDK/ChatProvidersSDK.h>
#import <MessagingSDK/MessagingSDK.h>
#import <MessagingAPI/MessagingAPI.h>
#import <SDKConfigurations/SDKConfigurations.h>
#import <ZendeskCoreSDK/ZendeskCoreSDK.h>
#import <SupportSDK/SupportSDK.h>

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
      [ZDKChat.connectionProvider connect];
    result(@(true));
  } else if ([@"setVisitorInfo" isEqualToString:call.method]) {
      NSString *email = call.arguments[@"email"];
      NSString *phoneNumber = call.arguments[@"phoneNumber"];
      NSString *name = call.arguments[@"name"];
      NSString *note = call.arguments[@"note"];

      ZDKChatAPIConfiguration *chatAPIConfiguration = [[ZDKChatAPIConfiguration alloc] init];
      chatAPIConfiguration.department = @"";
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
      NSNumber *navigationBarColor = call.arguments[@"iosNavigationBarColor"];
      NSNumber *navigationTitleColor = call.arguments[@"iosNavigationTitleColor"];
      
      
      
      UINavigationController *navVc = [[UINavigationController alloc] init];
      navVc.navigationBar.translucent = NO;
      navVc.navigationBar.barTintColor = ARGB_COLOR([navigationBarColor integerValue]);//
      navVc.navigationBar.titleTextAttributes = @{
                                                           NSForegroundColorAttributeName: ARGB_COLOR([navigationTitleColor integerValue])
                                                           };
      
      // Name for Bot messages
      ZDKMessagingConfiguration *messagingConfiguration = [[ZDKMessagingConfiguration alloc] init];
      messagingConfiguration.name = @"";
      NSError *error = nil;
      
      
      ZDKChatConfiguration *chatConfiguration = [[ZDKChatConfiguration alloc] init];
      chatConfiguration.isPreChatFormEnabled = NO;
      chatConfiguration.isAgentAvailabilityEnabled = YES;
      chatConfiguration.chatMenuActions = @[@(ZDKChatMenuActionEmailTranscript), @(ZDKChatMenuActionEndChat)];
      ZDKChatFormConfiguration *formConfiguration = [[ZDKChatFormConfiguration alloc] initWithName:ZDKFormFieldStatusRequired
                                                                                             email:ZDKFormFieldStatusOptional
                                                                                       phoneNumber:ZDKFormFieldStatusHidden
                                                                                        department:ZDKFormFieldStatusRequired];
      chatConfiguration.preChatFormConfiguration = formConfiguration;
      // Build view controller
      ZDKChatEngine* chatEngine = [ZDKChatEngine engineAndReturnError: &error];
      UIViewController *viewController = [ZDKMessaging.instance buildUIWithEngines:@[chatEngine]
                                                                           configs:@[messagingConfiguration, chatConfiguration]
                                                                             error:&error];
      // Present view controller
      [navVc pushViewController:viewController animated:YES];
      
      
      UIViewController *rootVc = [UIApplication sharedApplication].keyWindow.rootViewController ;
      [rootVc presentViewController:navVc
                           animated:true
                         completion:^{
                             UIBarButtonItem *back = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"Back", comment: @"")
                                                                                      style:UIBarButtonItemStylePlain
                                                                                     target:self
                                                                                     action:@selector(close:)];
          [back setTitleTextAttributes:@{ NSForegroundColorAttributeName: ARGB_COLOR([navigationTitleColor integerValue])} forState:UIControlStateNormal];
          
          navVc.topViewController.navigationItem.leftBarButtonItem = back;
                             
                         }];
      [ZDKCoreLogger setEnabled:YES];
      [ZDKCoreLogger setLogLevel:ZDKLogLevelDebug];

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
