package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;


/**
 *  This is the main Teleop that contains all of the mechanisms
 */

@TeleOp(name="Holonomic Drive", group="Drive")
public class HolonomicDrive extends OpMode {

    /* Declare OpMode members. */
    Hardware robot = new Hardware(); // Use the class created to define the robot's hardware

    /*
     * Code to run ONCE when the driver hits INIT
     */
    @Override
    public void init() {
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        robot.init(hardwareMap);
        // Set servos to starting values
        robot.startServo();
    }

    /*
     * Code to run REPEATEDLY after the driver hits INIT, but before they hit PLAY
     */
    @Override
    public void init_loop() {
    }

    /*
     * Code to run ONCE when the driver hits PLAY
     */
    @Override
    public void start() {
        robot.naturalServo();
    }

    /*
     * Code to run REPEATEDLY after the driver hits PLAY but before they hit STOP
     */
    @Override
    public void loop() {
        setSpeed();
        drive();
        grip();
        slide();
        slideWinch();
        slideGrab();
        slideRotate();
        telemetry.addData("Grip Left: ",  robot.gl.getPosition());
        telemetry.addData("Grip Right: ", robot.gr.getPosition());
    }

    double pwrRatio = 0.5;

    public void setSpeed() {
        if(gamepad1.a) {
            pwrRatio = 0.9;
        } else if(gamepad1.b) {
            pwrRatio = 0.5;
        }
    }

    public void drive() {
        double x = gamepad1.left_stick_x;
        double y = -gamepad1.left_stick_y;
        double z = -gamepad1.right_stick_x;

        int dir = Hardware.Direction_Stop;
        if(y >= 0.2) {
            dir |= Hardware.Direction_Forward;
        } else if(y <= -0.2) {
            dir |= Hardware.Direction_Reverse;
        }
        if(x >= 0.2) {
            dir |= Hardware.Direction_Right;
        } else if(x <= -0.2) {
            dir |= Hardware.Direction_Left;
        }
        if(dir == Hardware.Direction_Stop) {
            if(z >= 0.2) {
                dir |= Hardware.Direction_RotateRight;
            } else if(z <= -0.2) {
                dir |= Hardware.Direction_RotateLeft;
            }
        }
        double pwr = Math.max(Math.max(Math.abs(x), Math.abs(y)), Math.abs(z));
        pwr *= pwrRatio;
        robot.drive(dir, pwr*pwr);
    }

    // Set button activity to false
    boolean xActive   = false;
    boolean yActive   = false;

    // Set previous and current state of button
    boolean xPrevState = false;
    boolean yPrevState = false;

    public void grip() {

        // Check the status of the buttons
        boolean xCurrState = gamepad1.x;           // X current state
        boolean yCurrState = gamepad1.y;           // Y current state

        // Check for button state transitions
        // Opens cube manipulator grippers wider
        if ((xCurrState == true) && (xCurrState != xPrevState))  {
            // Button is transitioning to a pressed state
            xActive = !xActive;
            if (xActive == true) {
                robot.grip(true, 0.2);
            } else {
                robot.grip(false, 0.2);
            }
        } else if ((yCurrState == true) && (yCurrState != yPrevState)) {
            // Button is transitioning to a pressed state
            yActive = !yActive;
            robot.grip(false, 0.4); // Grip Wide
        }
        xPrevState = xCurrState;
        yPrevState = yCurrState;
    }

    public void slide() {
        if (gamepad1.right_bumper) {
            robot.slide.setPower(0.8);
        } else if (gamepad1.left_bumper) {
            robot.slide.setPower(-0.5);
        } else {
            robot.slide.setPower(0);
        }
    }

    public void slideWinch() {
        if(gamepad1.dpad_right) {
            robot.sw.setPower(-1);
        } else if(gamepad1.dpad_left) {
            robot.sw.setPower(1);
        } else {
            robot.sw.setPower(0);
        }
    }

    public void slideGrab() {
        if(gamepad1.right_trigger > 0.5) {
            robot.sg.setPosition(robot.SlideGrabPos);
        } else {
            robot.sg.setPosition(robot.NeutralSlideGrabPos);
        }
    }

    public void slideRotate() {
        if(gamepad1.left_trigger > 0.5) {
            robot.sr.setPosition(robot.SlideRotatePos);
        } else {
            robot.sr.setPosition(robot.NeutralSlideRotatePos);
        }
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    @Override
    public void stop() {
    }
}
