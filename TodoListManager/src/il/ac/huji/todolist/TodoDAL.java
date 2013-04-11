package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class TodoDAL {
	
	
	private Context _context;
	private SQLiteDatabase db;
	private Cursor cursor;
	private TodoDBHelper db_helper;
	
	private final String KEY_ID = "_id";
	private final String KEY_TITLE = "title";
	private final String KEY_DUE = "due";
	private static final String DB_NAME = "todo_db";
	private static final String DB_TABLE_NAME = "todo";
	

	/*
	 *  Constructor
	 *  
	 *  initializes sqlite database and parse interface
	 */
	public TodoDAL(Context context) {  
		 
		 this._context = context;
		 
	     Resources res = context.getResources();
	     
	     // init parse interface
	     Parse.initialize(context, res.getString(R.string.parseApplication), res.getString(R.string.clientKey));
	     ParseUser.enableAutomaticUser();
		 
		 // init db
	     db_helper = new TodoDBHelper(this._context,DB_NAME,DB_TABLE_NAME);
	     db = db_helper.getWritableDatabase();
	}
	
	/*
	 *  Opens DB. 
	 *  
	 *  returns true on success.
	 */
	public boolean openDB(){
		db = db_helper.getWritableDatabase();
		return (db != null ? true:false);
	}
	
	/*
	 *   Close DB.
	 *
	 */
	public void closeDB(){
		db_helper.close();
	}
	
	/*
	 * Close cursor - this should be called when main activity is destroyed.
	 */
	public void closeCursor() {
		cursor.close();
	}
	
	
	/*
	 *   Store the given todoItem in DB and parse i/f 
	 *   returns true on success.
	 */
	public boolean insert(ITodoItem todoItem) {
		
		String title    = todoItem.getTitle();
		Date due_date   = todoItem.getDueDate();
		
		
		long retVal = -1;
		
		// insert to sqlite database
		if(! openDB() ) return false;
		
		ContentValues vals = new ContentValues();
		vals.put(KEY_TITLE, title);
		if(due_date != null)
			vals.put(KEY_DUE, due_date.getTime());
		else
			vals.putNull(KEY_DUE);
		retVal = db.insert(DB_TABLE_NAME, null, vals);
		
		closeDB();
		
		// insert to parse cloud
		ParseObject pVals = new ParseObject("todo");
		pVals.put(KEY_TITLE, title);
		if(due_date != null) pVals.put(KEY_DUE, due_date.getTime());
		pVals.saveInBackground();
		
		return retVal != -1;  
	}
	
	/*
	 * update the due_date field of an item corresponding to the given item's title.
	 * 
	 * returns true on success
	 * 
	 */
	public boolean update(ITodoItem todoItem) {
		
		String title    = todoItem.getTitle();
		final Date due_date   = todoItem.getDueDate();
		long retVal = -1;
		
		
		// update in sqlite database
		if(! openDB()) return false;
		
		ContentValues vals = new ContentValues();
		
		if(due_date != null)
			vals.put(KEY_DUE, due_date.getTime());
		else
			vals.putNull(KEY_DUE);
		
		retVal = db.update(DB_TABLE_NAME,vals,"title = '"+title+"'",null);
		
		closeDB();
		
		
		// update from parse cloud
		ParseQuery query = new ParseQuery("todo");
		query.whereEqualTo(KEY_TITLE, title);
		
		query.findInBackground(new FindCallback() {			

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if(e == null){
					for(ParseObject obj : objects){
						
						if(due_date != null) obj.put(KEY_DUE, due_date.getTime());
						else                 obj.put(KEY_DUE, JSONObject.NULL);
						
						obj.saveInBackground();
					}
				}
			}
		});

		return retVal > 0; 		 
	}
	
	/*
	 * delete the given item from storage.
	 * 
	 * returns true on success.
	 */
	public boolean delete(ITodoItem todoItem) {
		
		String title    = todoItem.getTitle();
		Date due_date   = todoItem.getDueDate();
		int retVal = 0;

		// delete from sqlite database
		if(! openDB()) return false;
		
		ContentValues vals = new ContentValues();
		vals.put(KEY_TITLE, title);
		vals.put(KEY_DUE, due_date.getTime());
		retVal = db.delete(DB_TABLE_NAME, KEY_TITLE + " = '" + title+"'", null);
		
		closeDB();
		
		// delete from parse cloud
		ParseQuery query = new ParseQuery("todo");
		query.whereEqualTo(KEY_TITLE, title);
		
		query.findInBackground(new FindCallback() {			

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				if(e == null){
					for(ParseObject obj : objects){
						obj.deleteInBackground();
					}
				}
			}
		});
		
		return retVal > 0;  
	}
	
	/*
	 *  return a list of all items currently stored.
	 */
	public List<ITodoItem> all() {
		
		List<ITodoItem> ret = new ArrayList<ITodoItem>();
		
		if(! openDB()) return ret;
		
		cursor = db.query(DB_TABLE_NAME, new String[]{KEY_ID,KEY_TITLE,KEY_DUE}, null, null, null, null, null);
		
		int iTitle = cursor.getColumnIndex(KEY_TITLE);
		int iDue = cursor.getColumnIndex(KEY_DUE);
		
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
			ret.add( new TodoItem( cursor.getString(iTitle), new Date(cursor.getLong(iDue)) ) );			
		}

		closeDB();
		return ret;  
	}
	
	
	// FOR DEBUGGING
	public void printMSG(String msg){
		System.out.println("=========================================================\n");
		System.out.println("\t\t" + msg + "\n");
		System.out.println("=========================================================\n");
	}
}