package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class TodoListManagerActivity extends Activity {

	private List<TodoItem> items = new ArrayList<TodoItem>();
	private ArrayAdapter<TodoItem> todoItemsAdapter;
	private ListView list;

	private String CALL_PREFIX_STR = "Call ";
	
	final private int requestCode = 1234;
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

        registerForContextMenu(list);
        
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
    	//super.onCreateContextMenu(menu, v, menuInfo);
    	
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;	
    	
    	
    	TextView itemTitleView = (TextView) info.targetView.findViewById(R.id.txtTodoTitle);
    	String itemTitle = itemTitleView.getText().toString();
    	
    	// inflate menu
    	getMenuInflater().inflate(R.menu.list_context_menu, menu);

    	MenuItem mItem = menu.findItem(R.id.menuItemCall);
    	
    	// if this is not a call task - remove the call item from the menu
    	if(itemTitle.startsWith(CALL_PREFIX_STR)) {
    		mItem.setTitle(itemTitle).setVisible(true);
    	}
    	else{
    		mItem.setVisible(false);
    	}
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	TodoItem listItem = (TodoItem) list.getItemAtPosition(info.position);
    	switch(item.getItemId()) {
    	
    	case R.id.menuItemCall:
    		String uriTelNum = listItem.getTitle().replaceAll(" +"," ").replace(CALL_PREFIX_STR, "tel:");
    		
    		Intent dial = new Intent(Intent.ACTION_DIAL,Uri.parse(uriTelNum));
    		startActivity(dial);
    		break;
    		
    	case R.id.menuItemDelete:
    		
    		todoItemsAdapter.remove(listItem);
    		checkedItemPos = -1;
    		break;
    	default:
    		return super.onContextItemSelected(item);
    	}
    	return true;
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
    		
    		Intent intent = new Intent(this,AddNewTodoItemActivity.class);
    		startActivityForResult(intent, requestCode);
    		
    		
//    		EditText et = (EditText)findViewById(R.id.edtNewItem);
//    		String newItem = et.getText().toString();
//    		todoItemsAdapter.add(new TodoItem(newItem));
//    		checkedItemPos = -1;
//    		et.setText("");
    		return true;
    		
    	case R.id.menuDelete:
    		if(checkedItemPos == -1) return true;
    		
    		// workaround for the dPad issue....
    		
    		//todoItemsAdapter.remove((TodoItem) list.getItemAtPosition(list.getCheckedItemPosition()));		
    		todoItemsAdapter.remove((TodoItem) list.getItemAtPosition(checkedItemPos));
    		
    		checkedItemPos = -1;
    		return true;
    	}
    	
    	
    	return super.onOptionsItemSelected(item);
    } 
    
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
    	
    	Resources res = getResources();
    	
    	switch (reqCode) {
    	
		case requestCode:
			
			
	    	if(resultCode == RESULT_OK && 
	           data.hasExtra(res.getString(R.string.ext_date)) && 
	    	   data.hasExtra(res.getString(R.string.ext_title)) ) {
	    		
	    		String newItemTitle = data.getStringExtra(res.getString(R.string.ext_title));
	    		Date dueDate        = (Date) data.getSerializableExtra(res.getString(R.string.ext_date));
	    		todoItemsAdapter.add(new TodoItem(newItemTitle,dueDate));
	    	}
			break;

		default:
			break;
		}
    }
    
    
}
