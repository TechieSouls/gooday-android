package com.deploy.fragment.gathering;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.deploy.R;
import com.deploy.fragment.CenesFragment;

public class GatheirngMessageFragment extends CenesFragment {

    public static String TAG = "GatheirngMessageFragment";

    private Button btnCancel, btnDone;
    private EditText etMessageArea;
    private TextView tvCharactersCount;

    String message = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_gathering_message, container, false);

        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        btnDone = (Button) view.findViewById(R.id.btn_done);

        etMessageArea = (EditText) view.findViewById(R.id.et_message_area);
        tvCharactersCount = (TextView) view.findViewById(R.id.tv_characters_count);

        btnCancel.setOnClickListener(onClickListener);
        btnDone.setOnClickListener(onClickListener);

        etMessageArea.addTextChangedListener(textWatcherListener);

        if (message != null) {
            etMessageArea.setText(message);
        }
        return view;

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_cancel:
                    getFragmentManager().popBackStack();
                    break;

                case R.id.btn_done:
                    Intent intent = new Intent(getContext(), GatheirngMessageFragment.class);
                    intent.putExtra("message", etMessageArea.getText().toString());
                    getTargetFragment().onActivityResult(getTargetRequestCode(), getActivity().RESULT_OK, intent);
                    getFragmentManager().popBackStack();
                    break;
            }
        }
    };

    TextWatcher textWatcherListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

                int totalCount = 150;
                int typedInCharsCount = s.toString().length();
                int remainigChars = totalCount - typedInCharsCount;
                if (remainigChars >= 0) {
                    tvCharactersCount.setText(remainigChars+" Characters Remaining");
                }
        }
    };
}
