package org.firstinspires.ftc.teamcode.Teleops;

import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.pedro.Constants;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Turret Test")
public class turretTest extends OpMode {

    private Follower follower;

    private DcMotor turretMotor = hardwareMap.dcMotor.get("turretMotor");

    Pose robotPose = follower.pose();

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
    }

    @Override
    public void loop() {
        DrivePowers powers = ManualDrive.fieldCentric(
                -gamepad1.left_stick_y,
                gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                follower.pose().heading()
        );
        follower.manual(powers);
        follower.update();
        robotPose = follower.pose();

        telemetry.addData("Robot X", robotPose.x());
        telemetry.addData("Robot Y", robotPose.y());
        telemetry.addData("Robot Heading", Math.toDegrees(robotPose.heading()));
    }

    public double scalingFactor = 0.05;

    public double encoderTicksToAngle(int ticks){
        return (ticks*scalingFactor);
    }

    public double angleToEncoderTicks(double degrees){
        return (degrees/scalingFactor);
    }

    public double turnTurretBy(double degrees){
        double currentPosition = turretMotor.getCurrentPosition();
        double TARGET_TICK_VALUE = angleToEncoderTicks(degrees) + currentPosition;

        turretMotor.setTargetPosition(TARGET_TICK_VALUE);
        turretMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turretMotor.setPower(1);
        return currentPosition;
    }


    // you would call aimtoobject when you want the turret to move.
    public void aimToObject(){
        double robotYPosition = robotPose.y(), robotXPosition = robotPose.x();
        double destinationAngle = Math.atan2(72 - robotYPosition,
                72 - robotXPosition);

       double turretAngle = encoderTicksToAngle(turretMotor.getCurrentPosition());
       double robotAngle = Math.toDegrees(robotPose.heading());

        double toTurn = destinationAngle - (turretAngle + robotAngle);
        turnTurretBy(toTurn%360); // take the mod/remainder of toTurn/360
        // to keep the angle in the range of [0,360]
    }
}