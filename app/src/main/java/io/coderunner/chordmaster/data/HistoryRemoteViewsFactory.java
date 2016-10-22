package io.coderunner.chordmaster.data;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.coderunner.chordmaster.R;
import io.coderunner.chordmaster.data.model.Score;
import io.coderunner.chordmaster.ui.widget.HistoryWidgetProvider;
import io.coderunner.chordmaster.util.Constants;
import io.coderunner.chordmaster.util.Util;

public class HistoryRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    public final String LOG_TAG = HistoryRemoteViewsFactory.class.getSimpleName();

    private Context mContext;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private String mUserId;
    private DatabaseReference mScoresRef;
    private List<Score> mScores = new ArrayList<Score>();

    public HistoryRemoteViewsFactory(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "Creating remote views factory");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    mUserId = user.getUid();
                    getData();
                } else {
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        mFirebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "login successful as " + mFirebaseAuth.getCurrentUser().getUid());
                    mUserId = mFirebaseAuth.getCurrentUser().getUid();
                    getData();
                } else {
                    Log.e(LOG_TAG, "Unable to login");
                }
            }
        });

    }

    private void getData(){
        mScoresRef = mDatabase.child(Constants.getFirebaseLocationUsers(mContext)).child(mUserId).child(Constants.getFirebaseLocationScores(mContext));
        getHistory();
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
    }

    @Override
    public int getCount() {
        return mScores.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(),
                R.layout.list_item_widget_history);

        Score score = mScores.get(position);

        views.setTextViewText(R.id.widget_history_chordpair, score.getChangeString());
        views.setContentDescription(R.id.widget_history_chordpair, score.getChange().getContentDescriptionString());
        views.setTextViewText(R.id.widget_history_score, String.valueOf(score.getScore()));
        views.setContentDescription(R.id.widget_history_score, String.valueOf(score.getScore()));
        views.setTextViewText(R.id.widget_history_achieved, Util.getDateFormatter(mContext).format(score.getCreatedTimestamp()));

        final Intent fillInIntent = new Intent();
        views.setOnClickFillInIntent(R.id.widget_history_list_item, fillInIntent);
        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    private void getHistory() {
        Log.d(LOG_TAG, "Retrieving data from Firebase");
        mScoresRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(LOG_TAG, "Data retrieved");
                GenericTypeIndicator<Map<String, Score>> t = new GenericTypeIndicator<Map<String, Score>>() {
                };
                Map<String, Score> result = dataSnapshot.getValue(t);
                if (result != null) {
                    Collection<Score> scores = dataSnapshot.getValue(t).values();

                    mScores.clear();
                    mScores.addAll(scores);

                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
                    int appWidgetIds[] = appWidgetManager
                            .getAppWidgetIds(new ComponentName(mContext, HistoryWidgetProvider.class));
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_history_list);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
