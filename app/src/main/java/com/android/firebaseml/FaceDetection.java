package com.android.firebaseml;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.firebaseml.Helper.GraphicOverlay;
import com.android.firebaseml.Helper.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import dmax.dialog.SpotsDialog;

public class FaceDetection extends AppCompatActivity {
Button faceDetectButton;
GraphicOverlay graphicOverlay;
CameraView cameraView;
Camera camera;
AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);

        faceDetectButton=findViewById(R.id.detect_face);
        graphicOverlay=findViewById(R.id.graphic_overlay1);
        cameraView=findViewById(R.id.camera_view);


        alertDialog=new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait")
                .setCancelable(false)
                .build();

faceDetectButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        cameraView.start();
        cameraView.captureImage();
        graphicOverlay.clear();
    }
});


cameraView.addCameraKitListener(new CameraKitEventListener() {
    @Override
    public void onEvent(CameraKitEvent cameraKitEvent) {

    }


    @Override
    public void onError(CameraKitError cameraKitError) {

    }

    @Override
    public void onImage(CameraKitImage cameraKitImage) {


        alertDialog.show();
        Bitmap bitmap = cameraKitImage.getBitmap();
        bitmap=Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
        cameraView.stop();
        processfacedetection(bitmap);
    }

    @Override
    public void onVideo(CameraKitVideo cameraKitVideo) {

    }
});

    }

    private void processfacedetection(Bitmap bitmap) {
        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions=new FirebaseVisionFaceDetectorOptions.Builder().build();
        FirebaseVisionFaceDetector firebaseVisionFaceDetector= FirebaseVision.getInstance().getInstance()
                .getVisionFaceDetector(firebaseVisionFaceDetectorOptions);
        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                getfaces(firebaseVisionFaces);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FaceDetection.this,"error"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getfaces(List<FirebaseVisionFace> firebaseVisionFaces) {
        int count=0;
        for(FirebaseVisionFace faces:firebaseVisionFaces)
        {
            Rect rect= faces.getBoundingBox();
            RectOverlay rectOverlay=new RectOverlay(graphicOverlay,rect);
            graphicOverlay.add(rectOverlay);
            count=count+1;

        }
        alertDialog.dismiss();
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }
}
