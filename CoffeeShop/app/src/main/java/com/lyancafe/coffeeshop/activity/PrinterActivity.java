package com.lyancafe.coffeeshop.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


import com.lyancafe.coffeeshop.R;
import com.lyancafe.coffeeshop.bean.ItemContentBean;
import com.lyancafe.coffeeshop.bean.OrderBean;
import com.lyancafe.coffeeshop.helper.OrderHelper;
import com.lyancafe.coffeeshop.utils.ToastUtil;
import com.xls.http.HttpUtils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrinterActivity extends Activity {

	private static final String TAG = "print";
	public static final String ORDERS = "Orders";
	public static final String ORDERS_INFO = "Orders_Info";
	public static final String ORDER_ID = "Order_Id";
	public static final String ORDER_LAT = "Order_Lat";
	public static final String ORDER_LNG = "Order_Lng";
	public static final String ORDER_SN = "Order_Sn";
	public static final String PHONE_NUM = "Phone_Num";
	public static final String ADDRESS = "Address";
	public static final String ORDER_LIST = "Order_List";
	public static final String TOTAL_QUANTITY = "Total_Quantity";
	private Button printBtn;
	private Button printBtn2;
	private Button pingBtn;
	private String phoneNum = "";
	private String orderSn = "";
	private String address = "";
	private String cupCount = ""; //盒中的杯数
	private String boxCount = ""; //一个订单打包的盒子数
	private String currBox =""; //当前盒子是此单对应的第几个盒子
	private String productName = "";
	private String recipient = "";
	private String courierName = "";
	private String courierPhone = "";
	private int totalQuantity = 0;
	private String order1 = "";
	private String order2 = "";
	private String order3 = "";
	private String order4 = "";
	private List<ItemContentBean> orderItemList = null;
	private String printer1add = "192.168.1.231";
	private String printer2add = "192.168.1.232";
	private List<String> toBePrintedList = new ArrayList<String>();
	private boolean PrinterIsAvailable = true;
	private boolean toBePrintedReady = false;
	private Context mContext;

	private static final int MSG_PING = 66;
	private static final int MSG_EXCEPTION = 67;

	private void genToBePrintedList(List<ItemContentBean> orderItemList) {

		for (int i = 0; i < orderItemList.size(); i++) {
			for (int j = 0; j < orderItemList.get(i).getQuantity();j++){
				toBePrintedList.add(orderItemList.get(i).getProduct());
			}
		}
		toBePrintedReady = true;


	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(HttpUtils.BASE_URL.contains("test")){
			printer1add = "192.168.1.231";
			printer2add = "192.168.1.232";
		}else{
			printer1add = "192.19.1.231";
			printer2add = "192.19.1.232";
		}
		mContext =this;
		setContentView(R.layout.activity_printer);
		printBtn = (Button)findViewById(R.id.PrintBtn);
		printBtn2 = (Button)findViewById(R.id.PrintBtn2);
		pingBtn = (Button)findViewById(R.id.PingBtn);

		OrderBean orderBean = (OrderBean) getIntent().getSerializableExtra("order");
		Log.d(TAG, "OrderInfo: " + orderBean);
		if(orderBean.getItems().size()>0){
			orderItemList = orderBean.getItems();
			orderSn = orderBean.getOrderSn();
			address = orderBean.getAddress();
			phoneNum = orderBean.getPhone();
			recipient = orderBean.getRecipient();
			courierName = orderBean.getCourierName();
			courierPhone = orderBean.getCourierPhone();
			totalQuantity = getTotalQuantity(orderBean.getItems());
		}


		printBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PrintOrderInfo();
				OrderHelper.addPrintedSet(mContext,orderSn);
			}
		});

		printBtn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PrintItemsInfo();
			}
		});

		pingBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckPrinterStatus();
			}
		});

	}

	private int getTotalQuantity(List<ItemContentBean> items){
		int sum = 0;
		for(int i=0;i<items.size();i++){
			sum+=items.get(i).getQuantity();
		}
		return sum;
	}
	private void PrintItemsInfo() {
		if (toBePrintedReady == false) {
			genToBePrintedList(orderItemList);
		}
		for (int a = 0; a < toBePrintedList.size(); a++) {
			setPrintContent2(toBePrintedList.get(a));
			DoPrint2();
		}
	}

	private void PrintOrderInfo() {
		if (toBePrintedReady == false) {
			genToBePrintedList(orderItemList);
		}
		if (toBePrintedList.size() == totalQuantity) {
			int boxIndex = 1;
			if (totalQuantity > 4) {
				int boxCount = totalQuantity/4 + 1;
				for (int a=0, b=0; a < totalQuantity/4; a++) {
					Log.d(TAG, toBePrintedList.get(b) + " " +
							toBePrintedList.get(b + 1) + " " +
							toBePrintedList.get(b + 2) + " " +
							toBePrintedList.get(b + 3));
					setPrintContent(phoneNum, orderSn, address, recipient,"4",boxCount+"",boxIndex+"",
							toBePrintedList.get(b),
							toBePrintedList.get(b+1),
							toBePrintedList.get(b+2),
							toBePrintedList.get(b+3));
					DoPrint();
					b = b + 4;
					boxIndex++;
				}
				int left = totalQuantity%4;
				if (left == 1) {
					Log.d(TAG, toBePrintedList.get(totalQuantity - 1));
					setPrintContent(phoneNum, orderSn, address, recipient,left+"",boxCount+"",boxIndex+"",
							toBePrintedList.get(totalQuantity - 1), "", "", "");
					DoPrint();
				} else if (left == 2) {
					Log.e(TAG, toBePrintedList.get(totalQuantity - 1) + " " +
							toBePrintedList.get(totalQuantity - 2));
					setPrintContent(phoneNum, orderSn, address, recipient,left+"",boxCount+"",boxIndex+"",
							toBePrintedList.get(totalQuantity - 1),
							toBePrintedList.get(totalQuantity - 2), "", "");
					DoPrint();
				}  else if (left == 3) {
					Log.d(TAG, toBePrintedList.get(totalQuantity - 1) + " " +
							toBePrintedList.get(totalQuantity - 2) + " " +
							toBePrintedList.get(totalQuantity - 3));
					setPrintContent(phoneNum, orderSn, address, recipient,left+"",boxCount+"",boxIndex+"",
							toBePrintedList.get(totalQuantity - 1),
							toBePrintedList.get(totalQuantity - 2),
							toBePrintedList.get(totalQuantity - 3), "");
					DoPrint();
				}
			}
			else {
				if (totalQuantity == 1) {
					Log.d(TAG, toBePrintedList.get(totalQuantity - 1));
					setPrintContent(phoneNum, orderSn, address, recipient,totalQuantity+"",1+"",boxIndex+"",
							toBePrintedList.get(totalQuantity - 1), "", "", "");
					DoPrint();
				} else if (totalQuantity == 2) {
					Log.d(TAG, toBePrintedList.get(totalQuantity - 1) + " " +
							toBePrintedList.get(totalQuantity - 2));
					setPrintContent(phoneNum, orderSn, address, recipient,totalQuantity+"",1+"",boxIndex+"",
							toBePrintedList.get(totalQuantity - 1),
							toBePrintedList.get(totalQuantity - 2), "", "");
					DoPrint();
				}  else if (totalQuantity == 3) {
					Log.d(TAG, toBePrintedList.get(totalQuantity - 1) + " " +
							toBePrintedList.get(totalQuantity - 2) + " " +
							toBePrintedList.get(totalQuantity - 3));
					setPrintContent(phoneNum, orderSn, address, recipient,totalQuantity+"",1+"",boxIndex+"",
							toBePrintedList.get(totalQuantity - 1),
							toBePrintedList.get(totalQuantity - 2),
							toBePrintedList.get(totalQuantity - 3), "");
					DoPrint();
				}else if (totalQuantity == 4) {
					Log.d(TAG, toBePrintedList.get(totalQuantity - 1) + " " +
							toBePrintedList.get(totalQuantity - 2) + " " +
							toBePrintedList.get(totalQuantity - 3) + " " +
							toBePrintedList.get(totalQuantity - 4));
					setPrintContent(phoneNum, orderSn, address, recipient,totalQuantity+"",1+"",boxIndex+"",
							toBePrintedList.get(totalQuantity - 1),
							toBePrintedList.get(totalQuantity - 2),
							toBePrintedList.get(totalQuantity - 3),
							toBePrintedList.get(totalQuantity - 4));
					DoPrint();
				}
			}
		} else {
			Log.e(TAG, "toBePrintedList.size: " + toBePrintedList.size() + " totalQuantity: " + totalQuantity);
			setPrintContent(phoneNum, orderSn, address, recipient,"","","",
					"", toBePrintedList.size()+"",
					totalQuantity+"", "总杯量数据不一致");
			DoPrint();
		}

	}

	public class DoPrintThread extends Thread {
		private String printerAdd = "";
		private String printConent = "";
		public void setPrinterAdd(String add) {
			this.printerAdd = add;
		}
		public void setPrinterContent(String content) {
			this.printConent = content;
			Log.d(TAG,"printConent = "+printConent);
		}
		@Override
		public void run() {
			String host = printerAdd;
			int port = 9100;
			Socket client;
			PrinterIsAvailable = false;
			try {
				client = new Socket(host, port);
				Writer writer = new OutputStreamWriter(client.getOutputStream(), "GBK");
				String tempString = null;
				writer.write(printConent);
				writer.flush();
				writer.close();
				client.close();
			} catch (UnknownHostException e) {
				e.printStackTrace();
				Log.e(TAG, "UnknownHostException:"+e.toString());
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "IOException"+e.toString());
				Message msg = handler.obtainMessage();
				msg.what = MSG_EXCEPTION;
				msg.obj = new String("打印机"+host+"无法连接");
				handler.sendMessage(msg);
			} finally {
				PrinterIsAvailable = true;
				Log.d(TAG,"run  finally");
			}
		}
	}


	public  void DoPrint(){
		Log.d(TAG,"DoPrint");
		while (PrinterIsAvailable == false) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		DoPrintThread dpt = new DoPrintThread();
		dpt.setPrinterAdd(printer1add);
		dpt.setPrinterContent(genContent1());
		dpt.start();

	}

	public  void DoPrint2(){
		Log.d(TAG,"DoPrint2");
		while (PrinterIsAvailable == false) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		DoPrintThread dpt = new DoPrintThread();
		dpt.setPrinterAdd(printer2add);
		dpt.setPrinterContent(genContent3());
		dpt.start();
	}
	public void setPrintContent(String phoneNum, String orderSn,
								String address, String recipient,
								String cupCount,String boxCount,String currBox,
								String order1, String order2,
								String order3, String order4){
		this.phoneNum = phoneNum;
		this.recipient = recipient;
		this.orderSn  = orderSn;
		this.address = address;
		this.cupCount = cupCount;
		this.boxCount = boxCount;
		this.currBox = currBox;
		this.order1   = order1;
		this.order2   = order2;
		this.order3   = order3;
		this.order4   = order4;
	}

	public void setPrintContent2(String productName) {
		this.productName = productName;
	}

	@SuppressWarnings("unused")
	private String genContent1() {
		Date date = new Date();
//		DateFormat df = DateFormat.getDateTimeInstance();
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if (phoneNum == null) {
			phoneNum = "";
		}
		if (recipient == null) {
			recipient = "";
		}
		if (orderSn == null) {
			orderSn = "";
		}
		if (address == null) {
			address = "";
		}
		if (order1 == null) {
			order1 = "";
		}
		if (order2 == null) {
			order2 = "";
		}
		if (order3 == null) {
			order3 = "";
		}
		if (order4 == null) {
			order4 = "";
		}
		String addressCMD, addr1, addr2;
		Log.d(TAG, "address len: " + address.length());
		if (address.length() <= 22) {
			addressCMD = "A90,160,0,200,1,1,N,\""+address+"\""+"\n";
		} else {
			addr1 = address.substring(0, 22);
			addr2 = address.substring(22);
			addressCMD = "A90,160,0,200,1,1,N,\""+addr1+"\""+"\n" +
					"A90,190,0,200,1,1,N,\""+addr2+"\""+"\n";
		}

		String orderSnDate, orderSnNum;
		orderSnDate = orderSn.substring(0, 6)+"-";
		orderSnNum = orderSn.substring(6, 9)+"-"+orderSn.substring(9);
		/*String text =
				"N"+"\n"+
				"OD"+"\n"+
				"q640"+"\n"+
				"Q400,16"+"\n"+
				"S3"+"\n"+
				"D8"+"\n"+
				"A10,10,0,200,1,1,N,\"生产时间:"+sdf.format(date)+"\""+"\n"+
				"A10,50,0,200,1,1,N,\"订单号:"+orderSnDate+orderSnNum+"\""+"\n"+ //订单号
				addressCMD +    //配送地址
				"A10,180,0,200,2,2,N,\""+phoneNum+":"+recipient+"\""+"\n"+   //联系人电话号码
				"A10,250,0,200,1,1,N,\"清单：\""+"\n"+
				"A50,270,0,200,1,1,N,\""+order1+"\""+"\n"+
				"A320,270,0,200,1,1,N,\""+order2+"\""+"\n"+
				"A50,300,0,200,1,1,N,\""+order3+"\""+"\n"+
				"A320,300,0,200,1,1,N,\""+order4+"\""+"\n"+
				"P1"+"\n";*/
		String text1 =
				"N"+"\n"+
				"q640"+"\n"+
				"Q400,16"+"\n"+
				"S3"+"\n"+
				"D8"+"\n"+
		//		"A10,10,0,200,1,1,N,\"生产时间:"+sdf.format(date)+"\""+"\n"+
				"A10,50,0,200,1,1,N,\"订单号:\""+"\n"+ //订单号
				"A90,40,0,200,2,2,N,\""+orderSnNum+"  "+currBox+"-"+boxCount+"|"+cupCount+"\""+"\n"+ //杯数盒子信息
				"A10,90,0,200,1,1,N,\"收货人:\""+"\n"+
				"A90,100,0,200,2,2,N,\""+recipient+" "+phoneNum+"\""+"\n"+
				addressCMD +                             //配送地址
				"A10,220,0,200,1,1,N,\"清单：\""+"\n"+
				"A50,250,0,200,1,1,N,\""+order1+"\""+"\n"+
				"A320,250,0,200,1,1,N,\""+order2+"\""+"\n"+
				"A50,280,0,200,1,1,N,\""+order3+"\""+"\n"+
				"A320,280,0,200,1,1,N,\""+order4+"\""+"\n"+
				"P1"+"\n";
		String text2 =
				"N"+"\n"+
				"q640"+"\n"+
				"Q400,16"+"\n"+
				"S3"+"\n"+
				"D8"+"\n"+
		//		"A10,10,0,200,1,1,N,\"生产时间:"+sdf.format(date)+"\""+"\n"+
				"A10,50,0,200,1,1,N,\"订单号:\""+"\n"+ //订单号
				"A90,40,0,200,2,2,N,\""+orderSnNum+"  "+currBox+"-"+boxCount+"|"+cupCount+"\""+"\n"+ //杯数盒子信息
				"A10,90,0,200,1,1,N,\"收货人:\""+"\n"+
				"A90,100,0,200,2,2,N,\""+recipient+" "+phoneNum+"\""+"\n"+
				addressCMD +                             //配送地址
				"A10,220,0,200,1,1,N,\"清单：\""+"\n"+
				"A50,250,0,200,1,1,N,\""+order1+"\""+"\n"+
				"A320,250,0,200,1,1,N,\""+order2+"\""+"\n"+
				"A50,280,0,200,1,1,N,\""+order3+"\""+"\n"+
				"A320,280,0,200,1,1,N,\""+order4+"\""+"\n"+
				"A10,330,0,200,1,1,N,\"配送员："+courierName+" "+courierPhone+"\""+"\n"+
				"P1"+"\n";

		if(TextUtils.isEmpty(courierName)){
			return text1;
		}

		return text2;
	}


	@SuppressWarnings("unused")
	private String genContent2() {
		Date date = new Date();
		if (phoneNum == null) {
			phoneNum = "";
		}
		if (orderSn == null) {
			orderSn = "";
		}
		if (order1 == null) {
			order1 = "";
		}
		if (order2 == null) {
			order2 = "";
		}
		if (order3 == null) {
			order3 = "";
		}
		if (order4 == null) {
			order4 = "";
		}

		String text =
				"N"+"\n"+
				"q640"+"\n"+
				"Q400,16"+"\n"+
				"S3"+"\n"+
				"D8"+"\n"+
				"A10,10,0,200,1,1,N,\"生产时间:"+date.toString()+"\""+"\n"+
				"A10,50,0,200,1,1,N,\"订单号:"+orderSn+"\""+"\n"+ //订单号
				"A10,100,0,200,2,2,N,\"上海徐汇区古美路1515号\""+"\n"+    //配送地址
				"A10,180,0,200,2,2,N,\"电话:"+phoneNum+"\""+"\n"+   //联系人电话号码
				"A10,250,0,200,1,1,N,\"清单：\""+"\n"+
				"A50,280,0,200,1,1,N,\""+order1+"\""+"\n"+
				"A320,280,0,200,1,1,N,\""+order2+"\""+"\n"+
				"A50,310,0,200,1,1,N,\""+order3+"\""+"\n"+
				"A320,310,0,200,1,1,N,\""+order4+"\""+"\n"+
				"P1"+"\n";

		return text;
	}

	private String genContent3() {
		Date date = new Date();
		if (productName == null) {
			productName = "";
		}


		String text =
				"N"+"\n"+
						"OD"+"\n"+
						"q240"+"\n"+
						"Q160,16"+"\n"+
						"S3"+"\n"+
						"D8"+"\n"+
						"A10,80,0,200,1,1,N,\""+productName+"\""+"\n"+
						"P1"+"\n";

		return text;
	}

	public void CheckPrinterStatus() {
		DoPingThread dpt = new DoPingThread();
		dpt.start();
	}

	public  class DoPingThread extends Thread {
		public void run() {
			boolean ping = Ping(printer1add);
			if (ping == true) {
				Message msg = handler.obtainMessage();
				msg.obj = new String(printer1add+"在线");
				msg.what = MSG_PING;
				handler.sendMessage(msg);
			} else {
				Message msg = handler.obtainMessage();
				msg.obj = new String(printer1add+"无法连接");
				msg.what = MSG_PING;
				handler.sendMessage(msg);
			}

			ping = Ping(printer2add);
			if (ping == true) {
				Message msg = handler.obtainMessage();
				msg.obj = new String(printer2add+"在线");
				msg.what = MSG_PING;
				handler.sendMessage(msg);
			} else {
				Message msg = handler.obtainMessage();
				msg.obj = new String(printer2add+"无法连接");
				msg.what = MSG_PING;
				handler.sendMessage(msg);
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what){
				case MSG_PING:
					ToastUtil.showToast(mContext, msg.obj.toString());
					break;
				case MSG_EXCEPTION:
					ToastUtil.showToast(mContext, msg.obj.toString());
					break;
			}

		};
	};

	private boolean Ping(String destip) {
		int timeOut = 3000; //定义超时，表明该时间内连不上即认定为不可达，超时值不能太小。
		try {//ping功能
			boolean status = InetAddress.getByName(destip).isReachable(timeOut);
			Log.d(TAG, "Status = " + status);
			return status;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Log.d(TAG, e.toString());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG, e.toString());
			return false;
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d(TAG,"onStart");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		Log.d(TAG, "onRestart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(TAG, "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}
}
