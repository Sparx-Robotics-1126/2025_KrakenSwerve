package org.team1126.robot.commands;

import static edu.wpi.first.wpilibj2.command.Commands.*;

import org.team1126.robot.Robot;
import org.team1126.robot.subsystems.Swerve;

import edu.wpi.first.wpilibj2.command.Command;

/**
 * The Routines class contains command compositions that require
 * multiple subsystems, such as sequences or parallel command groups.
 */
public final class Routines {

    private final Swerve swerve;

    public Routines(Robot robot) {
        swerve = robot.swerve;
    }

    /**
     * An example routine.
     */
    public Command example() {
        return sequence(
            swerve.drive(() -> 0.5, () -> 0.0, () -> 0.0).withTimeout(1.0),
            swerve.stop(false).withTimeout(1.0),
            print("Done!")
        ).withName("Routines.example()");
    }
}
