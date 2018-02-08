package com.yalematta.android.bakingproject.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;


public class ListViewService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewFactory(this.getApplicationContext());
    }
}
