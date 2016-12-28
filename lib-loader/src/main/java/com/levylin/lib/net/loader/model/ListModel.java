package com.levylin.lib.net.loader.model;

import java.util.List;

/**
 * 列表数据
 * Created by LinXin on 2016/6/21 9:23.
 */
public abstract class ListModel<INFO, ITEM> extends Model<INFO> {
    protected static int FIRST_PAGE = 1;
    protected int page = FIRST_PAGE;
    protected List<ITEM> mList;
    protected boolean isLoadMoreFromEnd = true;
    protected boolean hasNext = false;

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
        List<ITEM> mapList = map(response);
        hasNext = ensureHasNext(response, mapList);
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

    public boolean hasNext() {
        return hasNext;
    }

    /**
     * 确认是否有下一页
     *
     * @param response
     * @param mapList
     * @return
     */
    protected abstract boolean ensureHasNext(INFO response, List<ITEM> mapList);

    /**
     * 数据转换
     *
     * @param response
     * @return
     */
    protected abstract List<ITEM> map(INFO response);
}