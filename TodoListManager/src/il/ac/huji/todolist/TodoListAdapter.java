package il.ac.huji.todolist;

import java.util.List;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TodoListAdapter extends ArrayAdapter<TodoItem> {
	public TodoListAdapter(TodoListManagerActivity activity, List<TodoItem> courses) {
		super(activity, android.R.layout.simple_list_item_1, courses);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TodoItem item = getItem(position);
		int txtColor = (position%2==0) ? Color.RED : Color.BLUE;
		
		LayoutInflater inflater = LayoutInflater.from(getContext());
		View view = inflater.inflate(R.layout.item_layout, null);
		TextView txtName = (TextView)view.findViewById(R.id.name);
		txtName.setText(item.toString());
		txtName.setTextColor(txtColor);
		return view;
	}
}