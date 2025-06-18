package jp.jaxa.iss.kibo.rpc.sampleapk;

import android.hardware.Camera;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import gov.nasa.arc.astrobee.Result;
import jp.jaxa.iss.kibo.rpc.api.KiboRpcService;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import org.opencv.aruco.Aruco;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.calib3d.Calib3d;
import org.opencv.aruco.Dictionary;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.*;
import java.io.InputStream;
/**
 * Class meant to handle commands from the Ground Data System and execute them in Astrobee.
 */

public class YourService extends KiboRpcService {
    @Override
    protected void runPlan1() {
        // Init Camera stuff

        Mat cameraMatrix = new Mat(3, 3, CvType.CV_64F);
        cameraMatrix.put(0, 0, api.getNavCamIntrinsics()[0]);
        Mat cameraCoefficients = new Mat(1, 5, CvType.CV_64F);
        cameraCoefficients.put(0, 0, api.getNavCamIntrinsics()[1]);
        cameraCoefficients.convertTo(cameraCoefficients, CvType.CV_64F);

        // Init Aruco dict & list type of stuff

        Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_5X5_250);
        Mat ids = new Mat();
        List<Mat> corners = new ArrayList<>();

        // Init All the area to explore

        Map<Integer, Point> AreaPoint = new HashMap<>();
        Map<Integer, Quaternion> AreaQuaternion = new HashMap<>();
        AreaPoint.put(1, new Point(10.9d, -9.92284d, 5.195d));
        AreaQuaternion.put(1, new Quaternion(0f, 0f, -0.707f, 0.707f));
        AreaPoint.put(2, new Point(10.925d, -8.875d, 4.5d));
        AreaQuaternion.put(2, new Quaternion(0f, 0.7071f, 0f, 0.7071f));
        AreaPoint.put(3, new Point(10.925d, -7.925d, 4.5d));
        AreaQuaternion.put(3, new Quaternion(0f, 0.7071f, 0f, 0.7071f));
        AreaPoint.put(4, new Point(10.4d, -6.85d, 4.95d));
        AreaQuaternion.put(4, new Quaternion(0f, 0f, 1f, 0f));

        // Init all the image

        Mat[] template_images = new Mat[11];
        template_images[0] = Imgcodecs.imread("src/main/item_template_images/coin.png");
        template_images[1] = Imgcodecs.imread("src/main/item_template_images/compass.png");
        template_images[2] = Imgcodecs.imread("src/main/item_template_images/coral.png");
        template_images[3] = Imgcodecs.imread("src/main/item_template_images/crystal.png");
        template_images[4] = Imgcodecs.imread("src/main/item_template_images/diamond.png");
        template_images[5] = Imgcodecs.imread("src/main/item_template_images/emerald.png");
        template_images[6] = Imgcodecs.imread("src/main/item_template_images/fossil.png");
        template_images[7] = Imgcodecs.imread("src/main/item_template_images/key.png");
        template_images[8] = Imgcodecs.imread("src/main/item_template_images/letter.png");
        template_images[9] = Imgcodecs.imread("src/main/item_template_images/shell.png");
        template_images[10] = Imgcodecs.imread("src/main/item_template_images/treasure_box.png");

        for (int i = 0; i < 11; i++) {
            Imgproc.cvtColor(template_images[i], template_images[i], Imgproc.COLOR_BGR2GRAY);
        }

        //start mission

        api.startMission();

        // Move to a point.

        Point point;
        Quaternion quaternion;

        rMoveTo(AreaPoint.get(1), AreaQuaternion.get(1));

        // Get a camera image.
        Mat image = api.getMatNavCam();
        api.saveMatImage(image, "area1_raw.png");
        Mat undistorted_aruco = unDistortImage(image);
        Mat undistorted = undistorted_aruco.clone();
        api.saveMatImage(undistorted, "area1_undistorted.png");

        rMoveTo(AreaPoint.get(2), AreaQuaternion.get(2));

        // Get a camera image.
        image = api.getMatNavCam();
        api.saveMatImage(image, "area2_raw.png");
        undistorted_aruco = unDistortImage(image);
        undistorted = undistorted_aruco.clone();
        api.saveMatImage(undistorted, "area2_undistorted.png");

        rMoveTo(AreaPoint.get(3), AreaQuaternion.get(3));

        // Get a camera image.
        image = api.getMatNavCam();
        api.saveMatImage(image, "area3_raw.png");
        undistorted_aruco = unDistortImage(image);
        undistorted = undistorted_aruco.clone();
        api.saveMatImage(undistorted, "area3_undistorted.png");
        Mat undistortedAligned  = alignImage(undistorted,-45,45);
        api.saveMatImage(undistortedAligned, "area3_aligned.png");

