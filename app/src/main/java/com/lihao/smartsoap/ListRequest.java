package com.lihao.smartsoap;

import java.io.IOException;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;

import com.dzyd.auto_office_service_util.PageSet;
import com.dzyd.auto_office_util.AnalysisUtil;

public class ListRequest extends Request<List<SoapObject>> {

	private String colName;
	private String order;
	private String where;
	private int pageSize = 40;
	private int curPage;
	private PageSet pageSet;
	private String strOrder;
	private ListRequestListener<List<SoapObject>> mListener;

	public ListRequest(Context context, String url, String method, String colName, String order, String where, int curPage, ListRequestListener<List<SoapObject>> listener) {
		this.url = url;
		this.context = context;
		this.method = method;
		this.colName = colName;
		this.order = order;
		this.where = where;
		mListener = listener;
	}

	

	public void setPageSize(int num) {
		if (num > 0) {
			this.pageSize = num;
		}
	}

	@Override
	public Boolean call() {
		rpc = new SoapObject(NAMESPACE, method);
		if (!colName.equals("")) {
			strOrder = "order by " + colName + " " + order;
		} else {
			strOrder = "";
		}
		pi = new PropertyInfo();
		pageSet = getpagetset(pageSize, curPage);
		rpc = getPropertyInfo(pi, rpc, where);
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		if (needHeader) {
			headers = setdata(type);
			envelope.headerOut = headers;
		} else {
			envelope.headerOut = null;
		}

		envelope.headerOut = headers;
		envelope.dotNet = true;
		envelope.setOutputSoapObject(rpc);
		HttpTransportSE ht = new HttpTransportSE(url);
		envelope.addMapping(NAMESPACE, "pageSet", pageSet.getClass());
		try {
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
					mListener.onSuccess(AnalysisUtil.getDataList(envelope));
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

	private SoapObject getPropertyInfo(PropertyInfo pi, SoapObject rpc, String strWhere) {
		pi.setName("colList");
		pi.setValue("*");
		rpc.addProperty(pi);
		pi = new PropertyInfo();
		pi.setName("strWhere");
		pi.setValue(strWhere);
		rpc.addProperty(pi);
		pi = new PropertyInfo();
		pi.setName("strOrder");
		pi.setValue(strOrder);
		rpc.addProperty(pi);
		pi = new PropertyInfo();
		pi.setName("myPageSet");
		pi.setValue(pageSet);
		pi.setType(pageSet.getClass());
		rpc.addProperty(pi);
		return rpc;
	}

	private PageSet getpagetset(int pageSize, int curPage) {
		pageSet = new PageSet();
		pageSet.setProperty(0, pageSize);
		pageSet.setProperty(1, curPage);
		pageSet.setProperty(2, 0);
		pageSet.setProperty(3, 0);
		return pageSet;
	}

}
