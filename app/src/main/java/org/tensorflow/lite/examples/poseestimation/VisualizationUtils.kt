/* Copyright 2021 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================
*/

package org.tensorflow.lite.examples.poseestimation

import android.R.attr.translateX
import android.R.attr.translateY
import android.graphics.*
import com.google.mlkit.vision.common.PointF3D
import org.tensorflow.lite.examples.poseestimation.data.BodyPart
import org.tensorflow.lite.examples.poseestimation.data.Person
import kotlin.math.*


object VisualizationUtils {
    /** Radius of circle used to draw keypoints.  */
    private const val CIRCLE_RADIUS = 6f

    /** Width of line used to connected two keypoints.  */
    private const val LINE_WIDTH = 4f

    /** The text size of the person id that will be displayed when the tracker is available.  */
    private const val PERSON_ID_TEXT_SIZE = 30f

    /** Distance from person id to the nose keypoint.  */
    private const val PERSON_ID_MARGIN = 6f

    /** Pair of keypoints to draw lines between.  */
    private val bodyJoints = listOf(
        Pair(BodyPart.NOSE, BodyPart.LEFT_EYE),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_EYE),
        Pair(BodyPart.NOSE, BodyPart.LEFT_SHOULDER),
        Pair(BodyPart.NOSE, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_ELBOW),
        Pair(BodyPart.LEFT_ELBOW, BodyPart.LEFT_WRIST),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_ELBOW),
        Pair(BodyPart.RIGHT_ELBOW, BodyPart.RIGHT_WRIST),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.RIGHT_SHOULDER),
        Pair(BodyPart.LEFT_SHOULDER, BodyPart.LEFT_HIP),
        Pair(BodyPart.RIGHT_SHOULDER, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.RIGHT_HIP),
        Pair(BodyPart.LEFT_HIP, BodyPart.LEFT_KNEE),
        Pair(BodyPart.LEFT_KNEE, BodyPart.LEFT_ANKLE),
        Pair(BodyPart.RIGHT_HIP, BodyPart.RIGHT_KNEE),
        Pair(BodyPart.RIGHT_KNEE, BodyPart.RIGHT_ANKLE)
    )

    // Draw line and point indicate body pose
    fun drawBodyKeypoints(
        input: Bitmap,
        persons: List<Person>,
        isTrackerEnabled: Boolean = false
    ): Bitmap {



        var paintCircle = Paint().apply {
            strokeWidth = 0.3f
            color = Color.GRAY
            style = Paint.Style.FILL
        }
        var paintLine = Paint().apply {
            strokeWidth = LINE_WIDTH
            color = Color.GREEN
            style = Paint.Style.STROKE
        }

        var paintText = Paint().apply {
            textSize = PERSON_ID_TEXT_SIZE
            color = Color.GREEN
            textAlign = Paint.Align.LEFT
        }
        val output = input.copy(Bitmap.Config.ARGB_8888, true)
        val originalSizeCanvas = Canvas(output)

        persons.forEach { person ->
            // draw person id if tracker is enable
            if (isTrackerEnabled) {
                person.boundingBox?.let {
                    val personIdX = max(0f, it.left)
                    val personIdY = max(0f, it.top)



                    originalSizeCanvas.drawText(
                        person.id.toString(),
                        personIdX,
                        personIdY - PERSON_ID_MARGIN,
                        paintText
                    )
                    originalSizeCanvas.drawRect(it, paintLine)
                }
            }

            bodyJoints.forEach {
                val pointA = person.keyPoints[it.first.position].coordinate
                val pointB = person.keyPoints[it.second.position].coordinate
                originalSizeCanvas.drawLine(pointA.x, pointA.y, pointB.x, pointB.y, paintLine)

                val leftHip = person.keyPoints[BodyPart.LEFT_HIP.position].coordinate
                val rightHip = person.keyPoints[BodyPart.RIGHT_HIP.position].coordinate
                val nose =  person.keyPoints[BodyPart.NOSE.position].coordinate
                val leftShoulder = person.keyPoints[BodyPart.LEFT_SHOULDER.position].coordinate
                val rightShoulder = person.keyPoints[BodyPart.RIGHT_SHOULDER.position].coordinate
                val rightElbow = person.keyPoints[BodyPart.RIGHT_ELBOW.position].coordinate
                val leftElbow = person.keyPoints[BodyPart.LEFT_ELBOW.position].coordinate
                val leftWrist = person.keyPoints[BodyPart.LEFT_WRIST.position].coordinate
                val rightWrist = person.keyPoints[BodyPart.RIGHT_WRIST.position].coordinate
                val eye = person.keyPoints[BodyPart.RIGHT_EYE.position].coordinate


                //region Angle Tronc A




                val tempRight = PointF(person.keyPoints[BodyPart.RIGHT_HIP.position].coordinate.x,person.keyPoints[BodyPart.RIGHT_HIP.position].coordinate.y-60)
                val tempLeft = PointF(person.keyPoints[BodyPart.LEFT_HIP.position].coordinate.x,person.keyPoints[BodyPart.LEFT_HIP.position].coordinate.y-60)



                var angleDegreesLeft = calculateAngle(tempLeft, leftHip, nose);
                val angleDegreesRight = calculateAngle(tempRight, rightHip, nose);


                if ((angleDegreesRight > 30 && angleDegreesRight < 60 ) || (angleDegreesLeft > 30 && angleDegreesLeft < 60)) {

                    originalSizeCanvas.drawLine(leftHip.x, leftHip.y, leftShoulder.x, leftShoulder.y,Paint().apply {
                        strokeWidth = LINE_WIDTH
                        color = Color.YELLOW
                        style = Paint.Style.STROKE
                    })
                    originalSizeCanvas.drawLine(rightHip.x, rightHip.y, rightShoulder.x, rightShoulder.y,  Paint().apply {
                        strokeWidth = LINE_WIDTH
                        color = Color.YELLOW
                        style = Paint.Style.STROKE
                    })



                }else if ((angleDegreesRight >= 60 ) || (angleDegreesLeft >= 60)) {

                    originalSizeCanvas.drawLine(leftHip.x, leftHip.y, leftShoulder.x, leftShoulder.y, Paint().apply {
                        strokeWidth = LINE_WIDTH
                        color = Color.RED
                        style = Paint.Style.STROKE
                    })
                    originalSizeCanvas.drawLine(rightHip.x, rightHip.y, rightShoulder.x, rightShoulder.y, Paint().apply {
                        strokeWidth = LINE_WIDTH
                        color = Color.RED
                        style = Paint.Style.STROKE
                    })
                } else {

                    originalSizeCanvas.drawLine(leftHip.x, leftHip.y, leftShoulder.x, leftShoulder.y, Paint().apply {
                        strokeWidth = LINE_WIDTH
                        color = Color.GREEN
                        style = Paint.Style.STROKE
                    })
                    originalSizeCanvas.drawLine(rightHip.x, rightHip.y, rightShoulder.x, rightShoulder.y, Paint().apply {
                        strokeWidth = LINE_WIDTH
                        color = Color.GREEN
                        style = Paint.Style.STROKE
                    })

                }

                //endregion

                //region Angle Arm F5 (1,2)


                val mLeftShoulder = PointF(person.keyPoints[BodyPart.LEFT_SHOULDER.position].coordinate.x + 20,person.keyPoints[BodyPart.LEFT_SHOULDER.position].coordinate.y)
                val mRightShoulder = PointF(person.keyPoints[BodyPart.RIGHT_SHOULDER.position].coordinate.x - 20,person.keyPoints[BodyPart.RIGHT_SHOULDER.position].coordinate.y)

                originalSizeCanvas.drawPoint(mLeftShoulder.x,mLeftShoulder.y,Paint().apply {
                    strokeWidth = LINE_WIDTH
                    color = Color.BLUE
                    style = Paint.Style.STROKE
                })
                originalSizeCanvas.drawPoint(mRightShoulder.x,mRightShoulder.y,Paint().apply {
                    strokeWidth = LINE_WIDTH
                    color = Color.RED
                    style = Paint.Style.STROKE
                })
                val angleDegreesLeftArm = calculateAngle(mLeftShoulder, leftShoulder, leftWrist);
                val angleDegreesRightArm = calculateAngle(mRightShoulder, rightShoulder, rightWrist);


                //180°
                val angleDegreesStrictRightArm = calculateAngle(rightShoulder, rightElbow, rightWrist);
                val angleDegreesStrictLeftArm = calculateAngle(leftShoulder, leftElbow, leftWrist);


                if (eye.y > leftWrist.y || (angleDegreesLeftArm > 45 && angleDegreesStrictLeftArm > 150f)){

                    originalSizeCanvas.drawLine(leftElbow.x, leftElbow.y, leftWrist.x, leftWrist.y, Paint().apply {
                        strokeWidth = LINE_WIDTH
                        color = Color.RED
                        style = Paint.Style.STROKE
                    })

                    originalSizeCanvas.drawLine(leftShoulder.x, leftShoulder.y, leftElbow.x, leftElbow.y, Paint().apply {
                        strokeWidth = LINE_WIDTH
                        color = Color.RED
                        style = Paint.Style.STROKE
                    })

                }

                if (eye.y > rightWrist.y || (angleDegreesRightArm > 45 && angleDegreesStrictRightArm > 150f)){

                    originalSizeCanvas.drawLine(rightElbow.x, rightElbow.y, rightWrist.x, rightWrist.y, Paint().apply {
                        strokeWidth = LINE_WIDTH
                        color = Color.RED
                        style = Paint.Style.STROKE
                    })
                    originalSizeCanvas.drawLine(rightShoulder.x, rightShoulder.y, rightElbow.x, rightElbow.y, Paint().apply {
                        strokeWidth = LINE_WIDTH
                        color = Color.RED
                        style = Paint.Style.STROKE
                    })

                }
                //endregion


            }

            person.keyPoints.forEach { point ->
                originalSizeCanvas.drawCircle(
                    point.coordinate.x,
                    point.coordinate.y,
                    CIRCLE_RADIUS,
                    paintCircle
                )
            }
        }

        return output
    }

     fun calculateAngle(
        firstLandmark: PointF,
        secondLandmark: PointF,
        thirdLandmark: PointF
    ): Float {
        val firstSegmentLength = sqrt(
            (secondLandmark.x - firstLandmark.x).toDouble().pow(2.0) + (secondLandmark.y - firstLandmark.y).toDouble().pow(2.0)
        ).toFloat()
        val secondSegmentLength = sqrt(
            (thirdLandmark.x - secondLandmark.x).toDouble().pow(2.0) + (thirdLandmark.y - secondLandmark.y).toDouble().pow(2.0)
        ).toFloat()
        val dotProduct =
            (secondLandmark.x - firstLandmark.x) *
                    (thirdLandmark.x - secondLandmark.x) +
                    (secondLandmark.y - firstLandmark.y) *
                    (thirdLandmark.y - secondLandmark.y)

        val cosAngle = dotProduct / (firstSegmentLength * secondSegmentLength)
        var angle = Math.toDegrees(acos(cosAngle.toDouble())).toFloat()
        angle = 180 - angle;
        return if (angle >= 0) angle else angle + 360
    }




}
