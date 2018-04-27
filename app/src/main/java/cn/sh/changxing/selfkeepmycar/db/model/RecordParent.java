package cn.sh.changxing.selfkeepmycar.db.model;

/**
 * Created by ZengChao on 2017/11/17.
 */

public class RecordParent
{
    private String month;
    private double expenses;
    private double receipts;
    private double balance;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getExpenses() {
        return expenses;
    }

    public void setExpenses(double expenses) {
        this.expenses = expenses;
    }

    public double getReceipts() {
        return receipts;
    }

    public void setReceipts(double receipts) {
        this.receipts = receipts;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
