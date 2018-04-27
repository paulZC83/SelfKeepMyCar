package cn.sh.changxing.selfkeepmycar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.sh.changxing.selfkeepmycar.R;
import cn.sh.changxing.selfkeepmycar.db.model.RecordGroup;

/**
 * Created by ZengChao on 2017/11/17.
 */

public class RecordExAdapter extends BaseExpandableListAdapter {
    private List<RecordGroup> mGroups;
    private Context mContext;
    public RecordExAdapter(Context context, List<RecordGroup> groups){
        mContext = context;
        mGroups = groups;
    }
    public void updateData(List<RecordGroup> groups) {
        mGroups = groups;
        notifyDataSetChanged();
    }
    @Override
    public int getGroupCount() {
        return mGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mGroups.get(groupPosition).getChildList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition).getParent();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view;
        ParentViewHolder parentViewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_record_parent, parent, false);
            parentViewHolder = new ParentViewHolder(view);
            view.setTag(parentViewHolder);
        } else {
            view = convertView;
            parentViewHolder = (ParentViewHolder) view.getTag();
        }
        parentViewHolder.tvMonth.setText(mGroups.get(groupPosition).getParent().getMonth()+"月");
        parentViewHolder.tvExMoney.setText(String.format(mContext.getResources().getString(R.string.expenses_only), mGroups.get(groupPosition).getParent().getExpenses()+""));
        parentViewHolder.tvReMoney.setText(String.format(mContext.getResources().getString(R.string.receipts_only),mGroups.get(groupPosition).getParent().getReceipts()+""));
        parentViewHolder.tvBalance.setText(String.format(mContext.getResources().getString(R.string.balance),mGroups.get(groupPosition).getParent().getBalance()+""));
        if (isExpanded) {
            parentViewHolder.ivIndicator.setImageResource(R.drawable.navigate_up);
        } else {
            parentViewHolder.ivIndicator.setImageResource(R.drawable.navigate_down);
        }
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view;
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_record_child, parent, false);
            childViewHolder = new ChildViewHolder(view);
            view.setTag(childViewHolder);
        } else {
            view = convertView;
            childViewHolder = (ChildViewHolder) view.getTag();
        }
        childViewHolder.tvDay.setText(mGroups.get(groupPosition).getChildList().get(childPosition).getDay()+"日");
        childViewHolder.tvDayOfWeek.setText(mGroups.get(groupPosition).getChildList().get(childPosition).getDayOfWeek());
        childViewHolder.tvTarget.setText(mGroups.get(groupPosition).getChildList().get(childPosition).getTarget());
        childViewHolder.tvExType.setText(mGroups.get(groupPosition).getChildList().get(childPosition).getExType());
        childViewHolder.tvExMoney.setText(mGroups.get(groupPosition).getChildList().get(childPosition).getExMoney()+"");
        childViewHolder.tvReType.setText(mGroups.get(groupPosition).getChildList().get(childPosition).getReType());
        childViewHolder.tvReMoney.setText(mGroups.get(groupPosition).getChildList().get(childPosition).getReMoney()+"");
        childViewHolder.tvFu.setText("油耗："+mGroups.get(groupPosition).getChildList().get(childPosition).getFuelConsumption());
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public class ParentViewHolder{
        private TextView tvMonth, tvExMoney, tvReMoney, tvBalance;
        private ImageView ivIndicator;
        public ParentViewHolder(View view) {
            tvMonth = ((TextView) view.findViewById(R.id.item_parent_month));
            tvExMoney = ((TextView) view.findViewById(R.id.item_parent_ex));
            tvReMoney = ((TextView) view.findViewById(R.id.item_parent_re));
            tvBalance = ((TextView) view.findViewById(R.id.item_parent_balance));
            ivIndicator = ((ImageView) view.findViewById(R.id.iv_indicator));
        }
    }

    public class ChildViewHolder{
        private TextView tvDay, tvDayOfWeek, tvTarget, tvExType, tvExMoney, tvReType, tvReMoney, tvFu;
        public ChildViewHolder(View view) {
            tvDay = ((TextView) view.findViewById(R.id.item_child_day));
            tvDayOfWeek = ((TextView) view.findViewById(R.id.item_child_day_of_week));
            tvTarget = ((TextView) view.findViewById(R.id.item_child_target));
            tvExType = ((TextView) view.findViewById(R.id.item_child_ex_type));
            tvExMoney = ((TextView) view.findViewById(R.id.item_child_ex));
            tvReType = ((TextView) view.findViewById(R.id.item_child_re_type));
            tvReMoney = ((TextView) view.findViewById(R.id.item_child_re));
            tvFu = ((TextView) view.findViewById(R.id.item_child_fu));
        }
    }
}
