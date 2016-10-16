package io.coderunner.chordmaster.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.coderunner.chordmaster.R;

public class WelcomeFragment extends Fragment {

    @BindView(R.id.btnQuickChord) Button mBtnQuickChord;
    @BindView(R.id.btnChooseChord) Button mBtnChooseChord;
    private Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_welcome, container, false);
        ButterKnife.bind(this, root);

        mBtnQuickChord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PracticeActivity.class);
                startActivity(intent);
            }
        });

        mBtnChooseChord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChordsActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }
}
