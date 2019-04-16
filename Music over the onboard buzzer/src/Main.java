import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import io.dorukayhan.composer.BadNoteException;
import io.dorukayhan.composer.Composer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import edu.cmu.ri.createlab.terk.robot.finch.Finch;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}
	
	Finch finch;
	Composer composer;
	Thread nowPlaying;
	TextArea directScore;
	TextField scorePath;
	TextField bpm;
	TextField tuning;
	@Override
	public void init() {
		finch = new Finch();
		composer = new Composer(120);
		nowPlaying = null;
		directScore = new TextArea(Composer.NOKIA_TUNE);
		directScore.setPrefRowCount(5);
		directScore.setPrefColumnCount(20);
		scorePath = new TextField();
		scorePath.setPrefColumnCount(20);
		bpm = new TextField("120");
		bpm.setPrefColumnCount(5);
		tuning = new TextField("440");
		
	}
	
	@Override
	public void start(Stage stage) {
		// Buttons
		Button playFromTextbox = new Button("Play!");
		Button playFromFile = new Button("Play from file");
		Button quit = new Button("Quit");
		quit.setCancelButton(true);
		
		// Button actions
		playFromTextbox.setOnAction(event -> {
			System.out.println("playFromTextbox fired: "+event.toString());
			if(nowPlaying != null && !nowPlaying.isAlive())
				nowPlaying.stop();
			reinitComposer();
			finch.sleep(100);
			
			nowPlaying = new Thread(() -> {
				System.out.println("Now playing from textbox");
				try {
					play(composer.compileScore(directScore.getText()));
				}catch(BadNoteException e) {
					fInTheConsole(e, "Invalid note", "There's an invalid note in the score!");
				}catch(ThreadDeath murder) {
					/*
					 * Thread.stop() apparently works by making the thread in concern
					 * throw a ThreadDeath, a Throwable that doesn't automatically
					 * printStackTrace() unlike other Throwables. To make sure threads
					 * brutally murdered by stop() commit unexist instead of squirming in agony,
					 * don't forget to throw back any ThreadDeath that you catch.
					 * 
					 * Seriously, don't try this at home.
					 */
					System.out.println("Textbox playback aborted!");
					throw murder;
				}
				System.out.println("Finished playing from textbox");
			});
			
			nowPlaying.start();
		});
		
		playFromFile.setOnAction(event -> {
			System.out.println("playFromFile fired: "+event.toString());
			if(nowPlaying != null && !nowPlaying.isAlive())
				nowPlaying.stop();
			reinitComposer();
			finch.sleep(100);
			
			nowPlaying = new Thread(() -> {
				System.out.println("Now playing from file");
				try(BufferedReader file = new BufferedReader(new InputStreamReader(new FileInputStream(scorePath.getText())))){
					// Concatenate all lines into one massive line. Yes, yes, that's no identity, now shut the heap your up
					play(composer.compileScore(file.lines().reduce("", (one, two)->one+" "+two)));
				}catch(FileNotFoundException e) {
					fInTheConsole(e, "File not found", scorePath.getText() + " doesn't exist or is otherwise inaccessible. Maybe try giving a full path?");
				}catch(IOException e) {
					fInTheConsole(e, "Something happened to I/O", "Something happened to I/O");
				}catch(BadNoteException e) {
					fInTheConsole(e, "Invalid note", "There's an invalid note in the score!");
				}catch(ThreadDeath murder) {
					System.out.println("File playback aborted!");
					throw murder;
				}
				System.out.println("Finished playing from file");
			});
		});
		
		quit.setOnAction(event -> {
			System.out.println("quit fired: "+event.toString());
			if(nowPlaying != null && !nowPlaying.isAlive())
				nowPlaying.stop();
			finch.quit();
			stage.hide();
			System.exit(0);
		});
		
		GridPane screen = new GridPane();
		
		
	}
	
	void fInTheConsole(Throwable t, String title, String message) {
		t.printStackTrace();
		Alert dialog = new Alert(AlertType.ERROR);
		dialog.setTitle(title);
		dialog.setHeaderText(message);
		dialog.getDialogPane().setExpandableContent(stackTraceTextbox(t));
		dialog.showAndWait();
	}
	
	Node stackTraceTextbox(Throwable t) {
		// Blurb before textbox
		Label header = new Label("Here's the stack barf^H^H^H^Htrace, you might be able to do something with it:");
		
		// Textbox
		StringWriter hmpf = new StringWriter();
		t.printStackTrace(new PrintWriter(hmpf));
		TextArea okbuddyjava = new TextArea(hmpf.toString());
		okbuddyjava.setEditable(false);
		okbuddyjava.setWrapText(true);
		okbuddyjava.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		GridPane.setVgrow(okbuddyjava, Priority.ALWAYS);
		GridPane.setHgrow(okbuddyjava, Priority.ALWAYS);
		
		// Glue it all together
		GridPane theThing = new GridPane();
		theThing.setMaxWidth(Double.MAX_VALUE);
		theThing.add(header, 0, 0);
		theThing.add(okbuddyjava, 0, 1);
		return theThing;
	}

	void play(int[][] song) {
		for(int[] note : song)
			finch.buzzBlocking(note[0], note[1]);
	}
	
	void reinitComposer() {
		if(composer.tempo() != Integer.parseInt(bpm.getText()) || composer.a4Frequency() != Integer.parseInt(tuning.getText()))
			composer = new Composer(Integer.parseInt(bpm.getText()), Integer.parseInt(tuning.getText()));
	}
}