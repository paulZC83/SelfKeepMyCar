package cn.sh.changxing.selfkeepmycar.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.sh.changxing.selfkeepmycar.R;
import cn.sh.changxing.selfkeepmycar.activity.AddRecordActivity;
import cn.sh.changxing.selfkeepmycar.base.BaseFragment;
import cn.sh.changxing.selfkeepmycar.db.model.Record;
import cn.sh.changxing.selfkeepmycar.db.model.RecordBmob;
import cn.sh.changxing.selfkeepmycar.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    private Unbinder unbinder;
    @BindView(R.id.balance)
    TextView mBalance;
    @BindView(R.id.expenses)
    TextView mExpenses;
    @BindView(R.id.receipts)
    TextView mReceipts;
    @BindView(R.id.bt_add)
    Button mAddBt;
    @BindView(R.id.bt_update_to_Server)
    Button mUpdateServer;
   @BindView(R.id.bt_download_from_Server)
   Button mDownloadBt;
    @BindView(R.id.today_fuel)
    TextView mTodayFuel;
    @BindView(R.id.today_re)
    TextView mTodayRe;
    @BindView(R.id.today_ex)
    TextView mTodayEx;
    @BindView(R.id.week_fuel)
    TextView mWeekFuel;
    @BindView(R.id.week_re)
    TextView mWeekRe;
    @BindView(R.id.week_ex)
    TextView mWeekEx;
    @BindView(R.id.month_fuel)
    TextView mMonthFuel;
    @BindView(R.id.month_re)
    TextView mMonthRe;
    @BindView(R.id.month_ex)
    TextView mMonthEx;

    @BindView(R.id.home_bk)
    ImageView mHomeBk;
    @BindView(R.id.title_layout_bk)
    ImageView mIvTitle;

    private String mMonday, mFirstDayOfMonth;
    private List<Record> mAllRecord;
    private int mIndex = 0;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
