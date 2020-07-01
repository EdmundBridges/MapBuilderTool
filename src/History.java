import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class History
{
	private MapGraphic mapGraphic;//History wishes to makes changes to this object
	private ArrayList<ArrayList<MapHistory>> history = new ArrayList<>();//Archives memories of past events
	private ArrayList<MapHistory> holdHistory = new ArrayList<>();//Active memory of events
	private int index = 0;//Used to track the objects in history
	
	public History(MapGraphic mapGraphic)
	{
		this.mapGraphic = mapGraphic;
	}
	
	public boolean addToHold(int index)//from just the index we can gleam information.
	{
		
		Paint paint = ((Rectangle) mapGraphic.getTerrainList().get(index).getChildren().get(0)).getFill();//like color
		String text = ((Label) mapGraphic.getTerrainList().get(index).getChildren().get(1)).getText();//and identifier
		
		MapHistory m = new MapHistory(paint,text,index);
		
		for(MapHistory map: holdHistory)//checks to make sure the index is unique
			if(map.getIndex() == index)
				return false;
		
		holdHistory.add(m);//adds the new and unique history to active memory
		
		return true;
	}
	
	public void addHoldToHistory()
	{
		while(index != history.size())//if new history is add, it must be the latest
			history.remove(index);
		
		
		if(history.size() > 50)//limits size of history to 50 events
		{
			history.remove(0);
			index--;
		}
		
		history.add(holdHistory);//stores active memory to archive
		
		holdHistory = new ArrayList<>();//creates new active memory (rather than just clearing it) due to object pointers
		
		index++;//a new memory was added, so increment index
	}
	
	public void undoHistory()
	{
		if(index > 0)//if index is zero, there is no memory to undo.
		{
			index--;//index moves back one in memory
			
			ArrayList<MapHistory> previous = history.remove(index);//memory is being taken from archive
			
			Group g;
			for(MapHistory i: previous)
			{
				g = mapGraphic.getTerrainList().get(i.getIndex());
				addToHold(i.getIndex());//stores a single event active memory of what is about to be undone
				((Rectangle) g.getChildren().get(0)).setFill(i.getPaint());	//the undoing
				((Label) g.getChildren().get(1)).setText(i.getText());		//the undoing
			}
			
			//we can't use addHoldToHistory method because it would clear memory that might want to be redone.
			history.add(index, holdHistory);//stores active memory to archive
			holdHistory = new ArrayList<>();//creates new active memory
		}
	}
	
	public void redoHistory()
	{
		if(index < history.size())//if index is equal to history size, there is no memory to redone
		{
			ArrayList<MapHistory> next = history.remove(index);//memory is being taken from archive
			
			Group g;
			for(MapHistory i: next)
			{
				g = mapGraphic.getTerrainList().get(i.getIndex());
				addToHold(i.getIndex());//stores a single event active memory of what is about to be redone
				((Rectangle) g.getChildren().get(0)).setFill(i.getPaint());	//the redoing
				((Label) g.getChildren().get(1)).setText(i.getText());		//the redoing
			}
			
			//we can't use addHoldToHistory method because it would clear memory that might want to be redone.
			history.add(index, holdHistory);//stores active memory to archive
			holdHistory = new ArrayList<>();//creates new active memory
			
			index++;
		}
	}
	
	private class MapHistory
	{
		private Paint paint;
		private String text;
		private int index;
		
		public MapHistory(Paint paint, String text, int index)
		{
			this.paint = paint;
			this.text = text;
			this.index = index;
		}

		public Paint getPaint() {
			return paint;
		}

		public String getText() {
			return text;
		}

		public int getIndex() {
			return index;
		}
	
		public String toString()
		{
			return ("Index " + index + " is: " + paint + " " + text + " ");
		}
		
	}
	
	
	
	
	
}
