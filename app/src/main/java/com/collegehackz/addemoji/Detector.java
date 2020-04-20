package com.collegehackz.addemoji;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;


class Detector {


    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), backgroundBitmap.getConfig());
        float scaleFactor = 0.9f;
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() * newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);
        float emojiPositionX = (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY = (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);
        return resultBitmap;
    }


    static Bitmap detectFaces(Context context, Bitmap currMap){
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        Frame frame = new Frame.Builder().setBitmap(currMap).build();
        SparseArray<Face> faces = detector.detect(frame);

        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);
            Bitmap currEmojiMap = getEmojiBitmap(face,context);
            currMap = addBitmapToFace(currMap,currEmojiMap,face);
        }
        detector.release();
        return currMap;

    }


    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    private static Bitmap getEmojiBitmap(Face face, Context context){
        boolean smile = face.getIsSmilingProbability() > SMILING_PROB_THRESHOLD;
        boolean leftEye = face.getIsLeftEyeOpenProbability() > EYE_OPEN_PROB_THRESHOLD;
        boolean rightEye = face.getIsRightEyeOpenProbability() > EYE_OPEN_PROB_THRESHOLD;
        Bitmap resultMap;
        if(smile){
            if(leftEye&&rightEye){
                resultMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.smile);
            }else if(leftEye){
                resultMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.rightwink);
            }else if(rightEye){
                resultMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.leftwink);
            }else{
                resultMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.closed_smile);
            }
        }else{
            if(leftEye&&rightEye){
                resultMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.frown);
            }else if(leftEye){
                resultMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.rightwinkfrown);
            }else if(rightEye){
                resultMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.leftwinkfrown);
            }else{
                resultMap = BitmapFactory.decodeResource(context.getResources(), R.drawable.closed_frown);
            }
        }
        return resultMap;
    }


}
