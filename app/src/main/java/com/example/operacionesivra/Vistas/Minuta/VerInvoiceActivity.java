package com.example.operacionesivra.Vistas.Minuta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.example.operacionesivra.R;
import com.example.operacionesivra.Vistas.Inventarios.Vistas.EnHistorico.DetallesHistorico.PdfDocumentAdapter;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class VerInvoiceActivity extends AppCompatActivity {
    PDFView pdfView;
    ImageButton buttonGuardar, buttonImprimir, buttonVolver, buttonCompartir;
    File file1 = null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minuta_ver_invoice);
        String pdf = getIntent().getStringExtra("pdf");
        pdfView = findViewById(R.id.pdfView);
        file1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/"+pdf);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pdfView.fromStream(fis).load();

        File file = crearFichero(pdf);

        buttonVolver = findViewById(R.id.imageButtonVolverReporteMinutaVistaPrevia);
        buttonGuardar = findViewById(R.id.imageButtonGuardarReporteMinutaVistaPrevia);
        buttonImprimir = findViewById(R.id.imageButtonImprimirReporteMinutaVistaPrevia);
        buttonCompartir = findViewById(R.id.imageButtonCompartirReporteMinutaVistaPrevia);

        buttonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                file1.delete();
                finish();
            }
        });
        buttonImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(file1.exists()){
                    printPDF(file1);
                }else{
                    printPDF(file);
                }
            }
        });
        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Se copia el archivo a la carpeta destino adecuada y se elimina de la carpeta anterior
                try {
                    Files.copy(FileSystems.getDefault().getPath(file1.getPath()),FileSystems.getDefault().getPath(file.getPath())
                            , StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Se guardó correctamente el archivo",Toast.LENGTH_SHORT).show();
                file1.delete();
            }
        });
        buttonCompartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(VerInvoiceActivity.this, "com.codepath.fileprovider", file1);

              Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setType("application/pdf");
                startActivity(Intent.createChooser(shareIntent, "Envió"));

                /*Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.");
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);*/

                /*Uri uri = FileProvider.getUriForFile(VerInvoiceActivity.this, "com.mydomain.fileprovider", file1);
                Intent intent = ShareCompat.IntentBuilder.from(VerInvoiceActivity.this)
                        .setType("application/pdf")
                        .setStream(uri)
                        .setChooserTitle("Choose bar")
                        .createChooserIntent()
                        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);*/

               /*
                //Uri uri = Uri.fromFile(file1.getAbsoluteFile());
                Uri uri = FileProvider.getUriForFile(VerInvoiceActivity.this, "com.mydomain.fileprovider", file1);
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
               emailIntent.setType("application/pdf");
                emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
                startActivity(Intent.createChooser(emailIntent, "Send email using:"));*/
            }
        });
    }

    public void printPDF(File file){
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try{
            PrintDocumentAdapter printDocumentAdapter = new PdfDocumentAdapter(getApplicationContext(), file.getAbsolutePath());
            printManager.print("Document", printDocumentAdapter, new PrintAttributes.Builder().build());
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public File crearFichero(String nombreFichero) {
        File ruta = getRuta();

        File fichero = null;
        if (ruta != null) {
            fichero = new File(ruta, nombreFichero);
        }

        return fichero;
    }

    public File getRuta() {
        File ruta = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            ruta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Reportes Minutas");

            if (ruta != null) {
                if (!ruta.mkdirs()) {
                    if (!ruta.exists()) {
                        return null;
                    }
                }
            }
        }
        return ruta;
    }
}
