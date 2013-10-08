import java.awt.AWTException;
import java.awt.Robot;


public class Iapetus {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Robot robot = new Robot();
			
			
			robot.mouseMove(300, 550);
			
			
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
