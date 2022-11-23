package za.nmu.wrpv;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import za.nmu.wrpv.messages.History;
import za.nmu.wrpv.messages.OrderAcknowledgedSubscribe;
import za.nmu.wrpv.messages.OrderPublish;
import za.nmu.wrpv.messages.R;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryItemHolder>{
    public final List<History> histories;
    public HistoryAdapter(List<History> histories) {
        this.histories = histories;
    }

    @NonNull
    @Override
    public HistoryItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new HistoryItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryItemHolder holder, int position) {
        History history = histories.get(position);
        holder.set(history);
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }

    public void add(History history) {
        histories.add(history);
        notifyItemInserted(getItemCount() - 1);
    }

    public void updateState(History history) {
        for (int i = 0; i < histories.size(); i++) {
            History h = histories.get(i);
            if (h.id == history.id) {
                h.acknowledged = history.acknowledged;
                h.ready = history.ready;
                notifyItemChanged(i);
                return;
            }
        }
    }

    public class HistoryItemHolder extends RecyclerView.ViewHolder {
        public TextView tvDate;
        public TextView tvTime;
        public TextView tvTel;
        public TextView tvDetails;
        public TextView tvCost;
        public Button btnAcknowledgeOrder;
        public HistoryItemHolder(@NonNull View itemView) {
            super(itemView);

            tvDate = itemView.findViewById(R.id.tv_history_date);
            tvTime = itemView.findViewById(R.id.tv_history_time);
            tvTel = itemView.findViewById(R.id.tv_history_tel);
            tvDetails = itemView.findViewById(R.id.tv_history_details);
            tvCost = itemView.findViewById(R.id.tv_history_cost);
            btnAcknowledgeOrder = itemView.findViewById(R.id.btn_acknowledge_order);
        }

        public void set(History history) {
            System.out.println("------------------------" + history.ready + " " + history.acknowledged + " " + history.cancelled);
            if (history.cancelled) {
                btnAcknowledgeOrder.setText(R.string.cancelled);
            }
            else if (history.ready) {
                if (history.acknowledged) {
                    btnAcknowledgeOrder.setText(R.string.acknowledged);
                    btnAcknowledgeOrder.setEnabled(false);
                }else
                {
                    btnAcknowledgeOrder.setText(R.string.acknowledge);
                    btnAcknowledgeOrder.setEnabled(true);
                }
            }
            else{
                if (history.acknowledged) {
                    btnAcknowledgeOrder.setText(R.string.acknowledged);
                }
                else {
                    btnAcknowledgeOrder.setText(R.string.processing);
                }
                btnAcknowledgeOrder.setEnabled(false);
            }

            btnAcknowledgeOrder.setOnClickListener(view -> {
                Button btnAcknowledgeOrder = itemView.findViewById(R.id.btn_acknowledge_order);
                btnAcknowledgeOrder.setEnabled(false);
                btnAcknowledgeOrder.setText(R.string.acknowledged);
                new OrderAcknowledgedSubscribe(null, null).apply(history);
                history.acknowledged = true;
                try {
                    XMLHandler.modifyXML(history, OrderPublish.fileName, "orders");
                } catch (IOException | TransformerException | ParserConfigurationException | XPathExpressionException | SAXException e) {
                    e.printStackTrace();
                }
            });

            btnAcknowledgeOrder.setTag(history.id);

            tvDate.setText(history.date);
            tvTime.setText(history.time);
            tvTel.setText(history.tel);
            tvDetails.setText(history.items);
            tvCost.setText(history.total);
        }
    }
}
