import edu.cmu.ri.createlab.terk.robot.finch.Finch;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * A Finch that steps back from whatever light it sees, making sure to keep it in its FOV.
 * 
 * Also, it has a GUI. Ugly, but not as hideous as Swing.
 */
public class Yes extends Application{
	
	public static void main(String[] args) {
		launch(args);
	}

	Finch finch;
	Text leftSensorVal = new Text();
	Text rightSensorVal = new Text();
	boolean runrunrun = true;
	public Yes() {
		super();
		finch = new Finch();
		leftSensorVal.setFont(new Font(20));
		leftSensorVal.relocate(0, 0);
		rightSensorVal.setFont(new Font(20));
		rightSensorVal.relocate(0, 60);
		new Thread(() -> {
			while(runrunrun) {
				finch.setWheelVelocities(-(finch.getLeftLightSensor() * 2), -(finch.getRightLightSensor() * 2));
				leftSensorVal.setText("Left sensor value: "+finch.getLeftLightSensor());
				rightSensorVal.setText("Right sensor value: "+finch.getRightLightSensor());
				finch.sleep(50);
			}
		}).start();
	}
	@Override
	public void start(Stage stage) {
		stage.setTitle("Finch that avoids light");
		Button quit = new Button("Quit");
		quit.setCancelButton(true);
		quit.relocate(280, 200);
		quit.setOnAction(e -> {
			runrunrun = false; // Because Thread.stop() is a bad idea
			finch.quit();
			System.exit(0);
		});
		Pane root = new Pane();
		root.setLayoutX(0);
		root.setLayoutY(0);
		root.getChildren().addAll(leftSensorVal, rightSensorVal, quit);
		stage.setScene(new Scene(root, 320, 240));
		stage.show();
	}
}
