package org.team1126.robot;

import static edu.wpi.first.wpilibj2.command.Commands.*;

import edu.wpi.first.epilogue.Logged;
import edu.wpi.first.epilogue.NotLogged;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;

import org.team1126.lib.logging.LoggedRobot;
import org.team1126.lib.logging.Profiler;
import org.team1126.lib.util.DisableWatchdog;
import org.team1126.robot.commands.Autos;
import org.team1126.robot.commands.Routines;
import org.team1126.robot.subsystems.Lights;
import org.team1126.robot.subsystems.Swerve;

@Logged
public final class Robot extends LoggedRobot {

    private final CommandScheduler scheduler = CommandScheduler.getInstance();

    public final Swerve swerve;
 public final Lights lights;

    public final Routines routines;
    public final Autos autos;

    private final CommandXboxController driver;
    private final CommandXboxController coDriver;

    public Robot() {
        // Initialize subsystems
        lights = new Lights();
        swerve = new Swerve();

        // Initialize compositions
        routines = new Routines(this);
        autos = new Autos(this);

        // Initialize controllers
        driver = new CommandXboxController(Constants.DRIVER);
        coDriver = new CommandXboxController(Constants.CO_DRIVER);

        // Set default commands
        swerve.setDefaultCommand(swerve.drive(this::driverX, this::driverY, this::driverAngular));

        // Driver bindings
        driver.a().onTrue(none());
        driver.povLeft().onTrue(swerve.tareRotation());
        driver.leftStick().whileTrue(swerve.turboSpin(this::driverX, this::driverY, this::driverAngular));

        // Co-driver bindings
        coDriver.a().onTrue(none());
        // Setup lights only if the LED controller initialized correctly
        if (lights.isAvailable()) {
            routines.lightsPreMatch(autos::defaultSelected).schedule();

            RobotModeTriggers.disabled().whileTrue(
                routines.lightsPreMatch(autos::defaultSelected)
            );
        }

        // lights.sides.setDefaultCommand(lights.sides.levelSelection(selection));
        // lights.top.setDefaultCommand(
        //     lights.top.coralDisplay(gooseNeck::hasCoral, gooseNeck::goosing, gooseNeck::getPosition, selection)
        // );
        // Disable loop overrun warnings from the command
        // scheduler, since we already log loop timings
        DisableWatchdog.in(scheduler, "m_watchdog");

        // Configure the brownout threshold to match RIO 1
        RobotController.setBrownoutVoltage(6.3);
    }

    @NotLogged
    public double driverX() {
        return driver.getLeftX();
    }

    @NotLogged
    public double driverY() {
        return driver.getLeftY();
    }

    @NotLogged
    public double driverAngular() {
        return -driver.getRightX();
        //this is how 340 drives
        // return driver.getLeftTriggerAxis() - driver.getRightTriggerAxis();
    }

    @Override
    public void robotPeriodic() {
        Profiler.run("scheduler", scheduler::run);
        if (lights.isAvailable()) {
            Profiler.run("lights", lights::update);
        }
    }
}
