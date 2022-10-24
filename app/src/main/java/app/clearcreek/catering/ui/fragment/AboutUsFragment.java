package app.clearcreek.catering.ui.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import app.clearcreek.catering.R;
import app.clearcreek.catering.databinding.FragmentAboutUsBinding;
import app.clearcreek.catering.databinding.FragmentMainBinding;

public class AboutUsFragment extends Fragment {

    private FragmentAboutUsBinding binding;

    public AboutUsFragment() {
        super(R.layout.fragment_about_us);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentAboutUsBinding.bind(view);
    }
}
