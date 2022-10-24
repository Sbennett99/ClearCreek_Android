package app.clearcreek.catering.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.LinkedList;
import java.util.List;

import app.clearcreek.catering.AppController;
import app.clearcreek.catering.R;
import app.clearcreek.catering.data.model.AddressLocation;
import app.clearcreek.catering.databinding.FragmentLocationsBinding;

public class LocationsFragment extends Fragment {

    private FragmentLocationsBinding binding;
    private List<AddressLocation> addressLocations = new LinkedList<>();

    private GoogleMap googleMap;

    private final OnMapReadyCallback callback = googleMap -> {
        this.googleMap = googleMap;

        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setTiltGesturesEnabled(false);
        googleMap.getUiSettings().setRotateGesturesEnabled(false);

        loadAddresses();
    };

    public LocationsFragment() {
        super(R.layout.fragment_locations);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding = FragmentLocationsBinding.bind(view);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void loadAddresses() {
        binding.progress.setVisibility(View.VISIBLE);

        AppController.getDb().collection("locations")
                .get()
                .addOnSuccessListener(task -> {
                    addressLocations.clear();
                    for (DocumentSnapshot snapshot : task.getDocuments()) {
                        AddressLocation location = snapshot.toObject(AddressLocation.class);
                        addressLocations.add(location);
                    }
                    updateMap();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    binding.progress.setVisibility(View.GONE);
                });
    }

    private void updateMap() {
        binding.progress.setVisibility(View.GONE);
        if(!addressLocations.isEmpty()) {
            LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();

            for (AddressLocation location : addressLocations) {
                LatLng latLng = new LatLng(location.getGeoPoint().getLatitude(), location.getGeoPoint().getLongitude());
                MarkerOptions markerOption = new MarkerOptions();
                markerOption.position(latLng);

                Marker marker = googleMap.addMarker(markerOption);
                if (marker != null) {
                    marker.setTitle(location.getName());
                    marker.setTag(location.getAddress());
                }

                boundsBuilder.include(latLng);
            }

            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 64));

            googleMap.setOnMarkerClickListener(marker -> {
                binding.address.setText(String.valueOf(marker.getTag()));

                String urlString = String.format("geo:%f,%f", marker.getPosition().latitude, marker.getPosition().longitude);
                Uri gmmIntentUri = Uri.parse(urlString);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(mapIntent);
                }

                return true;
            });

            binding.address.setText(addressLocations.get(0).getAddress());
            binding.addressLayout.setVisibility(View.VISIBLE);
        } else {
            binding.addressLayout.setVisibility(View.GONE);
        }
    }
}
