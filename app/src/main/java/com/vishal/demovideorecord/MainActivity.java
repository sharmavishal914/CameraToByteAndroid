package com.vishal.demovideorecord;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private ImageView m_VideCaptureBitmap;
    Camera mCamera;
    Button m_StartVideoChat;
    Button m_close_camera;
    EditText IPAdresse;
    private android.view.SurfaceView m_VideoCaptureView;
    private static final int TIMEOUT_MS = 10000;
    private static final int server_port = 13011;
    byte[] m_buffer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();


        // Vishal sharma


        // Sharamama

        StrictMode.setThreadPolicy(policy);
        m_VideoCaptureView = (android.view.SurfaceView) findViewById(R.id.imgSentView);

        m_close_camera = (Button) findViewById(R.id.btnCameraClose);
        m_close_camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCamera.release();
            }
        });


        m_VideCaptureBitmap = findViewById(R.id.videoView);
        IPAdresse = (EditText) findViewById(R.id.etIPAdresse);
        IPAdresse.setText("192.168.2.32");
        m_StartVideoChat = (Button) findViewById(R.id.btnStartVideo);
        m_StartVideoChat.setOnClickListener(this);


        new Thread(new Runnable() {
            @Override
            public void run() {

//                final byte[] buffer = new byte[2048];
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        frameToBuffer(buffer);
//                    }
//                });


//                DatagramSocket s;
//                try {
//                    s = new DatagramSocket(server_port);
//                    s.setSoTimeout(TIMEOUT_MS);
//
//                    while (true) {
//                        try {
//                            final DatagramPacket p = new DatagramPacket(buffer, buffer.length);
//                            s.receive(p);
//
//                            m_buffer = buffer.clone();
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    frameToBuffer(p.getData());
//                                }
//                            });
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
////                    }
//                } catch (SocketException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                    //rstep.setText("fail socket create");
//                }
            }

        }).start();
    }

    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btnStartVideo:
                startVideo();

        }
    }

    private Camera openFrontFacingCameraGingerbread(Camera cam) {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }

        return cam;
    }

    private void startVideo() {
        if (mCamera != null) {
            System.out.println("======mCamera======" + mCamera);
            return;
        }
        SurfaceHolder videoCaptureViewHolder = null;
        try {
            //openFrontFacingCameraGingerbread(mCamera);
            int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
            if (Camera.getNumberOfCameras() > 1 && camId < Camera.getNumberOfCameras() - 1) {
                int i = 1 + camId;
                mCamera = Camera.open(i);
            }
            //mCamera = Camera.open();
        } catch (RuntimeException e) {
            Log.e("CameraTest", "Camera Open filed");
            return;
        }
        mCamera.setErrorCallback(new Camera.ErrorCallback() {
            public void onError(int error, Camera camera) {
            }
        });
        Camera.Parameters parameters = mCamera.getParameters();

        List<Camera.Size> allSizes = parameters.getSupportedPictureSizes();
        Camera.Size size = allSizes.get(0); // get top size
        for (int i = 0; i < allSizes.size(); i++) {
            if (allSizes.get(i).width > size.width)
                size = allSizes.get(i);
        }

        parameters.setPictureSize(size.width, size.height);
        mCamera.setParameters(parameters);

        if (null != m_VideoCaptureView)
            videoCaptureViewHolder = m_VideoCaptureView.getHolder();
        try {
            mCamera.setPreviewDisplay(videoCaptureViewHolder);
        } catch (Throwable t) {
        }
        mCamera.stopPreview();
        mCamera.setDisplayOrientation(90);
        mCamera.startPreview();

        Log.v("CameraTest", "Camera PreviewFrameRate = " + mCamera.getParameters().getPreviewFrameRate());
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        int dataBufferSize = (int) (previewSize.height * previewSize.width *
                (ImageFormat.getBitsPerPixel(mCamera.getParameters().getPreviewFormat()) / 8.0));
        mCamera.addCallbackBuffer(new byte[dataBufferSize]);
        mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {

            private long timestamp = 0;

            public synchronized void onPreviewFrame(byte[] data, Camera camera) {
//
//                System.out.println("=========HELLOWWOOWOWO======");
//
//
//                frameToBuffer(data);


                int size = data.length;
                Camera.Size previewSize = camera.getParameters().getPreviewSize();
                YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 80, baos);
//                yuvimage.compressToJpeg(new Rect(0, 0, 128, 96), 80, baos);
                byte[] jdata = baos.toByteArray();
//                int sizeOfData = jdata.length;
//
//                DatagramSocket s;
//                try {
//                    s = new DatagramSocket();
//                    s.setBroadcast(true);
//                    s.setSoTimeout(TIMEOUT_MS);
//                    InetAddress local = InetAddress.getByName(IPAdresse.getText().toString());
//
//                    DatagramPacket p = new DatagramPacket(jdata, jdata.length, local, server_port);
//                    s.send(p);
//
//                } catch (SocketException e) {
//                    e.printStackTrace();
//                } catch (UnknownHostException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                // Convert to Bitmap
                Bitmap bmp = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
                m_VideCaptureBitmap.setImageBitmap(bmp);
//                bitmapArray.add(bmp);
//
//
//                // set our renderer to be the main renderer with
//                // the current activity context
//
//                //setContentView(glSurfaceView);
//
//
                Log.v("CameraTest", "Frame size = " + data.length);
                timestamp = System.currentTimeMillis();
                try {
                    camera.addCallbackBuffer(data);
                } catch (Exception e) {
                    Log.e("CameraTest", "addCallbackBuffer error");
                    return;
                }
                return;
            }
        });

        try {
            mCamera.startPreview();
        } catch (Throwable e) {
            mCamera.release();
            mCamera = null;
            e.printStackTrace();
            return;
        }
    }


    private void frameToBuffer(byte[] data) {

        System.out.println("====datadata========" + data.length);

        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
        m_VideCaptureBitmap.setImageBitmap(bmp);
    }
}