package com.ayush.steganography;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ayush.imagesteganographylibrary.Text.AsyncTaskCallback.TextDecodingCallback;
import com.ayush.imagesteganographylibrary.Text.ImageSteganography;
import com.ayush.imagesteganographylibrary.Text.TextDecoding;

import java.io.IOException;

public class Decode extends AppCompatActivity implements TextDecodingCallback{

    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "Decode Class";



    private Uri filepath;

    //Bitmap
    private Bitmap original_image;

    //Initializing the UI components
    TextView textView;
    ImageView imageView;
    EditText message, secret_key;
    Button choose_image_button, decode_button;

    //ImageSteganography object
    ImageSteganography result;
    TextDecoding textDecoding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);

        //Instantiation of UI components
        textView = (TextView) findViewById(R.id.whether_decoded);

        imageView = (ImageView) findViewById(R.id.imageview);

        message = (EditText) findViewById(R.id.message);

        secret_key = (EditText) findViewById(R.id.secret_key);

        choose_image_button = (Button) findViewById(R.id.choose_image_button);
        decode_button = (Button) findViewById(R.id.decode_button);

        //Choose Image Button
        choose_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageChooser();
            }
        });

        //Decode Button
        decode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filepath != null){

                    //Making the ImageSteganography object
                    ImageSteganography imageSteganography = new ImageSteganography(secret_key.getText().toString(),
                            original_image.copy(Bitmap.Config.ARGB_8888, false));

                    //Making the TextDecoding object
                     textDecoding = new TextDecoding(Decode.this, Decode.this);

                    //Execute Task
                    textDecoding.execute(imageSteganography);


                }
            }
        });


    }

    void ImageChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image set to imageView
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null){

            filepath = data.getData();
            try{
                original_image = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);




            }
            catch (IOException e){
                Log.d(TAG, "Error : " + e);
            }
            imageView.setImageBitmap(original_image);
        }

    }

    @Override
    public void onStartTextEncoding() {
        //Whatever you want to do by the start of textDecoding
    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {

        //By the end of textDecoding

        this.result = result;



        if (result != null){
            if (!result.isDecoded())
                //textView.setText("No message found");
            Toast.makeText(Decode.this,"No message found ",Toast.LENGTH_LONG).show();
            else{
                if (!result.isSecretKeyWrong()){

                 message.setText(result.getMessage());

                    Toast.makeText(Decode.this,"Decrypted ",Toast.LENGTH_LONG).show();

                   // textView.setText("Decrypted");

                   //message.setLongClickable(false);
                   //message.setTextIsSelectable(false);
                }
                else {
                    //textView.setText("Wrong secret key");
                    Toast.makeText(Decode.this,"Wrong secret key",Toast.LENGTH_LONG).show();
                }
            }
        }
        else {
            textView.setText("Select Image First");
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
