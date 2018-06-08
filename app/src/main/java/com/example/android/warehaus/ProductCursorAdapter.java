package com.example.android.warehaus;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.warehaus.data.ProductContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter{

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView nameTextView = view.findViewById(R.id.name_text_view);
        TextView priceTextView = view.findViewById(R.id.price_text_view);
        TextView quantityTextView = view.findViewById(R.id.quantity_text_view);
        Button saleButton = view.findViewById(R.id.sale_button);

        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        String productName = cursor.getString(nameColumnIndex);
        double productPrice = cursor.getDouble(priceColumnIndex);
        int productQuantity = cursor.getInt(quantityColumnIndex);

        nameTextView.setText(productName);
        priceTextView.setText("$" + Double.toString(productPrice));
        quantityTextView.setText(Integer.toString(productQuantity));
        final int rowId = cursor.getInt(cursor.getColumnIndex(ProductEntry._ID));
        final int quantity = cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantity-1 >= 0) {
                    ContentValues values = new ContentValues();
                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity-1);

                    Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, rowId);
                    context.getContentResolver().update(uri, values, null, null);
                }
            }
        });
    }
}
