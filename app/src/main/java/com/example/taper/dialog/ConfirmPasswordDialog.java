package com.example.taper.dialog;

//android:icon="@mipmap/taper_logo"
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.taper.R;

public class ConfirmPasswordDialog extends DialogFragment {
    public interface OnConfirmPasswordListener{
        public void onConfirmPassword(String password);
    }
    OnConfirmPasswordListener monConfirmPasswordListener;
    TextView mPassword;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.dialog_confirm_password,container,false);
        mPassword=(TextView) view.findViewById(R.id.confirm_password);

        TextView confirmDialog=(TextView) view.findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password=mPassword.getText().toString();
                if(!password.equals("")) {
                    monConfirmPasswordListener.onConfirmPassword(password);
                    getDialog().dismiss();
                }
                else{
                    Toast.makeText(getActivity(), "You must enter your password", Toast.LENGTH_SHORT).show();
                }

            }
        });
        TextView cancelDialog=(TextView) view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            monConfirmPasswordListener=(OnConfirmPasswordListener)getTargetFragment();
        }
        catch (ClassCastException e){

        }
    }
}
