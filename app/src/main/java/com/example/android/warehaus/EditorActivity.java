package com.example.android.warehaus;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.warehaus.data.ProductContract.ProductEntry;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private Uri currentProductUri;

    private static final int EXISTING_LOADER = 0;

    private EditText nameEditText;
    private EditText priceEditText;
    private TextView quantityTextView;
    private EditText supplierNameEditText;
    private EditText supplierPhoneEditText;
    private Button decreaseButton;
    private Button increaseButton;
    private Button orderButton;
    private FloatingActionButton fabDelete;
    private FloatingActionButton fabSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        currentProductUri = intent.getData();

        nameEditText = findViewById(R.id.product_name_edit);
        priceEditText = findViewById(R.id.price_edit);
        quantityTextView = findViewById(R.id.quantity_text);
        supplierNameEditText = findViewById(R.id.supplier_name_edit);
        supplierPhoneEditText = findViewById(R.id.supplier_phone_edit);
        decreaseButton = findViewById(R.id.decrease_button);
        increaseButton = findViewById(R.id.increase_button);
        orderButton = findViewById(R.id.order_button);
        fabDelete = findViewById(R.id.fab_delete);
        fabSave = findViewById(R.id.fab_save);

        if(currentProductUri == null) {
            setTitle(getString(R.string.add_product_title));
            orderButton.setVisibility(View.GONE);
            fabDelete.setVisibility(View.GONE);
        }
        else {
            setTitle(getString(R.string.edit_product_title));

            getLoaderManager().initLoader(EXISTING_LOADER, null, this);
        }

        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(quantityTextView.getText().toString());
                if(quantity > 0)
                    quantityTextView.setText(String.valueOf(quantity-1));
            }
        });

        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(quantityTextView.getText().toString());
                quantityTextView.setText(String.valueOf(quantity+1));
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};
        return new CursorLoader(this, currentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if(cursor == null || cursor.getCount() < 1)
            return;

        if(cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            String productName = cursor.getString(nameColumnIndex);
            double productPrice = cursor.getDouble(priceColumnIndex);
            int productQuantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            String supplierPhone = cursor.getString(supplierPhoneColumnIndex);

            nameEditText.setText(productName);
            priceEditText.setText("$" + Double.toString(productPrice));
            quantityTextView.setText(Integer.toString(productQuantity));
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityTextView.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
    }
}
