import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;

public class MapBuilderSceneController
{	
	//MapBuilderScene Nodes
	public VBox selection;
	public HBox pallet;
	public TabPane mapTabs;
	public TreeView<MapFile> treeView;
	private TreeItem<MapFile> mapTreeRoot;//rootTreeItem
	public Group topRight, topLeft, bottomLeft, bottomRight;
	public Group selectionTile1, selectionTile2;
	public Group tile1, tile2, tile3, tile4, tile5, tile6, tile7, tile8;
	public MenuItem undo, redo, cut, copy, paste, delete;
	public MenuItem insertRows, insertColumns;
	
	//Used in Logic and Control MapBuilderScene
	private ArrayList<TabContents> tabList = new ArrayList<>();
	private MapGraphic activeMap;
	private History activeHistory;
	private File activeFile;
	private Stage primaryStage;
	private boolean dragging = false, selecting = false, painting  = true, isClosing = false;
	private int startingX = 0, startingY = 0, endingX = 0, endingY = 0;
	private ArrayList<Paint> clipboard1 = new ArrayList<>();
	private ArrayList<String> clipboard2 = new ArrayList<>();
	private int clipboardWidth = 0; private int clipboardHeight = 0; 

	//Handles Header Clicks to Alter Map Clicks
	public void handleSelectionMouseClicked(MouseEvent e)
	{
		painting = false; selecting = true;
		cut.setDisable(false); copy.setDisable(false); paste.setDisable(clipboard1.isEmpty()); delete.setDisable(false);
		pallet.setStyle("-fx-border-color: #666666");
		selection.setStyle("-fx-border-color: #1100ff");
	}
	
	public void handlePalletMouseClicked(MouseEvent e)
	{
		painting = true; selecting = false;
		cut.setDisable(true); copy.setDisable(true); paste.setDisable(true); delete.setDisable(true);
		selection.setStyle("-fx-border-color: #666666");
		pallet.setStyle("-fx-border-color: #1100ff");
		
		topRight.setVisible(false);
		topLeft.setVisible(false);
		bottomLeft.setVisible(false);
		bottomRight.setVisible(false);
	}
	
	//Handles Tile Clicks, paint Tiles Use This
	public void handleTileClicked(MouseEvent e)
	{
		if(e.getButton().equals(MouseButton.PRIMARY))
		{
			((Rectangle) selectionTile1.getChildren().get(0)).setFill(
					((Rectangle) ((Group) e.getSource()).getChildren().get(0)).getFill());
			((Label) selectionTile1.getChildren().get(1)).setText(
					((Label) ((Group) e.getSource()).getChildren().get(1)).getText());
		}
    	else if(e.getButton().equals(MouseButton.SECONDARY))
    	{
    		((Rectangle) selectionTile2.getChildren().get(0)).setFill(
					((Rectangle) ((Group) e.getSource()).getChildren().get(0)).getFill());
			((Label) selectionTile2.getChildren().get(1)).setText(
					((Label) ((Group) e.getSource()).getChildren().get(1)).getText());
    	}
	}

