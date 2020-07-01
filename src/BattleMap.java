import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BattleMap implements Serializable
{
	private static final long serialVersionUID = 1820084282671315024L;
	private String title;
	private int columns;
	private int rows;
	private ArrayList<TerrainTile> tileList;
	
	public BattleMap()
	{
		title = "";
		columns = 0;
		rows = 0;
		tileList = new ArrayList<>();
	}
	
	public BattleMap(String title, int columns, int rows)
	{
		this.title = title;
		this.columns = columns;
		this.rows = rows;
		
		tileList = new ArrayList<>();
		for(int i = 0; i < columns; i++)
			for(int j = 0; j < rows; j++)
				tileList.add(new TerrainTile(i,j,TerrainTile.Type.Blank));
	}
	
	public BattleMap(String title, int columns, int rows, ArrayList<TerrainTile> tileList)
	{
		this.title = title;
		this.columns = columns;
		this.rows = rows;
		
		this.tileList = new ArrayList<>();
		for(int i = 0; i < columns; i++)
			for(int j = 0; j < rows; j++)
				this.tileList.add(new TerrainTile(i,j,tileList.get(i*rows+j).getType()));
	}
	
	public void addRowAt(int row)
	{
		for(TerrainTile t: tileList)
			if(t.getRow() >= row)
				t.setRow(t.getRow() + 1);
		rows++;
		
		for(int i = 0; i < columns; i++)
			tileList.add(new TerrainTile(i,row,TerrainTile.Type.Blank));
		
		Collections.sort(tileList, new CompareTiles());
	}
	
	public void addColumnAt(int column)
	{
		for(TerrainTile t: tileList)
			if(t.getColumn() >= column)
				t.setColumn(t.getColumn() + 1);
		
		columns++;
		
		for(int j = 0; j < rows; j++)
			tileList.add(new TerrainTile(column,j,TerrainTile.Type.Blank));
		
		Collections.sort(tileList, new CompareTiles());
	}

	public String getTitle() {
		return title;
	}

	public int getColumns() {
		return columns;
	}

	public int getRows() {
		return rows;
	}

	public ArrayList<TerrainTile> getTileList() {
		return tileList;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public void setTileList(ArrayList<TerrainTile> tileList) {
		this.tileList = tileList;
	}
	
	private class CompareTiles implements Comparator<TerrainTile>
	{
		@Override
		public int compare(TerrainTile o1, TerrainTile o2) {
			int first = o1.getColumn()*rows + o1.getRow();
			int second = o2.getColumn()*rows + o2.getRow();
			if( first < second)
				return -1;
			else
				return 1;
		}	
	}
	
}
