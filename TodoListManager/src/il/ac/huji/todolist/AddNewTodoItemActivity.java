package il.ac.huji.todolist;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

public class AddNewTodoItemActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_new_item_layout);
		
		final Intent resIntent = new Intent();
		final Resources res = getResources();
		
		
		Button btnOk = (Button) findViewById(R.id.btnOK);
		btnOk.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				
				EditText et = (EditText) findViewById(R.id.edtNewItem);
				resIntent.putExtra(res.getString(R.string.ext_title), et.getText().toString());
				
				DatePicker dp = (DatePicker) findViewById(R.id.datePicker);
				resIntent.putExtra(res.getString(R.string.ext_date), 
						new Date(dp.getYear() - 1900,dp.getMonth(),dp.getDayOfMonth()));
				setResult(RESULT_OK, resIntent);
				finish();
			}
		});
		
		
		Button btnCancel = (Button) findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED,resIntent);
				finish();
			}
		});
	}
}
