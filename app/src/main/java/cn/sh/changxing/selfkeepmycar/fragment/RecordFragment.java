package cn.sh.changxing.selfkeepmycar.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.victor.loading.newton.NewtonCradleLoading;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.sh.changxing.selfkeepmycar.R;
import cn.sh.changxing.selfkeepmycar.adapter.RecordExAdapter;
import cn.sh.changxing.selfkeepmycar.base.BaseFragment;
import cn.sh.changxing.selfkeepmycar.db.model.Record;
import cn.sh.changxing.selfkeepmycar.db.model.RecordChild;
import cn.sh.changxing.selfkeepmycar.db.model.RecordGroup;
import cn.sh.changxing.selfkeepmycar.db.model.RecordParent;
import cn.sh.changxing.selfkeepmycar.utils.Utils;
import cn.sh.changxing.selfkeepmycar.view.ConfirmCommDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecordFragment extends BaseFragment {

    private Unbinder unbinder;
    private RecordExAdapter adapter;
    private List<RecordGroup> mData = new ArrayList<>();
    @BindView(R.id.record_ex_lv)
    ExpandableListView mExListview;
    @BindView(R.id.balance)
    TextView mBalance;
    @BindView(R.id.expenses)
    TextView mExpenses;
    @BindView(R.id.receipts)
    TextView mReceipts;
    @BindView(R.id.record_bk)
    ImageView mRecordBk;
    @BindView(R.id.record_title_bk)
    ImageView mTitleBk;
    @BindView(R.id.newton_cradle_loading)
    NewtonCradleLoading mLoading;
    public RecordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        unbinder = ButterKnife.bind(this, view);
        adapter = new RecordExAdapter(getActivity(),mData);
        mExListview.setAdapter(adapter);
        mExListview.setGroupIndicator(null);
//        Glide.with(getActivity()).load("http://img5.imgtn.bdimg.com/it/u=14690098,4013785429&fm=27&gp=0.jpg").into(mTitleBk);
//        Glide.with(getActivity()).load("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3797329642,3426897074&fm=11&gp=0.jpg").into(mRecordBk);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setTopData();
        List<Record> records = DataSupport.order("dateYmd desc").find(Record.class);
        if (records.size()>0) {
            mData = getData(records);
            adapter.updateData(mData);
            mExListview.expandGroup(0);
        } else {
            Toast.makeText(getActivity(), "暂无流水记录", Toast.LENGTH_SHORT).show();
        }

        mExListview.setOnItemLongClickListener(onItemLongClickListener);

//        mExListview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                final ConfirmCommDialog dialog = new ConfirmCommDialog(getActivity());
//                dialog.setContentText("确认删除？");
//                dialog.setOnCancelClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                dialog.setOnConfirmClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        dialog.dismiss();
//                    }
//                });
//                return false;
//            }
//        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final long packedPosition = mExListview.getExpandableListPosition(position);
            final int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
            final int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);
            //长按的是group的时候，childPosition = -1
            if (childPosition != -1) {
                final ConfirmCommDialog dialog = new ConfirmCommDialog(getActivity());
                StringBuffer buffer = new StringBuffer();
                buffer.append("确认删除以下记录？").append("\n").append(mData.get(groupPosition).getChildList().get(childPosition).getDay()).append("日")
                        .append("  ").append(mData.get(groupPosition).getChildList().get(childPosition).getDayOfWeek())
                        .append("  ").append(mData.get(groupPosition).getChildList().get(childPosition).getTarget())
                        .append("  ").append("油耗:").append(mData.get(groupPosition).getChildList().get(childPosition).getFuelConsumption())
                        .append("  ").append(mData.get(groupPosition).getChildList().get(childPosition).getExType())
                        .append("  ").append(mData.get(groupPosition).getChildList().get(childPosition).getExMoney())
                        .append("  ").append(mData.get(groupPosition).getChildList().get(childPosition).getReType())
                        .append("  ").append(mData.get(groupPosition).getChildList().get(childPosition).getReMoney());

                dialog.setContentText(buffer.toString());
                dialog.setOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.setOnConfirmClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLoading.setVisibility(View.VISIBLE);
                        mLoading.start();
//                        mLoading.setLoadingColor(R.color.colorPrimary);
                        final int id = mData.get(groupPosition).getChildList().get(childPosition).getId();
                        Record record = DataSupport.where("id = ?", id+"").findFirst(Record.class);
                        record.setObjectId(record.getObjId());
                        record.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.d("RecordFragment", "RecordBmob已删除");
                                } else {
                                    Log.d("RecordFragment", "RecordBmob删除失败:>"+e.getMessage());
                                }
                                DataSupport.delete(Record.class, id);
                                List<Record> records = DataSupport.order("dateYmd desc").find(Record.class);
                                mData = getData(records);
                                adapter.updateData(mData);
                                dialog.dismiss();
                                mLoading.stop();
                                mLoading.setVisibility(View.GONE);
                            }
                        });


