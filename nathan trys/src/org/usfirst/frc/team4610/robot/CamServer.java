package org.usfirst.frc.team4610.robot;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;

public class CamServer implements Runnable {
	
	private int ImageWidth=640;
	private int ImageHeight=480;
	private boolean VisionRecognition=false;
	/**
	 * 
	 * @param ResolutionWidth
	 * 	Width resolution of the image
	 * @param ResolutionHeight
	 * 	Height resolution of the image
	 * @param UseOpenCV
	 * 	Boolean value to use image processing to manipulate the raw video,
	 * 	 see FIRST Java Docs for all possible options org.opencv.imgproc.Imgproc
	 */
	public CamServer(int ResolutionWidth,int ResolutionHeight,boolean UseOpenCV) {
		VisionRecognition=UseOpenCV;
		ImageWidth=ResolutionWidth;
		ImageHeight=ResolutionHeight;
	}

	@Override
	public void run() {
		
		// TODO Auto-generated method stub
		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
        camera.setResolution(ImageWidth, ImageHeight); //Sets Resolution of camera
        if(VisionRecognition) {
	        CvSink cvSink = CameraServer.getInstance().getVideo(); //Gets the raw video stream
	        CvSource outputStream = CameraServer.getInstance().putVideo("GrayScale", ImageWidth, ImageHeight);//Creates new video stream 
	        
	        Mat source = new Mat();//Source Image Matrix
	        Mat output = new Mat();//Output Image Matrix
	        
	        while(!Thread.interrupted()) {
	            cvSink.grabFrame(source); //Sets source to the raw image
	            Imgproc.cvtColor(source, output, Imgproc.COLOR_BGR2GRAY); //Changes the raw image to a grayscale and sets output to the gray scale
	            outputStream.putFrame(output);//Send the grayscale immage
	        }
        }
	}
}
