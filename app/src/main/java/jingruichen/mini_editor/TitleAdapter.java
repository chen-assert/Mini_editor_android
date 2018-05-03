package jingruichen.mini_editor;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View;

import java.util.List;

public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.ViewHolder> {

    private List<Index> mIndex;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView titleName;

        public ViewHolder(View view){
            super(view);
            titleName = (TextView) view.findViewById(R.id.title_name);
        }
    }

    public TitleAdapter(List<Index> indexList){
        mIndex = indexList;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.index_layout, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Index index = mIndex.get(holder.getAdapterPosition());
                //input index content <- need to build a new activity
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Index index = mIndex.get(position);
        holder.titleName.setText(index.getTitle());
    }

    @Override
    public int getItemCount() {
        return mIndex.size();
    }


}
