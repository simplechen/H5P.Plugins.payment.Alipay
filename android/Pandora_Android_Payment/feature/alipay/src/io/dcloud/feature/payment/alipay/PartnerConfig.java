package io.dcloud.feature.payment.alipay;


public class PartnerConfig {

	// 合作商户ID。用签约支付宝账号登录ms.alipay.com后，在账户信息页面获取。
	public static final String PARTNER = "2088801273866834";
	// 商户收款的支付宝账号
	public static final String SELLER = "alipay@d-heaven.com";
	// 商户（RSA）私钥
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBALhcTT9eL3MvxSA+7rYvel3UyfU8ORPm+3KjMA2SojdcFXu2/LlCCvEBjccl2dB8Sgf9zkaXeWDRPAJxmpF6+Ao8O28k4ETbm1RKx8s/G8phugr0o8WKEe7RBEfB6cuXzU/r2Yy7bIm8kl+kD3NMdo4ZoaTAukN31IxrcW7DRk7bAgMBAAECgYEAqouoYiBs4K+mOcg954dQOZDpKtWFL6YTod7YSxYEvhWPQhzq+S3hFUYMs9eOAVGY4n+l3KOvgyLL0q7o//+zh6G4mrsEjBV4wmgKR2jRcaGkMJ6lkwuL2utgI2Sa7DuBh4P0FXRCZUU+QPcPtYMZlN1SYw4+c5qQRNZDM0w1HHECQQDmvfqRuAr3JpADzhnZA+aWvI3y7qlEjzeWD5Ue9IWFQBu2+S0Zk67cloceKJfMNSL+SsnRJmZdJHIA86VT9dlfAkEAzIqV9dwHWQFLs5HPhK2lo9SYbudCbBwjW5NFPhH1ZuL1+dRD/30D4u7j013w6idslLVugUbRjiscQTpUClDwBQJADTeQpMwwBJw236DsphutF7FRSDsXgw/ZFUVrO0yyYUGc1MxNIwvslkIhFz8oAtCAYqSSDuLuNiOiTB+otrh8bQJAbcs6YgYg35MFerebSCIaFs4ooOd8TSHxjNeJxJ7JNtg/p0Jc2cjWjDslijET/+z3qP+W+Mf19rWHbjAMKpl1rQJAA+th/o8039oJv4Lfdpml3q/DzWlHNbPLDkfjZqR+4ix/fxwczb6wOV5SBDD2OZF0hpZgAEw+KNqey5j7dc4rHQ==";
	// 支付宝（RSA）公钥 用签约支付宝账号登录ms.alipay.com后，在密钥管理页面获取。
	public static final String RSA_ALIPAY_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDBPnfXasQC//Nn3FG7S+nRiDmtk2EHJ/Db0ABU 8F7gVyoqVZnYiSbl4SK1ne8cp3AwQEYEofIVIw8E1Z4W/LohziUG3I0r5gmGNeQr7m28HxYk9jV1 TisNUWE/1iAvnO1pAb5xd8TKOB5Pl6Y5tb6ybY4CbYXry26qDfTqxcrniwIDAQAB";
	// 支付宝安全支付服务apk的名称，必须与assets目录下的apk名称一致
	public static final String ALIPAY_PLUGIN_NAME = "alipay_plugin_20120428msp.apk";

}