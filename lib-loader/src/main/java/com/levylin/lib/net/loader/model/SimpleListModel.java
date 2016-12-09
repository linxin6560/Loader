package com.levylin.lib.net.loader.model;

import java.util.List;

/**
 * Created by LinXin on 2016/12/9 11:20.
 */
public abstract class SimpleListModel<ITEM> extends ListModel<List<ITEM>, ITEM> {
    public SimpleListModel(List<ITEM> list) {
        super(list);
    }

    @Override
    public List<ITEM> map(List<ITEM> response) {
        return response;
    }
}