//                        BmobQuery<RecordBmob> query = new BmobQuery<>();
//                        //查询playerName叫“比目”的数据
//                        query.addWhereEqualTo("id", id);
//                        //执行查询方法
//                        query.findObjects(new FindListener<RecordBmob>() {
//                            @Override
//                            public void done(List<RecordBmob> object, BmobException e) {
//                                if(e==null){
//                                    String bmobObjectId = object.get(0).getObjectId();
//                                    object.get(0).delete(bmobObjectId, new UpdateListener() {
//                                        @Override
//                                        public void done(BmobException e) {
//                                            if (e == null) {
//                                                Log.d("RecordFragment", "RecordBmob已删除");
//                                            } else {
//                                                Log.d("RecordFragment", "RecordBmob删除失败:>"+e.getMessage());
//                                            }
//
//                                        }
//                                    });
//                                }else{
//                                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
//                                }
//                            }
//                        });



                    }
                });
                dialog.show();
                return false;
            }
            return true;
        }
    };

    private void setTopData(){
        List<Record> allRecord =  DataSupport.select("dateYmd","receiptsMoney", "driveMoney", "expensesMoney").find(Record.class);
        double[] allData = computeAllMoney(allRecord);
        mExpenses.setText(String.format(getResources().getString(R.string.total_expenses), String.valueOf(allData[0])));
        mReceipts.setText(String.format(getResources().getString(R.string.total_receipts),String.valueOf(allData[1])));
        mBalance.setText(String.format(getResources().getString(R.string.total_balance),
                Utils.sub(allData[1], allData[0]) > 0?"+"+Utils.sub(allData[1], allData[0]):""+Utils.sub(allData[1], allData[0])));
    }

    private List<RecordGroup> getData(List<Record> records){

        List<RecordGroup> groups = new ArrayList<>();
        RecordGroup group = new RecordGroup();
        RecordParent parent = new RecordParent();
        List<RecordChild> childList = new ArrayList<>();
        String lastMonth = records.get(0).getDateYmd().substring(0, 7);
        double parentExMonty = 0.0d;
        double parentReMonty = 0.0d;
        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            String newMoth = record.getDateYmd().substring(0, 7);
            String day = record.getDateYmd().substring(8, 10);
            RecordChild recordChild = new RecordChild();

            if (newMoth.equals(lastMonth)) {
                // 同一月
                parent.setMonth(newMoth);
                parentExMonty = Utils.add(parentExMonty, record.getExpensesMoney());
                parentReMonty = Utils.add(parentReMonty, record.getReceiptsMoney());
                parent.setExpenses(parentExMonty);
                parent.setReceipts(parentReMonty);
                parent.setBalance(Utils.sub(parentReMonty, parentExMonty));

                recordChild.setId(record.getId());
                recordChild.setMonth(newMoth);
                recordChild.setDay(day);
                recordChild.setDayOfWeek(Utils.getDayOfWeek(record.getDateYmd()));
                recordChild.setTarget("其他".equals(record.getTarget())?record.getOtherTarget():record.getTarget());
                recordChild.setExType("其他费用".equals(record.getExpensesType())?record.getOtherExpenses():record.getExpensesType());
                recordChild.setExMoney(record.getExpensesMoney());
                recordChild.setReType(record.getReceiptsType());
                recordChild.setReMoney(record.getReceiptsMoney());
                recordChild.setFuelConsumption(record.getFuelConsumption());
                childList.add(recordChild);
            } else {
                group.setParent(parent);
                group.setChildList(childList);
                groups.add(group);

                group = new RecordGroup();

                parent = new RecordParent();
                parentExMonty = record.getExpensesMoney();
                parentReMonty = record.getReceiptsMoney();

                parent.setMonth(newMoth);
                parent.setExpenses(parentExMonty);
                parent.setReceipts(parentReMonty);
                parent.setBalance(Utils.sub(parentReMonty, parentExMonty));

                childList = new ArrayList<>();
                recordChild.setId(record.getId());
                recordChild.setMonth(newMoth);
                recordChild.setDay(day);
                recordChild.setDayOfWeek(Utils.getDayOfWeek(record.getDateYmd()));
                recordChild.setTarget("其他".equals(record.getTarget())?record.getOtherTarget():record.getTarget());
                recordChild.setExType(record.getExpensesType());
                recordChild.setExMoney(record.getExpensesMoney());
                recordChild.setReType(record.getReceiptsType());
                recordChild.setReMoney(record.getReceiptsMoney());
                recordChild.setFuelConsumption(record.getFuelConsumption());
                childList.add(recordChild);


            }

            if (i == records.size() -1) {
                group.setParent(parent);
                group.setChildList(childList);
                groups.add(group);
            }
            lastMonth = newMoth;
        }
        return groups;
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


}
