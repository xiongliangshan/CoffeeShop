/*
package com.lyancafe.coffeeshop.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lyancafe.app.hub.bean.ItemDetailBean;
import com.lyancafe.app.util.ScreenUtil;
import com.lyancafe.app.util.StringUtil;
import com.lyancafe.app.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PrinterActivity extends Activity {
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
	private String productName = "";
	private String recipient = "";
	private int totalQuantity = 0;
	private String order1 = "";
	private String order2 = "";
	private String order3 = "";
	private String order4 = "";
	private List<ItemDetailBean> orderItemList = null;
	private String printer1add = "192.19.1.231";
	private String printer2add = "192.19.1.232";
	private int PrintedNum = 0;
	private List<String> toBePrintedList = new ArrayList<String>();
	private boolean PrinterIsAvailable = true;
	private boolean toBePrintedReady = false;
	private RelativeLayout printer_back;
	
	private void genToBePrintedList(List<ItemDetailBean> orderItemList) {
		//toBePrintedList.size() = totalQuantity;
		
		for (int i = 0; i < orderItemList.size(); i++) {
			for (int j = 0; j < orderItemList.get(i).getQuantity();j++){
				toBePrintedList.add(orderItemList.get(i).getProductName());
			}
		}
		toBePrintedReady = true;
*/
/*		for (int k = 0; k < toBePrintedList.size(); k++) {
			Log.e("Mike", "#"+toBePrintedList.get(k)+" ");
		}*//*


	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置横纵屏
		ScreenUtil.autoSetOrientation(this);
		setContentView(R.layout.activity_printer);
		printBtn = (Button)findViewById(R.id.PrintBtn);
		printBtn2 = (Button)findViewById(R.id.PrintBtn2);
		pingBtn = (Button)findViewById(R.id.PingBtn);

*/
/*		orderSn = getIntent().getStringExtra(ORDER_SN);
		address = getIntent().getStringExtra(ADDRESS);
		phoneNum = getIntent().getStringExtra(PHONE_NUM);
		totalQuantity = getIntent().getIntExtra(TOTAL_QUANTITY, 0);
		orderItemList = (List<ItemDetailBean>)getIntent().getSerializableExtra(ORDER_LIST);*//*


		String orderInfoStr = getIntent().getStringExtra("orderInfo");
		Log.e("Mike", "OrderInfoStr: " + orderInfoStr);
		JSONObject obj;
		try {
			obj = new JSONObject(orderInfoStr);
			JSONObject orderObj = obj.getJSONObject("orderInfo");
			if(orderObj!=null){
				orderSn = orderObj.getString("orderSn");
				address = orderObj.getString("address");
				phoneNum = orderObj.getString("phoneNum");
				recipient = orderObj.getString("recipient");
				totalQuantity = orderObj.getInt("totalQuantity");
				String items = orderObj.getString("items");
				List<ItemDetailBean> list = new ArrayList<ItemDetailBean>();
				if (!StringUtil.isNullOrEmpty(items)) {
					list = parseToOrderList(items);
					if (list != null && list.size() > 0) {
						orderItemList = list;
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		printBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
*/
/*				ConfirmDialog dialog = new ConfirmDialog(PrinterActivity.this,
						R.style.MyDialog, new ConfirmDialog.OnClickYesListener() {
							@Override
							public void onClickYes() {
								PrintOrderInfo();
							}
						});
				dialog.setContent(R.string.confirm_do_print);
				dialog.setBtnTxt(R.string.click_error, R.string.confirm);
				dialog.show();*//*

				PrintOrderInfo();
				LyanApplication.setOrderPrinted(orderSn);
			}
		});

		printBtn2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PrintItemsInfo();
			}
		});

		pingBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CheckPrinterStatus();
			}
		});

		printer_back = (RelativeLayout) findViewById(R.id.printer_back);
		printer_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = null;
				intent = new Intent(getApplicationContext(), MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	private static List<ItemDetailBean> parseToOrderList(String orders) {
		Type lt = new TypeToken<List<ItemDetailBean>>() {
		}.getType();
		Gson g = new Gson();
		List<ItemDetailBean> list = g.fromJson(orders, lt);
		return list;
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
			if (totalQuantity > 4) {
				for (int a=0, b=0; a < totalQuantity/4; a++) {
					Log.e("Mike", toBePrintedList.get(b) + " " +
							toBePrintedList.get(b + 1) + " " +
							toBePrintedList.get(b + 2) + " " +
							toBePrintedList.get(b + 3));
					setPrintContent(phoneNum, orderSn, address, recipient,
									toBePrintedList.get(b),
									toBePrintedList.get(b+1),
									toBePrintedList.get(b+2),
									toBePrintedList.get(b+3));
					DoPrint();
					b = b + 4;
				}
				int left = totalQuantity%4;
				if (left == 1) {
					Log.e("Mike", toBePrintedList.get(totalQuantity - 1));
					setPrintContent(phoneNum, orderSn, address, recipient,
							toBePrintedList.get(totalQuantity - 1), "", "", "");
					DoPrint();
				} else if (left == 2) {
					Log.e("Mike", toBePrintedList.get(totalQuantity - 1) + " " +
							toBePrintedList.get(totalQuantity - 2));
					setPrintContent(phoneNum, orderSn, address, recipient,
							toBePrintedList.get(totalQuantity - 1),
							toBePrintedList.get(totalQuantity - 2), "", "");
					DoPrint();
				}  else if (left == 3) {
					Log.e("Mike", toBePrintedList.get(totalQuantity - 1) + " " +
							toBePrintedList.get(totalQuantity - 2) + " " +
							toBePrintedList.get(totalQuantity - 3));
					setPrintContent(phoneNum, orderSn, address, recipient,
							toBePrintedList.get(totalQuantity - 1),
							toBePrintedList.get(totalQuantity - 2),
							toBePrintedList.get(totalQuantity - 3), "");
					DoPrint();
				}
			}
			else {
				if (totalQuantity == 1) {
					Log.e("Mike", toBePrintedList.get(totalQuantity - 1));
					setPrintContent(phoneNum, orderSn, address, recipient,
							toBePrintedList.get(totalQuantity - 1), "", "", "");
					DoPrint();
				} else if (totalQuantity == 2) {
					Log.e("Mike", toBePrintedList.get(totalQuantity - 1) + " " +
							toBePrintedList.get(totalQuantity - 2));
					setPrintContent(phoneNum, orderSn, address, recipient,
							toBePrintedList.get(totalQuantity - 1),
							toBePrintedList.get(totalQuantity - 2), "", "");
					DoPrint();
				}  else if (totalQuantity == 3) {
					Log.e("Mike", toBePrintedList.get(totalQuantity - 1) + " " +
							toBePrintedList.get(totalQuantity - 2) + " " +
							toBePrintedList.get(totalQuantity - 3));
					setPrintContent(phoneNum, orderSn, address, recipient,
							toBePrintedList.get(totalQuantity - 1),
							toBePrintedList.get(totalQuantity - 2),
							toBePrintedList.get(totalQuantity - 3), "");
					DoPrint();
				}else if (totalQuantity == 4) {
					Log.e("Mike", toBePrintedList.get(totalQuantity - 1) + " " +
							toBePrintedList.get(totalQuantity - 2) + " " +
							toBePrintedList.get(totalQuantity - 3) + " " +
							toBePrintedList.get(totalQuantity - 4));
					setPrintContent(phoneNum, orderSn, address, recipient,
							toBePrintedList.get(totalQuantity - 1),
							toBePrintedList.get(totalQuantity - 2),
							toBePrintedList.get(totalQuantity - 3),
							toBePrintedList.get(totalQuantity - 4));
					DoPrint();
				}
			}
		} else {
			Log.e("Mike", "toBePrintedList.size: " + toBePrintedList.size() + " totalQuantity: " + totalQuantity);
			setPrintContent(phoneNum, orderSn, address, recipient,
					"", toBePrintedList.size()+"",
					totalQuantity+"", "逻辑错误");
			DoPrint();
		}

	}

	public  class DoPrintThread extends Thread {
		private String printerAdd = "";
		private String printConent = "";
		public void setPrinterAdd(String add) {
			this.printerAdd = add;
		}
		public void setPrinterContent(String content) {
			this.printConent = content;
		}
		@Override
		public void run() {
			super.run();
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

*/
/*		        try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*//*

		        writer.close();
		        client.close();
		        PrinterIsAvailable = true;
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Log.e("Mike", e1.toString());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Log.e("Mike", e1.toString());
			}
		}
	}


	public  void DoPrint(){
		while (PrinterIsAvailable == false) {
	        try {
	            Thread.sleep(500);
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
		while (PrinterIsAvailable == false) {
	        try {
	            Thread.sleep(500);
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
								String order1, String order2,
								String order3, String order4){
		this.phoneNum = phoneNum;
		this.recipient = recipient;
		this.orderSn  = orderSn;
		this.address = address;
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
		DateFormat df = DateFormat.getDateTimeInstance();//DateFormat.getDateTimeInstance(DateFormat.FULL,DateFormat.FULL);
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
		Log.e("Mike", "address len: " + address.length());
		if (address.length() <= 26) {
			addressCMD = "A10,100,0,200,1,1,N,\""+address+"\""+"\n";
		} else {
			addr1 = address.substring(0, 26);
			addr2 = address.substring(26);
			addressCMD = "A10,100,0,200,1,1,N,\""+addr1+"\""+"\n" +
						 "A10,140,0,200,1,1,N,\""+addr2+"\""+"\n";
		}

		String orderSnDate, orderSnNum;
		orderSnDate = orderSn.substring(0, 6)+"-";
		orderSnNum = orderSn.substring(6, 9)+"-"+orderSn.substring(9);
		//Log.e("Mike", orderSnDate+orderSnNum);

		String text =
				"N"+"\n"+
				"OD"+"\n"+
			    "q640"+"\n"+
			    "Q400,16"+"\n"+
			    "S3"+"\n"+
			    "D8"+"\n"+
			    "A10,10,0,200,1,1,N,\"生产时间:"+df.format(date)+"\""+"\n"+
			    "A10,50,0,200,1,1,N,\"订单号:"+orderSnDate+orderSnNum+"\""+"\n"+ //订单号
			    addressCMD +    //配送地址
			    "A10,180,0,200,2,2,N,\""+phoneNum+":"+recipient+"\""+"\n"+   //联系人电话号码
			    "A10,250,0,200,1,1,N,\"清单：\""+"\n"+
			    "A50,280,0,200,1,1,N,\""+order1+"\""+"\n"+
			    "A320,280,0,200,1,1,N,\""+order2+"\""+"\n"+
			    "A50,310,0,200,1,1,N,\""+order3+"\""+"\n"+
			    "A320,310,0,200,1,1,N,\""+order4+"\""+"\n"+
			    "P1"+"\n";

		return text;
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
				//msg.what =;
				Bundle bundle = new Bundle();
				bundle.putString("PingStatus",printer1add+"在线");
				msg.setData(bundle);
				//msg.sendToTarget();
				handler.sendMessage(msg);
			} else {
				Message msg = handler.obtainMessage();
				//msg.what =;
				Bundle bundle = new Bundle();
				bundle.putString("PingStatus",printer1add+"无法访问");
				msg.setData(bundle);
				//msg.sendToTarget();
				handler.sendMessage(msg);
			}

			ping = Ping(printer2add);
			if (ping == true) {
				Message msg = handler.obtainMessage();
				//msg.what =;
				Bundle bundle = new Bundle();
				bundle.putString("PingStatus",printer2add+"在线");
				msg.setData(bundle);
				//msg.sendToTarget();
				handler.sendMessage(msg);
			} else {
				Message msg = handler.obtainMessage();
				//msg.what =;
				Bundle bundle = new Bundle();
				bundle.putString("PingStatus",printer2add+"无法访问");
				msg.setData(bundle);
				//msg.sendToTarget();
				handler.sendMessage(msg);
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(android.os.Message msg) {
			ToastUtil.show(getApplicationContext(), msg.getData().getString("PingStatus"));
		};
	};

	private boolean Ping(String destip) {
		int timeOut = 3000; //定义超时，表明该时间内连不上即认定为不可达，超时值不能太小。
		try {//ping功能
			boolean status = InetAddress.getByName(destip).isReachable(timeOut);
		    Log.e("Mike", "Status = " + status);

		    return status;
		} catch (UnknownHostException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		    Log.e("Mike", e.toString());
		    return false;
		} catch (IOException e) {
			// TODO Auto-generated catch blo                    
			e.printStackTrace();
			Log.e("Mike", e.toString());
			return false;
		}
	}
	
	@Override
    protected void onStart() {
    	super.onStart();
		//ToastUtil.show(getApplicationContext(), "printer start");
    }

	@Override
    protected void onRestart() {
    	super.onRestart();
		//ToastUtil.show(getApplicationContext(), "printer restart");
    }

	@Override
    protected void onResume() {
    	super.onResume();
		//ToastUtil.show(getApplicationContext(), "printer resume");
    }

	@Override
    protected void onPause() {
    	super.onPause();
		//ToastUtil.show(getApplicationContext(), "printer pause");
    }

	@Override
    protected void onStop() {
    	super.onStop();
		//ToastUtil.show(getApplicationContext(), "printer stop");
    }

    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
		//ToastUtil.show(getApplicationContext(), "printer destroy");
    }
}
*/
