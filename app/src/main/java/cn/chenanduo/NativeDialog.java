package cn.chenanduo;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;


public class NativeDialog extends Dialog {
	private Context context;
	private EditText editText;
    private TextView negativeButton,positiveButton;
    private TextView title;
    private LinearLayout lay_apps;
    private boolean isSucceed;//是否定位成功
    private List<AppInfo> apps;
    private String msg = "选择您需要打开的应用";
    private String msg_default = "您的手机中没有安装地图导航工具,我们建议您下载高德地图进行导航";
    private String positiveStr = "继续导航";
    private String negativeStr = "取消";
    private Location loc_now;
    private Location loc_end;
    private double mLatitude;
    private double mLongitude;
    public static double pi = 3.1415926535897932384626;
    public static double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
    public static double a = 6378245.0;
    public static double ee = 0.00669342162296594323;

    public NativeDialog(Context context, Location loc_now, Location loc_end) {
    	super(context, R.style.NativeDialog);
        this.context = context;
        this.loc_now = loc_now;
        this.loc_end = loc_end;
        initApps();
        setMsgDialog();
    }
    
    private void initApps() {
    	apps = APPUtil.getMapApps(context);
    	//只显示前5个应用
        if (apps!=null && apps.size()>5) {
			apps = apps.subList(0, 5);
		}
	}

