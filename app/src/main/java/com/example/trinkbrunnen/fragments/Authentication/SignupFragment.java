package com.example.trinkbrunnen.fragments.Authentication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.trinkbrunnen.MainActivity;
import com.example.trinkbrunnen.Model.ParseQuarries;
import com.example.trinkbrunnen.R;
import com.example.trinkbrunnen.databinding.FragmentSignupBinding;
import com.example.trinkbrunnen.fragments.MapFragment;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignupFragment extends Fragment {
    private FragmentSignupBinding binding;

    MapFragment returnFragment;

    public SignupFragment(MapFragment returnFragment) {
        // Required empty public constructor
        this.returnFragment = returnFragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding =  FragmentSignupBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setOnTouchClickListenerToRootView();
        setRegisterTextViewFancyModification();

        view.findViewById(R.id.btn_register_signupFragment).setOnClickListener(View->{
            validateData();
        });

    }

    private void validateData() {
        //if name , email and password are empty, show warning

        if (
                !binding.evNameSignupFragment.getText().toString().isEmpty() &&
                !binding.evEmailSignupFragment.getText().toString().isEmpty() &&
                !binding.evPasswordLoginFragment.getText().toString().isEmpty()
        ){
            //if valid email is provided
            String emailValidityPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
            String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(binding.evEmailSignupFragment.getText());
            if (!matcher.matches()){
                //email not valid, show warning
                binding.tvWarning.setText("Email is not valid");
                binding.tvWarning.setVisibility(View.VISIBLE);
                return;
            }

            //password length is attest 8 character
            if (binding.evPasswordLoginFragment.getText().toString().length()<8){
                //password is short, show warning
                binding.tvWarning.setText("Password must be at lest 8 character");
                binding.tvWarning.setVisibility(View.VISIBLE);
                return;
            }

            //check for existing user
            ParseQuarries.sighUp(
                    binding.evNameSignupFragment.getText().toString(),
                    binding.evEmailSignupFragment.getText().toString(),
                    binding.evPasswordLoginFragment.getText().toString(),
                    var->{
                        onSignUpFinishCallback((Bundle) var);
                    }
            );

        } else {
            //show warning
            binding.tvWarning.setText("Fill up all the information");
            binding.tvWarning.setVisibility(View.VISIBLE);
        }

    }


    private void onSignUpFinishCallback(Bundle b){
        if (!b.getBoolean("succ")){
            //problem occur, show error message
            binding.tvWarning.setText(b.getString("message"));
            binding.tvWarning.setVisibility(View.VISIBLE);
            return;
        }
        binding.tvWarning.setVisibility(View.INVISIBLE);
        MainActivity.mapFragment = new MapFragment(MainActivity.baseContext);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer_mainActivity, MainActivity.mapFragment)
                .addToBackStack(null)
                .commit();

        //MainActivity.mapFragment.onReloadFragment();

    }


    //set keyboard hiding functionality
    //it calls another function: hideKeyboard();
    @SuppressLint("ClickableViewAccessibility")
    private void setOnTouchClickListenerToRootView(){
        binding.parentSignupFragment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    //implements the functionality of hiding keyboard upon clicking anywhere in the page
    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View currentFocus = getActivity().getCurrentFocus();
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }


    //adding extra feature in text view login.
    // making last only clickable.
    // making it italic and underlined
    private void setRegisterTextViewFancyModification(){
        String st = "Have an account? Login from here";
        SpannableString spannableString = new SpannableString(st);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer_mainActivity,new LoginFragment(new MapFragment(MainActivity.baseContext)))
                        .commit();

                //returnFragment.onReload();
            }
        };
        int start = st.length()-4; // Start index of the word "clickable"
        int end = st.length();   // End index of the word "clickable"
        spannableString.setSpan(clickableSpan, start,  end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //italic text
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //underline text
        spannableString.setSpan(new UnderlineSpan(),  start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        //so that the color of the text remain the same
        spannableString.setSpan(new ForegroundColorSpan(binding.tvLoginSignupFragment.getCurrentTextColor()), 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.tvLoginSignupFragment.setText(spannableString);
        binding.tvLoginSignupFragment.setMovementMethod(LinkMovementMethod.getInstance());
    }
}