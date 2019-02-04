import edu.cmu.ri.createlab.terk.robot.finch.Finch;
import java.util.Scanner;
import java.security.SecureRandom;
public class Feench {

	public static void main(String[] args) {
		Finch bot = new Finch();
		bot.setLED(255, 0, 255);
		new Thread(() -> {
			while(true) {
				bot.setWheelVelocities(100, 100, 250);
				bot.setWheelVelocities(-100, -100, 250);
			}
		}).start();
		System.out.println("\nPress Enter to randomize the LED");
		Scanner enter = new Scanner(System.in);
		SecureRandom rng = new SecureRandom();
		
		while(true) {
			enter.nextLine();
			bot.setLED(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
		}
	}
}