	private void setMsgDialog() {
    	View mView;
    	if (apps!=null && apps.size()!=0) {
    		mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_native, null);
            title = (TextView) mView.findViewById(R.id.title2);
            negativeButton = (TextView) mView.findViewById(R.id.negativeButton);
            if(title!=null) title.setText(msg);
            if(negativeButton!=null) negativeButton.setText(negativeStr);
            if(negativeButton!=null) negativeButton.setOnClickListener(deflistener);
            
            LinkedList<TextView> views = new LinkedList<TextView>();
            lay_apps = (LinearLayout) mView.findViewById(R.id.lay_apps);
            lay_apps.setOrientation(LinearLayout.VERTICAL);
//    		for (AppInfo app : apps) {
    		for (int i = 0; i < apps.size(); i++) {
				AppInfo app = apps.get(i);
    			//定义左右边距15
    			LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    			para.setMargins(15, 0, 15, 0); // left,top,right, bottom
    			para.gravity = Gravity.CENTER;
    			
    			TextView textView = new TextView(context);
    			textView.setCompoundDrawablesWithIntrinsicBounds(null,app.getAppIcon(), null, null);	//设置图标
    			textView.setText(app.getAppName());								//设置文字
    			textView.setTextAppearance(context, R.style.text_small_dark);	//设置风格
    			textView.setLayoutParams(para);									//设置边距
    			textView.setGravity(Gravity.CENTER_HORIZONTAL);					//设置图标文字水平居中
    			textView.setSingleLine(true);									//设置单行显示
    			textView.setEllipsize(TruncateAt.END);							//设置超出长度显示省略…
    			textView.setMaxEms(6);											//设置最大长度
    			textView.setTag(app.getPackageName());							//设置包名为tag
    			textView.setOnClickListener(applistener);						//设置监听
    			
    			views.add(textView);
    			if (views.size()==3 || i==apps.size()-1) {
    				LinearLayout.LayoutParams para_lay = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    				para_lay.setMargins(0, 30, 0, 0); // left,top,right, bottom
    				para_lay.gravity = Gravity.CENTER;
    				
					LinearLayout row = new LinearLayout(context);
	    			row.setOrientation(LinearLayout.HORIZONTAL);
	    			row.setGravity(Gravity.CENTER_HORIZONTAL);
	    			row.setLayoutParams(para_lay);
	    			for (TextView view : views) {
	    				row.addView(view);
					}
	    			lay_apps.addView(row);
	    			views.clear();
				}
    		}
		}else {
			mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_native_default, null);
			title = (TextView) mView.findViewById(R.id.title1);
            positiveButton = (TextView) mView.findViewById(R.id.positiveButton);
            negativeButton = (TextView) mView.findViewById(R.id.negativeButton);
            if(title!=null) title.setText(msg_default);
            if(positiveButton!=null) positiveButton.setText(positiveStr);
            if(negativeButton!=null) negativeButton.setText(negativeStr);
            if(positiveButton!=null) positiveButton.setOnClickListener(weblistener);
            if(negativeButton!=null) negativeButton.setOnClickListener(deflistener);
		}
        super.setContentView(mView);
    }
    
    @Override
    public void show() {
    	super.show();
    	Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        /////////获取屏幕宽度
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);;
		wm.getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		/////////设置高宽
		lp.width = (int) (screenWidth * 0.85); // 宽度  
        dialogWindow.setAttributes(lp);  
    }
     
    public View getEditText(){
        return editText;
    }
     
     @Override
    public void setContentView(int layoutResID) {
    }
 
    @Override
    public void setContentView(View view, LayoutParams params) {
    }
 
    @Override
    public void setContentView(View view) {
    }
 
    /**
     * 取消键监听器
     * @param listener
     */ 
    public void setOnNegativeListener(View.OnClickListener listener){
        negativeButton.setOnClickListener(listener); 
    }
    
    /**
     * 默认的监听器
     */
    private View.OnClickListener deflistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	NativeDialog.this.dismiss();
        }
    };

    /**
     * App图标点击监听，启动app进行导航
     */
    private View.OnClickListener applistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	String pak = (String)v.getTag();
        	switch (pak) {
			case "com.baidu.BaiduMap":
			    //TODO:不使用百度地图  只使用高德  如果使用百度 加个包名即可
                break;
			case "com.autonavi.minimap":
			    //高德地图GCJ-02转原始坐标 84  不然定位后获取的经纬度直接导航会有偏差  需要转换
                double[] doubles = gcj02_To_Gps84(loc_end.getLat(), loc_end.getLng());
                Location loca = new Location();
                loca.setLat(doubles[0]);
                loca.setLng(doubles[1]);
                APPUtil.startNative_Gaode(context,loca);
//                APPUtil.startNative_Gaode(context,loc_end);
				break;
			}
        	NativeDialog.this.dismiss();
        }
    };

    /**
     * * 火星坐标系 (GCJ-02) to 84 * * @param lon * @param lat * @return
     * */
    public static double[] gcj02_To_Gps84(double lat, double lon) {
        double[] gps = transform(lat, lon);
        double lontitude = lon * 2 - gps[1];
        double latitude = lat * 2 - gps[0];
        return new double[]{latitude, lontitude};
    }

    /**
     * 高德地图GCJ-02转原始坐标 84  不然定位后获取的经纬度直接导航会有偏差  需要转换
     * @param lat
     * @param lon
     * @return
     */
    public static boolean outOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    /**
     * 高德地图GCJ-02转原始坐标 84  不然定位后获取的经纬度直接导航会有偏差  需要转换
     * @param lat
     * @param lon
     * @return
     */
    public static double[] transform(double lat, double lon) {
        if (outOfChina(lat, lon)) {
            return new double[]{lat,lon};
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new double[]{mgLat,mgLon};
    }

    /**
     * 高德地图GCJ-02转原始坐标 84  不然定位后获取的经纬度直接导航会有偏差  需要转换
     * @param x
     * @param y
     * @return
     */
    public static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 高德地图GCJ-02转原始坐标 84  不然定位后获取的经纬度直接导航会有偏差  需要转换
     * @param x
     * @param y
     * @return
     */
    public static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 启动web进行导航
     */
    private View.OnClickListener weblistener =   new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        	//###########################################
        	//第一种方式：这种方式需要导入百度sdk，才能进行启调，如果没导入会找不到类
        	//建议使用这种方式，对浏览器的兼容更好。
        	//注释掉这里段代码，取消下面第二种方式的注释可以启用第二种方式
        	//NaviParaOption para = new NaviParaOption().startPoint(MyDistanceUtil.entity2Baidu(loc_now)).endPoint(MyDistanceUtil.entity2Baidu(loc_end));
        	//BaiduMapNavigation.openWebBaiduMapNavi(para, context);
        	//###########################################
        	//第二种方式：这种方式不需要导入百度sdk，可以直接使用
        	//不推建使用这种方式，浏览器兼容问题比较严重，比如qq浏览器会封杀百度的此功能。
        	//注释掉这里段代码，取消上面第一种方式的注释可以启用第一种方式
        	//###########################################
        	/*String url = APPUtil.getWebUrl_Baidu(loc_now, loc_end);
        	Intent intent = new Intent(Intent.ACTION_VIEW);
        	intent.setData(Uri.parse(url));
        	context.startActivity(intent);
        	//###########################################*/
            Toast.makeText(context, "尚未安装高德地图", Toast.LENGTH_SHORT).show();
        	NativeDialog.this.dismiss();
        }
    };
}
