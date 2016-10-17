package ar.fi.uba.jobify.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by smpiano on 9/28/16.
 */
public abstract class ConfirmDialog implements DialogInterface.OnClickListener {
    private Context context;

    public ConfirmDialog(Context ctx) {
        this.context = ctx;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                onConfirm();
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                break;
        }
    }

    public abstract void onConfirm();

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Estás seguro?").setPositiveButton("Si", this)
                .setNegativeButton("No", this).show();
    }

}
