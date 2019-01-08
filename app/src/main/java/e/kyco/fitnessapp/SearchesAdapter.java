package e.kyco.fitnessapp;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class SearchesAdapter extends RecyclerView.Adapter<SearchesAdapter.ViewHolder> {

    // listeners from MainActivity that are registered for each list item
    private final View.OnClickListener clickListener;
    private final View.OnLongClickListener longClickListener;



    // List<String> used to obtain RecyclerView items' data
  //  private final List<String> tags; // search tags
    public ImageView holdImage;
    private final List<Workout> workoutTest;





    // constructor
    public SearchesAdapter(List<Workout> workoutTest,
                           View.OnClickListener clickListener,
                           View.OnLongClickListener longClickListener) {
        this.workoutTest = workoutTest;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    // sets up new list item and its ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // inflate the list_item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item, parent, false);

        // create a ViewHolder for current item
        return (new ViewHolder(view, clickListener, longClickListener));
    }

    // sets the text of the list item to display the search tag
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textViewType.setText(workoutTest.get(position).getWorkoutType());
        holder.textViewLength.setText(workoutTest.get(position).getWorkoutLength());
        holder.imageType.setImageResource(workoutTest.get(position).getWorkoutImage());


            }

    // returns the number of items that adapter binds
    @Override
    public int getItemCount() {
        return workoutTest.size();
    }

    // nested subclass of RecyclerView.ViewHolder used to implement
    // the view-holder pattern in the context of a RecyclerView--the logic
    // of recycling views that have scrolled offscreen is handled for you
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView textViewType;
        public final TextView textViewLength;
        public final ImageView imageType;

        // configures a RecyclerView item's ViewHolder
        public ViewHolder(View itemView,
                          View.OnClickListener clickListener,
                          View.OnLongClickListener longClickListener) {
            super(itemView);
            textViewType = (TextView) itemView.findViewById(R.id.nameOfActivityTV);
            textViewLength = (TextView) itemView.findViewById(R.id.amountOfActivityTV);
            imageType = (ImageView) itemView.findViewById(R.id.conditionImageView) ;

            // attach listeners to itemView
            itemView.setOnClickListener(clickListener);
            itemView.setOnLongClickListener(longClickListener);
        }
    }


}