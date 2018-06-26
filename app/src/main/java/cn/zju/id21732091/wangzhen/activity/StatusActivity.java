package cn.zju.id21732091.wangzhen.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import cn.iipc.android.tweetlib.YambaClient;
import cn.iipc.android.tweetlib.YambaClientException;
import cn.zju.id21732091.wangzhen.R;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class StatusActivity extends AppCompatActivity implements TextWatcher{

    private EditText mEtEditStatus;
    private TextView mTvCharStatistic;
    private TextView mTvAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        //透明状态栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
       // window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_status);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sp.getString("username",getResources().getString(R.string.author_info));

        mTvCharStatistic = findViewById(R.id.tv_char_statistic);
        mEtEditStatus = findViewById(R.id.et_edit_status);
        mTvAuthor = findViewById(R.id.tv_author_info);
        mTvAuthor.setText(username);
        mEtEditStatus.addTextChangedListener(this);
        findViewById(R.id.btn_send_status).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(getApplicationContext(),"点击发送",Toast.LENGTH_SHORT).show();
                String status = mEtEditStatus.getText().toString();
                if(TextUtils.isEmpty(status)){
                    Toast.makeText(getApplicationContext(),"还没有输入内容呢", Toast.LENGTH_SHORT).show();
                    return;
                }
                new PostTask().execute(status);
                //new SubmitProgram().doSubmit(StatusActivity.this,"D2");
            }
        });

        findViewById(R.id.tv_cancel_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StatusActivity.this,MainActivity.class));
                finish();
            }
        });


    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        int count = editable.length();
        mTvCharStatistic.setText(String.valueOf(count) + "/360");
        if(count>=300){
            mTvCharStatistic.setTextColor(getResources().getColor(R.color.warning_yellow));
        }
        if(count>360){
            mTvCharStatistic.setTextColor(getResources().getColor(R.color.colorAccent));

        }
    }

    private final class PostTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(StatusActivity.this);
            String username = sp.getString("username","");//wangzhen21732091
            String password = sp.getString("password","");//123456

            YambaClient yambaCloud = new YambaClient(username,password);
            try {
                yambaCloud.postStatus(strings[0]);
                return "Successfully posted: "+strings[0].length()+"chars";
            } catch (YambaClientException e) {
                e.printStackTrace();
                return "Failed to post to yamba service";
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Toast.makeText(StatusActivity.this,result,Toast.LENGTH_LONG).show();
            //mTvAuthor.setText(result + " By<"+getPackageName()+">");
            if(result.startsWith("Successfully")){
                mEtEditStatus.setText("");
            }
        }
    }
}
