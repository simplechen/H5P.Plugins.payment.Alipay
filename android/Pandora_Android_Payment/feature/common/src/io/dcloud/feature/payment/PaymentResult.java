package io.dcloud.feature.payment;

public final class PaymentResult {
	AbsPaymentChannel channel;
	public String url;
	public String signature;
	public String tradeno;
	public String description;
	
	public PaymentResult(AbsPaymentChannel pChannel){
		channel = pChannel;
	}
	@Override
	public String toString() {
		String ret = "{channel:%s,description:'%s',url:'%s',signature:'%s',tradeno:'%s'}";
		return String.format(ret, channel.toJsonString(),description,url,signature,tradeno);
	}
}
