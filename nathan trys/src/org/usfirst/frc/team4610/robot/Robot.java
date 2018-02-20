/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4610.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.drive.RobotDriveBase.MotorType;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4610.robot.commands.ExampleCommand;
import org.usfirst.frc.team4610.robot.subsystems.ExampleSubsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.kauailabs.navx.frc.AHRS.SerialDataType;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends TimedRobot {
	public static final ExampleSubsystem kExampleSubsystem
			= new ExampleSubsystem();
	public static OI m_oi;
	//the wpi talons are the 4 motors for the drive base 
	//in order to address (the number in parantehesis, look at the internet
	// explorer (which is needed) page 172.22.11.2 if using usb to address the cam motors to
	// whatever you write here
	//************update firmware may be nessesary***************
	//screensteps has more info if needed 
	//public WPI_TalonSRX driveFrontLeft4 = new WPI_TalonSRX(4);
	//public WPI_TalonSRX driveFrontRight2 = new WPI_TalonSRX(2);
	//public WPI_TalonSRX driveRearLeft3 = new WPI_TalonSRX(3);
	//public WPI_TalonSRX driveRearRight1 = new WPI_TalonSRX(1);
	//being depricated isnt an issue 
	//RobotDrive chassis=new RobotDrive(driveFrontRight2, driveRearRight1 , driveFrontLeft4, driveRearLeft3 );
	Joystick gamePad=new Joystick(0);
	Joystick control=new Joystick(1);
	//Compressor c1=new Compressor();
	//DoubleSolenoid s1=new DoubleSolenoid(1,2);
	Command m_autonomousCommand;
	SendableChooser<Command> m_chooser = new SendableChooser<>();
	//user input for robot position
	SendableChooser<String> position = new SendableChooser<>();
	String gameData = DriverStation.getInstance().getGameSpecificMessage();
	String testPosition = "";
	//Things for automated intake
	boolean intakeAuto = true;
	boolean intakeAutoRunning = false;
	DigitalInput intakeSwitch = new DigitalInput(9);
		 //commented out code for things not get on the bot 
	 //the folowing is fow the intake/outake 
	WPI_TalonSRX intakeLeft5 = new WPI_TalonSRX(5);
	WPI_TalonSRX intakeRight6 = new WPI_TalonSRX(6); 
	Counter intakeCounter = new Counter(intakeSwitch);
	boolean intakeFixer = false;
	int intakeFixCounter = 0;
	
	// the following is for the lift 
	WPI_TalonSRX lift7=new WPI_TalonSRX(7);
	int liftPosition = 1;
	DigitalInput liftSwitchLowest = new DigitalInput(8);
	DigitalInput intakeSwitchSwitch = new DigitalInput(7);
	DigitalInput intakeSwitchScale = new DigitalInput(6);
	Counter liftCounter1 = new Counter(liftSwitchLowest);
	Counter liftCounter2 = new Counter(intakeSwitchSwitch);
	Counter liftCounter3 = new Counter(intakeSwitchScale);
	int liftMovingTo = 0;
	
	//gyro things
	AHRS gyro = new AHRS(SerialPort.Port.kUSB1);
	
	//EXAMPLE ENCODER THINGS
	//Encoder exampleEncoder = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
	/*sampleEncoder.setMaxPeriod(.1);
	sampleEncoder.setMinRate(10);
	sampleEncoder.setDistancePerPulse(5); IMPORTANT - sets distance to measure per pulse(duh)
	sameEncoder.get();  IMPORTANT finds count (pulses)
	sampleEncoder.setReverseDirection(true);
	sampleEncoder.setSamplesToAverage(7);
	sampleEncoder.reset();
	USE .get TO FIND THESE THINGS
	*/
	//Encoder liftEncoder = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
	//liftEncoder.setDistancePerPulse(5);
	//WPI_TalonSRX liftBackup8=new WPI_TalonSRX(8);
	
	
	// this section is for the climber
//		WPI_TalonSRX climbLeft9=new WPI_TalonSRX(9);
	//	WPI_TalonSRX climbRight10=new WPI_TalonSRX(10);
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_oi = new OI();
		m_chooser.addDefault("Default Auto", new ExampleCommand());
		CameraServer.getInstance().startAutomaticCapture();
		//_rearRightDrive.follow(_frontRightDrive);
		//_rearLeftDrive.follow(_frontLeftDrive);
		// chooser.addObject("My Auto", new MyAutoCommand());
		//this is one option we have. also we can set it to inverted 
		
		SmartDashboard.putData("Auto mode", m_chooser);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		m_autonomousCommand = m_chooser.getSelected();
		position.addObject("Left", "L");
		position.addDefault("Middle", "M");
		position.addObject("Right", "R");
		SmartDashboard.putData("Auto Mode", position);
		testPosition = position.getSelected();
		// ***put back in for testing ***gameData = "LRR";
		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		if (m_autonomousCommand != null) {
			m_autonomousCommand.start();
		}
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		//gyro.getAngle() for angle in auto
		while(isEnabled() && isAutonomous())
		{
			if((position.getSelected()).charAt(0)==gameData.charAt(1))
			{
				//scale code
			}
			else if((position.getSelected()).charAt(0)==gameData.charAt(0))
			{
				//switch code
			}
			else
			{
				//move forward
			}
		}
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		//help for encoder at
		//http://www.ctr-electronics.com/downloads/api/java/html/com/ctre/phoenix/motorcontrol/can/WPI_TalonSRX.html
		//https://github.com/CrossTheRoadElec/Phoenix-Documentation/blob/master/Migration%20Guide.md
		//https://www.chiefdelphi.com/forums/showthread.php?t=138641
		lift7.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, 0);
		//lift7.config_kP(.5, 10) ;
		//lift7.config_kI(0, 10); 
		//lift7.config_kD(0, 10); 
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (m_autonomousCommand != null) {
			m_autonomousCommand.cancel();
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		while(isOperatorControl()&&isEnabled())
		{
			lift7.set(ControlMode.Position, 1);
			lift7.set(10);
		// following is drive train 
			//chassis.tankDrive(joy1, joy2);
			//intakeAuto is a bool to toggele whether the intake
			//function is completed auotmatically or when the user releases the button
			//all the if statements are to allow other code to run
			
			//if(joy1.getRawButton(3))
		//	{
		//		s1.set(DoubleSolenoid.Value.kForward);
		//	}
		//	else if(joy1.getRawButton(4))
		//	{
		//		s1.set(DoubleSolenoid.Value.kReverse);
		
		// following is intake
			//code is there just commented out untill on bot
			/*if((intakeAuto &&intakeAutoRunning) ||(intakeAuto &&control.getRawButton(5)))
			{
				intakeLeft5.set(-1);
				intakeRight6.set(1);
				if(!intakeAutoRunning)
				{
					intakeAutoRunning=true;
				}
				if(intakeCounter.get()>0)
				{
					intakeLeft5.set(0);
					intakeFixer = true;
					intakeAutoRunning = false;
					intakeCounter.reset();
					//add a lift code for ~3 inches for ramps/wire on floor
				}
			}	
			else if(control.getRawButton(5))
			{
				intakeLeft5.set(-1);
				intakeRight6.set(1);
			}
			else if(control.getRawButton(6))
			{
				intakeLeft5.set(1);
				intakeRight6.set(-1);
				intakeFixer = false;
			}
			else 
			{
				if(!intakeFixer)
				{				
				intakeRight6.set(0);
				}
				intakeLeft5.set(0);
			}
		if(control.getRawButton(10))
		{
			intakeAuto = true;
		}
		else if (control.getRawButton(12))
		{
			intakeAuto = false;
		}
		
		if(intakeFixer)
		{
			if(intakeFixCounter >= 50)
			{
				intakeRight6.set(0);
				intakeFixer = false;
				intakeFixCounter = -1;
			}
			intakeFixCounter += 1;
		}
		
		*/
			
			
		 //following is lift with switches
			/*if(liftMovingTo == 1||(liftMovingTo == 0 &&control.getRawButton(7)))
			{
				if(liftPosition != 1)
				{
					if(intakeCounter.get()>0)
					{
						lift7.set(0);
						liftMovingTo = 0;
						liftPosition = 1;
						liftCounter1.reset();
					}
					else
					{
						lift7.set(-1);
						liftMovingTo = 1;
					}
				}
			}
			else if(liftMovingTo == 2||(liftMovingTo == 0 &&control.getRawButton(9)))
			{
				if(liftPosition == 1)
				{
					if(intakeCounter.get()>0)
					{
						lift7.set(0);
						liftMovingTo = 0;
						liftPosition = 2;
						liftCounter2.reset();
					}
					else
					{
						lift7.set(1);
						liftMovingTo = 2;
					}
				}
				else if(liftPosition == 3)
				{
					if(intakeCounter.get()>0)
					{
						lift7.set(0);
						liftMovingTo = 0;
						liftPosition = 2;
						liftCounter2.reset();
					}
					else
					{
						lift7.set(-1);
						liftMovingTo = 2;
					}
				}
			}
			if (liftMovingTo == 3||(liftMovingTo == 0 &&control.getRawButton(11)))
				{
				if(liftPosition != 3)
				{
					if(intakeCounter.get()>0)
					{
						lift7.set(0);
						liftMovingTo = 0;
						liftPosition = 3;
						liftCounter3.reset();
					}
					else
					{
						lift7.set(1);
						liftMovingTo = 3;
					}
				}	
				}*/
			
			
			//lift with encoder
			/*if(liftMovingTo == 1||(liftMovingTo == 0 &&control.getRawButton(7)))
			{
				if(liftPosition != 1)
				{
					if(liftEncoder.getDistance()>1)
					{
						lift7.set(0);
						liftMovingTo = 0;
						liftPosition = 1;
						liftEncoder.reset();
					}
					else
					{
						lift7.set(-1);
						liftMovingTo = 1;
					}
				}
			}
			else if(liftMovingTo == 2||(liftMovingTo == 0 &&control.getRawButton(9)))
			{
				if(liftPosition == 1)
				{
					if(liftEncoder.getDistance()>1)
					{
						lift7.set(0);
						liftMovingTo = 0;
						liftPosition = 2;
						liftEncoder.reset();					}
					else
					{
						lift7.set(1);
						liftMovingTo = 2;
					}
				}
				else if(liftPosition == 3)
				{
					if(liftEncoder.getDistance()>1)
					{
						lift7.set(0);
						liftMovingTo = 0;
						liftPosition = 2;
						liftEncoder.reset();
					}
					else
					{
						lift7.set(-1);
						liftMovingTo = 2;
					}
				}
			}
			if (liftMovingTo == 3||(liftMovingTo == 0 &&control.getRawButton(11)))
				{
				if(liftPosition != 3)
				{
					if(liftEncoder.getDistance()>1)
					{
						lift7.set(0);
						liftMovingTo = 0;
						liftPosition = 3;
						liftEncoder.reset();
					}
					else
					{
						lift7.set(1);
						liftMovingTo = 3;
					}
				}	
				}*/
			
			
		// following is climber
			//**** code is there just commented out untill on bot
			
		/*	if(control.getRawButton(1))
			{
				climbLeft9.set(.25);
				climbRight10.set(.25);
			}
			else if(control.getRawButton(2))
			{
				climbLeft9.set(-.25);
				climbRight10.set(-.25);
			}
			else 
			{
				climbLeft9.set(0);
				climbRight10.set(0);

			}*/
		
		Scheduler.getInstance().run();
	}}
//	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