	//Handles Mouse Actions on the Map
	public void handleMapMousePressed(MouseEvent e)
	{
		if(dragging) return;
		dragging = true;
		
		int x = (int) e.getX()/MapGraphic.TILE_SIZE;
		int y = (int) e.getY()/MapGraphic.TILE_SIZE;

		boolean inBounds = (x >= 0 && x < activeMap.getMap().getColumns() && y >= 0 && y < activeMap.getMap().getRows());
		
		if(painting && inBounds)
		{
			Group g = ((Group) activeMap.getTerrainList().get(x*activeMap.getMap().getRows()+y));
			activeHistory.addToHold(x*activeMap.getMap().getRows()+y);
			
			if(e.getButton().equals(MouseButton.PRIMARY))
			{
				((Rectangle) g.getChildren().get(0)).setFill(
						((Rectangle) selectionTile1.getChildren().get(0)).getFill());
				((Label) g.getChildren().get(1)).setText(
						((Label) selectionTile1.getChildren().get(1)).getText());
			}
	    	else if(e.getButton().equals(MouseButton.SECONDARY))
	    	{
	    		((Rectangle) g.getChildren().get(0)).setFill(
						((Rectangle) selectionTile2.getChildren().get(0)).getFill());
				((Label) g.getChildren().get(1)).setText(
						((Label) selectionTile2.getChildren().get(1)).getText());
			}
	    	else
	    	{
	    		System.out.println("(" + x + "," + y + ") - > " + (x*activeMap.getMap().getRows()+y));
	    	}
		}
		else if(selecting && inBounds)
		{
			startingX = x; endingX = x;
			startingY = y; endingY = y;

			topRight.setTranslateX(startingX*MapGraphic.TILE_SIZE);
			topRight.setTranslateY(startingY*MapGraphic.TILE_SIZE);
			  topLeft.setTranslateX(startingX*MapGraphic.TILE_SIZE);
			  topLeft.setTranslateY(startingY*MapGraphic.TILE_SIZE);
			bottomLeft.setTranslateX(startingX*MapGraphic.TILE_SIZE);
			bottomLeft.setTranslateY(startingY*MapGraphic.TILE_SIZE);
			  bottomRight.setTranslateX(startingX*MapGraphic.TILE_SIZE);
			  bottomRight.setTranslateY(startingY*MapGraphic.TILE_SIZE);
			
			topRight.setVisible(true);
			topLeft.setVisible(true);
			bottomLeft.setVisible(true);
			bottomRight.setVisible(true);
		}
	}
	
	public void handleMapMouseDragged(MouseEvent e)
	{
		if(!dragging) return;

		int x = (int) e.getX()/MapGraphic.TILE_SIZE;
		int y = (int) e.getY()/MapGraphic.TILE_SIZE;
		
		boolean xInBounds = (x >= 0 && x < activeMap.getMap().getColumns());
		boolean yInBounds = (y >= 0 && y < activeMap.getMap().getRows());
		
		
		if(painting && xInBounds && yInBounds)
		{
			Group g = ((Group) activeMap.getTerrainList().get(x*activeMap.getMap().getRows()+y));
			activeHistory.addToHold(x*activeMap.getMap().getRows()+y);
			if(e.getButton().equals(MouseButton.PRIMARY))
			{
				((Rectangle) g.getChildren().get(0)).setFill(
						((Rectangle) selectionTile1.getChildren().get(0)).getFill());
				((Label) g.getChildren().get(1)).setText(
						((Label) selectionTile1.getChildren().get(1)).getText());
			}
	    	else if(e.getButton().equals(MouseButton.SECONDARY))
	    	{
	    		((Rectangle) g.getChildren().get(0)).setFill(
						((Rectangle) selectionTile2.getChildren().get(0)).getFill());
				((Label) g.getChildren().get(1)).setText(
						((Label) selectionTile2.getChildren().get(1)).getText());
			}
			
		}
		else if(selecting)
		{
			if(!xInBounds)
				if(x < 0)
					x = 0;
				else
					x = activeMap.getMap().getColumns()-1;
			if(!yInBounds)
				if(y<0)
					y=0;
				else
					y = activeMap.getMap().getRows()-1;
				
			if(x >= startingX && y <= startingY)//Dragging towards top-right
			{
				topRight.setTranslateX(x*MapGraphic.TILE_SIZE);
				topRight.setTranslateY(y*MapGraphic.TILE_SIZE);
				  topLeft.setTranslateX(startingX*MapGraphic.TILE_SIZE);
				  topLeft.setTranslateY(y*MapGraphic.TILE_SIZE);
				bottomLeft.setTranslateX(startingX*MapGraphic.TILE_SIZE);
				bottomLeft.setTranslateY(startingY*MapGraphic.TILE_SIZE);
				  bottomRight.setTranslateX(x*MapGraphic.TILE_SIZE);
				  bottomRight.setTranslateY(startingY*MapGraphic.TILE_SIZE);
			}
			else if(x <= startingX && y <= startingY)//Dragging toward top-left
			{
				topRight.setTranslateX(startingX*MapGraphic.TILE_SIZE);
				topRight.setTranslateY(y*MapGraphic.TILE_SIZE);
				  topLeft.setTranslateX(x*MapGraphic.TILE_SIZE);
				  topLeft.setTranslateY(y*MapGraphic.TILE_SIZE);
				bottomLeft.setTranslateX(x*MapGraphic.TILE_SIZE);
				bottomLeft.setTranslateY(startingY*MapGraphic.TILE_SIZE);
				  bottomRight.setTranslateX(startingX*MapGraphic.TILE_SIZE);
				  bottomRight.setTranslateY(startingY*MapGraphic.TILE_SIZE);
			}
			else if(x <= startingX && y >= startingY)//Dragging toward bottom-left
			{
				topRight.setTranslateX(startingX*MapGraphic.TILE_SIZE);
				topRight.setTranslateY(startingY*MapGraphic.TILE_SIZE);
				  topLeft.setTranslateX(x*MapGraphic.TILE_SIZE);
				  topLeft.setTranslateY(startingY*MapGraphic.TILE_SIZE);
				bottomLeft.setTranslateX(x*MapGraphic.TILE_SIZE);
				bottomLeft.setTranslateY(y*MapGraphic.TILE_SIZE);
				  bottomRight.setTranslateX(startingX*MapGraphic.TILE_SIZE);
				  bottomRight.setTranslateY(y*MapGraphic.TILE_SIZE);
			}
			else if(x >= startingX && y >= startingY)//Dragging toward bottom-right
			{
				topRight.setTranslateX(x*MapGraphic.TILE_SIZE);
				topRight.setTranslateY(startingY*MapGraphic.TILE_SIZE);
				  topLeft.setTranslateX(startingX*MapGraphic.TILE_SIZE);
				  topLeft.setTranslateY(startingY*MapGraphic.TILE_SIZE);
				bottomLeft.setTranslateX(startingX*MapGraphic.TILE_SIZE);
				bottomLeft.setTranslateY(y*MapGraphic.TILE_SIZE);
				  bottomRight.setTranslateX(x*MapGraphic.TILE_SIZE);
				  bottomRight.setTranslateY(y*MapGraphic.TILE_SIZE);
			}
			
			endingX = x;
			endingY = y;
		}
	}
	
