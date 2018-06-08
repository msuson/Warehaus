package com.example.android.warehaus;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.warehaus.data.InventoryDbHelper;
import com.example.android.warehaus.data.ProductContract.ProductEntry;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private InventoryDbHelper dbHelper = new InventoryDbHelper(this);

    ProductCursorAdapter cursorAdapter;

    private static final int PRODUCT_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button insertButton = findViewById(R.id.insert_button);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertData();
            }
        });

        Button displayButton = findViewById(R.id.display_button);
//        displayButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                displayData();
//            }
//        });

        ListView productList = findViewById(R.id.product_list);

        cursorAdapter = new ProductCursorAdapter(this, null);
        productList.setAdapter(cursorAdapter);

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);

                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

                intent.setData(currentProductUri);
                startActivity(intent);
            }
        });

        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    private void insertData() {
        //SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Nikkon D3100");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 399.99);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 20);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Nikkon");
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "555-123-4567");

        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        //long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);

//        String text = "Product inserted with id " + newRowId;
//        int duration = Toast.LENGTH_SHORT;
//        Toast toast = Toast.makeText(this, text, duration);
//        toast.show();
    }

    private Cursor queryData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER
        };

        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        return cursor;
    }

//    private void displayData() {
//        Cursor cursor = queryData();
//
//        TextView dataTextView = findViewById(R.id.data_text_display);
//        dataTextView.setText("");
//
//        try {
//            int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
//            int prodNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
//            int prodPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
//            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
//            int supNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
//            int supPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
//
//            while(cursor.moveToNext()) {
//                int currentId = cursor.getInt(idColumnIndex);
//                String currentProdName = cursor.getString(prodNameColumnIndex);
//                double currentPrice = cursor.getDouble(prodPriceColumnIndex);
//                int currentQuantity = cursor.getInt(quantityColumnIndex);
//                String currentSupplierName = cursor.getString(supNameColumnIndex);
//                String currentSupplierNumber = cursor.getString(supPhoneColumnIndex);
//
//                dataTextView.append(currentId + " - " +
//                    currentProdName + " - " +
//                    currentPrice + " - " +
//                    currentQuantity + " - " +
//                    currentSupplierName + " - " +
//                    currentSupplierNumber + "\n");
//            }
//
//        } finally {
//            cursor.close();
//        }
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY};

        return new CursorLoader(this, ProductEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
