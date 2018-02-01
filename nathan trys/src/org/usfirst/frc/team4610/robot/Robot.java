/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4610.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team4610.robot.commands.ExampleCommand;
import org.usfirst.frc.team4610.robot.subsystems.ExampleSubsystem;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

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
	WPI_TalonSRX _frontLeftDrive = new WPI_TalonSRX(4);
	WPI_TalonSRX _frontRightDrive = new WPI_TalonSRX(2);
	WPI_TalonSRX _rearLeftDrive = new WPI_TalonSRX(3);
	WPI_TalonSRX _rearRightDrive = new WPI_TalonSRX(1);
	RobotDrive chassis=new RobotDrive(_frontLeftDrive, _frontRightDrive , _rearLeftDrive, _rearRightDrive );
	Joystick joy1=new Joystick(0);
	Joystick joy2=new Joystick(1);
	Compressor c1=new Compressor();
	DoubleSolenoid s1=new DoubleSolenoid(1,2);
	Command m_autonomousCommand;
	SendableChooser<Command> m_chooser = new SendableChooser<>();
		//*** commented out code for things not get on the bot 
	// the folowing is fow the intake/outake 
		//Victor leftIntake=new Victor(1);
		//Victor rightIntake=new Victor(2); 
	
	// the following is for the elevator 
		//Victor e1=new Victor(3);
		//Victor e2=new Victor(4); 
	
	// this section is for the climber
		//Victor climb=new Victor(5);
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
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
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
		// following is drive train 
			chassis.tankDrive(joy1, joy2);
			if(joy2.getRawButton(3))
			{
				s1.set(DoubleSolenoid.Value.kForward);
			}
			if(joy2.getRawButton(4))
			{
				s1.set(DoubleSolenoid.Value.kReverse);
			}
		// following is intake
			//**** code is there just commented out untill on bot
			
		//if(joy1.getRawButton(3))
		//{
		//	leftIntake.set(1);
		//	rightIntake.set(-1);
		//}
		//else if(joy1.getRawButton(4))
		//{
		//leftIntake.set(-1);
		//rightIntake.set(1);
		//}
		//else 
		//{
		//	leftIntake.set(0);
		//	rightIntake.set(0);
		//}
		
		// following is elevator
		
		// following is climber
			//**** code is there just commented out untill on bot
			
			//if(joy1.getRawButton(1))
			//{
				//climb.set(1);
			
			//}
			//else 
			//{
				//climb.set(0);
			//}
		
		Scheduler.getInstance().run();
	}
	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}
