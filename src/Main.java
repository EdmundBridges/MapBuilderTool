import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application
{
	public static void main(String args[])
	{
		launch(args);
	}

	public void start(Stage primaryStage) throws Exception
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MapBuilderScene.fxml"));
		Parent root = loader.load();
		
		primaryStage.setTitle("Map Builder");
		primaryStage.setScene(new Scene(root, 600, 400));
		
		MapBuilderSceneController mbsc = (MapBuilderSceneController) loader.getController();
		
		mbsc.plantTree();
		primaryStage.show();
		primaryStage.setMinWidth(650);
		primaryStage.setMinHeight(450);
		
		mbsc.setPrimaryStage(primaryStage);
	}
}