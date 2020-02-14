//package com.frcteam195.cyberscouter;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//
//
///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link TelePopUpPage.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link TelePopUpPage#newInstance} factory method to
// * create an instance of this fragment.
// */
//public class TelePopUpPage extends Fragment implements View.OnClickListener{
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//    private static Integer INNER_VALUE = 0;
//    private static Integer OUTER_VALUE = 0;
//    private static Integer LOWER_VALUE = 0;
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;
//
//    public TelePopUpPage() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment TelePopUpPage.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static TelePopUpPage newInstance(String param1, String param2) {
//        TelePopUpPage fragment = new TelePopUpPage();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_tele_pop_up_page, container, false);
//
//        Button upButton = view.findViewById(R.id.lowerPlusButton);
//        upButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                lowerPlusButton();
//            }
//        });
//
//        upButton =  view.findViewById(R.id.LowerMinusButton);
//        upButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                lowerMinusButton();
//            }
//        });
//
//        upButton =  view.findViewById(R.id.outerPlusButton);
//        upButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                outerPlusButton();
//            }
//        });
//
//        upButton =  view.findViewById(R.id.outerMinusButton);
//        upButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                outerMinusButton();
//            }
//        });
//
//        upButton =  view.findViewById(R.id.InnerPlusButton);
//        upButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                innerPlusButton();
//            }
//        });
//
//        upButton =  view.findViewById(R.id.InnerMinusButton);
//        upButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                innerMinusButton();
//            }
//        });
//
//        upButton =  view.findViewById(R.id.BackButton);
//        upButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                backButton();
//            }
//        });
//
//        return view;
//    }
//
//    private void backButton() {
//        Intent intent = new Intent(this, TelePage.class);
//        startActivity(intent);
//    }
//
//    private void innerMinusButton() {
//        Button = view.findViewById(R.id.InnerMinusButton);
//        if (INNER_VALUE > 0)
//            INNER_VALUE --;
//        Button.setText(String.valueOf(INNER_VALUE));
//    }
//
//    private void innerPlusButton() {
//        Button = view.findViewById(R.id.InnerPlusButton);
//        Button.setText(String.valueOf(INNER_VALUE));
//    }
//
//    private void outerMinusButton() {
//        Button = view.findViewById(R.id.outerMinusButton);
//        if (OUTER_VALUE > 0)
//            OUTER_VALUE --;
//        Button.setText(String.valueOf(OUTER_VALUE));
//    }
//
//    private void outerPlusButton() {
//        Button = view.findViewById(R.id.outerPlusButton);
//        Button.setText(String.valueOf(OUTER_VALUE));
//    }
//
//    private void lowerMinusButton() {
//        Button = view.findViewById(R.id.LowerMinusButton);
//        if (LOWER_VALUE > 0)
//            LOWER_VALUE --;
//        Button.setText(String.valueOf(LOWER_VALUE));
//    }
//
//    private void lowerPlusButton() {
//        Button = view.findViewById(R.id.lowerPlusButton);
//        Button.setText(String.valueOf(LOWER_VALUE));
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    @Override
//    public void onClick(View view) {
//
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
//}
