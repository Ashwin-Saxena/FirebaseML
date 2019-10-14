package com.android.firebaseml;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.firebaseml.Helper.GraphicOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.wonderkiln.camerakit.CameraView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ImageRecognition extends AppCompatActivity {
public Button opencam;
    ImageView img ;
    TextView t2;
    AlertDialog waiting;
    public Bitmap selectedImage;
    public static final int CAMERA_REQUEST=999,RESULT_LOAD_IMG=9090;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recognition);
    img=findViewById(R.id.imageView2);
    t2=findViewById(R.id.textView2);
    opencam=findViewById(R.id.button3);
        waiting=new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait")
                .setCancelable(false)
                .build();

    opencam.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            opengallery();
        }
    });

    }

    public void opengallery(){
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, RESULT_LOAD_IMG);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                img.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(ImageRecognition.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(ImageRecognition.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
        imagefromBitmap(selectedImage);
    }

    public void imagefromBitmap(Bitmap imaage)
    {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imaage);
        FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();
        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(highAccuracyOpts);
        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        // Task completed successfully
                                        // ...
                                  Toast.makeText(ImageRecognition.this,"DetectinImage() Method Successful",Toast.LENGTH_SHORT).show();
                                   getfaceinfo(faces);
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });

    }

    public void getfaceinfo(List<FirebaseVisionFace> faces)
    {
        for (FirebaseVisionFace face : faces) {
        Rect bounds = face.getBoundingBox();
        float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
        float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees
        Toast.makeText(this," Head Is rotated to the right "+rotY,Toast.LENGTH_SHORT).show();
        Toast.makeText(this," Head Is titled sideways"+rotY,Toast.LENGTH_SHORT).show();
        // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
        // nose available):
        FirebaseVisionFaceLandmark leftEar = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR);
        if (leftEar != null) {
            FirebaseVisionPoint leftEarPos = leftEar.getPosition();
            t2.append("LeftEarPos : "+leftEarPos);
        }

        // If contour detection was enabled:
        List<FirebaseVisionPoint> leftEyeContour =
                face.getContour(FirebaseVisionFaceContour.LEFT_EYE).getPoints();
        List<FirebaseVisionPoint> upperLipBottomContour =
                face.getContour(FirebaseVisionFaceContour.UPPER_LIP_BOTTOM).getPoints();
t2.append("\n LefteyeCountor : "+leftEyeContour+"\n UpperLipBottomContour : "+upperLipBottomContour);
        // If classification was enabled:
        if (face.getSmilingProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
            float smileProb = face.getSmilingProbability();
            t2.append("\n SmileProbability : "+smileProb);

        }
        if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
            float rightEyeOpenProb = face.getRightEyeOpenProbability();
            t2.append("\n RightEyeOpenProbability : "+rightEyeOpenProb);
        }

        // If face tracking was enabled:
        if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
            int id = face.getTrackingId();
        t2.append("\n FaceTrackingId : "+id);

        }
    }

waiting.dismiss();
    }


}
