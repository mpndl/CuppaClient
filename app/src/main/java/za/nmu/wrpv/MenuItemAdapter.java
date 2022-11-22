package za.nmu.wrpv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import za.nmu.wrpv.messages.R;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MenuItemHolder> {
    public final List<Item> items;
    public MenuItemAdapter(List<Item> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MenuItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        return new MenuItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemHolder holder, int position) {
        Item item = items.get(position);
        holder.set(item);
        holder.itemView.setOnClickListener(view -> {
            NumberPicker npItemQuantity = holder.itemView.findViewById(R.id.np_item_quantity);
            items.get(position).quantity = npItemQuantity.getValue();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class MenuItemHolder extends RecyclerView.ViewHolder {
        private final TextView tvItemName;
        private final TextView tvItemDescription;
        private final ImageView ivItemImage;
        private final TextView tvItemCost;

        private final View itemView;
        public MenuItemHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            NumberPicker npItemQuantity = itemView.findViewById(R.id.np_item_quantity);
            npItemQuantity.setOnValueChangedListener((numberPicker, i, i1) -> {
                itemView.performClick();
            });

            npItemQuantity.setMaxValue(100);
            npItemQuantity.setMinValue(0);

            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemDescription =  itemView.findViewById(R.id.tv_item_description);
            ivItemImage = itemView.findViewById(R.id.iv_item_image);
            tvItemCost = itemView.findViewById(R.id.tv_item_cost);

            ImageButton ibIncrementQuantity = itemView.findViewById(R.id.ib_decrement_quantity);
            ImageButton ibDecrementQuantity = itemView.findViewById(R.id.ib_increment_quantity);
            ibIncrementQuantity.setOnClickListener(view -> {
                onIncrement();
                itemView.performClick();
            });
            ibDecrementQuantity.setOnClickListener(view -> {
                onDecrement();
                itemView.performClick();
            });
        }

        public void set(Item item) {
            NumberPicker npItemQuantity = itemView.findViewById(R.id.np_item_quantity);
            npItemQuantity.setValue(item.quantity);

            Locale local = Locale.getDefault();
            Currency currency = Currency.getInstance(local);

            tvItemName.setText(item.name);
            tvItemDescription.setText(item.description);
            tvItemCost.setText(currency.getCurrencyCode() + " " + item.cost);
            tvItemCost.setTag(item.cost);
            if (item.image != null) {
                BitmapDrawable bitmapDrawable = null;
                try (FileOutputStream fos = ServerHandler.activity.openFileOutput(item.imageName, Context.MODE_PRIVATE)) {
                    fos.write(item.image);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try (FileInputStream fis = ServerHandler.activity.openFileInput(item.imageName)) {
                    bitmapDrawable = new BitmapDrawable(fis);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (bitmapDrawable != null) {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    Drawable d = new BitmapDrawable(ServerHandler.activity.getResources(), bitmap);
                    ivItemImage.setBackground(d);
                    ivItemImage.setVisibility(View.VISIBLE);
                }
            }else ivItemImage.setVisibility(View.GONE);
        }

        public void onDecrement() {
            NumberPicker npItemQuantity = itemView.findViewById(R.id.np_item_quantity);

            npItemQuantity.setValue(npItemQuantity.getValue() - 1);
        }

        public void onIncrement() {
            NumberPicker npItemQuantity = itemView.findViewById(R.id.np_item_quantity);

            npItemQuantity.setValue(npItemQuantity.getValue() + 1);
        }
    }
}
