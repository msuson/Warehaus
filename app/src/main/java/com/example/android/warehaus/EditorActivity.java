package com.example.android.warehaus;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.warehaus.data.ProductContract.ProductEntry;

import java.util.Locale;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private Uri currentProductUri;

    private static final int EXISTING_LOADER = 0;

    private boolean productHasChanged = false;

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

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            productHasChanged = true;
            return false;
        }
    };

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

        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        decreaseButton.setOnTouchListener(touchListener);
        increaseButton.setOnTouchListener(touchListener);
        supplierNameEditText.setOnTouchListener(touchListener);
        supplierPhoneEditText.setOnTouchListener(touchListener);

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

        if(currentProductUri == null) {
            setTitle(getString(R.string.add_product_title));
            orderButton.setVisibility(View.GONE);
            fabDelete.setVisibility(View.GONE);
        }
        else {
            setTitle(getString(R.string.edit_product_title));

            fabDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog();
                }
            });

            orderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callSupplier();
                }
            });

            getLoaderManager().initLoader(EXISTING_LOADER, null, this);
        }

        fabSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct();
            }
        });
    }

    private void deleteProduct() {
        if(currentProductUri != null) {
            Toast toast;
            String text;
            int duration = Toast.LENGTH_SHORT;

            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);
            if(rowsDeleted == 0)
                text = getString(R.string.delete_product_failed);
            else
                text = getString(R.string.delete_product_success);

            toast = Toast.makeText(this, text, duration);
            toast.show();
        }
    }

    private void saveProduct() {
        String productName = nameEditText.getText().toString().trim();
        String productPrice = priceEditText.getText().toString().trim();
        String productQuantity = quantityTextView.getText().toString().trim();
        String supplierName = supplierNameEditText.getText().toString().trim();
        String supplierPhone = supplierPhoneEditText.getText().toString().trim();

        if(productName.isEmpty()) {
            Toast.makeText(this, getString(R.string.provide_product_name), Toast.LENGTH_SHORT).show();
            return;
        } else if(productPrice.isEmpty() || Double.parseDouble(productPrice) < 0) {
            Toast.makeText(this, getString(R.string.provide_product_price), Toast.LENGTH_SHORT).show();
            return;
        } else if(supplierName.isEmpty()) {
            Toast.makeText(this, getString(R.string.provide_supplier_name), Toast.LENGTH_SHORT).show();
            return;
        } else if(supplierPhone.isEmpty() || supplierPhone.length() < 10) {
            Toast.makeText(this, getString(R.string.provide_supplier_number), Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
        String formattedPrice = String.format(Locale.getDefault(),"%.2f", Double.parseDouble(productPrice));
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, Double.parseDouble(formattedPrice));
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, Integer.parseInt(productQuantity));
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhone);

        Toast toast;
        String text;
        int duration = Toast.LENGTH_SHORT;

        if(currentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if(newUri == null)
                text = getString(R.string.add_product_failed);
            else
                text = getString(R.string.add_product_success);
        } else {
            int rowsUpdated = getContentResolver().update(currentProductUri, values, null, null);
            if(rowsUpdated == 0)
                text = getString(R.string.edit_product_failed);
            else
                text = getString(R.string.edit_product_success);
        }

        toast = Toast.makeText(this, text, duration);
        toast.show();

        finish();
    }

    private void callSupplier() {
        String supplierPhone = supplierPhoneEditText.getText().toString().trim();
        if(supplierPhone.contains("-"))
            supplierPhone = supplierPhone.replace("-", "");
        if(supplierPhone.length() >= 10) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + supplierPhone));
            if(intent.resolveActivity(getPackageManager()) != null)
                startActivity(intent);
        }
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
            String formattedPrice = String.format(Locale.getDefault(),"%.2f", productPrice);
            priceEditText.setText(formattedPrice);
            String quantityString = Integer.toString(productQuantity);
            quantityTextView.setText(quantityString);
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

    @Override
    public void onBackPressed() {
        if(!productHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
