package il.ac.huji.todolist;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TodoItem {

		private String title;
		private Date dueDate;
		final private String DATE_FORMAT = "dd/MM/yyyy"; 
		
		
		public TodoItem(String i,Date date){title = i; dueDate = date;}
		
		public String toString(){
			return title;
		}
		
		public String getTitle(){
			return title;
		}
		
		public Date getDueDate(){
			return dueDate;
		}
		
		@SuppressLint("SimpleDateFormat")
		public String getDueDateStr(){
			SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
			return df.format(dueDate);			
		}
		
}
