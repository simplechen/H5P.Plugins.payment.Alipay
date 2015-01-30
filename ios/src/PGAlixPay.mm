/*
 *------------------------------------------------------------------
 *  pandora/feature/PGShare
 *  Description:
 *    上传插件实现定义
 *      负责和js层代码交互，js native层对象维护
 *  DCloud Confidential Proprietary
 *  Copyright (c) Department of Research and Development/Beijing/DCloud.
 *  All Rights Reserved.
 *
 *  Changelog:
 *	number	author	modify date modify record
 *   0       xty     2013-03-22 创建文件
 *------------------------------------------------------------------
 */
#import "PGAlixPay.h"
#import "PDRCore.h"
#import <AlipaySDK/AlipaySDK.h>

@implementation PGAlixPay

@synthesize callBackID;

- (id)init {
    if ( self = [super init] ) {
        self.type = @"alipay";
        self.description = @"支付宝";
        [[NSNotificationCenter defaultCenter] addObserver:self
                                                 selector:@selector(handleOpenURL:)
                                                     name:PDRCoreOpenUrlNotification
                                                   object:nil];
    }
    return self;
}

- (void)request:(PGMethod*)command {
    
    NSString *cbID = [command.arguments objectAtIndex:2];
    NSString *arg1 = [command.arguments objectAtIndex:1];
    NSString *orderString = nil;
    NSString *applicationScheme = nil;
    
    PDRPluginResult *result = nil;
    //int errorCode = PGPayErrorOther;
    if ( self.callBackID ) {
        result = [PDRPluginResult resultWithStatus:PDRCommandStatusError
                              messageToErrorObject:PGPayErrorOther];
        [self toCallback:cbID withReslut:[result toJSONString]];
        return;
    }
    
    if ( [arg1 isKindOfClass:NSString.class] ) {
        NSArray *urlSchemes = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleURLTypes"];
        for ( NSDictionary *shemeItem in urlSchemes ) {
            NSString *URLIdentifier = [shemeItem objectForKey:@"CFBundleURLName"];
            if ( NSOrderedSame == [@"alixpay" caseInsensitiveCompare:URLIdentifier] ) {
                NSArray *appids = [shemeItem objectForKey:@"CFBundleURLSchemes"];
                applicationScheme = [appids objectAtIndex:0];
                break;
            }
        }
        if ( nil == applicationScheme ) {
            result = [PDRPluginResult resultWithStatus:PDRCommandStatusError
                                  messageToErrorObject:PGPayErrorConfig];
            [self toCallback:cbID withReslut:[result toJSONString]];
            return;
        }
        orderString = arg1;
        self.callBackID = [cbID isKindOfClass:[NSString class]]?cbID:nil;
        
        [[AlipaySDK defaultService] payOrder:orderString fromScheme:applicationScheme callback:^(NSDictionary *resultDic) {
            NSLog(@"reslut = %@",resultDic);
            [self handleAlixPayResult:resultDic];
        }];

        
        //[AlixLibService payOrder:orderString AndScheme:applicationScheme seletor:@selector(paymentResult:) target:self];
      /*
        int retCode = [_alixPay pay:statement applicationScheme:applicationScheme];
        switch (retCode) {
            case kSPErrorAlipayClientNotInstalled:
                errorCode = PGPayErrorNotInstall;
                break;
            case kSPErrorSignError:
                errorCode = PGPayErrorBadParam;
            case kSPErrorOK:
                self.callBackID = cbID;
                errorCode = PGPayErrorNO;
                break;
            default:
                break;
        }
    }
    if ( errorCode != PGPayErrorNO) {
        result = [PDRPluginResult resultWithStatus:PDRCommandStatusError
                              messageToErrorObject:errorCode];
        [self toCallback:cbID withReslut:[result toJSONString]];
    }*/
    }
}
//wap回调函数
/*
-(void)paymentResult:(NSString *)resultd
{
    AlixPayResult* result = [[[AlixPayResult alloc] initWithString:resultd] autorelease];
	if (result){
        [self handleAlixPayResult:result];
		if (result.statusCode == 9000){
			用公钥验证签名 严格验证请使用result.resultString与result.signString验签
 
            //交易成功
            //NSString* key = AlipayPubKey;//签约帐户后获取到的支付宝公钥
			//id<DataVerifier> verifier;
            //verifier = CreateRSADataVerifier(key);
            
			//if ([verifier verifyString:result.resultString withSign:result.signString]){
                //验证签名成功，交易结果无篡改
			//}
        } else {
            //交易失败
        }
    }   else  {
        //失败
    }
}

- (AlixPayResult *)resultFromURL:(NSURL *)url {
    if (url != nil && [[url host] compare:@"safepay"] == 0) {
		NSString * query = [[url query] stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        return [[[AlixPayResult alloc] initWithString:query] autorelease];
	}
    return nil;
}
*/
-(void)handleAlixPayResult:(NSDictionary*)resultDic {
    int errorCode = PGPayErrorOther;
    NSString *resultString = [resultDic objectForKey:@"memo"];
    NSUInteger statusCode = [[resultDic objectForKey:@"resultStatus"] integerValue];
    switch (statusCode) {
        case 9000:
        {
            errorCode = PGPayErrorNO;
            NSDictionary *dict = [NSDictionary
                                  dictionaryWithObjectsAndKeys:self.type,@"channel",
                                  @"", @"tradno",
                                  resultString ? resultString : @"", @"description",
                                   @"" , @"signature",
                                  @"", @"url", nil];
            PDRPluginResult *result = [PDRPluginResult resultWithStatus:PDRCommandStatusOK messageAsDictionary:dict];
            [self toCallback:self.callBackID withReslut:[result toJSONString]];
        }
            break;
        case 4000:
            break;
        case 6001:
            errorCode = PGPayErrorUserCancel;
            break;
        case 6002:
            errorCode = PGPayErrorNet;
            break;
        case 8000:
            break;
        default:
            break;
    }
    if ( errorCode != PGPayErrorNO) {
        PDRPluginResult *result = [PDRPluginResult resultWithStatus:PDRCommandStatusError messageToErrorObject:errorCode withMessage:resultString];
        [self toCallback:self.callBackID withReslut:[result toJSONString]];
    }
    self.callBackID = nil;
}

- (void)handleOpenURL:(NSNotification*)notification {
     NSURL *url = [notification object];
    if ([url.host isEqualToString:@"safepay"]) {
        [[AlipaySDK defaultService]
         processOrderWithPaymentResult:url
         standbyCallback:^(NSDictionary *resultDic) {
             [self handleAlixPayResult:resultDic];
             NSLog(@"result = %@", resultDic);
         }];
    }
    
   // NSURL *url = [notification object];
  //  AlixPayResult *payResult = [self resultFromURL:url];
  //  [self handleAlixPayResult:payResult];
}

- (void)installService {
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString:@"itms-apps://itunes.apple.com/cn/app/kuai-jie-zhi-fu/id535715926?mt=8"]];
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self
                                                    name:PDRCoreOpenUrlNotification
                                                  object:nil];
    self.callBackID = nil;
    [super dealloc];
}

@end
