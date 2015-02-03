package io.dcloud.feature.payment.alipay;

import io.dcloud.feature.payment.AbsPaymentChannel;
import io.dcloud.feature.payment.IPaymentListener;
import io.dcloud.feature.payment.PaymentResult;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AliPay extends AbsPaymentChannel {
	
	boolean DEBUG = false;
	static String TAG = "AliPay";
	@Override
	protected void request(String pStatement) {
		// check to see if the MobileSecurePay is already installed.
		// 检测安全支付服务是否安装
		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(mContext);
		if(mspHelper.detectMobile_sp()){//必须在支付之前进行检测
			serviceReady = true;
			// retrieve and show the product list.
			// 显示商品列表
			if(DEBUG){
				AliDemo aliDemo = new AliDemo();
				int index = 0;
				pStatement = aliDemo.getOrderInfo(index);
			}
			try {
				Log.v("orderInfo:", pStatement);
				// 调用pay方法进行支付
				MobileSecurePayer msp = new MobileSecurePayer();
				boolean bRet = msp.pay(pStatement, mHandler, AlixId.RQF_PAY, mWebview.getActivity());
			} catch (Exception ex) {
				mListener.onError(IPaymentListener.CODE_UNKNOWN, ex.getMessage());
			}
		}else{
			mListener.onError(IPaymentListener.CODE_NO_INSTALL_MOBILE_SP, null);
		}
	}

	
	@Override
	public void init(Context context) {
		super.init(context);
		description = "支付宝";
	}

	@Override
	public String toJsonString() {
		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(mContext);
		serviceReady = mspHelper.detectMobile_sp();//检测是否安装了安全支付服务,交给super.toJsonString()方法进行拼接
		return super.toJsonString();
	}
	//
	// the handler use to receive the pay result.
	// 这里接收支付结果，支付宝手机端同步通知
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			try {
				String strRet = (String) msg.obj;

				Log.e(TAG, strRet);	// strRet范例：resultStatus={9000};memo={支付成功};result={partner="2088201564809153"&seller="2088201564809153"&out_trade_no="050917083121576"&subject="123456"&body="2010新款NIKE 耐克902第三代板鞋 耐克男女鞋 386201 白红"&total_fee="0.01"&notify_url="http://notify.java.jpxx.org/index.jsp"&success="true"&sign_type="RSA"&sign="d9pdkfy75G997NiPS1yZoYNCmtRbdOP0usZIMmKCCMVqbSG1P44ohvqMYRztrB6ErgEecIiPj9UldV5nSy9CrBVjV54rBGoT6VSUF/ufjJeCSuL510JwaRpHtRPeURS1LXnSrbwtdkDOktXubQKnIMg2W0PreT1mRXDSaeEECzc="}
				switch (msg.what) {
				case AlixId.RQF_PAY: {
					//
					BaseHelper.log(TAG, strRet);
					// 处理交易结果
					try {
						// 获取交易状态码，具体状态代码请参看文档
						String tradeStatus = "resultStatus={";
						int imemoStart = strRet.indexOf("resultStatus=");
						imemoStart += tradeStatus.length();
						int imemoEnd = strRet.indexOf("};memo=");
						tradeStatus = strRet.substring(imemoStart, imemoEnd);
						{
							if(tradeStatus.equals("9000")){//判断交易状态码，只有9000表示交易成功
								String memo = "memo={";
								imemoStart = strRet.indexOf("memo={");
								imemoStart += memo.length();
								imemoEnd = strRet.indexOf("};result");
								memo = strRet.substring(imemoStart, imemoEnd);
								
								String result = "result={";
								imemoStart = strRet.indexOf("result={");
								imemoStart += result.length();
								imemoEnd = strRet.length() - 1;
								result = strRet.substring(imemoStart, imemoEnd);
								
								String url = null,signature = null,tradeno=null;
								String[] params = result.split("\\&");
								if(params != null && params.length > 0){
									int len = params.length;
									for(int i = 0; i < len; i++){
										String param = params[i];
										if(param.indexOf("=") > 0){
											if(param.indexOf("sign=\"") >= 0){//signature需要特殊处理，因为字符串中含有‘=’字符
												signature = param.substring("sign=\"".length(), param.length() - 1);//除去值前后的‘"’字符
											}else{
												String[] params0 = param.split("\\=");
												if(params0.length >= 2){
													if("notify_url".equals(params0[0])){
														url = params0[1].substring(1, params0[1].length() - 1);//除去值前后的‘"’字符
													}else if("out_trade_no".equals(params0[0])){
														tradeno = params0[1].substring(1, params0[1].length() - 1);//除去值前后的‘"’字符
													}
												}
											}
										}
									}
								}
								
								
								PaymentResult pr = new PaymentResult(AliPay.this);
								pr.description = memo;
								pr.url = url;
								pr.tradeno = tradeno;
								pr.signature = signature;
								mListener.onSuccess(pr);
							}else{
								int state = IPaymentListener.CODE_UNKNOWN;
								String error_msg = null;
								if(tradeStatus.equals("4000")){//系统异常
									state = IPaymentListener.CODE_UNKNOWN;
								}else if(tradeStatus.equals("4001")){//数据格式不正确
									state = IPaymentListener.CODE_DATA_ERROR;
								}else if(tradeStatus.equals("4003")){//该用户绑定的支付宝账户被冻结或不允许支付
									state = IPaymentListener.CODE_ACCOUNT_STATUS_ERROR;
								}else if(tradeStatus.equals("4004")){//该用户已解除绑定
									state = IPaymentListener.CODE_ACCOUNT_STATUS_ERROR;
								}else if(tradeStatus.equals("4005")){//绑定失败或没有绑定
									state = IPaymentListener.CODE_ACCOUNT_STATUS_ERROR;
								}else if(tradeStatus.equals("4006")){//订单支付失败
									state = IPaymentListener.CODE_PAY_OPTION_ERROR;
								}else if(tradeStatus.equals("4010")){//重新绑定账户。
									state = IPaymentListener.CODE_ACCOUNT_STATUS_ERROR;
								}else if(tradeStatus.equals("6000")){//支付服务正在进行升级操作。
									state = IPaymentListener.CODE_PAY_SERVER_ERROR;
								}else if(tradeStatus.equals("6001")){//用户中途取消支付操作。
									state = IPaymentListener.CODE_USER_CANCEL;
								}else if(tradeStatus.equals("6002")){//网络连接异常。
									state = IPaymentListener.CODE_NETWORK_ERROR;
								}
								mListener.onError(state, error_msg);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
						mListener.onError(IPaymentListener.CODE_UNKNOWN, e.getMessage());
					}
				}
					break;
				}

				super.handleMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	@Override
	protected void installService() {
		MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(mContext);
		mspHelper.installMobile_sp();
	}
	
	class AliDemo{
		
		AliDemo(){
			initProductList();
		}

		ArrayList<Products.ProductDetail> mproductlist;
		/**
		 * retrieve the product list. 设置商品列表
		 */
		void initProductList() {
			Products products = new Products();
			this.mproductlist = products.retrieveProductInfo();
		}
		
		String getOrderInfo(int index){
			// prepare the order info.
			// 准备订单信息
			String orderInfo = getOrderInfo0(index);
			// 这里根据签名方式对订单信息进行签名
			String signType = getSignType();
			String strsign = sign(signType, orderInfo);
			Log.v("sign:", strsign);
			// 对签名进行编码
			strsign = URLEncoder.encode(strsign);
			// 组装好参数
			String info = orderInfo + "&sign=" + "\"" + strsign + "\"" + "&"
					+ getSignType();
			return info;
		}
		private String getOrderInfo0(int position) {
			String strOrderInfo = "partner=" + "\"" + PartnerConfig.PARTNER + "\"";
			strOrderInfo += "&";
			strOrderInfo += "seller=" + "\"" + PartnerConfig.SELLER + "\"";
			strOrderInfo += "&";
			strOrderInfo += "out_trade_no=" + "\"" + getOutTradeNo() + "\"";
			strOrderInfo += "&";
			strOrderInfo += "subject=" + "\"" + mproductlist.get(position).subject
					+ "\"";
			strOrderInfo += "&";
			strOrderInfo += "body=" + "\"" + mproductlist.get(position).body + "\"";
			strOrderInfo += "&";
			strOrderInfo += "total_fee=" + "\""
					+ mproductlist.get(position).price.replace("一口价:", "") + "\"";
			strOrderInfo += "&";
			strOrderInfo += "notify_url=" + "\""
					+ "http://notify.java.jpxx.org/index.jsp" + "\"";
			
			return strOrderInfo;
		}

		/**
		 * get the out_trade_no for an order. 获取外部订单号
		 * 
		 * @return
		 */
		String getOutTradeNo() {
			SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss");
			Date date = new Date();
			String strKey = format.format(date);

			java.util.Random r = new java.util.Random();
			strKey = strKey + r.nextInt();
			strKey = strKey.substring(0, 15);
			return strKey;
		}

		//
		//
		/**
		 * sign the order info. 对订单信息进行签名
		 * 
		 * @param signType
		 *            签名方式
		 * @param content
		 *            待签名订单信息
		 * @return
		 */
		String sign(String signType, String content) {
			return Rsa.sign(content, PartnerConfig.RSA_PRIVATE);
		}

		/**
		 * get the sign type we use. 获取签名方式
		 * 
		 * @return
		 */
		String getSignType() {
			String getSignType = "sign_type=" + "\"" + "RSA" + "\"";
			return getSignType;
		}

	}
}
