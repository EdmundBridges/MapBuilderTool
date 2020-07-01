import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;

public class MapGraphic
{
	public static final int TILE_SIZE = 30;
	private BattleMap map;
	private ObservableList<Group> terrainList;
	
	public MapGraphic()
	{
		map = new BattleMap("", 20,20);
		fillTerrainList(map);
	}
	
	public MapGraphic(String title, int columns, int rows)
	{
		map = new BattleMap(title, columns, rows);
		fillTerrainList(map);
	}
	
	public MapGraphic(BattleMap map)
	{
		this.map = map;
		fillTerrainList(map);
	}
	//used to convert BattleMap Data to MapGraphic Data
	private void fillTerrainList(BattleMap map)
	{
		terrainList = FXCollections.observableArrayList();
		
		Group g;
		Rectangle r;
		TerrainTile t;
		Label l;
		
		ConversionTables.PaintToType ptt;
		for(int i = 0; i < map.getTileList().size(); i++)
		{
			t = map.getTileList().get(i);
			
			g = new Group();
			g.setLayoutX(TILE_SIZE*t.getColumn());
			g.setLayoutY(TILE_SIZE*t.getRow());
			
			r = new Rectangle();
			r.setWidth(TILE_SIZE);
			r.setHeight(TILE_SIZE);
			r.setStroke(Color.BLACK); 
			
			l = new Label();
			l.setLayoutX(TILE_SIZE/4);
			l.setLayoutY(TILE_SIZE/4);
			
			ptt = ConversionTables.convertTypeToPaint(t.getType());
			r.setFill(ptt.getPaint());
			l.setText(ptt.getIdentifier());
			
			g.getChildren().addAll(r,l);
			
			terrainList.add(g);
		}
		
		
	}
	//used to convert the Map Graphic Data to Battle Map Data
	public void saveMap()
	{
		Group g;
		Rectangle r;
		Paint p;
		ConversionTables.PaintToType ptt;
		ArrayList<TerrainTile> t = getMap().getTileList();
		for(int i = 0; i < getTerrainList().size(); i++)
		{
			g = (Group) terrainList.get(i);
			r = ((Rectangle) g.getChildren().get(0));
			p = r.getFill();
			ptt = ConversionTables.convertPaintToType(p);
			t.get(i).setType(ptt.getType());
		}
	}
	
	public void addRowAt(int row)
	{
		saveMap();
		map.addRowAt(row);
		fillTerrainList(map);
		
	}
	
	public void addColumnAt(int column)
	{
		saveMap();
		map.addColumnAt(column);
		fillTerrainList(map);
	}
	
	public BattleMap getMap() {
		return map;
	}

	public ObservableList<Group> getTerrainList() {
		return terrainList;
	}

	public void setMap(BattleMap map) {
		this.map = map;
	}

	public void setTerrainList(ObservableList<Group> terrainList) {
		this.terrainList = terrainList;
	}
}
