package org.ea.sqrl.activites;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;

import org.ea.sqrl.R;
import org.ea.sqrl.activites.base.LoginBaseActivity;
import org.ea.sqrl.processors.CommunicationFlowHandler;
import org.ea.sqrl.processors.SQRLStorage;


public class CPSMissingActivity  extends LoginBaseActivity {
    private static final String TAG = "CPSMissingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cps_missing);

        communicationFlowHandler = CommunicationFlowHandler.getInstance(this, handler);
        setupBasePopups(getLayoutInflater(), true);

        final TextView txtSite = findViewById(R.id.txtSite);
        txtSite.setText(communicationFlowHandler.getDomain());

        SQRLStorage storage = SQRLStorage.getInstance();

        communicationFlowHandler.setDoneAction(() -> {
            storage.clear();
            handler.post(() -> {
                progressPopupWindow.dismiss();
                closeActivity();
            });
        });

        communicationFlowHandler.setErrorAction(() -> {
            storage.clear();
            storage.clearQuickPass(CPSMissingActivity.this);
            handler.post(() -> progressPopupWindow.dismiss());
        });

        final Button btnCPSContinue = findViewById(R.id.btnCPSContinue);
        btnCPSContinue.setOnClickListener(v -> {
            progressPopupWindow.showAtLocation(progressPopupWindow.getContentView(), Gravity.CENTER, 0, 0);
            new Thread(() -> {
                communicationFlowHandler.setNoCPSServer();
                communicationFlowHandler.handleNextAction();
            }).start();
        });

        final Button btnCPSCancel = findViewById(R.id.btnCPSCancel);
        btnCPSCancel.setOnClickListener(v -> {
            startActivity(new Intent(this, SimplifiedActivity.class));
            this.finish();
        });
    }

    @Override
    protected void closeActivity() {
        CPSMissingActivity.this.finishAffinity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CPSMissingActivity.this.finishAndRemoveTask();
        }
    }
}
