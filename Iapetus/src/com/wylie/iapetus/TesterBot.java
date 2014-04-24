package com.wylie.iapetus;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class TesterBot
{
	private Robot robot;
	private ArrayList<ApplicationModel> runningApps;
	private ScreenRectangle taskSwitcher;
	
	public TesterBot()
	{
		try
		{
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
			runningApps = new ArrayList<ApplicationModel>();
			robot = new Robot();
		} catch (Exception e)
		{
			JOptionPane.showMessageDialog(new JFrame(), "OpenCV could not find it's native library./n(Check the project's .classpath file)", "Fatal Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	public void findTaskBar() {
		// show desktop
		// open window's start menu via key press
		// absolute difference with the desktop image
		// perform an open
		// largest blob should be the windows screen
		
		// unfortunately if the task bar is on the left or the top it will show
		// up in the same place
		// right click on both and see if it a menu with 'start task menu' pops
		// up
		
		// test hypothesis:
		// - hover over window's button
		// - (see if start text pops up)
		// - press windows button
		// - (see if the start menu pops up)
		
		showDesktop();
		waitFor(1000);
		Mat orig = screenshotAsMat();
		Mat result = new Mat();
		robot.keyPress(KeyEvent.VK_WINDOWS);
		robot.keyRelease(KeyEvent.VK_WINDOWS);
		waitFor(1000);
		Mat matTwo = screenshotAsMat();
		
		Mat mat = new Mat();
		
		Core.absdiff(orig, matTwo, result);
		Imgproc.cvtColor(result, result, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(result, result, 1, 255, Imgproc.THRESH_BINARY);
		
		show(result);
		ArrayList<Integer> horizontals = new ArrayList<Integer>();
		
		Imgproc.cvtColor(orig, mat, Imgproc.COLOR_BGR2GRAY);
		for (int y = 0; y < mat.height(); y++)
		{
			double[] dL = mat.get(y, 0);
			double line = dL[0];
			boolean isLine = true;
			
			for (int x = 0; x < mat.width() && isLine; x++)
			{
				double[] data = mat.get(y, x);
				double d = data[0];
				if (Math.abs(d - line) > 30)
				{
					isLine = false;
				}
				
			}
			if (isLine)
			{
				horizontals.add(y);
			}
		}
		
		for (Integer y : horizontals)
		{
			Core.line(orig, new Point(0, y), new Point(orig.width() - 1, y), new Scalar(0, 0, 255), 5);
		}
		
		this.shrinkBy(orig, .5);
		show(orig);
	}
	
	public Point findTarget(Mat orig, Mat target) {
		Mat result = new Mat();
		Imgproc.matchTemplate(orig, target, result, Imgproc.TM_SQDIFF_NORMED);
		MinMaxLocResult mmr = Core.minMaxLoc(result);
		int match_method = Imgproc.TM_SQDIFF_NORMED;
		
		Point matchLoc;
		if (match_method == Imgproc.TM_SQDIFF || match_method == Imgproc.TM_SQDIFF_NORMED)
		{
			matchLoc = mmr.minLoc;
		} else
		{
			matchLoc = mmr.maxLoc;
		}
		
		Mat clone = orig.clone();
		Core.rectangle(clone, matchLoc, new Point(matchLoc.x + target.cols(), matchLoc.y + target.rows()), new Scalar(0, 0, 255), 5);
		return matchLoc;
	}
	
	public void findTaskSwitcherBounds(boolean keepTaskSwitcherOpen) {
		showDesktop();
		waitFor(1000);
		Mat mat = screenshotAsMat();
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_TAB);
		waitFor(1000);
		
		Mat matTwo = screenshotAsMat();
		
		// If keep open a function wants it to stay open, leave it open
		if (!keepTaskSwitcherOpen)
		{
			robot.keyRelease(KeyEvent.VK_ALT);
		}
		
		Mat result = new Mat();
		
		Core.absdiff(mat, matTwo, result);
		Imgproc.cvtColor(result, result, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(result, result, 1, 255, Imgproc.THRESH_BINARY);
		
		Mat lines = new Mat();
		
		Imgproc.dilate(result, result, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(40, 40)));
		Imgproc.erode(result, result, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(40, 40)));
		Imgproc.Canny(result, result, 80, 100);
		Imgproc.HoughLines(result, lines, 1, Math.PI / 180, 100);
		
		ArrayList<Double> verticalLines = new ArrayList<Double>();
		ArrayList<Double> horizontalLines = new ArrayList<Double>();
		mat = screenshotAsMat();
		for (int i = 0; i < lines.width(); i++)
		{
			double[] temp = lines.col(i).get(0, 0);
			
			System.out.println(temp[0] + " " + temp[1]);
			double rho = temp[0];
			double theta = temp[1];
			
			if (theta == 0)
			{
				verticalLines.add(rho);
			} else
			{
				horizontalLines.add(rho);
			}
			
		}
		
		double minX = result.width(), maxX = 0;
		double minY = result.height(), maxY = 0;
		
		for (double x : verticalLines)
		{
			if (x < minX && x > 0)
			{
				minX = x;
			}
			
			if (x > maxX && x < mat.width())
			{
				maxX = x;
			}
		}
		
		for (double y : horizontalLines)
		{
			if (y < minY && y > 0)
			{
				minY = y;
			}
			
			if (y > maxY && y < mat.height() - 100)
			{
				maxY = y;
			}
		}
		this.taskSwitcher = new ScreenRectangle(minX, minY, maxX, maxY);
		
	}
	
	public Mat getScreenRectAsMat(Rectangle rect) {
		Mat mat = new Mat();
		BufferedImage image = robot.createScreenCapture(rect);
		// robot.keyRelease(KeyEvent.VK_ALT);
		BufferedImage bgrScreenshot = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		boolean done = bgrScreenshot.getGraphics().drawImage(image, 0, 0, null);
		
		byte[] pixels = ((DataBufferByte) bgrScreenshot.getRaster().getDataBuffer()).getData();
		
		mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		
		mat.put(0, 0, pixels);
		return mat;
	}
	
	public Mat getTaskSwitcher() {
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_TAB);
		
		Mat mat = new Mat();
		BufferedImage image = robot.createScreenCapture(taskSwitcher.toRectangle());
		robot.keyRelease(KeyEvent.VK_ALT);
		BufferedImage bgrScreenshot = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		boolean done = bgrScreenshot.getGraphics().drawImage(image, 0, 0, null);
		
		byte[] pixels = ((DataBufferByte) bgrScreenshot.getRaster().getDataBuffer()).getData();
		
		mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		
		mat.put(0, 0, pixels);
		return mat;
	}
	
	public Mat screenshotAsMat() {
		
		Mat mat = new Mat();
		
		BufferedImage image = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		
		BufferedImage bgrScreenshot = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		boolean done = bgrScreenshot.getGraphics().drawImage(image, 0, 0, null);
		
		byte[] pixels = ((DataBufferByte) bgrScreenshot.getRaster().getDataBuffer()).getData();
		
		mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		
		mat.put(0, 0, pixels);
		return mat;
		
	}
	
	public BufferedImage matToBufferedImage(Mat mat) {
		try
		{
			MatOfByte bytemat = new MatOfByte();
			Highgui.imencode(".jpg", mat, bytemat);
			byte[] bytes = bytemat.toArray();
			InputStream in = new ByteArrayInputStream(bytes);
			BufferedImage img = ImageIO.read(in);
			return img;
			
		} catch (IOException e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public Mat shrinkBy(Mat mat, double ratio) {
		Imgproc.resize(mat, mat, new Size(Math.floor(mat.width() * ratio), Math.floor(mat.height() * ratio)), 0, 0, Imgproc.INTER_CUBIC);
		return mat;
	}
	
	public void keyPressSequence(Collection<Integer> keys) {
		for (Integer key : keys)
		{
			robot.keyPress(key);
		}
		
		for (Integer key : keys)
		{
			robot.keyRelease(key);
		}
	}
	
	public void maximizeSelectedWindow() {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		
		keys.add(KeyEvent.VK_ALT);
		keys.add(KeyEvent.VK_SPACE);
		keyPressSequence(keys);
		
		keys.clear();
		keys.add(KeyEvent.VK_SHIFT);
		keys.add(KeyEvent.VK_X);
		keyPressSequence(keys);
	}
	
	public void fitToWindow() {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		keys.add(KeyEvent.VK_SHIFT);
		keys.add(KeyEvent.VK_CONTROL);
		keys.add(KeyEvent.VK_W);
		keyPressSequence(keys);
	}
	
	public void windowKeys() {
		robot.keyPress(KeyEvent.VK_WINDOWS);
		robot.keyRelease(KeyEvent.VK_WINDOWS);
		this.waitFor(1000);
		robot.keyPress(KeyEvent.VK_WINDOWS);
		robot.keyRelease(KeyEvent.VK_WINDOWS);
	}
	
	public void showDesktop() {
		ArrayList<Integer> keys = new ArrayList<Integer>();
		keys.add(KeyEvent.VK_WINDOWS);
		keys.add(KeyEvent.VK_D);
		keyPressSequence(keys);
	}
	
	public void show(Mat mat) {
		BufferedImage bi2 = this.matToBufferedImage(mat);
		JFrame jf2 = new JFrame("Result");
		jf2.setLocationRelativeTo(null);
		jf2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf2.add(new ImagePanel(bi2));
		
		jf2.pack();
		jf2.setVisible(true);
		jf2.setSize(500, 500);
		// maximizeSelectedWindow();
	}
	
	public void closeApp(String appName) {
		findTaskSwitcherBounds(false);
		
		robot.keyPress(KeyEvent.VK_ALT);
		robot.keyPress(KeyEvent.VK_TAB);
		robot.keyRelease(KeyEvent.VK_TAB);
		
		Mat orig = getScreenRectAsMat(taskSwitcher.toRectangle());
		Mat mat = new Mat();
		Mat grey = new Mat();
		Mat textGrey = new Mat();
		
		Imgproc.cvtColor(orig, mat, Imgproc.COLOR_BGR2GRAY);
		grey = mat.clone();
		textGrey = mat.clone();
		
		Imgproc.threshold(textGrey, textGrey, 15, 255, Imgproc.THRESH_BINARY_INV);
		ScreenRectangle r = getTextBounds(textGrey);
		
		Mat textAreaOrig = orig.clone();
		Core.rectangle(orig, r.getTopLeft(), r.getBottomRight(), new Scalar(255, 255, 0));
		
		System.out.println((int) r.getTopLeft().y + " " + (int) r.getTopLeft().x + " " + (int) r.getBottomRight().y + " " + (int) r.getBottomRight().x);
		
		textAreaOrig = textAreaOrig.submat((int) r.getTopLeft().y, (int) r.getBottomRight().y, (int) r.getTopLeft().x, (int) r.getBottomRight().x);
		
		readText(textAreaOrig);
		
		Imgproc.threshold(grey, grey, 200, 255, Imgproc.THRESH_TOZERO_INV);
		
		Imgproc.erode(grey, grey, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
		Imgproc.dilate(grey, grey, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
		Imgproc.threshold(grey, grey, 30, 255, Imgproc.THRESH_BINARY);
		
		Imgproc.dilate(grey, grey, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(14, 14)));
		Imgproc.erode(grey, grey, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(14, 14)));
		
		int topline = 0;
		for (int i = 0; i < grey.height(); i++)
		{
			double[] ds = grey.get(i, 0);
			if (ds[0] > 0)
			{
				topline++;
			} else
			{
				break;
			}
		}
		
		mat = mat.submat(topline, grey.height(), 0, grey.width());
		grey = grey.submat(topline, grey.height(), 0, grey.width());
		orig = orig.submat(topline, orig.height(), 0, orig.width());
		Collection<Blob> blobs = VisHel.lableConnectedComponents(grey);
		
		for (Blob blob : blobs)
		{
			if (blob.getSize() > 2500)
			{
				
				Core.rectangle(orig, blob.getTopLeft(), blob.getBottomRight(), new Scalar(255, 0, 0), 4);
			} else
			{
				Core.rectangle(orig, blob.getTopLeft(), blob.getBottomRight(), new Scalar(0, 0, 255), 2);
			}
		}
		
		show(orig);
		robot.keyRelease(KeyEvent.VK_ALT);
	}
	
	private String readText(Mat textAreaOrig) {
		String result = "failed to read text";
		return result;
	}
	
	private ScreenRectangle getTextBounds(Mat textGrey) {
		int xMin = textGrey.width();
		int xMax = 0;
		int yMin = textGrey.height();
		int yMax = 0;
		
		for (int y = 0; y < textGrey.height(); y++)
		{
			for (int x = 0; x < textGrey.width(); x++)
			{
				double[] data = textGrey.get(y, x);
				
				if (data[0] > 0)
				{
					if (x < xMin)
					{
						xMin = x;
					}
					if (y < yMin)
					{
						yMin = y;
					}
					if (x > xMax)
					{
						xMax = x;
					}
					if (y > yMax)
					{
						yMax = y;
					}
				}
			}
		}
		
		int buffer = 10;
		
		xMin -= buffer;
		yMin -= buffer;
		xMax += buffer;
		yMax += buffer;
		
		ScreenRectangle result = new ScreenRectangle(xMin, yMin, xMax, yMax);
		
		return result;
	}
	
	public ScreenRectangle getPopupBounds(Mat textGrey) {
		int xMin = textGrey.width();
		int xMax = 0;
		int yMin = textGrey.height();
		int yMax = 0;
		
		for (int y = 0; y < textGrey.height(); y++)
		{
			for (int x = 0; x < textGrey.width(); x++)
			{
				double[] data = textGrey.get(y, x);
				
				if (data[0] > 0)
				{
					if (x < xMin)
					{
						xMin = x;
					}
					if (y < yMin)
					{
						yMin = y;
					}
					if (x > xMax)
					{
						xMax = x;
					}
					if (y > yMax)
					{
						yMax = y;
					}
				}
			}
		}
		
		yMin += 150;
		
		ScreenRectangle result = new ScreenRectangle(xMin, yMin, xMax, yMax);
		
		return result;
	}
	
	public void processTaskSwitcher() {
		
		findTaskSwitcherBounds(true);
		/* task Switcher should already be open */
		
		Mat orig = getScreenRectAsMat(taskSwitcher.toRectangle());
		Mat mat = new Mat();
		Mat grey = new Mat();
		Mat textGrey = new Mat();
		
		Imgproc.cvtColor(orig, mat, Imgproc.COLOR_BGR2GRAY);
		grey = mat.clone();
		textGrey = mat.clone();
		
		histogramTest(grey);
		
		Imgproc.threshold(textGrey, textGrey, 15, 255, Imgproc.THRESH_BINARY_INV);
		ScreenRectangle r = getTextBounds(textGrey);
		
		Mat textAreaOrig = orig.clone();
		Core.rectangle(orig, r.getTopLeft(), r.getBottomRight(), new Scalar(255, 255, 0));
		
		System.out.println((int) r.getTopLeft().y + " " + (int) r.getTopLeft().x + " " + (int) r.getBottomRight().y + " " + (int) r.getBottomRight().x);
		
		textAreaOrig = textAreaOrig.submat((int) r.getTopLeft().y, (int) r.getBottomRight().y, (int) r.getTopLeft().x, (int) r.getBottomRight().x);
		
		readText(textAreaOrig);
		
		Imgproc.threshold(grey, grey, 200, 255, Imgproc.THRESH_TOZERO_INV);
		
		Imgproc.erode(grey, grey, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
		Imgproc.dilate(grey, grey, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3)));
		Imgproc.threshold(grey, grey, 30, 255, Imgproc.THRESH_BINARY);
		
		Imgproc.dilate(grey, grey, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(14, 14)));
		Imgproc.erode(grey, grey, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(14, 14)));
		
		int topline = 0;
		for (int i = 0; i < grey.height(); i++)
		{
			double[] ds = grey.get(i, 0);
			if (ds[0] > 0)
			{
				topline++;
			} else
			{
				break;
			}
		}
		
		mat = mat.submat(topline, grey.height(), 0, grey.width());
		grey = grey.submat(topline, grey.height(), 0, grey.width());
		orig = orig.submat(topline, orig.height(), 0, orig.width());
		Collection<Blob> blobs = VisHel.lableConnectedComponents(grey);
		
		int numApps = 0;
		
		for (Blob blob : blobs)
		{
			
			if (blob.getSize() > 1000)
			{
				numApps++;
				
				Core.rectangle(orig, blob.getTopLeft(), blob.getBottomRight(), new Scalar(255, 0, 0), 4);
			}
		}
		
		// show(orig);
		
		// First determine the number of applications running
		
		// Tab through the TaskSwitcher to read all the application names
		String build = "";
		for (int i = 0; i < numApps; i++)
		{
			System.out.println("Loop index = " + i);
			robot.keyPress(KeyEvent.VK_TAB);
			this.waitFor(100);
			robot.keyRelease(KeyEvent.VK_TAB);
			this.waitFor(1000);
			Mat currentTaskSwitcher = getScreenRectAsMat(taskSwitcher.toRectangle());
			String appName = getRunningAppName(currentTaskSwitcher);
			appName = appName.replaceAll("(\\r|\\n)", "");
			build += appName + "\n";
		}
		
		// canvas.addLabel("Sikuli Will Look Only Here");
		
		System.out.println("RUNNING APP NAMES: ");
		System.out.println(build);
		robot.keyRelease(KeyEvent.VK_ALT);
		for (ApplicationModel app : runningApps)
		{
			System.out.println(app.getName());
		}
		
	}
	
	private void waitFor(int i) {
		
	}
	
	public void histogramTest(Mat mat) {
		int[] count = new int[256];
		
		for (int i = 0; i < 256; i++)
		{
			count[i] = 0;
		}
		
		for (int y = 0; y < mat.height(); y++)
		{
			for (int x = 0; x < mat.width(); x++)
			{
				double[] data = mat.get(y, x);
				count[(int) data[0]] += 1;
			}
		}
		
		int max = 0;
		int median = 0;
		
		for (int i = 0; i < 256; i++)
		{
			if (count[i] > max)
			{
				max = count[i];
				median = i;
			}
		}
		
		System.out.println("============================");
		System.out.println("median = " + median);
		System.out.println("============================");
	}
	
	private void writeImageToFile(Mat image, String fileName) {
		System.out.println("Writing " + fileName + " to file.");
		Highgui.imwrite(fileName, image);
	}
	
	private String getRunningAppName(Mat image) {
		String result = "";
		Mat orig = getScreenRectAsMat(taskSwitcher.toRectangle());
		Mat mat = new Mat();
		Mat textGrey = new Mat();
		
		Imgproc.cvtColor(orig, mat, Imgproc.COLOR_BGR2GRAY);
		textGrey = mat.clone();
		
		Imgproc.threshold(textGrey, textGrey, 15, 255, Imgproc.THRESH_BINARY_INV);
		
		Imgproc.dilate(textGrey, textGrey, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 20)));
		Imgproc.erode(textGrey, textGrey, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(24, 24)));
		Imgproc.dilate(textGrey, textGrey, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4, 4)));
		show(textGrey);
		ScreenRectangle r = getTextBounds(textGrey);
		Mat textAreaOrig = orig.clone();
		
		System.out.println((int) r.getTopLeft().y + " " + (int) r.getTopLeft().x + " " + (int) r.getBottomRight().y + " " + (int) r.getBottomRight().x);
		try
		{
			textAreaOrig = textAreaOrig.submat((int) r.getTopLeft().y, (int) r.getBottomRight().y, (int) r.getTopLeft().x, (int) r.getBottomRight().x);
			result = readText(textAreaOrig);
		} catch (Exception e)
		{
			return " Error processing name.";
		}
		
		return result;
	}
	/*
	public VertacleScrollArea findStencleRibbon(ScreenLocatin p) {
		Mat orig = screenshotAsMat();
		Mat mat = orig.clone();
		
		Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
		
		Imgproc.threshold(mat, mat, 254, 255, Imgproc.THRESH_BINARY);
		
		Imgproc.dilate(mat, mat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));
		Imgproc.erode(mat, mat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(20, 20)));
		Imgproc.dilate(mat, mat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(100, 100)));
		Imgproc.erode(mat, mat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(100, 100)));
		Imgproc.dilate(mat, mat, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(25, 25)));
		
		Collection<Blob> blobs = VisHel.lableConnectedComponents(mat);
		
		Blob target = null;
		for (Blob blob : blobs)
		{
			double xBr = blob.getBottomRight().x;
			double yBr = blob.getBottomRight().y;
			double xTl = blob.getTopLeft().x;
			double yTl = blob.getTopLeft().y;
			// Core.rectangle(orig, blob.getTopLeft(), blob.getBottomRight(),
			// new Scalar(0, 255, 0), 4);
			if (((xTl < p.getX()) && (p.getX() < xBr)) && ((yTl < p.getY()) && (p.getY() < yBr)))
			{
				target = blob;
				target.setBottomRight(new Point(target.getBottomRight().x + 20, target.getBottomRight().y));
				// Core.rectangle(orig, blob.getTopLeft(),
				// blob.getBottomRight(), new Scalar(255, 0, 0), 4);
			}
			
		}
		// shrinkBy(orig, .5);
		// show(orig);
		
		VertacleScrollArea vsa = new VertacleScrollArea(target.getTopLeft().x, target.getTopLeft().y, target.getBottomRight().x, target.getBottomRight().y);
		vsa.setUp(new Point(target.getBottomRight().x - 15, target.getTopLeft().y + 15));
		vsa.setDown(new Point(target.getBottomRight().x - 15, target.getBottomRight().y - 15));
		
		return vsa;
	}
	*/
	private void waitForSeconds(int i) {
		try
		{
			Thread.sleep(i * 1000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void clickOn(Point down) {
		robot.mouseMove((int) down.x, (int) down.y);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	public void zoomIn(int j) {
		try
		{
			robot.keyPress(KeyEvent.VK_CONTROL);
			Thread.sleep(100);
			robot.mouseWheel(-3);
			Thread.sleep(100);
			robot.mouseWheel(-3);
			Thread.sleep(100);
			robot.mouseWheel(-3);
			Thread.sleep(100);
			robot.mouseWheel(-3);
			Thread.sleep(100);
			robot.mouseWheel(-3);
			Thread.sleep(100);
			robot.keyRelease(KeyEvent.VK_CONTROL);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void dragTo(Point point) {
		this.waitFor(500);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		this.waitFor(500);
		robot.mouseMove((int) point.x, (int) point.y);
		this.waitFor(500);
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
}
