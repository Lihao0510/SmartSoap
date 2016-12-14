package com.lihao.smartsoap;

import java.io.IOException;
import java.util.Map;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;

public class SoapRequest extends Request<SoapObject> {

	private Map<String, Object> param;
	private SoapRequestListener<SoapObject> mListener;

	public SoapRequest(Context context, String url, String method, Map<String, Object> param, SoapRequestListener<SoapObject> listener) {
		this.url = url;
		this.context = context;
		this.method = method;
		this.param = param;
		mListener = listener;
	}

	@Override
	public Boolean call() {
		try {
			rpc = new SoapObject(NAMESPACE, method);
			pi = new PropertyInfo();
			if (param != null) {
				for (Map.Entry<String, Object> entry : param.entrySet()) {
					pi = new PropertyInfo();
					pi.setName(entry.getKey());
					pi.setValue(param.get(entry.getKey()));
					rpc.addProperty(pi);
				}
			}
			envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			if (needHeader) {
				headers = setdata(type);
				envelope.headerOut = headers;
			} else {
				envelope.headerOut = null;
			}
			envelope.bodyOut = rpc;
			envelope.dotNet = true;
			envelope.setOutputSoapObject(rpc);
			HttpTransportSE ht = new HttpTransportSE(url);
			ht.call(NAMESPACE + method, envelope);
		} catch (final IOException e) {
			((Activity) context).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mListener.onError(e.getMessage());
				}
			});
			e.printStackTrace();
			return false;
		} catch (final XmlPullParserException e) {
			((Activity) context).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mListener.onError(e.getMessage());
				}
			});
			e.printStackTrace();
			return false;
		}
		if (envelope.bodyIn != null) {
			((Activity) context).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mListener.onSuccess((SoapObject) envelope.bodyIn);
				}
			});
		} else {
			((Activity) context).runOnUiThread(new Runnable() {

				@Override
				public void run() {
					mListener.onError("返回结果为空,有可能是WebService服务端错误!");
				}
			});
		}
		return true;
	}

}
