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
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/*
 *  Main activity
 */
public class TodoListManagerActivity extends Activity {

	// holds todo items
	private List<ITodoItem> items = new ArrayList<ITodoItem>();
	
	// adapter for the ListView 'list' and the collection 'items'
	private ArrayAdapter<ITodoItem> todoItemsAdapter;
	
	// displays todo items from the 'items' collection 
	private ListView list;
	
	// An Item with this prefix will have
	// a "Call" option in it's context-menu.
	private String CALL_PREFIX_STR = "Call ";
	
	// request code for activity that handles items addition
	final private int requestCode = 1234;
	
	// request code for activity that handles items update
	final private int requestUpdateCode = 1235;
	
	// holds the index of last selected item in the list -
	// used for workaround the dPad issue
	private int checkedItemPos = -1;

	//This class (TodoDAL) handles all data access functionality
	private TodoDAL dal;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list_manager);

        // create data access class
        dal = new TodoDAL(this);
        
        // populate items with data stored on previous sessions
        items.addAll(dal.all());
        
        //bind data and view via adapter
        list = (ListView)findViewById(R.id.lstTodoItems);
        todoItemsAdapter = new TodoListAdapter(this, items);
        list.setAdapter(todoItemsAdapter);
        
        // register ListView for context-menu
        registerForContextMenu(list);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {

    	// retrieve data
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;	
    	TextView itemTitleView = (TextView) info.targetView.findViewById(R.id.txtTodoTitle);
    	String itemTitle = itemTitleView.getText().toString();
    	
    	// inflate menu
    	getMenuInflater().inflate(R.menu.list_context_menu, menu);

    	// get the "call" menu item 
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
    
    	// get menu & item info and determine item position in list
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	checkedItemPos = info.position;
    	TodoItem listItem = (TodoItem) list.getItemAtPosition(info.position);
    	
    	
    	switch(item.getItemId()) {
    	
    		// execute a phone call
	    	case R.id.menuItemCall:
	    		String uriTelNum = listItem.getTitle().replaceAll(" +"," ").replace(CALL_PREFIX_STR, "tel:");
	    		Intent dial = new Intent(Intent.ACTION_DIAL,Uri.parse(uriTelNum));
	    		startActivity(dial);
	    		break;
	    	
	    		
	    	// update item in list	
	    	case R.id.menuItemUpdate:
	    		
	    		Intent intent = new Intent(this,AddNewTodoItemActivity.class);
	    		startActivityForResult(intent, requestUpdateCode);
	    		return true;	
	    			
	    		
	    	// delete item from list
	    	case R.id.menuItemDelete:
	    			    		
	    		//update external storage(db & parse cloud)
	    		dal.delete(listItem);
	    		
	    		// update local data
	    		todoItemsAdapter.remove(listItem);
	    		
	    		// reset checked item variable
	    		checkedItemPos = -1;
	    		break;
	    		
	    	// unrecognized option - use parent class handler if any.	
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
    	
    	Intent intent;
    	boolean success = false;
    	checkedItemPos = list.getSelectedItemPosition();
    	
    	switch(item.getItemId()){
    	
    		// add item 
    		case R.id.menuItemAdd:
	    		intent = new Intent(this,AddNewTodoItemActivity.class);
	    		startActivityForResult(intent, requestCode);
	    		return true;
	    		
	    	// delete item	
	    	case R.id.menuDelete:
	    		
	    		// item selected - delete it
	    		if(checkedItemPos != -1){
	    			
	    			
	    			//delete from external storage
	    			success = dal.delete((ITodoItem) list.getItemAtPosition(checkedItemPos));
	    			
	    			// delete from local data
	    			if(success)
	    				todoItemsAdapter.remove((TodoItem) list.getItemAtPosition(checkedItemPos)); 			    			
	    			
	    			checkedItemPos = -1;
	    		}
	    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    } 
    
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	dal.closeCursor();
    	dal.closeDB();
    }
    
    
    
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
    	
    	Resources res = getResources();
    	boolean success = false;
    	
    	switch (reqCode) {

    		// add request
			case requestCode:

		    	if(resultCode == RESULT_OK && 
		           data.hasExtra(res.getString(R.string.ext_date)) && 
		    	   data.hasExtra(res.getString(R.string.ext_title)) ) {
		    		
		    		String newItemTitle = data.getStringExtra(res.getString(R.string.ext_title));
		    		Date dueDate        = (Date) data.getSerializableExtra(res.getString(R.string.ext_date));
		    		TodoItem i = new TodoItem(newItemTitle,dueDate);
		    		
		    		// update external storage
		    		success = dal.insert(i);
		    		
		    		// update local data
		    		if(success)
		    			todoItemsAdapter.add(i);
		    	}
				break;
			
				
			// update request	
		    // 
			// This wasn't  a requirement - only for DEBUG
			case requestUpdateCode:
				
		    	if(resultCode == RESULT_OK && 
		           data.hasExtra(res.getString(R.string.ext_date)) && 
		    	   data.hasExtra(res.getString(R.string.ext_title)) ) {
		    		
		    		String itemTitle = data.getStringExtra(res.getString(R.string.ext_title));
		    		Date dueDate     = (Date) data.getSerializableExtra(res.getString(R.string.ext_date));
		    		TodoItem i = new TodoItem(itemTitle,dueDate);
		    		
		    		// update external storage
		    		success = dal.update(i);
		    		
		    		
		    		// if update was successful - update local data
		    		ArrayList<Integer> indexes = new ArrayList<Integer>();
		    		if(success){
		    			
		    			for(int k = 0; k < items.size(); ++k){
		    				
		    				if(items.get(k).getTitle().equals(itemTitle)){
		    					indexes.add(k);
		    				}
		    			}
		    			
		    			
		    			int ind;
						for(int j = 0; j < indexes.size(); ++j){
								ind = indexes.get(j);
					    		todoItemsAdapter.remove(items.get(ind));
					    		todoItemsAdapter.insert(i, ind);
															
						}
		    		}
		    	}
				break;				
	
			default:
				break;
		}
    }
    
    
}
