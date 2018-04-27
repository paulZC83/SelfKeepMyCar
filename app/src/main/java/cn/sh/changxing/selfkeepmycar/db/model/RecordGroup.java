package cn.sh.changxing.selfkeepmycar.db.model;

import java.util.List;

/**
 * Created by ZengChao on 2017/11/17.
 */

public class RecordGroup {
    private RecordParent parent;
    private List<RecordChild> childList;

    public RecordParent getParent() {
        return parent;
    }

    public void setParent(RecordParent parent) {
        this.parent = parent;
    }

    public List<RecordChild> getChildList() {
        return childList;
    }

    public void setChildList(List<RecordChild> childList) {
        this.childList = childList;
    }
}
