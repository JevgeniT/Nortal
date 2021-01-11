package com.nortal.clark.training.assignment.controller;


import com.nortal.clark.training.assignment.model.CityMap;
import com.nortal.clark.training.assignment.model.Clark;
import com.nortal.clark.training.assignment.model.Direction;
import com.nortal.clark.training.assignment.model.Position;
import com.nortal.clark.training.assignment.model.SpeedLevel;
import com.nortal.clark.training.assignment.model.VoiceCommand;
import java.util.Comparator;
import java.util.List;


public class You {

    private List<Position> targetsToCapture;

    /**
     * Calculates distance to closest target from current location
     */
    private double getDistance(Clark clark, Position position){
        return Math.sqrt(Math.pow(clark.getPosition().x - position.x, 2) + Math.pow(clark.getPosition().y - position.y, 2));
    }

    /**
     * Gets closest target
     */
    private Position closest(Clark clark){
        return targetsToCapture.stream().min(Comparator.comparing(position -> getDistance(clark,position))).get();
    }


    public VoiceCommand getNextStep(Clark clark, CityMap cityMap) {
        VoiceCommand voiceCommand = new VoiceCommand(Direction.SOUTH, SpeedLevel.L0_RUNNING_HUMAN);

        if (targetsToCapture == null) {
            targetsToCapture = cityMap.getTargets();
        }

        Position targetToCapture = closest(clark);
        System.out.println(clark + " ->> x=" + targetToCapture.x + ", y=" + targetToCapture.y);

        int diffX = Math.abs(targetToCapture.x - clark.getPosition().x);
        int diffY = Math.abs(targetToCapture.y - clark.getPosition().y);

        SpeedLevel horizontalSpeedLevel = thinkOfSpeedLevel(diffX, clark.getHorizontal());
        SpeedLevel verticalSpeedLevel = thinkOfSpeedLevel(diffY, clark.getVertical());

        if (diffX < 2 && diffY < 2) {
            targetsToCapture.remove(closest(clark));
        } else if (targetToCapture.x > clark.getPosition().x) {
            voiceCommand = new VoiceCommand(Direction.EAST, horizontalSpeedLevel);
        } else if (targetToCapture.y > clark.getPosition().y) {
            voiceCommand = new VoiceCommand(Direction.NORTH, verticalSpeedLevel);
        } else if (targetToCapture.x < clark.getPosition().x) {
            voiceCommand = new VoiceCommand(Direction.WEST, horizontalSpeedLevel);
        } else if (targetToCapture.y < clark.getPosition().y) {
            voiceCommand = new VoiceCommand(Direction.SOUTH, verticalSpeedLevel);
        }

        //<<SOLUTION END>>
        return voiceCommand;
    }

    //<<SOLUTION START>>
    private SpeedLevel thinkOfSpeedLevel(int distanceDiff, double speed) {
        double actualSpeed = speed - getDragAcceleration(speed);
        double time = Math.abs(distanceDiff/ actualSpeed);

        if (time <= 3.6) { // magic number
            if (speed <= 2){
                return SpeedLevel.L2_SUB_SONIC;
            }
            return SpeedLevel.L0_RUNNING_HUMAN;
        }
        return SpeedLevel.L4_MACH_9350;
    }


    double getDragAcceleration(double currentSpeed) {
        // 0 if currentSpeed = 0
        double dragDirectionalModifier = -Math.signum(currentSpeed);
        //
        // Clark's maximum speed is x²
        double waterDrag = 1.6 + (Math.pow(currentSpeed, 2) / 200);
        return dragDirectionalModifier * waterDrag;
    }
}
