package io.dcloud.feature.payment;

import io.dcloud.DHInterface.AbsMgr;
import io.dcloud.DHInterface.IFeature;
import io.dcloud.DHInterface.IMgr.MgrEvent;
import io.dcloud.DHInterface.IMgr.MgrType;
import io.dcloud.DHInterface.IWebview;
import io.dcloud.constant.DOMException;
import io.dcloud.util.JSUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.content.Context;

/**
 * 支付Feature实现类
 *
 * @version 1.0
 * @author yanglei Email:yanglei@dcloud.io
 * @Date 2013-5-30 下午06:00:46 created.
 * 
 * <br/>Create By: yanglei Email:yanglei@dcloud.io at 2013-5-30 下午06:00:46
 */
public class PaymentFeatureImpl implements IFeature {
	String mFeatureName;
	AbsMgr mFeatureMgr;
	Context mContext;
	ArrayList<AbsPaymentChannel> mChannels = null;
	@Override
	public String execute(IWebview pWebViewImpl, String pActionName,
			String[] pJsArgs) {
		if("getChannels".equals(pActionName)){
			if(mChannels.isEmpty()){//加载基座配置的支付插件
				loadPaymentChannel(pWebViewImpl);
			}
			//TODO yanglei 获取失败
			String ret = getChannelJsonString();
			String callbackId = pJsArgs[0];
			JSUtil.execCallback(pWebViewImpl, callbackId, ret, JSUtil.OK, true, false);
		}else if("request".equals(pActionName)){//支付接口
			String id = pJsArgs[0];
			AbsPaymentChannel paymentChannel = findPaymentChannel(id);
			String statement = pJsArgs[1];
			String callbackId = pJsArgs[2];
			if(paymentChannel != null){
				paymentChannel.updateWebview(pWebViewImpl);
				paymentChannel.request(statement, callbackId);
			}else{
				String msg = String.format(DOMException.JSON_ERROR_INFO, 62009,"not found channel");
				JSUtil.execCallback(pWebViewImpl, callbackId, msg, JSUtil.ERROR, true, false);
			}
		}else if("installService".equals(pActionName)){//安装支付模块所需的安全服务
			String id = pJsArgs[0];
			AbsPaymentChannel paymentChannel = findPaymentChannel(id);
			if(paymentChannel != null){
				paymentChannel.updateWebview(pWebViewImpl);
				paymentChannel.installService();
			}
		}
		return null;
	}

	@Override
	public void init(AbsMgr pFeatureMgr, String pFeatureName) {
		mFeatureName = pFeatureName;
		mFeatureMgr = pFeatureMgr;
		mContext = pFeatureMgr.getContext();
		mChannels = new ArrayList<AbsPaymentChannel>(2);
	}
	/**
	 * 加载支付插件信息
	 * @param pWebViewImpl
	 * <br/>Create By: yanglei Email:yanglei@dcloud.io at 2013-5-30 下午04:15:30
	 */
	private void loadPaymentChannel(IWebview pWebViewImpl){
		HashMap<String, String> channels = (HashMap<String, String>)mFeatureMgr.processEvent(MgrType.FeatureMgr, MgrEvent.OBTAIN_FEATURE_EXT_HASHMAP, mFeatureName);
		if(channels != null && !channels.isEmpty()){
			Set<String> keys = channels.keySet();
			for(String key : keys){
				String value = channels.get(key);
				AbsPaymentChannel pc;
				try {
					Object obj = Class.forName(value).newInstance();
					if(obj instanceof AbsPaymentChannel){
						pc = (AbsPaymentChannel)obj;
						pc.init(mContext);
						pc.id = key;
						mChannels.add(pc);
					}
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String getChannelJsonString(){
		StringBuffer ret = new StringBuffer("[");
		int size = mChannels.size();
		for(int i = 0; i < size; i++){
			AbsPaymentChannel channel  = mChannels.get(i);
			ret.append(channel.toJsonString());
			if(i != size - 1){
				ret.append(",");
			}
		}
		ret.append("]");
		return ret.toString();
	}
	
	private AbsPaymentChannel findPaymentChannel(String pId){
		for(AbsPaymentChannel ret : mChannels){
			if(ret.id.equals(pId)){
				return ret;
			}
		}
		return null;
	}
	@Override
	public void dispose(String pAppid) {
	}

}
