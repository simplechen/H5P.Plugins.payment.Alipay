# H5P.Plugins.payment.Alipay
该工程为HTML5 plus 支付宝插件实现,参见https://github.com/dcloudio/H5P.Core
# 目录结构
 Android AndroidNative层实现  
 iOS iOSNative层实现  
 js JS Api实现  
#集成步骤
准备工作:  
1. 下载运行环境https://github.com/dcloudio/H5P.Core  
2. 到支付宝开发平台获取SDK  
iOS配置:  
1. 使用XCode打开Pandora.xcodeproj,新建类型为CocoaTouch Static Library的 target  
2. 将本工程中src添加到新建的target  
3. Pandora.xcodeproj 引入新建target依赖 ,添加支付包SDK

License
-------
遵循MIT License；
