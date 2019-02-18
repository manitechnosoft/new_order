package com.mobile.order.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.mobile.order.R;

/**
 */
public class CustomerInfoFragment extends Fragment {
  private View mViewHolder;
  Button nextButton;
  ArrayAdapter<String> itemsAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
    // Inflate the xml file for the fragment
    if (mViewHolder == null) {
      mViewHolder = inflater.inflate(R.layout.fragment_customerinfo, parent, false);
      nextButton = getActivity().findViewById(R.id.next_button);
      nextButton.setText("Print  ");
      nextButton.setEnabled(true);
      //nextButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icons8_receipt_64, 0);
    }
    return mViewHolder;
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {

  }

  //private OnItemSelectedListener listener;



  //--OnItemSelectedListener listener;
  // This event fires 1st, before creation of fragment or any views
  // The onAttach method is called when the Fragment instance is associated with an Activity.
  // This does not mean the Activity is fully initialized.
  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
  /*  if(context instanceof OnItemSelectedListener){      // context instanceof YourActivity
      this.listener = (OnItemSelectedListener) context; // = (YourActivity) context
    } else {
      throw new ClassCastException(context.toString()
        + " must implement PizzaMenuFragment.OnItemSelectedListener");
    }*/
  }


  // Define the events that the fragment will use to communicate
  public interface OnItemSelectedListener {
    // This can be any number of events to be sent to the activity
    void onPizzaItemSelected(int position);
  }

}
