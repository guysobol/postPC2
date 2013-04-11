package il.ac.huji.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDBHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	
	public TodoDBHelper(Context context,String db_name,String db_table_name) {
		super(context,db_name,null,DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String DATABASE_CREATE = "CREATE TABLE "+ "todo" +" ("
				+ " _id INTEGER primary key autoincrement,"
				+ " title TEXT NOT NULL," 
				+ " due INTEGER" 
				+ " );";
			
		
		System.out.println(DATABASE_CREATE);
		
		
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

}
