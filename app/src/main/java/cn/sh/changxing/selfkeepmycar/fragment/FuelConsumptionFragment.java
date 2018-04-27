package cn.sh.changxing.selfkeepmycar.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.sh.changxing.selfkeepmycar.R;
import cn.sh.changxing.selfkeepmycar.activity.MainActivity;
import cn.sh.changxing.selfkeepmycar.base.BaseFragment;
//import cn.sh.changxing.selfkeepmycar.db.model.FuelConsumption;
import cn.sh.changxing.selfkeepmycar.db.model.Record;
import cn.sh.changxing.selfkeepmycar.utils.Utils;
import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FuelConsumptionFragment extends BaseFragment {

    private Unbinder unbinder;

    @BindView(R.id.line_chart)
    LineChartView lineChart;
    @BindView(R.id.last_month)
    Button mBtLastMonth;
    @BindView(R.id.next_month)
    Button mBtNextMonth;
    @BindView(R.id.title_month)
    TextView mTvTitle;
    @BindView(R.id.max_fuel)
    TextView mTvMaxFuel;
    @BindView(R.id.min_fuel)
    TextView mTvMinFuel;
    @BindView(R.id.ave_fuel)
    TextView mTvAveFuel;
    @BindView(R.id.bt_week_fuel)
    Button mBtWeek;
    // 当前切换月份第一天：yyyy-MM-dd
    private String mCurrentFirstDayOFMonth;
    private List<Record> mCurrentRecords;

    private String[] mDate = new String[30];//X轴的标注
    private double[] score = new double[30];//图表的数据点
    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();

    public FuelConsumptionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fuel_consumption, container, false);
        unbinder = ButterKnife.bind(this, view);

        mCurrentFirstDayOFMonth = Utils.getFirstDayOfMonth(new Date());
        List<Record> monthFuelConsumption = getDataForYmd(mCurrentFirstDayOFMonth);
        if (monthFuelConsumption.size()>0) {
            getDate(monthFuelConsumption);
            getAxisXLables();//获取x轴的标注
            getAxisPoints();//获取坐标点
            initLineChart();//初始化
            lineChart.setOnValueTouchListener(new LineChartOnValueSelectListener() {
                @Override
                public void onValueSelected(int i, int i1, PointValue pointValue) {
                    Toast.makeText(getActivity(), Integer.valueOf(mDate[i1])+"日："+String.valueOf(pointValue.getY()),Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onValueDeselected() {

                }
            });
        } else {
            Toast.makeText(getActivity(), "暂无油耗记录", Toast.LENGTH_SHORT).show();
        }

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private List<Record> getDataForYmd(String firstDayOfMonth){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String ymd = "";
        try {
            Date date = format.parse(firstDayOfMonth);
            String ym = simpleDateFormat.format(date);
            ymd = new StringBuffer().append(ym).append("-31").toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mCurrentRecords = new ArrayList<>();
        if (TextUtils.isEmpty(ymd)) {
            mCurrentRecords =  DataSupport.select("dateYmd","fuelConsumption","distance").where("dateYmd >= ?", firstDayOfMonth).order("dateYmd desc").find(Record.class);
        } else {
            mCurrentRecords = DataSupport.select("dateYmd","fuelConsumption","distance").where("dateYmd >= ? and dateYmd <= ?", firstDayOfMonth, ymd).order("dateYmd desc").find(Record.class);
        }
        return mCurrentRecords;
    }

    private void getDate(List<Record> monthFuelConsumption){

        computFuel(monthFuelConsumption);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat titleFormat = new SimpleDateFormat("yyyy-MM月");
        for (int i = 0; i < mDate.length; i++) {
            Date date = null;
            try {
                date = format.parse(mDate[i]);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String strDate = (new SimpleDateFormat("dd")).format(date);
            mDate[i] = strDate;
            System.out.println(strDate);
        }
        if (mDate.length > 0) {
            try {
                mTvTitle.setText(titleFormat.format(format.parse(monthFuelConsumption.get(0).getDateYmd())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        getMinMaxData();
    }

    private void getMinMaxData() {
        double maxValue = 0.0d;
        double minValue = 0.0d;
        double totalValue = 0.0d;
        double aveValue = 0.0d;
        int maxDay = 0;
        int minDay = 0;
        if (score.length > 0 && score[0] > 0d){
            maxValue = score[0];
            minValue = score[0];
        }
        for (int i = 0; i < score.length; i++) {
            totalValue += score[i];
            if (maxValue < score[i]) {
                maxValue = score[i];
                maxDay = i;
            }
            if (minValue > score[i]){
                minValue = score[i];
                minDay = i;
            }
        }
        aveValue = computAveFuel(mCurrentRecords);
        String strMaxDay = mTvTitle.getText().toString().substring(0, mTvTitle.getText().toString().length() - 1) +"-"+ mDate[maxDay];
        String strMinDay = mTvTitle.getText().toString().substring(0, mTvTitle.getText().toString().length() - 1) +"-"+ mDate[minDay];
        String maxDayOfWeek = Utils.getDayOfWeek(strMaxDay);
        String minDayOfWeek = Utils.getDayOfWeek(strMinDay);

        mTvMaxFuel.setText(Html.fromHtml("<font color=\"#048aea\">"+Integer.valueOf(mDate[maxDay])+"日:"+maxDayOfWeek+"油耗最高："+"</font>"+"<font color=\"red\">"+maxValue+"</font>"+"<font color=\"#048aea\">"+"L/100KM"+"</font>"));
        mTvMinFuel.setText(Html.fromHtml("<font color=\"#048aea\">"+Integer.valueOf(mDate[minDay])+"日:"+minDayOfWeek+"油耗最低："+"</font>"+"<font color=\"red\">"+minValue+"</font>"+"<font color=\"#048aea\">"+"L/100KM"+"</font>"));
        mTvAveFuel.setText(Html.fromHtml("<font color=\"#048aea\">"+"月平均油耗："+"</font>"+"<font color=\"red\">"+aveValue+"</font>"+"<font color=\"#048aea\">"+"L/100KM"+"</font>"));
    }

    @OnClick({R.id.last_month, R.id.next_month, R.id.bt_week_fuel})
    public void Onclick(View view){
        switch (view.getId()){
            case R.id.last_month:
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date lastDate = format.parse(mCurrentFirstDayOFMonth);
                    String firstDayOfLastMonth = Utils.getFirstDayOfLastMonth(lastDate);
                    List<Record> lastDatas =  getDataForYmd(firstDayOfLastMonth);
                    if (lastDatas.size() > 0) {
                        initAllData();
                        getDate(lastDatas);
                        getAxisXLables();//获取x轴的标注
                        getAxisPoints();//获取坐标点
                        initLineChart();//初始化
                        mCurrentFirstDayOFMonth = firstDayOfLastMonth;
                    } else {
                        Toast.makeText(getActivity(), "上月没有数据哦",Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.next_month:
                SimpleDateFormat nextFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date nextDate = nextFormat.parse(mCurrentFirstDayOFMonth);
                    String firstDayOfNextMonth = Utils.getFirstDayOfNextMonth(nextDate);
                    List<Record> nextDatas =  getDataForYmd(firstDayOfNextMonth);
                    if (nextDatas.size() > 0) {
                        initAllData();
                        getDate(nextDatas);
                        getAxisXLables();//获取x轴的标注
                        getAxisPoints();//获取坐标点
                        initLineChart();//初始化
                        mCurrentFirstDayOFMonth = firstDayOfNextMonth;
                    } else {
                        Toast.makeText(getActivity(), "下月没有数据哦",Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.bt_week_fuel:
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.hide(FuelConsumptionFragment.this);
                WeekFuelFragment weekFuelFragment = new WeekFuelFragment();
                SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
                int year = -1;
                int month = -1;
                Calendar calendar = Calendar.getInstance();
                try {
                    Date date = fm.parse(mCurrentFirstDayOFMonth);
                    calendar.setTime(date);
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ((MainActivity) getActivity()).intoWeek(year, month);
                break;
        }
    }

    private void initAllData(){
        mDate = new String[30];//X轴的标注
        score = new double[30];//图表的数据点
        mPointValues = new ArrayList<PointValue>();
        mAxisXValues = new ArrayList<AxisValue>();
    }

    /**
     * 设置X 轴的显示
     */
    private void getAxisXLables() {
        for (int i = 0; i < mDate.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(mDate[i]));
        }
    }

    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints() {
        for (int i = 0; i < mDate.length; i++) {
            mPointValues.add(new PointValue(i, (float) score[i]));
        }
    }
    private void initLineChart(){
        Line line = new Line(mPointValues).setColor(Color.parseColor("#FFCD41"));  //折线的颜色（橙色）
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setCubic(false);//曲线是否平滑，即是曲线还是折线
        line.setFilled(true);//是否填充曲线的面积
//        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setHasLabelsOnlyForSelected(false);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(false);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setTextColor(Color.GREEN);  //设置字体颜色
        //axisX.setName("date");  //表格名称
        axisX.setTextSize(10);//设置字体大小
//        axisX.setMaxLabelChars(7); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
        axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
        //data.setAxisXTop(axisX);  //x 轴在顶部
        axisX.setHasLines(true); //x 轴分割线


//        // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
//        Axis axisY = new Axis();  //Y轴
//        axisY.setName("");//y轴标注
//        axisY.setTextSize(10);//设置字体大小
//        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //data.setAxisYRight(axisY);  //y轴设置在右边


        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL);
        lineChart.setMaxZoom((float) 2);//最大方法比例
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
        /**注：下面的7，10只是代表一个数字去类比而已
         * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
         */
        Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right= 7;
        lineChart.setCurrentViewport(v);
    }

    private void computFuel(List<Record> fuelConsumptions){
        double fuel = 0.0d;

        double[] cache = new double[30];
        String[] cacheYmd = new String[30];

        if (fuelConsumptions.size() > 0) {
            String lastYmd = fuelConsumptions.get(0).getDateYmd();
            int index = 0;
            double totalL = 0.0d;
            double totalKM = 0.0d;
            for (int i = 0; i < fuelConsumptions.size(); i++) {
                Record fuelConsumption = fuelConsumptions.get(i);
                if (fuelConsumption.getFuelConsumption()<=0.0d) {
                    continue;
                }
                if (lastYmd.equals(fuelConsumption.getDateYmd())) {

                    totalKM = Utils.add(totalKM, fuelConsumption.getDistance());
                    totalL = Utils.add(totalL, (Utils.div(Utils.mul(fuelConsumption.getFuelConsumption(), fuelConsumption.getDistance()), 100, 2)));
                    if (totalKM == 0) {
                        Log.d("FuelConsumption", fuelConsumption.toString());
                    }
                    fuel = Utils.mul(Utils.div(totalL, totalKM, 3), 100);

                } else {
                    index++;
                    totalKM = fuelConsumption.getDistance();
                    totalL = Utils.div(Utils.mul(fuelConsumption.getFuelConsumption(), fuelConsumption.getDistance()), 100, 2);
                    fuel = fuelConsumption.getFuelConsumption();
                }
                cache[index] = fuel;
                cacheYmd[index] = fuelConsumption.getDateYmd();
                lastYmd = fuelConsumption.getDateYmd();
            }

            score = new double[index+1];
            mDate = new String[index+1];
            for (int i = 0; i <= index; i++) {
                score[i] = cache[i];
                mDate[i] = cacheYmd[i];
            }
        }else {
            score = new double[0];
            mDate = new String[0];
        }
    }

    // 计算平均油耗
    private double computAveFuel(List<Record> records){
        double fuel = 0.0d;
        double totalL = 0.0d;
        double totalKM = 0.0d;
        for (Record record : records) {
            if (record.getFuelConsumption() > 0.0d) {
                totalKM = Utils.add(totalKM, record.getDistance());
                totalL = Utils.add(totalL, (Utils.div(Utils.mul(record.getFuelConsumption(), record.getDistance()), 100, 3)));
            }

        }
        if (totalL > 0) {
            fuel = (Utils.div(Utils.mul(totalL, 100), totalKM, 1));
        }

        return fuel;
    }
}
