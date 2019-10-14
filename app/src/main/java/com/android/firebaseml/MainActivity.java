package com.android.firebaseml;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import com.wonderkiln.camerakit.CameraView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
Button  btn_gallery,openimage ;
ImageView img ;
TextView t1;
public Bitmap selectedImage;
public static final int CAMERA_REQUEST=999,RESULT_LOAD_IMG=9090;


    public void face(View face)
    {
        Intent intent=new Intent(MainActivity.this,FaceDetection.class);
        startActivity(intent);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        openimage=findViewById(R.id.button2);
img=findViewById(R.id.imageView3);

t1=findViewById(R.id.textView);
btn_gallery=findViewById(R.id.button);



openimage.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent=new Intent(MainActivity.this,ImageRecognition.class) ;
    startActivity(intent);
    }
});
btn_gallery.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        opengallery();
    }
});



    }

    public void opengallery()
    {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        startActivityForResult(i, RESULT_LOAD_IMG);

    }
public void opencamera()
{
    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    startActivityForResult(intent,CAMERA_REQUEST);

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
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(MainActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
imageFromBitmap(selectedImage);
        //
//        try{
//if(requestCode==CAMERA_REQUEST)
//{
//
//    Bitmap bitmap=(Bitmap)data.getExtras().get("data");
//    img.setImageBitmap(bitmap);
//}
//else if(requestCode==RESULT_LOAD_IMG)
//{
//    Bitmap bitmap=(Bitmap)data.getExtras().get("data");
//    img.setImageBitmap(bitmap);
//
//
//}
//        }
//        catch (Exception e )
//        {
//            Toast.makeText(this,"Error : "+e.getMessage(),Toast.LENGTH_SHORT).show();
//
//        }
    }

    public void imageFromBitmap(Bitmap selectedImage) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(selectedImage);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();

        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                processText(firebaseVisionText);
                                // Task completed successfully
                                // ...
                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                     t1.setText("Error"+e.getMessage());
                                        // Task failed with an exception
                                        // ...
                                    }
                                });
    }
public void processText(FirebaseVisionText result)
{

//    List<FirebaseVisionText.Block> blocks=  result.getBlocks();

    String resultText = result.getText();
    for (FirebaseVisionText.TextBlock block: result.getTextBlocks()) {
        String blockText = block.getText();
        String jj=t1.getText().toString();
            t1.setText(" "+jj+" "+blockText);
//Toast.makeText(this,blockText,Toast.LENGTH_SHORT).show();
    }
//        Toast.makeText(this,"HEllo "+blockText,Toast.LENGTH_SHORT).show();
//        Float blockConfidence = block.getConfidence();
//        List<RecognizedLanguage> blockLanguages = block.getRecognizedLanguages();
//        Point[] blockCornerPoints = block.getCornerPoints();
//        Rect blockFrame = block.getBoundingBox();
//
//        for (FirebaseVisionText.Line line: block.getLines()) {
//            String lineText = line.getText();
//          String jj=t1.getText().toString();
//            t1.setText(" "+jj+" "+lineText);
//
//            Float lineConfidence = line.getConfidence();
//            List<RecognizedLanguage> lineLanguages = line.getRecognizedLanguages();
//            Point[] lineCornerPoints = line.getCornerPoints();
//            Rect lineFrame = line.getBoundingBox();
//            for (FirebaseVisionText.Element element: line.getElements()) {
//                String elementText = element.getText();
//                Float elementConfidence = element.getConfidence();
//                List<RecognizedLanguage> elementLanguages = element.getRecognizedLanguages();
//                Point[] elementCornerPoints = element.getCornerPoints();
//                Rect elementFrame = element.getBoundingBox();
//            }
//        }
       // t1.setText("Text :"+blockText+"\n Confidence :");
    }




}