//        Glide.with(getActivity()).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524822723673&di=866e4895af9e304d4a9b24230ceafe94&imgtype=0&src=http%3A%2F%2Fimg2.ph.126.net%2FX5bNnx85ljFUP8Bjy4-iqA%3D%3D%2F1008243366595017101.jpg").into(mHomeBk);
//        Glide.with(getActivity()).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1524822338386&di=399271671128a43c5f7c686a848a4d8b&imgtype=0&src=http%3A%2F%2Fpic.90sjimg.com%2Fback_pic%2Fqk%2Fback_origin_pic%2F00%2F03%2F77%2F3201b6d05cbb5ba09620ed557c49fdb0.jpg").into(mIvTitle);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMonday = Utils.getMonday(new Date());
        mFirstDayOfMonth = Utils.getFirstDayOfMonth(new Date());
        getData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @OnClick({R.id.bt_add,R.id.bt_update_to_Server, R.id.bt_download_from_Server})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.bt_add:
                getActivity().startActivity(new Intent(getActivity(), AddRecordActivity.class));
                break;

            case R.id.bt_download_from_Server:
                if (mAllRecord != null && mAllRecord.size() > 0) {
                    DataSupport.deleteAll(Record.class);
                }

                BmobQuery<RecordBmob> query = new BmobQuery<>();
                //返回500条数据，如果不加上这条语句，默认返回10条数据
                query.setLimit(500);
                //执行查询方法
                query.findObjects(new FindListener<RecordBmob>() {
                    @Override
                    public void done(List<RecordBmob> object, BmobException e) {
                        if(e==null){
                            Log.d("HomeFragment", "Record:查询成功：共"+object.size()+"条数据。");
                            List<Record> records = new ArrayList<>();
                            for (RecordBmob recordBmob : object) {
                                records.add(Utils.castToRecordFromBmob(recordBmob));
                            }
                            DataSupport.saveAll(records);
                        }else{
                            Log.d("HomeFragment","Record:失败："+e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });

                break;
            case R.id.bt_update_to_Server:
                List<BmobObject> recordBmobs = new ArrayList<>();
                mIndex++;
                for (int i = 0; i < mAllRecord.size(); i++) {
                    if (i >= (mIndex -1)*50 && i < mIndex*50) {
                        recordBmobs.add(Utils.castToBmobFromRecord(mAllRecord.get(i)));
                    } else {
                        continue;
                    }

                }
                // 批量添加
                new BmobBatch().insertBatch(recordBmobs).doBatch(new QueryListListener<BatchResult>() {
                    @Override
                    public void done(List<BatchResult> o, BmobException e) {
                        if(e==null){
                            for(int i=0;i<o.size();i++){
                                BatchResult result = o.get(i);
                                BmobException ex =result.getError();
                                if(ex==null){
                                    Log.d("HomeFragment","Record:第"+i+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
                                }else{
                                    Log.d("HomeFragment","Record:第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
                                }
                            }
                        }else{
                            Log.d("HomeFragment","Record:失败："+e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });
                break;
        }
    }

    private void getData(){
        List<Record> allRecord =  DataSupport.select("dateYmd","receiptsMoney", "driveMoney", "expensesMoney").find(Record.class);
        double[] allData = computeAllMoney(allRecord);
        mExpenses.setText(String.format(getResources().getString(R.string.total_expenses), String.valueOf(allData[0])));
        mReceipts.setText(String.format(getResources().getString(R.string.total_receipts),String.valueOf(allData[1])));
        mBalance.setText(String.format(getResources().getString(R.string.total_balance),
                Utils.sub(allData[1], allData[0]) > 0?"+"+Utils.sub(allData[1], allData[0]):""+Utils.sub(allData[1], allData[0])));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        List<Record> todayRecord = DataSupport.select("receiptsMoney", "driveMoney", "expensesMoney","expensesType", "distance", "fuelConsumption").where("dateYmd = ?", format.format(new Date())).find(Record.class);
//        List<FuelConsumption> todayFuelConsumption = DataSupport.select("fuelConsumption").where("dateYmd = ?", format.format(new Date())).find(FuelConsumption.class);
        double[] todayMoney = computeMoney(todayRecord);
        double todayFuel = computFuel(todayRecord);
        mTodayFuel.setText(String.format(getResources().getString(R.string.av_fuel_consumption),""+todayFuel));
        mTodayEx.setText(String.format(getResources().getString(R.string.expenses),""+todayMoney[0]));
        mTodayRe.setText(String.format(getResources().getString(R.string.receipts),""+todayMoney[1]));




        List<Record> weekRecord = DataSupport.select("receiptsMoney", "driveMoney", "expensesMoney","expensesType", "distance", "fuelConsumption").where("dateYmd >= ?", mMonday).find(Record.class);
//        List<FuelConsumption> weekFuelConsumption = DataSupport.select("fuelConsumption").where("dateYmd >= ?", mMonday).find(FuelConsumption.class);
        double[] weekMoney = computeMoney(weekRecord);
        mWeekEx.setText(String.format(getResources().getString(R.string.expenses),""+weekMoney[0]));
        mWeekRe.setText(String.format(getResources().getString(R.string.receipts),""+weekMoney[1]));
        double weekFuel = computFuel(weekRecord);
        mWeekFuel.setText(String.format(getResources().getString(R.string.av_fuel_consumption),""+weekFuel));


        List<Record> monthRecord = DataSupport.select("receiptsMoney", "driveMoney", "expensesMoney","expensesType", "distance", "fuelConsumption").where("dateYmd >= ?", mFirstDayOfMonth).find(Record.class);
//        List<FuelConsumption> monthFuelConsumption = DataSupport.select("fuelConsumption").where("dateYmd >= ?", mFirstDayOfMonth).find(FuelConsumption.class);
        double[] monthMoney = computeMoney(monthRecord);
        mMonthEx.setText(String.format(getResources().getString(R.string.expenses),""+monthMoney[0]));
        mMonthRe.setText(String.format(getResources().getString(R.string.receipts),""+monthMoney[1]));
        double monthFuel = computFuel(monthRecord);
        mMonthFuel.setText(String.format(getResources().getString(R.string.av_fuel_consumption),""+monthFuel));


        mAllRecord =  DataSupport.order("dateYmd desc").find(Record.class);
        Log.d("HomeFragment", "mAllRecord's size is:"+mAllRecord.size());

    }

    private double[] computeAllMoney(List<Record> records) {
        double[] result = new double[2];
        double allExpenses = 0.0d;
        double allReceipts = 0.0d;
        for (Record record : records){
            allExpenses = Utils.add(allExpenses, record.getExpensesMoney());
            allReceipts = Utils.add(allReceipts, record.getReceiptsMoney());
        }
        result[0] = Utils.parseDouble(allExpenses);
        result[1] = Utils.parseDouble(allReceipts);
        return result;
    }

    private double[] computeMoney(List<Record> records) {
        double[] result = new double[2];
        double allExpenses = 0.0d;
        double allReceipts = 0.0d;
        for (Record record : records){
            allExpenses = Utils.add(allExpenses, record.getDriveMoney());
            if (!"加油费".equals(record.getExpensesType())){
                allExpenses = Utils.add(allExpenses, record.getExpensesMoney());
            }
            allReceipts = Utils.add(allReceipts, record.getReceiptsMoney());
        }
        result[0] = Utils.parseDouble(allExpenses);
        result[1] = Utils.parseDouble(allReceipts);
        return result;
    }

    private double computFuel(List<Record> records){
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
