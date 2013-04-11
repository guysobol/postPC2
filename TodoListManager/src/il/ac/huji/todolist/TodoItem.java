package il.ac.huji.todolist;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 *    This class represents a todo item.
 */
public class TodoItem implements ITodoItem{

		// field
		private String title;
		
		// field
		private Date dueDate;
		
		// date format 
		final private String DATE_FORMAT = "dd/MM/yyyy"; 
		
		/*
		 * 	Constructor
		 */
		public TodoItem(String i,Date date){title = i; dueDate = date;}
		
		
		public String toString(){
			return title;
		}
		
		/*
		 *  Returns title field
		 * 
		 * (non-Javadoc)
		 * @see il.ac.huji.todolist.ITodoItem#getTitle()
		 */
		public String getTitle(){
			return title;
		}
		
		/*
		 * Returns dueDate field.
		 * 
		 * (non-Javadoc)
		 * @see il.ac.huji.todolist.ITodoItem#getDueDate()
		 */
		public Date getDueDate(){
			return dueDate;
		}
		
		
		@SuppressLint("SimpleDateFormat")
		/*
		 *  Returns a string representation of the dueDate field 
		 *  according to DATE_FORMAT
		 */
		public String getDueDateStr(){
			SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
			return df.format(dueDate);			
		}
		
}
