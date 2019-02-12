import edu.cmu.ri.createlab.terk.robot.finch.Finch;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * A Finch that steps back from whatever light it sees, making sure to keep it in its FOV.
 * 
 * Also, it has a GUI. Dull, but not as hideous as Swing.
 */
public class Yes extends Application{
	
	public static void main(String[] args) {
		launch(args);
	}

	Finch finch;
	Text leftSensorVal = new Text();
	Text rightSensorVal = new Text();
	boolean runrunrun = true;
	@Override
	public void init() {
		finch = new Finch();
		leftSensorVal.setFont(new Font(20));
		rightSensorVal.setFont(new Font(20));
		new Thread(() -> {
			while(runrunrun) {
				// Upon seeing a light source, back up with a speed proportional to how bright the light is
				finch.setWheelVelocities(-(finch.getLeftLightSensor() * 2), -(finch.getRightLightSensor() * 2));
				leftSensorVal.setText("Left sensor value: "+finch.getLeftLightSensor());
				rightSensorVal.setText("Right sensor value: "+finch.getRightLightSensor());
				// Hol up so that we don't destroy the computer's CPU
				finch.sleep(50);
			}
			finch.quit();
			System.exit(0);
		}).start();
	}
	@Override
	public void start(Stage stage) {
		Button quit = new Button("Quit");
		quit.setCancelButton(true);
		quit.setOnAction(e -> {
			stage.hide();
			runrunrun = false;
		});
		
		GridPane root = new GridPane();
		root.add(leftSensorVal, 0, 0);
		root.add(rightSensorVal, 0, 1);
		root.add(quit, 0, 2);
		
		// Tons of boilerplate to center all the things and
		// make them move around when you resize the window
		GridPane.setHgrow(leftSensorVal, Priority.ALWAYS);
		GridPane.setVgrow(leftSensorVal, Priority.ALWAYS);
		GridPane.setHalignment(leftSensorVal, HPos.CENTER);
		GridPane.setHgrow(rightSensorVal, Priority.ALWAYS);
		GridPane.setVgrow(rightSensorVal, Priority.ALWAYS);
		GridPane.setHalignment(rightSensorVal, HPos.CENTER);
		GridPane.setHgrow(quit, Priority.ALWAYS);
		GridPane.setVgrow(quit, Priority.ALWAYS);
		GridPane.setHalignment(quit, HPos.CENTER);
		
		stage.setTitle("Finch that avoids light");
		stage.setScene(new Scene(root, 300, 200));
		stage.show();
	}
}
