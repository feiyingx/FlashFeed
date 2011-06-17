package com.snapperfiche.mobile.custom;

import java.util.LinkedHashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import com.snapperfiche.mobile.R;

public class SeparatedListAdapter extends BaseAdapter{
	public final Map<String, Adapter> sections = new LinkedHashMap<String, Adapter>();
	public final ArrayAdapter<String> headers;
	public final static int TYPE_SECTION_HEADER = 0;
	
	public SeparatedListAdapter(Context context){
		super();
		headers = new ArrayAdapter<String>(context, R.layout.list_header);
	}
	
	public void addSection(String section, Adapter adapter){
		this.headers.add(section);
		this.sections.put(section, adapter);
	}

	@Override
	public int getCount() {
		int count = 0;
		for(Adapter adapter : this.sections.values()){
			count += adapter.getCount()+1;
		}
			
		return count;
	}
	
	@Override
	public int getViewTypeCount(){
		int total = 1;
		for(Adapter adapter : this.sections.values()){
			total += adapter.getViewTypeCount();
		}
		return total;
	}
	
	@Override
	public int getItemViewType(int position){
		int type = 1;
		for(Object section : this.sections.keySet()){
			Adapter adapter = sections.get(section);
			int size = adapter.getCount()+1;
			
			if(position == 0) return TYPE_SECTION_HEADER;
			if(position < size) return type + adapter.getItemViewType(position-1);
			
			position -= size;
			type += adapter.getViewTypeCount();
		}
		return -1;
	}
	
	//TODO: need this?
	@Override
	public boolean areAllItemsEnabled(){
		return false;
	}
	
	@Override
	public boolean isEnabled(int position){
		return (getItemViewType(position) != TYPE_SECTION_HEADER);
	}
	
	@Override
	public boolean isEmpty(){
		return getCount() == 0;
	}

	@Override
	public Object getItem(int position) {
		for(Object section : this.sections.keySet()){
			Adapter adapter = sections.get(section);
			int size = adapter.getCount()+1;
			
			if(position == 0) return section;
			if(position < size) return adapter.getItem(position-1);
			
			position -= size;
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		int sectionnum = 0;
		for(Object section : this.sections.keySet()){
			Adapter adapter = sections.get(section);
			int size = adapter.getCount()+1;
			
			if(position == 0) return headers.getView(sectionnum, convertView, parent);
			if(position < size) return adapter.getView(position-1, convertView, parent);
			
			position -= size;
			sectionnum++;
		}
		return null;
	}
}
