package cn.sh.changxing.selfkeepmycar.db.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by ZengChao on 2017/11/15.
 */

public class RecordBmob extends BmobObject{
    private int id;
    // 日期-年月日
    private String dateYmd;
    // 干嘛去
    private String target;
    // 其他用途
    private String otherTarget;
    // 行驶距离
    private double distance;
    // 收支类型
    private int exAndReType;
    // 收入类型
    private String receiptsType;
    // 收入金额
    private double receiptsMoney;
    // 驾驶花费金额
    private double driveMoney;
    // 支出类型
    private String expensesType;
    // 支出金额
    private double expensesMoney;
    // 其他费用备注
    private String otherExpenses;
    // 汽油单价
    private double unitPrice;
    // 收支
    private double total;
    // 油耗
    private double fuelConsumption;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateYmd() {
        return dateYmd;
    }

    public void setDateYmd(String dateYmd) {
        this.dateYmd = dateYmd;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getExAndReType() {
        return exAndReType;
    }

    public void setExAndReType(int exAndReType) {
        this.exAndReType = exAndReType;
    }

    public String getReceiptsType() {
        return receiptsType;
    }

    public void setReceiptsType(String receiptsType) {
        this.receiptsType = receiptsType;
    }

    public String getExpensesType() {
        return expensesType;
    }

    public void setExpensesType(String expensesType) {
        this.expensesType = expensesType;
    }

    public String getOtherTarget() {
        return otherTarget;
    }

    public void setOtherTarget(String otherTarget) {
        this.otherTarget = otherTarget;
    }

    public double getReceiptsMoney() {
        return receiptsMoney;
    }

    public void setReceiptsMoney(double receiptsMoney) {
        this.receiptsMoney = receiptsMoney;
    }

    public double getDriveMoney() {
        return driveMoney;
    }

    public void setDriveMoney(double driveMoney) {
        this.driveMoney = driveMoney;
    }

    public double getExpensesMoney() {
        return expensesMoney;
    }

    public void setExpensesMoney(double expensesMoney) {
        this.expensesMoney = expensesMoney;
    }

    public String getOtherExpenses() {
        return otherExpenses;
    }

    public void setOtherExpenses(String otherExpenses) {
        this.otherExpenses = otherExpenses;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(double fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", dateYmd='" + dateYmd + '\'' +
                ", target='" + target + '\'' +
                ", otherTarget='" + otherTarget + '\'' +
                ", distance=" + distance +
                ", exAndReType=" + exAndReType +
                ", receiptsType='" + receiptsType + '\'' +
                ", receiptsMoney=" + receiptsMoney +
                ", driveMoney=" + driveMoney +
                ", expensesType='" + expensesType + '\'' +
                ", expensesMoney=" + expensesMoney +
                ", otherExpenses='" + otherExpenses + '\'' +
                ", unitPrice=" + unitPrice +
                ", total=" + total +
                ", fuelConsumption=" + fuelConsumption +
                '}';
    }
}
