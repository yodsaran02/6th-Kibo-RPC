package jp.jaxa.iss.kibo.rpc.sampleapk;

import android.hardware.Camera;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import org.opencv.aruco.Aruco;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.calib3d.Calib3d;
import org.opencv.aruco.Dictionary;

import java.util.*;
/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee.
 */

public class YourService extends KiboRpcService {
    @Override
    protected void runPlan1(){
        // Init Aruco dict & list type of stuff

        Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        Mat ids = new Mat();
        List<Mat> corners = new ArrayList<>();
        float markerLength = 0.05f;
        float targetWidth = 0.15f;
        float targetLength = 0.27f;

        // Init All the area to explore

        Map<Integer, Point> AreaPoint = new HashMap<>();
        Map<Integer, Quaternion> AreaQuaternion = new HashMap<>();
        AreaPoint.put(1, new Point(10.9d, -9.92284d, 5.195d));
        AreaQuaternion.put(1, new Quaternion(0f, 0f, -0.707f, 0.707f));
        api.startMission();

        // Move to a point.

        Point point;
        Quaternion quaternion;
        rMoveTo(AreaPoint.get(1), AreaQuaternion.get(1));

        // Get a camera image.
        Mat image = api.getMatNavCam();
        api.saveMatImage(image, "area1_raw.png");
        Mat undistorted = unDistortImage(image);
        api.saveMatImage(undistorted, "area1_undistorted.png");
        Aruco.detectMarkers(undistorted, dictionary, corners, ids);
        Aruco.drawDetectedMarkers(undistorted, corners, ids);
        api.saveMatImage(undistorted, "area1_arucotag.png");

        /* ******************************************************************************** */
        /* Write your code to recognize the type and number of landmark items in each area! */
        /* If there is a treasure item, remember it.                                        */
        /* ******************************************************************************** */

        // When you recognize landmark items, let’s set the type and number.
        api.setAreaInfo(1, "item_name", 1);

        /* **************************************************** */
        /* Let's move to each area and recognize the items. */
        /* **************************************************** */

        // When you move to the front of the astronaut, report the rounding completion.
        point = new Point(11.143d, -6.7607d, 4.9654d);
        quaternion = new Quaternion(0f, 0f, 0.707f, 0.707f);
        rMoveTo(point, quaternion);
        api.reportRoundingCompletion();

        /* ********************************************************** */
        /* Write your code to recognize which target item the astronaut has. */
        /* ********************************************************** */

        // Let's notify the astronaut when you recognize it.
        api.notifyRecognitionItem();

        /* ******************************************************************************************************* */
        /* Write your code to move Astrobee to the location of the target item (what the astronaut is looking for) */
        /* ******************************************************************************************************* */

        // Take a snapshot of the target item.
        api.takeTargetItemSnapshot();
        // test
        // function image processing lens


        // test from me
        //hello test dek mwit tam araii
    }

    @Override
    protected void runPlan2(){
       // write your plan 2 here.
    }

    @Override
    protected void runPlan3(){
        // write your plan 3 here.
    }

    // reliable api.moveto
    private void rMoveTo(Point point, Quaternion quaternion) {
        Result result;
        final int LOOP_MAX = 5;

        result = api.moveTo(point, quaternion, true);

        int loopCounter = 0;
        while (!result.hasSucceeded() && loopCounter < LOOP_MAX) {
            result = api.moveTo(point, quaternion, true);
            ++loopCounter;
        }
    }
    private Mat unDistortImage(Mat image){
        Mat cameraMatrix = new Mat(3,3,CvType.CV_64F);
        cameraMatrix.put(0,0,api.getNavCamIntrinsics()[0]);
        Mat cameraCoefficients = new Mat(1,5,CvType.CV_64F);
        cameraCoefficients.put(0, 0, api.getNavCamIntrinsics()[1]);
        cameraCoefficients.convertTo(cameraCoefficients, CvType.CV_64F);
        Mat undistortImg = new Mat();
        Calib3d.undistort(image, undistortImg, cameraMatrix, cameraCoefficients);
        return undistortImg;
    }
    private String ImageRecognition(Mat image){
        return "object type";
    }
}
// hfs shift5
