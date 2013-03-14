package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class TodoListManagerActivity extends Activity {

	private List<TodoItem> items = new ArrayList<TodoItem>();
	private ArrayAdapter<TodoItem> todoItemsAdapter;
	private ListView list;
	
	// holds the index of last selected item in the list -
	// used for workaround the dPad issue
	private int checkedItemPos = -1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);
        
        list = (ListView)findViewById(R.id.lstTodoItems);
        todoItemsAdapter = new TodoListAdapter(this, items);
        list.setAdapter(todoItemsAdapter);
        list.setOnItemClickListener(
        		new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> adapter, View v,int pos, long id) {
						checkedItemPos = pos;
					}
        		
        		}
        );
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_list_manager, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	switch(item.getItemId()){
    	
    	case R.id.menuItemAdd:
    		EditText et = (EditText)findViewById(R.id.edtNewItem);
    		String newItem = et.getText().toString();
    		todoItemsAdapter.add(new TodoItem(newItem));
    		checkedItemPos = -1;
    		et.setText("");
    		return true;
    		
    	case R.id.menuItemDelete:
    		if(checkedItemPos == -1) return true;
    		
    		// workaround for the dPad issue....
    		
    		//todoItemsAdapter.remove((TodoItem) list.getItemAtPosition(list.getCheckedItemPosition()));		
    		todoItemsAdapter.remove((TodoItem) list.getItemAtPosition(checkedItemPos));
    		
    		checkedItemPos = -1;
    		return true;
    	}
    	
    	
    	return super.onOptionsItemSelected(item);
    } 
    
}
