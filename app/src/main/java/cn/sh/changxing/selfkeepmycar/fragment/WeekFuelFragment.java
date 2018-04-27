package cn.sh.changxing.selfkeepmycar.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.sh.changxing.selfkeepmycar.R;
import cn.sh.changxing.selfkeepmycar.activity.MainActivity;
import cn.sh.changxing.selfkeepmycar.adapter.WeekFuelAdapter;
import cn.sh.changxing.selfkeepmycar.db.model.ItemWeek;
import cn.sh.changxing.selfkeepmycar.db.model.Record;
import cn.sh.changxing.selfkeepmycar.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeekFuelFragment extends Fragment {
    private final String TAG = "WeekFuelFragment";
    private ListView mLv;
    private Button mBack;
    private int year = -1;
    private int month = -1;
    private WeekFuelAdapter mAdapter;

    public WeekFuelFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate...");
        Bundle bundle = getArguments();
        if (bundle!=null){
            year = bundle.getInt("YEAR");
            month = bundle.getInt("MONTH");
            Log.d(TAG, "YEAR:"+year+"---MONTH:"+month);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView...");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_week_fuel, container, false);
        mLv = ((ListView) view.findViewById(R.id.week_lv));
        mBack = ((Button) view.findViewById(R.id.week_back));
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).intoFuel();
            }
        });
        mAdapter = new WeekFuelAdapter(getActivity(), getData());
        mLv.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        return view;
    }

    // 只有hidden过才会调用,第一次进oncreate不会进这里
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged...:"+hidden);
        if (!hidden) {
            updateOnDataChanged();
        }
    }

    private void updateOnDataChanged(){
        Bundle bundle = getArguments();
        if (bundle!=null){
            year = bundle.getInt("YEAR");
            month = bundle.getInt("MONTH");
            Log.d(TAG, "updateOnDataChanged YEAR:"+year+"---MONTH:"+month);
            mAdapter.updateData(getData());
        }
    }


    private List<ItemWeek> getData(){
        List<ItemWeek> data = new ArrayList<>();
        List<List<String>> weeks =  Utils.getWeekListOfMonth(Utils.getDayListOfMonth(year, month));
        for (int i = 0; i < weeks.size(); i++) {
            List<String> week = weeks.get(i);
            ItemWeek itemWeek = new ItemWeek();
            itemWeek.setTitle("第 "+(i+1)+" 周");
            List<Record> shRecords = new ArrayList<>();
            List<Record> xiaRecords = new ArrayList<>();
            List<Record> records = DataSupport.select("dateYmd","target","fuelConsumption","distance","receiptsMoney","driveMoney","expensesMoney").where("dateYmd >= ? and dateYmd <= ?", week.get(0), week.get(week.size()-1)).order("dateYmd asc").find(Record.class);
            for (int j = 0; j < records.size(); j++) {
                if ("上班".equals(records.get(j).getTarget())) {
                    shRecords.add(records.get(j));
                } else if ("下班".equals(records.get(j).getTarget())) {
                    xiaRecords.add(records.get(j));
                }


            }
            int shDays = computShDays(shRecords);
            int xiaDays = computShDays(xiaRecords);
            itemWeek.setShFuel("上班油耗："+computFuel(shRecords));
            itemWeek.setXiaFuel("下班油耗："+computFuel(xiaRecords));
            itemWeek.setShDis("日均里程："+ computDis(shRecords, shDays));
            itemWeek.setXiaDis("日均里程："+ computDis(xiaRecords, xiaDays));
            itemWeek.setShIn("日均收入："+ computIn(shRecords, shDays));
            itemWeek.setXiaIn("日均收入："+ computIn(xiaRecords, xiaDays));
            itemWeek.setShOut("日均支出："+ computOut(shRecords, shDays));
            itemWeek.setXiaOut("日均支出："+ computOut(xiaRecords, xiaDays));
            itemWeek.setShDays("有效天数："+shDays+"天");
            itemWeek.setXiaDays("有效天数："+xiaDays+"天");
            itemWeek.setDate(week.get(0)+" ~ "+ week.get(week.size()-1));
            data.add(itemWeek);
        }
        return data;
    }

    /*
     * 计算有效天数
     */
    private int computShDays(List<Record> records){
        int count = 0;
        if (records != null && records.size() > 0) {
            String lastYmd = records.get(0).getDateYmd();
            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);
                if (record.getFuelConsumption() > 0.0d) {
                    if (i == 0) {
                        count++;
                    }
                    if (!lastYmd.equals(record.getDateYmd())) {
                        count++;
                    }
                }
                lastYmd = record.getDateYmd();
            }
        }
        return count;
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

    private double computDis(List<Record> records, int count){
        double totalDis = 0.0d;
        double result = 0.0d;
        if (records != null && records.size() > 0) {
            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);
                if (record.getFuelConsumption() > 0.0d) {
                    totalDis = Utils.add(totalDis, record.getDistance());
                }
            }
            if (count > 0) {
                result = Utils.div(totalDis, count, 1);
            }
        }
        return result;
    }


    private double computIn(List<Record> records, int count){
        double totalIn = 0.0d;
        double result = 0.0d;
        if (records != null && records.size() > 0) {
            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);
                if (record.getFuelConsumption() > 0.0d) {

                    totalIn = Utils.add(totalIn, record.getReceiptsMoney());
                }
            }
            if (count > 0){
                result = Utils.div(totalIn, count, 1);
            }
        }

        return result;
    }


    private double computOut(List<Record> records, int count){
        double totalOut = 0.0d;
        double result = 0.0d;
        if (records != null && records.size() > 0) {
            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);
                if (record.getFuelConsumption() > 0.0d) {
                    totalOut = Utils.add(totalOut, record.getDriveMoney());
                    if (!"加油费".equals(record.getExpensesType())){
                        totalOut = Utils.add(totalOut, record.getExpensesMoney());
                    }
                }
            }
            if (count > 0){
                result = Utils.div(totalOut, count, 1);
            }
        }


        return result;
    }

}
