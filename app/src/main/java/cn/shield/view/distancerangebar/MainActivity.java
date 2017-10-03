package cn.shield.view.distancerangebar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import cn.shield.view.rangebar.DistanceRangeBar;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements DistanceRangeBar.OnRangeBarChangeListener {

    private TextView mTvCurrentIndex, mTvCurrentIndex2;
    private DistanceRangeBar mDistanceRangeBar, mDistanceRangeBar2;
    private Button mBtnSetIndex, mBtnVisibility, mBtnSetIndex2;
    private EditText mEtIndex, mEtIndex2;
    private FrameLayout mParentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mTvCurrentIndex = (TextView) findViewById(R.id.tv);
        mDistanceRangeBar = (DistanceRangeBar) findViewById(R.id.rangebar);
        mBtnSetIndex = (Button) findViewById(R.id.btn);
        mEtIndex = (EditText) findViewById(R.id.et);
        mParentLayout = (FrameLayout) findViewById(R.id.layout);
        mBtnVisibility = (Button) findViewById(R.id.btn_visibility);

        mTvCurrentIndex2 = (TextView) findViewById(R.id.tv2);
        mDistanceRangeBar2 = (DistanceRangeBar) findViewById(R.id.rangebar2);
        mBtnSetIndex2 = (Button) findViewById(R.id.btn2);
        mEtIndex2 = (EditText) findViewById(R.id.et2);

        mBtnSetIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mEtIndex.getText().toString()) && TextUtils.isDigitsOnly(mEtIndex.getText().toString())) {
                    mDistanceRangeBar.setRangePinsByIndices(Integer.parseInt(mEtIndex.getText().toString()));
                }
            }
        });
        mDistanceRangeBar.setOnRangeBarChangeListener(this);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 100; i <= 500; i += 50) {
            list.add(String.valueOf(i) + "米");
        }
        // 直接设置 集合，显示集合内容
        mDistanceRangeBar.setTickNum(list);

        //---------------------------------------------------------------------------------//

        mBtnSetIndex2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mEtIndex2.getText().toString()) && TextUtils.isDigitsOnly(mEtIndex2.getText().toString())) {
                    mDistanceRangeBar2.setRangePinsByIndices(Integer.parseInt(mEtIndex2.getText().toString()));
                }
            }
        });
        mDistanceRangeBar2.setOnRangeBarChangeListener(this);

        // 直接设置 个数
        mDistanceRangeBar2.setTickNum(12);
    }

    @Override
    public void onRangeChangeListener(DistanceRangeBar distanceRangeBar, int rightPinIndex, boolean isMove) {
        switch (distanceRangeBar.getId()) {
            case R.id.rangebar:
                mTvCurrentIndex.setText("下标是：" + rightPinIndex + "...isMove：" + isMove);
                break;
            case R.id.rangebar2:
                mTvCurrentIndex2.setText("下标是：" + rightPinIndex + "...isMove：" + isMove);
                break;
            default:
                break;
        }
    }

    public void onClick(View view) {
        mParentLayout.setVisibility(mParentLayout.getVisibility() == VISIBLE ? GONE : VISIBLE);
        mBtnVisibility.setText(mParentLayout.getVisibility() == VISIBLE ? "点我隐藏" : "点我显示");
    }
}
