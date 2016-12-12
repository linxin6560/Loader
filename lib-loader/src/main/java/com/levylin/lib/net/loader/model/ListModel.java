package com.levylin.lib.net.loader.model;

import java.util.List;

/**
 * 列表数据
 * INFO:返回数据中data的数据类型，若data为列表，则用List<Item>,若是普通Info类，则用相应的类
 * ITEM:列表数据子项类
 * Created by LinXin on 2016/6/21 9:23.
 */
public abstract class ListModel<INFO, ITEM> extends Model<INFO> {
    protected static int FIRST_PAGE = 1;
    protected int page = FIRST_PAGE;
    protected List<ITEM> mList;
    protected List<ITEM> mapList;//由map方法转换的零时数据列表
    protected boolean isLoadMoreFromEnd = true;

    public ListModel(List<ITEM> list) {
        this.mList = list;
    }

    public void clear() {
        mList.clear();
    }

    public void preLoadNext() {
        page++;
        cacheStrategy.setIsReadCache(false);
        cacheStrategy.setIsSaveCache(false);
    }

    @Override
    public void preRefresh() {
        super.preRefresh();
        page = FIRST_PAGE;
    }

    public int size() {
        return mList.size();
    }

    public int getPage() {
        return page;
    }

    @Override
    public void setData(boolean isRefreshing, INFO response) {
        mapList = map(response);
        if (mapList == null) {
            return;
        }
        if (page == FIRST_PAGE) {
            clear();
        }
        if (isLoadMoreFromEnd) {
            mList.addAll(mapList);
        } else {
            mList.addAll(0, mapList);
        }
    }

    @Override
    public boolean isEmpty() {
        return mList.isEmpty();
    }

    public abstract boolean hasNext(INFO response);

    public abstract List<ITEM> map(INFO response);
}