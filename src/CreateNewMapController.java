import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateNewMapController
    {
    	//CreateNewMapNodes
    	public TextField titleField;
    	public TextField rowField;
    	public TextField columnField;
    	
    	//Used in Logic and Control for CreateNewMap
    	private String title;
    	private int rows;
    	private int columns;
    	private boolean created = false;
    	private Stage myStage;
    	
    	public void handleCreateMap()
    	{
    		try
    		{
    			title = titleField.getText();
    			if(title.length() < 1)
    				throw new Exception();
    			rows = Integer.parseInt(rowField.getText());
    			columns = Integer.parseInt(columnField.getText());
    			created = true;
    			myStage.close();
    		}
    		catch(Exception e)
    		{
    			rowField.clear();
    			columnField.clear();
    		}
    	}
    	
    	public void handleCancelMap()
    	{
    		created = false;
    		myStage.close();
    	}

		public String getTitle() {
			return title;
		}

		public int getRows() {
			return rows;
		}

		public int getColumns() {
			return columns;
		}

		public boolean isCreated() {
			return created;
		}
	
		public void setMyStage(Stage myStage) {
			this.myStage = myStage;
		}
    	
    }