        rMoveTo(AreaPoint.get(4), AreaQuaternion.get(4));

        // Get a camera image.
        image = api.getMatNavCam();
        api.saveMatImage(image, "area4_raw.png");
        undistorted_aruco = unDistortImage(image);
        undistorted = undistorted_aruco.clone();
        api.saveMatImage(undistorted, "area4_undistorted.png");

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
    protected void runPlan2() {
        // write your plan 2 here.
    }

    @Override
    protected void runPlan3() {
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
        try {
            Thread.sleep(500);  // wait 1 second after move completes
        } catch (InterruptedException e) {
        }

    }

    private Mat unDistortImage(Mat image) {
        Mat cameraMatrix = new Mat(3, 3, CvType.CV_64F);
        cameraMatrix.put(0, 0, api.getNavCamIntrinsics()[0]);
        Mat cameraCoefficients = new Mat(1, 5, CvType.CV_64F);
        cameraCoefficients.put(0, 0, api.getNavCamIntrinsics()[1]);
        cameraCoefficients.convertTo(cameraCoefficients, CvType.CV_64F);
        Mat undistortImg = new Mat();
        Calib3d.undistort(image, undistortImg, cameraMatrix, cameraCoefficients);
        return undistortImg;
    }

    private String ImageRecognition(Mat image) {
        return "object type";
    }

    private Map<Integer, double[][]> getCornersById(Mat image) {
        Dictionary dictionary = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_50);
        Mat ids = new Mat();
        List<Mat> corners = new ArrayList<>();

        Aruco.detectMarkers(image, dictionary, corners, ids);

        Map<Integer, double[][]> cornerMap = new HashMap<>();

        for (int i = 0; i < corners.size(); i++) {
            // Get marker ID
            int id = (int) ids.get(i, 0)[0];

            // Get corners
            Mat cornerMat = corners.get(i);
            float[] data = new float[8]; // 4 corners * 2 coords
            cornerMat.get(0, 0, data);

            double[][] cornerArray = new double[4][2];
            for (int j = 0; j < 4; j++) {
                cornerArray[j][0] = data[j * 2];     // x
                cornerArray[j][1] = data[j * 2 + 1]; // y
            }

            cornerMap.put(id, cornerArray);
        }

        return cornerMap;
    }

    private Mat rotateImage(Mat src, double angle) {
        double cx = src.width() / 2.0;
        double cy = src.height() / 2.0;

        double cos = Math.cos(Math.toRadians(angle));
        double sin = Math.sin(Math.toRadians(angle));

        Mat rotMat = new Mat(2, 3, CvType.CV_64F);
        rotMat.put(0, 0,
                cos, -sin, (1 - cos) * cx + sin * cy,
                sin, cos, (1 - cos) * cy - sin * cx
        );

        Mat dst = new Mat();
        Imgproc.warpAffine(src, dst, rotMat, src.size());
        return dst;
    }

    private double getSlope(double[] p1, double[] p2) {
        double dx = p2[0] - p1[0];
        if (Math.abs(dx) < 1e-6) return Double.POSITIVE_INFINITY;
        return (p2[1] - p1[1]) / dx;
    }

    private double[] extractCorner(Mat cornerMat, int idx) {
        float[] data = new float[8];
        cornerMat.get(0, 0, data);
        return new double[]{data[idx * 2], data[idx * 2 + 1]};
    }

    private double tryGetSlope(Mat image) {
        Dictionary dict = Aruco.getPredefinedDictionary(Aruco.DICT_4X4_50);
        Mat ids = new Mat();
        List<Mat> corners = new ArrayList<>();
        Aruco.detectMarkers(image, dict, corners, ids);

        if (corners.isEmpty()) return Double.NaN;

        Mat firstCorner = corners.get(0);
        double[] p0 = extractCorner(firstCorner, 0);
        double[] p2 = extractCorner(firstCorner, 2);
        return getSlope(p0, p2);
    }

    private Mat alignImage(Mat input, double minAngle, double maxAngle) {
        while ((maxAngle - minAngle) > 0.1) {
            double midAngle = (minAngle + maxAngle) / 2.0;
            Mat rotated = rotateImage(input, midAngle);
            double slope = tryGetSlope(rotated);

            if (Double.isNaN(slope)) {
                System.out.println("Marker not found at angle: " + midAngle);
                break;
            }

            System.out.printf("Angle: %.2f, Slope: %.4f%n", midAngle, slope);

            if (Math.abs(slope + 1) < 0.05) {
                return rotated;
            }

            if (slope > -1) {
                minAngle = midAngle;
            } else {
                maxAngle = midAngle;
            }
        }

        return input;
    }
}
// hfs shift5