	public void handleMapMouseReleased(MouseEvent e)
	{
		dragging = false;
		
		if(painting)
		{
			activeHistory.addHoldToHistory();
		}
	}
	
	//Handling Menu Items
	//Handling File Menu
	public void handleNewFile() throws Exception
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateNewMap.fxml"));
		Parent root = loader.load();
		CreateNewMapController cnmc = (CreateNewMapController) loader.getController();
		
		Stage newMapStage = new Stage();
		newMapStage.initModality(Modality.APPLICATION_MODAL);
		newMapStage.setTitle("New Map");
		newMapStage.setScene(new Scene(root));
		newMapStage.setResizable(false);
		
		cnmc.setMyStage(newMapStage);
		newMapStage.showAndWait();
		
		if(cnmc.isCreated())
		{
			createNewTab(new MapGraphic(cnmc.getTitle(), cnmc.getColumns(), cnmc.getRows()), null);
		}
		
		
	}
	
	public void handleOpenFile()
	{
		FileChooser fileChooser = new FileChooser();
		//Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MAP files (*.map)", "*.map");
        fileChooser.getExtensionFilters().add(extFilter);
        //DefaultDirectory
	    File records = new File("maps");
	    fileChooser.setInitialDirectory(records);
	    
	  //Show save file dialog
        activeFile = fileChooser.showOpenDialog(primaryStage);
        Tab t = null;
        
        boolean isOpen = false;
        for(TabContents tC: tabList)
        	if(activeFile.equals(tC.getFile()))
        	{isOpen = true; t = tC.getTab();}
        
        if (activeFile != null && !isOpen)
        {
            try
            {
            	FileInputStream fileIn = new FileInputStream(activeFile);
	        	ObjectInputStream objectIn = new ObjectInputStream(fileIn);
	        	BattleMap bMap = (BattleMap) objectIn.readObject();
	        	
	        	createNewTab(new MapGraphic(bMap), activeFile);
	    		
	        	objectIn.close();
            }
            catch (IOException ex)
            {
            	System.out.println(ex.getMessage());
            }
	        catch (ClassNotFoundException cExcept)
	        {
	        	System.out.println("Class Not Found Exception");
	        }
        }
        else if (activeFile != null && t != null)//select tab
        {
        	mapTabs.getSelectionModel().select(t);
        }
	}
	
	public void handleSaveFile()
	{
		if(activeFile != null)
			try
		        {
					activeMap.saveMap();
		        	FileOutputStream fileOut = new FileOutputStream(activeFile);
		        	ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
		        	objectOut.writeObject(activeMap.getMap());
		        	objectOut.close();
		        }
		        catch (IOException ex)
		        {
		        	
		        }
		else
			handleSaveFileAs();
	}
	
	public void handleSaveFileAs()
	{
		activeMap.saveMap();
		
		FileChooser fileChooser = new FileChooser();
		//Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("MAP files (*.map)", "*.map");
        fileChooser.getExtensionFilters().add(extFilter);
        //DefaultDirectory
	    File records = new File("maps");
	    fileChooser.setInitialDirectory(records);
	    //SetDefaultName
	    fileChooser.setInitialFileName(activeMap.getMap().getTitle());
	    
	    //Show save file dialog
	    activeFile = fileChooser.showSaveDialog(primaryStage);
        
        if (activeFile != null)
        {
            try
            {
            	FileOutputStream fileOut = new FileOutputStream(activeFile);
            	ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            	objectOut.writeObject(activeMap.getMap());
            	addRoot(activeFile);
            	objectOut.close();
            }
            catch (IOException ex)
            {
            	
            }
        }  
	}
	
	//Handling Edit Menu
	public void handleUndo()
	{
		activeHistory.undoHistory();
	}
	
	public void handleRedo()
	{
		activeHistory.redoHistory();
	}
	
	public void handleCut()
	{
		handleCopy();
		handleDelete();
	}

	public void handleCopy()
	{
		clipboard1.clear();
		clipboard2.clear();
		paste.setDisable(false);
		int xMin, yMin, xMax, yMax;
		
		if(startingX < endingX) {xMin = startingX; xMax = endingX;}
		else {xMax = startingX; xMin = endingX;}
		if(startingY < endingY) {yMin = startingY; yMax = endingY;}
		else {yMax = startingY; yMin = endingY;}
		xMin *= MapGraphic.TILE_SIZE;
		xMax *= MapGraphic.TILE_SIZE;
		yMin *= MapGraphic.TILE_SIZE;
		yMax *= MapGraphic.TILE_SIZE;
		
		clipboardWidth = xMax - xMin;
		clipboardHeight = yMax - yMin;

		Group g;
		Rectangle r;
		Label l;
		for(int i = 0; i < activeMap.getTerrainList().size(); i++)
		{
			g = (Group) activeMap.getTerrainList().get(i);
			r = (Rectangle) g.getChildren().get(0);
			l = (Label) g.getChildren().get(1);
			
			if(g.getLayoutX() >= xMin && g.getLayoutX() <= xMax && g.getLayoutY() >= yMin && g.getLayoutY() <= yMax)
			{
				clipboard1.add(r.getFill());
				clipboard2.add(l.getText());
			}	
		}
	}
	
	public void handlePaste()//need to fix pasting when selection isn't full
	{
		
		int xMin, yMin, xMax, yMax;//used to determine where to paste
		//calculating those values
		if(endingX == startingX && endingY == startingY)
		{
		xMin = endingX*MapGraphic.TILE_SIZE;
		xMax = (endingX)*MapGraphic.TILE_SIZE+clipboardWidth;
		yMin = endingY*MapGraphic.TILE_SIZE;
		yMax = (endingY)*MapGraphic.TILE_SIZE+clipboardHeight;
		}
		else
		{
			if(startingX < endingX) {xMin = startingX; xMax = endingX;}
			else {xMax = startingX; xMin = endingX;}
			if(startingY < endingY) {yMin = startingY; yMax = endingY;}
			else {yMax = startingY; yMin = endingY;}
			
			xMin *= MapGraphic.TILE_SIZE;
			yMin *= MapGraphic.TILE_SIZE;
			
			xMax *= MapGraphic.TILE_SIZE;
			if(xMax > (startingX)*MapGraphic.TILE_SIZE+clipboardWidth)
				xMax = (startingX)*MapGraphic.TILE_SIZE+clipboardWidth;
			
			yMax *= MapGraphic.TILE_SIZE;
			if(yMax > (startingY)*MapGraphic.TILE_SIZE+clipboardHeight)
				yMax = (startingY)*MapGraphic.TILE_SIZE+clipboardHeight;
		}//end calculations
		
		Group g;
		Rectangle r;
		Label l;
		int j = 0;
		for(int i = 0; i < activeMap.getTerrainList().size(); i++)
		{
			g = (Group) activeMap.getTerrainList().get(i);
			r = (Rectangle) g.getChildren().get(0);
			l = (Label) g.getChildren().get(1);
			
			if(j < clipboard1.size() && g.getLayoutX() >= xMin && g.getLayoutX() <= xMax && g.getLayoutY() >= yMin && g.getLayoutY() <= yMax)
				{
					activeHistory.addToHold(i);//storing a single event into active memory
					r.setFill(clipboard1.get(j));//pasting color
					l.setText(clipboard2.get(j));//pasting text
					j++;
				}
		}
		activeHistory.addHoldToHistory();//archiving active memory
	}
	
	public void handleDelete()
	{
		int xMin, yMin, xMax, yMax;
		
		if(startingX < endingX) {xMin = startingX; xMax = endingX;}
		else {xMax = startingX; xMin = endingX;}
		if(startingY < endingY) {yMin = startingY; yMax = endingY;}
		else {yMax = startingY; yMin = endingY;}
		
		xMin *= MapGraphic.TILE_SIZE;
		xMax *= MapGraphic.TILE_SIZE;
		yMin *= MapGraphic.TILE_SIZE;
		yMax *= MapGraphic.TILE_SIZE;

		Group g;
		Rectangle r;
		Label l;
		for(int i = 0; i < activeMap.getTerrainList().size(); i++)
		{
			g = (Group) activeMap.getTerrainList().get(i);
			r = (Rectangle) g.getChildren().get(0);
			l = (Label) g.getChildren().get(1);
			if(g.getLayoutX() >= xMin && g.getLayoutX() <= xMax && g.getLayoutY() >= yMin && g.getLayoutY() <= yMax)
				{
				activeHistory.addToHold(i);//storing a single event into active memory
					r.setFill(Color.WHITE);//deleting color
					l.setText("");//deleting text
				}
		}
		activeHistory.addHoldToHistory();//archiving active memory
	}	

	//Handling Insert Menu
	public void handleInsertRows()
	{
		Pane p = (Pane) activeMap.getTerrainList().get(0).getParent();
		p.getChildren().removeAll(activeMap.getTerrainList());
		activeMap.addRowAt(0);
		p.getChildren().addAll(activeMap.getTerrainList());	
		moveSelectionCursor(p);
	}
	
	public void handleInsertColumns()
	{
		Pane p = (Pane) activeMap.getTerrainList().get(0).getParent();
		p.getChildren().removeAll();
		activeMap.addColumnAt(0);
		p.getChildren().addAll(activeMap.getTerrainList());	
		moveSelectionCursor(p);
	}
	
    //creating a tab and its handle methods
    private void createNewTab(MapGraphic m, File f)
    {
    	//making history and adding to respective lists
    	History h = new History(m);
    	
    	//creating the context menu
    	ContextMenu c = new ContextMenu();
	    	MenuItem close = new MenuItem("Close");
	    	MenuItem closeOthers = new MenuItem("Close Others");
	    	MenuItem closeRight = new MenuItem("Close Tabs to the Right");
	    	MenuItem closeAll = new MenuItem("Close All");
	    c.getItems().addAll(close, closeOthers, closeRight, closeAll);
    	
    	//setting the format
    	Tab t = new Tab(m.getMap().getTitle());
    	t.setContextMenu(c);
	    	ScrollPane s = new ScrollPane();
			s.setStyle("-fx-background: #000000; -fx-border-color: #000000;");
			s.setPadding(new Insets(5,5,5,5));
				Pane p = new Pane();
				p.setStyle("-fx-background-color: #000000;");
				p.setPadding(new Insets(5,5,5,5));
				p.getChildren().addAll(m.getTerrainList());
			s.setContent(p);
		t.setContent(s);
		mapTabs.getTabs().add(t);
    	mapTabs.getSelectionModel().select(t);

		//setting the action commands
    	t.setOnSelectionChanged(e -> handleTabSelect(e));
    	t.setOnCloseRequest(e -> handleTabCloseRequest(e));
    	
		p.setOnMousePressed(e -> handleMapMousePressed(e));
		p.setOnMouseDragged(e -> handleMapMouseDragged(e));
		p.setOnMouseReleased(e -> handleMapMouseReleased(e));
		
		close.setOnAction(e -> handleTabClose(e));
		closeOthers.setOnAction(e -> handleTabCloseOthers(e));
		closeRight.setOnAction(e -> handleTabCloseRight(e));
		closeAll.setOnAction(e -> handleTabCloseAll(e));
		
		
    	TabContents tC = new TabContents(t, m, h, f);
    	
    	boolean wasEmpty = tabList.isEmpty();
    	tabList.add(tC);
    	
    	if(wasEmpty)
    	{
    		activeMap = m;
    		activeHistory = h;
	    	
	    	Pane pane = (Pane) ((ScrollPane) t.getContent()).getContent();
	    	moveSelectionCursor(pane);
	    	mapTabs.getSelectionModel().select(t);
	    	insertRows.setDisable(false);
	    	insertColumns.setDisable(false);
    	}
    }
    
    private void handleTabSelect(Event e)
    {
    	if(isClosing) 
    	{
    		isClosing = false;
    		return;
    	}
	    	Tab t = (Tab) e.getSource();
	    	int i = mapTabs.getTabs().indexOf(t);
	    	activeMap = tabList.get(i).getMap();
	    	activeHistory = tabList.get(i).getHistory();
	    	activeFile = tabList.get(i).getFile();
	    	
	    	Pane p = (Pane) ((ScrollPane) t.getContent()).getContent();
	    	moveSelectionCursor(p);
	    	topRight.setVisible(false);
	    	topLeft.setVisible(false);
	    	bottomLeft.setVisible(false);
	    	bottomRight.setVisible(false);
    	
    }
    
    private void handleTabCloseRequest(Event e)
    {
    	e.consume();//will be closed latter.
    	isClosing = true;//so that handleTabSelect doesn't trigger
    	
    	Tab t = (Tab) e.getSource();
    	
    	TabContents tabContents = null;
    	for(TabContents tC: tabList)
    		if(tC.getTab().equals(t))
    			tabContents = tC;
    	
    	tabList.remove(tabContents);//removes the tab's corresponding file from memory
    	
    	mapTabs.getTabs().remove(t);//tab closed
    	
    	if(tabList.isEmpty())//no tabs are displayed anymore
    	{
    		insertRows.setDisable(true);
    		insertColumns.setDisable(true);
    	}
    }

    private void handleTabClose(Event e)
    {
    	
    }

    private void handleTabCloseOthers(Event e)
    {
    	
    }

    private void handleTabCloseRight(Event e)
    {
    	
    }
    
    private void handleTabCloseAll(Event e)
    {
    	
    }


   
    //creating the tree view
    public void plantTree() throws IOException//createTree
    {
    	mapTreeRoot = new TreeItem<>(new MapFile(Paths.get("maps")));//starts by looking in maps
    	mapTreeRoot.setExpanded(true);
    	
    	growTree(mapTreeRoot);
    	
    	treeView.setRoot(mapTreeRoot);
    	
    	treeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    	
    	treeView.setEditable(true);
    	    	
    	treeView.setOnMouseClicked(e -> handleTreeSelection(e));
    	
    }
    
    private void growTree(TreeItem<MapFile> rootItem) throws IOException
    {
        try 
        {	//opens the file and creates a list of paths inside
        	DirectoryStream<Path> directoryStream = Files.newDirectoryStream(rootItem.getValue().getPath());
        	TreeItem<MapFile> newItem;
        	
        	for(Path path : directoryStream)
        	{
        		newItem = new TreeItem<>(new MapFile(path));
        		newItem.setExpanded(true);
        		rootItem.getChildren().add(newItem);
        		
        		if(Files.isDirectory(path))//if the path is also a file, goes again
        		{
        			growTree(newItem);
        		}
        		else//is a map file
        		{
        			
        		}
        	}
        }
    	catch( Exception ex)
    	{
            ex.printStackTrace();
        }	
    }
    
    private void handleTreeSelection(MouseEvent e)
    {
    	if(e.getClickCount() != 2)
    		return;
		TreeItem<MapFile> selectedItem = treeView.getSelectionModel().getSelectedItem();
		if(!selectedItem.getChildren().isEmpty()) return; //it is a folder, it won't open
		File f = selectedItem.getValue().getPath().toFile();
		
		Tab t = null;
        
		boolean isOpen = false;
        for(TabContents tC: tabList)
        	if(f.equals(tC.getFile()))
        	{isOpen = true; t = tC.getTab();}
        
		if(!isOpen)//file is not already open
	    	try
	        {
	        	FileInputStream fileIn = new FileInputStream(f);
	        	ObjectInputStream objectIn = new ObjectInputStream(fileIn);
	        	BattleMap bMap = (BattleMap) objectIn.readObject();
	        	
	        	createNewTab(new MapGraphic(bMap), f);
	    		
	        	objectIn.close();
	        }
	        catch (IOException ex)
	        {
	        	System.out.println(ex.getMessage());
	        }
	        catch (ClassNotFoundException cExcept)
	        {
	        	System.out.println("Class Not Found Exception");
	        }
		else//file is open, lets selected it form them
		{
			mapTabs.getSelectionModel().select(t);
		}
    }
    
    private void addRoot(File f)//wants to efficiently add root to tree view based on path name
    {
    	
    }

    private void moveSelectionCursor(Pane pane)
    {

    	((Pane) topRight.getParent()).getChildren().remove(topRight);
    	((Pane) topLeft.getParent()).getChildren().remove(topLeft);
    	((Pane) bottomLeft.getParent()).getChildren().remove(bottomLeft);
    	((Pane) bottomRight.getParent()).getChildren().remove(bottomRight);
    	pane.getChildren().addAll(topRight, topLeft, bottomLeft, bottomRight);
    }
    
    public void setPrimaryStage(Stage primaryStage)
	{
		this.primaryStage = primaryStage;
	}  
    //new classes that help structure and connect
    private class TabContents
    {
    	private Tab tab;
    	private MapGraphic map;
    	private History history;
    	private File file;
    	
    	public TabContents(Tab t, MapGraphic m, History h, File f)
    	{
    		tab = t;
    		map = m;
    		history = h;
    		file = f;
    	}

    	public Tab getTab() {
    		return tab;
    	}
    	
		public MapGraphic getMap() {
			return map;
		}

		public History getHistory() {
			return history;
		}

		public File getFile() {
			return file;
		}
    	
    	
    }

    private class MapFile
    {
    	private Path path;
        private String text;

        public MapFile( Path path)
        {
            this.path = path;

            // display text: the last path part
            if( path.getNameCount() == 0)//root
                this.text = path.toString();
            else //folder structure
                this.text = path.getName(path.getNameCount() - 1).toString();
        }
        
        public Path getPath() {return path;}

        public String toString() {return text;}//As seen in TreeView
    }

}
