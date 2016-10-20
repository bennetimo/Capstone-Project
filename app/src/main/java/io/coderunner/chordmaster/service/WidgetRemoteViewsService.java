package io.coderunner.chordmaster.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

import io.coderunner.chordmaster.data.HistoryRemoteViewsFactory;

public class WidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new HistoryRemoteViewsFactory(getBaseContext());
    }

}