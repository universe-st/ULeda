package ecnu.uleda;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class MyTaskInFo extends AppCompatActivity
 implements OnClickListener {
    private ImageButton mback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_already_release);

        Button released=(Button)findViewById(R.id.released) ;
        Button doing=(Button)findViewById(R.id.doing) ;
        Button evaluate=(Button)findViewById(R.id.evaluate) ;
        Button done=(Button)findViewById(R.id.done) ;
        Button evaluation=(Button)findViewById(R.id.evaluation) ;
         mback = (ImageButton)findViewById(R.id.Release_back);

        released.setOnClickListener(this);
        doing.setOnClickListener(this);
        evaluate.setOnClickListener(this);
        done.setOnClickListener(this);
        evaluation.setOnClickListener(this);
        mback.setOnClickListener(this);

        Intent i=getIntent();
        String i0=i.getStringExtra("data");
        int i1=Integer.valueOf(i0);
        replaceFragementByNum(i1);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.released:
                replaceFragement(new MyTask_ReleasedFragment());
                break;
            case R.id.doing:
                replaceFragement(new MyTask_DoingFragment());
                break;
            case R.id.done:
                replaceFragement(new MyTask_DoneFragment());
                break;
            case R.id.evaluate:
                replaceFragement(new MyTask_ToEvaluateFragment());
                break;
            case R.id.evaluation:
                replaceFragement(new MyTask_MyEvaluationFragment());
                break;
            case R.id.Release_back:
                finish();
            default:
                break;
        }

    }

    private void replaceFragement(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.task_layout,fragment);
        transaction.commit();
    }

    private void replaceFragementByNum(int x){
        switch (x) {
            case 1: {
                replaceFragement(new MyTask_ReleasedFragment());
                break;
            }
            case 2: {
                replaceFragement(new MyTask_DoingFragment());
                break;
            }
            case 3: {
                replaceFragement(new MyTask_ToEvaluateFragment());
                break;
            }
            case 4: {
                replaceFragement(new MyTask_DoneFragment());
                break;
            }
            case 5: {
                replaceFragement(new MyTask_MyEvaluationFragment());
                break;
            }
            default:
                break;
        }

    }
}
