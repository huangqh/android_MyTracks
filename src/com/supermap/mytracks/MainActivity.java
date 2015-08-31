package com.supermap.mytracks;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.supermap.mytracks.activity.LoginActivity;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.RelativeLayout;

/**
 * <p>
 * 应用入口，启动页。
 * </p>
 * @author ${huangqh}
 * @version ${Version}
 * @since 1.0.0
 * 
 */
public class MainActivity extends Activity {
    @ViewInject(R.id.rl_main)
    private RelativeLayout rl_main = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        // 启动页动画结束跳转到登录页面
        ObjectAnimator animator = ObjectAnimator.ofFloat(this.rl_main, "alpha", 0.1f, 1.0f);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(intent);
                MainActivity.this.finish();
            }
        });
        animator.setDuration(1500);
        animator.start();
    }
}
