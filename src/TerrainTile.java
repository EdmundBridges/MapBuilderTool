import java.io.Serializable;

public class TerrainTile implements Serializable
{
	private static final long serialVersionUID = 7340972198472865733L;
	public enum Type {Blank, Plains, Forest, Mountain, Peak, Water, Fort, Wall}
	private int column;
	private int row;
	private Type type;
	
	public TerrainTile()
	{
		column = 0;
		row = 0;
		type = Type.Blank;
	}
	
	public TerrainTile(int column, int row, Type type)
	{
		this.column = column;
		this.row = row;
		this.type = type;
	}
	
	public String toString()
	{
		return "(" + column +"," + row + ") -> " + type;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}

	public Type getType() {
		return type;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void setRow(int row) {
		this.row = row;
	}

	public void setType(Type type) {
		this.type = type;
	}
}
