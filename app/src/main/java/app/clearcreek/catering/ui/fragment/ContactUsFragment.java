package app.clearcreek.catering.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import app.clearcreek.catering.R;
import app.clearcreek.catering.databinding.FragmentAboutUsBinding;
import app.clearcreek.catering.databinding.FragmentContactUsBinding;

public class ContactUsFragment extends Fragment {

    private FragmentContactUsBinding binding;

    public ContactUsFragment() {
        super(R.layout.fragment_contact_us);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentContactUsBinding.bind(view);
    }
}
