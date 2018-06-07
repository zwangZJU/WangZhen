package cn.zju.id21732091.wangzhen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.text.Selection;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CalculatorActivity extends AppCompatActivity implements View.OnClickListener{

    private GridLayout mGlKeyboard;
    private EditText mEtShowDetail;
    private EditText mEtCalcResult;
    private int columnCount; //列数
    private int screenWidth; //屏幕宽度
    private int screenHeight;
    private double result;
    private boolean FLAG_PRESS_EQUAL = false;
    ArrayList<String> resultList = new ArrayList<String>() ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        mGlKeyboard = findViewById(R.id.gl_keyboard);
        mEtShowDetail = findViewById(R.id.et_show_details);
        mEtShowDetail.setMovementMethod(ScrollingMovementMethod.getInstance());
        mEtShowDetail.setSelection(mEtShowDetail.getText().length(),mEtShowDetail.getText().length());

        mEtCalcResult = findViewById(R.id.et_calc_result);
        mEtShowDetail.setSelection(mEtShowDetail.getText().length());
        columnCount = mGlKeyboard.getColumnCount();
        screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        mEtShowDetail.setHeight(screenHeight / 3 +10);


        findViewById(R.id.btn_0).setOnClickListener(this);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
        findViewById(R.id.btn_7).setOnClickListener(this);
        findViewById(R.id.btn_8).setOnClickListener(this);
        findViewById(R.id.btn_9).setOnClickListener(this);
        findViewById(R.id.btn_clean).setOnClickListener(this);
        findViewById(R.id.ibt_backspace).setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_sub).setOnClickListener(this);
        findViewById(R.id.btn_mul).setOnClickListener(this);
        findViewById(R.id.btn_div).setOnClickListener(this);
        findViewById(R.id.btn_equle).setOnClickListener(this);
        findViewById(R.id.btn_dot).setOnClickListener(this);


        for (int i = 0; i < mGlKeyboard.getChildCount(); i++) {
            if(i!=1){
                Button button = (Button) mGlKeyboard.getChildAt(i);
                button.setWidth(screenWidth / columnCount);
                button.setHeight((screenHeight) / 10);
            }else{
                ImageButton imageButton = (ImageButton) mGlKeyboard.getChildAt(i);
                imageButton.setMaxWidth(screenWidth / columnCount);

                imageButton.setMaxHeight((screenHeight) / 10);
            }

        }
    }


    private String calculate(double a, double b,  String[] strArr){

        String operator = strArr[strArr.length-2];
        // ＋
        if(operator.equals("+")){
            return subZeroAndDot(String.valueOf(a + b));
        }else if(operator.equals("－")){
            return subZeroAndDot(String.valueOf(a - b));
        }else if(operator.equals("÷")){

            if( b == 0){
                return "不能除以0";
            }
            DecimalFormat format = new DecimalFormat("0.0000000000");
            int baseLength = 4;
            int strArrlength = strArr.length;
            if(strArrlength>=baseLength){
                while(baseLength <= strArrlength && (!strArr[strArrlength-baseLength].equals("+") && !strArr[strArrlength-baseLength].equals("－"))){
                    baseLength += 2;
                }
                if(baseLength >= strArrlength){
                    return subZeroAndDot(String.valueOf(a / b));
                }else{
                    if(strArr[strArrlength-baseLength].equals("+")){
                        double prefix = Double.parseDouble(resultList.get((strArrlength-baseLength-1)/2));
                        double suffix = Double.parseDouble(resultList.get(strArrlength/2-1)) - prefix;

                        return subZeroAndDot(String.valueOf(suffix / b + prefix));
                    }else{
                        double prefix = Double.parseDouble(resultList.get((strArrlength-baseLength-1)/2));
                        double suffix = prefix - Double.parseDouble(resultList.get(strArrlength/2-1));
                        return subZeroAndDot(String.valueOf(prefix - suffix / b));
                    }
                }
            }else{
                return subZeroAndDot(String.valueOf(format.format(a/b)));
            }


        }else if(operator.equals("×")){
            int baseLength = 4;
            int strArrlength = strArr.length;
            if(strArrlength>=baseLength){
                while(baseLength <= strArrlength && (!strArr[strArrlength-baseLength].equals("+") && !strArr[strArrlength-baseLength].equals("－"))){
                    baseLength += 2;
                }
                if(baseLength >= strArrlength){
                    return subZeroAndDot(String.valueOf((float)(a * b)));
                }else{
                    if(strArr[strArrlength-baseLength].equals("+")){
                        double prefix = Double.parseDouble(resultList.get((strArrlength-baseLength-1)/2));
                        double suffix = Double.parseDouble(resultList.get(strArrlength/2-1)) - prefix;

                        return subZeroAndDot(String.valueOf((float)(suffix * b + prefix)));
                    }else{
                        double prefix = Double.parseDouble(resultList.get((strArrlength-baseLength-1)/2));
                        double suffix = prefix - Double.parseDouble(resultList.get(strArrlength/2-1));
                        return subZeroAndDot(String.valueOf((float)(prefix - suffix * b)));
                    }
                }

            }else{
                return subZeroAndDot(String.valueOf((float)(a * b)));
            }


        }else{
            return null;
        }

    }

    //获得当前光标所在的行
    private int getCurrentCursorLine(EditText editText) {
        int selectionStart = Selection.getSelectionStart(editText.getText());
        Layout layout = editText.getLayout();
        if (selectionStart != -1) {
            return layout.getLineForOffset(selectionStart) + 1;
        }
        return -1;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);


        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.getItem(0).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id == R.id.action_close){
            finish();
            return true;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_submit) {
           // new SubmitProgram().doSubmit(this,"C1");
            Toast.makeText(this, R.string.pause_submit,Toast.LENGTH_SHORT).show();

            return true;
        }else if(id == R.id.action_status){
            startActivity(new Intent(this,StatusActivity.class));
            finish();
            return true;
        }else if(id == R.id.action_file_storage){
            startActivity(new Intent(this,FileStorageActivity.class));
            finish();
            return true;
        }else if(id == R.id.action_settings){
            startActivity(new Intent(this,SettingsActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        String str = mEtShowDetail.getText().toString();

        switch(view.getId()){
            case R.id.btn_0:
            case R.id.btn_1:
            case R.id.btn_2:
            case R.id.btn_3:
            case R.id.btn_4:
            case R.id.btn_5:
            case R.id.btn_6:
            case R.id.btn_7:
            case R.id.btn_8:
            case R.id.btn_9:
            case R.id.btn_dot:
                mEtCalcResult.setTextColor(getResources().getColor(R.color.gray));

                if(str.equals("0") ){
                    mEtShowDetail.setText("");
                  //  result = Double.parseDouble(mEtCalcResult.getText().toString().substring(1));
                }else if(FLAG_PRESS_EQUAL){
                    mEtShowDetail.setText("");
                    result = Double.parseDouble(mEtCalcResult.getText().toString().substring(1));
                    FLAG_PRESS_EQUAL = false;
                }

                mEtShowDetail.append(((Button)view).getText());
                str = mEtShowDetail.getText().toString();
                String[] strArr = str.split(" ");

                if(strArr.length == 1){
                    mEtCalcResult.setText("=" + strArr[strArr.length-1]);

                }else{
                    mEtCalcResult.setText("=" + calculate(result, Double.parseDouble(strArr[strArr.length-1].trim()),strArr));
                }

                break;
            case R.id.btn_clean:
                mEtCalcResult.setTextColor(getResources().getColor(R.color.gray));
                mEtShowDetail.setText("0");
                mEtCalcResult.setText("0");
                result = 0;
                resultList.clear();
                FLAG_PRESS_EQUAL = false;
                break;
            case R.id.ibt_backspace:
                mEtCalcResult.setTextColor(getResources().getColor(R.color.gray));
                String[] strBackArr = str.split(" ");
                if(str.substring(str.length()-1).matches("([0-9]+)?")&&(strBackArr.length>1 || str.length()==1)) {
                    mEtShowDetail.setText(str.length()>1?str.substring(0, str.length() - 1):"0");
                    if(str.length()>1){
                        if(strBackArr[strBackArr.length-1].length() == 1 ){//非第一行删除一个数字，删除后该行只剩运算符
                            //  mEtShowDetail.setText(str.length()>1?str.substring(0, str.length() - 1):"0");
                            mEtCalcResult.setText("="+subZeroAndDot(resultList.get(resultList.size()-1)));
                            //  resultList.remove(resultList.size()-1);
                            result = Double.parseDouble(mEtCalcResult.getText().toString().substring(1));

                        }else{//非第一行删除一个数字，删除后该行还有运算符和数字
                            String[] newStrBackArr = mEtShowDetail.getText().toString().split(" ");
                            mEtCalcResult.setText("=" + calculate(Double.parseDouble(resultList.get(resultList.size()-1).trim()), Double.parseDouble(newStrBackArr[newStrBackArr.length-1].trim()),newStrBackArr));
                            // mEtCalcResult.setText("="+subZeroAndDot(resultList.get(resultList.size()-1)));
                            Log.i("sssss",String.valueOf(resultList.size()));
                            Log.i("sssss","a");
                            //result = Double.parseDouble(mEtCalcResult.getText().toString().substring(1));
                        }
                    }else{//只剩一个数字
                        mEtCalcResult.setText("=0");
                        resultList.clear();
                    }


                }else if(str.substring(str.length()-1).equals(" ")){//如果删除的是个运算符
                    mEtShowDetail.setText(str.length()>1?str.substring(0, str.length() - 4):"0");

                    mEtCalcResult.setText("="+subZeroAndDot(resultList.get(resultList.size()-1)));
                    resultList.remove(resultList.size()-1);
                    result = Double.parseDouble(mEtCalcResult.getText().toString().substring(1));

                }else if(strBackArr.length==1){//第一行有多个数字，删除最后一个
                    mEtShowDetail.setText(str.length()>1?str.substring(0, str.length() - 1):"0");
                    mEtCalcResult.setText("="+mEtShowDetail.getText().toString());
                }
                break;
            case R.id.btn_add:
            case R.id.btn_sub:
            case R.id.btn_mul:
            case R.id.btn_div:
                mEtCalcResult.setTextColor(getResources().getColor(R.color.gray));
                if(!FLAG_PRESS_EQUAL) {
                    if (str.substring(str.length() - 1).equals(" ")) {
                        mEtShowDetail.setText(str.substring(0, str.length() - 2) + ((Button) view).getText() + " ");
                    } else {
                        //mEtShowDetail.setText(str + "\n" + ((Button)view).getText());
                        mEtShowDetail.append("\n" + " " + ((Button) view).getText() + " ");
                        try {
                            result = Double.parseDouble(mEtCalcResult.getText().toString().substring(1));
                        } catch (Exception e) {
                            result = 0;
                        }

                        String s = String.valueOf(result);
                        Log.i("CalcActivity", s + " ");
                        resultList.add(String.valueOf(result));
                        Log.i("CalcActivity", resultList.get(0) + " ");
                    }
                }else{
                    FLAG_PRESS_EQUAL = false;
                    mEtShowDetail.setText(mEtCalcResult.getText().toString().substring(1)+"\n"+" "+((Button) view).getText() + " ");

                }
                break;
            case R.id.btn_percent:
                mEtCalcResult.setTextColor(getResources().getColor(R.color.gray));
                break;
            case R.id.btn_equle:
                mEtCalcResult.setTextColor(getResources().getColor(R.color.colorAccent));
                FLAG_PRESS_EQUAL = true;
                result = Double.parseDouble(mEtCalcResult.getText().toString().substring(1));
                resultList.clear();
                break;

        }
    }

    // 去掉小数点后多余的零
    public static String subZeroAndDot(String s){
        if(s.indexOf(".") > 0){
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }
}
