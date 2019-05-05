package co.comorin.eventdex;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import co.comorin.eventdex.adapters.DetailsAdapter;
import co.comorin.eventdex.utils.UserDetails;

public class MainActivity extends AppCompatActivity {

    ArrayList<UserDetails> userDetailsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Creating list of Items
        try {
            JSONArray detailsArray = new JSONArray(getJSONFromAsseet());
            userDetailsArrayList = new ArrayList<>();
            for (int i = 0; i < detailsArray.length(); i++) {
                UserDetails userDetails = new UserDetails();
                JSONObject jsonObject = detailsArray.getJSONObject(i);
                userDetails.setFirstName(jsonObject.getJSONObject("TktProfileInfo").getString("firstname"));
                userDetails.setLastName(jsonObject.getJSONObject("TktProfileInfo").getString("lastname"));
                userDetails.setEmail(jsonObject.getJSONObject("TktProfileInfo").getString("email"));
                userDetails.setOrderIdemId(jsonObject.getString("orderItemId"));
                userDetails.setOrderItemName(jsonObject.getString("orderItemName"));
                userDetails.setLastModifiesDate(jsonObject.getString("lastModifieddate"));
                userDetailsArrayList.add(userDetails);
            }

            //Setting data in RecyclerView
            RecyclerView detailRecycler = findViewById(R.id.details_recycler);
            detailRecycler.setLayoutManager(new LinearLayoutManager(this));
            detailRecycler.setAdapter(new DetailsAdapter(this, userDetailsArrayList));


            //On click of send Button
            ImageButton exportExcel = findViewById(R.id.export_excel);
            exportExcel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                0);
                    } else {
                        exportToExcel(userDetailsArrayList);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 0: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportToExcel(userDetailsArrayList);
                } else {
                    Toast.makeText(this, "Permissions Required", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    // Reading json file from asset and changin it to JSONARRAY
    public String getJSONFromAsseet() {
        String json;
        try {
            InputStream is = getAssets().open("document.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    //Crating Excel File using Apache POI
    private void exportToExcel(ArrayList<UserDetails> userDetailsArrayList) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        CellStyle style = workbook.createCellStyle(); // Creating Style
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short)11);
        font.setFontName("Courier New");
        font.setBold(true);
        style.setFont(font);


        // Creating Header
        HSSFSheet sheet = workbook.createSheet("Sheet No 1");
        sheet.setDefaultColumnWidth(30);
        HSSFRow rowA = sheet.createRow(0);
        HSSFCell cellHeader = rowA.createCell(0);
        cellHeader.setCellStyle(style);
        cellHeader.setCellValue(new HSSFRichTextString("First Name"));
        cellHeader = rowA.createCell(1);
        cellHeader.setCellStyle(style);
        cellHeader.setCellValue(new HSSFRichTextString("Last Name"));
        cellHeader = rowA.createCell(2);
        cellHeader.setCellStyle(style);
        cellHeader.setCellValue(new HSSFRichTextString("Email"));
        cellHeader = rowA.createCell(3);
        cellHeader.setCellStyle(style);
        cellHeader.setCellValue(new HSSFRichTextString("Order Item Name"));
        cellHeader = rowA.createCell(4);
        cellHeader.setCellStyle(style);
        cellHeader.setCellValue(new HSSFRichTextString("Order Item Id"));
        cellHeader = rowA.createCell(5);
        cellHeader.setCellStyle(style);
        cellHeader.setCellValue(new HSSFRichTextString("Last Modified Date"));


        //Filling all Rows
        for (int i = 1; i < userDetailsArrayList.size(); i++) {
            HSSFRow row = sheet.createRow(i);
            HSSFCell cell = row.createCell(0);
            cell.setCellValue(new HSSFRichTextString(userDetailsArrayList.get(i - 1).getFirstName()));
            cell = row.createCell(1);
            cell.setCellValue(new HSSFRichTextString(userDetailsArrayList.get(i - 1).getLastName()));
            cell = row.createCell(2);
            cell.setCellValue(new HSSFRichTextString(userDetailsArrayList.get(i - 1).getEmail()));
            cell = row.createCell(3);
            cell.setCellValue(new HSSFRichTextString(userDetailsArrayList.get(i - 1).getOrderItemName()));
            cell = row.createCell(4);
            cell.setCellValue(new HSSFRichTextString(userDetailsArrayList.get(i - 1).getOrderIdemId()));
            cell = row.createCell(5);
            cell.setCellValue(new HSSFRichTextString(userDetailsArrayList.get(i - 1).getLastModifiesDate()));
        }

        //Storing in Storage
        FileOutputStream fos = null;
        try {
            String str_path = Environment.getExternalStorageDirectory().toString();
            File file;
            file = new File(str_path, getString(R.string.app_name) + ".xls");
            fos = new FileOutputStream(file);
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(MainActivity.this, "Excel Sheet Generated", Toast.LENGTH_SHORT).show();


            // Sending mail
            String str_path = Environment.getExternalStorageDirectory().toString();
            File file;
            file = new File(str_path, getString(R.string.app_name) + ".xls");
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.setPackage("com.google.android.gm");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"ajay@globalnest.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "OredDetails");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi Ajay,\n    I am sharing records of order details. Please review it\n\nRegards");
            Uri uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider",file);;
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));
        }
    }
}
