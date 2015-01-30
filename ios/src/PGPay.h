/*
 *------------------------------------------------------------------
 *  pandora/PGShare.h
 *  Description:
 *      上传插件头文件定义
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

#import "PGPlugin.h"
#import "PGMethod.h"

typedef NS_ENUM(NSInteger, PGPayError) {
    PGPayErrorNO = 0, //ok
    PGPayErrorNotInstall = 62000, //客户端未安装支付通道依赖的服务
    PGPayErrorUserCancel = 62001, //此设备不支持支付
    PGPayErrorNotSupport = 62002, //此设备不支持支付
    PGPayErrorBadFormat = 62003, //数据格式错误
    PGPayErrorAccoutStatus = 62004, //支付账号状态错误
    PGPayErrorBadParam = 62005, //订单信息错误
    PGPayErrorPayInner = 62006, //支付操作内部错误
    PGPayErrorPayServer = 62007, //支付服务器错误
    PGPayErrorNet = 62008, // 网络问题引起的错误
    PGPayErrorOther = 62009, //其它未定义的错误
    PGPayErrorConfig = 62010 // 配置错误
};

@interface  PGPay : PGPlugin {
}
@property(nonatomic, copy)NSString* type;
@property(nonatomic, copy)NSString *description;
@property(nonatomic, assign)BOOL serviceReady;
- (NSDictionary*)JSDict;
- (void)request:(PGMethod*)command;
- (void)installService;
@end